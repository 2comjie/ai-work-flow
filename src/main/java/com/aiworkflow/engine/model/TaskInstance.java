package com.aiworkflow.engine.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务实例模型
 */
@Entity
@Table(name = "task_instance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstance {

    @Id
    @Column(name = "task_id")
    private String taskId;

    @Column(name = "instance_id", nullable = false)
    private String instanceId;

    @Column(name = "node_id", nullable = false)
    private String nodeId;

    @Column(name = "task_name", nullable = false)
    private String taskName;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_type")
    private TaskType taskType;

    @Column(name = "agent_id")
    private String agentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "input_data", columnDefinition = "JSON")
    private String inputDataJson;

    @Column(name = "output_data", columnDefinition = "JSON")
    private String outputDataJson;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retries")
    private Integer maxRetries;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", insertable = false, updatable = false)
    private ProcessInstance processInstance;

    // 运行时数据（不持久化）
    @Transient
    private Map<String, Object> inputData;

    @Transient
    private Map<String, Object> outputData;

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = TaskStatus.PENDING;
        }
        if (this.priority == null) {
            this.priority = 0;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        if (this.maxRetries == null) {
            this.maxRetries = 3;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.status == TaskStatus.RUNNING && this.startTime == null) {
            this.startTime = LocalDateTime.now();
        }
        if ((this.status == TaskStatus.COMPLETED || this.status == TaskStatus.FAILED) && this.endTime == null) {
            this.endTime = LocalDateTime.now();
        }
    }

    /**
     * 任务类型枚举
     */
    public enum TaskType {
        USER_TASK("用户任务"),
        SERVICE_TASK("服务任务"),
        SCRIPT_TASK("脚本任务"),
        AGENT_TASK("Agent任务"),
        START_EVENT("开始事件"),
        END_EVENT("结束事件"),
        GATEWAY("网关");

        private final String description;

        TaskType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        PENDING("待执行"),
        RUNNING("执行中"),
        COMPLETED("已完成"),
        FAILED("失败"),
        SKIPPED("跳过"),
        CANCELLED("取消");

        private final String description;

        TaskStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 获取执行持续时间（毫秒）
     */
    public Long getDurationInMillis() {
        if (startTime == null) return null;
        LocalDateTime endTimeToUse = endTime != null ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, endTimeToUse).toMillis();
    }

    /**
     * 判断是否已结束
     */
    public boolean isFinished() {
        return status == TaskStatus.COMPLETED || 
               status == TaskStatus.FAILED || 
               status == TaskStatus.SKIPPED ||
               status == TaskStatus.CANCELLED;
    }

    /**
     * 判断是否可以重试
     */
    public boolean canRetry() {
        return status == TaskStatus.FAILED && retryCount < maxRetries;
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
    }
}