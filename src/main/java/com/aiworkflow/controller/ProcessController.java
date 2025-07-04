package com.aiworkflow.controller;

import com.aiworkflow.common.result.ApiResult;
import com.aiworkflow.engine.ProcessEngine;
import com.aiworkflow.entity.ProcessDefinition;
import com.aiworkflow.entity.ProcessInstance;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 流程管理控制器
 * 
 * @author AI Workflow Team
 */
@RestController
@RequestMapping("/api/v1/processes")
@Tag(name = "流程管理", description = "流程定义和实例管理API")
@Slf4j
public class ProcessController {

    @Autowired
    private ProcessEngine processEngine;

    @PostMapping("/definitions")
    @Operation(summary = "部署流程定义", description = "创建或更新流程定义")
    public ApiResult<ProcessDefinition> deployProcess(@RequestBody ProcessDefinition processDefinition) {
        try {
            ProcessDefinition result = processEngine.deployProcess(processDefinition);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("部署流程定义失败", e);
            return ApiResult.error("部署流程定义失败: " + e.getMessage());
        }
    }

    @PostMapping("/instances")
    @Operation(summary = "启动流程实例", description = "根据流程定义启动新的流程实例")
    public ApiResult<ProcessInstance> startProcess(
            @Parameter(description = "流程定义ID") @RequestParam String processId,
            @Parameter(description = "业务键") @RequestParam(required = false) String businessKey,
            @Parameter(description = "流程变量") @RequestBody(required = false) Map<String, Object> variables) {
        try {
            ProcessInstance result = processEngine.startProcess(processId, businessKey, variables);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("启动流程实例失败", e);
            return ApiResult.error("启动流程实例失败: " + e.getMessage());
        }
    }

    @PutMapping("/instances/{instanceId}/suspend")
    @Operation(summary = "暂停流程实例", description = "暂停指定的流程实例")
    public ApiResult<Boolean> suspendProcess(
            @Parameter(description = "流程实例ID") @PathVariable String instanceId) {
        try {
            boolean result = processEngine.suspendProcess(instanceId);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("暂停流程实例失败", e);
            return ApiResult.error("暂停流程实例失败: " + e.getMessage());
        }
    }

    @PutMapping("/instances/{instanceId}/resume")
    @Operation(summary = "恢复流程实例", description = "恢复被暂停的流程实例")
    public ApiResult<Boolean> resumeProcess(
            @Parameter(description = "流程实例ID") @PathVariable String instanceId) {
        try {
            boolean result = processEngine.resumeProcess(instanceId);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("恢复流程实例失败", e);
            return ApiResult.error("恢复流程实例失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/instances/{instanceId}")
    @Operation(summary = "终止流程实例", description = "终止指定的流程实例")
    public ApiResult<Boolean> terminateProcess(
            @Parameter(description = "流程实例ID") @PathVariable String instanceId,
            @Parameter(description = "终止原因") @RequestParam(required = false) String reason) {
        try {
            boolean result = processEngine.terminateProcess(instanceId, reason);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("终止流程实例失败", e);
            return ApiResult.error("终止流程实例失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/definitions/{processId}")
    @Operation(summary = "删除流程定义", description = "删除指定的流程定义")
    public ApiResult<Boolean> deleteProcess(
            @Parameter(description = "流程定义ID") @PathVariable String processId,
            @Parameter(description = "是否级联删除实例") @RequestParam(defaultValue = "false") boolean cascade) {
        try {
            boolean result = processEngine.deleteProcess(processId, cascade);
            return ApiResult.success(result);
        } catch (Exception e) {
            log.error("删除流程定义失败", e);
            return ApiResult.error("删除流程定义失败: " + e.getMessage());
        }
    }

    @GetMapping("/instances/{instanceId}")
    @Operation(summary = "获取流程实例", description = "根据ID获取流程实例详情")
    public ApiResult<ProcessInstance> getProcessInstance(
            @Parameter(description = "流程实例ID") @PathVariable String instanceId) {
        try {
            ProcessInstance result = processEngine.getProcessInstance(instanceId);
            if (result != null) {
                return ApiResult.success(result);
            } else {
                return ApiResult.error("流程实例不存在: " + instanceId);
            }
        } catch (Exception e) {
            log.error("获取流程实例失败", e);
            return ApiResult.error("获取流程实例失败: " + e.getMessage());
        }
    }

    @GetMapping("/definitions/{processId}")
    @Operation(summary = "获取流程定义", description = "根据ID获取流程定义详情")
    public ApiResult<ProcessDefinition> getProcessDefinition(
            @Parameter(description = "流程定义ID") @PathVariable String processId) {
        try {
            ProcessDefinition result = processEngine.getProcessDefinition(processId);
            if (result != null) {
                return ApiResult.success(result);
            } else {
                return ApiResult.error("流程定义不存在: " + processId);
            }
        } catch (Exception e) {
            log.error("获取流程定义失败", e);
            return ApiResult.error("获取流程定义失败: " + e.getMessage());
        }
    }
}