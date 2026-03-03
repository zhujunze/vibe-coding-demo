# API 模块使用指南

## ✅ 已完成的功能模块

根据接口文档，已创建以下 6 个功能模块的 API 接口：

### 📁 文件列表

| 文件名 | 功能模块 | 接口数量 | 说明 |
|--------|---------|---------|------|
| `auth.js` | 认证模块 | 6 个 | 短信验证码、注册、登录、重置密码 |
| `user.js` | 用户模块 | 3 个 | 用户信息、统计数据 |
| `category.js` | 分类模块 | 3 个 | 获取分类、创建/删除自定义分类 |
| `transaction.js` | 账单模块 | 6 个 | 账单 CRUD、月度统计 |
| `chart.js` | 图表模块 | 2 个 | 收支趋势、分类占比 |
| `budget.js` | 预算模块 | 2 个 | 预算状态、设置预算 |

## 🚀 快速开始

### 1. 导入 API 模块

```javascript
// 方式 1: 按需导入
import { authApi, transactionApi, userApi } from '@/utils/api/index.js'

// 方式 2: 全部导入
import api from '@/utils/api/index.js'
```

### 2. 使用示例

#### 认证流程

```javascript
import { authApi } from '@/utils/api/index.js'

// 1. 发送短信验证码
await authApi.sendSmsCode({
  phone: '13800138000',
  scene: 'login'
})

// 2. 用户登录
const res = await authApi.login({
  phone: '13800138000',
  password: '123456'
})

// 3. 退出登录
await authApi.logout()
```

#### 获取用户信息

```javascript
import { userApi } from '@/utils/api/index.js'

// 获取当前用户信息
const userInfo = await userApi.getCurrentUser()

// 更新用户信息
await userApi.updateUserInfo({
  username: '新用户名',
  avatar: 'https://example.com/avatar.jpg'
})

// 获取用户统计数据
const stats = await userApi.getUserStats()
```

#### 分类管理

```javascript
import { categoryApi } from '@/utils/api/index.js'

// 获取所有分类
const categories = await categoryApi.getCategories()

// 获取支出分类
const expenseCats = await categoryApi.getCategories({ type: 'expense' })

// 创建自定义分类
await categoryApi.createCustomCategory({
  name: '游戏',
  type: 'expense',
  icon: 'game',
  color: '#000000'
})

// 删除自定义分类
await categoryApi.deleteCategory(10)
```

#### 账单管理

```javascript
import { transactionApi } from '@/utils/api/index.js'

// 创建账单
await transactionApi.createTransaction({
  categoryId: 1,
  amount: 100,
  type: 'expense',
  date: '2024-01-01',
  note: '早餐'
})

// 分页查询账单
const bills = await transactionApi.getTransactions({
  page: 1,
  size: 10,
  year: 2024,
  month: 1
})

// 获取账单详情
const detail = await transactionApi.getTransactionDetail(1001)

// 更新账单
await transactionApi.updateTransaction({
  id: 1001,
  amount: 150,
  note: '午餐'
})

// 删除账单
await transactionApi.deleteTransaction(1001)

// 获取月度收支统计
const stats = await transactionApi.getMonthlyStats({
  year: 2024,
  month: 1
})
```

#### 图表统计

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

#### 预算管理

```javascript
import { budgetApi } from '@/utils/api/index.js'

// 获取预算状态
const status = await budgetApi.getBudgetStatus({
  year: 2024,
  month: 1
})

// 设置预算
await budgetApi.setBudget({
  categoryId: 1,
  month: '2024-01',
  amount: 2000,
  alertThreshold: 0.8
})
```

## 📊 接口详情

### 1. 认证模块 (auth.js)

| 方法 | 接口路径 | 请求方式 | 参数 |
|------|----------|----------|------|
| `sendSmsCode` | `/api/auth/sms-code` | POST | phone, scene |
| `register` | `/api/auth/register` | POST | phone, smsCode, password, username |
| `login` | `/api/auth/login` | POST | phone, password |
| `getResetPasswordToken` | `/api/auth/reset-token` | POST | phone, smsCode |
| `resetPassword` | `/api/auth/reset-password` | POST | token, newPassword |
| `logout` | `/api/auth/logout` | POST | - |

