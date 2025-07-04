package com.aiworkflow.mcp.model;

import lombok.Data;

import java.util.Map;

/**
 * MCP请求模型
 * 
 * @author AI Workflow Team
 */
@Data
public class MCPRequest {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求参数
     */
    private Map<String, Object> params;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 模型配置
     */
    private ModelConfig modelConfig;

    /**
     * 工具调用列表
     */
    private ToolCall[] toolCalls;

    /**
     * 资源访问列表
     */
    private ResourceAccess[] resourceAccess;

    @Data
    public static class ModelConfig {
        private String modelName;
        private Double temperature;
        private Integer maxTokens;
        private Map<String, Object> additionalParams;
    }

    @Data
    public static class ToolCall {
        private String toolName;
        private Map<String, Object> arguments;
    }

    @Data
    public static class ResourceAccess {
        private String resourceType;
        private String resourceId;
        private String action;
    }
}