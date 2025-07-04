package com.aiworkflow.engine;

import com.aiworkflow.entity.ProcessDefinition;
import com.aiworkflow.entity.ProcessInstance;

import java.util.Map;

/**
 * 流程引擎接口
 * 
 * @author AI Workflow Team
 */
public interface ProcessEngine {

    /**
     * 部署流程定义
     * 
     * @param processDefinition 流程定义
     * @return 部署结果
     */
    ProcessDefinition deployProcess(ProcessDefinition processDefinition);

    /**
     * 启动流程实例
     * 
     * @param processId 流程定义ID
     * @param businessKey 业务键
     * @param variables 流程变量
     * @return 流程实例
     */
    ProcessInstance startProcess(String processId, String businessKey, Map<String, Object> variables);

    /**
     * 暂停流程实例
     * 
     * @param instanceId 流程实例ID
     * @return 是否成功
     */
    boolean suspendProcess(String instanceId);

    /**
     * 恢复流程实例
     * 
     * @param instanceId 流程实例ID
     * @return 是否成功
     */
    boolean resumeProcess(String instanceId);

    /**
     * 终止流程实例
     * 
     * @param instanceId 流程实例ID
     * @param reason 终止原因
     * @return 是否成功
     */
    boolean terminateProcess(String instanceId, String reason);

    /**
     * 删除流程定义
     * 
     * @param processId 流程定义ID
     * @param cascade 是否级联删除实例
     * @return 是否成功
     */
    boolean deleteProcess(String processId, boolean cascade);

    /**
     * 获取流程实例
     * 
     * @param instanceId 流程实例ID
     * @return 流程实例
     */
    ProcessInstance getProcessInstance(String instanceId);

    /**
     * 获取流程定义
     * 
     * @param processId 流程定义ID
     * @return 流程定义
     */
    ProcessDefinition getProcessDefinition(String processId);
}