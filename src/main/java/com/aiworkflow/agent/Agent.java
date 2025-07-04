package com.aiworkflow.agent;

import com.aiworkflow.agent.model.AgentCapability;
import com.aiworkflow.agent.model.Task;
import com.aiworkflow.agent.model.TaskResult;
import com.aiworkflow.common.enums.HealthStatus;

/**
 * Agent接口
 * 
 * @author AI Workflow Team
 */
public interface Agent {

    /**
     * 获取Agent唯一标识
     * 
     * @return Agent ID
     */
    String getAgentId();

    /**
     * 获取Agent名称
     * 
     * @return Agent名称
     */
    String getAgentName();

    /**
     * 获取Agent能力描述
     * 
     * @return Agent能力
     */
    AgentCapability getCapability();

    /**
     * 执行任务
     * 
     * @param task 任务
     * @return 任务结果
     */
    TaskResult execute(Task task);

    /**
     * 检查健康状态
     * 
     * @return 健康状态
     */
    HealthStatus getHealthStatus();

    /**
     * 初始化Agent
     */
    void initialize();

    /**
     * 销毁Agent
     */
    void destroy();
}