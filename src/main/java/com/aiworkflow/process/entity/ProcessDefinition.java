package com.aiworkflow.process.entity;

import com.aiworkflow.common.entity.BaseEntity;
import com.aiworkflow.common.enums.ProcessStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 流程定义实体
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "process_definition", indexes = {
    @Index(name = "idx_process_key", columnList = "processKey"),
    @Index(name = "idx_status", columnList = "status")
})
public class ProcessDefinition extends BaseEntity {

    /**
     * 流程ID
     */
    @Id
    @Column(name = "process_id", length = 64)
    private String processId;

    /**
     * 流程名称
     */
    @Column(name = "process_name", nullable = false, length = 255)
    private String processName;

    /**
     * 流程键值（用于版本管理）
     */
    @Column(name = "process_key", nullable = false, length = 255)
    private String processKey;

    /**
     * BPMN XML内容
     */
    @Lob
    @Column(name = "bpmn_xml", columnDefinition = "TEXT")
    private String bpmnXml;

    /**
     * 流程版本
     */
    @Column(name = "version", nullable = false)
    private Integer processVersion = 1;

    /**
     * 流程状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProcessStatus status = ProcessStatus.DRAFT;

    /**
     * 流程描述
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * 流程标签（JSON格式）
     */
    @Column(name = "tags", columnDefinition = "JSON")
    private String tags;

    /**
     * 流程分类
     */
    @Column(name = "category", length = 100)
    private String category;

    /**
     * 是否为最新版本
     */
    @Column(name = "is_latest", nullable = false)
    private Boolean isLatest = true;

    /**
     * 流程实例列表
     */
    @OneToMany(mappedBy = "processDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProcessInstance> instances;
}