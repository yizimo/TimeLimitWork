package com.zimo.demo.controller;

import com.zimo.demo.bean.*;
import com.zimo.demo.exception.CommonEnum;
import com.zimo.demo.inter.AuthorizationInterceptor;
import com.zimo.demo.inter.Token;
import com.zimo.demo.service.WorkService;
import com.zimo.demo.util.Msg;
import com.zimo.demo.util.ResultBody;
import com.zimo.demo.util.TokenUtils;
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
     * @return
     */
    @GetMapping("/stu")
    @Token(validate = "学生")
    public ResultBody selectWork(@RequestParam Integer workId) {

        Integer stuId = AuthorizationInterceptor.getUserId();
        logger.info(stuId+"");
        Msg msg = workService.selectWork(workId, stuId);
        if(200 == msg.getCode()) {
            return ResultBody.success(null);
        }
        logger.info("学生获取作业信息" + msg.toString());
        return ResultBody.success(msg.getExtend());
    }

    /**
     * 添加备注 --- 作业完成提交
     * @param taskResourceType
     * @return
     */
    @PostMapping("/stu")
    public ResultBody insertTaskInfo(@RequestBody TaskTypeInfo taskResourceType) {
        logger.info("完成作业" + taskResourceType.toString());
        Msg msg = workService.insertTaskTypeAndUpdateWorkType(taskResourceType.getTaskResourceTypeId(),
                taskResourceType.getWorkId(), taskResourceType.getInfo(),
                new Date(), taskResourceType.getStuId());
        logger.info(msg.toString());
        if(msg.getCode() == 200) {
            return ResultBody.error("-1", (String) msg.getExtend().get("info"));
        }
        return ResultBody.success(null);
    }

    /**
     * 开始作业
     * @return
     */
    @PutMapping("/stu/start")
    public ResultBody updateStuTaskStartTime(@RequestBody WorkType workType) {
        Msg msg = workService.updateWorkType(workType.getId(), new Date());
        if(200 == msg.getCode()) {
            return ResultBody.error("-1","数据缺失");
        }
       return ResultBody.success(null);
    }

    @PutMapping("/stu/end")
    public ResultBody updateStuWorkType() {

        return ResultBody.success(null);
    }

    @GetMapping("/size")
    @Token(validate = "学生")
    public ResultBody findListBySizeAndTeaId(@RequestParam("size") int size) {
        Integer userId = AuthorizationInterceptor.getUserId();
        return ResultBody.success(workService.findListWorkByPage(size,userId).getExtend());
    }

    @GetMapping("/size/stu")
    @Token(validate = "学生")
    public ResultBody findListBySizeAndStuId(@RequestParam("size") int size) {
        Integer userId = AuthorizationInterceptor.getUserId();
        return ResultBody.success(workService.findListWorkByPageAndStuId(size,userId).getExtend());
    }
}
