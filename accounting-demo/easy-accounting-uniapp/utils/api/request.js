/**
 * HTTP 请求封装工具
 * 基于 axios 封装，提供统一的请求拦截器和响应拦截器
 */
import axios from 'axios'
import config from './config.js'

/**
 * 创建 axios 实例
 */
const axiosInstance = axios.create({
  baseURL: config.baseURL,
  timeout: config.timeout,
  headers: config.headers
})

/**
 * 请求拦截器
 * 统一处理请求头，添加 token 等信息
 */
axiosInstance.interceptors.request.use(
  (request) => {
    // 开发环境打印请求日志
    if (config.enableLog) {
      console.log('[Request]', {
        url: request.url,
        method: request.method,
        params: request.params,
        data: request.data
      })
    }
    
    // 从本地存储获取 token
    const token = uni.getStorageSync('token')
    
    // 判断是否需要添加 token
    const needAuth = config.authPaths.some(path => request.url.startsWith(path))
    
    if (token && needAuth) {
      request.headers[config.tokenKey] = config.tokenPrefix + token
    }
    
    return request
  },
  (error) => {
    console.error('[Request Error]', error)
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 统一处理响应数据和错误
 */
axiosInstance.interceptors.response.use(
  (response) => {
    // 开发环境打印响应日志
    if (config.enableLog) {
      console.log('[Response]', {
        url: response.config.url,
        status: response.status,
        data: response.data
      })
    }
    
    const res = response.data
    
    // 判断响应码
    if (res.code !== 200) {
      // 401: 未授权，需要登录
      if (res.code === 401) {
        // 清除 token
        uni.removeStorageSync('token')
        
        // 跳转到登录页
        uni.reLaunch({
          url: '/pages/auth/Login.vue'
        })
        
        return Promise.reject(new Error(res.message || '未授权，请登录'))
      }
      
      // 显示错误提示
      uni.showToast({
        title: res.message || '请求失败',
        icon: 'none',
        duration: 2000
      })
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  (error) => {
    console.error('[Response Error]', error)
    
    // 错误信息处理
    let message = '网络请求失败'
    
    if (error.response) {
      // 服务器返回错误响应
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未授权，请登录'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        case 502:
          message = '网关错误'
          break
        case 503:
          message = '服务不可用'
          break
        case 504:
          message = '网关超时'
          break
        default:
          message = `连接错误 (${error.response.status})`
      }
    } else if (error.message.includes('timeout')) {
      // 超时错误
      message = '请求超时，请稍后重试'
    } else if (error.message.includes('Network')) {
      // 网络错误
      message = '网络连接失败，请检查网络'
    }
    
    // 显示错误提示
    uni.showToast({
      title: message,
      icon: 'none',
      duration: 2000
    })
    
    return Promise.reject(error)
  }
)

/**
 * GET 请求
 * @param {string} url 请求地址
 * @param {Object} params 请求参数
 * @param {Object} config 其他配置
 * @returns {Promise} 请求 Promise
 */
export function get(url, params = {}, config = {}) {
  return axiosInstance.get(url, { params, ...config })
}

/**
 * POST 请求
 * @param {string} url 请求地址
 * @param {Object} data 请求数据
 * @param {Object} config 其他配置
 * @returns {Promise} 请求 Promise
 */
export function post(url, data = {}, config = {}) {
  return axiosInstance.post(url, data, config)
}

/**
 * PUT 请求
 * @param {string} url 请求地址
 * @param {Object} data 请求数据
 * @param {Object} config 其他配置
 * @returns {Promise} 请求 Promise
 */
export function put(url, data = {}, config = {}) {
  return axiosInstance.put(url, data, config)
}

/**
 * DELETE 请求
 * @param {string} url 请求地址
 * @param {Object} params 请求参数
 * @param {Object} config 其他配置
 * @returns {Promise} 请求 Promise
 */
export function del(url, params = {}, config = {}) {
  return axiosInstance.delete(url, { params, ...config })
}

/**
 * 上传文件
 * @param {string} url 请求地址
 * @param {Object} formData 表单数据
 * @param {Object} config 其他配置
 * @returns {Promise} 请求 Promise
 */
export function upload(url, formData, config = {}) {
  return axiosInstance.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    ...config
  })
}

/**
 * 下载文件
 * @param {string} url 请求地址
 * @param {Object} config 其他配置
 * @returns {Promise} 请求 Promise
 */
export function download(url, config = {}) {
  return axiosInstance.get(url, {
    responseType: 'blob',
    ...config
  })
}

// 导出 axios 实例，供特殊场景使用
export { axiosInstance }

// 默认导出
export default {
  get,
  post,
  put,
  del,
  upload,
  download,
  axiosInstance
}
