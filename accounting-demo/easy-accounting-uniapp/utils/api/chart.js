/**
 * 图表相关 API 接口
 * 包含：获取收支趋势、获取分类占比等功能
 */

import request from './request.js'

/**
 * 获取收支趋势
 * @param {Object} params - 请求参数
 * @param {number} params.year - 年份
 * @param {number} params.month - 月份
 * @param {string} params.type - 类型 (daily/weekly/monthly)
 * @returns {Promise}
 */
export function getIncomeExpenseTrend(params) {
  return request.get('/api/charts/trend', params)
}

/**
 * 获取分类占比
 * @param {Object} params - 请求参数
 * @param {number} params.year - 年份
 * @param {number} params.month - 月份
 * @param {string} params.type - 类型 (expense/income)
 * @returns {Promise}
 */
export function getCategoryDistribution(params) {
  return request.get('/api/charts/distribution', params)
}

export default {
  getIncomeExpenseTrend,
  getCategoryDistribution
}
