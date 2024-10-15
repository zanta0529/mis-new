<script setup>
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  titleType: 'TIB',
  describe: 'CNT_TRADE_NOTE',
  showTab: false,
});

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

// 表頭資料
const theadData = computed(() => {
  return [
    { name: '', key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('LATEST_PRICE'), key: 'z', width: '7%', theadAlignment: 'end' },
    { name: t('PRICE_CHANGE_N'), key: '', width: '15%', theadAlignment: 'end' },
    { name: t('TRADE_VOLUME'), key: 'tv', width: '7%', theadAlignment: 'end' },
    { name: t('ACC_TRADE_VOLUME_N'), key: 'v', width: '7%', theadAlignment: 'end' },
    { name: t('REFERENCE_PRICE_N'), key: 'pz', width: '8%', theadAlignment: 'end' },
    { name: t('REFERENCE_VOLUME_N'), key: 'ps', width: '8%', theadAlignment: 'end' },
    { name: t('OPEN_PRICE'), key: 'o', width: '7%', theadAlignment: 'end' },
    { name: t('HIGHEST_PRICE'), key: 'h', width: '7%', theadAlignment: 'end' },
    { name: t('LOWEST_PRICE'), key: 'l', width: '7%', theadAlignment: 'end' },
    { name: t('CURRENT_TIME'), key: 't', width: '7%', theadAlignment: 'end' },
    { name: t('COMMENT'), key: '', width: '5%', theadAlignment: 'end' },
  ];
});

// 取得列表資料
const tableData = ref([]);
const totalCount = ref(0);

const fetchData = async (type, code, bp, currentPage) => {
  try {
    // 如果 type 或 code 為 undefined，則使用 Session Storage 中的值
    if (type === undefined) {
      type = sessionStorage.getItem('currentType');
    }
    if (bp === undefined) {
      bp = sessionStorage.getItem('currentBp');
    }

    // 更新 Session Storage 中的值
    sessionStorage.setItem('currentType', type);
    sessionStorage.setItem('currentBp', bp);

    const start = (currentPage - 1) * count.value;
    const end = start + count.value;

    const data = await getCategory(type, code, bp);
    totalCount.value = data.size;

    // start: 索引位置 end: 資料取得筆數
    const slicedData = data.msgArray.slice(start * 1, end * 1);

    await fetchStockInfoData(slicedData);
  } catch (error) {
    // 處理錯誤，例如顯示錯誤訊息或進行其他適當的處理
    console.error('Error fetching data:', error.message);
    // 可以根據實際情況進行其他處理，例如顯示錯誤訊息給用戶
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
  tableData.value = msgArray;
};

// 分頁
const currentPage = ref(1);
const count = ref(10);
const countDropDownList = ref([10, 20, 50, 100]);

const countValue = (value) => {
  count.value = value;
  count.value = Number(value);

  // 從 Session Storage 中獲取存儲的類型和代碼
  const storedType = sessionStorage.getItem('currentType');
  const storedCode = sessionStorage.getItem('currentBp');

  // 調用 fetchData 並傳遞存儲的類型和代碼
  fetchData(storedType, null, storedCode, 1);
  paginationData.value.setCurrentPage(1); // 設定子組件資料
};

const paginationData = ref(1);

// 透過分頁組件觸發函示
const paginateAndFetch = (value) => {
  currentPage.value = value;
  fetchData(undefined, null, undefined, value);
};

useWatchLang(fetchData);

let nIntervId;
const updateDataPeriodically = () => {
  nIntervId = setInterval(() => {
    fetchData('tse', null, '03', currentPage.value);
  }, 5000);
};

onMounted(() => {
  updateDetailDataInStore();
  fetchData('tse', null, '03', 1);
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
      <div class="row justify-content-between fles-wrap">
        <p class="fs-2 text-black fw-medium p-0 col">{{ t('TIB') }}</p>
        <p class="text-end text-secondary mt-3 lh-lg p-0 col">{{ t('CNT_TRADE_NOTE') }}</p>
      </div>
    </div>

    <!-- 表格 -->
    <generic-table
      :thead-data="theadData"
      :table-data="tableData"
      :title="t('TIB')"
      :subtitle="t('UNIT_STR')"
      :type="t('FIBEST_TSE')"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === ''"
          class="text-primary"
        >
          <router-link
            :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, type: `${row.ex}` } }"
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
          <template v-if="row.diff">
            <template v-if="row.colorType === 'downColor'">
              <i class="bi bi-caret-up-fill"></i>
            </template>
            <template v-else-if="row.colorType === 'upColor'">
              <i class="bi bi-caret-down-fill"></i>
            </template>
            <template v-else>-</template>
            {{ Math.abs(row.diff) }} {{ row.zHtmlStr }}
          </template>
          <template v-else>-</template>
        </span>
        <span
          v-if="thead.name === t('REFERENCE_PRICE_N') || thead.name === t('REFERENCE_VOLUME_N')"
        >
          {{
            row.ts == '0' || isNaN(Number(row[thead.key])) ? '-' : Number(row[thead.key]).toFixed(2)
          }}
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

    <!-- 每頁顯示筆數 -->
    <div
      v-if="tableData.length !== 0"
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
          v-if="tableData.length !== 0"
          ref="paginationData"
          :total-count="totalCount"
          :page-size="count"
          @emit-paginate-and-fetch="paginateAndFetch"
        ></page>
      </div>
    </div>

    <!-- 備註 -->
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
$background: #f7f7f7;
$disabled: #e9ecef;

.active > .page-link {
  background-color: $brand-tertiary;
}
.page-link {
  background-color: $background;
}
.disabled > .page-link {
  background-color: $disabled;
}
</style>
