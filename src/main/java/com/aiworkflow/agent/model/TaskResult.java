package com.aiworkflow.agent.model;

import com.aiworkflow.common.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务结果模型
 * 
 * @author AI Workflow Team
 */
@Data
public class TaskResult {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 执行状态
     */
    private TaskStatus status;

    /**
     * 输出数据
     */
    private Map<String, Object> outputData;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 错误代码
     */
    private String errorCode;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 处理耗时（毫秒）
     */
    private Long processingTimeMs;

    /**
     * 执行的Agent ID
     */
    private String executedBy;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 执行日志
     */
    private String executionLog;

    /**
     * 扩展属性
     */
    private Map<String, Object> metadata;

    /**
     * 创建成功结果
     * 
     * @param taskId 任务ID
     * @param outputData 输出数据
     * @return 任务结果
     */
    public static TaskResult success(String taskId, Map<String, Object> outputData) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setStatus(TaskStatus.COMPLETED);
        result.setOutputData(outputData);
        result.setEndTime(LocalDateTime.now());
        return result;
    }

    /**
     * 创建失败结果
     * 
     * @param taskId 任务ID
     * @param errorMessage 错误信息
     * @return 任务结果
     */
    public static TaskResult failure(String taskId, String errorMessage) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setStatus(TaskStatus.FAILED);
        result.setErrorMessage(errorMessage);
        result.setEndTime(LocalDateTime.now());
        return result;
    }

    /**
     * 创建失败结果
     * 
     * @param taskId 任务ID
     * @param errorCode 错误代码
     * @param errorMessage 错误信息
     * @return 任务结果
     */
    public static TaskResult failure(String taskId, String errorCode, String errorMessage) {
        TaskResult result = failure(taskId, errorMessage);
        result.setErrorCode(errorCode);
        return result;
    }

    /**
     * 计算处理耗时
     */
    public void calculateProcessingTime() {
        if (startTime != null && endTime != null) {
            long startMillis = startTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endMillis = endTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            this.processingTimeMs = endMillis - startMillis;
        }
    }
}