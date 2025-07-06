package com.aiworkflow.common.core.spi;

import com.aiworkflow.common.core.execution.NodeExecutor;
import com.aiworkflow.common.core.node.NodeTypeDefinition;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class SPIManager {

    private final Map<String, NodeTypeDefinition> nodeTypeRegistry = new ConcurrentHashMap<>();

    private final Map<String, NodeExecutor> executorRegistry = new ConcurrentHashMap<>();

    private List<NodeTypeProvider> nodeTypeProviders = new ArrayList<>();

    private List<ExecutorProvider> executorProviders = new ArrayList<>();

    @Autowired(required = false)
    public void setNodeTypeProviders(List<NodeTypeProvider> providers) {
        this.nodeTypeProviders = providers != null ? providers : new ArrayList<>();
    }

    @Autowired(required = false)
    public void setExecutorProviders(List<ExecutorProvider> providers) {
        this.executorProviders = providers != null ? providers : new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        loadNodeTypes();
        loadExecutors();
        logRegistryStatus();
    }

    /**
     * 加载节点
     */
    private void loadNodeTypes() {
        // 按优先级排序
        nodeTypeProviders.sort((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()));
        for (NodeTypeProvider provider : nodeTypeProviders) {
            if (provider.isEnabled()) {
                List<NodeTypeDefinition> nodeTypes = provider.getProvidedNodeTypes();
                for (NodeTypeDefinition nodeType : nodeTypes) {
                    String taskType = nodeType.getTaskType().code();
                    if (nodeTypeRegistry.containsKey(taskType)) {
                        log.warn("节点类型 {} 已存在，来自提供者: {}，将被覆盖",
                                taskType, provider.getClass().getSimpleName());
                    }
                    nodeTypeRegistry.put(taskType, nodeType);
                    log.info("注册节点类型: {} -> {}", taskType, nodeType.getDisplayName());
                }
                log.info("加载节点类型提供者: {}，提供 {} 个节点类型",
                        provider.getClass().getSimpleName(), nodeTypes.size());
            }
        }
    }

    /**
     * 加载执行器
     */
    private void loadExecutors() {
        // 按优先级排序
        executorProviders.sort((p1, p2) -> Integer.compare(p2.getPriority(), p1.getPriority()));

        for (ExecutorProvider provider : executorProviders) {
            List<NodeExecutor> executors = provider.getProvidedExecutors();
            for (NodeExecutor executor : executors) {
                String[] supportedTypes = executor.getSupportedNodeTypes();
                for (String nodeType : supportedTypes) {
                    if (executorRegistry.containsKey(nodeType)) {
                        log.warn("节点类型 {} 的执行器已存在，来自提供者: {}，将被覆盖",
                                nodeType, provider.getClass().getSimpleName());
                    }

                    executorRegistry.put(nodeType, executor);
                    log.info("注册执行器: {} -> {}", nodeType, executor.getClass().getSimpleName());
                }
            }
            log.info("加载执行器提供者: {}，提供 {} 个执行器",
                    provider.getClass().getSimpleName(), executors.size());
        }
    }

    private void logRegistryStatus() {
        log.info("=== SPI注册完成 ===");
        log.info("节点类型提供者数量: {}", nodeTypeProviders.size());
        log.info("执行器提供者数量: {}", executorProviders.size());
        log.info("已注册节点类型: {}", nodeTypeRegistry.keySet());
        log.info("已注册执行器支持的节点类型: {}", executorRegistry.keySet());
        log.info("==================");
    }

    /**
     * 获取所有节点类型定义
     */
    public List<NodeTypeDefinition> getAllNodeTypes() {
        return new ArrayList<>(nodeTypeRegistry.values());
    }

    /**
     * 根据任务类型获取节点类型定义
     */
    public Optional<NodeTypeDefinition> getNodeTypeByTaskType(String taskType) {
        return Optional.ofNullable(nodeTypeRegistry.get(taskType));
    }

    /**
     * 获取所有执行器
     */
    public List<NodeExecutor> getAllExecutors() {
        return new ArrayList<>(executorRegistry.values());
    }

    /**
     * 根据节点类型获取执行器
     */
    public Optional<NodeExecutor> getExecutorByNodeType(String nodeType) {
        return Optional.ofNullable(executorRegistry.get(nodeType));
    }

    /**
     * 获取所有支持的节点类型
     */
    public Set<String> getSupportedNodeTypes() {
        return new HashSet<>(nodeTypeRegistry.keySet());
    }

    /**
     * 检查节点类型是否支持
     */
    public boolean isNodeTypeSupported(String nodeType) {
        return nodeTypeRegistry.containsKey(nodeType) && executorRegistry.containsKey(nodeType);
    }

    /**
     * 获取节点类型统计信息
     */
    public Map<String, Object> getRegistryStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("nodeTypeCount", nodeTypeRegistry.size());
        stats.put("executorCount", executorRegistry.size());
        stats.put("providerCount", nodeTypeProviders.size() + executorProviders.size());
        stats.put("supportedNodeTypes", getSupportedNodeTypes());

        Map<String, Integer> categoryStats = nodeTypeRegistry.values().stream()
                .collect(Collectors.groupingBy(
                        NodeTypeDefinition::getCategory,
                        Collectors.summingInt(v -> 1)
                ));
        stats.put("categoryStats", categoryStats);

        return stats;
    }

    /**
     * 重新加载SPI提供者
     */
    public void reload() {
        nodeTypeRegistry.clear();
        executorRegistry.clear();
        loadNodeTypes();
        loadExecutors();
        logRegistryStatus();
        log.info("SPI重新加载完成");
    }

    /**
     * 动态注册节点类型定义
     */
    public void registerNodeType(NodeTypeDefinition nodeType) {
        String taskType = nodeType.getTaskType().code();
        nodeTypeRegistry.put(taskType, nodeType);
        log.info("动态注册节点类型: {} -> {}", taskType, nodeType.getDisplayName());
    }

    /**
     * 动态注册执行器
     */
    public void registerExecutor(NodeExecutor executor) {
        String[] supportedTypes = executor.getSupportedNodeTypes();
        for (String nodeType : supportedTypes) {
            executorRegistry.put(nodeType, executor);
            log.info("动态注册执行器: {} -> {}", nodeType, executor.getClass().getSimpleName());
        }
    }
}
