package com.zimo.demo.service;

import com.zimo.demo.bean.*;
import com.zimo.demo.exception.CommonEnum;
import com.zimo.demo.exception.ZimoException;
import com.zimo.demo.mybatis.dao.*;
import com.zimo.demo.util.Msg;
import com.zimo.demo.util.RedisUtil;
import net.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class WorkService {

    private static final Logger logger = LoggerFactory.getLogger(WorkService.class);

    @Autowired
    WorkMapper workMapper;

    @Autowired
    TaskResourceMapper taskResourceMapper;

    @Autowired
    WorkTypeMapper workTypeMapper;

    @Autowired
    TaskResourceTypeMapper taskResourceTypeMapper;

    @Autowired
    WorkStartMapper workStartMapper;

    @Autowired
    WorkTimeLimitMapper workTimeLimitMapper;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 插入作业
     * @param receive
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Msg insertWork(Receive receive) {
        Work work = receive.getWork();
        workMapper.insert(work);
        logger.info("作业插入成功主键为" + work.getId());
        WorkStart workStart = new WorkStart();
        workStart.setStartTime(work.getStartTime());
        workStart.setWorkId(work.getId());
        workStart.setStartTimeLong(workStart.getStartTime().getTime());
        workStartMapper.insert(workStart);
        JonStartRun.queue.put(workStart);
        logger.info("workStart:" + workStart.getId());
        TaskResource taskResource = new TaskResource();
        taskResource.setTaskInfo(receive.getTaskResourceInfo());
        taskResource.setWorkId(work.getId());
        taskResourceMapper.insert(taskResource);
        logger.info("资料插入成功主键为：" + taskResource.getId());
        for(Integer i: receive.getIds()) {
            WorkType workType = new WorkType();
            workType.setStuId(i);
            workType.setWorkId(work.getId());
            workType.setType(0);
            workTypeMapper.insert(workType);
            logger.info("学生插入成功主键为：" + workType.getId());
        }
       //throw new RuntimeException();
        return Msg.success();
    }

    /**
     * 学生获取某个作业
     * @param wordId
     * @return
     */
    public Msg selectWork(Integer wordId,Integer stuId) {
        // 获取学生这个作业的信息
        Example example1 = new Example(WorkType.class);
        example1.createCriteria().andEqualTo("workId",wordId).andEqualTo("stuId",stuId);
        List<WorkType> workTypes = workTypeMapper.selectByExample(example1);
        if(workTypes.size() == 0) {
            return Msg.fail();
        }
        logger.info("wordId:" + wordId);
        // 获取作业详情等
        Work work = workMapper.selectByPrimaryKey(wordId);
        logger.info(work.toString());
        // 获取作业内容资料等
        Example example = new Example(TaskResource.class);
        example.createCriteria().andEqualTo("workId",wordId);
        List<TaskResource> taskResources = taskResourceMapper.selectByExample(example);
        logger.info(taskResources.get(0).toString());
        for(WorkType workType: workTypes) {
            workType.setTaskResourceList(taskResources);
            workType.setWork(work);
        }
        return Msg.success().add("info",workTypes.get(0));
    }

    /**
     * 备注添加 -- 作业完成
     * @param taskResourceType
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Msg insertTaskType(TaskResourceType taskResourceType) {
        if(taskResourceType.getStuId() == null || taskResourceType.getTaskResourceId() == null) {
            return Msg.fail().add("info","格式错误");
        }
        taskResourceTypeMapper.insert(taskResourceType);
        logger.info(taskResourceType.getId() + "");
        return Msg.success().add("info","添加成功");
    }

    /**
     * 开始答题
     * @param id
     * @param date 开始时间
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Msg updateWorkType(Integer id, Date date) {
        if(date == null || id == null) {
            return Msg.fail();
        }
        // 更新开始时间
        WorkType workType = new WorkType();
        workType.setStartTime(date);
        Example example = new Example(WorkType.class);
        example.createCriteria().andEqualTo("id",id);
        workTypeMapper.updateByExampleSelective(workType,example);

        // 查找到作业内容
        WorkType workType1 = workTypeMapper.selectByPrimaryKey(id);
        Work work = workMapper.selectByPrimaryKey(workType1.getWorkId());
        // 判断是否需要限时完成
        if(work.getTimeLong() == null || work.getTimeLong() == 0) {
            logger.info("不需要进行限时完成");
            return Msg.success().add("info","修改成功");
        }
        // 需要
        // 判断结束时间为作业截止时间还是开始时间+时长
        Long startTime = date.getTime();
        Long endTime = work.getEndTime().getTime();
        // 结束时间为截止时间
        WorkTimeLimit workTimeLimit = new WorkTimeLimit();
        workTimeLimit.setWorkId(workType1.getWorkId());
        workTimeLimit.setStuId(workType1.getStuId());
        if(startTime + work.getTimeLong() > endTime) {
            workTimeLimit.setEndTime(new Date(endTime));
        } else {
            workTimeLimit.setEndTime(new Date(startTime + work.getTimeLong()));
        }
        // 添加redis 的zset 中
        workTimeLimit.setEndTimeLong(workTimeLimit.getEndTime().getTime());
        redisUtil.zsetAdd("delayQueue",workTimeLimit.getWorkId()+"&" + workTimeLimit.getStuId(),workTimeLimit.getEndTimeLong());
        workTimeLimitMapper.insert(workTimeLimit);
        logger.info("限时任务插入成功");
        return Msg.success().add("info","修改成功");
    }

    /**
     * 完成作业
     */
    @Transactional(rollbackFor = Exception.class)
    public Msg insertTaskTypeAndUpdateWorkType(Integer taskResourceId,
                                               Integer workId, String info,
                                               Date endTime, Integer stuId) {
        // 插入资料记录
        Msg msg = insertTaskType(taskResourceId, stuId, info);
        if(msg.getCode() == 100) {
            logger.info("资料记录插入成功");
        }
        // 修改状态
        Work work = workMapper.selectByPrimaryKey(workId);
        // 不需要限时完成
        if(work.getTimeLong() == null || work.getTimeLong() == 0) {
            if(work.getEndTime().getTime() + 1000 > endTime.getTime()) {
                workTypeMapper.updateWorkTypeByIdL(workId,stuId,2,endTime);
                logger.info("不需要限时完成提交成功");
            }else {
                return Msg.success().add("info","已经过期");
            }
        } else {
            WorkType workType = workTypeMapper.findByWorkIdAndStuId(workId, stuId);
            long endTimeByWorkType = workType.getStartTime().getTime() + work.getTimeLong();
            endTimeByWorkType = Math.min(endTimeByWorkType,work.getEndTime().getTime());
            if(endTimeByWorkType + 1000 > endTime.getTime()) {
                // 按时完成
                workTypeMapper.updateWorkTypeByIdL(workId,stuId,2,endTime);
            }  else {
                return Msg.success().add("info","已经过期");
            }
        }
        return Msg.success();
    }

    /**
     * 备注
     * @param taskResourceId
     * @param stuId
     * @param info
     * @return
     */
    public Msg insertTaskType(Integer taskResourceId,
                              Integer stuId, String info) {
        TaskResourceType taskResourceType = new TaskResourceType();
        taskResourceType.setInfo(info);
        taskResourceType.setStuId(stuId);
        taskResourceType.setTaskResourceId(taskResourceId);
        taskResourceTypeMapper.insert(taskResourceType);
        return Msg.success();
    }

}
