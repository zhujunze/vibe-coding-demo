# Backend Service Agent 指令文档 (SERVICE_AGENT.md)

## 1. 目标与范围 (Goal & Scope)

### 1.1 核心目标
构建一个高可用、高安全、具备智能分析能力的后端服务代理（Backend Service Agent），支撑“极简记账 App”的核心业务。该代理需严格遵循 `ACCOUNTING_PRD.md` 的业务需求与 `TECH_DESIGN.md` 的技术架构，同时满足企业级非功能性指标。

### 1.2 职责边界
*   **负责**:
    *   提供 RESTful API 响应前端请求（UniApp）。
    *   管理核心业务数据（用户、账单、分类、预算）。
    *   执行复杂业务逻辑（预算预警、趋势分析、自然语言查询解析）。
    *   保障数据的一致性、安全性和持久化。
    *   集成第三方服务（短信验证码、对象存储）。
*   **依赖**:
    *   **Upstream**: UniApp 前端客户端。
    *   **Downstream**: MySQL 8.0 (主存储), Redis 6.x (缓存/Session), 阿里云/腾讯云短信服务。
*   **禁止行为**:
    *   直接处理前端 UI 渲染逻辑（纯后端 API）。
    *   在日志中明文记录敏感数据（如密码、Token、手机号）。
    *   执行未经授权的 DDL 操作。
    *   绕过 Service 层直接从 Controller 访问 Mapper。

---

## 2. 功能规格 (Functional Specifications)

### 2.1 核心能力

#### 2.1.1 业务交易处理 (Transaction Processing)
*   **功能**: 实现记账（收入/支出）、编辑、删除、查询。
*   **约束**:
    *   **事务完整性**: 记账与用户统计更新必须在同一事务中 (`@Transactional`)。
    *   **幂等性**: 关键写接口需支持幂等控制（基于 Request ID 或 业务主键）。
    *   **输入**: JSON (`amount`, `categoryId`, `type`, `date`, `note`)。
    *   **输出**: 交易 ID, 更新后的状态。

#### 2.1.2 智能分析与查询 (NL2SQL & Analytics)
*   **功能**:
    *   支持基于规则的统计分析（趋势、占比、排行）。
    *   **[增强] 自然语言转 SQL (NL2SQL)**: 解析用户自然语言查询（如“上个月餐饮花了多少钱？”）并转换为安全的 SQL 查询。
*   **约束**:
    *   **只读限制**: NL2SQL 生成的 SQL 必须仅限于 `SELECT` 操作，严禁 `DELETE/UPDATE`。
    *   **查询范围**: 严格限制在当前用户的 `user_id` 范围内。

#### 2.1.3 预算管理与预警
*   **功能**: 实时监控分类预算，触发预警。
*   **逻辑**: 每次新增/修改支出时，异步计算当前分类月度消耗 -> 对比预算阈值 -> 触发红点/通知。

#### 2.1.4 多租户与隔离
*   **策略**: 逻辑隔离。所有 SQL 查询必须强制带上 `WHERE user_id = ?`。
*   **实现**: 使用 MyBatis-Plus 租户插件或 Service 层切面拦截。

#### 2.1.5 审计日志
*   **范围**: 所有写操作（增删改）、敏感数据查询、登录/登出。
*   **格式**: JSON 结构化日志，包含 `trace_id`, `user_id`, `action`, `resource`, `timestamp`, `ip`, `result`。

### 2.2 接口规范
*   **格式**: RESTful API
*   **通用响应**:
    ```json
    {
      "code": 200,
      "message": "success",
      "data": { ... },
      "trace_id": "c0a80101-..."
    }
    ```
*   **异常码**:
    *   `400`: 参数错误 (Bad Request)
    *   `401`: 未认证 (Unauthorized)
    *   `403`: 权限不足 (Forbidden - 访问他人数据)
    *   `429`: 请求过多 (Too Many Requests)
    *   `500`: 内部错误 (Internal Server Error)
    *   `1001`: 业务逻辑错误 (如余额不足，虽不适用本场景但保留段位)

---

## 3. 技术规范 (Technical Specifications)

