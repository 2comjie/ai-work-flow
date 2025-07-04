package com.aiworkflow.repository;

import com.aiworkflow.entity.AgentRegistry;
import com.aiworkflow.common.enums.AgentStatus;
import com.aiworkflow.common.enums.AgentType;
import com.aiworkflow.common.enums.HealthStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Agent注册Repository
 * 
 * @author AI Workflow Team
 */
@Repository
public interface AgentRegistryRepository extends JpaRepository<AgentRegistry, String> {

    /**
     * 根据状态查找Agent列表
     * 
     * @param status Agent状态
     * @return Agent列表
     */
    List<AgentRegistry> findByStatus(AgentStatus status);

    /**
     * 根据状态和健康状态查找Agent列表
     * 
     * @param status Agent状态
     * @param healthStatus 健康状态
     * @return Agent列表
     */
    List<AgentRegistry> findByStatusAndHealthStatus(AgentStatus status, HealthStatus healthStatus);

    /**
     * 根据Agent类型查找Agent列表
     * 
     * @param agentType Agent类型
     * @return Agent列表
     */
    List<AgentRegistry> findByAgentType(AgentType agentType);

    /**
     * 查找心跳超时的Agent
     * 
     * @param timeoutBefore 超时时间点
     * @return 超时Agent列表
     */
    @Query("SELECT a FROM AgentRegistry a WHERE a.lastHeartbeat < :timeoutBefore AND a.status = :status")
    List<AgentRegistry> findTimeoutAgents(@Param("timeoutBefore") LocalDateTime timeoutBefore, 
                                         @Param("status") AgentStatus status);

    /**
     * 统计各状态Agent数量
     * 
     * @return 状态统计
     */
    @Query("SELECT a.status, COUNT(a) FROM AgentRegistry a GROUP BY a.status")
    List<Object[]> countByStatus();

    /**
     * 统计各类型Agent数量
     * 
     * @return 类型统计
     */
    @Query("SELECT a.agentType, COUNT(a) FROM AgentRegistry a GROUP BY a.agentType")
    List<Object[]> countByType();

    /**
     * 查找活跃且健康的Agent
     * 
     * @return Agent列表
     */
    @Query("SELECT a FROM AgentRegistry a WHERE a.status = 'ACTIVE' AND a.healthStatus = 'HEALTHY'")
    List<AgentRegistry> findActiveAndHealthyAgents();
}