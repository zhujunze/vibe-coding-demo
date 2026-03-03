import { defineConfig } from 'vite'
import uni from '@dcloudio/vite-plugin-uni'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni()],
  server: {
    proxy: {
      '/api': {
        target: 'http://192.168.1.28:8080', // 后端API地址
        changeOrigin: true,
        secure: false, // 如果是https接口，需要配置这个参数
        rewrite: (path) => path // 移除多余的重写，让路径保持原样
      }
    },
    // 设置允许外部访问，便于调试
    host: '0.0.0.0',
    port: 5173
  }
})