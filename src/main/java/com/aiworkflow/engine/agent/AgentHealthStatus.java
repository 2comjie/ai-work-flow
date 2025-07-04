package com.aiworkflow.engine.agent;

/**
 * Agent健康状态枚举
 */
public enum AgentHealthStatus {
    
    HEALTHY("健康"),
    UNHEALTHY("不健康"),
    UNKNOWN("未知"),
    MAINTENANCE("维护中"),
    OVERLOADED("过载");

    private final String description;

    AgentHealthStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 判断是否可用
     */
    public boolean isAvailable() {
        return this == HEALTHY;
    }

    /**
     * 判断是否需要关注
     */
    public boolean needsAttention() {
        return this == UNHEALTHY || this == OVERLOADED;
    }
}