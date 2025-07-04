package com.aiworkflow.engine.designer;

import com.aiworkflow.engine.core.ProcessEngineException;
import com.aiworkflow.engine.model.ProcessDefinition;
import com.aiworkflow.engine.model.ProcessNode;
import com.aiworkflow.engine.repository.ProcessDefinitionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * 流程设计器
 * 负责BPMN解析、流程定义管理、流程验证等功能
 */
@Component
@Slf4j
public class ProcessDesigner {

    @Autowired
    private BpmnParser bpmnParser;

    @Autowired
    private ProcessValidator processValidator;

    @Autowired
    private ProcessDefinitionRepository processDefinitionRepository;

    /**
     * 解析和验证BPMN
     */
    public ProcessDefinition parseAndValidateBpmn(String bpmnXml, String processName, String processKey) {
        log.info("开始解析BPMN: processName={}, processKey={}", processName, processKey);

        try {
            // 1. 解析BPMN XML
            List<ProcessNode> processNodes = bpmnParser.parse(bpmnXml);
            
            // 2. 创建流程定义
            ProcessDefinition definition = ProcessDefinition.builder()
                .processId(generateProcessId())
                .processName(processName)
                .processKey(processKey)
                .bpmnXml(bpmnXml)
                .status(ProcessDefinition.ProcessStatus.DRAFT)
                .processNodes(processNodes)
                .build();

            // 3. 验证流程定义
            processValidator.validate(definition);

            log.info("BPMN解析完成: processId={}, 节点数量={}", definition.getProcessId(), processNodes.size());
            return definition;

        } catch (Exception e) {
            log.error("BPMN解析失败: processName={}", processName, e);
            throw new ProcessEngineException.BpmnParseException("BPMN解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 保存流程定义
     */
    public String saveProcessDefinition(ProcessDefinition definition) {
        log.info("保存流程定义: processId={}", definition.getProcessId());

        try {
            // 检查是否存在同名流程
            if (processDefinitionRepository.existsByProcessKey(definition.getProcessKey())) {
                // 版本递增
                Integer maxVersion = processDefinitionRepository.findMaxVersionByProcessKey(definition.getProcessKey());
                definition.setVersion((maxVersion != null ? maxVersion : 0) + 1);
            }

            ProcessDefinition saved = processDefinitionRepository.save(definition);
            log.info("流程定义保存成功: processId={}, version={}", saved.getProcessId(), saved.getVersion());
            
            return saved.getProcessId();

        } catch (Exception e) {
            log.error("流程定义保存失败: processId={}", definition.getProcessId(), e);
            throw new ProcessEngineException.ProcessDefinitionException("流程定义保存失败", e);
        }
    }

    /**
     * 获取流程定义
     */
    public ProcessDefinition getProcessDefinition(String processId) {
        ProcessDefinition definition = processDefinitionRepository.findById(processId)
            .orElseThrow(() -> new ProcessEngineException.ProcessDefinitionException("流程定义不存在: " + processId));

        // 重新解析BPMN以获取节点信息
        if (definition.getBpmnXml() != null) {
            try {
                List<ProcessNode> processNodes = bpmnParser.parse(definition.getBpmnXml());
                definition.setProcessNodes(processNodes);
            } catch (Exception e) {
                log.warn("重新解析BPMN失败: processId={}", processId, e);
            }
        }

        return definition;
    }

    /**
     * 激活流程定义
     */
    public void activateProcess(String processId) {
        log.info("激活流程定义: processId={}", processId);

        ProcessDefinition definition = getProcessDefinition(processId);
        
        // 再次验证流程
        processValidator.validate(definition);
        
        definition.setStatus(ProcessDefinition.ProcessStatus.ACTIVE);
        processDefinitionRepository.save(definition);

        log.info("流程定义激活成功: processId={}", processId);
    }

    /**
     * 暂停流程定义
     */
    public void suspendProcess(String processId) {
        log.info("暂停流程定义: processId={}", processId);

        ProcessDefinition definition = getProcessDefinition(processId);
        definition.setStatus(ProcessDefinition.ProcessStatus.SUSPENDED);
        processDefinitionRepository.save(definition);

        log.info("流程定义暂停成功: processId={}", processId);
    }

    /**
     * 删除流程定义
     */
    public void deleteProcess(String processId) {
        log.info("删除流程定义: processId={}", processId);

        ProcessDefinition definition = getProcessDefinition(processId);
        
        // 检查是否有运行中的实例
        // TODO: 实现实例检查逻辑
        
        definition.setStatus(ProcessDefinition.ProcessStatus.DELETED);
        processDefinitionRepository.save(definition);

        log.info("流程定义删除成功: processId={}", processId);
    }

    /**
     * 获取流程的开始节点
     */
    public ProcessNode getStartNode(ProcessDefinition definition) {
        return definition.getProcessNodes().stream()
            .filter(ProcessNode::isStartNode)
            .findFirst()
            .orElseThrow(() -> new ProcessEngineException.ProcessDefinitionException("流程缺少开始节点"));
    }

    /**
     * 根据节点ID获取节点
     */
    public ProcessNode getNodeById(ProcessDefinition definition, String nodeId) {
        return definition.getProcessNodes().stream()
            .filter(node -> node.getNodeId().equals(nodeId))
            .findFirst()
            .orElseThrow(() -> new ProcessEngineException.ProcessDefinitionException("节点不存在: " + nodeId));
    }

    /**
     * 获取节点的下一个节点列表
     */
    public List<ProcessNode> getNextNodes(ProcessDefinition definition, String nodeId) {
        ProcessNode currentNode = getNodeById(definition, nodeId);
        
        return currentNode.getOutgoingFlows().stream()
            .map(flowId -> getNodeById(definition, flowId))
            .toList();
    }

    /**
     * 生成流程ID
     */
    private String generateProcessId() {
        return "proc_" + UUID.randomUUID().toString().replace("-", "");
    }
}