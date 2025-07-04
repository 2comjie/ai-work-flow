package com.aiworkflow.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 包含所有实体的公共字段
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "created_time", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "updated_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 64)
    private String createdBy;

    /**
     * 更新人
     */
    @Column(name = "updated_by", length = 64)
    private String updatedBy;

    /**
     * 逻辑删除标识
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @Column(name = "version")
    private Long version;
}