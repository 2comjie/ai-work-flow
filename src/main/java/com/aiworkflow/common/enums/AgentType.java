package com.aiworkflow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Agent类型枚举
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum AgentType {
    
    LLM("LLM", "大语言模型Agent"),
    TOOL("TOOL", "工具Agent"),
    CUSTOM("CUSTOM", "自定义Agent");

    private final String code;
    private final String description;
}