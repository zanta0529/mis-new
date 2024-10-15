<script setup>
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  showGoBack: true,
});

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

const scrollToTable = (id) => {
  const element = document.getElementById(id);

  if (element) {
    element.scrollIntoView({
      block: 'start',
      behavior: 'smooth',
    });
  }
};

const newData = ref({});
const route = useRoute();
const router = useRouter();

let tabType = '1';
const changeTabType = async (value) => {
  if (value === '1') {
    tabType = value;
    router.push(`/various-areas/etf-price/indicator-disclosure-etf`);
    await fetchData();
  } else {
    router.push(`/various-areas/etf-price/value-disclosure-etf`);
  }
};
let nIntervId;
const updateDataPeriodically = () => {
  nIntervId = setInterval(() => {
    fetchData();
  }, 15000);
};

onMounted(async () => {
  await updateDetailDataInStore();
  await fetchData();
  await updateDataPeriodically();
});

onUnmounted(() => {
  clearInterval(nIntervId);
});

// 表頭資料
const theadData = computed(() => {
  return [
    { name: t('ALL_ETF_ID'), key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('ALL_ETF_ISSUED'), key: 'c', mark: '1', width: '12%', theadAlignment: 'end' },
    { name: t('ALL_ETF_DIFF_N'), key: 'd', width: '12%', theadAlignment: 'end' },
    { name: t('ALL_ETF_LAST_PRICE'), key: 'e', width: '8%', theadAlignment: 'end' },
    { name: t('ALL_ETF_NAV'), key: 'f', mark: '2', width: '8%', theadAlignment: 'end' },
    { name: t('ALL_ETF_NAV_DIFF'), key: 'g', mark: '3', width: '8%', theadAlignment: 'end' },
    { name: t('ALL_ETF_LAST_NAV'), key: 'h', mark: '4', width: '9%', theadAlignment: 'end' },
    { name: t('ALL_ETF_LINK'), key: '', width: '7.5%', theadAlignment: 'end' },
    { name: t('ALL_ETF_TIME'), key: '', width: '15%', theadAlignment: 'end' },
  ];
});
// 表一 國內成分證券ETF 新台幣交易
const DOMESTIC_SECURITIES_ETF_TWD = computed(() => {
  if (!newData.value[1]) return [];

  return newData.value[1].filter((item) => {
    return !['K', 'M', 'V', 'C'].includes(item.a[item.a.length - 1]);
  });
});

// 表二 標的指數或商品位於亞洲時區之ETF 新台幣交易
const ASIA_ETF_TWD = computed(() => {
  if (!newData.value[2]) return [];

  return newData.value[2].filter((item) => {
    return !['K', 'M', 'V', 'C'].includes(item.a[item.a.length - 1]);
  });
});

// 表三 標的指數或商品位於亞洲時區之ETF 外幣交易
const ASIAN_ETF_FOREIGN_CURRENCY = computed(() => {
  if (!newData.value[2]) return [];

  return newData.value[2].filter((item) => {
    return ['K', 'M', 'V', 'C'].includes(item.a[item.a.length - 1]);
  });
});

// 表四 標的指數或商品位於歐美時區之ETF 新台幣交易
const EUROPE_AMERICA_ETF_TWD = computed(() => {
  if (!newData.value[3]) return [];

  return newData.value[3].filter((item) => {
    return !['K', 'M', 'V', 'C'].includes(item.a[item.a.length - 1]);
  });
});

// 表五 標的指數或商品位於歐美時區之ETF 外幣交易
const EUROPE_AMERICA_ETF_FOREIGN_CURRENCY = computed(() => {
  if (!newData.value[3]) return [];

  return newData.value[3].filter((item) => {
    return ['K', 'M', 'V', 'C'].includes(item.a[item.a.length - 1]);
  });
});

// 表六 全球時區ETF 台幣
const GLOBAL_ETF_TWD = computed(() => {
  if (!newData.value[4]) return [];

  return newData.value[4].filter((item) => {
    return !['K', 'M', 'V', 'C'].includes(item.a[item.a.length - 1]);
  });
});

// 表七 全球時區ETF 外幣
const GLOBAL_ETF_FOREIGN_CURRENCY = computed(() => {
  if (!newData.value[4]) return [];

  return newData.value[4].filter((item) => {
    return ['K', 'M', 'V', 'C'].includes(item.a[item.a.length - 1]);
  });
});

// 成交價、發行人預估指標價值、預估折溢價幅度、前一營業日指標價值，回傳的資料不一定是小數點後兩位，有可能超過，所以要做格式化
const formatDetail = (val) => {
  const valueStr = val.toString();
  let formatValue = valueStr;

  if (valueStr.includes('.')) {
    const [integerPart, decimalPart] = valueStr.split('.');
    if (decimalPart.length > 2) {
      formatValue = `${integerPart}.${decimalPart.substring(0, 2)}`;
    }
  }
  return formatValue;
};

