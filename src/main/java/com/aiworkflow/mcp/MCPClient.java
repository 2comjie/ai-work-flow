package com.aiworkflow.mcp;

import com.aiworkflow.mcp.model.MCPRequest;
import com.aiworkflow.mcp.model.MCPResponse;

import java.util.concurrent.CompletableFuture;

/**
 * MCP客户端接口
 * 
 * @author AI Workflow Team
 */
public interface MCPClient {

    /**
     * 发送MCP请求
     * 
     * @param request MCP请求
     * @return MCP响应
     */
    CompletableFuture<MCPResponse> sendRequest(MCPRequest request);

    /**
     * 发送异步MCP请求
     * 
     * @param request MCP请求
     * @return MCP响应
     */
    MCPResponse sendRequestSync(MCPRequest request);

    /**
     * 检查连接状态
     * 
     * @return 是否连接
     */
    boolean isConnected();

    /**
     * 关闭连接
     */
    void close();
}