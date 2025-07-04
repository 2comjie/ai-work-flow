package com.aiworkflow.process.interfaces.rest;

import com.aiworkflow.process.application.dto.ProcessDto;
import com.aiworkflow.process.application.dto.ProcessInstanceDto;
import com.aiworkflow.process.application.service.ProcessService;
import com.aiworkflow.process.interfaces.rest.request.CreateProcessRequest;
import com.aiworkflow.process.interfaces.rest.request.StartProcessInstanceRequest;
import com.aiworkflow.process.interfaces.rest.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * 流程管理REST控制器
 * 提供流程定义和实例管理的API接口
 */
@RestController
@RequestMapping("/api/v1/processes")
@Validated
public class ProcessController {

    private final ProcessService processService;

    @Autowired
    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    /**
     * 创建流程定义
     */
    @PostMapping("/definitions")
    public Mono<ResponseEntity<ApiResponse<ProcessDto>>> createProcessDefinition(
            @RequestBody @Valid CreateProcessRequest request) {
        
        return processService.createProcess(request)
            .map(process -> ResponseEntity.ok(ApiResponse.success("流程创建成功", process)))
            .onErrorResume(throwable -> 
                Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.error("创建流程失败: " + throwable.getMessage()))));
    }

    /**
     * 获取流程定义列表
     */
    @GetMapping("/definitions")
    public Flux<ProcessDto> getProcessDefinitions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        
        return processService.getProcesses(page, size, status);
    }

    /**
     * 根据ID获取流程定义
     */
    @GetMapping("/definitions/{processId}")
    public Mono<ResponseEntity<ApiResponse<ProcessDto>>> getProcessDefinition(
            @PathVariable String processId) {
        
        return processService.getProcessById(processId)
            .map(process -> ResponseEntity.ok(ApiResponse.success(process)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 激活流程定义
     */
    @PostMapping("/definitions/{processId}/activate")
    public Mono<ResponseEntity<ApiResponse<String>>> activateProcess(
            @PathVariable String processId) {
        
        return processService.activateProcess(processId)
            .map(result -> ResponseEntity.ok(ApiResponse.success("流程激活成功")))
            .onErrorResume(throwable ->
                Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.error("激活流程失败: " + throwable.getMessage()))));
    }

    /**
     * 暂停流程定义
     */
    @PostMapping("/definitions/{processId}/suspend")
    public Mono<ResponseEntity<ApiResponse<String>>> suspendProcess(
            @PathVariable String processId) {
        
        return processService.suspendProcess(processId)
            .map(result -> ResponseEntity.ok(ApiResponse.success("流程暂停成功")))
            .onErrorResume(throwable ->
                Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.error("暂停流程失败: " + throwable.getMessage()))));
    }

    /**
     * 启动流程实例
     */
    @PostMapping("/instances")
    public Mono<ResponseEntity<ApiResponse<ProcessInstanceDto>>> startProcessInstance(
            @RequestBody @Valid StartProcessInstanceRequest request) {
        
        return processService.startProcessInstance(request)
            .map(instance -> ResponseEntity.ok(ApiResponse.success("流程实例启动成功", instance)))
            .onErrorResume(throwable ->
                Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.error("启动流程实例失败: " + throwable.getMessage()))));
    }

    /**
     * 获取流程实例列表
     */
    @GetMapping("/instances")
    public Flux<ProcessInstanceDto> getProcessInstances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String processId,
            @RequestParam(required = false) String status) {
        
        return processService.getProcessInstances(page, size, processId, status);
    }

    /**
     * 根据ID获取流程实例
     */
    @GetMapping("/instances/{instanceId}")
    public Mono<ResponseEntity<ApiResponse<ProcessInstanceDto>>> getProcessInstance(
            @PathVariable String instanceId) {
        
        return processService.getProcessInstanceById(instanceId)
            .map(instance -> ResponseEntity.ok(ApiResponse.success(instance)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 获取流程实例变量
     */
    @GetMapping("/instances/{instanceId}/variables")
    public Mono<ResponseEntity<ApiResponse<Map<String, Object>>>> getProcessInstanceVariables(
            @PathVariable String instanceId) {
        
        return processService.getProcessInstanceVariables(instanceId)
            .map(variables -> ResponseEntity.ok(ApiResponse.success(variables)))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * 终止流程实例
     */
    @PostMapping("/instances/{instanceId}/terminate")
    public Mono<ResponseEntity<ApiResponse<String>>> terminateProcessInstance(
            @PathVariable String instanceId,
            @RequestParam(required = false) String reason) {
        
        return processService.terminateProcessInstance(instanceId, reason)
            .map(result -> ResponseEntity.ok(ApiResponse.success("流程实例已终止")))
            .onErrorResume(throwable ->
                Mono.just(ResponseEntity.badRequest()
                    .body(ApiResponse.error("终止流程实例失败: " + throwable.getMessage()))));
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<ApiResponse<String>>> health() {
        return Mono.just(ResponseEntity.ok(ApiResponse.success("Process Service is running")));
    }
}