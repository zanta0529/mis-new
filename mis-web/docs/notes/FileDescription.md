# 資料夾說明

```txt
mis-web
├─ docs                             // 文件相關
│  └─ images                        // 文件圖片存放
│
├─ public                           // 不需編譯的靜態資源
│
├─ src
│  ├─ api                           // API相關檔案
│  ├─ assets                        // font, scss, image等需靜態資源
│  ├─ components                    // 元件
│  │  ├─ button                     // 通用按鈕
│  │  ├─ chart                      // 圖表
│  │  ├─ common                     // 通用組件
│  │  ├─ independentPage            // 外部網站圖表
│  │  ├─ modal                      // 使用條款、隱私權保護說明彈出視窗
│  │  ├─ tab                        // 走勢圖分頁
│  │  ├─ table                      // 表格
│  ├─ composables                   // 可複用的邏輯
│  ├─ composable                    // 可複用的邏輯
│  ├─ languages                     // i18n
│  ├─ router                        // 路由
│  ├─ store                         // 狀態管理
│  ├─ test-json                     // 測試時使用的json
│  ├─ utils                         // 功能性邏輯
│  ├─ views                         // 頁面檔案
│  ├─ main.js                 
│  └─ App.vue  
│      
├─ .eslintignore                    // 需eslint 忽略的檔案
├─ .eslintrc-auto-import.json       // 避免auto import 被eslint 偵錯
├─ .eslintrc.cjs                    // eslint
├─ .gitignore         
├─ .prettierrc.yml               
├─ index.html              
├─ package-lock.json
├─ package.json
├─ README.md
└─ vite.config.js
```