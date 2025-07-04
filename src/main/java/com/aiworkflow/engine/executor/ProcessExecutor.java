package com.aiworkflow.engine.executor;

import com.aiworkflow.engine.core.ProcessEngineException;
import com.aiworkflow.engine.designer.ProcessDesigner;
import com.aiworkflow.engine.model.*;
import com.aiworkflow.engine.repository.ProcessInstanceRepository;
import com.aiworkflow.engine.repository.TaskInstanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 流程执行器
 * 负责流程实例创建、任务调度、状态管理等功能
 */
@Component
@Slf4j
public class ProcessExecutor {

    @Autowired
    private ProcessDesigner processDesigner;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private StateManager stateManager;

    @Autowired
    private EventHandler eventHandler;

    @Autowired
    private ProcessInstanceRepository processInstanceRepository;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 创建流程实例
     */
    public ProcessInstance createProcessInstance(ProcessDefinition definition, String businessKey, Object variables) {
        log.info("创建流程实例: processId={}, businessKey={}", definition.getProcessId(), businessKey);

        try {
            // 1. 创建流程实例
            ProcessInstance instance = ProcessInstance.builder()
                .instanceId(generateInstanceId())
                .processId(definition.getProcessId())
                .businessKey(businessKey)
                .status(ProcessInstance.InstanceStatus.RUNNING)
                .build();

            // 2. 设置变量
            if (variables != null) {
                String variablesJson = objectMapper.writeValueAsString(variables);
                instance.setVariablesJson(variablesJson);
            }

            // 3. 保存实例
            ProcessInstance saved = processInstanceRepository.save(instance);
            log.info("流程实例创建成功: instanceId={}", saved.getInstanceId());

            return saved;

        } catch (Exception e) {
            log.error("流程实例创建失败: processId={}", definition.getProcessId(), e);
            throw new ProcessEngineException.ProcessExecutionException("流程实例创建失败", e);
        }
    }

