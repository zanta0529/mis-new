<script setup>
const handleWheel = (event) => {
  event.preventDefault();
  const chartContainer = document.getElementById('chart-otc');
  chartContainer.style.pointerEvents = 'none'; // 禁用滑動時的手勢
  setTimeout(() => {
    chartContainer.style.pointerEvents = 'auto'; // 在稍後恢復為 auto
  }, 10); // 停止禁用滑動手勢的時間（以毫秒為單位）
};

const { t, locale } = useI18n();
const emit = defineEmits(['update:model-value', 'stockInfoData']);

StockModule(Highcharts);
Highcharts.setOptions({
  global: {
    useUTC: false,
  },
});

const chartRef = ref(null);
const chartOptionsOTC = ref({
  chart: {
    animation: false,
    renderTo: 'otcChart',
    alignTicks: true,
    plotBorderColor: '#000000',
    plotBorderWidth: 0,
    zoomType: '',
  },
  accessibility: {
    enabled: false,
  },
  scrollbar: {
    enabled: false,
  },
  credits: {
    text: '',
  },
  navigator: {
    enabled: false,
  },
  tooltip: {
    shared: true,
    useHTML: true,
    borderWidth: 1,
    borderColor: '#757575',
  },
  rangeSelector: {
    enabled: false,
  },
  title: {
    text: '',
    align: 'left',
  },
  subtitle: {
    text: '',
    align: 'right',
    style: {
      fontSize: '18px',
    },
  },
  legend: {
    enabled: false,
  },
  xAxis: {
    type: 'datetime',
    tickInterval: 60 * 60 * 1000,
    minorTickInterval: 60 * 60 * 1000,
    dateTimeLabelFormats: {
      hour: '%H',
    },
    startOnTick: false,
    endOnTick: false,
    maxPadding: 0,
    labels: {
      align: 'right',
      style: {
        fontFamily: 'Tahoma, Verdana, Arial, Helvetica, sans-serif',
        fontSize: '14px',
        color: '#757575',
      },
      x: 5,
    },
  },
  yAxis: [
    {
      labels: {
        style: {
          color: '#757575',
          fontSize: '14px',
        },
        align: 'right',
        // x: -5,
        // y: 4,
      },
      gridLineColor: '#E4E4E4',
      gridLineWidth: 0,
      startOnTick: false,
      endOnTick: false,
      showLastLabel: true,
      title: {
        text: '',
      },
    },
    {
      allowDecimals: true,
      gridLineColor: '#E4E4E4',
      title: {
        text: '',
      },
      opposite: true,
    },
  ],

  series: [
    {
      type: 'line',
      name: '加權指數',
      color: '#ff0000',
      colorup: (() => {
        return locale.value === 'zhHant' ? '#e05e5e' : '#2c812d';
      })(),
      colordown: (() => {
        return locale.value === 'zhHant' ? '#2c812d' : '#e05e5e';
      })(),
      zIndex: 2,
      lineWidth: 1.8,
      dataGrouping: {
        enabled: false,
      },
    },
    {
      type: 'area',
      name: '成交金額',
      color: {
        linearGradient: { x1: 0, x2: 1, y1: 0, y2: 1 },
        stops: [
          [0, '#0000C7'],
          [1, '#038BE688'],
        ],
      },
      zIndex: 1,
      yAxis: 1,
      lineColor: '#366FB9',
      lineWidth: 1.5,
      dataGrouping: {
        enabled: false,
      },
    },
  ],
});

const showMessage = ref(false);
const message = ref('');
const timezoneOffset = ref(0); // 時區偏移量
const hourOffset = ref(0); // 小時偏移量
const msecOffset = ref(0); // 毫秒偏移量
onMounted(async () => {
  // 檢查瀏覽器是否過就
  const ie = /msie/.test(navigator.userAgent.toLowerCase()); // 檢查使用者的瀏覽器代理字串中是否包含 "msie"，回傳 true 或 false。
  if (ie) {
    try {
      const agent = navigator.userAgent.split(';')[1];
      const version = agent.split(' ')[2].split('.')[0];
      if (version < 9) {
        message.value =
          '您正在使用過時的瀏覽器軟體，為了提昇安全性且正確顯示網頁內容，建議使用 Google Chrome 等新版瀏覽器。';
        showMessage.value = true;
      }
    } catch (e) {
      console.error(e);
    }
  }

  timezoneOffset.value = new Date().getTimezoneOffset() + 480;
  hourOffset.value = timezoneOffset.value / 60;
  msecOffset.value = hourOffset.value * 60 * 60 * 1000;
  await procIndexPageOTC();
  emit('stockInfoData', 'otc', getStockOTCData());

  // 間隔ID
  const intervalId = setInterval(() => {
    if (!isTradingTime.value) {
      clearAllIntervals();
    }
  }, 5 * 1000); // 每5秒觸發一次
});

