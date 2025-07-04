-- AI工作流流程服务数据库迁移脚本
-- 版本: V001
-- 描述: 创建流程定义和实例相关表

-- 流程定义表
CREATE TABLE workflow_process (
    id VARCHAR(64) PRIMARY KEY COMMENT '流程ID',
    name VARCHAR(255) NOT NULL COMMENT '流程名称',
    description TEXT COMMENT '流程描述',
    process_key VARCHAR(255) NOT NULL COMMENT '流程键值',
    definition JSON COMMENT '流程定义(JSON格式)',
    version INTEGER NOT NULL DEFAULT 1 COMMENT '版本号',
    status ENUM('DRAFT', 'ACTIVE', 'SUSPENDED', 'DELETED') NOT NULL DEFAULT 'DRAFT' COMMENT '流程状态',
    created_by VARCHAR(64) NOT NULL COMMENT '创建人',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_process_key (process_key),
    INDEX idx_status (status),
    INDEX idx_created_by (created_by),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_process_key_version (process_key, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工作流程定义表';

-- 流程实例表
CREATE TABLE process_instance (
    id VARCHAR(64) PRIMARY KEY COMMENT '实例ID',
    process_id VARCHAR(64) NOT NULL COMMENT '流程定义ID',
    business_key VARCHAR(255) COMMENT '业务键值',
    status ENUM('RUNNING', 'COMPLETED', 'FAILED', 'SUSPENDED', 'TERMINATED') NOT NULL DEFAULT 'RUNNING' COMMENT '实例状态',
    variables JSON COMMENT '流程变量',
    initiated_by VARCHAR(64) NOT NULL COMMENT '发起人',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    duration_millis BIGINT COMMENT '执行时长(毫秒)',
    
    FOREIGN KEY (process_id) REFERENCES workflow_process(id) ON DELETE CASCADE,
    INDEX idx_process_id (process_id),
    INDEX idx_status (status),
    INDEX idx_business_key (business_key),
    INDEX idx_initiated_by (initiated_by),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程实例表';

-- 任务实例表
CREATE TABLE task_instance (
    id VARCHAR(64) PRIMARY KEY COMMENT '任务ID',
    instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    name VARCHAR(255) NOT NULL COMMENT '任务名称',
    type ENUM('USER_TASK', 'SERVICE_TASK', 'SCRIPT_TASK', 'AGENT_TASK', 'GATEWAY') NOT NULL COMMENT '任务类型',
    status ENUM('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'SKIPPED', 'CANCELLED') NOT NULL DEFAULT 'PENDING' COMMENT '任务状态',
    priority INTEGER NOT NULL DEFAULT 0 COMMENT '优先级',
    agent_id VARCHAR(64) COMMENT '分配的Agent ID',
    assignee VARCHAR(64) COMMENT '任务执行人',
    input_data JSON COMMENT '输入数据',
    output_data JSON COMMENT '输出数据',
    error_message TEXT COMMENT '错误信息',
    start_time TIMESTAMP NULL COMMENT '开始时间',
    end_time TIMESTAMP NULL COMMENT '结束时间',
    duration_millis BIGINT COMMENT '执行时长(毫秒)',
    retry_count INTEGER DEFAULT 0 COMMENT '重试次数',
    
    FOREIGN KEY (instance_id) REFERENCES process_instance(id) ON DELETE CASCADE,
    INDEX idx_instance_id (instance_id),
    INDEX idx_status (status),
    INDEX idx_type (type),
    INDEX idx_priority (priority DESC),
    INDEX idx_agent_id (agent_id),
    INDEX idx_assignee (assignee),
    INDEX idx_start_time (start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务实例表';

-- 流程变量历史表
CREATE TABLE process_variable_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '历史ID',
    instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    variable_name VARCHAR(255) NOT NULL COMMENT '变量名',
    variable_value JSON COMMENT '变量值',
    variable_type VARCHAR(50) NOT NULL COMMENT '变量类型',
    operation ENUM('CREATE', 'UPDATE', 'DELETE') NOT NULL COMMENT '操作类型',
    operated_by VARCHAR(64) COMMENT '操作人',
    operated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    
    FOREIGN KEY (instance_id) REFERENCES process_instance(id) ON DELETE CASCADE,
    INDEX idx_instance_id (instance_id),
    INDEX idx_variable_name (variable_name),
    INDEX idx_operated_at (operated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程变量历史表';

-- 流程执行日志表
CREATE TABLE process_execution_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    instance_id VARCHAR(64) NOT NULL COMMENT '流程实例ID',
    task_id VARCHAR(64) COMMENT '任务ID',
    log_level ENUM('DEBUG', 'INFO', 'WARN', 'ERROR') NOT NULL COMMENT '日志级别',
    message TEXT NOT NULL COMMENT '日志消息',
    details JSON COMMENT '详细信息',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    FOREIGN KEY (instance_id) REFERENCES process_instance(id) ON DELETE CASCADE,
    INDEX idx_instance_id (instance_id),
    INDEX idx_task_id (task_id),
    INDEX idx_log_level (log_level),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程执行日志表';

-- 流程事件表
CREATE TABLE process_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '事件ID',
    event_id VARCHAR(64) NOT NULL UNIQUE COMMENT '事件唯一标识',
    event_type VARCHAR(100) NOT NULL COMMENT '事件类型',
    aggregate_id VARCHAR(64) NOT NULL COMMENT '聚合根ID',
    aggregate_type VARCHAR(100) NOT NULL COMMENT '聚合根类型',
    event_data JSON NOT NULL COMMENT '事件数据',
    version INTEGER NOT NULL COMMENT '版本号',
    occurred_at TIMESTAMP NOT NULL COMMENT '发生时间',
    processed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已处理',
    processed_at TIMESTAMP NULL COMMENT '处理时间',
    
    INDEX idx_aggregate_id (aggregate_id),
    INDEX idx_event_type (event_type),
    INDEX idx_occurred_at (occurred_at),
    INDEX idx_processed (processed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程事件表';

-- 创建初始数据
INSERT INTO workflow_process (id, name, process_key, description, status, created_by) VALUES 
('process_sample_001', '示例流程', 'sample_process', '这是一个示例流程，用于演示系统功能', 'DRAFT', 'system');