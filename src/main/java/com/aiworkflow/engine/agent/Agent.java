package com.aiworkflow.engine.agent;

import com.aiworkflow.engine.model.TaskInstance;

/**
 * Agent接口
 * 定义了Agent的基本能力和行为
 */
public interface Agent {

    /**
     * 获取Agent唯一标识
     */
    String getAgentId();

    /**
     * 获取Agent能力描述
     */
    AgentCapability getCapability();

    /**
     * 执行任务
     */
    TaskResult execute(TaskInstance task);

    /**
     * 获取健康状态
     */
    AgentHealthStatus getHealthStatus();

    /**
     * 停止Agent
     */
    void shutdown();

    /**
     * 判断是否可以处理指定任务
     */
    default boolean canHandle(TaskInstance task) {
        return getCapability().getSupportedTaskTypes().contains(task.getTaskType());
    }
}