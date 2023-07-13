import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'

// https://vitejs.dev/config/
export default defineConfig({
  build: {
    outDir: "../src/main/resources/static"
  },
  server: {
    port: 3000,
    proxy: {
      "/": {
        target: "http://localhost:8080",
        rewrite: (path) => path.replace(/^\/api/, '')
      },
    }
  },
  plugins: [
    vue(),
    vueJsx(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
