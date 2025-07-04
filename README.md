# AI工作流编排系统 - 重新设计版本

基于现代微服务架构、事件驱动、响应式编程模式重新设计的AI工作流编排系统。

## 🚀 核心特性

### 架构优势
- **领域驱动设计(DDD)** - 清晰的业务边界和更好的可维护性
- **事件驱动架构(EDA)** - 松耦合的组件通信和更好的扩展性
- **响应式编程** - 高并发处理能力和更好的资源利用率
- **微服务架构** - 独立部署和更好的容错能力
- **云原生设计** - 更好的可观测性和运维友好

### 技术栈
- **后端**: Spring Boot 3.5+ with WebFlux, Java 17
- **数据库**: MySQL 8.0 + R2DBC (响应式)
- **缓存**: Redis Cluster
- **消息队列**: Apache Kafka
- **监控**: Prometheus + Grafana + Jaeger
- **容器化**: Docker + Kubernetes

## 📁 项目结构

```
ai-work-flow/
├── ai-workflow-common/           # 公共模块 - 领域模型和工具类
├── ai-workflow-process-service/  # 流程管理服务
├── ai-workflow-execution-service/# 任务执行服务
├── ai-workflow-agent-service/    # Agent管理服务
├── ai-workflow-mcp-service/      # MCP代理服务
├── ai-workflow-gateway/          # API网关
├── doc/                          # 文档
├── docker-compose.dev.yml        # 开发环境Docker配置
└── README.md
```

## 🛠️ 快速开始

### 前置条件
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### 1. 启动基础设施

```bash
# 启动MySQL、Redis、Kafka等基础设施
docker-compose -f docker-compose.dev.yml up -d

# 等待服务启动完成
docker-compose -f docker-compose.dev.yml ps
```

### 2. 创建数据库

```bash
# 连接MySQL并创建数据库
docker exec -it ai-workflow-mysql mysql -uroot -proot123 -e "CREATE DATABASE IF NOT EXISTS ai_workflow_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 3. 运行数据库迁移

```bash
# 进入流程服务目录
cd ai-workflow-process-service

# 运行Flyway迁移
mvn flyway:migrate
```

### 4. 启动流程服务

```bash
# 编译项目
mvn clean compile

# 启动流程服务
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. 验证服务

```bash
# 检查服务健康状态
curl http://localhost:8080/actuator/health

# 查看API文档
open http://localhost:8080/swagger-ui.html
```

## 🔧 服务监控

### 监控工具访问地址

| 服务 | 地址 | 用户名/密码 | 说明 |
|------|------|-------------|------|
| Grafana | http://localhost:3000 | admin/admin123 | 监控仪表板 |
| Prometheus | http://localhost:9090 | - | 指标收集 |
| Kafka UI | http://localhost:8081 | - | Kafka管理界面 |
| Jaeger | http://localhost:16686 | - | 链路追踪 |

### 关键指标

- **流程实例数量**: `process_instances_total`
- **任务执行速率**: `task_execution_rate`
- **Agent响应时间**: `agent_response_time_seconds`
- **系统错误率**: `http_requests_errors_rate`

## 🏗️ 开发指南

### 代码规范

#### 包结构规范
```
com.aiworkflow
├── domain/                    # 领域层
│   ├── process/              # 流程域
│   ├── execution/            # 执行域
│   ├── agent/                # Agent域
│   └── shared/               # 共享值对象
├── application/              # 应用层
│   ├── service/              # 应用服务
│   ├── handler/              # 事件处理器
│   └── dto/                  # 数据传输对象
├── infrastructure/           # 基础设施层
│   ├── repository/           # 仓储实现
│   ├── mcp/                  # MCP客户端
│   ├── event/                # 事件发布器
│   └── config/               # 配置类
└── interfaces/               # 接口层
    ├── rest/                 # REST控制器
    ├── graphql/              # GraphQL解析器
    └── websocket/            # WebSocket处理器
```

#### 编码规范
- 使用Java 17特性 (records, sealed classes, pattern matching)
- 遵循DDD原则：聚合根、值对象、领域事件
- 响应式编程：使用Mono/Flux，避免阻塞操作
- 构造函数注入，避免字段注入
- 完善的单元测试和集成测试

### API设计

