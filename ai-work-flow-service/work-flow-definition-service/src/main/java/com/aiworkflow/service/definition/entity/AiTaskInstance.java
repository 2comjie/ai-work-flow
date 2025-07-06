package com.aiworkflow.service.definition.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "ai_task_instance")
@Data
public class AiTaskInstance {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private Long flowInstanceId; // 流程实例ID

    private Long executionId; // 执行实例ID

    private String taskKey; // 任务标识

    private String taskName; // 任务名称

    private String taskType; // 任务类型:START_EVENT,END_EVENT,USER_TASK,SERVICE_TASK,LLM_AGENT,MCP_TOOL,AI_DECISION,EXCLUSIVE_GATEWAY,PARALLEL_GATEWAY

    private String status; // 任务状态:CREATED,READY,RUNNING,COMPLETED,FAILED,CANCELLED

    private LocalDateTime startTime; // 开始时间

    private LocalDateTime endTime; // 结束时间

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object inputData; // 输入数据

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object outputData; // 输出数据

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object configData; // 配置数据(AI配置、工具配置等)

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object rollbackData; // 回滚数据(策略、状态、记录)

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object errorInfo; // 错误信息

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt; // 更新时间
} 