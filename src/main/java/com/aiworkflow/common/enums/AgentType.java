package com.aiworkflow.common.enums;

/**
 * Agent类型枚举
 */
public enum AgentType {
    /**
     * 大语言模型Agent
     */
    LLM("大语言模型"),
    
    /**
     * 工具Agent
     */
    TOOL("工具"),
    
    /**
     * 自定义Agent
     */
    CUSTOM("自定义");

    private final String description;

    AgentType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}