package com.aiworkflow.common.enums;

/**
 * 连接状态枚举
 */
public enum ConnectionStatus {
    /**
     * 已连接
     */
    CONNECTED("已连接"),
    
    /**
     * 已断开
     */
    DISCONNECTED("已断开"),
    
    /**
     * 错误
     */
    ERROR("错误");

    private final String description;

    ConnectionStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}