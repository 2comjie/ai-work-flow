package com.aiworkflow.common.enums;

/**
 * 流程状态枚举
 */
public enum ProcessStatus {
    /**
     * 草稿状态
     */
    DRAFT("草稿"),
    
    /**
     * 激活状态
     */
    ACTIVE("激活"),
    
    /**
     * 暂停状态
     */
    SUSPENDED("暂停"),
    
    /**
     * 已删除
     */
    DELETED("已删除");

    private final String description;

    ProcessStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}