package com.aiworkflow.process.entity;

import com.aiworkflow.common.entity.BaseEntity;
import com.aiworkflow.common.enums.TaskStatus;
import com.aiworkflow.common.enums.TaskType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 任务实例实体
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "task_instance", indexes = {
    @Index(name = "idx_instance_id", columnList = "instanceId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_priority", columnList = "priority"),
    @Index(name = "idx_agent_id", columnList = "agentId")
})
public class TaskInstance extends BaseEntity {

    /**
     * 任务ID
     */
    @Id
    @Column(name = "task_id", length = 64)
    private String taskId;

    /**
     * 流程实例ID
     */
    @Column(name = "instance_id", nullable = false, length = 64)
    private String instanceId;

    /**
     * 任务名称
     */
    @Column(name = "task_name", nullable = false, length = 255)
    private String taskName;

    /**
     * 任务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "task_type", nullable = false)
    private TaskType taskType;

    /**
     * 执行Agent ID
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
     * 任务优先级
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 输入数据（JSON格式）
     */
    @Lob
    @Column(name = "input_data", columnDefinition = "JSON")
    private String inputData;

    /**
     * 输出数据（JSON格式）
     */
    @Lob
    @Column(name = "output_data", columnDefinition = "JSON")
    private String outputData;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 任务配置（JSON格式）
     */
    @Lob
    @Column(name = "task_config", columnDefinition = "JSON")
    private String taskConfig;

    /**
     * 重试次数
     */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    /**
     * 最大重试次数
     */
    @Column(name = "max_retry", nullable = false)
    private Integer maxRetry = 3;

    /**
     * 超时时间（秒）
     */
    @Column(name = "timeout_seconds")
    private Long timeoutSeconds;

    /**
     * 分配人
     */
    @Column(name = "assignee", length = 64)
    private String assignee;

    /**
     * 流程实例关联
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    private ProcessInstance processInstance;
}