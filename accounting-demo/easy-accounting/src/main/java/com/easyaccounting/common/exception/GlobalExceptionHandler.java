package com.easyaccounting.common.exception;

import com.easyaccounting.common.result.Result;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 *
 * <p>统一捕获 Controller 层抛出的异常，转换为标准响应格式。</p>
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MeterRegistry meterRegistry;
    private static final String TRACE_ID = "traceId";
    private static final String METRIC_NAME = "api.errors";

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常 [{}]: code={}, message={}", request.getRequestURI(), e.getCode(), e.getMessage());
        meterRegistry.counter(METRIC_NAME, "type", "business", "code", String.valueOf(e.getCode())).increment();
        
        Result<?> result = Result.error(e.getCode(), e.getMessage());
        result.setTraceId(MDC.get(TRACE_ID));
        return result;
    }

    /**
     * 处理参数校验异常 (JSON Body @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数校验异常 [{}]: {}", request.getRequestURI(), message);
        meterRegistry.counter(METRIC_NAME, "type", "validation").increment();
        
        Result<?> result = Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
        result.setTraceId(MDC.get(TRACE_ID));
        return result;
    }

    /**
     * 处理参数校验异常 (Form @Valid / @Validated)
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.warn("参数绑定异常 [{}]: {}", request.getRequestURI(), message);
        meterRegistry.counter(METRIC_NAME, "type", "validation").increment();
        
        Result<?> result = Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
        result.setTraceId(MDC.get(TRACE_ID));
        return result;
    }

    /**
     * 处理单个参数校验异常 (@Validated on Class)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getMessage();
        // 去掉前缀，只保留错误信息
        if (message != null && message.contains(": ")) {
            message = message.substring(message.indexOf(": ") + 2);
        }
        log.warn("参数约束异常 [{}]: {}", request.getRequestURI(), message);
        meterRegistry.counter(METRIC_NAME, "type", "validation").increment();
        
        Result<?> result = Result.error(ErrorCode.PARAM_ERROR.getCode(), message);
        result.setTraceId(MDC.get(TRACE_ID));
        return result;
    }

    /**
     * 处理 JSON 解析异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("JSON解析异常 [{}]: {}", request.getRequestURI(), e.getMessage());
        meterRegistry.counter(METRIC_NAME, "type", "client_error").increment();
        
        Result<?> result = Result.error(ErrorCode.PARAM_ERROR.getCode(), "请求体格式错误");
        result.setTraceId(MDC.get(TRACE_ID));
        return result;
    }

    /**
     * 处理 404
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.warn("资源未找到 [{}]: {}", request.getRequestURI(), e.getMessage());
        meterRegistry.counter(METRIC_NAME, "type", "not_found").increment();
        
        Result<?> result = Result.error(ErrorCode.NOT_FOUND.getCode(), ErrorCode.NOT_FOUND.getMessage());
        result.setTraceId(MDC.get(TRACE_ID));
        return result;
    }

    /**
     * 处理系统未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统内部错误 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        meterRegistry.counter(METRIC_NAME, "type", "system_error").increment();
        
        Result<?> result = Result.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
        result.setTraceId(MDC.get(TRACE_ID));
        return result;
    }
}
