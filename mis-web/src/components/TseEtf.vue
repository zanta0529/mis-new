<script setup>
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  titleType: 'MENU_CATEGORIES_ETF_TWSE',
  describe: 'CNT_TRADE_NOTE',
  showTab: true,
  showGoBack: true,
});

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

// 表頭資料
const theadData = computed(() => {
  return [
    { name: '', key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('LATEST_PRICE'), key: 'z', width: '8%', theadAlignment: 'end' },
    { name: t('PRICE_CHANGE_N'), key: '', width: '15%', theadAlignment: 'end' },
    { name: t('TRADE_VOLUME'), key: 'tv', width: '7%', theadAlignment: 'end' },
    { name: t('ACC_TRADE_VOLUME_N'), key: 'v', width: '8%', theadAlignment: 'end' },
    { name: t('REFERENCE_PRICE_N'), key: 'pz', width: '8%', theadAlignment: 'end' },
    { name: t('REFERENCE_VOLUME_N'), key: 'ps', width: '8%', theadAlignment: 'end' },
    { name: t('CURRENT_TIME'), key: 't', width: '8%', theadAlignment: 'end' },
    { name: t('GROUP_ETF_ESTIMATED_N'), key: 'nu', width: '7%', theadAlignment: 'end' },
  ];
});

// 取得列表資料
const TableData = ref([]); // 全部資料
const totalCount = ref(0); // 資料筆數

const fetchData = async (type, code, currentPage) => {
  try {
    // 如果 type 或 code 為 undefined，則使用 Session Storage 中的值
    if (type === undefined) {
      type = sessionStorage.getItem('currentType');
    }
    if (code === undefined) {
      code = sessionStorage.getItem('currentCode');
    }

    // 更新 Session Storage 中的值
    sessionStorage.setItem('currentType', type);
    sessionStorage.setItem('currentCode', code);

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
  // 處理 call getStockInfo 所需參數
  const categoryList = data.reduce((result, current) => {
    result += `${current.ex}_${current.ch}|`;
    return result;
  }, '');

  const stockInfoData = await getStockInfo(categoryList);

  // 計算漲跌百分比
  const { msgArray } = await useCalculateValue(stockInfoData.msgArray, stockInfoData);
  TableData.value = msgArray;
};

const currentPage = ref(1);
const count = ref(10);
const countDropDownList = ref([10, 20, 50, 100]);

const paginationData = ref(null);
// 設定每頁顯示的資料筆數
const countValue = (value) => {
  count.value = Number(value);
  currentPage.value = 1;
  // 從 Session Storage 中獲取存儲的類型和代碼
  const storedType = sessionStorage.getItem('currentType');
  const storedCode = sessionStorage.getItem('currentCode');

  // 調用 fetchData 並傳遞存儲的類型和代碼
  fetchData(storedType, storedCode, currentPage.value);
};

// 透過分頁組件觸發函示
const paginateAndFetch = async (value) => {
  currentPage.value = value;
  try {
    await fetchData(undefined, undefined, value);
  } catch (error) {
    console.error(error);
  }
};

useWatchLang(fetchData);
let nIntervId;
const updateDataPeriodically = () => {
  nIntervId = setInterval(() => {
    fetchData('tse', 'B0', currentPage.value);
  }, 5000);
};
onMounted(() => {
  updateDetailDataInStore();
  fetchData('tse', 'B0', 1);
  updateDataPeriodically();
});

onUnmounted(() => {
  clearInterval(nIntervId);
});
</script>
<template>
  <div>
    <!-- 標題區域 -->
    <div class="container-fluid">
      <div class="row justify-content-between">
        <p class="fs-2 text-black fw-medium p-0 mb-0 col-3">{{ t('MENU_CATEGORIES_ETF_TWSE') }}</p>
        <p class="text-end text-secondary mt-3 mb-0 lh-lg p-0 col-9">{{ t('CNT_TRADE_NOTE') }}</p>
      </div>
    </div>
    <!-- 表格 -->
    <generic-table
      :thead-data="theadData"
      :table-data="TableData"
      :title="t('MENU_ETF')"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 5 ' + t('UPDATE_TIME1')"
      :type="t('FIBEST_TSE')"
    >
      <template #cell="{ row, thead }">
        <!-- 標題 -->
        <span
          v-if="thead.name === ''"
          class="text-primary"
        >
          <router-link
            :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
          >
            {{ row.c }} {{ row.n }}
          </router-link>
        </span>
        <!-- 成交價 -->
        <span
          v-if="thead.name === t('LATEST_PRICE')"
          :style="{ color: row.pColor }"
        >
          {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(2) : '-' }}
        </span>
        <!-- 漲跌價差(百分比) -->
        <span
          v-if="thead.name === t('PRICE_CHANGE_N')"
          :style="{ color: row.pColor }"
        >
          <template v-if="row.diff">
            <i
              v-if="row.colorType === 'downColor'"
              class="bi bi-caret-up-fill"
            ></i>
            <i
              v-else-if="row.colorType === 'upColor'"
              class="bi bi-caret-down-fill"
            ></i>
            <span v-else>-</span>
            {{ Math.abs(row.diff) }} {{ row.zHtmlStr }}
          </template>
          <template v-else>-</template>
        </span>
        <!-- 試算參考成交價 -->
        <span
          v-if="thead.name === t('REFERENCE_PRICE_N') || thead.name === t('REFERENCE_VOLUME_N')"
        >
          {{
            row.ts == '0' || isNaN(Number(row[thead.key])) ? '-' : Number(row[thead.key]).toFixed(2)
          }}
        </span>
        <span v-if="thead.name === t('GROUP_ETF_ESTIMATED_N')">
          <a
            :href="row.nu"
            target="_blank"
            class="link-primary"
          >
            {{ t('GROUP_ETF_ESTIMATED_LINK') }}
          </a>
        </span>
      </template>
    </generic-table>

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
              v-for="(count, index) in countDropDownList"
              :key="index"
            >
              <button
                class="dropdown-item"
                href="#"
                @click="countValue(count)"
              >
                {{ count }}
              </button>
            </li>
          </ul>
        </div>
        <span>{{ t('GROUP_TOTAL_RECORDS') }}{{ totalCount }}</span>
      </div>
      <div class="col">
        <!-- 分頁 -->
        <page
          v-if="TableData.length !== 0"
          ref="paginationData"
          :total-count="totalCount"
          :current-page="currentPage"
          :page-size="count"
          @emit-paginate-and-fetch="paginateAndFetch"
        ></page>
      </div>
    </div>
    <manual-table>
      <manual-table-item>{{ t('GROUP_NOTE_OTC_ETF') }}</manual-table-item>
      <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED1') }}</manual-table-item>
      <manual-table-item>
        {{ t('GROUP_NOTE_ETF1') }}
        <a
          target="_blank"
          href="https://www.twse.com.tw/zh/products/securities/etf/news.html"
          class="text-custom-red"
        >
          {{ t('GROUP_NOTE_ETF2') }}
        </a>
      </manual-table-item>
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
.dropdown-menu {
  max-height: 250px;
  overflow-y: auto;
}
</style>
