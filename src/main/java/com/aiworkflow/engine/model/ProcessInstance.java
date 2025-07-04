package com.aiworkflow.engine.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 流程实例模型
 */
@Entity
@Table(name = "process_instance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessInstance {

    @Id
    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "process_id", nullable = false)
    private String processId;

    @Column(name = "business_key")
    private String businessKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstanceStatus status;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "variables", columnDefinition = "JSON")
    private String variablesJson;

    @Column(name = "current_node_id")
    private String currentNodeId;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", insertable = false, updatable = false)
    private ProcessDefinition processDefinition;

    @OneToMany(mappedBy = "processInstance", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TaskInstance> tasks;

    // 运行时变量（不持久化）
    @Transient
    private Map<String, Object> variables;

    @PrePersist
    public void prePersist() {
        this.startTime = LocalDateTime.now();
        if (this.status == null) {
            this.status = InstanceStatus.RUNNING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        if (this.status == InstanceStatus.COMPLETED || this.status == InstanceStatus.FAILED) {
            this.endTime = LocalDateTime.now();
        }
    }

    /**
     * 流程实例状态枚举
     */
    public enum InstanceStatus {
        RUNNING("运行中"),
        COMPLETED("已完成"),
        FAILED("失败"),
        SUSPENDED("暂停"),
        TERMINATED("终止");

        private final String description;

        InstanceStatus(String description) {
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
        return status == InstanceStatus.COMPLETED || 
               status == InstanceStatus.FAILED || 
               status == InstanceStatus.TERMINATED;
    }
}