const stockInfo = reactive({
  staticObj: [],
  ohlcArray: [],
  indexData: [],
  amountData: [],
  infoArray: [],
  zHtmlStr: '',
  pColor: '',
});
const time = ref('');
const refreshTseId = ref(-1);
const userDelay = ref(-1);
let startTime = '';
let closeTime = '';

const procIndexPageOTC = async () => {
  const data = await getMisOhlcOTC();
  stockInfo.staticObj = data.staticObj;
  stockInfo.ohlcArray = data.ohlcArray;
  stockInfo.indexData = data.ohlcArray.map((item) => [parseInt(item.t), parseInt(item.c)]);
  stockInfo.amountData = data.ohlcArray.map((item) => [parseInt(item.t), parseInt(item.s)]);
  stockInfo.infoArray = data.infoArray;
  time.value = data.infoArray[0].t;
  chartOptionsOTC.value.series[0].data = stockInfo.indexData;
  chartOptionsOTC.value.series[1].data = stockInfo.amountData;
  if (data.rtcode == '0000') {
    if (typeof data.infoArray !== 'undefined' && data.infoArray.length > 0) {
      const exeYear = data.infoArray[0].d.substring(0, 4);
      const exeMonth = data.infoArray[0].d.substring(4, 6) - 1;
      const exeDay = data.infoArray[0].d.substring(6);

      const dtB = new Date(exeYear, exeMonth, exeDay, 13, 33, 0);
      closeTime = dtB.getTime();

      const dtA = new Date(exeYear, exeMonth, exeDay, 9, 0, 0);
      startTime = dtA.getTime();

      const item = data.infoArray[0];
      let upper = item.u;
      let lower = item.w;
      const prevClosePrice = item.y;

      if (upper == undefined) {
        upper = item.h;
      }

      if (lower == undefined) {
        lower = item.l;
      }

      loadChart('o00', item.d, upper * 1, lower * 1, prevClosePrice * 1, data);
      procIndexPage(data);
    }
    emit('stockInfoData', 'otc', getStockOTCData());
    if (refreshTseId.value === -1) {
      let userDelay = 15000;
      if (typeof data.userDelay !== 'undefined') {
        userDelay = data.userDelay;
      }

      refreshTseId.value = setInterval(() => {
        procIndexPageOTC();
      }, userDelay);
    }
  }
};

const refreshOtcId = ref(-1);
const startTimeO = ref(0);
const closeTimeO = ref(0);

const getColor = (diff) => {
  const isUpper = diff > 0;
  const isZhHant = locale.value === 'zhHant';
  if (isUpper) {
    return isZhHant ? '#e05e5e' : '#2c812d';
  }
  return isZhHant ? '#2c812d' : '#e05e5e';
};

const procIndexPage = (data) => {
  const item = data?.infoArray[0];

  const channel = `${item?.ex}_${item?.ch}`;
  const d0 = item?.d;
  const upper = item?.u;
  const lower = item?.w;
  const prevClosePrice = item?.y;

  let ch = '#INDEX_';
  if (item?.ch.indexOf('t00') == 0) {
    ch += 'TWSE';
  } else {
    ch += 'OTC';
  }
  if (typeof item?.z != 'undefined') {
    let zHtmlStr = item?.z;
    let diff = (item?.z - item?.y).toFixed(2);
    const diffPre = (Math.round((diff / item?.y) * 10000) / 100).toFixed(2);
    let newColor = '#000000';
    diff = diff > 0 ? `+${diff}` : diff;
    newColor = getColor(diff);
    zHtmlStr = `<font color=${newColor}>(${diff}/${diffPre}%)</font>`;
    stockInfo.pColor = newColor;
    stockInfo.zHtmlStr = `(${diff} / ${diffPre} %)`;
  }
};

