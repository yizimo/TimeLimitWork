package com.zimo.demo.mybatis.dao;

import com.zimo.demo.bean.WorkTimeLimit;
import com.zimo.demo.mybatis.TkMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkTimeLimitMapper extends TkMapper<WorkTimeLimit> {

    @Delete("delete from work_time_limit where work_id = #{workId} and stu_id = #{stuId}")
    void deleteByWorkId(@Param("workId") Integer workId, @Param("stuId") Integer stuId);

    @Select("select * from work_time_limit where end_time < DATE_SUB(curdate(),INTERVAL -1 DAY) ")
    List<WorkTimeLimit> findListBeforeTom();
}
