package com.aiworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI工作流编排系统主应用类
 * 
 * @author AI Workflow Team
 * @since 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
public class AiWorkflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiWorkflowApplication.class, args);
    }
}