let chart;
let chartChannel;
let otcChart;
let otcChartChannel;
const groupingUnits = [['minute', [1]]];
const groupingUnits1 = [
  ['minute', [1, 5, 10, 30, 6]],
  ['hour', [1]],
];

let tseLastTime = 0;
let tseLastTimeStamp = 0;
let otcLastTime = 0;
let otcLastTimeStamp = 0;

const loadChart = (channel, d0, upper, lower, prevClosePrice, data) => {
  upper = parseInt(upper) < parseInt(prevClosePrice) ? prevClosePrice : upper;
  lower = parseInt(lower) > parseInt(prevClosePrice) ? prevClosePrice : lower;

  const ohlc = [];
  const volume = [];
  const cPrice = [];
  const dataLength = data.ohlcArray.length;
  let maxValue = 0;

  for (let i = 0; i < dataLength; i++) {
    const currTime = parseInt(data.ohlcArray[i].t) + msecOffset.value;
    if (i == 0) {
      const date = new Date(currTime);
      const minutes = date.getMinutes();
      const hours = date.getHours();
      const seconds = date.getSeconds();
      if (!(hours == 9 && minutes == 0)) {
        date.setMinutes(0);
        date.setHours(9);
        cPrice.push([
          date.getTime(), // currentTime, // the date
          prevClosePrice * 1, // the volume
        ]);
      }
    }
    if (dataLength - 1 === i) {
      if (channel === 't00') {
        tseLastTime = data.ohlcArray[i].ts;
        tseLastTimeStamp = parseInt(data.ohlcArray[i].t) + 12 * 60 * 60 * 1000;
      }
      if (channel === 'o00') {
        otcLastTime = data.ohlcArray[i].ts;
        otcLastTimeStamp = parseInt(data.ohlcArray[i].t) + 12 * 60 * 60 * 1000;
      }
    }
    if (channel === 't00') {
      data.ohlcArray[i].s = (data.ohlcArray[i].s / 100).toFixed(2);
    }

    if (channel === 'o00') {
      data.ohlcArray[i].s = (data.ohlcArray[i].s / 100).toFixed(2);
    }

    if (data.ohlcArray[i].s >= 0) {
      cPrice.push([
        currTime, // currentTime, // the date
        data.ohlcArray[i].c * 1, // the volume
      ]);

      if (data.ohlcArray[i].s > maxValue) {
        maxValue = data.ohlcArray[i].s * 1 + 1;
      }
      volume.push([
        currTime, // currentTime, // the date
        data.ohlcArray[i].s * 1, // the volume
      ]);
    }
  }
  loadOhcl(channel, cPrice, volume, upper, lower, maxValue, prevClosePrice);
};

