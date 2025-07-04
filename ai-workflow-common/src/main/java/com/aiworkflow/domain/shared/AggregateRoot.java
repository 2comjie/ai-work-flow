package com.aiworkflow.domain.shared;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根基类
 * 实现DDD的聚合根模式，管理领域事件
 */
public abstract class AggregateRoot<ID> {
    
    @Id
    protected ID id;
    
    @Version
    protected Integer version;
    
    protected Instant createdAt;
    protected Instant updatedAt;
    
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected AggregateRoot() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.version = 0;
    }
    
    protected AggregateRoot(ID id) {
        this();
        this.id = id;
    }
    
    /**
     * 添加领域事件
     */
    protected void addDomainEvent(DomainEvent event) {
        this.domainEvents.add(event);
        this.updatedAt = Instant.now();
    }
    
    /**
     * 获取所有领域事件（只读）
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    /**
     * 清除所有领域事件
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
    
    /**
     * 标记为已修改
     */
    protected void markAsModified() {
        this.updatedAt = Instant.now();
    }
    
    // Getters
    public ID getId() {
        return id;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AggregateRoot<?> that = (AggregateRoot<?>) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return String.format("%s{id=%s, version=%d}", 
            getClass().getSimpleName(), id, version);
    }
}