### 3.1 运行时要求
*   **语言**: **Java 21 (LTS)**。
    *   所有代码、API 及依赖库必须使用 Java 21 兼容特性。
    *   构建工具（Maven/Gradle）必须显式声明 `source` 和 `target` 版本为 `21`。
*   **框架**: **Spring Boot 3.2+** (确保完全适配 Java 21)。
*   **ORM**: MyBatis-Plus 3.5.5+ (支持 Spring Boot 3)。
*   **依赖库**: Lombok, Hutool, Knife4j (适配 SB3), JJWT。
*   **最小资源配额**: 1 CPU Core, 2GB RAM (容器化部署)。

### 3.2 接口与鉴权
*   **协议**: HTTP/1.1 (REST), 支持 Gzip 压缩。
*   **鉴权**:
    *   标准: OAuth2 (简化版) / JWT (JSON Web Token)。
    *   Header: `Authorization: Bearer <token>`。
    *   Token 有效期: Access Token 2小时, Refresh Token 7天。
*   **限流**:
    *   全局: 1000 QPS。
    *   单用户: 10 QPS (防止恶意刷接口)。
    *   工具: Redis Cell 或 Bucket4j。
*   **熔断**: 针对下游服务（如短信网关），失败率 > 50% 触发熔断，降级为默认响应。

### 3.3 数据一致性
*   **模型**: 强一致性（单库事务）。
*   **并发控制**: 乐观锁 (`version` 字段) 防止并发修改同一笔账单。
*   **回滚**: Spring `@Transactional` 自动回滚；应用层异常需手动捕获并确保资源释放。

### 3.4 代码质量与注释规范
*   **覆盖率要求**: 全量代码注释覆盖率 **≥ 90%**（按行数统计）。
*   **语言要求**: **所有注释必须使用中文编写**。
    *   包括类说明、方法说明、参数解释、返回值说明、异常说明、业务逻辑描述、TODO 事项等。
    *   专业技术术语（如 Spring Security, JWT, OAuth2 等）可保留英文原文，但注释的主体描述必须为中文。
    *   确保中文表达通顺、准确，符合中文语法习惯。
*   **注释标准**:
    *   **目的式注释**: 每个类、接口、枚举、方法、复杂业务块、算法片段、异常分支均需写出“目的式”注释。
    *   **内容要求**: 说明“为什么存在”、“要完成什么业务意图”、“输入输出含义”。
    *   **禁止**: 严禁出现无意义的翻译式注释（如 `// 打印日志`），必须让读者通过注释即可理解代码行为而无需读实现。
    *   **示例**:
        ```java
        /**
         * 校验账户余额是否足以支付当前交易金额，防止用户透支。
         * 业务规则：仅校验支出类型交易，收入类型直接通过。
         *
         * @param amount 交易金额，必须大于0
         * @param type 交易类型
         * @throws InsufficientBalanceException 当余额不足以支付时抛出
         */
        public void validateBalance(BigDecimal amount, TransactionType type) { ... }
        ```

### 3.5 API 文档与注解规范 (Swagger v3/OpenAPI)
*   **技术选型**: 使用 `springdoc-openapi-starter-webmvc-ui` (Spring Boot 3+ 标准依赖)，严禁使用过时的 springfox。
*   **规范要求**:
    1.  **全覆盖**: 所有 REST 接口 (`Controller`) 和数据传输对象 (`DTO`) 必须添加 OpenAPI 3.0 注解。
    2.  **属性完整**: 注解必须包含 `summary` (简述), `description` (详细说明), `example` (示例值), `required` (是否必填)。
    3.  **响应明确**: 必须显式定义 `@ApiResponse`，涵盖 `200` (成功), `400` (参数错误), `401` (未认证), `403` (无权限), `404` (未找到), `500` (系统错误) 等场景。
