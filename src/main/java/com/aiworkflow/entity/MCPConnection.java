package com.aiworkflow.entity;

import com.aiworkflow.common.enums.ConnectionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * MCP连接实体
 * 
 * @author AI Workflow Team
 */
@Entity
@Table(name = "mcp_connection", indexes = {
    @Index(name = "idx_provider", columnList = "modelProvider"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = false)
public class MCPConnection {

    /**
     * 连接ID
     */
    @Id
    @Column(name = "connection_id", length = 64)
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.aiworkflow.common.generator.SnowflakeGenerator")
    private String connectionId;

    /**
     * 模型提供商
     */
    @Column(name = "model_provider", length = 100, nullable = false)
    private String modelProvider;

    /**
     * 模型名称
     */
    @Column(name = "model_name", nullable = false)
    private String modelName;

    /**
     * 端点URL
     */
    @Column(name = "endpoint_url", length = 500, nullable = false)
    private String endpointUrl;

    /**
     * 加密的API密钥
     */
    @Column(name = "api_key_encrypted", length = 512)
    private String apiKeyEncrypted;

    /**
     * 连接配置（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "connection_config")
    private Map<String, Object> connectionConfig;

    /**
     * 连接状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConnectionStatus status = ConnectionStatus.DISCONNECTED;

    /**
     * 最后连接时间
     */
    @Column(name = "last_connected")
    private LocalDateTime lastConnected;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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