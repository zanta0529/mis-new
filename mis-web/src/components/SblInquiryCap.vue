<script setup>
const { t } = useI18n();

// 盤中整股行情
const tseTheadData = computed(() => {
  return [
    { name: '', key: '', width: '10%', theadAlignment: 'center' },
    { name: t('SBL_STOCK_CODE'), key: 'stkno', width: '30%', theadAlignment: 'end' },
    {
      name: t('SBL_REAL_TIME_AVAILABLE_VOL'),
      key: 'slblimit',
      width: '30%',
      theadAlignment: 'end',
    },
    { name: t('SBL_LAST_MODIFY'), key: 'txtime', width: '30%', theadAlignment: 'center' },
  ];
});
const start = ref(0);
const count = ref(10);
const countDropDownList = ref([10, 20, 50, 100]);
const TableData = ref([]); // 全部資料
const totalCount = ref(0); // 資料筆數
const fetchData = async (currentPage, inputValue) => {
  try {
    const res = await getStockSblsCap();
    totalCount.value = res.msgArray.length;

    start.value = (currentPage - 1) * count.value;
    let end = start.value + count.value;
    let slicedData = res.msgArray.slice(start.value * 1, end * 1);
    TableData.value = slicedData;

    if (inputValue) {
      const filterData = res.msgArray.filter((item) => item.stkno.includes(inputValue));
      totalCount.value = filterData.length;
      start.value = (currentPage - 1) * count.value;
      end = start.value + count.value;
      slicedData = filterData.slice(start.value * 1, end * 1);
      TableData.value = slicedData;
      // slicedData(currentPage, filterData);
    }
  } catch (error) {
    console.error(error);
  }
};

// 設定每頁顯示的資料筆數
const countValue = (value) => {
  count.value = Number(value);
  // 調用 fetchData 並傳遞存儲的類型和代碼
  fetchData(1);
  paginationData.value.setCurrentPage(1);
};

const paginationData = ref(1);

const searchValue = ref('');

const inputChange = debounce(async () => {
  const inputValue = searchValue.value.trim();
  if (/^[a-zA-Z0-9]*$/.test(inputValue)) {
    await fetchData(1, inputValue);
  }
}, 200);

const nowPage = ref(1);

// 透過分頁組件觸發函示
const paginateAndFetch = (page) => {
  nowPage.value = page;
  if (searchValue.value) {
    fetchData(page, searchValue.value);
  } else {
    fetchData(page);
  }
};

let intervalGetFetchData = null;

onMounted(async () => {
  await fetchData(nowPage.value, searchValue.value);
  intervalGetFetchData = setInterval(() => {
    fetchData(nowPage.value, searchValue.value);
  }, 5000);
});

onUnmounted(() => {
  clearInterval(intervalGetFetchData);
});
</script>
<template>
  <div>
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-end mb-32">
        <span class="fs-2 text-black">{{ t('MENU_SBL_REAL_TIME_AVAILABLE') }}</span>
      </div>

      <div class="d-flex align-items-center">
        <span class="fs-7 text-black me-2">{{ t('SBL_INPUTSTOCK0') }}</span>
        <div class="w-75">
          <el-input
            v-model="searchValue"
            class="w-50"
            :placeholder="t('SBL_MULTIPLE_STOCK1')"
            @input="inputChange"
          >
            <template #suffix>
              <el-icon
                class="text-primary el-input__icon"
                size="large"
              >
                <i class="bi bi-search"></i>
              </el-icon>
            </template>
          </el-input>
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
          >
            <template #cell="{ row, thead, index }">
              <span v-if="thead.name === ''">
                {{ start + index + 1 }}
              </span>
              <span
                v-if="thead.name === t('SBL_STOCK_CODE')"
                class="text-primary"
              >
                <router-link :to="{ name: 'DetailSblInquiry', query: { ch: row.stkno } }">
                  {{ row.stkno }}
                </router-link>
              </span>
              <span v-if="thead.name === t('SBL_REAL_TIME_AVAILABLE_VOL')">
                {{ Number(row[thead.key]).toLocaleString() }}
              </span>
            </template>
          </generic-table>
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
    </div>
  </div>
</template>
<style lang="scss" scoped>
:deep(.el-input__wrapper) {
  padding-top: 4px;
  padding-bottom: 4px;
}
</style>
