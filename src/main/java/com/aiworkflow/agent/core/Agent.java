package com.aiworkflow.agent.core;

import com.aiworkflow.agent.entity.AgentRegistry;
import com.aiworkflow.process.entity.TaskInstance;

import java.util.concurrent.CompletableFuture;

/**
 * Agent核心接口
 * 所有Agent实现必须实现此接口
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
public interface Agent {

    /**
     * 获取Agent唯一标识
     *
     * @return Agent ID
     */
    String getAgentId();

    /**
     * 获取Agent能力描述
     *
     * @return Agent能力
     */
    AgentCapability getCapability();

    /**
     * 执行任务
     *
     * @param task 任务实例
     * @return 任务执行结果
     */
    CompletableFuture<TaskResult> execute(TaskInstance task);

    /**
     * 获取健康状态
     *
     * @return 健康状态
     */
    AgentRegistry.HealthStatus getHealthStatus();

    /**
     * 检查是否可以执行指定任务
     *
     * @param task 任务实例
     * @return 是否可以执行
     */
    boolean canExecute(TaskInstance task);

    /**
     * 获取当前负载
     *
     * @return 当前负载数
     */
    int getCurrentLoad();

    /**
     * 获取最大并发数
     *
     * @return 最大并发数
     */
    int getMaxConcurrency();

    /**
     * 初始化Agent
     */
    void initialize();

    /**
     * 销毁Agent
     */
    void destroy();

    /**
     * 发送心跳
     *
     * @return 心跳响应
     */
    CompletableFuture<HeartbeatResponse> heartbeat();
}