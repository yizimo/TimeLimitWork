package com.zimo.demo.mybatis.dao;

import com.zimo.demo.bean.Work;
import com.zimo.demo.mybatis.TkMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkMapper extends TkMapper<Work> {

    Work getWork(Integer workId);
}
