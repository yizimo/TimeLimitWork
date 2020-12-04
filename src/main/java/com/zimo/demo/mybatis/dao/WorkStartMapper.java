package com.zimo.demo.mybatis.dao;

import com.zimo.demo.bean.WorkStart;
import com.zimo.demo.mybatis.TkMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WorkStartMapper extends TkMapper<WorkStart> {
    @Select("select * from work_start where start_time < #{date} ")
    List<WorkStart> findListBeforeTom(Date date);

}
