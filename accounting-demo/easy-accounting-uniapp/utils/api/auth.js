/**
 * 认证相关 API 接口
 * 包含：账号密码登录、用户注册、忘记密码、重置密码等功能
 */

import request from './request.js'

/**
 * 发送短信验证码（用于手机号验证等场景）
 * @param {Object} data - 请求参数
 * @param {string} data.phone - 手机号
 * @param {string} data.scene - 场景 (register/login/reset)
 * @returns {Promise}
 */
export function sendSmsCode(data) {
  return request.post('/api/auth/sms-code', data)
}

/**
 * 用户注册（手机号验证码方式）
 * @param {Object} data - 请求参数
 * @param {string} data.phone - 手机号
 * @param {string} data.password - 密码（需包含大小写字母和数字，至少8位）
 * @param {string} data.smsCode - 短信验证码
 * @param {string} [data.inviteCode] - 邀请码（可选）
 * @returns {Promise}
 */
export function register(data) {
  return request.post('/api/auth/register', {
    phone: data.phone,
    password: data.password,
    smsCode: data.smsCode,
    inviteCode: data.inviteCode
  })
}

/**
 * 用户登录（账号密码方式）
 * @param {Object} data - 请求参数
 * @param {string} data.account - 手机号或邮箱
 * @param {string} data.password - 密码
 * @returns {Promise}
 */
export function login(data) {
  return request.post('/api/auth/login', data)
}

/**
 * 忘记密码（发送重置链接）
 * @param {Object} data - 请求参数
 * @param {string} data.phone - 手机号
 * @param {string} data.smsCode - 短信验证码
 * @returns {Promise}
 */
export function forgotPassword(data) {
  return request.post('/api/auth/reset-password-token', data)
}

/**
 * 重置密码（通过邮件链接）
 * @param {Object} data - 请求参数
 * @param {string} data.token - 重置令牌（从邮件链接获取）
 * @param {string} data.confirmPassword - 确认密码
 * @returns {Promise}
 */
export function resetPassword(data) {
  return request.post('/api/auth/reset-password', data)
}

/**
 * 验证重置密码令牌
 * @param {string} token - 重置令牌
 * @returns {Promise}
 */
export function validateResetToken(token) {
  return request.get('/api/auth/validate-reset-token', { 
    params: { token } 
  })
}

/**
 * 获取重置密码 Token（旧版短信验证码方式，保留兼容）
 * @param {Object} data - 请求参数
 * @param {string} data.phone - 手机号
 * @param {string} data.smsCode - 短信验证码
 * @returns {Promise}
 */
export function getResetPasswordToken(data) {
  return request.post('/api/auth/reset-token', data)
}

/**
 * 刷新访问令牌
 * @param {Object} data - 请求参数
 * @param {string} data.refreshToken - 刷新令牌
 * @returns {Promise}
 */
export function refreshToken(data) {
  return request.post('/api/auth/refresh-token', data)
}

/**
 * 退出登录
 * @returns {Promise}
 */
export function logout() {
  return request.post('/api/auth/logout')
}

export default {
  sendSmsCode,
  register,
  login,
  forgotPassword,
  resetPassword,
  validateResetToken,
  getResetPasswordToken,
  refreshToken,
  logout
}
