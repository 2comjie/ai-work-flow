package com.aiworkflow.engine.model;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 流程节点模型（运行时对象，不持久化）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessNode {

    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     */
    private NodeType nodeType;

    /**
     * 输入连接
     */
    private List<String> incomingFlows;

    /**
     * 输出连接
     */
    private List<String> outgoingFlows;

    /**
     * 节点属性
     */
    private Map<String, Object> properties;

    /**
     * 条件表达式（用于网关）
     */
    private String conditionExpression;

    /**
     * 指定的Agent类型
     */
    private String agentType;

    /**
     * 任务优先级
     */
    private Integer priority;

    /**
     * 最大重试次数
     */
    private Integer maxRetries;

    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;

    /**
     * 节点类型枚举
     */
    public enum NodeType {
        START_EVENT("开始事件"),
        END_EVENT("结束事件"),
        USER_TASK("用户任务"),
        SERVICE_TASK("服务任务"),
        SCRIPT_TASK("脚本任务"),
        AGENT_TASK("Agent任务"),
        EXCLUSIVE_GATEWAY("排他网关"),
        PARALLEL_GATEWAY("并行网关"),
        INCLUSIVE_GATEWAY("包容网关");

        private final String description;

        NodeType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 是否为事件节点
     */
    public boolean isEvent() {
        return nodeType == NodeType.START_EVENT || nodeType == NodeType.END_EVENT;
    }

    /**
     * 是否为任务节点
     */
    public boolean isTask() {
        return nodeType == NodeType.USER_TASK || 
               nodeType == NodeType.SERVICE_TASK || 
               nodeType == NodeType.SCRIPT_TASK ||
               nodeType == NodeType.AGENT_TASK;
    }

    /**
     * 是否为网关节点
     */
    public boolean isGateway() {
        return nodeType == NodeType.EXCLUSIVE_GATEWAY || 
               nodeType == NodeType.PARALLEL_GATEWAY ||
               nodeType == NodeType.INCLUSIVE_GATEWAY;
    }

    /**
     * 是否为开始节点
     */
    public boolean isStartNode() {
        return nodeType == NodeType.START_EVENT;
    }

    /**
     * 是否为结束节点
     */
    public boolean isEndNode() {
        return nodeType == NodeType.END_EVENT;
    }

    /**
     * 获取属性值
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }

    /**
     * 获取字符串属性值
     */
    public String getStringProperty(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取整数属性值
     */
    public Integer getIntProperty(String key) {
        Object value = getProperty(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取布尔属性值
     */
    public Boolean getBooleanProperty(String key) {
        Object value = getProperty(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
}