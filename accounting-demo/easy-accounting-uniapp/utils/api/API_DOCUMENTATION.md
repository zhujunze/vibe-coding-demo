# API 接口使用文档

## 📁 文件结构

```
utils/api/
├── config.js          # 基础配置
├── request.js         # HTTP 请求封装
├── index.js           # 统一导出
├── auth.js            # 认证相关 API
├── user.js            # 用户相关 API
├── transaction.js     # 账单相关 API
├── category.js        # 分类相关 API
├── chart.js           # 图表相关 API
└── budget.js          # 预算相关 API
```

## 📋 API 接口列表

### 1. 认证模块 (auth.js)

| 方法 | 功能 | 接口路径 | 请求方式 |
|------|------|----------|----------|
| sendSmsCode | 发送短信验证码 | `/api/auth/sms-code` | POST |
| register | 用户注册 | `/api/auth/register` | POST |
| login | 用户登录 | `/api/auth/login` | POST |
| getResetPasswordToken | 获取重置密码 Token | `/api/auth/reset-token` | POST |
| resetPassword | 重置密码 | `/api/auth/reset-password` | POST |
| logout | 退出登录 | `/api/auth/logout` | POST |

### 2. 用户模块 (user.js)

| 方法 | 功能 | 接口路径 | 请求方式 |
|------|------|----------|----------|
| getCurrentUser | 获取当前用户信息 | `/api/users/me` | GET |
| updateUserInfo | 更新用户信息 | `/api/users/me` | PUT |
| getUserStats | 获取用户统计数据 | `/api/users/stats` | GET |

### 3. 分类模块 (category.js)

| 方法 | 功能 | 接口路径 | 请求方式 |
|------|------|----------|----------|
| getCategories | 获取所有分类 | `/api/categories` | GET |
| createCustomCategory | 创建自定义分类 | `/api/categories/custom` | POST |
| deleteCategory | 删除自定义分类 | `/api/categories/{id}` | DELETE |

### 4. 账单模块 (transaction.js)

| 方法 | 功能 | 接口路径 | 请求方式 |
|------|------|----------|----------|
| createTransaction | 创建账单 | `/api/transactions` | POST |
| getTransactions | 分页查询账单 | `/api/transactions` | GET |
| getTransactionDetail | 获取账单详情 | `/api/transactions/{id}` | GET |
| updateTransaction | 更新账单 | `/api/transactions` | PUT |
| deleteTransaction | 删除账单 | `/api/transactions/{id}` | DELETE |
| getMonthlyStats | 获取月度收支统计 | `/api/transactions/stats/month` | GET |

### 5. 图表模块 (chart.js)

| 方法 | 功能 | 接口路径 | 请求方式 |
|------|------|----------|----------|
| getIncomeExpenseTrend | 获取收支趋势 | `/api/charts/trend` | GET |
| getCategoryDistribution | 获取分类占比 | `/api/charts/distribution` | GET |

### 6. 预算模块 (budget.js)

| 方法 | 功能 | 接口路径 | 请求方式 |
|------|------|----------|----------|
| getBudgetStatus | 获取预算状态 | `/api/budgets/status` | GET |
| setBudget | 设置预算 | `/api/budgets` | POST |

## 💡 使用示例

### 导入方式

```javascript
// 方式 1: 导入特定模块
import { authApi, transactionApi } from '@/utils/api/index.js'

// 方式 2: 导入全部
import api from '@/utils/api/index.js'
```

### 认证模块示例

```javascript
import { authApi } from '@/utils/api/index.js'

// 发送短信验证码
const smsRes = await authApi.sendSmsCode({
  phone: '13800138000',
  scene: 'login'
})

// 用户注册
const registerRes = await authApi.register({
  phone: '13800138000',
  smsCode: '123456',
  password: 'Password123',
  username: '张三'
})

// 用户登录
const loginRes = await authApi.login({
  phone: '13800138000',
  password: 'Password123'
})

// 重置密码
const tokenRes = await authApi.getResetPasswordToken({
  phone: '13800138000',
  smsCode: '123456'
})

const resetRes = await authApi.resetPassword({
  token: tokenRes.data,
  newPassword: 'NewPassword123'
})
```

