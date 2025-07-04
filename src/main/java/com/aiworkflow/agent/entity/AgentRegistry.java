package com.aiworkflow.agent.entity;

import com.aiworkflow.common.entity.BaseEntity;
import com.aiworkflow.common.enums.AgentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Agent注册表实体
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "agent_registry", indexes = {
    @Index(name = "idx_agent_type", columnList = "agentType"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_last_heartbeat", columnList = "lastHeartbeat")
})
public class AgentRegistry extends BaseEntity {

    /**
     * Agent ID
     */
    @Id
    @Column(name = "agent_id", length = 64)
    private String agentId;

    /**
     * Agent名称
     */
    @Column(name = "agent_name", nullable = false, length = 255)
    private String agentName;

    /**
     * Agent类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "agent_type", nullable = false)
    private AgentType agentType;

    /**
     * Agent能力描述（JSON格式）
     */
    @Lob
    @Column(name = "capabilities", columnDefinition = "JSON")
    private String capabilities;

    /**
     * Agent端点URL
     */
    @Column(name = "endpoint_url", length = 500)
    private String endpointUrl;

    /**
     * 认证配置（JSON格式）
     */
    @Lob
    @Column(name = "auth_config", columnDefinition = "JSON")
    private String authConfig;

    /**
     * Agent状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AgentStatus status = AgentStatus.ACTIVE;

    /**
     * 健康状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "health_status", nullable = false)
    private HealthStatus healthStatus = HealthStatus.UNKNOWN;

    /**
     * 最后心跳时间
     */
    @Column(name = "last_heartbeat")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastHeartbeat;

    /**
     * 当前负载
     */
    @Column(name = "current_load", nullable = false)
    private Integer currentLoad = 0;

    /**
     * 最大并发数
     */
    @Column(name = "max_concurrency", nullable = false)
    private Integer maxConcurrency = 10;

    /**
     * Agent版本
     */
    @Column(name = "agent_version", length = 50)
    private String agentVersion;

    /**
     * Agent标签（JSON格式）
     */
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    /**
     * Agent描述
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Agent状态枚举
     */
    public enum AgentStatus {
        ACTIVE("ACTIVE", "激活"),
        INACTIVE("INACTIVE", "未激活"),
        MAINTENANCE("MAINTENANCE", "维护中");

        private final String code;
        private final String description;

        AgentStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 健康状态枚举
     */
    public enum HealthStatus {
        HEALTHY("HEALTHY", "健康"),
        UNHEALTHY("UNHEALTHY", "不健康"),
        UNKNOWN("UNKNOWN", "未知");

        private final String code;
        private final String description;

        HealthStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}