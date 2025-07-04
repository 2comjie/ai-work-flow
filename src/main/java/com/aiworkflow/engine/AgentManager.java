package com.aiworkflow.engine;

import com.aiworkflow.agent.Agent;
import com.aiworkflow.agent.model.Task;
import com.aiworkflow.common.enums.AgentStatus;
import com.aiworkflow.common.enums.HealthStatus;
import com.aiworkflow.entity.AgentRegistry;
import com.aiworkflow.repository.AgentRegistryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Agent管理器
 * 
 * @author AI Workflow Team
 */
@Component
@Slf4j
public class AgentManager {

    private static final String AGENT_LOAD_PREFIX = "ai_workflow:agent_load:";
    
    @Autowired
    private AgentRegistryRepository agentRegistryRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    // 内存中的Agent实例缓存
    private final Map<String, Agent> agentCache = new ConcurrentHashMap<>();
    
    /**
     * 注册Agent
     * 
     * @param agent Agent实例
     * @return 是否注册成功
     */
    public boolean registerAgent(Agent agent) {
        try {
            log.info("注册Agent: agentId={}, agentName={}", agent.getAgentId(), agent.getAgentName());
            
            // 初始化Agent
            agent.initialize();
            
            // 创建数据库记录
            AgentRegistry registry = new AgentRegistry();
            registry.setAgentId(agent.getAgentId());
            registry.setAgentName(agent.getAgentName());
            registry.setAgentType(agent.getCapability().getSupportedTaskTypes().isEmpty() ? 
                com.aiworkflow.common.enums.AgentType.CUSTOM : 
                com.aiworkflow.common.enums.AgentType.LLM);
            registry.setStatus(AgentStatus.ACTIVE);
            registry.setHealthStatus(agent.getHealthStatus());
            registry.setLastHeartbeat(LocalDateTime.now());
            
            // 保存能力信息
            Map<String, Object> capabilities = new ConcurrentHashMap<>();
            capabilities.put("supportedTaskTypes", agent.getCapability().getSupportedTaskTypes());
            capabilities.put("supportedSkills", agent.getCapability().getSupportedSkills());
            capabilities.put("maxConcurrency", agent.getCapability().getMaxConcurrency());
            capabilities.put("supportedLanguages", agent.getCapability().getSupportedLanguages());
            registry.setCapabilities(capabilities);
            
            agentRegistryRepository.save(registry);
            
            // 缓存Agent实例
            agentCache.put(agent.getAgentId(), agent);
            
            // 初始化负载信息
            updateAgentLoad(agent.getAgentId(), 0);
            
            log.info("Agent注册成功: agentId={}", agent.getAgentId());
            return true;
            
        } catch (Exception e) {
            log.error("Agent注册失败: agentId={}", agent.getAgentId(), e);
            return false;
        }
    }
    
    /**
     * 注销Agent
     * 
     * @param agentId Agent ID
     * @return 是否注销成功
     */
    public boolean unregisterAgent(String agentId) {
        try {
            log.info("注销Agent: agentId={}", agentId);
            
            // 从缓存中移除
            Agent agent = agentCache.remove(agentId);
            if (agent != null) {
                agent.destroy();
            }
            
            // 更新数据库状态
            AgentRegistry registry = agentRegistryRepository.findById(agentId).orElse(null);
            if (registry != null) {
                registry.setStatus(AgentStatus.INACTIVE);
                registry.setHealthStatus(HealthStatus.UNKNOWN);
                agentRegistryRepository.save(registry);
            }
            
            // 清理负载信息
            String loadKey = AGENT_LOAD_PREFIX + agentId;
            redisTemplate.delete(loadKey);
            
            log.info("Agent注销成功: agentId={}", agentId);
            return true;
            
        } catch (Exception e) {
            log.error("Agent注销失败: agentId={}", agentId, e);
            return false;
        }
    }
    
    /**
     * 选择合适的Agent执行任务
     * 
     * @param task 任务
     * @return 选中的Agent
     */
    public Agent selectAgent(Task task) {
        try {
            log.debug("选择Agent执行任务: taskId={}, taskType={}", task.getTaskId(), task.getTaskType());
            
            // 获取所有活跃的Agent
            List<AgentRegistry> activeAgents = agentRegistryRepository.findByStatusAndHealthStatus(
                AgentStatus.ACTIVE, HealthStatus.HEALTHY);
            
            if (activeAgents.isEmpty()) {
                log.warn("没有可用的Agent");
                return null;
            }
            
            // 筛选支持该任务类型的Agent
            List<AgentRegistry> suitableAgents = activeAgents.stream()
                .filter(agent -> canHandleTask(agent, task))
                .toList();
            
            if (suitableAgents.isEmpty()) {
                log.warn("没有找到支持任务类型的Agent: taskType={}", task.getTaskType());
                return null;
            }
            
            // 负载均衡选择Agent
            AgentRegistry selectedRegistry = selectByLoadBalancing(suitableAgents);
            Agent selectedAgent = agentCache.get(selectedRegistry.getAgentId());
            
            if (selectedAgent != null) {
                // 增加负载计数
                increaseAgentLoad(selectedAgent.getAgentId());
                log.debug("选择Agent: agentId={}, agentName={}", 
                    selectedAgent.getAgentId(), selectedAgent.getAgentName());
            }
            
            return selectedAgent;
            
        } catch (Exception e) {
            log.error("选择Agent失败: taskId={}", task.getTaskId(), e);
            return null;
        }
    }
    
