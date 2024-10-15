## 使用技術說明

### 主要技術

| 名稱    | 版本     | 說明      | 文檔                        | 備註                                                 |
| ------- | -------- | --------- | --------------------------- | ---------------------------------------------------- |
| Node.js | >=22.8.0 | -         | -                           | 若有調整版本，package.json 的 engines 設定須一併調整 |
| vite    | 5.4.6    | -         | [連結](https://vitejs.dev/) | -                                                    |
| Vue     | 3.5.6    | Framework | [連結](https://vuejs.org/)  | `compositionAPI` + `script setup`                    |

### Vite/Vue Plugin

| 名稱                        | 版本   | 說明                 | 文檔                                                               | 備註 |
| --------------------------- | ------ | -------------------- | ------------------------------------------------------------------ | ---- |
| vue-router                  | 4.4.5  | 路由管理             | [連結](https://router.vuejs.org/zh/)                               |      |
| vue-gtag                    | 2.0.1  | vue整合 GA4          | [連結](https://www.npmjs.com/package/vue-gtag)                     |      |
| vueuse                      | 11.0.3 | vue 專用工具庫       | [連結](https://v10-9-0.vueuse.org/)                                |      |
| pinia                       | 2.2.2  | 狀態管理             | [連結](https://seb-l.github.io/pinia-plugin-persist/)              |      |
| pinia-plugin-persistedstate | 3.2.1  | 把store存進storage   | [連結](https://prazdevs.github.io/pinia-plugin-persistedstate/zh/) |      |
| unplugin-auto-import        | 0.18.3 | 自動將依賴套件import | [連結](https://github.com/unplugin/unplugin-auto-import)           |      |
| unplugin-vue-components     | 0.27.4 | 自動import component | [連結](https://github.com/unplugin/unplugin-vue-components)        |      |

### Lint/Format

| 名稱                         | 版本   | 說明                         | 文檔                                                               | 備註 |
| ---------------------------- | ------ | ---------------------------- | ------------------------------------------------------------------ | ---- |
| prettier                     | 3.3.3  | 程式碼格式化                 | [連結](https://prettier.io/)                                       |      |
| eslint                       | 8.57.0 | 程式碼規範器                 | [連結](https://eslint.org/)                                        |      |
| eslint-config-prettier       | 9.1.0  | 解決 eslint 與 prettier 衝突 | [連結](https://github.com/prettier/eslint-config-prettier)         |      |
| eslint-plugin-prettier       | 5.2.1  | 將 Prettier 整合進 eslint    | [連結](https://github.com/prettier/eslint-plugin-prettier)         |      |
| eslint-config-airbnb-base    | 15.0.0 | eslint airbnb 規則           | [連結](https://www.npmjs.com/package/eslint-config-airbnb-base)    |      |
| eslint-import-resolver-alias | 1.1.2  | 解析別名                     | [連結](https://www.npmjs.com/package/eslint-import-resolver-alias) |      |
| eslint-plugin-import         | 2.30.0 | 檢查 ES6 module 導入問題     | [連結](https://www.npmjs.com/package/eslint-plugin-import)         |      |
| eslint-plugin-vue            | 9.28.0 | 檢查 vue 程式碼風格          | [連結](https://eslint.vuejs.org/)                                  |      |

### UI Library

| 名稱                   | 版本   | 說明                  | 文檔                                                       | 備註                                                   |
| ---------------------- | ------ | --------------------- | ---------------------------------------------------------- | ------------------------------------------------------ |
| bootstrap              | 5.3.3  | UI Library            | [連結](https://bootstrap5.hexschool.com/)                  | 有英文文檔，但為了方便閱讀，所以連結是六角翻譯的中文版 |
| bootstrap-icon         | 1.11.3 | bootstrap icon套件    | [連結](https://icons.getbootstrap.com/)                    |                                                        |
| sass                   | 1.77.6 | Sass模組              | [連結](https://www.npmjs.com/package/sass)                 | Bootstrap 需要安裝sass才能正常編譯                     |
| element-plus           | 2.8.3  | UI Library            | [連結](https://element-plus.org/zh-CN/)                    | 因為原開發者對bootstrap不熟悉 所以另外引入element-plus |
| element-plus/icons-vue | 2.3.1  | element-plus icon套件 | [連結](https://element-plus.org/zh-CN/component/icon.html) |                                                        |

### JS Library
| 名稱      | 版本    | 說明            | 文檔                                            | 備註 |
| --------- | ------- | --------------- | ----------------------------------------------- | ---- |
| lodash-es | 4.17.21 | JS函式庫        | [連結](https://www.npmjs.com/package/lodash-es) |      |
| axios     | 1.7.7   | promise請求工具 | [連結](https://github.com/axios/axios)          |      |

### 圖表工具
| 名稱           | 版本   | 說明        | 文檔                                                 | 備註 |
| -------------- | ------ | ----------- | ---------------------------------------------------- | ---- |
| highcharts     | 11.4.8 | 圖表套件    | [連結](https://highcharts.com.cn/)                   |      |
| highcharts-vue | 2.0.1  | 圖表vue整合 | [連結](https://github.com/highcharts/highcharts-vue) |      |
