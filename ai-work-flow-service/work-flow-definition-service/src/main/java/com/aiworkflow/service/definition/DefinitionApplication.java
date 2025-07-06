package com.aiworkflow.service.definition;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.aiworkflow.service.definition.mapper")
public class DefinitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(DefinitionApplication.class, args);
        log.info("server start");
    }
}
