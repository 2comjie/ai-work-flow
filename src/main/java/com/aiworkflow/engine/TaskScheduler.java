package com.aiworkflow.engine;

import com.aiworkflow.agent.model.Task;
import com.aiworkflow.agent.model.TaskResult;
import com.aiworkflow.common.enums.TaskStatus;
import com.aiworkflow.entity.TaskInstance;
import com.aiworkflow.repository.TaskInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 任务调度器
 * 
 * @author AI Workflow Team
 */
@Component
@Slf4j
public class TaskScheduler {

    private static final String TASK_QUEUE_HIGH = "ai_workflow:task_queue:high";
    private static final String TASK_QUEUE_NORMAL = "ai_workflow:task_queue:normal";
    private static final String TASK_QUEUE_LOW = "ai_workflow:task_queue:low";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AgentManager agentManager;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    /**
     * 提交任务到队列
     * 
     * @param task 任务
     */
    public void submitTask(Task task) {
        try {
            log.info("提交任务到队列: taskId={}, priority={}", task.getTaskId(), task.getPriority());
            
            // 根据优先级选择不同的队列
            String queueName = getQueueByPriority(task.getPriority());
            
            // 将任务添加到Redis队列
            redisTemplate.opsForList().leftPush(queueName, task);
            
            // 更新任务状态为等待执行
            updateTaskStatus(task.getTaskId(), TaskStatus.PENDING);
            
            // 异步执行任务
            executeTaskAsync(task);
            
        } catch (Exception e) {
            log.error("提交任务失败: taskId={}", task.getTaskId(), e);
            updateTaskStatus(task.getTaskId(), TaskStatus.FAILED);
        }
    }

    /**
     * 异步执行任务
     * 
     * @param task 任务
     * @return 任务结果的Future
     */
    @Async("taskExecutor")
    public CompletableFuture<TaskResult> executeTaskAsync(Task task) {
        return CompletableFuture.supplyAsync(() -> executeTask(task));
    }

    /**
     * 执行任务
     * 
     * @param task 任务
     * @return 任务结果
     */
    public TaskResult executeTask(Task task) {
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            log.info("开始执行任务: taskId={}, taskType={}", task.getTaskId(), task.getTaskType());
            
            // 更新任务状态为执行中
            updateTaskStatus(task.getTaskId(), TaskStatus.RUNNING);
            updateTaskStartTime(task.getTaskId(), startTime);
            
            // 检查任务超时
            if (task.isTimeout()) {
                log.warn("任务已超时: taskId={}", task.getTaskId());
                return TaskResult.failure(task.getTaskId(), "TIMEOUT", "任务执行超时");
            }
            
            // 选择合适的Agent
            var agent = agentManager.selectAgent(task);
            if (agent == null) {
                log.error("没有找到合适的Agent执行任务: taskId={}", task.getTaskId());
                return TaskResult.failure(task.getTaskId(), "NO_AGENT", "没有找到合适的Agent");
            }
            
            // 执行任务
            TaskResult result = agent.execute(task);
            result.setStartTime(startTime);
            result.setEndTime(LocalDateTime.now());
            result.calculateProcessingTime();
            result.setExecutedBy(agent.getAgentId());
            
            // 更新任务状态
            updateTaskStatus(task.getTaskId(), result.getStatus());
            updateTaskResult(task.getTaskId(), result);
            
            log.info("任务执行完成: taskId={}, status={}, 耗时={}ms", 
                task.getTaskId(), result.getStatus(), result.getProcessingTimeMs());
            
            return result;
            
        } catch (Exception e) {
            log.error("任务执行失败: taskId={}", task.getTaskId(), e);
            
            TaskResult errorResult = TaskResult.failure(task.getTaskId(), e.getMessage());
            errorResult.setStartTime(startTime);
            errorResult.setEndTime(LocalDateTime.now());
            errorResult.calculateProcessingTime();
            
            updateTaskStatus(task.getTaskId(), TaskStatus.FAILED);
            updateTaskResult(task.getTaskId(), errorResult);
            
            return errorResult;
        }
    }

    /**
     * 根据优先级获取队列名称
     * 
     * @param priority 优先级
     * @return 队列名称
     */
    private String getQueueByPriority(Integer priority) {
        if (priority == null) {
            return TASK_QUEUE_NORMAL;
        }
        
        if (priority >= 8) {
            return TASK_QUEUE_HIGH;
        } else if (priority >= 5) {
            return TASK_QUEUE_NORMAL;
        } else {
            return TASK_QUEUE_LOW;
        }
    }

    /**
     * 更新任务状态
     * 
     * @param taskId 任务ID
     * @param status 状态
     */
    private void updateTaskStatus(String taskId, TaskStatus status) {
        try {
            TaskInstance taskInstance = taskInstanceRepository.findById(taskId).orElse(null);
            if (taskInstance != null) {
                taskInstance.setStatus(status);
                if (status == TaskStatus.RUNNING) {
                    taskInstance.setStartTime(LocalDateTime.now());
                } else if (status == TaskStatus.COMPLETED || status == TaskStatus.FAILED) {
                    taskInstance.setEndTime(LocalDateTime.now());
                }
                taskInstanceRepository.save(taskInstance);
            }
            
            // 缓存任务状态
            String statusKey = "ai_workflow:task_status:" + taskId;
            redisTemplate.opsForValue().set(statusKey, status.name());
            
        } catch (Exception e) {
            log.error("更新任务状态失败: taskId={}, status={}", taskId, status, e);
        }
    }

    /**
     * 更新任务开始时间
     * 
     * @param taskId 任务ID
     * @param startTime 开始时间
     */
    private void updateTaskStartTime(String taskId, LocalDateTime startTime) {
        try {
            TaskInstance taskInstance = taskInstanceRepository.findById(taskId).orElse(null);
            if (taskInstance != null) {
                taskInstance.setStartTime(startTime);
                taskInstanceRepository.save(taskInstance);
            }
        } catch (Exception e) {
            log.error("更新任务开始时间失败: taskId={}", taskId, e);
        }
    }

    /**
     * 更新任务结果
     * 
     * @param taskId 任务ID
     * @param result 任务结果
     */
    private void updateTaskResult(String taskId, TaskResult result) {
        try {
            TaskInstance taskInstance = taskInstanceRepository.findById(taskId).orElse(null);
            if (taskInstance != null) {
                taskInstance.setOutputData(result.getOutputData());
                taskInstance.setErrorMessage(result.getErrorMessage());
                taskInstance.setEndTime(result.getEndTime());
                taskInstanceRepository.save(taskInstance);
            }
        } catch (Exception e) {
            log.error("更新任务结果失败: taskId={}", taskId, e);
        }
    }
}