import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// 导入对应包
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

import ElementPlus from 'unplugin-element-plus/vite'

// https://vitejs.dev/config/
export default defineConfig({
  build: {
    assetsInlineLimit: 0,
    chunkSizeWarningLimit: 2000
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver(
          { importStyle: 'sass' }
      )],
    }),
    Components({
      resolvers: [ElementPlusResolver(
          { importStyle: 'sass' }
      )],
    }),
    // 按需定制主题配置
    ElementPlus({
      useSource: true,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        // 自动导入定制化样式文件进行样式覆盖
        additionalData: `@use "@/assets/css/index.scss" as *;`,
      }
    }
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://lilili.qicp.vip:9080',  // 转发到目标地址
        changeOrigin: true,  // 更改请求头中的 Origin 字段
        secure: false,  // 如果目标是 http 的地址，设置为 false
        pathRewrite: {
          '^/api': '/api'  // 可选，重写路径，如果需要去掉/api前缀
        },
      },
      '/local': {
        target: 'http://localhost:9090/',  // 转发到目标地址
        changeOrigin: true,  // 更改请求头中的 Origin 字段
        secure: false,  // 如果目标是 http 的地址，设置为 false
        withCredentials: true, // 传递凭证
        rewrite: (path) => path.replace(/^\/local/, ''), // 手动重写路径
      },
    },
  },
})
