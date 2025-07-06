package com.aiworkflow.service.definition.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "ai_flow_execution")
@Data
public class AiFlowExecution {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private Long flowInstanceId; // 流程实例ID

    private Long parentExecutionId; // 父执行ID(支持简单分支)

    private String activityId; // 当前活动ID

    private Boolean isActive; // 是否活跃

    private Boolean isConcurrent; // 是否并发分支

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object executionData; // 执行数据

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt; // 更新时间
} 