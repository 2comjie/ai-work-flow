package com.aiworkflow.entity;

import com.aiworkflow.common.enums.AgentStatus;
import com.aiworkflow.common.enums.AgentType;
import com.aiworkflow.common.enums.HealthStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Agent注册实体
 * 
 * @author AI Workflow Team
 */
@Entity
@Table(name = "agent_registry", indexes = {
    @Index(name = "idx_agent_type", columnList = "agentType"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_last_heartbeat", columnList = "lastHeartbeat")
})
@Data
@EqualsAndHashCode(callSuper = false)
public class AgentRegistry {

    /**
     * Agent ID
     */
    @Id
    @Column(name = "agent_id", length = 64)
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.aiworkflow.common.generator.SnowflakeGenerator")
    private String agentId;

    /**
     * Agent名称
     */
    @Column(name = "agent_name", nullable = false)
    private String agentName;

    /**
     * Agent类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "agent_type", nullable = false)
    private AgentType agentType;

    /**
     * Agent能力配置（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "capabilities")
    private Map<String, Object> capabilities;

    /**
     * 端点URL
     */
    @Column(name = "endpoint_url", length = 500)
    private String endpointUrl;

    /**
     * 认证配置（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "auth_config")
    private Map<String, Object> authConfig;

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
    private LocalDateTime lastHeartbeat;

    /**
     * 创建时间
     */
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
}