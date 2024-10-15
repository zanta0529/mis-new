<script setup>
const handleWheel = (event) => {
  event.preventDefault();
  const chartContainer = document.getElementById('chart-www');
  chartContainer.style.pointerEvents = 'none';
  setTimeout(() => {
    chartContainer.style.pointerEvents = 'auto';
  }, 10);
};

const { t, locale } = useI18n();
let closeTime = '';
let startTime = '';
let chart;
const upcolor = '#ff0000';
const downcolor = '#1f8f1f';
let exeYear = '';
let exeMonth = '';
let exeDay = '';
let refreshId = -1;
let firstRefreshId = -1;
let timezoneOffset = 0; // 時區偏移量
let hourOffset = 0; // 小時偏移量
let msecOffset = 0; // 毫秒偏移量
const chartRef = ref(null);

StockModule(Highcharts);
Highcharts.setOptions({
  global: {
    useUTC: false,
  },
});

const chartWWW = ref({
  chart: {
    animation: false,
    renderTo: 'wwwChart',
    alignTicks: true,
    plotBorderColor: '#000000',
    plotBorderWidth: 1,
    zoomType: '',
    style: {
      fontFamily: 'Times New Roman',
    },
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
    enabled: false,
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
  plotOptions: {
    area: {
      color: '#4b32f9',
      fillOpacity: 0.6,
      lineWidth: 1,
      states: {
        hover: {
          enabled: false,
        },
      },
    },
    column: {
      animation: false,
      color: 'red',
      grouping: false,
    },
    line: {
      animation: false,
      grouping: false,
      color: '#ff0000',
      lineWidth: 1,
      states: {
        hover: {
          enabled: false,
        },
      },
    },
  },
  xAxis: {
    type: 'datetime',
    tickInterval: 60 * 60 * 1000,
    minTickInterval: 60 * 60 * 1000,
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
        fontSize: '11px',
        color: '#000000',
      },
      y: 15,
    },
    tickLength: 5,
  },
  yAxis: [
    {
      allowDecimals: true,
      gridLineColor: '#E4E4E4',
      title: {
        text: '',
      },
      startOnTick: false,
      labels: {
        x: 2,
        style: {
          fontFamily: 'Tahoma, Verdana, Arial, Helvetica, sans-serif',
          color: '#000000',
          fontSize: '11px',
        },
      },
      opposite: true,
    },
    {
      labels: {
        x: -2,
        style: {
          fontFamily: 'Tahoma, Verdana, Arial, Helvetica, sans-serif',
          color: '#000000',
          fontSize: '11px',
        },
      },
      gridLineColor: '#E4E4E4',
      gridLineWidth: 0,
      startOnTick: true,
      showLastLabel: true,
      title: {
        rotation: 0,
        align: 'high',
        text: '',
      },
    },
  ],
  series: [
    {
      type: 'area',
      name: '成交價',
      color: {
        linearGradient: { x1: 0, x2: 1, y1: 0, y2: 1 },
        stops: [
          [0, '#0000C7'],
          [1, '#038BE688'],
        ],
      },
      colorup: '#e05e5e',
      colordown: '#2c812d',
      zIndex: 1,
      lineWidth: 1.5,
      dataGrouping: {
        enabled: false,
      },
      marker: {
        enabled: false,
      },
    },
    {
      type: 'line',
      name: '成交價',
      lineWidth: 1,
      yAxis: 1,
      dataGrouping: {
        enabled: false,
      },
      marker: {
        enabled: false,
      },
    },
  ],
});

let unitStr = ' (億)';
const TWSE_TXT1 = ref('加權指數:');
const HIGH_TXT1 = ref('Highest:');
const LOW_TXT1 = ref('Lowest:');
const VALUE_TXT = ref('成交金額:');
const note = ref('成交金額不含零股、鉅額、盤後定價、拍賣及標購。');

onMounted(async () => {
  timezoneOffset = new Date().getTimezoneOffset() + 480;
  hourOffset = timezoneOffset / 60;
  msecOffset = hourOffset * 60 * 60 * 1000;

  const langParam = new URLSearchParams(window.location.search).get('lang');
  const defaultLang = 'zhHant';
  if (langParam === 'en') {
    locale.value = 'en';
  } else if (langParam === 'jp') {
    locale.value = 'jp';
  } else {
    locale.value = defaultLang;
  }

  firstRefreshId = setInterval(() => {
    loadStockInfo1();
  }, 5000);

  loadStockInfo1();
  updateTextContent();
});

