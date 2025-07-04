package com.aiworkflow.common.enums;

/**
 * Agent状态枚举
 */
public enum AgentStatus {
    /**
     * 激活状态
     */
    ACTIVE("激活"),
    
    /**
     * 非激活状态
     */
    INACTIVE("非激活"),
    
    /**
     * 维护状态
     */
    MAINTENANCE("维护");

    private final String description;

    AgentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}