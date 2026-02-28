# 前端开发综合指南 (agent.md)

> 本文档为 **极简记账 App (Easy Accounting)** 前端开发的唯一事实来源，基于 [PRD](../ACCOUNTING_PRD.md) 和 [技术设计文档](../TECH_DESIGN.md) 生成。

## 1. 项目背景与目标 (Project Background & Goals)

### 1.1 核心目标
打造一款 **极简、温馨、无复杂会员体系** 的记账 App，核心价值在于降低用户记账门槛，实现 "3秒完成记账"。

### 1.2 用户故事 (User Stories)
- **作为用户**，我希望在 3 秒内完成一笔收支记录，以便养成记账习惯。
- **作为用户**，我希望一眼看清本月收支总览，了解财务状况。
- **作为用户**，我希望设置分类预算并在超支时收到提醒，以控制开销。
- **作为用户**，我希望能离线记账，网络恢复后自动同步数据。

### 1.3 核心功能 (MVP)
1.  **5-Tab 导航架构**：账单 (首页)、图表、记账、预算、我的。
2.  **快速记账**：大键盘设计、预加载分类、默认当天日期。
3.  **图表分析**：趋势图、饼图、排行榜。
4.  **预算管理**：月度分类预算设置与预警。
5.  **多端同步**：支持手机号登录与数据云端同步。

### 1.4 非功能需求
-   **性能**：首屏加载 ≤ 2秒，记账操作响应 ≤ 300ms。
-   **兼容性**：iOS (首发)、Android、微信小程序。
-   **视觉**：温馨可爱风格，主色奶白+柔粉/柔蓝。

---

## 2. 技术约束与前置条件 (Tech Constraints)

### 2.1 技术栈
-   **核心框架**: UniApp (Vue 3 Composition API)
-   **构建工具**: Vite / HBuilderX
-   **状态管理**: Pinia (持久化插件: pinia-plugin-persistedstate)
-   **UI 组件库**: UniUI + 自定义组件 (uCharts 用于图表)
-   **样式预处理**: SCSS (遵循 BEM 命名规范)
-   **图标库**: iconfont / 静态 SVG

### 2.2 开发规范 (Code Standards)
-   **格式化**: Prettier (强制)
    ```javascript
    // .prettierrc
    {
      "semi": true,
      "singleQuote": true,
      "tabWidth": 2,
      "printWidth": 120,
      "trailingComma": "es5",
      "arrowParens": "avoid",
      "endOfLine": "lf"
    }
    ```
-   **命名规范**:
    -   **目录/文件**: CamelCase (e.g., `pages/tabBar/bill/index.vue`, `utils/api/request.js`)
    -   **组件文件**: PascalCase (e.g., `components/Bill/BillItem.vue`, `components/Common/BaseTabBar.vue`)
    -   **变量/函数**: camelCase (e.g., `handleSubmit`, `isLoading`)
    -   **常量**: UPPER_CASE_SNAKE_CASE (e.g., `MAX_RETRY_COUNT`)
    -   **类名**: kebab-case (e.g., `.bill-item__title`)
-   **代码量限制**: 单文件 < 500行 (推荐300)，单函数 < 100行 (推荐50)。

### 2.3 接口规范
-   **通信协议**: HTTPS + JSON
-   **认证方式**: JWT (Header: `Authorization: Bearer <token>`)
-   **响应结构**:
    ```json
    {
      "code": 200, // 200: 成功, 401: 未授权, 500: 服务器错误
      "message": "success",
      "data": { ... }
    }
    ```

---

## 3. 组件与页面级任务拆解 (Component & Tasks)

### 3.1 页面开发 (Pages)
| 路径 | 描述 | 关键功能点 | 验收标准 |
| :--- | :--- | :--- | :--- |
| `pages/tabBar/bill/index.vue` | **账单首页** | 收支总览卡片、每日明细列表、下拉刷新、上拉加载 | 显示本月收支；列表按日分组；加载无卡顿 |
| `pages/tabBar/bill/Detail.vue` | **账单详情** | 详情展示、编辑/删除入口 | 数据准确；删除有二次确认；编辑跳转正确 |
| `pages/tabBar/chart/index.vue` | **图表首页** | 趋势图(折线)、占比图(饼图)、排行榜 | 切换周期(年/月/周)图表联动；无数据展示空态 |
| `pages/tabBar/bookkeeping/index.vue` | **记账页** | 类型切换、分类网格、数字键盘、备注 | 打开速度<1s；键盘响应灵敏；保存后自动返回 |
| `pages/tabBar/budget/index.vue` | **预算首页** | 预算列表、进度条、设置入口 | 超支红色预警；进度条动画流畅 |
| `pages/tabBar/me/index.vue` | **我的首页** | 用户信息、统计数据、功能入口 | 未登录显示登录引导；统计数据实时更新 |
| `pages/auth/Login.vue` | **登录页** | 手机号输入、验证码获取、协议勾选 | 验证码倒计时；手机号格式校验；登录成功跳转 |

