package com.aiworkflow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程状态枚举
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ProcessStatus {
    
    DRAFT("DRAFT", "草稿"),
    ACTIVE("ACTIVE", "激活"),
    SUSPENDED("SUSPENDED", "挂起"),
    DELETED("DELETED", "已删除");

    private final String code;
    private final String description;
}