package com.aiworkflow.engine.controller;

import com.aiworkflow.engine.core.ProcessEngine;
import com.aiworkflow.engine.model.ProcessInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 流程引擎REST API控制器
 */
@RestController
@RequestMapping("/api/v1/processes")
@Slf4j
public class ProcessEngineController {

    @Autowired
    private ProcessEngine processEngine;

    /**
     * 部署流程定义
     */
    @PostMapping("/definitions")
    public ResponseEntity<Map<String, Object>> deployProcess(@RequestBody DeployProcessRequest request) {
        log.info("部署流程: processName={}", request.getProcessName());

        try {
            String processId = processEngine.deployProcess(
                request.getBpmnXml(),
                request.getProcessName(),
                request.getProcessKey()
            );

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "success",
                "data", Map.of(
                    "processId", processId,
                    "status", "DEPLOYED"
                )
            ));

        } catch (Exception e) {
            log.error("流程部署失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 启动流程实例
     */
    @PostMapping("/instances")
    public ResponseEntity<Map<String, Object>> startProcess(@RequestBody StartProcessRequest request) {
        log.info("启动流程: processId={}", request.getProcessId());

        try {
            String instanceId = processEngine.startProcess(
                request.getProcessId(),
                request.getBusinessKey(),
                request.getVariables()
            );

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "success",
                "data", Map.of(
                    "instanceId", instanceId,
                    "status", "RUNNING"
                )
            ));

        } catch (Exception e) {
            log.error("流程启动失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 执行任务
     */
    @PostMapping("/tasks/{taskId}/execute")
    public ResponseEntity<Map<String, Object>> executeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Object taskData) {
        
        log.info("执行任务: taskId={}", taskId);

        try {
            processEngine.executeTask(taskId, taskData);

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "任务提交成功",
                "data", Map.of(
                    "taskId", taskId,
                    "status", "SUBMITTED"
                )
            ));

        } catch (Exception e) {
            log.error("任务执行失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 查询流程实例
     */
    @GetMapping("/instances/{instanceId}")
    public ResponseEntity<Map<String, Object>> getProcessInstance(@PathVariable String instanceId) {
        log.info("查询流程实例: instanceId={}", instanceId);

        try {
            ProcessInstance instance = processEngine.getProcessInstance(instanceId);

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "success",
                "data", Map.of(
                    "instanceId", instance.getInstanceId(),
                    "processId", instance.getProcessId(),
                    "status", instance.getStatus(),
                    "startTime", instance.getStartTime(),
                    "endTime", instance.getEndTime(),
                    "currentNodeId", instance.getCurrentNodeId(),
                    "businessKey", instance.getBusinessKey()
                )
            ));

        } catch (Exception e) {
            log.error("查询流程实例失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 停止流程实例
     */
    @PostMapping("/instances/{instanceId}/stop")
    public ResponseEntity<Map<String, Object>> stopProcess(
            @PathVariable String instanceId,
            @RequestParam(defaultValue = "用户手动停止") String reason) {
        
        log.info("停止流程实例: instanceId={}, reason={}", instanceId, reason);

        try {
            processEngine.stopProcess(instanceId, reason);

            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "流程停止成功",
                "data", Map.of(
                    "instanceId", instanceId,
                    "status", "TERMINATED"
                )
            ));

        } catch (Exception e) {
            log.error("流程停止失败", e);
            return ResponseEntity.badRequest().body(Map.of(
                "code", 400,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * 部署流程请求
     */
    public static class DeployProcessRequest {
        private String processName;
        private String processKey;
        private String bpmnXml;
        private String description;

        // getters and setters
        public String getProcessName() { return processName; }
        public void setProcessName(String processName) { this.processName = processName; }
        public String getProcessKey() { return processKey; }
        public void setProcessKey(String processKey) { this.processKey = processKey; }
        public String getBpmnXml() { return bpmnXml; }
        public void setBpmnXml(String bpmnXml) { this.bpmnXml = bpmnXml; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    /**
     * 启动流程请求
     */
    public static class StartProcessRequest {
        private String processId;
        private String businessKey;
        private Object variables;

        // getters and setters
        public String getProcessId() { return processId; }
        public void setProcessId(String processId) { this.processId = processId; }
        public String getBusinessKey() { return businessKey; }
        public void setBusinessKey(String businessKey) { this.businessKey = businessKey; }
        public Object getVariables() { return variables; }
        public void setVariables(Object variables) { this.variables = variables; }
    }
}