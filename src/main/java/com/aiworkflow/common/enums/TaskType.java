package com.aiworkflow.common.enums;

/**
 * 任务类型枚举
 */
public enum TaskType {
    /**
     * 用户任务
     */
    USER_TASK("用户任务"),
    
    /**
     * 服务任务
     */
    SERVICE_TASK("服务任务"),
    
    /**
     * 脚本任务
     */
    SCRIPT_TASK("脚本任务"),
    
    /**
     * Agent任务
     */
    AGENT_TASK("Agent任务");

    private final String description;

    TaskType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}