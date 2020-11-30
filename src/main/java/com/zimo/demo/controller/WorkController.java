package com.zimo.demo.controller;

import com.zimo.demo.bean.Receive;
import com.zimo.demo.bean.TaskResourceType;
import com.zimo.demo.bean.Work;
import com.zimo.demo.exception.CommonEnum;
import com.zimo.demo.service.WorkService;
import com.zimo.demo.util.Msg;
import com.zimo.demo.util.ResultBody;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.ResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping("work")
@RestController
public class WorkController {

    private static final Logger logger = LoggerFactory.getLogger(WorkController.class);

    @Autowired
    WorkService workService;

    /**
     * 发布作业
     * @param receive
     * @return
     */
    @PostMapping
    public ResultBody insertWork(@RequestBody Receive receive) {
        if(receive.getWork().getStartTime() == null) {
            return ResultBody.error("-1","开始时间不能为空");
        }
        if(receive.getWork().getEndTime() == null) {
            return  ResultBody.error("-1","结束时间不能为空");
        }
        logger.info(receive.toString());
        workService.insertWork(receive);
        return ResultBody.success(null);
    }

    /**
     * 学生获取作业
     * @param workId
     * @param stuId
     * @return
     */
    @GetMapping("/stu")
    public ResultBody selectWork(@RequestParam Integer workId, @RequestParam Integer stuId) {
        Msg msg = workService.selectWork(workId, stuId);
        if(200 == msg.getCode()) {
            return ResultBody.success(null);
        }
        logger.info("学生获取作业信息" + msg.toString());
        return ResultBody.success(msg.getExtend());
    }

    /**
     * 添加备注
     * @param taskResourceType
     * @return
     */
    @PostMapping("/stu")
    public ResultBody insertTaskInfo(@RequestBody TaskResourceType taskResourceType) {
        logger.info("添加备注：" + taskResourceType.toString());
        Msg msg = workService.insertTaskType(taskResourceType);
        if(200 == msg.getCode()) {
            return ResultBody.error("-1","数据缺失");
        }
        logger.info("添加备注：" + msg.toString());
        return ResultBody.success(null);
    }

    /**
     * 开始作业
     * @param date
     * @param id
     * @return
     */
    @PutMapping("/stu/start")
    public ResultBody updateStuTaskStartTime(@RequestParam Date date,@RequestParam Integer id) {
        Msg msg = workService.updateWorkType(id, date);
        if(200 == msg.getCode()) {
            return ResultBody.error("-1","数据缺失");
        }
       return ResultBody.success(null);
    }



}
