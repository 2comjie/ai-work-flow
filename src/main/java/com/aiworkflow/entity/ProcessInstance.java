package com.aiworkflow.entity;

import com.aiworkflow.common.enums.ProcessStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 流程实例实体
 * 
 * @author AI Workflow Team
 */
@Entity
@Table(name = "process_instance", indexes = {
    @Index(name = "idx_process_id", columnList = "processId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_start_time", columnList = "startTime")
})
@Data
@EqualsAndHashCode(callSuper = false)
public class ProcessInstance {

    /**
     * 实例ID
     */
    @Id
    @Column(name = "instance_id", length = 64)
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.aiworkflow.common.generator.SnowflakeGenerator")
    private String instanceId;

    /**
     * 流程定义ID
     */
    @Column(name = "process_id", length = 64, nullable = false)
    private String processId;

    /**
     * 业务键
     */
    @Column(name = "business_key")
    private String businessKey;

    /**
     * 实例状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcessStatus status = ProcessStatus.DRAFT;

    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 流程变量（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables")
    private Map<String, Object> variables;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 流程定义关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", insertable = false, updatable = false)
    private ProcessDefinition processDefinition;

    /**
     * 任务实例列表
     */
    @OneToMany(mappedBy = "processInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskInstance> taskInstances;

    @PrePersist
    protected void onCreate() {
        this.startTime = LocalDateTime.now();
    }
}