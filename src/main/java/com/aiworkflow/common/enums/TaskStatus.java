package com.aiworkflow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态枚举
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TaskStatus {
    
    PENDING("PENDING", "待执行"),
    RUNNING("RUNNING", "执行中"),
    COMPLETED("COMPLETED", "已完成"),
    FAILED("FAILED", "执行失败"),
    SKIPPED("SKIPPED", "已跳过");

    private final String code;
    private final String description;
}