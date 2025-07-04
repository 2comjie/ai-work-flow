package com.aiworkflow.agent.core;

import com.aiworkflow.common.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 任务执行结果
 *
 * @author AI Workflow Team
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResult {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * Agent ID
     */
    private String agentId;

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
     * 执行耗时（毫秒）
     */
    private Long executionTime;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 执行日志
     */
    private String executionLog;

    /**
     * 输出文件路径列表
     */
    private java.util.List<String> outputFiles;

    /**
     * 执行元数据
     */
    private Map<String, Object> metadata;

    /**
     * 下一步任务建议
     */
    private String nextTaskSuggestion;

    /**
     * 创建成功结果
     *
     * @param taskId     任务ID
     * @param agentId    Agent ID
     * @param outputData 输出数据
     * @return 任务结果
     */
    public static TaskResult success(String taskId, String agentId, Map<String, Object> outputData) {
        return TaskResult.builder()
                .taskId(taskId)
                .agentId(agentId)
                .status(TaskStatus.COMPLETED)
                .outputData(outputData)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建失败结果
     *
     * @param taskId       任务ID
     * @param agentId      Agent ID
     * @param errorMessage 错误信息
     * @param errorCode    错误代码
     * @return 任务结果
     */
    public static TaskResult failure(String taskId, String agentId, String errorMessage, String errorCode) {
        return TaskResult.builder()
                .taskId(taskId)
                .agentId(agentId)
                .status(TaskStatus.FAILED)
                .errorMessage(errorMessage)
                .errorCode(errorCode)
                .endTime(LocalDateTime.now())
                .build();
    }

    /**
     * 创建运行中结果
     *
     * @param taskId  任务ID
     * @param agentId Agent ID
     * @return 任务结果
     */
    public static TaskResult running(String taskId, String agentId) {
        return TaskResult.builder()
                .taskId(taskId)
                .agentId(agentId)
                .status(TaskStatus.RUNNING)
                .startTime(LocalDateTime.now())
                .build();
    }
}