### 2. 用户模块 (user.js)

| 方法 | 接口路径 | 请求方式 | 参数 |
|------|----------|----------|------|
| `getCurrentUser` | `/api/users/me` | GET | - |
| `updateUserInfo` | `/api/users/me` | PUT | username, avatar, gender |
| `getUserStats` | `/api/users/stats` | GET | - |

### 3. 分类模块 (category.js)

| 方法 | 接口路径 | 请求方式 | 参数 |
|------|----------|----------|------|
| `getCategories` | `/api/categories` | GET | type (可选) |
| `createCustomCategory` | `/api/categories/custom` | POST | name, type, icon, color |
| `deleteCategory` | `/api/categories/{id}` | DELETE | id (路径参数) |

### 4. 账单模块 (transaction.js)

| 方法 | 接口路径 | 请求方式 | 参数 |
|------|----------|----------|------|
| `createTransaction` | `/api/transactions` | POST | categoryId, amount, type, date, note |
| `getTransactions` | `/api/transactions` | GET | page, size, year, month, type, categoryId, startDate, endDate |
| `getTransactionDetail` | `/api/transactions/{id}` | GET | id (路径参数) |
| `updateTransaction` | `/api/transactions` | PUT | id, amount, categoryId, type, date, note |
| `deleteTransaction` | `/api/transactions/{id}` | DELETE | id (路径参数) |
| `getMonthlyStats` | `/api/transactions/stats/month` | GET | year, month |

### 5. 图表模块 (chart.js)

| 方法 | 接口路径 | 请求方式 | 参数 |
|------|----------|----------|------|
| `getIncomeExpenseTrend` | `/api/charts/trend` | GET | year, month, type |
| `getCategoryDistribution` | `/api/charts/distribution` | GET | year, month, type |

### 6. 预算模块 (budget.js)

| 方法 | 接口路径 | 请求方式 | 参数 |
|------|----------|----------|------|
| `getBudgetStatus` | `/api/budgets/status` | GET | year, month |
| `setBudget` | `/api/budgets` | POST | categoryId, month, amount, alertThreshold |

## ⚙️ 核心特性

### 1. 自动 Token 管理
- 所有 `/api` 开头的接口自动添加 token
- token 过期自动清除并跳转登录

### 2. 统一错误处理
- 网络错误友好提示
- 服务器错误状态码处理
- 401 自动跳转登录

### 3. 请求日志
- 开发环境自动打印请求日志
- 包含请求方法、URL、参数

### 4. 响应拦截
- 统一处理响应数据格式
- 统一错误提示

## 📖 详细文档

更详细的使用说明请查看：
- **API 接口文档**: `utils/api/API_DOCUMENTATION.md`
- **请求封装源码**: `utils/api/request.js`
- **配置文件**: `utils/api/config.js`

## 🎯 最佳实践

### 1. 统一导入
建议在页面或组件顶部统一导入需要的 API 模块：

```javascript
import { authApi, userApi, transactionApi } from '@/utils/api/index.js'
```

### 2. 错误处理
虽然 API 已统一处理错误，但如需特殊处理可添加 try-catch：

```javascript
try {
  const res = await transactionApi.createTransaction(data)
  // 成功处理
} catch (error) {
  // 自定义错误处理
}
```

### 3. 参数校验
建议在调用 API 前进行参数校验：

```javascript
if (!data.phone || !data.password) {
  uni.showToast({ title: '请填写完整信息', icon: 'none' })
  return
}
```

## 📝 注意事项

1. **基准 URL**: 当前配置为 `http://192.168.1.28:8080`，可在 `config.js` 中修改
2. **超时时间**: 默认 30 秒，可在 `config.js` 中修改
3. **Token 存储**: 使用 `uni.Storage` 存储 token
4. **请求日志**: 生产环境建议在 `config.js` 中关闭 `enableLog`
