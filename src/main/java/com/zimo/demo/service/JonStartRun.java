package com.zimo.demo.service;

import com.zimo.demo.bean.Work;
import com.zimo.demo.bean.WorkEnd;
import com.zimo.demo.bean.WorkStart;
import com.zimo.demo.bean.WorkTimeLimit;
import com.zimo.demo.mybatis.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.DelayQueue;

@Component
public class JonStartRun implements ApplicationRunner {

    private static final Logger logger =LoggerFactory.getLogger(JonStartRun.class);

    public  static DelayQueue<WorkStart> queue = new DelayQueue<>();
    private boolean flag = false;

    private boolean endFlag = false;
    public static DelayQueue<WorkEnd> workEndDelayQueue = new DelayQueue<>();


    private boolean limieFlag = false;
    public static DelayQueue<WorkTimeLimit> workTimeLimitDelayQueue = new DelayQueue<>();

    @Autowired
    WorkStartMapper workStartMapper;

    @Autowired
    WorkTypeMapper workTypeMapper;

    @Autowired
    WorkEndMapper workEndMapper;

    @Autowired
    WorkMapper workMapper;

    @Autowired
    WorkTimeLimitMapper workTimeLimitMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(() -> {
            while(true) {
                // 首次加载
                if(!flag) {
                    logger.info(Thread.currentThread().getName() + "启动加载数据");
                    List<WorkStart> workStarts = workStartMapper.findListBeforeTom();
                    for(WorkStart workStart: workStarts) {
                        workStart.setStartTimeLong(workStart.getStartTime().getTime());
                        queue.offer(workStart);
                    }
                    logger.info(Thread.currentThread().getName() + "加载完毕");
                    flag = true;
                } else {
                    while(true) {
                        while(queue.size() != 0) {
                            try {
                                WorkStart take = queue.take();
                                // 修改状态为开始
                                workTypeMapper.updateWorkByWorkId(take.getWorkId());
                                // 删除表记录
                                workStartMapper.deleteByPrimaryKey(take.getId());
                                // 添加截止完成
                                Work work = workMapper.selectByPrimaryKey(take.getWorkId());
                                WorkEnd workEnd = new WorkEnd();
                                workEnd.setEndTime(work.getEndTime());
                                workEnd.setWorkId(work.getId());
                                workEndMapper.insert(workEnd);
                                workEnd.setEndTimeLong(workEnd.getEndTime().getTime());
                                workEndDelayQueue.put(workEnd);
                                logger.info("修改，删除， 添加完成");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        logger.info(Thread.currentThread().getName() + "暂时没有定时发布的job");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        },"定时发布线程").start();

        new Thread(() -> {
            while(true) {
                if(!endFlag) {
                    logger.info(Thread.currentThread().getName() + "截止首次加载");
                    List<WorkEnd> workEnds = workEndMapper.findListWorkBeforeTom();
                    for (WorkEnd workEnd : workEnds) {
                        workEnd.setEndTimeLong(workEnd.getEndTime().getTime());
                        workEndDelayQueue.offer(workEnd);
                    }
                    logger.info(Thread.currentThread().getName() + "加载完毕");
                    endFlag = true;
                } else {

                    while(true) {
                        while(workEndDelayQueue.size() != 0) {
                            try {
                                WorkEnd take = workEndDelayQueue.take();
                                // 修改状态
                                workTypeMapper.updateWorkByEndTime(take.getWorkId());
                                // 删除记录
                                workEndMapper.deleteByPrimaryKey(take.getId());
                                logger.info("修改， 删除 执行完毕");
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        logger.info(Thread.currentThread().getName() + "暂时没有截止的job");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        },"截止时间线程").start();

        new Thread(() -> {
            while(true) {
                if(!limieFlag) {
                    List<WorkTimeLimit> workTimeLimits = workTimeLimitMapper.findListBeforeTom();
                    for (WorkTimeLimit workTimeLimit : workTimeLimits) {
                        workTimeLimit.setEndTimeLong(workTimeLimit.getEndTime().getTime());
                        workTimeLimitDelayQueue.offer(workTimeLimit);
                    }
                    logger.info(Thread.currentThread().getName() + "加载完毕");
                    limieFlag = true;
                } else {
                    while(true) {
                        while(workTimeLimitDelayQueue.size() != 0) {
                            WorkTimeLimit take = null;
                            try {
                                take = workTimeLimitDelayQueue.take();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            workTypeMapper.updateWorkTypeById(take.getWorkId(),take.getStuId(),3,null);
                            workTimeLimitMapper.deleteByWorkId(take.getWorkId(),take.getStuId());
                            logger.info("修改，删除执行完毕");
                        }
                        logger.info(Thread.currentThread().getName() + "暂时没有限时完成的job");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        },"限时任务线程").start();
    }
}
