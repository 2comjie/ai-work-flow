package com.aiworkflow.agent.model;

import com.aiworkflow.common.enums.TaskStatus;
import com.aiworkflow.common.enums.TaskType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务模型
 * 
 * @author AI Workflow Team
 */
@Data
public class Task {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务类型
     */
    private TaskType taskType;

    /**
     * 任务状态
     */
    private TaskStatus status;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 输入数据
     */
    private Map<String, Object> inputData;

    /**
     * 任务配置
     */
    private Map<String, Object> configuration;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 截止时间
     */
    private LocalDateTime dueTime;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetries;

    /**
     * 超时时间（毫秒）
     */
    private Long timeoutMs;

    /**
     * 依赖的任务ID列表
     */
    private String[] dependencies;

    /**
     * 检查任务是否超时
     * 
     * @return 是否超时
     */
    public boolean isTimeout() {
        if (timeoutMs == null || createdTime == null) {
            return false;
        }
        return System.currentTimeMillis() - 
               createdTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() 
               > timeoutMs;
    }

    /**
     * 检查是否可以重试
     * 
     * @return 是否可以重试
     */
    public boolean canRetry() {
        return maxRetries != null && retryCount != null && retryCount < maxRetries;
    }
}