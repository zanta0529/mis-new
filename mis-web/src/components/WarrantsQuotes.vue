<script setup>
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const title = ref(t('TWSE_WARRANT_CALL_NO_CBBC')); // 認購權證 (不含牛證)
const tseDropDownList = ref([]); // tse 下拉選單資料
const otcDropDownList = ref([]); // otc 下拉選單資料
const TableData = ref([]); // 全部資料
const totalCount = ref(0); // 資料筆數
const tse = ref(t('TWSE_WARRANT_CALL_NO_CBBC')); // 認購權證 (不含牛證)
const otc = ref(t('GROUP_GTSM_WARRANT_SELECT')); // 請選擇上櫃權證
const detailData = ref({
  titleType: 'MENU_CATEGORIES_WARRANTS',
  describe: 'CNT_TRADE_NOTE',
  showTab: true,
  showGoBack: true,
});

// 表頭資料
const tseTheadData = computed(() => {
  return [
    { name: '', key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('LATEST_PRICE'), key: 'z', width: '6%', theadAlignment: 'end' },
    { name: t('PRICE_CHANGE_N'), key: '', width: '15%', theadAlignment: 'end' },
    { name: t('TRADE_VOLUME'), key: 's', width: '6%', theadAlignment: 'end' },
    { name: t('ACC_TRADE_VOLUME_N'), key: 'v', width: '6%', theadAlignment: 'end' },
    { name: t('REFERENCE_PRICE_N'), key: 'pz', width: '8%', theadAlignment: 'end' },
    { name: t('REFERENCE_VOLUME_N'), key: 'ps', width: '8%', theadAlignment: 'end' },
    { name: t('OPEN_PRICE'), key: 'o', width: '7%', theadAlignment: 'end' },
    { name: t('HIGHEST_PRICE'), key: 'h', width: '8%', theadAlignment: 'end' },
    { name: t('LOWEST_PRICE'), key: 'l', width: '8%', theadAlignment: 'end' },
    { name: t('CURRENT_TIME_N'), key: 't', width: '8%', theadAlignment: 'end' },
    { name: t('GROUP_WARRANT_TARGET'), key: 'rn', width: '8%', theadAlignment: 'end' },
  ];
});

watch(
  () => i18n.global.locale.value,
  async (newValue, oldValue) => {
    if (newValue !== oldValue) {
      fetchDropDownList();
      await fetchData();
      tse.value = textChange(tse.value);
      otc.value = textChange(otc.value);
      location.reload();
    }
  }
);

// 中英切換
const textChange = (text) => {
  switch (text) {
    case '請選擇上市權證':
      return 'TWSE Warrants';
    case 'TWSE Warrants':
      return '請選擇上市權證';

    case '請選擇上櫃權證':
      return 'TPEx Warrants';
    case 'TPEx Warrants':
      return '請選擇上櫃權證';

    case '認購權證 (不含牛證)':
      return 'Warrant (call &amp; no CBBC)';
    case 'Warrant (call &amp; no CBBC)':
      return '認購權證 (不含牛證)';

    case '認售權證 (不含熊證)':
      return 'Warrant (put &amp; no CBBC)';
    case 'Warrant (put &amp; no CBBC)':
      return '認售權證 (不含熊證)';

    case '牛證(不含可展延牛證)':
      return 'Callable Bull Contracts (no Open-end CBBC)';
    case 'Callable Bull Contracts (no Open-end CBBC)':
      return '牛證(不含可展延牛證)';

    case '熊證(不含可展延牛證)':
      return 'Callable Bear Contracts (no Open-end CBBC)';
    case 'Callable Bear Contracts (no Open-end CBBC)':
      return '熊證(不含可展延牛證)';

    case '可展延牛證':
      return 'Open-end Callable Bull Contracts';
    case 'Open-end Callable Bull Contracts':
      return '可展延牛證';

    case '可展延熊證':
      return 'Open-end Callable Bear Contracts';
    case 'Open-end Callable Bear Contracts':
      return '可展延熊證';

    default:
      return text;
  }
};

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

// 選取 tse 下拉選單
const tseValue = (value, code) => {
  count.value = 10;
  tse.value = value;
  otc.value = t('GROUP_GTSM_WARRANT_SELECT'); // 請選擇上櫃權證
  title.value = value;
  fetchData('tse', code, 1);
  paginationData.value.setCurrentPage(1); // 設定子組件資料
};

