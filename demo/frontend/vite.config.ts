import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  base: '/',
  plugins: [vue()],
  define: {
    // sockjs-client が参照する Node.js グローバルをブラウザ向けに解決
    global: 'globalThis',
  },
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  }
})
