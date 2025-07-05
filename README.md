# AI工作流编排系统 (AI-WorkFlow)

## 项目介绍

AI工作流编排系统是一个基于MCP协议的企业级AI编排平台，旨在为企业提供标准化、可扩展的AI能力整合和流程自动化解决方案。

### 核心特性

- 🎯 **MCP协议原生支持** - 基于Model Context Protocol标准化AI交互
- ⚙️ **自研智能流程引擎** - 可视化BPMN流程设计器，支持多Agent协作
- 🚀 **企业级架构设计** - 微服务架构，高可用、可扩展
- 🔐 **完善的安全机制** - OAuth2.0认证，RBAC权限管理
- 📊 **全面的监控运维** - Prometheus指标，分布式链路追踪

## 技术栈

### 后端技术
- **应用框架**: Spring Boot 3.5.3 + Java 17
- **数据库**: MySQL 8.0+ (主数据库) + Redis 7.0+ (缓存)
- **连接池**: Alibaba Druid
- **ORM框架**: Spring Data JPA + Hibernate
- **缓存**: Spring Cache + Redis
- **监控**: Spring Boot Actuator + Micrometer + Prometheus
- **构建工具**: Maven 3.9+

### 核心依赖
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Cache
- Spring Boot Starter WebSocket
- Redisson (Redis客户端)
- HuTool (工具类库)
- Google Guava

### 开发工具
- Spring Boot DevTools (热重载)
- TestContainers (集成测试)
- JUnit 5 (单元测试)

## 环境要求

### 基础环境
- **JDK**: OpenJDK 17+
- **Maven**: 3.9+
- **MySQL**: 8.0+
- **Redis**: 7.0+

### 开发环境
- **IDE**: IntelliJ IDEA 2024.1+ (推荐)
- **数据库工具**: DBeaver / MySQL Workbench
- **API测试**: Postman / Insomnia

## 快速启动

### 1. 环境准备

```bash
# 1. 确保Java 17已安装
java -version

# 2. 确保Maven已安装
mvn -version

# 3. 启动MySQL (使用Docker)
docker run -d \
  --name mysql-aiworkflow \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123456 \
  -e MYSQL_DATABASE=ai_workflow_dev \
  mysql:8.0

# 4. 启动Redis (使用Docker)
docker run -d \
  --name redis-aiworkflow \
  -p 6379:6379 \
  redis:7.0
```

### 2. 项目启动

```bash
# 1. 克隆项目
git clone <repository-url>
cd ai-work-flow

# 2. 编译项目
mvn clean compile

# 3. 启动应用 (开发模式)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或者使用IDE直接运行 AiWorkflowApplication.main()
```

### 3. 验证启动

启动成功后，访问以下地址验证：

- **健康检查**: http://localhost:8080/api/v1/health
- **系统信息**: http://localhost:8080/api/v1/health/info
- **监控端点**: http://localhost:8080/api/v1/actuator
- **Druid监控**: http://localhost:8080/api/v1/druid (admin/admin123)

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/aiworkflow/
│   │       ├── AiWorkflowApplication.java     # 主启动类
│   │       ├── config/                        # 配置类
│   │       │   └── WebMvcConfig.java          # Web MVC配置
│   │       ├── controller/                    # 控制器层
│   │       │   └── HealthController.java      # 健康检查
│   │       ├── service/                       # 服务层
│   │       ├── repository/                    # 数据访问层
│   │       ├── entity/                        # 实体类
│   │       ├── dto/                          # 数据传输对象
│   │       ├── mcp/                          # MCP协议引擎
│   │       ├── workflow/                     # 流程引擎
│   │       └── agent/                        # Agent管理
│   └── resources/
│       ├── application.yml                   # 应用配置
│       └── static/                          # 静态资源
└── test/
    └── java/
        └── com/aiworkflow/
            └── AiWorkflowApplicationTests.java # 基础测试
```

## 配置说明

### 数据库配置

项目支持多环境配置，默认使用开发环境：

- **开发环境**: `ai_workflow_dev`
- **测试环境**: `ai_workflow_test`
- **生产环境**: `ai_workflow_prod`

### 核心配置项

```yaml
# MCP协议配置
ai-workflow:
  mcp:
    connection-timeout: 30000      # 连接超时
    read-timeout: 60000           # 读取超时
    max-connections: 100          # 最大连接数
    
  # 流程引擎配置
  workflow:
    max-concurrent-processes: 100  # 最大并发流程数
    task-timeout: 300000          # 任务超时时间
    
  # Agent管理配置
  agent:
    heartbeat-interval: 30000     # 心跳间隔
    health-check-timeout: 10000   # 健康检查超时
```

## 开发指南

### 编码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理和日志记录
- API接口遵循RESTful设计规范

### 提交规范
- 使用语义化提交信息
- 代码提交前进行单元测试
- 遵循Git Flow工作流

### 测试规范
- 单元测试覆盖率 > 80%
- 集成测试使用TestContainers
- API测试使用MockMvc

## 部署指南

### Docker部署

```bash
# 1. 构建镜像
mvn clean package -DskipTests
docker build -t ai-workflow:1.0.0 .

# 2. 运行容器
docker run -d \
  --name ai-workflow \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  ai-workflow:1.0.0
```

### Kubernetes部署

```bash
# 部署到Kubernetes集群
kubectl apply -f k8s/
```

## 监控运维

### 监控指标
- 应用健康状态
- JVM内存和GC
- 数据库连接池
- Redis缓存性能
- API响应时间

### 日志管理
- 结构化日志输出
- 分级日志记录
- 日志文件轮转
- 集中日志收集

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系我们

- 项目主页: [GitHub Repository]
- 问题反馈: [Issues]
- 技术文档: [Wiki]
- 邮箱: ai-workflow@example.com 