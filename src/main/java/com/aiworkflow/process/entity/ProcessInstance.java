package com.aiworkflow.process.entity;

import com.aiworkflow.common.entity.BaseEntity;
import com.aiworkflow.common.enums.ProcessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程实例实体
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "process_instance", indexes = {
    @Index(name = "idx_process_id", columnList = "processId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_start_time", columnList = "startTime"),
    @Index(name = "idx_business_key", columnList = "businessKey")
})
public class ProcessInstance extends BaseEntity {

    /**
     * 实例ID
     */
    @Id
    @Column(name = "instance_id", length = 64)
    private String instanceId;

    /**
     * 流程定义ID
     */
    @Column(name = "process_id", nullable = false, length = 64)
    private String processId;

    /**
     * 业务键值
     */
    @Column(name = "business_key", length = 255)
    private String businessKey;

    /**
     * 实例状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcessStatus status = ProcessStatus.ACTIVE;

    /**
     * 开始时间
     */
    @Column(name = "start_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 流程变量（JSON格式）
     */
    @Lob
    @Column(name = "variables", columnDefinition = "JSON")
    private String variables;

    /**
     * 当前活动节点
     */
    @Column(name = "current_activity", length = 255)
    private String currentActivity;

    /**
     * 父实例ID（子流程场景）
     */
    @Column(name = "parent_instance_id", length = 64)
    private String parentInstanceId;

    /**
     * 流程实例优先级
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 0;

    /**
     * 挂起原因
     */
    @Column(name = "suspend_reason", length = 500)
    private String suspendReason;

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
}