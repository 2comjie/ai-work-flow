package com.aiworkflow.common.web.result;

import lombok.Getter;

/**
 * 统一响应码枚举
 * 响应码设计规范：
 * - 2xx: 成功响应
 * - 4xx: 客户端错误
 * - 5xx: 服务器错误
 * - 1xxx: 业务逻辑错误
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    SERVER_ERROR(500, "服务端错误");
    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