const updateTextContent = () => {
  if (locale.value === 'en') {
    TWSE_TXT1.value = 'TAIEX:';
    HIGH_TXT1.value = 'Highest:';
    LOW_TXT1.value = 'Lowest:';
    VALUE_TXT.value = 'Value';
    unitStr = ' (100 million NTD)';
    note.value =
      'Odd-lot, Block, Off-hour, Auction and Tender Offer trading are not included in Trade Volume.';
  } else if (locale.value === 'jp') {
    TWSE_TXT1.value = '加権指数:';
    HIGH_TXT1.value = '最高値:';
    LOW_TXT1.value = '最安値:';
    VALUE_TXT.value = '売買代金';
    unitStr = ' (億台湾ドル)';
    note.value =
      '売買代金には端株、時間外終値、ブロック、競売買および買い入札の各取引を含みません。';
  }
};

const loadStockInfo1 = async () => {
  const data = await getMisOhlcWWW();
  if (data.rtcode == '0000') {
    let result;
    if (typeof data.infoArray != 'undefined' && data.infoArray.length > 0) {
      exeYear = data.infoArray[0].d.substring(0, 4);
      exeMonth = data.infoArray[0].d.substring(4, 6) - 1;
      exeDay = data.infoArray[0].d.substring(6);
      result = initInfoArray(data.infoArray);

      if (!result) {
        return;
      }
    }

    if (data.ohlcArray) {
      const { upper, lower, yesPrice } = result;
      loadOhlcArray(data.ohlcArray, upper, lower, yesPrice);
    }

    if (refreshId == -1) {
      clearInterval(firstRefreshId);
      refreshId = setInterval(() => {
        loadStockInfo1();
      }, 15000);
    }
  }
};

const loadOhlcArray = (ohlcArray, upper, lower, yesPrice) => {
  const ohlc = [];
  const cPrice = [];
  const valumeArr = [];
  const dataLength = ohlcArray.length;
  let maxValume = 0;

  let i = 0;
  for (i = 0; i < dataLength; i += 1) {
    const currTime = parseInt(ohlcArray[i].t * 100000, 10) + msecOffset;
    if (i == 0) {
      const dtB = new Date(exeYear, exeMonth, exeDay, 9, 0, 0);
      cPrice.push([dtB.getTime(), yesPrice * 1]);
      valumeArr.push([dtB.getTime(), 0]);
    }

    if (closeTime == '' && i == dataLength - 1) {
      const dt = new Date();
      dt.setTime(currTime);
      const dtB = new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 13, 35, 0);
      closeTime = dtB.getTime();
    }

    if (ohlcArray[i].s >= 0) {
      cPrice.push([currTime, ohlcArray[i].c * 1]);
      valumeArr.push([currTime, ohlcArray[i].s * 1]);

      if (ohlcArray[i].s > maxValume) {
        maxValume = ohlcArray[i].s * 1;
      }
    }
  }

  const dtB = new Date(exeYear, exeMonth, exeDay, 13, 32, 0);
  closeTime = dtB.getTime();

  const dtA = new Date(exeYear, exeMonth, exeDay, 8, 59, 0);
  startTime = dtA.getTime();
  loadOhcl(cPrice, upper, lower, maxValume, yesPrice, valumeArr);
};

const time = ref('');
const h = ref('');
const l = ref('');
const tv = ref('');
const d = ref('');
const p = ref('');
const i = ref('');
const dColor = ref('');
const pColor = ref('');

const initInfoArray = (infoArr) => {
  let upper = 0;
  let lower = 0;
  let yesPrice = 0;
  infoArr.forEach((item, index) => {
    const cStr = item.c;

    if (item.t.includes('13:33') || item.t.includes('13:31')) {
      let dateStr = item.d;
      dateStr = `${dateStr.substring(0, 4)}/${dateStr.substring(4, 6)}/${dateStr.substring(6)}`;
      time.value = dateStr;
    } else if (
      item.t.includes('07:50:00') ||
      item.t.includes('07:59:00') ||
      item.t.startsWith('07:')
    ) {
      time.value = '00:00:00';
    } else {
      time.value = item.t;
    }

    if (item.y !== '-' && item.z !== '-') {
      upper = item.u || item.h;
      lower = item.w || item.l;
      yesPrice = item.y;

      const diffStr = (item.z - item.y).toFixed(2);
      const preStr = ((diffStr / item.y) * 100).toFixed(2);

      h.value = item.h;
      l.value = item.l;

      if (item.v > 0) {
        let strTv = (item.v / 100).toString();
        const diffCount = strTv.length - strTv.indexOf('.');

        if (diffCount == 2) {
          strTv += '0';
        } else if (diffCount == strTv.length + 1) {
          strTv += '.00';
        }

        tv.value = strTv + unitStr;
      } else {
        tv.value = '0.0';
      }
      if (item.z) {
        if (item.z * 1 < item.y * 1) {
          dColor.value = downcolor;
          pColor.value = downcolor;
          i.value = item.z;
          d.value = `${diffStr}`;
          p.value = `${preStr}%`;
        } else if (item.z * 1 > item.y * 1) {
          dColor.value = upcolor;
          pColor.value = upcolor;
          i.value = item.z;
          d.value = `+${diffStr}`;
          p.value = `+${preStr}%`;
        } else if (item.z === item.y) {
          dColor.value = '#0000ff';
          pColor.value = '#0000ff';
          i.value = item.z;
        }
      } else {
        h.value = '0.0';
        l.value = '0.0';
        i.value = item.y;
        d.value = '0.0';
        p.value = '0.0%';
      }
    } else {
      h.value = '0.0';
      l.value = '0.0';
      i.value = item.y;
      d.value = '0.0';
      p.value = '0.0%';
    }
  });

  return { upper, lower, yesPrice };
};

