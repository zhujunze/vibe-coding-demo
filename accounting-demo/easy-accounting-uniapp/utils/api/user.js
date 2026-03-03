/**
 * 用户相关 API 接口
 * 包含：获取用户信息、更新用户信息、获取用户统计数据等功能
 */

import request from './request.js'

/**
 * 获取当前用户信息
 * @returns {Promise}
 */
export function getCurrentUser() {
  return request.get('/api/users/me')
}

/**
 * 更新用户信息
 * @param {Object} data - 请求参数
 * @param {string} data.username - 用户名 (可选)
 * @param {string} data.avatar - 头像 URL(可选)
 * @param {string} data.gender - 性别 (可选)
 * @returns {Promise}
 */
export function updateUserInfo(data) {
  return request.put('/api/users/me', data)
}

/**
 * 获取用户统计数据
 * @returns {Promise}
 */
export function getUserStats() {
  return request.get('/api/users/stats')
}

export default {
  getCurrentUser,
  updateUserInfo,
  getUserStats
}