// 選取 otc 下拉選單
const otcValue = (value, code) => {
  count.value = 10;
  otc.value = value;
  tse.value = t('GROUP_TWSE_WARRANT_SELECT'); // 請選擇上市權證
  title.value = value;
  fetchData('otc', code, 1);
  paginationData.value.setCurrentPage(1);
};

// 取得下拉選單資料
const fetchDropDownList = async () => {
  try {
    const DropDownData = await getIndustry();
    tseDropDownList.value = DropDownData.tse.filter((item) => item.code.startsWith('D'));
    otcDropDownList.value = DropDownData.otc.filter((item) => item.code.startsWith('D'));
  } catch (error) {
    console.error('Error fetching data:', error.message);
  }
};
const activeTab = ref('tse-tab');

// 取得列表資料（點擊分頁、設定每頁顯示筆數時觸發）
const fetchData = async (inputType, inputCode, currentPage) => {
  try {
    // 如果 type 或 code 為 undefined，則使用 Session Storage 中的值
    let type = inputType;
    let code = inputCode;
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
    const updatedResult = `${result}${current.ex}_${current.ch}|`;
    return updatedResult;
  }, '');

  const stockInfoData = await getStockInfo(categoryList);

  // 計算漲跌百分比
  const { msgArray } = await useCalculateValue(stockInfoData.msgArray, stockInfoData);
  TableData.value = msgArray;
};

// 分頁相關
const count = ref(10);
const countDropDownList = ref([10, 20, 50, 100]);
const paginationData = ref(1);

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

// 透過分頁組件觸發函示
const paginateAndFetch = (value) => {
  fetchData(undefined, undefined, value);
};

onMounted(() => {
  updateDetailDataInStore();
  fetchDropDownList();
  fetchData('tse', 'D1', 1);
});
</script>
<template>
  <div>
    <div class="container">
      <div class="row justify-content-between">
        <p class="fs-2 text-black fw-medium p-0 col-2">{{ t('MENU_CATEGORIES_WARRANTS') }}</p>
        <p class="text-end text-secondary mt-3 lh-lg p-0 col-9">
          {{ t('CNT_TRADE_NOTE') }}
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
              v-html="tse"
            ></button>
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
                  v-html="item.name"
                ></button>
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
              v-html="otc"
            ></button>
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
                  v-html="item.name"
                ></button>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div
        id="pills-tabContent"
        class="tab-content row p-0"
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
            :title="title"
            :subtitle="t('UNIT_STR')"
            :type="t('FIBEST_TSE')"
          >
            <template #cell="{ row, thead }">
              <span
                v-if="thead.name === ''"
                class="text-primary"
              >
                <el-tooltip
                  :enterable="false"
                  effect="customized"
                  :content="`${row.c}` + ' / ' + `${row.n}`"
                  placement="top-start"
                  :hide-after="10"
                >
                  <router-link
                    :to="{
                      name: 'DetailItem',
                      query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` },
                    }"
                  >
                    {{ row.c }} {{ row.n }}
                  </router-link>
                </el-tooltip>
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
              <span
                v-if="
                  thead.name === t('REFERENCE_PRICE_N') || thead.name === t('REFERENCE_VOLUME_N')
                "
              >
                {{
                  row.ts == '0' || isNaN(Number(row[thead.key]))
                    ? '-'
                    : Number(row[thead.key]).toFixed(2)
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
              <span
                v-if="
                  thead.name === t('GROUP_WARRANT_TARGET') ||
                  thead.name === t('GROUP_WARRANT_TARGET')
                "
              >
                <router-link
                  class="link-primary"
                  :to="{
                    name: 'DetailItem',
                    query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}`, ch: `${row.rch}` },
                  }"
                >
                  {{ row.rn }}
                </router-link>
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
</style>
<style>
.el-popper.is-customized {
  padding: 6px 12px;
  background-color: white;
  border: 1px solid #005688c3;
}

.el-popper.is-customized .el-popper__arrow::before {
  background-color: white;
  right: 0;
  border: 1px solid #005788;
}
</style>
