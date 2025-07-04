package com.aiworkflow.common.result;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * API统一响应结果
 * 
 * @author AI Workflow Team
 */
@Data
public class ApiResult<T> {

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间
     */
    private LocalDateTime timestamp;

    /**
     * 是否成功
     */
    private Boolean success;

    public ApiResult() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiResult(Integer code, String message, T data, Boolean success) {
        this();
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    /**
     * 成功响应
     * 
     * @param data 响应数据
     * @return ApiResult
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data, true);
    }

    /**
     * 成功响应
     * 
     * @param message 响应消息
     * @param data 响应数据
     * @return ApiResult
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return new ApiResult<>(200, message, data, true);
    }

    /**
     * 成功响应（无数据）
     * 
     * @return ApiResult
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "success", null, true);
    }

    /**
     * 错误响应
     * 
     * @param message 错误消息
     * @return ApiResult
     */
    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(500, message, null, false);
    }

    /**
     * 错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @return ApiResult
     */
    public static <T> ApiResult<T> error(Integer code, String message) {
        return new ApiResult<>(code, message, null, false);
    }

    /**
     * 错误响应
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param data 响应数据
     * @return ApiResult
     */
    public static <T> ApiResult<T> error(Integer code, String message, T data) {
        return new ApiResult<>(code, message, data, false);
    }

    /**
     * 参数错误响应
     * 
     * @param message 错误消息
     * @return ApiResult
     */
    public static <T> ApiResult<T> badRequest(String message) {
        return new ApiResult<>(400, message, null, false);
    }

    /**
     * 未授权响应
     * 
     * @param message 错误消息
     * @return ApiResult
     */
    public static <T> ApiResult<T> unauthorized(String message) {
        return new ApiResult<>(401, message, null, false);
    }

    /**
     * 禁止访问响应
     * 
     * @param message 错误消息
     * @return ApiResult
     */
    public static <T> ApiResult<T> forbidden(String message) {
        return new ApiResult<>(403, message, null, false);
    }

    /**
     * 资源未找到响应
     * 
     * @param message 错误消息
     * @return ApiResult
     */
    public static <T> ApiResult<T> notFound(String message) {
        return new ApiResult<>(404, message, null, false);
    }
}