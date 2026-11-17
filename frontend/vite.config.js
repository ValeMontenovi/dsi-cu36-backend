import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // el backend tiene CORS habilitado para este puerto
    proxy: {
      // Todo lo que empiece con /api se reenvía al backend Spring Boot
      '/api': 'http://localhost:8081',
    },
  },
})
