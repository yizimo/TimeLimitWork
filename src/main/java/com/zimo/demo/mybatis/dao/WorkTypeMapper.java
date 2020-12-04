package com.zimo.demo.mybatis.dao;

import com.zimo.demo.bean.WorkType;
import com.zimo.demo.mybatis.TkMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

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

    @Select("select * from work_stu_finish where stu_id = #{stuId} and `type` = 1 and work_id = #{workId} and start_time is not null")
    List<WorkType> selectWorkTypeById(@Param("workId") Integer workId
            , @Param("stuId") Integer stuId);

    @Select("select id from work_stu_finish where work_id = #{id} and `type` = 0")
    List<WorkType> selectIdByIdAndTypeNotStart(Integer id);

    @Select("select id from work_stu_finish where id = #{id} and start_time is null")
    List<WorkType> selectStartTimeById(Integer id);

    @Select("select id from work_stu_finish where work_id = #{workId} and `type` = 1")
    List<WorkType> selectIdByWorkAndIsNotFinish(Integer workId);
}
