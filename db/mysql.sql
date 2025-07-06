-- auto-generated definition
create table ai_flow_definition
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    flow_key    varchar(100)                          not null comment '流程标识',
    name        varchar(255)                          not null comment '流程名称',
    version     int         default 1                 not null comment '版本号',
    flow_model  json                                  not null comment 'BPMN流程模型JSON',
    status      varchar(20) default 'DRAFT'           null comment '状态:DRAFT,ACTIVE,INACTIVE',
    description text                                  null comment '描述',
    create_at   datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_at   datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_flow_key_version
        unique (flow_key, version)
)
    comment '流程定义表' charset = utf8mb4;

create index idx_flow_definition_key
    on ai_flow_definition (flow_key);

create index idx_flow_definition_status
    on ai_flow_definition (status);




-- auto-generated definition
create table ai_flow_execution
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    flow_instance_id    bigint                               not null comment '流程实例ID',
    parent_execution_id bigint                               null comment '父执行ID(支持简单分支)',
    activity_id         varchar(100)                         null comment '当前活动ID',
    is_active           tinyint(1) default 1                 null comment '是否活跃',
    is_concurrent       tinyint(1) default 0                 null comment '是否并发分支',
    execution_data      json                                 null comment '执行数据',
    create_at           datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_at           datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '执行实例表' charset = utf8mb4;

create index idx_execution_activity_id
    on ai_flow_execution (activity_id);

create index idx_execution_flow_instance_id
    on ai_flow_execution (flow_instance_id);

create index idx_execution_is_active
    on ai_flow_execution (is_active);

create index idx_execution_parent_execution_id
    on ai_flow_execution (parent_execution_id);

-- auto-generated definition
create table ai_flow_instance
(
    id                  bigint auto_increment comment '主键ID'
        primary key,
    flow_definition_id  bigint                             not null comment '流程定义ID',
    business_key        varchar(255)                       null comment '业务标识',
    name                varchar(255)                       null comment '实例名称',
    status              varchar(20)                        not null comment '状态:RUNNING,COMPLETED,FAILED,TERMINATED',
    current_activity_id varchar(100)                       null comment '当前节点ID',
    start_time          datetime default CURRENT_TIMESTAMP null comment '开始时间',
    end_time            datetime                           null comment '结束时间',
    input_data          json                               null comment '输入数据',
    output_data         json                               null comment '输出数据',
    context_data        json                               null comment '上下文数据(变量、执行路径、元数据等)',
    rollback_info       json                               null comment '回滚信息',
    error_message       text                               null comment '错误信息',
    create_at           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_at           datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    user_id_virtual     varchar(100) as (json_unquote(json_extract(`context_data`, _utf8mb4'$.variables.user_id'))),
    priority_virtual    varchar(50) as (json_unquote(json_extract(`context_data`, _utf8mb4'$.variables.priority')))
)
    comment '流程实例表' charset = utf8mb4;

create index idx_flow_instance_business_key
    on ai_flow_instance (business_key);

create index idx_flow_instance_current_activity
    on ai_flow_instance (current_activity_id);

create index idx_flow_instance_definition_id
    on ai_flow_instance (flow_definition_id);

create index idx_flow_instance_status
    on ai_flow_instance (status);

create index idx_priority_virtual
    on ai_flow_instance (priority_virtual);

create index idx_user_id_virtual
    on ai_flow_instance (user_id_virtual);

-- auto-generated definition
create table ai_task_instance
(
    id               bigint auto_increment comment '主键ID'
        primary key,
    flow_instance_id bigint                                not null comment '流程实例ID',
    execution_id     bigint                                null comment '执行实例ID',
    task_key         varchar(100)                          not null comment '任务标识',
    task_name        varchar(255)                          not null comment '任务名称',
    task_type        varchar(50)                           not null comment '任务类型:START_EVENT,END_EVENT,USER_TASK,SERVICE_TASK,LLM_AGENT,MCP_TOOL,AI_DECISION,EXCLUSIVE_GATEWAY,PARALLEL_GATEWAY',
    status           varchar(20) default 'CREATED'         not null comment '任务状态:CREATED,READY,RUNNING,COMPLETED,FAILED,CANCELLED',
    start_time       datetime                              null comment '开始时间',
    end_time         datetime                              null comment '结束时间',
    input_data       json                                  null comment '输入数据',
    output_data      json                                  null comment '输出数据',
    config_data      json                                  null comment '配置数据(AI配置、工具配置等)',
    rollback_data    json                                  null comment '回滚数据(策略、状态、记录)',
    error_info       json                                  null comment '错误信息',
    create_at        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_at        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment 'AI任务实例表' charset = utf8mb4;

create index idx_task_execution_id
    on ai_task_instance (execution_id);

create index idx_task_flow_instance_id
    on ai_task_instance (flow_instance_id);

create index idx_task_status
    on ai_task_instance (status);

create index idx_task_task_key
    on ai_task_instance (task_key);

create index idx_task_task_type
    on ai_task_instance (task_type);