const fetchData = async () => {
  try {
    const idArr = [];
    const tableData = [];

    const categoryRes = await getCategory('tse', 'B0');
    if (categoryRes.msgArray) {
      categoryRes.msgArray.forEach((element) => {
        idArr.push({
          id: element.ch.substring(0, element.ch.indexOf('.')),
          name: element.n,
          ex: element.ex,
          ch: element.ch,
        });
      });
    }

    const etfDataRes = await getETFData();
    if (etfDataRes.a1) {
      etfDataRes.a1.forEach((e) => {
        if (e.msgArray) {
          e.msgArray.forEach((originalDetail) => {
            const formattedDetail = {
              ...originalDetail,
              e: formatDetail(originalDetail.e),
              f: formatDetail(originalDetail.f),
              g: formatDetail(originalDetail.g),
              h: formatDetail(originalDetail.h),
            };

            const foundCategory = idArr.find((category) => category.id === formattedDetail.a);
            if (foundCategory) {
              const detail = {
                ...formattedDetail,
                n: foundCategory.name,
                ex: foundCategory.ex,
                ch: foundCategory.ch,
                refURL: e.refURL,
              };
              tableData.push(detail);
            }
          });
        }
      });
    }

    // 整理表格資料
    const tmp = {};
    idArr.forEach((e) => {
      const pushData = tableData.filter((item) => item.a === e.id);
      if (pushData.length > 0) {
        if (!tmp[pushData[0].k]) {
          tmp[pushData[0].k] = [];
        }
        tmp[pushData[0].k].push(pushData[0]);
      }
    });
    newData.value = tmp;
    console.log(newData.value);
  } catch (error) {
    console.error('Error fetching data:', error.message);
  }
};
useWatchLang(fetchData);

const formatDateString = (yyyymmdd) => {
  if (!yyyymmdd) return '';
  const year = yyyymmdd.substring(0, 4);
  const month = yyyymmdd.substring(4, 6);
  const day = yyyymmdd.substring(6, 8);
  return `${year}/${month}/${day}`;
};
</script>

