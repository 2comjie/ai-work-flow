package com.aiworkflow.process;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * AI工作流流程服务启动类
 */
@SpringBootApplication(scanBasePackages = {
    "com.aiworkflow.process",
    "com.aiworkflow.domain"
})
@EnableR2dbcRepositories(basePackages = "com.aiworkflow.process.infrastructure.repository")
@EnableKafka
@EnableAsync
@EnableTransactionManagement
@EnableConfigurationProperties
public class ProcessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcessServiceApplication.class, args);
    }
}