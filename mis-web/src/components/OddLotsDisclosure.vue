<script setup>
const { t } = useI18n();
const router = useRouter();
const route = useRoute();
const ex = computed(() => {
  return route.query.ex || 'tse';
});

const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  titleType: 'MENU_ODD_LOT_TRADE',
  describe: 'CNT_TRADE_NOTE',
  showTab: true,
  showGoBack: true,
});

watch(
  () => i18n.global.locale.value,
  async (newValue, oldValue) => {
    if (newValue !== oldValue) {
      await fetchData(
        sessionStorage.getItem('currentType'),
        sessionStorage.getItem('currentCode'),
        1
      );
      fetchDropDownList();
    }
  }
);

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

let periodicUpdateId;
const startPeriodicDataUpdate = () => {
  periodicUpdateId = setInterval(() => {
    if (searchInputValue.value) {
      fetchStockInfoData(searchInputValue.value, 'select');
    } else {
      fetchData(
        sessionStorage.getItem('currentType'),
        sessionStorage.getItem('currentCode'),
        currentPage.value
      );
    }
  }, 5000);
};

const title = ref('CEMENT_INDUSTRY');
const tseDropDownList = ref([]); // tse 下拉選單資料
const otcDropDownList = ref([]); // otc 下拉選單資料
const TableData = ref([]); // 全部資料
const totalCount = ref(0); // 資料筆數
const tse = ref('CEMENT_INDUSTRY');
const otc = ref('GROUP_GTSM_SELECT');

// 選取 tse 下拉選單
const tseValue = (value, code) => {
  count.value = 10;
  searchInputValue.value = '';
  tse.value = value;
  otc.value = 'GROUP_GTSM_SELECT';
  title.value = value;
  fetchData('tse', code, 1);
  paginationData.value.setCurrentPage(1); // 設定子組件資料

  router.push({
    path: route.path,
    query: { ...route.query, ex: 'tse' },
  });
};

// 選取 otc 下拉選單
const otcValue = (value, code) => {
  count.value = 10;
  searchInputValue.value = '';
  otc.value = value;
  tse.value = 'GROUP_TWSE_SELECT';
  title.value = value;
  fetchData('otc', code, 1);
  paginationData.value.setCurrentPage(1);

  router.push({
    path: route.path,
    query: { ...route.query, ex: 'otc' },
  });
};

