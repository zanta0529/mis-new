//取得今天日期
const getFullToday = () => {
  const d = new Date();
  const day_list = ['日', '一', '二', '三', '四', '五', '六'];
  const today = `${d.toLocaleDateString()}（${day_list[d.getDay()]}）`;
  return today;
};
//取得日期
const getFormatDate = (val = 0) => {
  const d = new Date();
  const target = new Date(d.getTime() + 3600 * 1000 * 24 * val);
  const format = `${target.getFullYear()}/${
    target.getMonth() < 9 ? '0' + (target.getMonth() + 1) : target.getMonth() + 1
  }/${target.getDate() < 10 ? '0' + target.getDate() : target.getDate()}`;
  return format;
};

//格式化日期時間
const formatDateTime = (type, val) => {
  let text = '';
  switch (type) {
    case 'YYYY/MM/DD HH:mm':
      text =
        val.slice(0, 4) +
        '/' +
        val.slice(4, 6) +
        '/' +
        val.slice(6, 8) +
        ' ' +
        val.slice(8, 10) +
        ':' +
        val.slice(10, 12);
      break;
    case 'YYYY-MM-DD HH:mm':
      text =
        val.slice(0, 4) +
        '-' +
        val.slice(4, 6) +
        '-' +
        val.slice(6, 8) +
        ' ' +
        val.slice(8, 10) +
        ':' +
        val.slice(10, 12);
      break;
    case 'YYYY/MM/DD':
      text = val.slice(0, 4) + '/' + val.slice(4, 6) + '/' + val.slice(6, 8);
      break;
    case 'HH:mm':
      text = val.slice(8, 10) + ':' + val.slice(10, 12);
      break;
    case 'YYYYMMDD':
      text = val.slice(0, 4) + val.slice(5, 7) + val.slice(8, 10);
      break;
    case 'YYYYMMDDHHmmss':
      text =
        val.slice(0, 4) +
        val.slice(5, 7) +
        val.slice(8, 10) +
        val.slice(11, 13) +
        val.slice(14) +
        '00';
      break;
    case 'HH:mm:ss':
      text =
        val.slice(0, 4) +
        val.slice(5, 7) +
        val.slice(8, 10) +
        val.slice(11, 13) +
        val.slice(14) +
        '00';
      break;
  }
  return text;
};

// 取得 時:分:秒
// 超過 13:33:00 的話，顯示 13:33:00
const getTime = (h, m, s) => {
  const d = new Date();
  const maxTime = new Date();
  maxTime.setHours(h, m, s);

  const targetTime = d > maxTime ? maxTime : d;

  const padWithZero = (num) => num.toString().padStart(2, '0');

  const hours = padWithZero(targetTime.getHours());
  const minutes = padWithZero(targetTime.getMinutes());
  const seconds = padWithZero(targetTime.getSeconds());

  return `${hours}:${minutes}:${seconds}`;
};

export { getFullToday, getFormatDate, formatDateTime, getTime };
