package com.aiworkflow.entity;

import com.aiworkflow.common.enums.TaskStatus;
import com.aiworkflow.common.enums.TaskType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务实例实体
 * 
 * @author AI Workflow Team
 */
@Entity
@Table(name = "task_instance", indexes = {
    @Index(name = "idx_instance_id", columnList = "instanceId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_priority", columnList = "priority")
})
@Data
@EqualsAndHashCode(callSuper = false)
public class TaskInstance {

    /**
     * 任务ID
     */
    @Id
    @Column(name = "task_id", length = 64)
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.aiworkflow.common.generator.SnowflakeGenerator")
    private String taskId;

    /**
     * 流程实例ID
     */
    @Column(name = "instance_id", length = 64, nullable = false)
    private String instanceId;

    /**
     * 任务名称
     */
    @Column(name = "task_name", nullable = false)
    private String taskName;

    /**
     * 任务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    /**
     * Agent ID
     */
    @Column(name = "agent_id", length = 64)
    private String agentId;

    /**
     * 任务状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    /**
     * 优先级
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /**
     * 输入数据（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "input_data")
    private Map<String, Object> inputData;

    /**
     * 输出数据（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "output_data")
    private Map<String, Object> outputData;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 流程实例关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    private ProcessInstance processInstance;

    @PrePersist
    protected void onCreate() {
        if (this.startTime == null && this.status == TaskStatus.RUNNING) {
            this.startTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.endTime == null && 
            (this.status == TaskStatus.COMPLETED || this.status == TaskStatus.FAILED)) {
            this.endTime = LocalDateTime.now();
        }
    }
}