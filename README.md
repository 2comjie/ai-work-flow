# AI工作流编排系统

## 系统概述

本项目是一个**基于SpringBoot+MCP+流程引擎的多Agent编排系统**，类似Dify但核心差异化在于支持MCP（Model Context Protocol）协议，为企业提供标准化的AI工作流编排能力。

## 核心特性

- 🎯 **MCP协议支持**：标准化AI模型交互，支持阿里通义千问等国内MCP模型
- ⚙️ **自研流程引擎**：可视化流程设计，多Agent协作编排
- 🚀 **企业级部署**：高可用、可扩展的生产环境支持
- 🔗 **开放生态**：标准化接口，易于集成第三方工具和服务

## 技术架构

### 技术栈
- **后端框架**：Spring Boot 3.5+, Java 17
- **协议标准**：MCP (Model Context Protocol) 
- **数据存储**：MySQL 8.0+, Redis 7.0+
- **消息队列**：Redis Stream / RabbitMQ
- **容器化**：Docker + Kubernetes

### 核心模块
- **MCP协议引擎**：标准化AI模型交互
- **流程编排引擎**：可视化流程设计和执行
- **Agent管理器**：多Agent注册、发现和负载均衡
- **任务调度器**：分布式任务队列和调度
- **API网关**：统一API接口和文档

## 快速开始

### 环境要求

- Java 17+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.6+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-org/ai-workflow.git
cd ai-workflow
```

2. **配置数据库**
```sql
CREATE DATABASE ai_workflow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置文件**
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_workflow
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
```

4. **启动应用**
```bash
mvn spring-boot:run
```

5. **访问接口文档**
打开浏览器访问：http://localhost:8080/swagger-ui.html

## API使用示例

### 1. 部署流程定义

```bash
curl -X POST http://localhost:8080/api/v1/processes/definitions \
  -H "Content-Type: application/json" \
  -d '{
    "processName": "智能客服流程",
    "processKey": "customer_service_flow",
    "description": "AI客服自动处理用户问题"
  }'
```

### 2. 启动流程实例

```bash
curl -X POST "http://localhost:8080/api/v1/processes/instances?processId=proc_123&businessKey=order_001" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "用户咨询产品价格",
    "customerId": "cust_001",
    "priority": "HIGH"
  }'
```

### 3. 查询流程状态

```bash
curl -X GET http://localhost:8080/api/v1/processes/instances/inst_123
```

## 系统设计

### 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    用户接入层                                │
├─────────────────┬─────────────────┬─────────────────────────┤
│   Web管理界面    │   REST API      │   WebSocket实时通信      │
└─────────────────┴─────────────────┴─────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                  核心业务层                                 │
├─────────────────┬─────────────────┬─────────────────────────┤
│  🔗 MCP协议引擎  │  ⚙️ 流程编排引擎 │   Agent管理器           │
└─────────────────┴─────────────────┴─────────────────────────┘
                            │
┌─────────────────────────────────────────────────────────────┐
│                  数据存储层                                 │
├─────────────────┬─────────────────┬─────────────────────────┤
│   MySQL主库     │   Redis缓存     │   文件存储              │
└─────────────────┴─────────────────┴─────────────────────────┘
```

### 核心组件

#### MCP协议引擎
- **协议处理器**：处理MCP标准协议
- **模型适配器**：适配不同AI模型
- **连接管理器**：管理模型连接池

#### 流程编排引擎
- **流程设计器**：可视化流程定义
- **流程执行器**：流程实例执行
- **任务调度器**：分布式任务调度

#### Agent管理器
- **Agent注册中心**：Agent注册和发现
- **负载均衡器**：智能负载分配
- **健康检查器**：Agent健康监控

## Agent开发指南

### 创建自定义Agent

```java
@Component
public class CustomAgent implements Agent {
    
    @Override
    public String getAgentId() {
        return "custom-agent-001";
    }
    
    @Override
    public String getAgentName() {
        return "自定义处理Agent";
    }
    
    @Override
    public AgentCapability getCapability() {
        AgentCapability capability = new AgentCapability();
        capability.setSupportedTaskTypes(Arrays.asList(TaskType.CUSTOM_TASK));
        capability.setSupportedSkills(Arrays.asList("数据处理", "文件转换"));
        capability.setMaxConcurrency(10);
        return capability;
    }
    
    @Override
    public TaskResult execute(Task task) {
        // 实现具体的任务处理逻辑
        Map<String, Object> result = processTask(task);
        return TaskResult.success(task.getTaskId(), result);
    }
    
    // 其他必需方法的实现...
}
```

### Agent注册

Agent会在系统启动时自动注册，或者可以通过API动态注册：

```java
@Autowired
private AgentManager agentManager;

// 注册Agent
agentManager.registerAgent(customAgent);
```

## MCP协议集成

### 支持的模型

- ✅ 阿里通义千问 (Qwen)
- ✅ 百度文心一言 (ERNIE)
- 🔄 OpenAI GPT (开发中)
- 🔄 Google Gemini (开发中)

### MCP请求示例

```java
MCPRequest request = new MCPRequest();
request.setMethod("completion");
request.setMessage("请分析这段文本的情感倾向");

MCPRequest.ModelConfig config = new MCPRequest.ModelConfig();
config.setModelName("qwen-turbo");
config.setTemperature(0.7);
config.setMaxTokens(2048);
request.setModelConfig(config);

MCPResponse response = mcpClient.sendRequestSync(request);
```

## 部署指南

### Docker部署

1. **构建镜像**
```bash
docker build -t ai-workflow:latest .
```

2. **启动服务**
```bash
docker-compose up -d
```

### Kubernetes部署

```bash
kubectl apply -f k8s/
```

## 监控与运维

### 健康检查
- **应用健康**：`/actuator/health`
- **指标监控**：`/actuator/metrics`
- **Prometheus**：`/actuator/prometheus`

### 日志管理
- **应用日志**：`logs/ai-workflow.log`
- **任务日志**：Redis Stream
- **审计日志**：数据库存储

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交改动 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系我们

- 项目主页：https://github.com/your-org/ai-workflow
- 问题反馈：https://github.com/your-org/ai-workflow/issues
- 邮箱：team@aiworkflow.com

---

**注意**：这是一个MVP版本的实现，主要展示了系统架构和核心功能。在生产环境中使用前，建议进行充分的测试和优化。