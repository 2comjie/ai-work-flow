package com.aiworkflow.config;

import com.aiworkflow.agent.impl.LLMAgent;
import com.aiworkflow.engine.AgentManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 工作流引擎初始化器
 * 
 * @author AI Workflow Team
 */
@Component
@Slf4j
public class WorkflowEngineInitializer implements ApplicationRunner {

    @Autowired
    private AgentManager agentManager;

    @Autowired
    private LLMAgent llmAgent;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("开始初始化工作流引擎...");

        // 注册内置Agent
        registerBuiltinAgents();

        log.info("工作流引擎初始化完成");
    }

    /**
     * 注册内置Agent
     */
    private void registerBuiltinAgents() {
        try {
            log.info("注册内置Agent...");

            // 注册LLM Agent
            boolean success = agentManager.registerAgent(llmAgent);
            if (success) {
                log.info("LLM Agent注册成功: {}", llmAgent.getAgentId());
            } else {
                log.error("LLM Agent注册失败: {}", llmAgent.getAgentId());
            }

            // TODO: 注册其他类型的Agent

        } catch (Exception e) {
            log.error("注册内置Agent失败", e);
        }
    }
}