package com.aiworkflow.domain.process;

/**
 * 流程状态枚举
 */
public enum ProcessStatus {
    
    /**
     * 草稿状态 - 流程刚创建，尚未激活
     */
    DRAFT("草稿"),
    
    /**
     * 激活状态 - 流程已激活，可以创建实例
     */
    ACTIVE("激活"),
    
    /**
     * 暂停状态 - 流程暂停，不能创建新实例，但现有实例可以继续执行
     */
    SUSPENDED("暂停"),
    
    /**
     * 已删除状态 - 流程已逻辑删除
     */
    DELETED("已删除");
    
    private final String description;
    
    ProcessStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 检查是否可以创建新实例
     */
    public boolean canCreateInstance() {
        return this == ACTIVE;
    }
    
    /**
     * 检查是否可以编辑流程定义
     */
    public boolean canEdit() {
        return this == DRAFT;
    }
    
    /**
     * 检查是否可以激活
     */
    public boolean canActivate() {
        return this == DRAFT || this == SUSPENDED;
    }
    
    /**
     * 检查是否可以暂停
     */
    public boolean canSuspend() {
        return this == ACTIVE;
    }
    
    /**
     * 检查是否可以删除
     */
    public boolean canDelete() {
        return this != DELETED;
    }
}