const sPoint = 200;
const loadOhcl = (channel, cPrice, volume, upper, lower, maxValue, prevClosePrice) => {
  if (channel.indexOf('o00') >= 0) {
    let gap = parseInt(prevClosePrice * 0.015);

    const upGap = upper - prevClosePrice;
    const lowGap = prevClosePrice - lower;

    let valueInt;
    let tkInt = 0.1;
    chartChannel = channel;

    const rPrevClosePrice = Math.round(prevClosePrice * 10) / 10;
    let vGap = 0;
    if (upper - rPrevClosePrice > rPrevClosePrice - lower) vGap = upper - rPrevClosePrice;
    else vGap = rPrevClosePrice - lower;

    if (vGap < 0.5) tkInt = 0.1;
    else {
      for (let i = 1; i < 100; i++) {
        if (vGap < 0.5 * i) {
          tkInt = 0.1 * i;
          break;
        }
      }
    }

    if (maxValue == 0) valueInt = 1;
    else {
      const roundedMaxValue = Math.ceil(maxValue);
      if (roundedMaxValue % 5 !== 0) {
        valueInt = roundedMaxValue + (5 - (roundedMaxValue % 5));
      } else {
        valueInt = roundedMaxValue + 5;
      }
    }

    if (upGap > gap || lowGap > gap) {
      gap = lowGap;
      if (upGap > lowGap) {
        gap = upGap;
      }
      if (chart != undefined) {
        otcChart = undefined;
      }
    }

    if (otcChart != undefined && maxValue > chart.yAxis[1].max) {
      otcChart.yAxis[1].max = maxValue;
      if (chart != undefined) {
        otcChart = undefined;
      }
    }

    if (1 == 1) {
      prevClosePrice = parseFloat(prevClosePrice);

      const upper0 = parseFloat(prevClosePrice) + gap;
      const lower0 = parseFloat(prevClosePrice - gap);
      if (upper > upper0) {
        upper = parseFloat(upper) + 1;
        lower = parseFloat(prevClosePrice - (upper - prevClosePrice));
      } else if (lower < lower0) {
        lower = parseFloat(lower) - 1;
        upper = parseFloat(prevClosePrice + (prevClosePrice - lower));
      } else {
        upper = upper0;
        lower = lower0;
      }
      (chartOptionsOTC.value.chart = {
        ...chartOptionsOTC.value.chart,
        ...{
          zoomType: '',
        },
      }),
        (chartOptionsOTC.value.xAxis[0] = {
          ...chartOptionsOTC.value.xAxis[0],
          ...{
            labels: {
              style: {
                color: '#757575',
                fontSize: '14px',
                fontWeight: '900',
                align: 'right',
              },
            },
            min: startTime,
            max: closeTime,
            plotLines: [
              {
                value: closeTime,
                color: '#DDDDDD',
                width: 1,
              },
            ],
          },
        });

      chartOptionsOTC.value.yAxis[0] = {
        ...chartOptionsOTC.value.yAxis[0],
        ...{
          labels: {
            formatter() {
              return this.value;
            },
            style: {
              color: '#757575',
              fontSize: '14px',
              fontWeight: '900',
              align: 'right',
            },
          },
          max: rPrevClosePrice + tkInt * 5,
          min: rPrevClosePrice - tkInt * 5,
          plotLines: [
            {
              value: prevClosePrice,
              color: '#a0a0a0',
              width: 1,
              zIndex: 5,
              dashStyle: 'Dash',
            },
          ],
          tickPositions: [
            (rPrevClosePrice - tkInt * 5).toFixed(1),
            (rPrevClosePrice - tkInt * 4).toFixed(1),
            (rPrevClosePrice - tkInt * 3).toFixed(1),
            (rPrevClosePrice - tkInt * 2).toFixed(1),
            (rPrevClosePrice - tkInt * 1).toFixed(1),
            rPrevClosePrice.toFixed(1),
            (rPrevClosePrice + tkInt * 1).toFixed(1),
            (rPrevClosePrice + tkInt * 2).toFixed(1),
            (rPrevClosePrice + tkInt * 3).toFixed(1),
            (rPrevClosePrice + tkInt * 4).toFixed(1),
            (rPrevClosePrice + tkInt * 5).toFixed(1),
          ],
        },
      };

      chartOptionsOTC.value.yAxis[1] = {
        ...chartOptionsOTC.value.yAxis[1],
        ...{
          max: valueInt,
          tickInterval: valueInt / 10,
          offset: 0,
          minPadding: 0,
          startOnTick: false,
          endOnTick: false,
          showLastLabel: true,
          labels: {
            style: {
              color: '#757575',
              fontSize: '14px',
              fontWeight: '900',
              align: 'right',
            },
            align: 'left',
            y: 4,
          },
        },
      };

      chartOptionsOTC.value.series[0] = {
        ...chartOptionsOTC.value.series[0],
        ...{
          data: cPrice,
          refprice: prevClosePrice,
        },
      };

      chartOptionsOTC.value.series[1] = {
        ...chartOptionsOTC.value.series[1],
        data: volume,
      };
      chartOptionsOTC.value.tooltip = {
        ...chartOptionsOTC.value.tooltip,
        ...{
          formatter() {
            let s = `<b>${Highcharts.dateFormat('%Y/%m/%d &nbsp; %H:%M', this.x)}</b>`;
            if (typeof otcLastTime != 'undefined' && otcLastTime.length >= 0 && otcLastTime > 0) {
              const tempTime = Highcharts.dateFormat('%H%M', this.x);
              if (otcLastTimeStamp - 43200000 === this.x && otcLastTime !== '133300') {
                const showTime = `${otcLastTime.substring(0, 2)}:${otcLastTime.substring(
                  2,
                  4
                )}:${otcLastTime.substring(4)}`;

                s = `<b>  ${Highcharts.dateFormat('%Y/%m/%d   ', this.x)} ${showTime}</b>`;
              }
            }

            let p_color = '';
            this.points.forEach((point, i) => {
              if (i === 1) {
                if (locale.value === 'zhHant') {
                  s += `<br/><span style="color:#0066a0">${point.series.name}: ${point.y}億</span>`;
                } else {
                  s += `<br/><span style="color:#0066a0">${point.series.name}: ${point.y} (100 million NTD)</span>`;
                }
              } else if (i === 0) {
                let tip_diff = (point.y - prevClosePrice).toFixed(2);
                if (point.y >= prevClosePrice) {
                  p_color = getColor(tip_diff);
                  // p_color = ${UP_COLOR};
                  tip_diff = `+${tip_diff}`;
                } else {
                  p_color = getColor(tip_diff);
                  // p_color = DOWN_COLOR;
                }
                s += `<br/><span style="color:${p_color}">${this.series.name}: ${point.y} (${tip_diff})</span>`;
              }
              return s;
            });
            return s;
          },
        },
      };
    } else {
      chart.series[0].setData(cPrice, false);
      chart.series[1].setData(volume, false);
    }
  }
};

