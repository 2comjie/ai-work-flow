package com.aiworkflow.engine.agent;

import com.aiworkflow.engine.core.ProcessEngineException;
import com.aiworkflow.engine.model.TaskInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent管理器
 * 负责Agent注册、选择、负载均衡、健康检查等功能
 */
@Component
@Slf4j
public class AgentManager {

    @Autowired
    private AgentRegistry agentRegistry;

    @Autowired
    private LoadBalancer loadBalancer;

    @Autowired
    private HealthChecker healthChecker;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Agent实例缓存
    private final Map<String, Agent> agentCache = new ConcurrentHashMap<>();

    /**
     * 注册Agent
     */
    public String registerAgent(Agent agent) {
        log.info("注册Agent: agentId={}, type={}", agent.getAgentId(), agent.getCapability().getAgentType());

        try {
            // 1. 验证Agent
            validateAgent(agent);

            // 2. 注册到注册中心
            agentRegistry.register(agent);

            // 3. 加入缓存
            agentCache.put(agent.getAgentId(), agent);

            // 4. 启动健康检查
            healthChecker.startHealthCheck(agent.getAgentId());

            log.info("Agent注册成功: agentId={}", agent.getAgentId());
            return agent.getAgentId();

        } catch (Exception e) {
            log.error("Agent注册失败: agentId={}", agent.getAgentId(), e);
            throw new ProcessEngineException.AgentException("Agent注册失败", e);
        }
    }

    /**
     * 注销Agent
     */
    public void unregisterAgent(String agentId) {
        log.info("注销Agent: agentId={}", agentId);

        try {
            // 1. 从注册中心移除
            agentRegistry.unregister(agentId);

            // 2. 从缓存移除
            agentCache.remove(agentId);

            // 3. 停止健康检查
            healthChecker.stopHealthCheck(agentId);

            log.info("Agent注销成功: agentId={}", agentId);

        } catch (Exception e) {
            log.error("Agent注销失败: agentId={}", agentId, e);
            throw new ProcessEngineException.AgentException("Agent注销失败", e);
        }
    }

    /**
     * 为任务选择最佳Agent
     */
    public String selectBestAgent(TaskInstance task) {
        log.debug("为任务选择Agent: taskId={}, taskType={}", task.getTaskId(), task.getTaskType());

        try {
            // 1. 获取可用的Agent列表
            List<Agent> availableAgents = getAvailableAgents(task);

            if (availableAgents.isEmpty()) {
                throw new ProcessEngineException.AgentException("没有可用的Agent处理任务: " + task.getTaskType());
            }

            // 2. 使用负载均衡算法选择Agent
            Agent selectedAgent = loadBalancer.select(availableAgents, task);

            log.info("选择Agent成功: taskId={}, agentId={}", task.getTaskId(), selectedAgent.getAgentId());
            return selectedAgent.getAgentId();

        } catch (Exception e) {
            log.error("选择Agent失败: taskId={}", task.getTaskId(), e);
            throw new ProcessEngineException.AgentException("选择Agent失败", e);
        }
    }

    /**
     * 获取Agent实例
     */
    public Agent getAgent(String agentId) {
        Agent agent = agentCache.get(agentId);
        if (agent == null) {
            throw new ProcessEngineException.AgentException("Agent不存在: " + agentId);
        }
        return agent;
    }

    /**
     * 获取所有可用Agent
     */
    public List<Agent> getAllAvailableAgents() {
        return agentRegistry.getAllActiveAgents().stream()
            .filter(agent -> healthChecker.isHealthy(agent.getAgentId()))
            .toList();
    }

    /**
     * 获取指定类型的可用Agent
     */
    public List<Agent> getAvailableAgents(TaskInstance task) {
        return agentRegistry.getAgentsByCapability(task.getTaskType()).stream()
            .filter(agent -> healthChecker.isHealthy(agent.getAgentId()))
            .filter(agent -> canHandleTask(agent, task))
            .toList();
    }

    /**
     * 更新Agent负载信息
     */
    public void updateAgentLoad(String agentId, AgentLoadInfo loadInfo) {
        String loadKey = "ai_workflow:agent_load:" + agentId;
        redisTemplate.opsForValue().set(loadKey, loadInfo);
        
        log.debug("更新Agent负载: agentId={}, load={}", agentId, loadInfo);
    }

    /**
     * 获取Agent负载信息
     */
    public AgentLoadInfo getAgentLoad(String agentId) {
        String loadKey = "ai_workflow:agent_load:" + agentId;
        Object load = redisTemplate.opsForValue().get(loadKey);
        return load != null ? (AgentLoadInfo) load : AgentLoadInfo.empty();
    }

    /**
     * Agent心跳处理
     */
    public void handleHeartbeat(String agentId, AgentHealthStatus healthStatus) {
        log.debug("处理Agent心跳: agentId={}, status={}", agentId, healthStatus);

        try {
            // 1. 更新健康状态
            healthChecker.updateHealthStatus(agentId, healthStatus);

            // 2. 更新注册信息
            agentRegistry.updateLastHeartbeat(agentId);

            // 3. 如果Agent不健康，暂时移除
            if (healthStatus != AgentHealthStatus.HEALTHY) {
                log.warn("Agent不健康: agentId={}, status={}", agentId, healthStatus);
                // 可以考虑暂时从负载均衡中移除
            }

        } catch (Exception e) {
            log.error("处理Agent心跳失败: agentId={}", agentId, e);
        }
    }

    /**
     * 验证Agent
     */
    private void validateAgent(Agent agent) {
        if (agent.getAgentId() == null || agent.getAgentId().trim().isEmpty()) {
            throw new ProcessEngineException.AgentException("Agent ID不能为空");
        }

        if (agent.getCapability() == null) {
            throw new ProcessEngineException.AgentException("Agent能力描述不能为空");
        }

        // 检查Agent ID是否已存在
        if (agentRegistry.exists(agent.getAgentId())) {
            throw new ProcessEngineException.AgentException("Agent ID已存在: " + agent.getAgentId());
        }
    }

    /**
     * 判断Agent是否能处理任务
     */
    private boolean canHandleTask(Agent agent, TaskInstance task) {
        AgentCapability capability = agent.getCapability();
        
        // 检查任务类型匹配
        if (!capability.getSupportedTaskTypes().contains(task.getTaskType())) {
            return false;
        }

        // 检查当前负载
        AgentLoadInfo loadInfo = getAgentLoad(agent.getAgentId());
        if (loadInfo.getCurrentTasks() >= capability.getMaxConcurrency()) {
            return false;
        }

        // 检查优先级要求
        if (task.getPriority() != null && capability.getMinPriority() != null) {
            if (task.getPriority() < capability.getMinPriority()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取Agent统计信息
     */
    public AgentStatistics getAgentStatistics() {
        List<Agent> allAgents = agentRegistry.getAllAgents();
        
        long totalAgents = allAgents.size();
        long activeAgents = allAgents.stream()
            .filter(agent -> healthChecker.isHealthy(agent.getAgentId()))
            .count();
        
        Map<String, Long> agentsByType = allAgents.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                agent -> agent.getCapability().getAgentType().toString(),
                java.util.stream.Collectors.counting()
            ));

        return AgentStatistics.builder()
            .totalAgents(totalAgents)
            .activeAgents(activeAgents)
            .agentsByType(agentsByType)
            .build();
    }
}