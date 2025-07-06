package com.aiworkflow.service.definition.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "ai_flow_definition")
@Data
public class AiFlowDefinition {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private String flowKey; // 流程标识

    private String name; // 流程名称

    private Integer version; // 版本号

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object flowModel; // BPMN流程模型JSON

    private String status; // 状态:DRAFT,ACTIVE,INACTIVE

    private String description; // 描述

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt; // 更新时间
}