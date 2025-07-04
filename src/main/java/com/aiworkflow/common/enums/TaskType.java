package com.aiworkflow.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务类型枚举
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum TaskType {
    
    USER_TASK("USER_TASK", "用户任务"),
    SERVICE_TASK("SERVICE_TASK", "服务任务"),
    SCRIPT_TASK("SCRIPT_TASK", "脚本任务"),
    AGENT_TASK("AGENT_TASK", "Agent任务"),
    MANUAL_TASK("MANUAL_TASK", "手工任务"),
    RECEIVE_TASK("RECEIVE_TASK", "接收任务"),
    SEND_TASK("SEND_TASK", "发送任务");

    private final String code;
    private final String description;
}