### 3.2 公共组件开发 (Components)
| 组件名 | 路径 | 功能描述 | Props |
| :--- | :--- | :--- | :--- |
| `BaseTabBar` | `components/Common/BaseTabBar.vue` | 自定义底部导航，支持红点 | `currentPath`, `badge` |
| `BasePageHeader` | `components/Layout/BasePageHeader.vue` | 通用页头，支持返回和标题 | `title`, `showBack`, `bgColor` |
| `BillItem` | `components/Bill/BillItem.vue` | 单条账单列表项 | `transaction` (Object) |
| `NumberKeyboard` | `components/Bookkeeping/NumberKeyboard.vue` | 记账专用数字键盘 | `value`, `@input`, `@delete`, `@confirm` |
| `TrendChart` | `components/Chart/TrendChart.vue` | 趋势折线图 (uCharts封装) | `data`, `type` (income/expense) |
| `BudgetProgress` | `components/Budget/BudgetProgress.vue` | 预算进度条组件 | `current`, `total`, `color` |

### 3.3 状态管理 (Pinia Modules)
-   **user**: 用户信息、Token、登录状态。
-   **transactions**: 账单列表缓存、当前选中账单。
-   **budget**: 预算设置数据、预警状态。
-   **global**: 全局配置、网络状态、Loading 状态。

---

## 4. 接口对接清单 (API Integration)

| 模块 | 接口路径 | 方法 | 功能描述 | Mock 规则 | 错误处理 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Auth** | `/api/auth/sms-login` | POST | 手机号验证码登录 | 返回 JWT Token | 提示"验证码错误" |
| **Auth** | `/api/auth/send-sms` | POST | 发送验证码 | 模拟发送成功 | 提示"发送频繁" |
| **Bill** | `/api/transactions` | POST | 创建交易记录 | 返回新记录 ID | 提示"保存失败" |
| **Bill** | `/api/transactions/monthly` | GET | 获取月度列表 | 返回分组列表 | 显示空状态页 |
| **Chart** | `/api/charts/trend` | GET | 获取趋势数据 | 返回日期-金额数组 | 图表显示"暂无数据" |
| **Budget** | `/api/budgets/category` | POST | 设置分类预算 | 返回设置结果 | 提示"设置失败" |
| **Budget** | `/api/budgets/status` | GET | 获取预算状态 | 返回预算列表及进度 | 降级显示 |

---

## 5. 测试与质量门禁 (Test & Quality)

-   **单元测试**: 核心工具函数 (`utils/`) 覆盖率 ≥ 80%。
-   **兼容性测试**:
    -   iOS: iPhone X 及以上 (Safari/微信)
    -   Android: 主流机型 (Chrome/微信)
-   **性能标准**:
    -   Lighthouse Performance ≥ 90
    -   FCP (First Contentful Paint) ≤ 1.5s
-   **代码审查 (Code Review)**:
    -   必须通过 ESLint & Prettier 检查。
    -   无 `console.log` (生产环境)。
    -   复杂逻辑需有注释。

---

## 6. 交付物与里程碑 (Deliverables)

### 阶段一：MVP 发布 (V1.0)
-   **交付物**:
    -   UniApp 前端源码包
    -   H5 静态资源包 (`dist/build/h5`)
    -   API 接口文档 (Swagger 导出)
    -   用户操作手册 (Markdown)
-   **完成标准**:
    -   所有 P0 功能 (注册登录、记账、账单、预算) 开发完成。
    -   无 P0/P1 级 Bug。
    -   产品验收通过。

---

## 7. 风险与依赖 (Risks)

| 风险点 | 影响 | 应对方案 | Owner |
| :--- | :--- | :--- | :--- |
| **UniApp 兼容性** | 某些 CSS 属性在小程序/App 表现不一致 | 开发时多端实时预览，使用条件编译 (`#ifdef`) | 前端负责人 |
| **图表性能** | 数据量大时图表渲染卡顿 | 服务端预聚合数据，前端仅渲染展示；使用 uCharts 原生渲染模式 | 全栈开发 |
| **离线同步** | 数据冲突导致丢失 | 采用 "Last Write Wins" 策略或保留冲突副本；本地 SQLite 缓存 | 后端负责人 |
| **接口延期** | 阻碍前端开发 | 前端优先使用 Mock 数据开发 (Mock.js) | 前端负责人 |

---

## 8. 附录 (Appendix)

### 8.1 环境变量模板 (.env)
```ini
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_TITLE=极简记账
VITE_ENABLE_MOCK=true
```

### 8.2 分支管理规范
-   `main`: 生产环境分支 (Protected)
-   `develop`: 开发主分支
-   `feature/<name>`: 功能分支 (e.g., `feature/budget-module`)
-   `fix/<issue>`: 修复分支 (e.g., `fix/login-error`)

### 8.3 Commit Message 格式
`type(scope): subject`
-   Example: `feat(bill): add transaction list component`
-   Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`
