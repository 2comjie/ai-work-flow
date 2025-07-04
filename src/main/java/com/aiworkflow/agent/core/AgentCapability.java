package com.aiworkflow.agent.core;

import com.aiworkflow.common.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Agent能力描述
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCapability {

    /**
     * 支持的任务类型列表
     */
    private List<TaskType> supportedTaskTypes;

    /**
     * 支持的操作列表
     */
    private List<String> supportedActions;

    /**
     * 支持的输入格式
     */
    private List<String> supportedInputFormats;

    /**
     * 支持的输出格式
     */
    private List<String> supportedOutputFormats;

    /**
     * 支持的语言列表
     */
    private List<String> supportedLanguages;

    /**
     * 最大并发数
     */
    private Integer maxConcurrency;

    /**
     * 平均响应时间（毫秒）
     */
    private Long averageResponseTime;

    /**
     * 支持的参数配置
     */
    private Map<String, Object> supportedParameters;

    /**
     * 能力标签
     */
    private List<String> tags;

    /**
     * 能力描述
     */
    private String description;

    /**
     * 版本信息
     */
    private String version;

    /**
     * 额外属性
     */
    private Map<String, Object> properties;
}