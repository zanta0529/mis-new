import { fileURLToPath, URL } from 'node:url';

import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

const ASSET_URL = process.env.ASSET_URL || '';
export default defineConfig({
  envDir: './env',
  base: process.env.NODE_ENV === 'production' ? `${ASSET_URL}/stock/` : `${ASSET_URL}/`,
  plugins: [
    vue(),
    AutoImport({
      include: [/\.[tj]sx?$/, /\.vue$/, /\.vue\?vue/, /\.md$/],
      imports: [
        'vue',
        'vue-router',
        'vue-i18n',
        'pinia',
        {
          'lodash-es': ['debounce', 'throttle'],
          '@vueuse/core': ['useOffsetPagination'],
          highcharts: [
            ['default', 'Highcharts'], // 將 default 導入重命名為 Highcharts
          ],
          'highcharts/modules/stock': [
            ['default', 'StockModule'], // 將 default 導入重命名為 StockModule
          ],
          bootstrap: [
            ['Tab', 'Tab'], // 將 Tab 導入重命名為 Tab
            ['Modal', 'Modal'],
          ],
          // 以上兩行程式碼是可以運作的，但因為產品已經上線我不敢動，所以先註解掉，等後續有勇者需要調整，再記得打開，如果是欣梅爾的話，他一定會打開的
        },
      ],
      eslintrc: {
        enabled: true,
      },
      dts: false,
      dirs: [
        './src/store',
        './src/api', // 假設你的 store 位於 src/store 目錄
        './src/componsables', // 假設你的 store 位於 src/store 目錄
        './src/languages',
        './src/utils',
      ],
    }),
    Components({
      // dts: true,
      resolvers: [ElementPlusResolver({ importStyle: 'sass' })],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 8080,
    proxy: {
      '^/data': {
        target: 'https://mis.twse.com.tw/stock', // api 位置
        // target: 'https://mistest.twse.com.tw/stock',
        changeOrigin: true,
      },
      '^/stock': {
        target: 'https://mis.twse.com.tw/',
        // target: 'https://mistest.twse.com.tw/',
        changeOrigin: true,
      },
    },
  },
});