#### REST API规范
```bash
# 流程管理
POST   /api/v1/processes/definitions     # 创建流程定义
GET    /api/v1/processes/definitions     # 获取流程列表
GET    /api/v1/processes/definitions/{id} # 获取流程详情
PUT    /api/v1/processes/definitions/{id} # 更新流程定义
DELETE /api/v1/processes/definitions/{id} # 删除流程

# 流程实例
POST   /api/v1/processes/instances       # 启动流程实例
GET    /api/v1/processes/instances       # 获取实例列表
GET    /api/v1/processes/instances/{id}  # 获取实例详情
POST   /api/v1/processes/instances/{id}/terminate # 终止实例
```

#### 统一响应格式
```json
{
  "code": 200,
  "message": "success",
  "data": {...},
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### 事件驱动开发

#### 定义领域事件
```java
public class ProcessInstanceStartedEvent extends DomainEvent {
    private final ProcessInstance processInstance;
    
    public ProcessInstanceStartedEvent(ProcessInstance processInstance) {
        super("ProcessInstanceStarted", processInstance.getId().getValue(), 
              processInstance.getVersion());
        this.processInstance = processInstance;
    }
}
```

#### 处理领域事件
```java
@Component
public class ProcessEventHandler {
    
    @EventHandler
    @Async("eventExecutor")
    public void handle(ProcessInstanceStartedEvent event) {
        // 处理流程实例启动事件
        log.info("Process instance started: {}", event.getProcessInstance().getId());
        
        // 调度初始任务
        taskScheduler.scheduleInitialTasks(event.getProcessInstance());
    }
}
```

## 🧪 测试

### 运行测试
```bash
# 单元测试
mvn test

# 集成测试
mvn test -Pintegration-test

# 测试覆盖率
mvn test jacoco:report
```

### 测试策略
- **单元测试**: 测试领域逻辑和业务规则
- **集成测试**: 测试服务间交互和数据库操作
- **契约测试**: 使用Spring Cloud Contract
- **端到端测试**: 使用TestContainers

## 🚀 部署

### 本地开发
```bash
# 使用Docker Compose
docker-compose -f docker-compose.dev.yml up -d
```

### Kubernetes部署
```bash
# 应用Kubernetes配置
kubectl apply -f k8s/

# 检查部署状态
kubectl get pods -n ai-workflow

# 查看服务日志
kubectl logs -f deployment/process-service -n ai-workflow
```

### 环境变量配置

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DATABASE_HOST | 数据库主机 | localhost |
| DATABASE_PORT | 数据库端口 | 3306 |
| DATABASE_NAME | 数据库名称 | ai_workflow |
| DATABASE_USERNAME | 数据库用户名 | root |
| DATABASE_PASSWORD | 数据库密码 | root123 |
| REDIS_HOST | Redis主机 | localhost |
| REDIS_PORT | Redis端口 | 6379 |
| KAFKA_SERVERS | Kafka服务器 | localhost:9092 |

## 📊 性能指标

### 预期性能
- **并发处理**: 1000+ 并发流程实例
- **响应时间**: API响应时间 < 100ms (P95)
- **吞吐量**: 10000+ 任务/分钟
- **可用性**: 99.9% SLA

### 性能测试
```bash
# 使用JMeter进行压力测试
jmeter -n -t performance-test.jmx -l results.jtl

# 生成性能报告
jmeter -g results.jtl -o performance-report/
```

## 🔒 安全

### 安全特性
- **认证授权**: Spring Security + JWT
- **数据加密**: 敏感数据AES加密
- **HTTPS**: TLS 1.3加密传输
- **API限流**: Redis + 令牌桶算法
- **审计日志**: 完整的操作审计

### 安全配置
```yaml
security:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 3600
  cors:
    allowed-origins: ${ALLOWED_ORIGINS:http://localhost:3000}
```

## 🤝 贡献指南

1. Fork项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

### 提交消息规范
```
<type>(<scope>): <subject>

<body>

<footer>
```

类型：feat, fix, docs, style, refactor, test, chore

## 📝 变更日志

### v2.0.0 (重新设计版本)
- ✨ 采用领域驱动设计(DDD)架构
- ✨ 实现事件驱动架构(EDA)
- ✨ 使用响应式编程模式
- ✨ 微服务架构拆分
- ✨ 完整的可观测性体系
- ✨ 云原生部署支持

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 🙋‍♂️ 联系我们

如有问题或建议，请提交Issue或联系项目维护者：

- 项目主页: https://github.com/ai-workflow/ai-work-flow
- 文档: https://docs.ai-workflow.com
- 邮箱: support@ai-workflow.com

---

**注意**: 这是重新设计的版本，采用了现代化的架构模式和技术栈，相比原有系统具有更好的性能、可扩展性和可维护性。