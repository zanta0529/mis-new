<script setup>
const handleWheel = (event) => {
  event.preventDefault();
  const chartContainer = document.getElementById('chart-ix0126');
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
const chartOptionsTSE = ref({
  chart: {
    animation: false,
    renderTo: 'ChartMisOhlcTSE',
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
      name: '臺股期貨指數',
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
  await initIX0126Data();
  emit('stockInfoData', 'ix0126', getStockIX0126Data());
  // ----------------------------目前進度------------------------------

  // 間隔ID
  const intervalId = setInterval(() => {
    if (!isTradingTime.value) {
      clearAllIntervals();
    }
  }, 5 * 1000); // 每5秒觸發一次
});

// getMisOhlcIX0126----------------------------------------------------------
const stockInfo = reactive({
  staticObj: [],
  ohlcArray: [],
  indexData: [],
  amountData: [],
  infoArray: [],
  zHtmlStr: '',
  pColor: '',
});
const dateTime = ref('');
const time = ref('');
const refreshTseId = ref(-1);
const userDelay = ref(-1);
let startTime = '';
let closeTime = '';

const initIX0126Data = async () => {
  const data = await getMisOhlcIX0126();
  stockInfo.staticObj = data.staticObj;
  stockInfo.ohlcArray = data.ohlcArray;
  stockInfo.indexData = data.ohlcArray.map((item) => [parseInt(item.t), parseInt(item.c)]);
  stockInfo.amountData = data.ohlcArray.map((item) => [parseInt(item.t), parseInt(item.s)]);
  stockInfo.infoArray = data.infoArray;
  dateTime.value = data.infoArray[0].d;
  time.value = data.infoArray[0].t;

  chartOptionsTSE.value.series[0].data = stockInfo.indexData;
  chartOptionsTSE.value.series[1].data = stockInfo.amountData;
  if (data.rtcode == '0000') {
    if (typeof data.infoArray !== 'undefined' && data.infoArray.length > 0) {
      const exeYear = data.infoArray[0].d.substring(0, 4);
      const exeMonth = data.infoArray[0].d.substring(4, 6) - 1;
      const exeDay = data.infoArray[0].d.substring(6);

      const dtB = new Date(exeYear, exeMonth, exeDay, 8, 44, 0);
      closeTime = dtB.getTime();

      const dtA = new Date(exeYear, exeMonth, exeDay, 13, 45, 0);
      startTime = dtA.getTime();
      procIX0126IndexPage(data);
    }
    if (refreshTseId.value === -1) {
      let userDelay = 15000;
      if (typeof data.userDelay !== 'undefined') {
        userDelay = data.userDelay;
      }

      refreshTseId.value = setInterval(() => {
        initIX0126Data();
      }, userDelay);
    }
  }
};
const getColor = (diff) => {
  const isUpper = diff > 0;
  const isZhHant = locale.value === 'zhHant';
  if (isUpper) {
    return isZhHant ? '#e05e5e' : '#2c812d';
  }
  return isZhHant ? '#2c812d' : '#e05e5e';
};

const procIX0126IndexPage = (data) => {
  const item = data.infoArray[0];
  const channel = `${item.ex}_${item.ch}`;
  const d0 = item.d;
  let upper = item.u;
  let lower = item.w;
  const prevClosePrice = item.y;
  // var ch = "#INDEX_TX";

  if (typeof item.z != 'undefined') {
    if (item.z.length > 0) {
      let zHtmlStr = item.z;
      if (item.y != '-' && item.z != '-' && item.y != '' && item.z != '') {
        let diff = (item.z - item.y).toFixed(2);
        const diffPre = (Math.round((diff / item.y) * 10000) / 100).toFixed(2);
        let newColor = '#000000';
        diff = diff > 0 ? `+${diff}` : diff;
        newColor = getColor(diff);
        zHtmlStr = `<font color=${newColor}>(${diff}/${diffPre}%)</font>`;
        stockInfo.pColor = newColor;
        stockInfo.zHtmlStr = `(${diff} / ${diffPre} %)`;
      }
    }
  }
  if (upper == undefined) {
    upper = item.h;
  }
  if (lower == undefined) {
    lower = item.l;
  }
  loadChartIX0126(channel, d0, upper * 1, lower * 1, prevClosePrice * 1, data);
};
// 時間----------------------------------------------------------
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
const otcLastTime = 0;
const otcLastTimeStamp = 0;

const loadChartIX0126 = (channel, d0, upper, lower, prevClosePrice, data) => {
  upper = upper < prevClosePrice ? prevClosePrice : upper;
  lower = lower > prevClosePrice ? prevClosePrice : lower;

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
      if (!(hours == 8 && minutes == 45)) {
        date.setMinutes(45);
        date.setHours(8);
        cPrice.push([
          date.getTime(), // currentTime, // the date
          prevClosePrice * 1, // the volume
        ]);
      }
    }
    if (dataLength - 1 === i) {
      tseLastTime = data.ohlcArray[i].ts;
      tseLastTimeStamp = data.ohlcArray[i].t;
    }
    if (data.ohlcArray[i].s >= 0) {
      cPrice.push([
        currTime, // currentTime, // the date
        data.ohlcArray[i].c * 1, // the volume
      ]);
      if (data.ohlcArray[i].s > maxValue) {
        maxValue = data.ohlcArray[i].s * 1;
      }
      volume.push([
        currTime, // currentTime, // the date
        data.ohlcArray[i].s * 1, // the volume
      ]);
    }
  }
  loadOhclIX0126(channel, cPrice, volume, upper, lower, maxValue, prevClosePrice);
};
//----------------------------------------------------------
const loadOhclIX0126 = (channel, cPrice, volume, upper, lower, maxValue, prevClosePrice) => {
  let gap = parseInt(prevClosePrice * 0.015);

  const upGap = upper - prevClosePrice;
  const lowGap = prevClosePrice - lower;

  let valueInt = 1;
  var tkInt = parseInt((upper + 10 - lower) / 10);
  chartChannel = channel;

  const rPrevClosePrice = Math.round(prevClosePrice / 10) * 10;
  var tkInt = 10;
  let vGap = 0;
  if (upper - rPrevClosePrice > rPrevClosePrice - lower) {
    vGap = upper - rPrevClosePrice;
  } else {
    vGap = rPrevClosePrice - lower;
  }

  if (vGap < 20) {
    tkInt = 5;
  } else {
    for (let i = 1; i < 100; i++) {
      if (vGap * 1.05 < 25 * i) {
        tkInt = 5 * i;
        break;
      }
    }
  }

  valueInt *= 100;

  if (upGap > gap || lowGap > gap) {
    gap = lowGap;
    if (upGap > lowGap) {
      gap = upGap;
    }
    if (chart != undefined) {
      chart = undefined;
    }
  }
  if (chart != undefined && maxValue > chart.yAxis[1].max) {
    chart.yAxis[1].max = maxValue;
    if (chart != undefined) {
      chart = undefined;
    }
  }

  if (1 == 1) {
    const upper0 = parseInt(prevClosePrice) + gap;
    const lower0 = parseInt(prevClosePrice - gap);
    if (upper > upper0) {
      upper = Math.ceil(upper);
      lower = Math.ceil(prevClosePrice - (upper - prevClosePrice));
    } else if (lower < lower0) {
      lower = Math.floor(lower);
      upper = Math.ceil(prevClosePrice + (prevClosePrice - lower));
    } else {
      upper = upper0;
      lower = lower0;
    }
    (chartOptionsTSE.value.chart = {
      ...chartOptionsTSE.value.chart,
      ...{
        zoomType: '',
      },
    }),
      (chartOptionsTSE.value.xAxis[0] = {
        ...chartOptionsTSE.value.xAxis[0],
        ...{
          labels: {
            style: {
              color: '#757575',
              fontSize: '14px',
              fontWeight: '900',
              align: 'right',
            },
          },
          min: closeTime,
          max: startTime,
          plotLines: [
            {
              value: startTime,
              color: '#DDDDDD',
              width: 1,
            },
            {
              value: closeTime,
              color: '#DDDDDD',
              width: 1,
            },
          ],
        },
      });
    chartOptionsTSE.value.yAxis[0] = {
      ...chartOptionsTSE.value.yAxis[0],
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
          rPrevClosePrice - tkInt * 5,
          rPrevClosePrice - tkInt * 4,
          rPrevClosePrice - tkInt * 3,
          rPrevClosePrice - tkInt * 2,
          rPrevClosePrice - tkInt * 1,
          rPrevClosePrice,
          rPrevClosePrice + tkInt * 1,
          rPrevClosePrice + tkInt * 2,
          rPrevClosePrice + tkInt * 3,
          rPrevClosePrice + tkInt * 4,
          rPrevClosePrice + tkInt * 5,
        ],
      },
    };

    chartOptionsTSE.value.yAxis[1] = {
      ...chartOptionsTSE.value.yAxis[1],
      ...{
        formatter() {
          return this.value;
        },
        max: valueInt * 10,
        tickInterval: valueInt,
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
          // 358 + 1,
          y: 4,
        },
      },
    };

    chartOptionsTSE.value.series[0] = {
      ...chartOptionsTSE.value.series[0],
      ...{
        data: cPrice,
        refprice: prevClosePrice,
      },
    };

    chartOptionsTSE.value.series[1] = {
      ...chartOptionsTSE.value.series[1],
      data: volume,
    };
    chartOptionsTSE.value.tooltip = {
      ...chartOptionsTSE.value.tooltip,
      ...{
        formatter() {
          let s = `<b>${Highcharts.dateFormat('%Y/%m/%d &nbsp; %H:%M', this.x)}</b>`;
          if (typeof tseLastTime != 'undefined' && tseLastTime.length > 0 && tseLastTimeStamp > 0) {
            const tempTime = Highcharts.dateFormat('%H%M', this.x);
            if (tseLastTimeStamp === this.x && tseLastTime === '134500') {
              const showTime = `${tseLastTime.substring(0, 2)}:${tseLastTime.substring(
                2,
                4
              )}:${tseLastTime.substring(4)}`;
              s = `<b>  ${Highcharts.dateFormat('%Y/%m/%d   ', this.x)}  ${showTime}</b>`;
            }
          }

          let p_color = '';
          this.points.forEach((point, i) => {
            if (i === 1) {
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
const getStockIX0126Data = () => {
  const data = {
    title: 'INDEX_CHART',
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
  chartOptionsTSE.value.series[0].name = t('INDEX_CHART');
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

  <!-- 臺股期貨指數 走勢圖 -->
  <div
    id="chart-ix0126"
    class="w-50 mx-3 my-3 bg-white px-3 py-3 border rounded-3"
    @wheel="handleWheel"
  >
    <div class="d-flex justify-content-between fw-bold">
      <p class="mx-2">{{ t('INDEX_CHART') }} {{ t('MENU_CHART') }}</p>
      <span class="me-2">{{ time }}</span>
    </div>
    <charts
      ref="chartRef"
      :options="chartOptionsTSE"
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
