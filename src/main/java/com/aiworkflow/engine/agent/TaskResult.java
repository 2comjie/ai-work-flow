package com.aiworkflow.engine.agent;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务执行结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResult {

    /**
     * 执行状态
     */
    private ExecutionStatus status;

    /**
     * 结果数据
     */
    private Object data;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行开始时间
     */
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行持续时间（毫秒）
     */
    private Long durationMillis;

    /**
     * 扩展属性
     */
    private Map<String, Object> properties;

    /**
     * 执行状态枚举
     */
    public enum ExecutionStatus {
        SUCCESS("成功"),
        FAILED("失败"),
        TIMEOUT("超时"),
        CANCELLED("取消");

        private final String description;

        ExecutionStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return status == ExecutionStatus.SUCCESS;
    }

    /**
     * 判断是否失败
     */
    public boolean isFailed() {
        return status == ExecutionStatus.FAILED;
    }

    /**
     * 创建成功结果
     */
    public static TaskResult success(Object data) {
        return TaskResult.builder()
            .status(ExecutionStatus.SUCCESS)
            .data(data)
            .endTime(LocalDateTime.now())
            .build();
    }

    /**
     * 创建失败结果
     */
    public static TaskResult failed(String errorMessage) {
        return TaskResult.builder()
            .status(ExecutionStatus.FAILED)
            .errorMessage(errorMessage)
            .endTime(LocalDateTime.now())
            .build();
    }

    /**
     * 创建超时结果
     */
    public static TaskResult timeout() {
        return TaskResult.builder()
            .status(ExecutionStatus.TIMEOUT)
            .errorMessage("任务执行超时")
            .endTime(LocalDateTime.now())
            .build();
    }

    /**
     * 创建取消结果
     */
    public static TaskResult cancelled() {
        return TaskResult.builder()
            .status(ExecutionStatus.CANCELLED)
            .errorMessage("任务被取消")
            .endTime(LocalDateTime.now())
            .build();
    }

    /**
     * 计算执行时间
     */
    public void calculateDuration() {
        if (startTime != null && endTime != null) {
            this.durationMillis = java.time.Duration.between(startTime, endTime).toMillis();
        }
    }

    /**
     * 获取属性值
     */
    public Object getProperty(String key) {
        return properties != null ? properties.get(key) : null;
    }

    /**
     * 设置属性
     */
    public void setProperty(String key, Object value) {
        if (properties == null) {
            properties = new java.util.HashMap<>();
        }
        properties.put(key, value);
    }
}