// 取得下拉選單資料
const fetchDropDownList = async () => {
  try {
    const DropDownData = await getOddIndustry();
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

// 取得列表資料（點擊分頁、設定每頁顯示筆數時觸發）
const fetchData = async (inputType, inputCode, currentPage) => {
  try {
    let type = inputType;
    let code = inputCode;
    // 如果 type 或 code 為 undefined，則使用 Session Storage 中的值
    if (inputType === undefined) {
      type = sessionStorage.getItem('currentType');
    }
    if (inputCode === undefined) {
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

const fetchStockInfoData = async (data, item) => {
  // 處理 call getStockInfo 所需參數
  const categoryList = item ? data : data.map((current) => `${current.ex}_${current.ch}`).join('|');

  const stockInfoData = await getStockInfo(categoryList);

  // 計算漲跌百分比
  const { msgArray } = await useCalculateValue(stockInfoData.msgArray);
  TableData.value = msgArray;
};

const currentPage = ref(1);
const count = ref(10);
const countDropDownList = ref([10, 20, 50, 100]);

// 設定每頁顯示的資料筆數
const countValue = (value) => {
  count.value = value;
  count.value = Number(value);
  // 從 Session Storage 中獲取存儲的類型和代碼
  const storedType = sessionStorage.getItem('currentType');
  const storedCode = sessionStorage.getItem('currentCode');

  // 調用 fetchData 並傳遞存儲的類型和代碼
  fetchData(storedType, storedCode, 1);
  paginationData.value.setCurrentPage(1);
};

const paginationData = ref(1);

// 透過分頁組件觸發函示
const paginateAndFetch = (value) => {
  currentPage.value = value;
  fetchData(undefined, undefined, value);
};

// 盤中整股行情
const tseTheadData = computed(() => {
  return [
    { name: '', key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('OPEN_PRICE'), key: 'o', width: '8%', theadAlignment: 'end' },
    { name: t('HIGHEST_PRICE'), key: 'h', width: '7%', theadAlignment: 'end' },
    { name: t('LOWEST_PRICE'), key: 'l', width: '7%', theadAlignment: 'end' },
    { name: t('LATEST_PRICE'), key: 'z', width: '7%', theadAlignment: 'end' },
    { name: t('U_PRICE'), key: 'u', width: '7%', theadAlignment: 'end' },
    { name: t('W_PRICE'), key: 'w', width: '7%', theadAlignment: 'end' },
    { name: t('TIME'), key: 'ot', width: '7%', theadAlignment: 'end' },
    { name: t('BID_PRICE_N'), key: 'ob', width: '7%', theadAlignment: 'end' },
    { name: t('ASK_PRICE_N'), key: 'oa', width: '7%', theadAlignment: 'end' },
    { name: t('ODDTRADE_TRADE_PRICE_N'), key: 'oz', width: '7%', theadAlignment: 'end' },
    { name: t('ODDTRADE_TRADE_VOLUME_N'), key: 'ov', width: '7%', theadAlignment: 'end' },
  ];
});

const options = ref([]);
const searchInputValue = ref('');
const loading = ref(false);

const store = useGetStockNamesStore();

function transformStockName(item, query) {
  const key = item.key.replace(/_\d+$/, '');
  const label = `${item.n}[${item.c}]`;
  if (label.toLowerCase().includes(query.toLowerCase())) {
    return { label, value: key };
  }
  return null;
}

const remoteMethod = debounce(async (query) => {
  try {
    if (query) {
      loading.value = true;
      await store.doGetStockNames(query);
      options.value = store.stockNameList
        .map((item) => transformStockName(item, query))
        .filter((item) => item !== null);
    }
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
}, 500);

const handleSelect = async (value) => {
  otc.value = 'GROUP_GTSM_SELECT';
  tse.value = 'GROUP_TWSE_SELECT';
  clearInterval(periodicUpdateId);
  await fetchStockInfoData(value, 'select');
  await startPeriodicDataUpdate();
};

onMounted(() => {
  updateDetailDataInStore();
  fetchDropDownList();
  fetchData('tse', '01', 1);
  startPeriodicDataUpdate();
});

onUnmounted(() => {
  if (periodicUpdateId) {
    clearInterval(periodicUpdateId);
  }
});
</script>

<template>
  <div>
    <div class="container-fluid">
      <div class="row justify-content-between">
        <p
          class="fs-2 text-black fw-medium p-0 col"
          v-html="t('MENU_ODD_LOT_TRADE_TITLE')"
        ></p>
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
                  href="#"
                  @click="tseValue(item.name, item.code)"
                >
                  {{ item.name }}
                </button>
              </li>
            </ul>
          </div>
          <div class="dropdown me-9">
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
                  href="#"
                  @click="otcValue(item.name, item.code)"
                >
                  {{ item.name }}
                </button>
              </li>
            </ul>
          </div>
          <div class="w-100">
            <el-select
              v-model="searchInputValue"
              class="w-100"
              filterable
              remote
              reserve-keyword
              :placeholder="t('BANNER_SIDEVIEW_INPUTCODE')"
              :remote-method="remoteMethod"
              :loading="loading"
              @change="handleSelect"
            >
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
              <template #prefix>
                <el-icon
                  class="text-primary"
                  size="large"
                  :style="{ 'font-weight': 'bold' }"
                >
                  <i class="bi bi-search"></i>
                </el-icon>
              </template>
            </el-select>
          </div>
        </div>
      </div>
      <div class="row p-0">
        <div
          id="pills-home"
          class="p-0"
        >
          <generic-table
            :thead-data="tseTheadData"
            :table-data="TableData"
            :title="title"
            :subtitle1="t('ODDTRADE_SUBTITLE')"
            :subtitle="t('UNIT_STR')"
            :subtitle-interval-time="t('ODDTRADE_TIME_NO_WRAP')"
            :type="ex === 'tse' ? t('FIBEST_TSE') : t('FIBEST_OTC')"
          >
            <template #cell="{ row, thead }">
              <span
                v-if="thead.name === ''"
                class="text-primary"
              >
                <router-link
                  :to="{
                    name: 'DetailItem',
                    query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` },
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
                v-if="
                  thead.name === t('OPEN_PRICE') ||
                  thead.name === t('HIGHEST_PRICE') ||
                  thead.name === t('LOWEST_PRICE') ||
                  thead.name === t('U_PRICE') ||
                  thead.name === t('W_PRICE') ||
                  thead.name === t('BID_PRICE_N') ||
                  thead.name === t('ASK_PRICE_N') ||
                  thead.name === t('ODDTRADE_TRADE_PRICE_N')
                "
              >
                {{ !isNaN(Number(row[thead.key])) ? Number(row[thead.key]).toFixed(2) : '-' }}
              </span>
              <span v-if="thead.name === t('ODDTRADE_TRADE_VOLUME_N')">
                {{ row[thead.key] ? row[thead.key] : '-' }}
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
          :page-size="count"
          @emit-paginate-and-fetch="paginateAndFetch"
        ></page>
      </div>
    </div>

    <manual-table>
      <manual-table-item>
        {{ t('ODDTRADE_NOTE') }}
      </manual-table-item>
      <manual-table-item>
        {{ t('ODDTRADE_NOTE1') }}
      </manual-table-item>
      <manual-table-item>
        {{ t('FIBEST_NOTE') }}
      </manual-table-item>
      <manual-table-item color="rgba(255, 133, 113, 0.1)">{{ t('NOTE1') }}</manual-table-item>
      <manual-table-item color="rgba(79, 170, 132, 0.1)">{{ t('NOTE2') }}</manual-table-item>
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
:deep(.el-input__wrapper) {
  padding-top: 4px;
  padding-bottom: 4px;
}
</style>