*   **Controller 示例规范**:
    ```java
    @Tag(name = "Transaction Management", description = "账单交易核心接口，提供记账、查询、删除功能")
    @RestController
    @RequestMapping("/api/transactions")
    public class TransactionController {

        @Operation(
            summary = "创建记账记录",
            description = "创建一笔新的收入或支出记录。创建成功后会触发异步的预算检查，如果超出预算将发送预警。",
            responses = {
                @ApiResponse(
                    responseCode = "200", 
                    description = "记账成功", 
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Result.class))
                ),
                @ApiResponse(
                    responseCode = "400", 
                    description = "参数校验失败（如金额小于0、分类ID不存在）",
                    content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"code\":400,\"message\":\"金额必须大于0\"}"))
                ),
                @ApiResponse(responseCode = "401", description = "用户未登录或Token过期"),
                @ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
        )
        @PostMapping
        public Result<Long> createTransaction(
            @Parameter(description = "交易创建请求体", required = true)
            @RequestBody @Valid CreateTransactionRequest request
        ) {
            // ... 业务逻辑 ...
        }
    }
    ```
*   **DTO 示例规范**:
    ```java
    @Schema(description = "创建交易请求参数")
    @Data
    public class CreateTransactionRequest {
        
        @Schema(
            description = "交易金额", 
            example = "128.50", 
            minimum = "0.01", 
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "金额不能为空")
        @Positive(message = "金额必须大于0")
        private BigDecimal amount;

        @Schema(
            description = "分类ID (对应 categories 表主键)", 
            example = "10", 
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "分类ID不能为空")
        private Long categoryId;

        @Schema(
            description = "交易类型", 
            example = "EXPENSE", 
            allowableValues = {"EXPENSE", "INCOME"},
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "交易类型不能为空")
        private TransactionType type;

        @Schema(
            description = "交易日期 (格式: yyyy-MM-dd)", 
            example = "2023-10-01", 
            pattern = "\\d{4}-\\d{2}-\\d{2}",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotNull(message = "日期不能为空")
        private LocalDate date;

        @Schema(
            description = "备注信息 (最大长度200字符)", 
            example = "周末聚餐", 
            maxLength = 200
        )
        private String note;
    }
    ```
*   **验证标准**: 交付前必须启动服务，访问 `/swagger-ui/index.html`，确保文档渲染无误，且所有接口可以进行 Try it out 测试。

---

## 4. 性能与可靠性 (Performance & Reliability)

### 4.1 性能指标
*   **Latency**: 核心接口（记账、首页查询）p99 ≤ 200 ms。
*   **Throughput**: 单实例支持 ≥ 1000 QPS。
*   **Cold Start**: 容器启动时间 ≤ 10s。

### 4.2 可靠性指标
*   **SLA**: 99.9% 可用性。
*   **Error Rate**: 接口错误率 ≤ 0.1% (排除 4xx 客户端错误)。
*   **故障转移**: K8s 多副本部署 (Replicas ≥ 2)，具备自动健康检查 (Liveness/Readiness Probe)。
*   **发布策略**: 支持滚动更新 (Rolling Update) 或 蓝绿发布。

### 4.3 可观测性
*   **Metrics**: 暴露 `/actuator/prometheus` 端点，包含 JVM、JDBC 连接池、HTTP 请求耗时等指标。
*   **Tracing**: 集成 OpenTelemetry Agent，全链路追踪 ID (`trace_id`) 透传。
*   **Logging**: Logback JSON 格式输出，通过 Filebeat/Fluentd 采集。

---

## 5. 安全与合规 (Security & Compliance)

### 5.1 配置安全 (Configuration Security)
*   **加密要求**:
    *   所有配置文件（`*.yml`, `*.properties`, `*.xml`, `*.json`）中凡是填写密钥（password, secret, token, private-key, access-key, salt, api-key 等）的字段，**必须采用加密存储**。
    *   **算法**: 行业标准对称加密算法 (**AES-256/GCM** 或更高) 或 非对称算法 (**RSA-2048+**)。
*   **密钥管理**:
    *   加密/解密密钥 **严禁硬编码** 在代码或配置文件中。
    *   密钥必须通过 **环境变量**、**KMS** (Key Management Service) 或 **Vault** 等外部安全源注入。
*   **解密机制**:
    *   提供配套的“加密工具类”用于生成密文。
    *   实现“启动解密逻辑”（如 Spring Boot `EnvironmentPostProcessor`），确保应用在运行期能自动解密并注入到 `Spring Environment` / `@Value`。
