// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// Import other plugins as needed

export default defineConfig({
  plugins: [react()],
  // No need to specify CSS/PostCSS config here as it will use postcss.config.js automatically
  // Other Vite configuration options
});