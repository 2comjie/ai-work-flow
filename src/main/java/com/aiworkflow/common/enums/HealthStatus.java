package com.aiworkflow.common.enums;

/**
 * 健康状态枚举
 */
public enum HealthStatus {
    /**
     * 健康
     */
    HEALTHY("健康"),
    
    /**
     * 不健康
     */
    UNHEALTHY("不健康"),
    
    /**
     * 未知
     */
    UNKNOWN("未知");

    private final String description;

    HealthStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}