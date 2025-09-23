package com.atguigu.lease.common.exception;

import com.atguigu.lease.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice //声明处理全局异常类
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handle(Exception e){
        e.getMessage();
        e.printStackTrace();
        return Result.fail();
    }

    @ExceptionHandler(LeaseException.class)
    @ResponseBody
    public Result error(LeaseException e){
        e.printStackTrace();
        return Result.fail(e.getCode(), e.getMessage());
    }
}