package com.zimo.demo.service;

import com.zimo.demo.bean.User;
import com.zimo.demo.exception.CommonEnum;
import com.zimo.demo.exception.ZimoException;
import com.zimo.demo.mybatis.dao.UserMapper;
import com.zimo.demo.util.Msg;
import com.zimo.demo.util.RedisUtil;
import com.zimo.demo.util.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

@Service
public class UserService {

    protected static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 根据用户名查找 用户
     * @param username 用户名
     * @return
     */
    public Msg findUserByName(String username) {
        User user = userMapper.findUserByName(username);
        logger.info(user.toString());
        return Msg.success().add("user",user);
    }

    /**
     * 判断是否登录成功
     * @param username
     * @param password
     * @return
     */
    public Msg isLogin(String username, String password) {
        String token = "";
        if(StringUtil.isEmpty(username) || "".equals(username)
                || StringUtil.isEmpty(password) || "".equals(password)) {
            logger.info("用户名或者密码为空，username:" + username + ", password:" + password);
            return Msg.fail().add("info","用户名或者密码为空");
        }
        Msg msg = findUserByName(username);
        User user = (User) msg.getExtend().get("user");
        if(user != null) {
            if(password.equals(user.getPassword())) {
                token = userPer(user.getId());
                redisUtil.set(username,token);
                return Msg.success().add("token",token);
            }
        }
        return Msg.fail().add("info","用户名或者密码错误");
    }

    /**
     * 获取用户权限
     * @param id 用户id
     * @return
     */
    private String userPer(Integer id) {
        User user = userMapper.findUserPer(id);
        logger.info(user.toString());
        String token = TokenUtils.token(user.getUsername(), user.getPer().getName());
        return token;
    }

}
