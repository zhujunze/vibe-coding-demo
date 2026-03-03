/**
 * 预算相关 API 接口
 * 包含：获取预算状态、设置预算等功能
 */

import request from './request.js'

/**
 * 获取预算状态
 * @param {Object} params - 请求参数
 * @param {number} params.year - 年份
 * @param {number} params.month - 月份
 * @returns {Promise}
 */
export function getBudgetStatus(params) {
  return request.get('/api/budgets/status', params)
}

/**
 * 设置预算
 * @param {Object} data - 请求参数
 * @param {number} data.categoryId - 分类 ID
 * @param {string} data.month - 月份 (YYYY-MM)
 * @param {number} data.amount - 预算金额
 * @param {number} data.alertThreshold - 预警阈值 (可选，0-1 之间)
 * @returns {Promise}
 */
export function setBudget(data) {
  return request.post('/api/budgets', data)
}

export default {
  getBudgetStatus,
  setBudget
}