*   **CI/CD 集成**:
    *   同步更新流水线脚本，确保构建产物中不出现明文密钥。

### 5.2 数据安全
*   **敏感数据**:
    *   密码: BCrypt 加盐哈希存储。
    *   手机号: 数据库层可选择性加密 (AES-256)，API 返回时脱敏 (138****0000)。
*   **传输加密**: 强制 HTTPS (TLS 1.2+)。
*   **列级加密**: 针对高敏感字段（如 OAuth Token）使用数据库列加密。
*   **密钥轮转**: 密钥轮转周期 ≤ 90天。

### 5.3 合规性
*   **等保三级/SOC2**:
    *   记录所有管理操作日志。
    *   数据库开启审计日志。
    *   定期进行漏洞扫描。
*   **GDPR**: 支持“被遗忘权”（用户注销后物理删除或匿名化处理数据）。

### 5.4 渗透测试
*   **SLA**: 漏洞修复 SLA：Critical ≤ 24h, High ≤ 72h, Medium ≤ 7 days。
*   **防护**: 集成 WAF (Web Application Firewall) 规则，防 SQL 注入、XSS。

---

## 6. 测试与验收 (Testing & Acceptance)

### 6.1 测试策略
*   **单元测试 (Unit Test)**: JUnit 5 + Mockito。覆盖 Service 层核心逻辑。覆盖率 ≥ 80%。
*   **集成测试 (Integration Test)**: Spring Boot Test + H2/Testcontainers。覆盖 Controller -> DB 完整链路。
*   **配置安全测试**: 必须包含针对配置解密的测试用例，覆盖解密失败、密钥缺失、加密格式错误等异常场景。
*   **场景覆盖**: 正向流程、参数校验失败、数据库异常、网络超时。

### 6.2 性能测试
*   **工具**: JMeter 或 Gatling。
*   **场景**: 登录 -> 记账 -> 查看报表 混合场景压测。
*   **基准**: 在 1000 并发下，平均响应时间 < 500ms，无 OOM。

### 6.3 质量门禁
*   **静态扫描**: SonarQube 扫描，质量门禁设置为 A 级 (无 Blocker/Critical 问题)。
*   **SCA**: 依赖漏洞扫描 (如 OWASP Dependency Check)，Critical = 0。
*   **安全扫描**: 必须包含配置扫描，证明**无明文密钥**。

---

## 7. 交付物 (Deliverables)

1.  **代码与文档**:
    *   源代码仓库 (Git)。
    *   `SERVICE_AGENT.md` (本文档)。
    *   **改造后的完整源码**: 包含 **90%+** 的注释覆盖率（JaCoCo 报告验证）。
    *   API 接口文档 (Swagger/OpenAPI YAML)。
    *   **加密方案说明文档**: 包含算法选型、密钥管理流程、加解密时序图。
2.  **构建产物**:
    *   容器镜像 (Docker Image)，包含 SBOM (Software Bill of Materials) 清单。
    *   镜像签名验证。
    *   CVE 漏洞扫描报告 (Clean)。
3.  **部署配置**:
    *   Helm Chart (包含 Deployment, Service, ConfigMap, Secret, Ingress)。
    *   CI/CD 流水线配置文件 (GitLab CI `.gitlab-ci.yml` 或 GitHub Actions workflows)。
4.  **运维资料**:
    *   运维手册 (Runbook)：包含扩容步骤、回滚流程、告警规则列表、常见故障排查指南。
5.  **验证报告**:
    *   **注释覆盖率报告**: HTML 格式。
    *   **静态安全扫描报告**: 证明无明文密钥。
    *   **单元/集成测试报告**: 包含配置解密测试结果。

---

## 8. 里程碑与评审 (Milestones)

*   **T+0**: 项目启动。
*   **T+3**: **架构设计评审 (Design Review)**。完成 API 定义、DB 设计、技术选型（含加密方案）。提交初稿。
*   **T+7**: **代码评审 (Code Review) 与 QA 验收**。核心功能开发完成，注释覆盖率达标，配置加密已实现，测试通过。
*   **T+10**: **发布评审 (Release Review)**。完成灰度发布，性能压测达标，安全扫描无高危漏洞，正式上线。