<template>
  <div>
    <div class="d-flex justify-content-between">
      <p class="fs-2 text-black fw-medium">{{ t('ALL_ETF_TITLE') }}</p>
      <ul class="nav nav-pills mb-3 p-0 justify-content-end col-3">
        <li class="nav-item">
          <button
            id="tse-tab"
            class="nav-link active nav-left"
            data-bs-toggle="pill"
            data-bs-target="#pills-home"
            type="button"
            role="tab"
            aria-controls="pills-home"
            aria-selected="true"
            @click="changeTabType('1')"
          >
            {{ t('ALL_ETF_TITLE_MK1') }}
          </button>
        </li>
        <li class="nav-item">
          <button
            id="odd-tab"
            class="nav-link nav-right"
            data-bs-toggle="pill"
            data-bs-target="#pills-profile"
            type="button"
            role="tab"
            aria-controls="pills-profile"
            aria-selected="false"
            @click="changeTabType('2')"
          >
            {{ t('ALL_ETF_TITLE_MK2') }}
          </button>
        </li>
      </ul>
    </div>
    <div class="rounded-4 bg-white border px-13 py-4 text-black my-3 fs-14">
      <ol
        style="counter-reset: my-counter; letter-spacing: 1.23px; padding-left: 0; margin-bottom: 0"
      >
        <li style="text-align: justify">
          <span class="text-custom-red fw-bold">{{ $t('ALL_ETF_NOTE') }}</span>
          <span>{{ $t('ALL_ETF_NOTE_O') }}</span>
        </li>
        <li style="text-align: justify">
          <span>{{ t('GROUP_NOTE_FOR_POSTPONED5') }}</span>
        </li>
      </ol>
    </div>
    <div class="w-100">
      <button
        type="button"
        class="btn btn-primary px-4 me-3 mb-3"
        @click="scrollToTable('domestic')"
      >
        {{ t('ALL_ETF_TYPE1') }}-{{ t('NEW_TAIWAN_DOLLAR') }}
      </button>
      <button
        type="button"
        class="btn btn-primary px-4 me-3 mb-3"
        @click="scrollToTable('asia')"
      >
        {{ t('ALL_ETF_TYPE2') }}-{{ t('NEW_TAIWAN_DOLLAR') }}
      </button>
      <button
        type="button"
        class="btn btn-primary px-4 me-3 mb-3"
        @click="scrollToTable('E&A')"
      >
        {{ t('ALL_ETF_TYPE3') }}-{{ t('NEW_TAIWAN_DOLLAR') }}
      </button>
      <button
        type="button"
        class="btn btn-primary px-4 me-3 mb-3"
        @click="scrollToTable('worldwide')"
      >
        {{ t('ALL_ETF_TYPE4') }}-{{ t('NEW_TAIWAN_DOLLAR') }}
      </button>
    </div>
    <div class="w-100">
      <button
        type="button"
        class="btn btn-foreign-currency px-4 me-3 mb-3"
        @click="scrollToTable('asiaForeignCurrency')"
      >
        {{ t('ALL_ETF_TYPE2') }}-{{ t('FOREIGN_CURRENCY') }}
      </button>
      <button
        type="button"
        class="btn btn-foreign-currency px-4 me-3 mb-3"
        @click="scrollToTable('E&AforeignCurrency')"
      >
        {{ t('ALL_ETF_TYPE3') }}-{{ t('FOREIGN_CURRENCY') }}
      </button>
      <button
        type="button"
        class="btn btn-foreign-currency px-4 me-3 mb-3"
        @click="scrollToTable('worldwideForeignCurrency')"
      >
        {{ t('ALL_ETF_TYPE4') }}-{{ t('FOREIGN_CURRENCY') }}
      </button>
    </div>
    <IndicatorDisclosureTable
      id="domestic"
      :thead-data="theadData"
      :table-data="DOMESTIC_SECURITIES_ETF_TWD"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :title="`${t('ALL_ETF_TYPE1')}-${t('NEW_TAIWAN_DOLLAR')}`"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETF_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
                :title="`${row.a} / ${row.n}`"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETF_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETF_LINK')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETF_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETF_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <!--  表二 標的指數或商品位於亞洲時區之ETF 新台幣交易-->
    <!-- ASIA_ETF_TWD -->
    <IndicatorDisclosureTable
      id="asia"
      :thead-data="theadData"
      :table-data="ASIA_ETF_TWD"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :title="`${t('ALL_ETF_TYPE2')}-${t('NEW_TAIWAN_DOLLAR')}`"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETF_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETF_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETF_LINK')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETF_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETF_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <IndicatorDisclosureTable
      id="asiaForeignCurrency"
      :thead-data="theadData"
      :table-data="ASIAN_ETF_FOREIGN_CURRENCY"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :title="`${t('ALL_ETF_TYPE2')}-${t('FOREIGN_CURRENCY')}`"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETF_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETF_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETF_LINK')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETF_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETF_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>
    <IndicatorDisclosureTable
      id="E&A"
      :thead-data="theadData"
      :table-data="EUROPE_AMERICA_ETF_TWD"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :title="`${t('ALL_ETF_TYPE3')}-${t('NEW_TAIWAN_DOLLAR')}`"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETF_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETF_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETF_LINK')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETF_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETF_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>
    <IndicatorDisclosureTable
      id="E&AforeignCurrency"
      :thead-data="theadData"
      :table-data="EUROPE_AMERICA_ETF_FOREIGN_CURRENCY"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :title="`${t('ALL_ETF_TYPE3')}-${t('FOREIGN_CURRENCY')}`"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETF_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETF_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETF_LINK')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETF_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETF_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>
    <IndicatorDisclosureTable
      id="worldwide"
      :thead-data="theadData"
      :table-data="GLOBAL_ETF_TWD"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :title="`${t('ALL_ETF_TYPE4')}-${t('NEW_TAIWAN_DOLLAR')}`"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETF_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETF_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETF_LINK')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETF_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETF_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <IndicatorDisclosureTable
      id="worldwideForeignCurrency"
      :thead-data="theadData"
      :table-data="GLOBAL_ETF_FOREIGN_CURRENCY"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :title="`${t('ALL_ETF_TYPE4')}-${t('FOREIGN_CURRENCY')}`"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETF_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETF_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETF_LINK')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETF_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETF_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <!-- 備註 -->
    <manual-table id="mark">
      <manual-table-item mark>
        {{ t('ALL_ETF_MEMO1_STR') }}
      </manual-table-item>
      <manual-table-item mark>
        <div>
          {{ t('ALL_ETF_MEMO2_STR') }}
          <ol>
            <li>
              {{ t('ALL_ETF_MEMO2_STR1') }}
            </li>
            <li>
              {{ t('ALL_ETF_MEMO2_STR2') }}
            </li>
            <li>{{ t('ALL_ETF_MEMO2_STR3') }}</li>
          </ol>
        </div>
      </manual-table-item>
      <manual-table-item mark>{{ t('ALL_ETF_MEMO3_STR') }}</manual-table-item>
      <manual-table-item mark>
        {{ t('ALL_ETF_MEMO4_STR') }}
      </manual-table-item>
    </manual-table>
  </div>
</template>

<style lang="scss" scoped>
$white: #ffffff;
$text-tertiary: #c8c8c8;
.nav-link {
  color: $text-tertiary;
  background-color: $white;
}
.truncate-container {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  display: inline-block;
  width: 250px;
  @media (min-width: 1440px) {
    max-width: 285px;
  }
  @media (min-width: 1600px) {
    max-width: 350px;
  }
}
</style>
<style>
.el-popper.is-customized {
  background-color: white;
  border: 1px solid #005688c3;
}

.el-popper.is-customized .el-popper__arrow::before {
  background-color: white;
  right: 0;
  border: 1px solid #005788;
}
</style>