### 用户模块示例

```javascript
import { userApi } from '@/utils/api/index.js'

// 获取当前用户信息
const userInfo = await userApi.getCurrentUser()

// 更新用户信息
const updateRes = await userApi.updateUserInfo({
  username: '新用户名',
  avatar: 'https://example.com/avatar.jpg',
  gender: 'male'
})

// 获取用户统计数据
const stats = await userApi.getUserStats()
```

### 分类模块示例

```javascript
import { categoryApi } from '@/utils/api/index.js'

// 获取所有分类
const categories = await categoryApi.getCategories()

// 获取支出分类
const expenseCategories = await categoryApi.getCategories({
  type: 'expense'
})

// 创建自定义分类
const newCategory = await categoryApi.createCustomCategory({
  name: '游戏',
  type: 'expense',
  icon: 'game',
  color: '#000000'
})

// 删除自定义分类
await categoryApi.deleteCategory(10)
```

### 账单模块示例

```javascript
import { transactionApi } from '@/utils/api/index.js'

// 创建账单
const createRes = await transactionApi.createTransaction({
  categoryId: 1,
  amount: 100,
  type: 'expense',
  date: '2024-01-01',
  note: '早餐'
})

// 分页查询账单
const transactions = await transactionApi.getTransactions({
  page: 1,
  size: 10,
  year: 2024,
  month: 1
})

// 获取账单详情
const detail = await transactionApi.getTransactionDetail(1001)

// 更新账单
const updateRes = await transactionApi.updateTransaction({
  id: 1001,
  amount: 150,
  note: '午餐'
})

// 删除账单
await transactionApi.deleteTransaction(1001)

// 获取月度收支统计
const monthlyStats = await transactionApi.getMonthlyStats({
  year: 2024,
  month: 1
})
```

### 图表模块示例

```javascript
import { chartApi } from '@/utils/api/index.js'

// 获取收支趋势
const trend = await chartApi.getIncomeExpenseTrend({
  year: 2024,
  month: 1,
  type: 'daily'
})

// 获取分类占比
const distribution = await chartApi.getCategoryDistribution({
  year: 2024,
  month: 1,
  type: 'expense'
})
```

### 预算模块示例

```javascript
import { budgetApi } from '@/utils/api/index.js'

// 获取预算状态
const budgetStatus = await budgetApi.getBudgetStatus({
  year: 2024,
  month: 1
})

// 设置预算
const setRes = await budgetApi.setBudget({
  categoryId: 1,
  month: '2024-01',
  amount: 2000,
  alertThreshold: 0.8
})
```

## 🔧 配置说明

### 基础配置 (config.js)

```javascript
const config = {
  baseURL: 'http://192.168.1.28:8080',  // 基准 URL
  timeout: 30000,                        // 超时时间 (毫秒)
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  },
  authPaths: ['/api'],                   // 需要 token 的接口路径
  tokenKey: 'Authorization',             // Token 存储 key
  tokenPrefix: 'Bearer ',                // Token 前缀
  enableLog: true                        // 是否开启请求日志
}
```

## ⚠️ 注意事项

1. **Token 管理**: 所有 `/api` 开头的接口会自动添加 token，请确保用户已登录
2. **错误处理**: 所有接口已统一处理错误，会自动显示错误提示
3. **401 处理**: 当 token 过期时，会自动清除 token 并跳转登录页
4. **请求日志**: 开发环境下会自动打印请求日志，方便调试

## 📊 响应数据结构

所有接口返回的数据格式统一为：

```javascript
{
  code: 200,           // 状态码
  message: 'success',  // 提示信息
  data: {},            // 数据
  trace_id: '...'      // 追踪 ID
}
```

## 🎯 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，token 无效或过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
