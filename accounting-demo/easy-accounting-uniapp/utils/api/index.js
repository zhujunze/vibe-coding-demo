/**
 * API 统一导出文件
 * 方便其他模块统一导入使用
 */
import config from './config.js'
import authApi from './auth.js'
import userApi from './user.js'
import transactionApi from './transaction.js'
import categoryApi from './category.js'
import chartApi from './chart.js'
import budgetApi from './budget.js'

export { default as config } from './config.js'
export * from './request.js'
export { default as authApi } from './auth.js'
export { default as userApi } from './user.js'
export { default as transactionApi } from './transaction.js'
export { default as categoryApi } from './category.js'
export { default as chartApi } from './chart.js'
export { default as budgetApi } from './budget.js'

// 默认导出
export default {
  config,
  authApi,
  userApi,
  transactionApi,
  categoryApi,
  chartApi,
  budgetApi
}
