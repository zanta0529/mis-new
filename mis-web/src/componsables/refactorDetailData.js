export function useRefactorDetailData(array) {
  if (array && array.length > 0) {
    const msgArray = array.map((item) => {
      // 計算漲跌百分比
      //這裡前人把漲跌的色碼跟colorType寫反了，但已經上線的產品怕牽一髮動全身，所以這裡將錯就錯，但後續如果有要重構這邊要記得修正，包含spot-stock裏頭icon的樣式也要調整
      let upColor = '#2c812d';
      let downColor = '#e05e5e';
      const locale = localStorage.getItem('lang');
      if (locale == 'en') {
        upColor = '#e05e5e';
        downColor = '#2c812d';
      }
      const defaultColor = '#000000';
      const defaultO = '#669';
      if (typeof item.z !== 'undefined') {
        let diff = (item.z - item.y).toFixed(2);
        const diffPre = (Math.round((diff / item.y) * 10000) / 100).toFixed(2);
        const colorType = '';
        diff = diff > 0 ? `+${diff}` : diff;

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

      if (item.it !== 't') {
        const response = [
          {
            a: item.a,
            f: item.f,
            b: item.b,
            g: item.g,
          },
        ];
        const data = response[0];
        const recordList = [];

        const reverseAList = data.a.split('_').reverse();
        const reverseBList = data.b.split('_');
        const reverseGList = data.g.split('_');
        const reverseFList = data.f.split('_').reverse();
        putInValue(recordList, reverseAList, reverseFList, true);
        putInValue(recordList, reverseBList, reverseGList, false);
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

        function putInValue(rawDataList, priceList, countList, isSell) {
          priceList.forEach((value, index) => {
            if (priceList[index] === '' || countList[index] === '') {
              return;
            }

            if (isSell) {
              rawDataList.push({ a: priceList[index], f: countList[index], b: '-', g: '-' });
            } else {
              rawDataList.push({ a: '-', f: '-', b: priceList[index], g: countList[index] });
            }
            item.recordList = recordList;
          });
          return item.recordList;
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

        // 判斷顏色
        if (item.recordList) {
          item.recordList.forEach((record) => {
            const a = parseFloat(record.a) || 0;
            const b = parseFloat(record.b) || 0;
            const y = parseFloat(item.y) || 0;
            if (a === 0 && b === 0) {
              record.color = defaultO;
              return;
            }
            if (a > y || b > y) {
              record.color = downColor;
            } else if (a < y || b < y) {
              record.color = upColor;
            } else {
              record.color = defaultO;
            }
          });
        }
      }

      return item;
    });

    return { msgArray };
  }
  // 如果 array 為空，回傳特定訊息
  return { msgArray: [] };
}
