package com.aiworkflow.common.web.handler;

import com.aiworkflow.common.web.exception.BusinessException;
import com.aiworkflow.common.web.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public Result<Void> businessExceptionHandler(BusinessException e) {
        log.error("business exception {} {}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public Result<Void> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtime exception {}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result<Void> exceptionHandler(Exception e) {
        log.error("exception {}", e.getMessage());
        return Result.fail(e.getMessage());
    }
}
