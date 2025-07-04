package com.aiworkflow.entity;

import com.aiworkflow.common.enums.ProcessStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程定义实体
 * 
 * @author AI Workflow Team
 */
@Entity
@Table(name = "process_definition", indexes = {
    @Index(name = "idx_process_key", columnList = "processKey"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@EqualsAndHashCode(callSuper = false)
public class ProcessDefinition {

    /**
     * 流程ID
     */
    @Id
    @Column(name = "process_id", length = 64)
    @GeneratedValue(generator = "snowflake")
    @GenericGenerator(name = "snowflake", strategy = "com.aiworkflow.common.generator.SnowflakeGenerator")
    private String processId;

    /**
     * 流程名称
     */
    @Column(name = "process_name", nullable = false)
    private String processName;

    /**
     * 流程键（唯一标识）
     */
    @Column(name = "process_key", nullable = false)
    private String processKey;

    /**
     * BPMN XML内容
     */
    @Column(name = "bpmn_xml", columnDefinition = "TEXT")
    private String bpmnXml;

    /**
     * 版本号
     */
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    /**
     * 流程状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcessStatus status = ProcessStatus.DRAFT;

    /**
     * 流程描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * 创建者
     */
    @Column(name = "created_by", length = 64)
    private String createdBy;

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

    /**
     * 流程实例列表
     */
    @OneToMany(mappedBy = "processDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProcessInstance> instances;

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