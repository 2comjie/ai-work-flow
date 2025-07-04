package com.aiworkflow.agent.model;

import com.aiworkflow.common.enums.TaskType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Agent能力模型
 * 
 * @author AI Workflow Team
 */
@Data
public class AgentCapability {

    /**
     * 支持的任务类型列表
     */
    private List<TaskType> supportedTaskTypes;

    /**
     * 支持的技能列表
     */
    private List<String> supportedSkills;

    /**
     * 最大并发数
     */
    private Integer maxConcurrency;

    /**
     * 支持的语言列表
     */
    private List<String> supportedLanguages;

    /**
     * 平均处理时间（毫秒）
     */
    private Long avgProcessingTimeMs;

    /**
     * 成功率（0-1之间）
     */
    private Double successRate;

    /**
     * 扩展属性
     */
    private Map<String, Object> extensions;

    /**
     * 检查是否支持指定任务类型
     * 
     * @param taskType 任务类型
     * @return 是否支持
     */
    public boolean supportsTaskType(TaskType taskType) {
        return supportedTaskTypes != null && supportedTaskTypes.contains(taskType);
    }

    /**
     * 检查是否支持指定技能
     * 
     * @param skill 技能名称
     * @return 是否支持
     */
    public boolean supportsSkill(String skill) {
        return supportedSkills != null && supportedSkills.contains(skill);
    }

    /**
     * 检查是否支持指定语言
     * 
     * @param language 语言代码
     * @return 是否支持
     */
    public boolean supportsLanguage(String language) {
        return supportedLanguages != null && supportedLanguages.contains(language);
    }
}