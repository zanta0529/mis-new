import axios from 'axios';

// 設定全域 AJAX 請求的默認配置
// 將不使用瀏覽器快取來存儲已經請求過的資料，而總是向伺服器發出請求以獲取最新的資料。
axios.defaults.cache = false;

const service = axios.create({
  baseURL: import.meta.env.baseURL,
  // timeout: 5000, // request timeout
  loading: false,
  headers: {
    Pragma: 'no-cache',
    'Cache-Control': 'no-cache,no-store',
  },
});

// 發送請求之前執行的 Axios 攔截器
service.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    console.error(error);
    return Promise.reject(error);
  }
);

let sessionExpired_flag = 0; //驗證失敗flag
let messageBox_flag = 0; //系統異常flag

// 這是一個在收到響應時執行的 Axios 攔截器
service.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default service;
