import request from '@/utils/request';

const apiURL = '/stock/api';
// const detaURL = process.env.NODE_ENV === 'development' ? apiURL : 'https://mistest.ecloudmobile.net/stock/api';
const detaURL = apiURL;
// 集中市場大盤資訊
export function getCategory(type, id, bp) {
  let lang = localStorage.getItem('lang');
  if (lang === 'en') {
    lang = 'en';
  } else {
    lang = 'zh_tw';
  }
  const timestamp = Date.now();
  let url = `${detaURL}/getCategory.jsp?ex=${type}&i=${id}&_=${timestamp}&lang=${lang}`;
  if (bp) {
    url = `${detaURL}/getCategory.jsp?ex=${type}&bp=${bp}&_=${timestamp}&lang=${lang}`;
  }

  return request({
    method: 'get',
    url,
  });
}

// 集中市場大盤資訊
export function getStockInfo(currList) {
  let lang = localStorage.getItem('lang');
  if (lang === 'en') {
    lang = 'en';
  } else {
    lang = 'zh_tw';
  }
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStockInfo.jsp?ex_ch=${currList}&json=1&delay=0&_=${timestamp}&lang=${lang}`,
  });
}

// 集中市場大盤資訊
export function getStatis(type) {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStatis.jsp?ex=${type}&delay=0&_=${timestamp}`,
  });
}

// 現貨類股行情下拉選單
export function getIndustry() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getIndustry.jsp?_=${timestamp}`,
  });
}

// 盤後零股揭示下拉選單
export function getOddIndustry() {
  return request({
    method: 'get',
    url: `${detaURL}/getIndustry.jsp?type=odd`,
  });
}

// 現貨類股行情(盤中零股行情)
export function getOddInfo(currList) {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getOddInfo.jsp?ex_ch=${currList}&json=1&delay=0&_=${timestamp}`,
  });
}

// ETF發行單位變動及淨值揭露專區
export function getETFData() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `/stock/data/all_etf.txt?_=${timestamp}`,
  });
}

// ETN發行單位變動及指標價值揭露專區
export function getETNData() {
  const timestamp = Date.now();
  let lang = localStorage.getItem('lang');
  if (lang === 'en') {
    lang = 'en';
  } else {
    lang = 'zh_tw';
  }
  return request({
    method: 'get',
    url: `/stock/data/all_etn.txt?_=${timestamp}&lang=${lang}`,
  });
}

// 側邊個股行情查詢(共用)
export function getStockNames(qryStr) {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStockNames.jsp?n=${encodeURIComponent(qryStr)}&_=${timestamp}`,
  });
}

// 盤後零股揭示
export function getStock(ch) {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStock.jsp?ch=${ch}.tw&json=1&_=${timestamp}`,
  });
}

// 暫緩開盤
export function getStockDelay(ex, ip) {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStockDelay.jsp?ex=${ex}&ip=${ip}&_=${timestamp}`,
  });
}

// 借券中心個股借券成交查詢
export function getStockSblsList() {
  let lang = localStorage.getItem('lang');
  if (lang === 'en') {
    lang = 'en';
  } else {
    lang = 'zh_tw';
  }
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStockSblsList.jsp?_=${timestamp}&lang=${lang}`,
  });
}

// 借券中心個股借券成交查詢
export function getStockSbls(ch) {
  let lang = localStorage.getItem('lang');
  if (lang === 'en') {
    lang = 'en';
  } else {
    lang = 'zh_tw';
  }
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStockSbls.jsp?ch=${ch}&_=${timestamp}&lang=${lang}`,
  });
}

// 借券賣出可用餘額
export function getStockSblsCap() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStockSblsCap.jsp?delay=5000&_=${timestamp}`,
  });
}

// 證券商或證金公司可出借證券資訊
export function getStockSblsBrk() {
  let lang = localStorage.getItem('lang');
  if (lang === 'en') {
    lang = 'en';
  } else {
    lang = 'zh_tw';
  }
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `${detaURL}/getStockSblsBrk.jsp?_=${timestamp}&lang=${lang}`,
  });
}

// 活動公告訊息
export function getMisAnnouncement() {
  const timestamp = Date.now();
  return request({
    method: 'get',
    url: `/stock/data/mis_announcement.txt?_=${timestamp}`,
  });
}
