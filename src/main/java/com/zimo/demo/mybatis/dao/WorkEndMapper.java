package com.zimo.demo.mybatis.dao;

import com.zimo.demo.bean.WorkEnd;
import com.zimo.demo.mybatis.TkMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WorkEndMapper extends TkMapper<WorkEnd> {

    @Select("select * from work_end where end_time < #{date}")
    List<WorkEnd> findListWorkBeforeTom(Date date);
}
