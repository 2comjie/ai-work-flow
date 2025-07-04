package com.aiworkflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI工作流编排系统启动类
 * 
 * @author AI Workflow Team
 * @version 1.0
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("==============================================");
        System.out.println("AI工作流编排系统启动成功!");
        System.out.println("API文档地址: http://localhost:8080/swagger-ui.html");
        System.out.println("==============================================");
    }
}