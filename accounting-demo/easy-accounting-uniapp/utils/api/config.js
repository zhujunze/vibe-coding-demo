/**
 * API 基础配置文件
 * 包含基准 URL、超时时间、请求头等全局配置
 */

const config = {
  // 基准 URL - 开发环境下使用相对路径以便通过代理访问
  baseURL: process.env.NODE_ENV === 'development' ? '' : 'http://192.168.1.28:8080',
  
  // 请求超时时间 (毫秒)
  timeout: 30000,
  
  // 默认请求头
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  },
  
  // 是否需要 token 的接口路径前缀
  authPaths: ['/api'],
  
  // Token 存储 key
  tokenKey: 'Authorization',
  
  // Token 前缀
  tokenPrefix: 'Bearer ',
  
  // 是否开启请求日志
  enableLog: true
}

export default config
