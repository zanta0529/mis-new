export function useCalculateValue(array, data, ip) {
  if (array && array.length > 0) {
    const msgArray = array.map((item) => {

      //因為SpotStorck.vue stockInfoData的資料有時候會沒有值，需要以getCategory回傳的資料補足失去的部分
      if (!item.c && !!item.ch) {
        let chArr = item.ch.split('.')[0];
        item.c = chArr;
      }
      // 計算漲跌百分比
      if (typeof item.z !== 'undefined') {
        let diff = (item.z - item.y).toFixed(2);
        let diffPre = (Math.round((diff / item.y) * 10000) / 100).toFixed(2);
        //這裡前人把漲跌的色碼跟colorType寫反了，但已經上線的產品怕牽一髮動全身，所以這裡將錯就錯，但後續如果有要重構這邊要記得修正，包含spot-stock裏頭icon的樣式也要調整
        let upColor = '#2c812d';
        let downColor = '#e05e5e';


        const locale = localStorage.getItem('lang');
        if (locale == 'en') {
          upColor = '#e05e5e';
          downColor = '#2c812d';
        }
        let defaultColor = '#000000';
        let colorType = '';
        diff = diff > 0 ? '+' + diff : diff;

        if (diff == 'NaN') {
          item.pColor = defaultColor;
          item.colorType = 'default';
          item.diff = '';
          item.zHtmlStr = '-';
          item.defaultColor;
        } else if (diff == 0.0) {
          item.pColor = defaultColor;
          item.colorType = 'default';
          item.diff = diff;
          item.zHtmlStr = `(${diffPre} %)`;
          item.defaultColor;
        } else if (diff > 0) {
          item.pColor = downColor;
          item.colorType = 'downColor';
          item.diff = diff;
          item.zHtmlStr = `(${diffPre} %)`;
          item.defaultColor;
        } else {
          item.pColor = upColor;
          item.colorType = 'upColor';
          item.diff = diff;
          item.zHtmlStr = `(${diffPre} %)`;
          item.defaultColor;
        }
      }

      // 揭示買價
      if (item.b != undefined) {
        item.b = useInsertFiBest(item.b);
      }
      // 揭示買量
      if (item.g != undefined) {
        item.g = useInsertFiBest(item.g);
      }
      // 揭示賣價
      if (item.a != undefined) {
        item.a = useInsertFiBest(item.a);
      }
      // 揭示賣量
      if (item.f != undefined) {
        item.f = useInsertFiBest(item.f);
      }

      const defaultBackground = '#ffffff';
      const tradingHalt = 'rgba(255, 133, 113, 0.1)'; // 暫停交易
      const tradingResumption = 'rgba(79, 170, 132, 0.1)'; // 恢復交易
      const postponedSession = 'rgba(58, 110, 165, 0.1)'; // 暫緩開盤或暫緩收盤
      const simulated = 'rgba(242, 181, 41, 0.1)'; // 試算資訊
      item.background = '';

      if (ip) {
        item.background = postponedSession;
      } else if (data) {
        if (typeof item.st !== 'undefined' && typeof item.rt !== 'undefined') {
          let stStr = item.st;
          let rtStr = item.rt;
          let tStr = data.queryTime.sysTime;

          stStr = parseInt(stStr.replace(/\:/g, ''));
          rtStr = parseInt(rtStr.replace(/\:/g, ''));
          tStr = parseInt(tStr.replace(/\:/g, ''));

          if (stStr == 80000 && rtStr == 999999) {
            item.background = tradingHalt;
          } else if (tStr >= stStr && tStr <= rtStr) {
            item.background = tradingHalt;
          } else {
            item.background = tradingResumption;
          }
          if (item.rt == '99:99:99') {
            item.rt = '13:30:00';
          }
        }
        // 試算標示
        if (item.ts == '1') {
          // TODO 試算標示
          item.background = simulated;

          // TODO 暫緩收盤，開盤
          if (item.ip == 4 || item.ip == 5 || item.ip == 6) {
            item.background = postponedSession;
          }

          // TODO 取消試算標示
        } else if (item.ts == '0') {
          item.background = defaultBackground;
        }
      }

      return item;
    });

    return { msgArray };
  } else {
    // 如果 array 為空，回傳特定訊息
    return { msgArray: [] };
  }
}

const useInsertFiBest = (values) => {
  const valArr = values.split('_');
  let result;
  if (valArr[0] == 'NaN') {
    result = '-';
  } else {
    result = Number(valArr[0]).toFixed(2);
  }
  return result;
};
