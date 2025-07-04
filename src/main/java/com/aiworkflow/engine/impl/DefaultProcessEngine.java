package com.aiworkflow.engine.impl;

import com.aiworkflow.agent.model.Task;
import com.aiworkflow.common.enums.ProcessStatus;
import com.aiworkflow.common.enums.TaskStatus;
import com.aiworkflow.common.enums.TaskType;
import com.aiworkflow.engine.ProcessEngine;
import com.aiworkflow.engine.TaskScheduler;
import com.aiworkflow.entity.ProcessDefinition;
import com.aiworkflow.entity.ProcessInstance;
import com.aiworkflow.entity.TaskInstance;
import com.aiworkflow.repository.ProcessDefinitionRepository;
import com.aiworkflow.repository.ProcessInstanceRepository;
import com.aiworkflow.repository.TaskInstanceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 流程引擎默认实现
 * 
 * @author AI Workflow Team
 */
@Service
@Slf4j
public class DefaultProcessEngine implements ProcessEngine {

    @Autowired
    private ProcessDefinitionRepository processDefinitionRepository;

    @Autowired
    private ProcessInstanceRepository processInstanceRepository;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    @Transactional
    public ProcessDefinition deployProcess(ProcessDefinition processDefinition) {
        try {
            log.info("部署流程定义: processKey={}, processName={}", 
                processDefinition.getProcessKey(), processDefinition.getProcessName());

            // 验证流程定义
            validateProcessDefinition(processDefinition);

            // 检查是否已存在相同key的流程
            ProcessDefinition existing = processDefinitionRepository
                .findByProcessKey(processDefinition.getProcessKey());
            
            if (existing != null) {
                // 更新版本号
                processDefinition.setVersion(existing.getVersion() + 1);
                // 将旧版本设为非激活状态
                existing.setStatus(ProcessStatus.SUSPENDED);
                processDefinitionRepository.save(existing);
            } else {
                processDefinition.setVersion(1);
            }

            // 设置状态为激活
            processDefinition.setStatus(ProcessStatus.ACTIVE);
            
            ProcessDefinition savedDefinition = processDefinitionRepository.save(processDefinition);

            log.info("流程定义部署成功: processId={}, version={}", 
                savedDefinition.getProcessId(), savedDefinition.getVersion());

            return savedDefinition;

        } catch (Exception e) {
            log.error("流程定义部署失败: processKey={}", processDefinition.getProcessKey(), e);
            throw new RuntimeException("流程定义部署失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ProcessInstance startProcess(String processId, String businessKey, Map<String, Object> variables) {
        try {
            log.info("启动流程实例: processId={}, businessKey={}", processId, businessKey);

            // 获取流程定义
            ProcessDefinition processDefinition = processDefinitionRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("流程定义不存在: " + processId));

            if (processDefinition.getStatus() != ProcessStatus.ACTIVE) {
                throw new RuntimeException("流程定义未激活: " + processId);
            }

            // 创建流程实例
            ProcessInstance processInstance = new ProcessInstance();
            processInstance.setProcessId(processId);
            processInstance.setBusinessKey(businessKey);
            processInstance.setStatus(ProcessStatus.ACTIVE);
            processInstance.setVariables(variables);

            ProcessInstance savedInstance = processInstanceRepository.save(processInstance);

            // 解析并创建任务
            createTasksFromProcessDefinition(savedInstance, processDefinition);

            log.info("流程实例启动成功: instanceId={}", savedInstance.getInstanceId());

            return savedInstance;

        } catch (Exception e) {
            log.error("启动流程实例失败: processId={}", processId, e);
            throw new RuntimeException("启动流程实例失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public boolean suspendProcess(String instanceId) {
        try {
            log.info("暂停流程实例: instanceId={}", instanceId);

            ProcessInstance instance = processInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("流程实例不存在: " + instanceId));

            instance.setStatus(ProcessStatus.SUSPENDED);
            processInstanceRepository.save(instance);

            // 暂停相关任务
            List<TaskInstance> tasks = taskInstanceRepository.findByInstanceId(instanceId);
            for (TaskInstance task : tasks) {
                if (task.getStatus() == TaskStatus.RUNNING || task.getStatus() == TaskStatus.PENDING) {
                    task.setStatus(TaskStatus.SKIPPED);
                    taskInstanceRepository.save(task);
                }
            }

            log.info("流程实例暂停成功: instanceId={}", instanceId);
            return true;

        } catch (Exception e) {
            log.error("暂停流程实例失败: instanceId={}", instanceId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean resumeProcess(String instanceId) {
        try {
            log.info("恢复流程实例: instanceId={}", instanceId);

            ProcessInstance instance = processInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("流程实例不存在: " + instanceId));

            instance.setStatus(ProcessStatus.ACTIVE);
            processInstanceRepository.save(instance);

            // 恢复待执行的任务
            List<TaskInstance> tasks = taskInstanceRepository.findByInstanceId(instanceId);
            for (TaskInstance task : tasks) {
                if (task.getStatus() == TaskStatus.SKIPPED) {
                    task.setStatus(TaskStatus.PENDING);
                    taskInstanceRepository.save(task);

                    // 重新提交到任务队列
                    Task agentTask = convertToAgentTask(task);
                    taskScheduler.submitTask(agentTask);
                }
            }

            log.info("流程实例恢复成功: instanceId={}", instanceId);
            return true;

        } catch (Exception e) {
            log.error("恢复流程实例失败: instanceId={}", instanceId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean terminateProcess(String instanceId, String reason) {
        try {
            log.info("终止流程实例: instanceId={}, reason={}", instanceId, reason);

            ProcessInstance instance = processInstanceRepository.findById(instanceId)
                .orElseThrow(() -> new RuntimeException("流程实例不存在: " + instanceId));

            instance.setStatus(ProcessStatus.DELETED);
            instance.setEndTime(LocalDateTime.now());
            instance.setErrorMessage(reason);
            processInstanceRepository.save(instance);

            // 终止所有相关任务
            List<TaskInstance> tasks = taskInstanceRepository.findByInstanceId(instanceId);
            for (TaskInstance task : tasks) {
                if (task.getStatus() == TaskStatus.RUNNING || task.getStatus() == TaskStatus.PENDING) {
                    task.setStatus(TaskStatus.FAILED);
                    task.setErrorMessage("流程实例被终止: " + reason);
                    task.setEndTime(LocalDateTime.now());
                    taskInstanceRepository.save(task);
                }
            }

            log.info("流程实例终止成功: instanceId={}", instanceId);
            return true;

        } catch (Exception e) {
            log.error("终止流程实例失败: instanceId={}", instanceId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteProcess(String processId, boolean cascade) {
        try {
            log.info("删除流程定义: processId={}, cascade={}", processId, cascade);

            ProcessDefinition definition = processDefinitionRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("流程定义不存在: " + processId));

            if (cascade) {
                // 级联删除所有实例
                List<ProcessInstance> instances = processInstanceRepository.findByProcessId(processId);
                for (ProcessInstance instance : instances) {
                    terminateProcess(instance.getInstanceId(), "流程定义被删除");
                }
            } else {
                // 检查是否有运行中的实例
                List<ProcessInstance> runningInstances = processInstanceRepository
                    .findByProcessIdAndStatus(processId, ProcessStatus.ACTIVE);
                if (!runningInstances.isEmpty()) {
                    throw new RuntimeException("存在运行中的流程实例，无法删除流程定义");
                }
            }

            definition.setStatus(ProcessStatus.DELETED);
            processDefinitionRepository.save(definition);

            log.info("流程定义删除成功: processId={}", processId);
            return true;

        } catch (Exception e) {
            log.error("删除流程定义失败: processId={}", processId, e);
            return false;
        }
    }

    @Override
    public ProcessInstance getProcessInstance(String instanceId) {
        return processInstanceRepository.findById(instanceId).orElse(null);
    }

    @Override
    public ProcessDefinition getProcessDefinition(String processId) {
        return processDefinitionRepository.findById(processId).orElse(null);
    }

    /**
     * 验证流程定义
     */
    private void validateProcessDefinition(ProcessDefinition processDefinition) {
        if (processDefinition.getProcessKey() == null || processDefinition.getProcessKey().trim().isEmpty()) {
            throw new IllegalArgumentException("流程键不能为空");
        }
        if (processDefinition.getProcessName() == null || processDefinition.getProcessName().trim().isEmpty()) {
            throw new IllegalArgumentException("流程名称不能为空");
        }
        // TODO: 添加BPMN XML格式验证
    }

    /**
     * 从流程定义创建任务
     */
    private void createTasksFromProcessDefinition(ProcessInstance instance, ProcessDefinition definition) {
        // 简化实现：创建一个默认的Agent任务
        // 实际应该解析BPMN XML来创建具体的任务流
        TaskInstance task = new TaskInstance();
        task.setInstanceId(instance.getInstanceId());
        task.setTaskName("默认处理任务");
        task.setTaskType(TaskType.AGENT_TASK);
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(5);
        
        TaskInstance savedTask = taskInstanceRepository.save(task);

        // 提交到任务调度器
        Task agentTask = convertToAgentTask(savedTask);
        taskScheduler.submitTask(agentTask);
    }

    /**
     * 转换为Agent任务
     */
    private Task convertToAgentTask(TaskInstance taskInstance) {
        Task task = new Task();
        task.setTaskId(taskInstance.getTaskId());
        task.setTaskName(taskInstance.getTaskName());
        task.setTaskType(taskInstance.getTaskType());
        task.setStatus(taskInstance.getStatus());
        task.setPriority(taskInstance.getPriority());
        task.setProcessInstanceId(taskInstance.getInstanceId());
        task.setInputData(taskInstance.getInputData());
        task.setCreatedTime(LocalDateTime.now());
        task.setMaxRetries(3);
        task.setTimeoutMs(300000L); // 5分钟超时
        
        return task;
    }
}