import request from '@/utils/request';
// const detaURL = process.env.NODE_ENV === 'development' ? '/data' : 'https://mistest.ecloudmobile.net/stock/data';
const detaURL = '/stock/data';
// 集中市場大盤走勢圖
export function getMisOhlcTSE() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/mis_ohlc_TSE.txt?_=${timestamp}`,
  });
}

// 上櫃股票大盤走勢圖
export function getMisOhlcOTC() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/mis_ohlc_OTC.txt?_=${timestamp}`,
  });
}

// 寶島指數走勢圖
export function getMisOhlcFRMSA() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/mis_ohlc_FRMSA.txt?_=${timestamp}`,
  });
}

// 臺指期走勢圖
export function getFuturesChart() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/futures_chart.txt?_=${timestamp}`,
  });
}

// 臺股期貨指數走勢圖
export function getMisOhlcIX0126() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/mis_ohlc_IX0126.txt?_=${timestamp}`,
  });
}

// mis_ohlc_WWW.txt
export function getMisOhlcWWW() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/mis_ohlc_WWW.txt?_=${timestamp}`,
  });
}

// mis_ohlc_TW50.txt
export function getMisOhlcTW50() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/mis_ohlc_TW50.txt?_=${timestamp}`,
  });
}

// mis_ohlc_CG100.txt
export function getMisOhlcCG100() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/mis_ohlc_CG100.txt?_=${timestamp}`,
  });
}
