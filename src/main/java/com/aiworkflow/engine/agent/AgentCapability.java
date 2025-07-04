package com.aiworkflow.engine.agent;

import com.aiworkflow.engine.model.TaskInstance;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * Agent能力描述
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCapability {

    /**
     * Agent类型
     */
    private AgentType agentType;

    /**
     * 支持的任务类型
     */
    private Set<TaskInstance.TaskType> supportedTaskTypes;

    /**
     * 支持的语言
     */
    private List<String> supportedLanguages;

    /**
     * 最大并发任务数
     */
    private Integer maxConcurrency;

    /**
     * 最小优先级（只处理大于等于此优先级的任务）
     */
    private Integer minPriority;

    /**
     * 平均响应时间（毫秒）
     */
    private Long avgResponseTime;

    /**
     * 成功率（0.0-1.0）
     */
    private Double successRate;

    /**
     * 扩展属性
     */
    private java.util.Map<String, Object> properties;

    /**
     * Agent类型枚举
     */
    public enum AgentType {
        LLM("大语言模型Agent"),
        TOOL("工具Agent"),
        CUSTOM("自定义Agent"),
        COMPOSITE("复合Agent");

        private final String description;

        AgentType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 判断是否支持指定任务类型
     */
    public boolean supports(TaskInstance.TaskType taskType) {
        return supportedTaskTypes != null && supportedTaskTypes.contains(taskType);
    }

    /**
     * 判断是否支持指定语言
     */
    public boolean supportsLanguage(String language) {
        return supportedLanguages != null && supportedLanguages.contains(language);
    }

    /**
     * 获取属性值
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }

    /**
     * 设置属性
     */
    public void setProperty(String key, Object value) {
        if (properties == null) {
            properties = new java.util.HashMap<>();
        }
        properties.put(key, value);
    }
}