    /**
     * 启动流程执行
     */
    @Async("processExecutor")
    public CompletableFuture<Void> startExecution(ProcessInstance instance) {
        log.info("启动流程执行: instanceId={}", instance.getInstanceId());

        try {
            // 1. 获取流程定义
            ProcessDefinition definition = processDesigner.getProcessDefinition(instance.getProcessId());

            // 2. 获取开始节点
            ProcessNode startNode = processDesigner.getStartNode(definition);

            // 3. 设置当前节点
            instance.setCurrentNodeId(startNode.getNodeId());
            processInstanceRepository.save(instance);

            // 4. 触发开始事件
            eventHandler.handleStartEvent(instance, startNode);

            // 5. 执行下一步
            executeNextNodes(instance, definition, startNode);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("流程执行启动失败: instanceId={}", instance.getInstanceId(), e);
            
            // 更新实例状态为失败
            instance.setStatus(ProcessInstance.InstanceStatus.FAILED);
            instance.setErrorMessage(e.getMessage());
            processInstanceRepository.save(instance);

            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 执行任务
     */
    @Async("taskExecutor")
    public CompletableFuture<Void> executeTask(TaskInstance task, String agentId, Object taskData) {
        log.info("执行任务: taskId={}, agentId={}", task.getTaskId(), agentId);

        try {
            // 1. 更新任务状态
            task.setStatus(TaskInstance.TaskStatus.RUNNING);
            task.setAgentId(agentId);
            taskInstanceRepository.save(task);

            // 2. 提交到任务调度器
            taskScheduler.submitTask(task, taskData);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("任务执行失败: taskId={}", task.getTaskId(), e);
            
            // 更新任务状态
            task.setStatus(TaskInstance.TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            taskInstanceRepository.save(task);

            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 任务完成回调
     */
    public void onTaskCompleted(String taskId, Object result) {
        log.info("任务完成回调: taskId={}", taskId);

        try {
            // 1. 获取任务实例
            TaskInstance task = getTaskInstance(taskId);
            
            // 2. 更新任务状态
            task.setStatus(TaskInstance.TaskStatus.COMPLETED);
            if (result != null) {
                task.setOutputDataJson(objectMapper.writeValueAsString(result));
            }
            taskInstanceRepository.save(task);

            // 3. 获取流程实例和定义
            ProcessInstance instance = getProcessInstance(task.getInstanceId());
            ProcessDefinition definition = processDesigner.getProcessDefinition(instance.getProcessId());

            // 4. 继续执行流程
            ProcessNode currentNode = processDesigner.getNodeById(definition, task.getNodeId());
            executeNextNodes(instance, definition, currentNode);

        } catch (Exception e) {
            log.error("任务完成回调处理失败: taskId={}", taskId, e);
        }
    }

    /**
     * 任务失败回调
     */
    public void onTaskFailed(String taskId, String errorMessage) {
        log.info("任务失败回调: taskId={}, error={}", taskId, errorMessage);

        try {
            TaskInstance task = getTaskInstance(taskId);
            
            // 检查是否可以重试
            if (task.canRetry()) {
                task.incrementRetryCount();
                task.setStatus(TaskInstance.TaskStatus.PENDING);
                task.setErrorMessage(null);
                taskInstanceRepository.save(task);
                
                log.info("任务重试: taskId={}, retryCount={}", taskId, task.getRetryCount());
                // 重新提交任务
                taskScheduler.retryTask(task);
            } else {
                // 标记任务失败
                task.setStatus(TaskInstance.TaskStatus.FAILED);
                task.setErrorMessage(errorMessage);
                taskInstanceRepository.save(task);

                // 标记流程实例失败
                ProcessInstance instance = getProcessInstance(task.getInstanceId());
                instance.setStatus(ProcessInstance.InstanceStatus.FAILED);
                instance.setErrorMessage("任务执行失败: " + errorMessage);
                processInstanceRepository.save(instance);
            }

        } catch (Exception e) {
            log.error("任务失败回调处理失败: taskId={}", taskId, e);
        }
    }

    /**
     * 执行下一个节点
     */
    private void executeNextNodes(ProcessInstance instance, ProcessDefinition definition, ProcessNode currentNode) {
        log.debug("执行下一个节点: instanceId={}, currentNode={}", instance.getInstanceId(), currentNode.getNodeId());

        try {
            List<ProcessNode> nextNodes = processDesigner.getNextNodes(definition, currentNode.getNodeId());

            if (nextNodes.isEmpty()) {
                // 没有下一个节点，流程结束
                completeProcess(instance);
                return;
            }

            for (ProcessNode nextNode : nextNodes) {
                if (nextNode.isEndNode()) {
                    // 到达结束节点
                    completeProcess(instance);
                } else if (nextNode.isTask()) {
                    // 创建任务实例
                    createTaskInstance(instance, nextNode);
                } else if (nextNode.isGateway()) {
                    // 处理网关逻辑
                    handleGateway(instance, definition, nextNode);
                }
            }

        } catch (Exception e) {
            log.error("执行下一个节点失败: instanceId={}", instance.getInstanceId(), e);
            throw new ProcessEngineException.ProcessExecutionException("执行下一个节点失败", e);
        }
    }

    /**
     * 创建任务实例
     */
    private void createTaskInstance(ProcessInstance instance, ProcessNode node) {
        TaskInstance task = TaskInstance.builder()
            .taskId(generateTaskId())
            .instanceId(instance.getInstanceId())
            .nodeId(node.getNodeId())
            .taskName(node.getNodeName())
            .taskType(mapToTaskType(node.getNodeType()))
            .priority(node.getPriority() != null ? node.getPriority() : 0)
            .maxRetries(node.getMaxRetries() != null ? node.getMaxRetries() : 3)
            .status(TaskInstance.TaskStatus.PENDING)
            .build();

        taskInstanceRepository.save(task);
        
        // 更新流程实例当前节点
        instance.setCurrentNodeId(node.getNodeId());
        processInstanceRepository.save(instance);

        log.info("任务实例创建成功: taskId={}, nodeId={}", task.getTaskId(), node.getNodeId());
    }

    /**
     * 处理网关
     */
    private void handleGateway(ProcessInstance instance, ProcessDefinition definition, ProcessNode gateway) {
        // TODO: 实现网关逻辑（排他、并行、包容）
        log.info("处理网关: instanceId={}, gatewayType={}", instance.getInstanceId(), gateway.getNodeType());
        
        // 目前简单处理：直接执行下一个节点
        executeNextNodes(instance, definition, gateway);
    }

    /**
     * 完成流程
     */
    private void completeProcess(ProcessInstance instance) {
        log.info("完成流程: instanceId={}", instance.getInstanceId());
        
        instance.setStatus(ProcessInstance.InstanceStatus.COMPLETED);
        processInstanceRepository.save(instance);

        // 触发完成事件
        eventHandler.handleEndEvent(instance);
    }

    /**
     * 停止流程实例
     */
    public void stopProcessInstance(String instanceId, String reason) {
        ProcessInstance instance = getProcessInstance(instanceId);
        instance.setStatus(ProcessInstance.InstanceStatus.TERMINATED);
        instance.setErrorMessage(reason);
        processInstanceRepository.save(instance);
        
        log.info("流程实例已停止: instanceId={}, reason={}", instanceId, reason);
    }

    /**
     * 获取流程实例
     */
    public ProcessInstance getProcessInstance(String instanceId) {
        return processInstanceRepository.findById(instanceId)
            .orElseThrow(() -> new ProcessEngineException.ProcessExecutionException("流程实例不存在: " + instanceId));
    }

    /**
     * 获取任务实例
     */
    public TaskInstance getTaskInstance(String taskId) {
        return taskInstanceRepository.findById(taskId)
            .orElseThrow(() -> new ProcessEngineException.TaskExecutionException("任务实例不存在: " + taskId));
    }

    /**
     * 映射节点类型到任务类型
     */
    private TaskInstance.TaskType mapToTaskType(ProcessNode.NodeType nodeType) {
        return switch (nodeType) {
            case USER_TASK -> TaskInstance.TaskType.USER_TASK;
            case SERVICE_TASK -> TaskInstance.TaskType.SERVICE_TASK;
            case SCRIPT_TASK -> TaskInstance.TaskType.SCRIPT_TASK;
            case AGENT_TASK -> TaskInstance.TaskType.AGENT_TASK;
            default -> TaskInstance.TaskType.SERVICE_TASK;
        };
    }

    /**
     * 生成实例ID
     */
    private String generateInstanceId() {
        return "inst_" + UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "task_" + UUID.randomUUID().toString().replace("-", "");
    }
}