const current = new Date(); // 獲取當前時間
const offset = current.getTimezoneOffset() / 60; // 計算時間偏移
const refreshHours = ref(parseInt(current.getHours()) + parseInt(offset)); // 計算當前小時數，加上時區偏移

// 定義交易時間範圍（以 UTC 時間表示）
const startTradingHour = 0; // 00:00 UTC
const endTradingHour = 10; // 10:00 UTC

// 交易時間 (檢查當前時間是否在交易時間範圍内)
const isTradingTime = ref(
  refreshHours.value >= startTradingHour && refreshHours.value < endTradingHour
);

// 清除所有間隔
const clearAllIntervals = () => {
  const highestIntervalId = setInterval(function () {}, 0);
  for (let i = 0; i < highestIntervalId; i++) {
    clearInterval(i);
  }
};

// 傳給外層所需資料
const getStockOTCData = () => {
  const data = {
    staticObj: stockInfo.staticObj,
    h: stockInfo.infoArray[0].h, // 指數(最高)
    l: stockInfo.infoArray[0].l, // 指數(最低)
    y: stockInfo.infoArray[0].y, // 開盤指數
    z: stockInfo.infoArray[0].z, // 當前指數
    tz: stockInfo.staticObj.tz,
    tv: stockInfo.staticObj.tv,
    tr: stockInfo.staticObj.tr,
    t1: stockInfo.staticObj.t1,
    t2: stockInfo.staticObj.t2,
    t3: stockInfo.staticObj.t3,
    t4: stockInfo.staticObj.t4,
    zHtmlStr: stockInfo.zHtmlStr, // 漲跌百分比
    pColor: stockInfo.pColor, // 漲跌百分比顏色
  };
  return data;
};

// 監聽語言切換
watch(
  () => locale.value,
  () => {
    updateSeriesName();
  }
);

// // 更新走勢圖名稱
function updateSeriesName() {
  chartOptionsOTC.value.series[0].name = t('CHART_INDEX_GTSM');
  chartOptionsOTC.value.series[1].name = t('INDEX_VALUE');
}

updateSeriesName();
</script>

<template>
  <div
    v-if="showMessage"
    class="alert-message"
  >
    {{ message }}
  </div>

  <!-- 上櫃股票大盤走勢圖 -->
  <div
    id="chart-otc"
    class="w-50 mx-3 my-3 bg-white px-3 py-3 border rounded-3"
    @wheel="handleWheel"
  >
    <div class="d-flex justify-content-between fw-bold">
      <p class="mx-2">{{ t('INDEX_OTC_CHART') }}</p>
      <span class="me-2">{{ time }}</span>
    </div>
    <charts
      ref="chartRef"
      :options="chartOptionsOTC"
      class="chart"
    ></charts>
  </div>
</template>

<style scoped>
.alert-message {
  color: yellow;
  background-color: #ce0000;
  top: 1;
  z-index: 100;
  padding: 10px;
  font-weight: bold;
  font-size: 18px;
}
.chart {
  width: auto;
  height: 375px;
}
</style>
