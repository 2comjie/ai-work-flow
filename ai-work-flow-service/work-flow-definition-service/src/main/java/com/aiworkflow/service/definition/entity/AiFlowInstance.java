package com.aiworkflow.service.definition.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "ai_flow_instance")
@Data
public class AiFlowInstance {

    @TableId(type = IdType.AUTO)
    private Long id; // 主键ID

    private Long flowDefinitionId; // 流程定义ID

    private String businessKey; // 业务标识

    private String name; // 实例名称

    private String status; // 状态:RUNNING,COMPLETED,FAILED,TERMINATED

    private String currentActivityId; // 当前节点ID

    private LocalDateTime startTime; // 开始时间

    private LocalDateTime endTime; // 结束时间

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object inputData; // 输入数据

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object outputData; // 输出数据

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object contextData; // 上下文数据(变量、执行路径、元数据等)

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object rollbackInfo; // 回滚信息

    private String errorMessage; // 错误信息

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createAt; // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateAt; // 更新时间

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private String userIdVirtual; // 用户ID虚拟列

    @TableField(insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private String priorityVirtual; // 优先级虚拟列
}