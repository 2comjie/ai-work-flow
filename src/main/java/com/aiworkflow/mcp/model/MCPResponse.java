package com.aiworkflow.mcp.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MCP响应模型
 * 
 * @author AI Workflow Team
 */
@Data
public class MCPResponse {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 响应状态
     */
    private ResponseStatus status;

    /**
     * 响应内容
     */
    private String content;

    /**
     * 响应数据
     */
    private Map<String, Object> data;

    /**
     * 错误信息
     */
    private ErrorInfo error;

    /**
     * 令牌使用情况
     */
    private TokenUsage tokenUsage;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTimeMs;

    public enum ResponseStatus {
        SUCCESS,
        ERROR,
        TIMEOUT,
        RATE_LIMITED
    }

    @Data
    public static class ErrorInfo {
        private String errorCode;
        private String errorMessage;
        private String errorType;
    }

    @Data
    public static class TokenUsage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}