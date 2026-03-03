/**
 * 账单相关 API 接口
 * 包含：创建账单、查询账单、更新账单、删除账单、获取统计等功能
 */

import request from './request.js'

/**
 * 创建账单
 * @param {Object} data - 请求参数
 * @param {number} data.categoryId - 分类 ID
 * @param {number} data.amount - 金额
 * @param {string} data.type - 类型 (expense/income)
 * @param {string} data.date - 日期
 * @param {string} data.note - 备注 (可选)
 * @returns {Promise}
 */
export function createTransaction(data) {
  return request.post('/api/transactions', data)
}

/**
 * 分页查询账单
 * @param {Object} params - 请求参数
 * @param {number} params.page - 当前页码 (默认 1)
 * @param {number} params.size - 每页大小 (默认 10)
 * @param {number} params.year - 年份 (可选)
 * @param {number} params.month - 月份 (可选)
 * @param {string} params.type - 交易类型 (EXPENSE/INCOME，可选)
 * @param {number} params.categoryId - 分类 ID(可选)
 * @param {string} params.startDate - 开始日期 (可选)
 * @param {string} params.endDate - 结束日期 (可选)
 * @returns {Promise}
 */
export function getTransactions(params) {
  return request.get('/api/transactions', params)
}

/**
 * 获取账单详情
 * @param {number} id - 账单 ID
 * @returns {Promise}
 */
export function getTransactionDetail(id) {
  return request.get(`/api/transactions/${id}`)
}

/**
 * 更新账单
 * @param {Object} data - 请求参数
 * @param {number} data.id - 账单 ID
 * @param {number} data.amount - 金额 (可选)
 * @param {number} data.categoryId - 分类 ID(可选)
 * @param {string} data.type - 交易类型 (可选)
 * @param {string} data.date - 日期 (可选)
 * @param {string} data.note - 备注 (可选)
 * @returns {Promise}
 */
export function updateTransaction(data) {
  return request.put('/api/transactions', data)
}

/**
 * 删除账单
 * @param {number} id - 账单 ID
 * @returns {Promise}
 */
export function deleteTransaction(id) {
  return request.delete(`/api/transactions/${id}`)
}

/**
 * 获取月度收支统计
 * @param {Object} params - 请求参数
 * @param {number} params.year - 年份
 * @param {number} params.month - 月份
 * @returns {Promise}
 */
export function getMonthlyStats(params) {
  return request.get('/api/transactions/stats/month', params)
}

export default {
  createTransaction,
  getTransactions,
  getTransactionDetail,
  updateTransaction,
  deleteTransaction,
  getMonthlyStats
}
