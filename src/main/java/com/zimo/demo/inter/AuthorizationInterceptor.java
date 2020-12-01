package com.zimo.demo.inter;

import com.zimo.demo.exception.ZimoException;
import com.zimo.demo.util.RedisUtil;
import com.zimo.demo.util.TokenUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Autowired
    RedisUtil redisUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
              String token = request.getHeader("token");

        if(!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        if(method.isAnnotationPresent(Token.class)) {
            Token annotation = method.getAnnotation(Token.class);
            if(annotation != null) {
                logger.info("token:" + token);
                if(token == null || "".equals(token)) {
                    throw new ZimoException("-1","请登录");
                }
                // 校验token 正确性
                String username = TokenUtils.getInfo(token, "username");
                if("" == username)  {
                    throw new ZimoException("-1","token校验失败");
                }
                String value = redisUtil.get(username,String.class);
                if(value == null || "".equals(value)) {
                    throw new ZimoException("-1","token 不存在");
                }
                if(!token.equals(value)) {
                    throw new ZimoException("-1","token失效");
                }
                String perName = TokenUtils.getInfo(token, "perName");
                String validate = annotation.validate();
                if(validate.equals("管理员") && perName.equals("管理员")) {
                    return true;
                }
                if(validate.equals("教师")) {
                    if(perName.equals("管理员") || perName.equals("教师")) {
                        return true;
                    }
                }
                if(validate.equals("同学")) {
                    if(perName.equals("管理员") || perName.equals("教师") || perName.equals("同学")) {
                        return true;
                    }
                }
                throw new ZimoException("-1","权限不够");
            }
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {
    }

}
