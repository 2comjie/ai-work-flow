package com.aiworkflow.agent.core;

import com.aiworkflow.agent.entity.AgentRegistry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 心跳响应
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeartbeatResponse {

    /**
     * Agent ID
     */
    private String agentId;

    /**
     * 健康状态
     */
    private AgentRegistry.HealthStatus healthStatus;

    /**
     * 当前负载
     */
    private Integer currentLoad;

    /**
     * 最大并发数
     */
    private Integer maxConcurrency;

    /**
     * 心跳时间
     */
    private LocalDateTime heartbeatTime;

    /**
     * Agent版本
     */
    private String version;

    /**
     * 系统信息
     */
    private Map<String, Object> systemInfo;

    /**
     * 性能指标
     */
    private Map<String, Object> metrics;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 是否健康
     *
     * @return 是否健康
     */
    public boolean isHealthy() {
        return AgentRegistry.HealthStatus.HEALTHY.equals(healthStatus);
    }

    /**
     * 创建健康心跳响应
     *
     * @param agentId        Agent ID
     * @param currentLoad    当前负载
     * @param maxConcurrency 最大并发数
     * @return 心跳响应
     */
    public static HeartbeatResponse healthy(String agentId, Integer currentLoad, Integer maxConcurrency) {
        return HeartbeatResponse.builder()
                .agentId(agentId)
                .healthStatus(AgentRegistry.HealthStatus.HEALTHY)
                .currentLoad(currentLoad)
                .maxConcurrency(maxConcurrency)
                .heartbeatTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建不健康心跳响应
     *
     * @param agentId      Agent ID
     * @param errorMessage 错误信息
     * @return 心跳响应
     */
    public static HeartbeatResponse unhealthy(String agentId, String errorMessage) {
        return HeartbeatResponse.builder()
                .agentId(agentId)
                .healthStatus(AgentRegistry.HealthStatus.UNHEALTHY)
                .errorMessage(errorMessage)
                .heartbeatTime(LocalDateTime.now())
                .build();
    }
}