    /**
     * 检查Agent是否可以处理任务
     * 
     * @param agentRegistry Agent注册信息
     * @param task 任务
     * @return 是否可以处理
     */
    private boolean canHandleTask(AgentRegistry agentRegistry, Task task) {
        Map<String, Object> capabilities = agentRegistry.getCapabilities();
        if (capabilities == null) {
            return false;
        }
        
        @SuppressWarnings("unchecked")
        List<String> supportedTaskTypes = (List<String>) capabilities.get("supportedTaskTypes");
        
        return supportedTaskTypes != null && 
               supportedTaskTypes.contains(task.getTaskType().name());
    }
    
    /**
     * 负载均衡选择Agent
     * 
     * @param agents Agent列表
     * @return 选中的Agent
     */
    private AgentRegistry selectByLoadBalancing(List<AgentRegistry> agents) {
        // 简单的加权随机负载均衡
        AgentRegistry bestAgent = null;
        int minLoad = Integer.MAX_VALUE;
        
        for (AgentRegistry agent : agents) {
            int currentLoad = getAgentLoad(agent.getAgentId());
            if (currentLoad < minLoad) {
                minLoad = currentLoad;
                bestAgent = agent;
            }
        }
        
        // 如果负载相同，随机选择
        if (bestAgent == null && !agents.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(agents.size());
            bestAgent = agents.get(randomIndex);
        }
        
        return bestAgent;
    }
    
    /**
     * 获取Agent当前负载
     * 
     * @param agentId Agent ID
     * @return 当前负载
     */
    private int getAgentLoad(String agentId) {
        try {
            String loadKey = AGENT_LOAD_PREFIX + agentId;
            Object load = redisTemplate.opsForValue().get(loadKey);
            return load != null ? Integer.parseInt(load.toString()) : 0;
        } catch (Exception e) {
            log.warn("获取Agent负载失败: agentId={}", agentId, e);
            return 0;
        }
    }
    
    /**
     * 更新Agent负载
     * 
     * @param agentId Agent ID
     * @param load 负载值
     */
    private void updateAgentLoad(String agentId, int load) {
        try {
            String loadKey = AGENT_LOAD_PREFIX + agentId;
            redisTemplate.opsForValue().set(loadKey, load);
        } catch (Exception e) {
            log.warn("更新Agent负载失败: agentId={}", agentId, e);
        }
    }
    
    /**
     * 增加Agent负载
     * 
     * @param agentId Agent ID
     */
    private void increaseAgentLoad(String agentId) {
        try {
            String loadKey = AGENT_LOAD_PREFIX + agentId;
            redisTemplate.opsForValue().increment(loadKey);
        } catch (Exception e) {
            log.warn("增加Agent负载失败: agentId={}", agentId, e);
        }
    }
    
    /**
     * 减少Agent负载
     * 
     * @param agentId Agent ID
     */
    public void decreaseAgentLoad(String agentId) {
        try {
            String loadKey = AGENT_LOAD_PREFIX + agentId;
            redisTemplate.opsForValue().decrement(loadKey);
        } catch (Exception e) {
            log.warn("减少Agent负载失败: agentId={}", agentId, e);
        }
    }
    
    /**
     * 定期健康检查
     */
    @Scheduled(fixedRate = 30000) // 每30秒执行一次
    public void healthCheck() {
        try {
            log.debug("开始Agent健康检查");
            
            for (Map.Entry<String, Agent> entry : agentCache.entrySet()) {
                String agentId = entry.getKey();
                Agent agent = entry.getValue();
                
                try {
                    HealthStatus healthStatus = agent.getHealthStatus();
                    
                    // 更新数据库中的健康状态
                    AgentRegistry registry = agentRegistryRepository.findById(agentId).orElse(null);
                    if (registry != null) {
                        registry.setHealthStatus(healthStatus);
                        registry.setLastHeartbeat(LocalDateTime.now());
                        agentRegistryRepository.save(registry);
                    }
                    
                } catch (Exception e) {
                    log.warn("Agent健康检查失败: agentId={}", agentId, e);
                    
                    // 标记为不健康
                    AgentRegistry registry = agentRegistryRepository.findById(agentId).orElse(null);
                    if (registry != null) {
                        registry.setHealthStatus(HealthStatus.UNHEALTHY);
                        agentRegistryRepository.save(registry);
                    }
                }
            }
            
            log.debug("Agent健康检查完成");
            
        } catch (Exception e) {
            log.error("Agent健康检查异常", e);
        }
    }
}