package com.zimo.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.zimo.demo.bean.WorkStart;
import com.zimo.demo.bean.WorkTimeLimit;
import com.zimo.demo.bean.WorkType;
import com.zimo.demo.mybatis.dao.WorkStartMapper;
import com.zimo.demo.mybatis.dao.WorkTimeLimitMapper;
import com.zimo.demo.mybatis.dao.WorkTypeMapper;
import com.zimo.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.DelayQueue;

@Component
@EnableScheduling
public class Job {

    private static final Logger logger = LoggerFactory.getLogger(Job.class);

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    WorkStartMapper workStartMapper;

    @Autowired
    WorkTypeMapper workTypeMapper;

    @Autowired
    WorkTimeLimitMapper workTimeLimitMapper;


//    /**
//     * JDK 自带阻塞队列实现
//     * @throws InterruptedException
//     */
//    @Scheduled(cron = "0/1 * * * * ?")
//    public void runFirst() throws InterruptedException {
//        try {
//            String value = redisUtil.get("queue111",String.class);
//            logger.info(value);
//            if(value != null) {
//                value = value.replace("\\", "").replace("\"{", "{").replace("}\"", "}");
//                DelayQueue<WorkStart> queue =
//                        JSONObject.parseObject(value, new TypeReference<DelayQueue<WorkStart>>(){});
//                logger.info("redis get queue:" + queue.toString());
//                WorkStart take = queue.poll();
//                while(take != null) {
//                    logger.info("take get:" + take.toString());
//
//                    // 符合要求
//                    // 修改状态
//                    Example example = new Example(WorkType.class);
//                    example.createCriteria().andEqualTo("workId",take.getWorkId());
//                    WorkType workType = new WorkType();
//                    workType.setType(1);
//                    workTypeMapper.updateByExampleSelective(workType,example);
//                    logger.info("修改成功");
//                    // 删除
//                    workStartMapper.deleteByPrimaryKey(take.getId());
//                    logger.info("删除成功");
//                    if(queue.size() == 0) {
//                        break;
//                    }
//                    take = queue.poll();
//                    if(take == null) {
//                        if(queue.size() > 0) {
//                            String str =  JSONObject.toJSONString(queue);
//                            redisUtil.set("queue111",str);
//                        }
//                    }
//                }
//                logger.info("queue size:" + queue.size());
//            } else {
//                DelayQueue<WorkStart> queue = new DelayQueue<>();
//                List<WorkStart> workStarts = workStartMapper.selectAll();
//
//                for(WorkStart workStart: workStarts) {
//                    workStart.setStartTimeLong(workStart.getStartTime().getTime());
//                    queue.offer(workStart);
//                }
//                if(queue.size() > 0) {
//                    logger.info("queue111:" + queue.toString());
//                    String str =  JSONObject.toJSONString(queue);
//                    redisUtil.set("queue111",str);
//                }
//
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//        //logger.info("1111");
//    }
//
    /**
     * 通过zset 完成限时任务
     */
    @Scheduled(cron = "0/1 * * * * ?")
    public void zset() {
        String key = "delayQueue";
        // 首次加载
        try {
            if(redisUtil.getZsetLong(key) == 0) {
                List<WorkTimeLimit> workTimeLimits = workTimeLimitMapper.selectAll();
                if(workTimeLimits.size() > 0) {
                    for (WorkTimeLimit workTimeLimit : workTimeLimits) {
                        workTimeLimit.setEndTimeLong(workTimeLimit.getEndTime().getTime());
                        redisUtil.zsetAdd(key,workTimeLimit.getWorkId()+"&" + workTimeLimit.getStuId(),workTimeLimit.getEndTimeLong());
                    }
                } else {
                    logger.info("没有限时完成作业的任务");
                }
            } else {
                Long time = new Date().getTime();
                Set<ZSetOperations.TypedTuple<Object>> zset
                        = redisUtil.getZset(key,time);
                if(zset.size() == 0) {
                    logger.info("zset 中没有记录");
                } else {
                    // 修改状态
                    ZSetOperations.TypedTuple<Object>[] tuples
                            = (ZSetOperations.TypedTuple<Object>[]) zset.toArray();
                    List<String> list = new ArrayList<>();
                    for (ZSetOperations.TypedTuple<Object> tuple : tuples) {
                        String value = (String) tuple.getValue();
                        String[] split = value.split("&");
                        logger.info("限时任务，workId：" + value);
                        workTypeMapper.updateWorkTypeById(Integer.valueOf(split[0]),Integer.valueOf(split[1]),3,null);
                        list.add(value);
                    }
                    // 删除表记录
                    for(String str : list) {
                        String[] split = str.split("&");
                        workTimeLimitMapper.deleteByWorkId(Integer.valueOf(split[0]),Integer.valueOf(split[1]));
                        logger.info("限时任务删除，workId & stuId：" + str);
                    }
                    // 删除zset
                    redisUtil.zsetRemove(key,time);
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

    }
}
