package com.aiworkflow.engine.core;

import com.aiworkflow.engine.designer.ProcessDesigner;
import com.aiworkflow.engine.executor.ProcessExecutor;
import com.aiworkflow.engine.agent.AgentManager;
import com.aiworkflow.engine.model.ProcessDefinition;
import com.aiworkflow.engine.model.ProcessInstance;
import com.aiworkflow.engine.model.TaskInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 流程引擎核心类
 * 
 * @author AI Workflow Team
 * @version 1.0
 */
@Component
@Slf4j
public class ProcessEngine {

    @Autowired
    private ProcessDesigner processDesigner;
    
    @Autowired
    private ProcessExecutor processExecutor;
    
    @Autowired
    private AgentManager agentManager;

    /**
     * 部署流程定义
     */
    public String deployProcess(String bpmnXml, String processName, String processKey) {
        log.info("开始部署流程: {}", processName);
        
        try {
            // 1. 验证和解析BPMN
            ProcessDefinition definition = processDesigner.parseAndValidateBpmn(bpmnXml, processName, processKey);
            
            // 2. 保存流程定义
            String processId = processDesigner.saveProcessDefinition(definition);
            
            log.info("流程部署成功: processId={}", processId);
            return processId;
            
        } catch (Exception e) {
            log.error("流程部署失败: {}", processName, e);
            throw new ProcessEngineException("流程部署失败", e);
        }
    }

    /**
     * 启动流程实例
     */
    public String startProcess(String processId, String businessKey, Object variables) {
        log.info("启动流程实例: processId={}, businessKey={}", processId, businessKey);
        
        try {
            // 1. 获取流程定义
            ProcessDefinition definition = processDesigner.getProcessDefinition(processId);
            
            // 2. 创建流程实例
            ProcessInstance instance = processExecutor.createProcessInstance(definition, businessKey, variables);
            
            // 3. 启动执行
            processExecutor.startExecution(instance);
            
            log.info("流程实例启动成功: instanceId={}", instance.getInstanceId());
            return instance.getInstanceId();
            
        } catch (Exception e) {
            log.error("流程启动失败: processId={}", processId, e);
            throw new ProcessEngineException("流程启动失败", e);
        }
    }

    /**
     * 执行任务
     */
    public void executeTask(String taskId, Object taskData) {
        log.info("执行任务: taskId={}", taskId);
        
        try {
            // 1. 获取任务实例
            TaskInstance task = processExecutor.getTaskInstance(taskId);
            
            // 2. 选择合适的Agent
            String agentId = agentManager.selectBestAgent(task);
            
            // 3. 执行任务
            processExecutor.executeTask(task, agentId, taskData);
            
        } catch (Exception e) {
            log.error("任务执行失败: taskId={}", taskId, e);
            throw new ProcessEngineException("任务执行失败", e);
        }
    }

    /**
     * 查询流程实例状态
     */
    public ProcessInstance getProcessInstance(String instanceId) {
        return processExecutor.getProcessInstance(instanceId);
    }

    /**
     * 停止流程实例
     */
    public void stopProcess(String instanceId, String reason) {
        log.info("停止流程实例: instanceId={}, reason={}", instanceId, reason);
        processExecutor.stopProcessInstance(instanceId, reason);
    }
}