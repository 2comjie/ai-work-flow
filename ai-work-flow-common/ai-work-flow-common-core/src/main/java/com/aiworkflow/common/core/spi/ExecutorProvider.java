package com.aiworkflow.common.core.spi;

import com.aiworkflow.common.core.execution.NodeExecutor;

import java.util.List;

/**
 * 执行器提供者SPI
 */
public interface ExecutorProvider {

    /**
     * 获取提供的执行器列表
     */
    List<NodeExecutor> getProvidedExecutors();

    /**
     * 获取提供者优先级
     */
    default int getPriority() {
        return 0;
    }
}
