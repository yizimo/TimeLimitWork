package com.zimo.demo.exception;

import com.zimo.demo.util.ResultBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 自定义异常
     * @param request
     * @param zimoException
     * @return
     */
    @ExceptionHandler(value = ZimoException.class)
    @ResponseBody
    public ResultBody zimoExceptionHandler(HttpServletRequest request, ZimoException zimoException) {
        logger.error("异常原因:" + zimoException.getErrorMsg());
        return ResultBody.error(zimoException.getErrorCode(),zimoException.getErrorMsg());
    }

// getErrorMsg   public ResultBody ExceptionHandler(HttpServletRequest request, )

    /**
     * 空指针异常
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest request, NullPointerException exception) {
        logger.error("空指针异常:" + exception);
        return ResultBody.error(CommonEnum.BODY_NOT_MATCH);
    }

    /**
     * 请求参数异常
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest request, MissingServletRequestParameterException exception) {
        logger.error("请求参数不正确:" + exception);
        return ResultBody.error(CommonEnum.BODY_NOT_MATCH);
    }

    /**
     * 其他异常
     * @param request
     * @param exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody exceptionHandler(HttpServletRequest request, Exception exception) {
        logger.error("其他异常：" + exception);
        return ResultBody.error(CommonEnum.INTERNAL_SERVER_ERROR);
    }
}
