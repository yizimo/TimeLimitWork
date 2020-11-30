package com.zimo.demo.mybatis.dao;

import com.zimo.demo.bean.User;
import com.zimo.demo.mybatis.TkMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends TkMapper<User> {

    @Select("select * from user where username = #{username}")
    User findUserByName(String username);

    User findUserPer(Integer id);

}
