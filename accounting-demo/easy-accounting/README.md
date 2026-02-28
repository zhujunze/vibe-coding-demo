# Easy Accounting Backend Service

## 模块列表
- **Authentication**: 用户登录注册 (AuthController)
- **Transaction**: 账单记账管理 (TransactionController)
- **Category**: 分类管理 (CategoryController)
- **Budget**: 预算管理 (BudgetController)
- **Chart**: 图表分析 (ChartController)
- **User**: 个人中心 (UserController)

## 技术栈版本
- **Java**: 21
- **Spring Boot**: 3.2.4
- **MyBatis Plus**: 3.5.5
- **MySQL**: 8.0
- **Redis**: 6.x

## 启动步骤
1.  初始化数据库: 执行 `src/main/resources/schema.sql`
2.  配置 `src/main/resources/application-dev.yml` 中的数据库和 Redis 连接信息
3.  运行 `init.ps1` 进行构建和测试
4.  运行 `EasyAccountingApplication` 启动服务

## 端口
- 服务端口: 8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## 异常处理规范

本项目采用统一的异常处理机制，所有业务异常必须抛出 `BusinessException`，严禁直接返回 Result 对象。

### 1. 错误码规范
错误码采用三段式设计（A-BB-CC）：
- **A**: 系统标识 (1: 记账系统)
- **BB**: 模块标识 (00: 通用, 01: 用户, 02: 账单, 03: 分类, 04: 预算, 05: 图表)
- **CC**: 错误序号 (01-99)

详细错误码请查阅 `com.easyaccounting.common.exception.ErrorCode` 枚举。

### 2. 异常抛出示例
```java
// 推荐写法
throw new BusinessException(ErrorCode.USER_NOT_EXIST);

// 自定义消息（仅限特殊场景）
throw new BusinessException(ErrorCode.PARAM_ERROR.getCode(), "金额不能为负数");
```

### 3. 日志规范
- **ERROR**: 仅记录系统级异常、数据库连接失败等需人工介入的错误。
- **WARN**: 记录业务异常（如参数校验失败、用户不存在），可自愈或预期的错误。
- **INFO**: 记录关键业务流程节点（如登录成功、记账完成）。
- **TraceId**: 所有日志已自动注入 TraceId，用于全链路追踪。

### 4. 前端对接
前端需统一拦截响应，判断 `code != 200` 时展示 `message` 提示用户。对于 `code == 401`，应跳转至登录页。
