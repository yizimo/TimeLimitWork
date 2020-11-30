package com.zimo.demo.controller;

import com.zimo.demo.exception.CommonEnum;
import com.zimo.demo.util.ResultBody;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotFoundController implements ErrorController {
    @Override
    public String getErrorPath() {

        return "/error";
    }

    @RequestMapping(value = {"/error"})
    @ResponseBody
    public ResultBody error(HttpServletRequest request) {

        return ResultBody.error(CommonEnum.NOT_FOUND);
    }
}