const loadOhcl = (cPrice, upper, lower, maxValume, yesPrice, valumeArr) => {
  let gap = parseInt(yesPrice * 0.015, 10);
  let inputMaxValue = maxValume;
  const upGap = (upper - yesPrice) * 1.2;
  const lowGap = (yesPrice - lower) * 1.2;
  let upperPrice = Math.round(yesPrice * 1.015);
  let lowerPrice = Math.round(yesPrice * 0.985);
  let tkInt = (upperPrice - lowerPrice) / 4;

  if (inputMaxValue == 0) {
    inputMaxValue = 4000;
  }

  if (upGap > gap || lowGap > gap) {
    gap = Math.max(upGap, lowGap);
    upperPrice = Math.round(parseFloat(yesPrice) + parseFloat(gap));
    lowerPrice = parseInt(yesPrice - gap, 10);
    tkInt = (upperPrice - lowerPrice) / 4;
    if (chart) {
      chart = null; // 重置 chart
    }
  }

  if (chart && maxValume > chart.yAxis[0].max) {
    chart.yAxis[0].max = maxValume;
    chart = null;
  }

  if (!chart) {
    chartWWW.value.xAxis[0] = {
      ...chartWWW.value.xAxis[0],
      ...{
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
    };
    chartWWW.value.yAxis[0] = {
      ...chartWWW.value.yAxis[0],
      ...{
        labels: {
          formatter() {
            return `${parseInt(this.value / 100, 10)}`;
          },
        },
        lineWidth: 1,

        max: inputMaxValue,
        tickInterval: inputMaxValue / 4,
        // tickInterval: Math.ceil((maxValume * 1.1) / 10),
      },
    };
    chartWWW.value.yAxis[1] = {
      ...chartWWW.value.yAxis[1],
      ...{
        min: lowerPrice,
        max: upperPrice,
        tickInterval: tkInt,
        labels: {
          formatter() {
            return this.value;
          },
        },
        tickPositions: [
          lowerPrice,
          Math.round(lowerPrice + tkInt * 1),
          Math.round(yesPrice),
          Math.round(lowerPrice + tkInt * 3),
          upperPrice,
        ],
        plotLines: [
          // 中間線
          {
            value: yesPrice,
            color: 'blue',
            dashStyle: 'shortdash',
            width: 1,
            zIndex: 3,
          },
        ],
      },
    };

    chartWWW.value.series[0] = {
      ...chartWWW.value.series[0],
      ...{
        data: valumeArr,
      },
    };
    chartWWW.value.series[1] = {
      ...chartWWW.value.series[1],
      ...{
        data: cPrice,
      },
    };
  } else {
    chart.series[0].setData(cPrice, false);
  }
};

watch(
  () => locale.value,
  () => {
    updateTextContent();
  }
);
</script>

<template>
  <div class="container-fluid">
    <div class="d-flex justify-content-center w-100">
      <div
        id="chart-www"
        style="min-width: 425px"
        class="bg-white"
        @wheel="handleWheel"
      >
        <charts
          ref="chartRef"
          :options="chartWWW"
          class="chart"
        ></charts>

        <div class="position-relative">
          <div
            class="position-absolute px-2"
            style="top: -10px; left: 50%; transform: translateX(-50%); width: 100%"
          >
            <div class="d-flex justify-content-between fs-11 fw-medium">
              <p class="mb-0">{{ time }}</p>
              <p class="mb-0">{{ TWSE_TXT1 }} {{ i }}</p>
              <p
                class="mb-0"
                :style="{ color: dColor }"
              >
                {{ d }}
              </p>
              <p
                class="mb-0"
                :style="{ color: pColor }"
              >
                {{ p }}
              </p>
              <p class="mb-0">{{ VALUE_TXT }} {{ tv }}</p>
            </div>
            <div class="text-center fs-11">
              <p class="mb-0 fw-medium">{{ note }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chart {
  width: 425px;
  height: 140px;
}
</style>
