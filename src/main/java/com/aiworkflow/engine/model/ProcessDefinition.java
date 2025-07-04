package com.aiworkflow.engine.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 流程定义模型
 */
@Entity
@Table(name = "process_definition")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDefinition {

    @Id
    @Column(name = "process_id")
    private String processId;

    @Column(name = "process_name", nullable = false)
    private String processName;

    @Column(name = "process_key", nullable = false)
    private String processKey;

    @Column(name = "bpmn_xml", columnDefinition = "TEXT")
    private String bpmnXml;

    @Column(name = "version")
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProcessStatus status;

    @Column(name = "description")
    private String description;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @OneToMany(mappedBy = "processDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProcessInstance> instances;

    // 解析后的流程节点信息（临时字段，不持久化）
    @Transient
    private List<ProcessNode> processNodes;

    @PrePersist
    public void prePersist() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        if (this.version == null) {
            this.version = 1;
        }
        if (this.status == null) {
            this.status = ProcessStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedTime = LocalDateTime.now();
    }

    /**
     * 流程状态枚举
     */
    public enum ProcessStatus {
        DRAFT("草稿"),
        ACTIVE("激活"),
        SUSPENDED("暂停"),
        DELETED("已删除");

        private final String description;

        ProcessStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}