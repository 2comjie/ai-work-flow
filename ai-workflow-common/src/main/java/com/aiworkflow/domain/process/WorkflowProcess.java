package com.aiworkflow.domain.process;

import com.aiworkflow.domain.shared.AggregateRoot;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Map;

/**
 * 工作流程聚合根
 * 管理流程的完整生命周期
 */
@Table("workflow_process")
public class WorkflowProcess extends AggregateRoot<ProcessId> {
    
    private String name;
    private String description;
    private String processKey;
    private ProcessDefinition definition;
    private ProcessVersion version;
    private ProcessStatus status;
    private String createdBy;
    
    // 构造函数
    protected WorkflowProcess() {
        super();
    }
    
    private WorkflowProcess(ProcessId id, String name, String processKey, ProcessDefinition definition, String createdBy) {
        super(id);
        this.name = name;
        this.processKey = processKey;
        this.definition = definition;
        this.createdBy = createdBy;
        this.version = ProcessVersion.initial();
        this.status = ProcessStatus.DRAFT;
        
        // 发布流程创建事件
        addDomainEvent(new ProcessCreatedEvent(id.getValue(), this, this.version.getValue()));
    }
    
    /**
     * 创建新的流程
     */
    public static WorkflowProcess create(String name, String processKey, ProcessDefinition definition, String createdBy) {
        ProcessId processId = ProcessId.generate();
        return new WorkflowProcess(processId, name, processKey, definition, createdBy);
    }
    
    /**
     * 激活流程
     */
    public void activate() {
        if (!status.canActivate()) {
            throw new IllegalStateException("流程当前状态不允许激活: " + status);
        }
        
        this.status = ProcessStatus.ACTIVE;
        markAsModified();
        
        addDomainEvent(new ProcessActivatedEvent(id.getValue(), this, this.version.getValue()));
    }
    
    /**
     * 暂停流程
     */
    public void suspend() {
        if (!status.canSuspend()) {
            throw new IllegalStateException("流程当前状态不允许暂停: " + status);
        }
        
        this.status = ProcessStatus.SUSPENDED;
        markAsModified();
        
        addDomainEvent(new ProcessSuspendedEvent(id.getValue(), this, this.version.getValue()));
    }
    
    /**
     * 更新流程定义
     */
    public void updateDefinition(ProcessDefinition newDefinition, String updatedBy) {
        if (!status.canEdit()) {
            throw new IllegalStateException("流程当前状态不允许编辑: " + status);
        }
        
        this.definition = newDefinition;
        this.version = this.version.increment();
        markAsModified();
        
        addDomainEvent(new ProcessDefinitionUpdatedEvent(id.getValue(), this, this.version.getValue()));
    }
    
    /**
     * 创建流程实例
     */
    public ProcessInstance createInstance(Map<String, Object> variables, String initiatedBy) {
        if (!status.canCreateInstance()) {
            throw new IllegalStateException("流程当前状态不允许创建实例: " + status);
        }
        
        return ProcessInstance.create(this.id, variables, initiatedBy);
    }
    
    /**
     * 逻辑删除流程
     */
    public void delete() {
        if (!status.canDelete()) {
            throw new IllegalStateException("流程当前状态不允许删除: " + status);
        }
        
        this.status = ProcessStatus.DELETED;
        markAsModified();
        
        addDomainEvent(new ProcessDeletedEvent(id.getValue(), this, this.version.getValue()));
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getProcessKey() {
        return processKey;
    }
    
    public ProcessDefinition getDefinition() {
        return definition;
    }
    
    public ProcessVersion getVersion() {
        return version;
    }
    
    public ProcessStatus getStatus() {
        return status;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
}