package com.aiworkflow.common.core.spi;

import com.aiworkflow.common.core.node.NodeTypeDefinition;

import java.util.List;

/**
 * 节点类型提供者 SPI
 */
public interface NodeTypeProvider {
    /**
     * 获取提供的节点类型列表
     */
    List<NodeTypeDefinition> getProvidedNodeTypes();

    /**
     * 获取提供者优先级
     */
    default int getPriority() {
        return 0;
    }

    /**
     * 是否启用
     */
    default boolean isEnabled() {
        return true;
    }

}
