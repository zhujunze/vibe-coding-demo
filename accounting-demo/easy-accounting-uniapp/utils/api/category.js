/**
 * 分类相关 API 接口
 * 包含：获取所有分类、创建自定义分类、删除自定义分类等功能
 */

import request from './request.js'

/**
 * 获取所有分类
 * @param {Object} params - 请求参数
 * @param {string} params.type - 分类类型 (expense/income，可选)
 * @returns {Promise}
 */
export function getCategories(params) {
  return request.get('/api/categories', params)
}

/**
 * 创建自定义分类
 * @param {Object} data - 请求参数
 * @param {string} data.name - 分类名称
 * @param {string} data.type - 类型 (expense/income)
 * @param {string} data.icon - 图标
 * @param {string} data.color - 颜色
 * @returns {Promise}
 */
export function createCustomCategory(data) {
  return request.post('/api/categories/custom', data)
}

/**
 * 删除自定义分类
 * @param {number} id - 分类 ID
 * @returns {Promise}
 */
export function deleteCategory(id) {
  return request.delete(`/api/categories/${id}`)
}

export default {
  getCategories,
  createCustomCategory,
  deleteCategory
}
