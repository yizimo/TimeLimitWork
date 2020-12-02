package com.zimo.demo.mybatis.dao;

import com.zimo.demo.bean.WorkType;
import com.zimo.demo.mybatis.TkMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface WorkTypeMapper extends TkMapper<WorkType> {

    // 作业结束
    boolean updateWorkTypeById(@Param("workId") Integer workId
            , @Param("stuId") Integer stuId, @Param("type") Integer type, @Param("endTime")Date endTime);

    boolean updateWorkTypeByIdL(@Param("workId") Integer workId
            , @Param("stuId") Integer stuId, @Param("type") Integer type, @Param("endTime")Date endTime);


    // 作业开始
    boolean updateWorkByWorkId(Integer workId);

    boolean updateWorkByEndTime(Integer workId);

    WorkType findByWorkIdAndStuId(@Param("workId") Integer workId,
                                  @Param("stuId") Integer stuId);
}
