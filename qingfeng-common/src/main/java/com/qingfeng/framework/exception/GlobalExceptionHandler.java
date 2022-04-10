package com.qingfeng.framework.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @ProjectName GlobalExceptionHandler
 * @author Administrator
 * @version 1.0.0
 * @Description 全局异常拦截器
 * @createTime 2022/1/11 21:41
 */
@ControllerAdvice//使用该注解表示开启了全局异常的捕获
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的业务异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public  FrontResult bizExceptionHandler(HttpServletRequest req, BizException e){
        logger.error("URL : " + req.getRequestURL().toString());
        logger.error("HTTP_METHOD : " + req.getMethod());
        logger.error("发生业务异常！原因是：{}",e.getErrorMsg());
        return FrontResult.getExceptionResult(e.getErrorCode(),e.getErrorMsg());
    }

    /**
     * 处理空指针的异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public FrontResult exceptionHandler(HttpServletRequest req, NullPointerException e)  {
        logger.error("URL : " + req.getRequestURL().toString());
        logger.error("HTTP_METHOD : " + req.getMethod());
        logger.error("发生空指针异常！原因是:",e);
        return FrontResult.getExceptionResult(ResultCodeEnum.FAIL.getCode(), ResutlMsgEnum.NULLPOINTERFAIL.getMsg());
    }

    /**
     * 处理索引越界异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =IndexOutOfBoundsException.class)
    @ResponseBody
    public FrontResult exceptionHandler(HttpServletRequest req, IndexOutOfBoundsException e){
        logger.error("URL : " + req.getRequestURL().toString());
        logger.error("HTTP_METHOD : " + req.getMethod());
        logger.error("索引越界异常！原因是:",e);
        return FrontResult.getExceptionResult(ResultCodeEnum.FAIL.getCode(), ResutlMsgEnum.INDEXOUTOFBOUNDSFAIL.getMsg());
    }

    /**
     * 处理类未找到异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =ClassNotFoundException.class)
    @ResponseBody
    public FrontResult exceptionHandler(HttpServletRequest req, ClassNotFoundException e)  {
        logger.error("URL : " + req.getRequestURL().toString());
        logger.error("HTTP_METHOD : " + req.getMethod());
        logger.error("发生类未找到异常！原因是:",e);
        return FrontResult.getExceptionResult(ResultCodeEnum.FAIL.getCode(), ResutlMsgEnum.CLASSNOTFOUNDFIL.getMsg());
    }


    /**
     * 处理SQL异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public FrontResult exceptionHandler(HttpServletRequest req, SQLException e)  {
        logger.error("URL : " + req.getRequestURL().toString());
        logger.error("HTTP_METHOD : " + req.getMethod());
        logger.error("发生SQL异常！原因是:",e);
        return FrontResult.getExceptionResult(ResultCodeEnum.FAIL.getCode(), ResutlMsgEnum.SQLFAIL.getMsg());
    }

    /**
     * 处理IO异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = IOException.class)
    @ResponseBody
    public FrontResult exceptionHandler(HttpServletRequest req, IOException e)  {
        logger.error("URL : " + req.getRequestURL().toString());
        logger.error("HTTP_METHOD : " + req.getMethod());
        logger.error("发生IO异常！原因是:",e);
        return FrontResult.getExceptionResult(ResultCodeEnum.FAIL.getCode(), ResutlMsgEnum.IOFAIL.getMsg());
    }


    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public FrontResult exceptionHandler(HttpServletRequest req, Exception e){
        logger.error("URL : " + req.getRequestURL().toString());
        logger.error("HTTP_METHOD : " + req.getMethod());
        logger.error("未知异常！原因是:",e);
        return FrontResult.getExceptionResult(ResultCodeEnum.FAIL.getCode(), ResutlMsgEnum.OTHERFAIL.getMsg());
    }

}