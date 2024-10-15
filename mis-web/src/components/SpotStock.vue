<script setup>
const ex = ref('tse');
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  titleType: 'MENU_SECTOR_GROUP',
  describe: 'CNT_TRADE_NOTE',
  showTab: true,
  showGoBack: true,
});

watch(
  () => i18n.global.locale.value,
  async (newValue, oldValue) => {
    if (newValue !== oldValue) {
      try {
        await fetchDropDownList();
        await fetchData(
          sessionStorage.getItem('currentType'),
          sessionStorage.getItem('currentCode'),
          1
        );
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    }
  }
);

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

const title = ref('CEMENT_INDUSTRY');
const tseDropDownList = ref([]); // tse 下拉選單資料
const otcDropDownList = ref([]); // otc 下拉選單資料
const TableData = ref([]); // 全部資料
const totalCount = ref(0); // 資料筆數
const tse = ref('CEMENT_INDUSTRY');
const otc = ref('GROUP_GTSM_SELECT');

// 上市櫃類別
const ipoType = ref('tse');
const switchToTseOrOtc = async (type, name, code) => {
  ipoType.value = type;
  count.value = 10;
  title.value = name;
  if (type === 'tse') {
    tse.value = name;
    otc.value = 'GROUP_GTSM_SELECT';
  } else {
    otc.value = name;
    tse.value = 'GROUP_TWSE_SELECT';
  }
  const res = await fetchData(type, code, 1);
  if (totalCount.value) {
    currentPage.value = 1;
    paginationData.value.setCurrentPage(1);
  }
};

// 取得下拉選單資料
const fetchDropDownList = async () => {
  try {
    const DropDownData = await getIndustry();
    tseDropDownList.value = DropDownData.tse;
    otcDropDownList.value = DropDownData.otc;

    const currentType = sessionStorage.getItem('currentType');
    const currentCode = sessionStorage.getItem('currentCode');
    const selectedDropdown = DropDownData[currentType].find((item) => item.code === currentCode);

    if (selectedDropdown) {
      const valueToUpdate = currentType === 'tse' ? tse : otc;
      valueToUpdate.value = selectedDropdown.name;
      title.value = selectedDropdown.name;
    }
  } catch (error) {
    console.error('Error fetching data:', error.message);
  }
};
// 整股或零股交易
const tabType = ref('lot');
const changeTabType = (type) => {
  tabType.value = type;
  currentPage.value = 1;
  fetchData(ipoType.value, sessionStorage.getItem('currentCode'), currentPage.value);
};

// 取得列表資料（點擊分頁、設定每頁顯示筆數時觸發）
const fetchData = async (inputType, inputCode, currentPage) => {
  try {
    let type = inputType;
    let code = inputCode;
    if (type === undefined) {
      type = sessionStorage.getItem('currentType');
    }
    if (code === undefined) {
      code = sessionStorage.getItem('currentCode');
    }

    if (type && code) {
      sessionStorage.setItem('currentType', type);
      sessionStorage.setItem('currentCode', code);
    }
    const start = (currentPage - 1) * count.value;
    const end = start + count.value;

    // 取得現貨類股詳細資料
    const data = await getCategory(type, code);
    totalCount.value = data.size;

    // start: 索引位置 end: 資料取得筆數
    const slicedData = data.msgArray.slice(start * 1, end * 1);
    await fetchStockInfoData(slicedData);
  } catch (error) {
    console.error(error);
  }
};

// 取得現貨類股詳細資料
const fetchStockInfoData = async (data) => {
  if (data.length === 0) {
    TableData.value = [];
    return;
  }

  // 處理 call getStockInfo 所需參數
  const categoryList = data.reduce((result, current) => {
    const newResult = `${result}${current.ex}_${current.ch}|`;
    return newResult;
  }, '');

  let stockInfoData = [];
  if (tabType.value === 'odd') {
    stockInfoData = await getOddInfo(categoryList);
  } else {
    stockInfoData = await getStockInfo(categoryList);
  }

  // 因為stockInfoData的資料有時候會沒有值，需要以getCategory回傳的資料補足失去的部分
  stockInfoData.msgArray.forEach((item, index) => {
    if (!item.n) {
      item.n = data[index].n;
    }
    if (!item.c) {
      item.c = data[index].c;
    }
    if (!item.ch) {
      item.ch = data[index].ch;
    }
    if (!item.ex) {
      item.ex = data[index].ex;
    }
    if (!item.key) {
      item.key = data[index].key;
    }
  });
  const { msgArray } = await useCalculateValue(stockInfoData.msgArray, stockInfoData);
  TableData.value = msgArray;
};

const currentPage = ref(1);
const count = ref(10);
const countDropDownList = ref([10, 20, 50, 100]);
const paginationData = ref(1);
// 設定每頁顯示的資料筆數
const countValue = async (value) => {
  count.value = Number(value);
  currentPage.value = 1;
  // 從 Session Storage 中獲取存儲的類型和代碼
  const storedType = sessionStorage.getItem('currentType');
  const storedCode = sessionStorage.getItem('currentCode');

  // 調用 fetchData 並傳遞存儲的類型和代碼
  try {
    await fetchData(storedType, storedCode, currentPage.value);
  } catch (error) {
    console.e(error);
  }
};

// 透過分頁組件觸發函示
const paginateAndFetch = async (value) => {
  currentPage.value = value;
  try {
    await fetchData(undefined, undefined, value);
  } catch (error) {
    console.e(error);
  }
};

// 盤中整股行情
const tseTheadData = computed(() => {
  return [
    { name: '', key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('LATEST_PRICE'), key: 'z', width: '7%', theadAlignment: 'end' }, // 成交價
    { name: t('PRICE_CHANGE_N'), key: '', width: '15%', theadAlignment: 'end' }, // 漲跌價差<BR>(百分比)
    { name: t('TRADE_VOLUME'), key: 's', width: '6%', theadAlignment: 'end' }, // 成交量
    { name: t('ACC_TRADE_VOLUME_N'), key: 'v', width: '6%', theadAlignment: 'end' }, // 累積<BR>成交量
    { name: t('REFERENCE_PRICE_N'), key: 'pz', width: '8%', theadAlignment: 'end' }, // 試算參考<BR>成交價
    { name: t('REFERENCE_VOLUME_N'), key: 'ps', width: '7%', theadAlignment: 'end' }, // 試算參考<BR>成交量
    { name: t('OPEN_PRICE'), key: 'o', width: '7%', theadAlignment: 'end' }, // 開盤
    { name: t('HIGHEST_PRICE'), key: 'h', width: '7%', theadAlignment: 'end' }, // 當日最高
    { name: t('LOWEST_PRICE'), key: 'l', width: '7%', theadAlignment: 'end' }, // 當日最低
    { name: t('CURRENT_TIME'), key: 't', width: '8%', theadAlignment: 'end' }, // 撮合<BR>時間
    { name: t('COMMENT'), key: '', width: '6%', theadAlignment: 'start' }, // 說明
  ];
});

// 盤中零股行情
// 表頭資料
const oddTheadData = computed(() => {
  return [
    { name: '', key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('LATEST_TRADE_PRICE_N'), key: 'z', width: '5%', theadAlignment: 'end' }, // 最近<BR>成交價
    { name: t('PRICE_CHANGE_N'), key: '', width: '12%', theadAlignment: 'end' }, // 漲跌價差<BR>(百分比)
    { name: t('PRE_TRADE_VOLUME_N'), key: 's', width: '6%', theadAlignment: 'end' }, // 當盤<BR>成交量
    { name: t('ACC_TRADE_VOLUME_N'), key: 'v', width: '6%', theadAlignment: 'end' }, // 累積<BR>成交量
    { name: t('REFERENCE_PRICE_N'), key: 'pz', width: '8%', theadAlignment: 'end' }, // 試算參考<BR>成交價
    { name: t('REFERENCE_VOLUME_N'), key: 'ps', width: '8%', theadAlignment: 'end' }, // 試算參考<BR>成交量
    { name: t('BID_PRICE_N'), key: 'b', width: '7%', theadAlignment: 'end' }, // 揭示<BR>買價
    { name: t('BID_VOLUME_N'), key: 'g', width: '6%', theadAlignment: 'end' }, // 揭示<BR>買量
    { name: t('ASK_PRICE_N'), key: 'a', width: '7%', theadAlignment: 'end' }, // 揭示<BR>賣價
    { name: t('ASK_VOLUME_N'), key: 'f', width: '6%', theadAlignment: 'end' }, // 揭示<BR>賣量
    { name: t('INDEX_HIGH'), key: 'h', width: '7%', theadAlignment: 'end' }, // 最高
    { name: t('INDEX_LOW'), key: 'l', width: '7%', theadAlignment: 'end' }, // 最低
    { name: t('TIME1_N'), key: 't', width: '8%', theadAlignment: 'end' }, // 揭示<BR>時間
    { name: t('COMMENT'), key: '', width: '8%', theadAlignment: 'start' }, // 說明
  ];
});

onMounted(async () => {
  ipoType.value = 'tse';
  updateDetailDataInStore();
  await fetchData(ipoType.value, '01', 1);
  await fetchDropDownList();
  updateDataPeriodically();
});

let nIntervId;
const updateDataPeriodically = () => {
  nIntervId = setInterval(() => {
    fetchData(
      sessionStorage.getItem('currentType'),
      sessionStorage.getItem('currentCode'),
      currentPage.value
    );
  }, 5000);
};

onUnmounted(() => {
  clearInterval(nIntervId);
});
</script>

<template>
  <div>
    <div class="container-fluid">
      <div class="row justify-content-between">
        <p class="fs-2 text-black fw-medium p-0 col">{{ t('MENU_SECTOR_GROUP') }}</p>
        <p class="text-end text-secondary mt-3 lh-lg p-0 col-9">
          {{ tabType == 'lot' ? t('CNT_TRADE_NOTE') : t('CNT_TRADE_NOTE1') }}
        </p>
      </div>

      <div class="row justify-content-between">
        <div class="col-6 p-0 d-flex">
          <div class="dropdown me-9">
            <button
              id="dropdownMenuButton1"
              class="btn bg-white btn-outline-line-gray text-primary dropdown-toggle"
              type="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              {{ tse === 'GROUP_TWSE_SELECT' ? t(tse) : tse }}
            </button>
            <ul
              class="dropdown-menu text-primary"
              aria-labelledby="dropdownMenuButton1"
            >
              <li
                v-for="item in tseDropDownList"
                :key="item.name"
              >
                <button
                  class="dropdown-item"
                  type="button"
                  @click="switchToTseOrOtc('tse', item.name, item.code)"
                >
                  {{ item.name }}
                </button>
              </li>
            </ul>
          </div>
          <div class="dropdown">
            <button
              id="dropdownMenuButton1"
              class="btn bg-white btn-outline-line-gray text-primary dropdown-toggle"
              type="button"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              {{ otc === 'GROUP_GTSM_SELECT' ? t(otc) : otc }}
            </button>
            <ul
              class="dropdown-menu text-primary"
              aria-labelledby="dropdownMenuButton1"
              data-bs-auto-close="true"
            >
              <li
                v-for="item in otcDropDownList"
                :key="item.name"
              >
                <button
                  class="dropdown-item"
                  type="button"
                  @click="switchToTseOrOtc('otc', item.name, item.code)"
                >
                  {{ item.name }}
                </button>
              </li>
            </ul>
          </div>
        </div>
        <ul
          id="tse-tab"
          class="nav nav-pills mb-3 p-0 justify-content-end col"
          role="tablist"
        >
          <li
            class="nav-item"
            role="presentation"
          >
            <button
              id="pills-home-tab"
              class="nav-link active nav-left"
              data-bs-toggle="pill"
              data-bs-target="#pills-home"
              type="button"
              role="tab"
              aria-controls="pills-home"
              aria-selected="true"
              @click="changeTabType('lot')"
            >
              {{ t('QUOTES_QUOTATION') }}
            </button>
          </li>
          <li
            class="nav-item"
            role="presentation"
          >
            <button
              id="odd-tab"
              class="nav-link nav-right"
              data-bs-toggle="pill"
              data-bs-target="#pills-profile"
              type="button"
              role="tab"
              aria-controls="pills-profile"
              aria-selected="false"
              @click="changeTabType('odd')"
            >
              {{ t('ODD_QUOTATION') }}
            </button>
          </li>
        </ul>
      </div>
      <div
        id="pills-tabContent"
        class="tab-content row"
      >
        <div
          id="pills-home"
          class="tab-pane fade show active p-0"
          role="tabpanel"
          aria-labelledby="pills-home-tab"
        >
          <generic-table
            :thead-data="tseTheadData"
            :table-data="TableData"
            :title="title === 'GROUP_TWSE_SELECT' ? t(title) : title"
            :subtitle="t('UNIT_STR')"
            :subtitle1="`（${t('QUOTES_QUOTATION')}）`"
            :type="ipoType === 'tse' ? t('FIBEST_TSE') : t('FIBEST_OTC')"
          >
            <template #cell="{ row, thead }">
              <span
                v-if="thead.name === ''"
                class="text-primary"
              >
                <router-link
                  :to="{
                    name: 'DetailItem',
                    query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}`, type: `${ex}` },
                  }"
                >
                  {{ row.c }} {{ row.n }}
                </router-link>
              </span>
              <span
                v-if="thead.name === t('LATEST_PRICE')"
                :style="{ color: row.pColor }"
              >
                {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(2) : '-' }}
              </span>
              <span
                v-if="thead.name === t('PRICE_CHANGE_N')"
                :style="{ color: row.pColor }"
              >
                <template v-if="row.colorType == 'default'">{{ `- ${row.zHtmlStr}` }}</template>
                <template v-else-if="!row.colorType">-</template>
                <template v-else>
                  <i
                    v-if="row.colorType == 'downColor'"
                    class="bi bi-caret-up-fill"
                  ></i>
                  <i
                    v-else
                    class="bi bi-caret-down-fill"
                  ></i>
                  {{ `${Math.abs(row.diff)} ${row.zHtmlStr}` }}
                </template>
              </span>
              <span v-if="thead.name === t('REFERENCE_PRICE_N')">
                <div>
                  {{
                    row.ts == '0' || isNaN(Number(row[thead.key]))
                      ? '-'
                      : Number(row[thead.key]).toFixed(2)
                  }}
                </div>
              </span>
              <span v-if="thead.name === t('REFERENCE_VOLUME_N')">
                {{ row.ts == '0' || isNaN(Number(row[thead.key])) ? '-' : Number(row[thead.key]) }}
              </span>
              <span
                v-if="
                  thead.name === t('OPEN_PRICE') ||
                  thead.name === t('HIGHEST_PRICE') ||
                  thead.name === t('LOWEST_PRICE')
                "
              >
                {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(2) : '-' }}
              </span>
            </template>
          </generic-table>
        </div>
        <div
          id="pills-profile"
          class="tab-pane fade p-0"
          role="tabpanel"
          aria-labelledby="pills-profile-tab"
        >
          <generic-table
            :thead-data="oddTheadData"
            :table-data="TableData"
            :title="title === 'GROUP_TWSE_SELECT' ? t(title) : title"
            :subtitle="t('UNIT_STR')"
            :subtitle1="`（${t('ODD_QUOTATION')}）`"
            :type="ipoType === 'tse' ? t('FIBEST_TSE') : t('FIBEST_OTC')"
          >
            <template #cell="{ row, thead }">
              <span
                v-if="thead.name === ''"
                class="text-primary"
              >
                <el-tooltip
                  :enterable="false"
                  effect="customized"
                  :content="`${row.c}` + '  ' + `${row.n}`"
                  placement="top-start"
                  :hide-after="10"
                >
                  <router-link
                    :to="{
                      name: 'DetailItem',
                      query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}`, type: `${ex}` },
                    }"
                  >
                    <span class="truncate-container">{{ row.c }} {{ row.n }}</span>
                  </router-link>
                </el-tooltip>
              </span>
              <span
                v-if="thead.name === t('LATEST_TRADE_PRICE_N')"
                :style="{ color: row.pColor }"
              >
                {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(2) : '-' }}
              </span>
              <span
                v-if="thead.name === t('PRICE_CHANGE_N')"
                :style="{ color: row.pColor }"
              >
                <template v-if="row.colorType == 'default'">{{ `- ${row.zHtmlStr}` }}</template>
                <template v-else-if="!row.colorType">-</template>
                <template v-else>
                  <i
                    v-if="row.colorType == 'downColor'"
                    class="bi bi-caret-up-fill"
                  ></i>
                  <i
                    v-else
                    class="bi bi-caret-down-fill"
                  ></i>
                  {{ `${Math.abs(row.diff)} ${row.zHtmlStr}` }}
                </template>
              </span>
              <span v-if="thead.name === t('REFERENCE_PRICE_N')">
                {{
                  row.ts == '0' || isNaN(Number(row[thead.key]))
                    ? '-'
                    : Number(row[thead.key]).toFixed(2)
                }}
              </span>
              <span v-if="thead.name === t('REFERENCE_VOLUME_N')">
                {{ row.ts == '0' || isNaN(Number(row[thead.key])) ? '-' : Number(row[thead.key]) }}
              </span>
              <span v-if="thead.name === t('INDEX_HIGH') || thead.name === t('INDEX_LOW')">
                {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(2) : '-' }}
              </span>
              <span v-if="thead.name === t('BID_VOLUME_N') || thead.name === t('ASK_VOLUME_N')">
                {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(0) : '-' }}
              </span>
              <span v-if="thead.name === t('BID_PRICE_N') || thead.name === t('ASK_PRICE_N')">
                {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(2) : '-' }}
              </span>
            </template>
          </generic-table>
        </div>
      </div>
    </div>
    <!-- 每頁顯示筆數 -->
    <div
      v-if="TableData.length !== 0"
      class="row"
    >
      <div class="col-4 d-flex align-items-center text-secondary mb-3">
        <span>{{ t('GROUP_NO_PRE_PAGE') }}</span>
        <div class="dropdown me-3">
          <button
            id="dropdownMenuButton1"
            class="btn bg-white btn-outline-line-gray text-secondary dropdown-toggle py-0"
            type="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            {{ count }}
          </button>
          <ul
            class="dropdown-menu text-primary"
            aria-labelledby="dropdownMenuButton1"
            data-bs-auto-close="true"
          >
            <li
              v-for="(pageIdx, index) in countDropDownList"
              :key="index"
            >
              <button
                class="dropdown-item"
                href="#"
                @click.prevent="countValue(pageIdx)"
              >
                {{ pageIdx }}
              </button>
            </li>
          </ul>
        </div>
        <span>{{ t('GROUP_TOTAL_RECORDS') }}{{ totalCount }}</span>
      </div>
      <div class="col-8">
        <!-- 分頁 -->
        <page
          v-if="TableData.length !== 0"
          ref="paginationData"
          :current-page="currentPage"
          :total-count="totalCount"
          :page-size="count"
          @emit-paginate-and-fetch="paginateAndFetch"
        ></page>
      </div>
    </div>

    <manual-table>
      <manual-table-item>{{ t('GROUP_NOTE') }}</manual-table-item>
      <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED1') }}</manual-table-item>
      <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED2') }}</manual-table-item>
      <manual-table-item>
        {{ t('FIBEST_NOTE') }}
      </manual-table-item>
      <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED5') }}</manual-table-item>
      <manual-table-item color="rgba(255, 133, 113, 0.1)">{{ t('NOTE1') }}</manual-table-item>
      <manual-table-item color="rgba(79, 170, 132, 0.1)">{{ t('NOTE2') }}</manual-table-item>
      <manual-table-item color="rgba(58, 110, 165, 0.1)">
        {{ t('NOTE3') }}
      </manual-table-item>
      <manual-table-item color="rgba(242, 181, 41, 0.1)">{{ t('NOTE4') }}</manual-table-item>
    </manual-table>
  </div>
</template>

<style lang="scss" scoped>
$brand-tertiary: #134372;
$white: #ffffff;
$text-tertiary: #c8c8c8;
.dropdown-menu {
  max-height: 250px;
  overflow-y: auto;
}
.nav-link {
  color: $text-tertiary;
  background-color: $white;
}
.nav-left {
  border-radius: 8px 0 0 8px;
}
.nav-right {
  border-radius: 0 8px 8px 0;
}
.nav-link.active {
  background-color: $brand-tertiary;
}
.truncate-container {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  display: inline-block;
  max-width: 90px;

  @media (min-width: 1360px) {
    max-width: 120px;
  }
  @media (min-width: 1440px) {
    width: auto;
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
