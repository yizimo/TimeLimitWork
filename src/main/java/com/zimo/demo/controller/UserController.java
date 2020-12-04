package com.zimo.demo.controller;

import com.zimo.demo.inter.Token;
import com.zimo.demo.service.UserService;
import com.zimo.demo.util.Msg;
import com.zimo.demo.util.ResultBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.util.StringUtil;

@RequestMapping("user")
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    UserService userService;

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public ResultBody isLogin(@RequestParam(value = "username") String username,
                                  @RequestParam(value = "password") String password) throws Exception {
        throw new Exception("123");
//        logger.info("username:" + username + (username.length()== 0) + ",password:" + password);
//        Msg msg = userService.isLogin(username, password);
//        if(200 == msg.getCode()) {
//            return ResultBody.error("-1",msg.getExtend().get("info").toString());
//        }
//        return ResultBody.success(msg.getExtend());
    }


}
