package com.aiworkflow.agent.impl;

import com.aiworkflow.agent.Agent;
import com.aiworkflow.agent.model.AgentCapability;
import com.aiworkflow.agent.model.Task;
import com.aiworkflow.agent.model.TaskResult;
import com.aiworkflow.common.enums.HealthStatus;
import com.aiworkflow.common.enums.TaskType;
import com.aiworkflow.mcp.MCPClient;
import com.aiworkflow.mcp.model.MCPRequest;
import com.aiworkflow.mcp.model.MCPResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LLM Agent实现
 * 支持通过MCP协议调用大语言模型
 * 
 * @author AI Workflow Team
 */
@Component
@Slf4j
public class LLMAgent implements Agent {

    private static final String AGENT_ID = "llm-agent-001";
    private static final String AGENT_NAME = "智能文本处理Agent";

    @Autowired
    private MCPClient mcpClient;

    private AgentCapability capability;

    @Override
    public String getAgentId() {
        return AGENT_ID;
    }

    @Override
    public String getAgentName() {
        return AGENT_NAME;
    }

    @Override
    public AgentCapability getCapability() {
        if (capability == null) {
            capability = createCapability();
        }
        return capability;
    }

    @Override
    public TaskResult execute(Task task) {
        LocalDateTime startTime = LocalDateTime.now();
        
        try {
            log.info("开始执行任务: taskId={}, taskName={}", task.getTaskId(), task.getTaskName());

            // 验证任务类型
            if (!getCapability().supportsTaskType(task.getTaskType())) {
                return TaskResult.failure(task.getTaskId(), "UNSUPPORTED_TASK_TYPE", 
                    "不支持的任务类型: " + task.getTaskType());
            }

            // 构建MCP请求
            MCPRequest mcpRequest = buildMCPRequest(task);

            // 发送MCP请求
            MCPResponse mcpResponse = mcpClient.sendRequestSync(mcpRequest);

            // 处理响应
            TaskResult result = processResponse(task, mcpResponse);
            result.setStartTime(startTime);
            result.setExecutedBy(getAgentId());

            log.info("任务执行完成: taskId={}, status={}", task.getTaskId(), result.getStatus());
            return result;

        } catch (Exception e) {
            log.error("任务执行失败: taskId={}", task.getTaskId(), e);
            
            TaskResult errorResult = TaskResult.failure(task.getTaskId(), e.getMessage());
            errorResult.setStartTime(startTime);
            errorResult.setExecutedBy(getAgentId());
            return errorResult;
        }
    }

    @Override
    public HealthStatus getHealthStatus() {
        try {
            // 检查MCP客户端连接状态
            if (mcpClient.isConnected()) {
                return HealthStatus.HEALTHY;
            } else {
                return HealthStatus.UNHEALTHY;
            }
        } catch (Exception e) {
            log.warn("健康检查失败: agentId={}", getAgentId(), e);
            return HealthStatus.UNKNOWN;
        }
    }

    @Override
    public void initialize() {
        log.info("初始化LLM Agent: agentId={}", getAgentId());
        // 初始化逻辑
    }

    @Override
    public void destroy() {
        log.info("销毁LLM Agent: agentId={}", getAgentId());
        // 清理资源
    }

    /**
     * 创建Agent能力配置
     */
    private AgentCapability createCapability() {
        AgentCapability capability = new AgentCapability();
        
        // 支持的任务类型
        capability.setSupportedTaskTypes(Arrays.asList(
            TaskType.AGENT_TASK, 
            TaskType.SERVICE_TASK
        ));
        
        // 支持的技能
        capability.setSupportedSkills(Arrays.asList(
            "文本分析", "内容生成", "情感分析", "文本摘要", "问答对话"
        ));
        
        // 最大并发数
        capability.setMaxConcurrency(5);
        
        // 支持的语言
        capability.setSupportedLanguages(Arrays.asList("zh-CN", "en-US"));
        
        // 平均处理时间
        capability.setAvgProcessingTimeMs(3000L);
        
        // 成功率
        capability.setSuccessRate(0.95);
        
        return capability;
    }

    /**
     * 构建MCP请求
     */
    private MCPRequest buildMCPRequest(Task task) {
        MCPRequest request = new MCPRequest();
        request.setRequestId(task.getTaskId());
        request.setMethod("completion");
        
        // 从任务输入数据中获取消息内容
        String message = extractMessage(task);
        request.setMessage(message);
        
        // 设置模型配置
        MCPRequest.ModelConfig modelConfig = new MCPRequest.ModelConfig();
        modelConfig.setModelName("qwen-turbo"); // 阿里通义千问
        modelConfig.setTemperature(0.7);
        modelConfig.setMaxTokens(2048);
        request.setModelConfig(modelConfig);
        
        return request;
    }

    /**
     * 从任务中提取消息内容
     */
    private String extractMessage(Task task) {
        Map<String, Object> inputData = task.getInputData();
        if (inputData != null) {
            Object messageObj = inputData.get("message");
            if (messageObj != null) {
                return messageObj.toString();
            }
            
            Object promptObj = inputData.get("prompt");
            if (promptObj != null) {
                return promptObj.toString();
            }
        }
        
        // 默认消息
        return "请处理任务: " + task.getTaskName();
    }

    /**
     * 处理MCP响应
     */
    private TaskResult processResponse(Task task, MCPResponse response) {
        if (response.getStatus() == MCPResponse.ResponseStatus.SUCCESS) {
            // 成功响应
            Map<String, Object> outputData = new HashMap<>();
            outputData.put("response", response.getContent());
            outputData.put("tokenUsage", response.getTokenUsage());
            outputData.put("processingTime", response.getProcessingTimeMs());
            
            return TaskResult.success(task.getTaskId(), outputData);
        } else {
            // 错误响应
            String errorMessage = response.getError() != null ? 
                response.getError().getErrorMessage() : "MCP请求失败";
            String errorCode = response.getError() != null ? 
                response.getError().getErrorCode() : "MCP_ERROR";
                
            return TaskResult.failure(task.getTaskId(), errorCode, errorMessage);
        }
    }
}