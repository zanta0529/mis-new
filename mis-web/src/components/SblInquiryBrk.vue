<script setup>
const { t } = useI18n();
const brkidInput = ref('');
const stkInput = ref('');

const debouncedChangePage = debounce((eventString) => {
  changePage(eventString);
}, 300);
watch(brkidInput, (newVal, oldVal) => {
  debouncedChangePage('filter');
});
watch(stkInput, (newVal, oldVal) => {
  debouncedChangePage('filter');
});

const SBLBrokersTheadList = [
  { name: '', key: 'index', width: '4%', theadAlignment: 'center', tbodyAlignment: 'end' },
  {
    name: t('SBL_LENDING_FROM'),
    key: 'brkid',
    width: '15%',
    theadAlignment: 'center',
    tbodyAlignment: 'center',
  },
  {
    name: t('SBL_STOCK_CODE'),
    key: 'stkno',
    width: '10%',
    theadAlignment: 'center',
    tbodyAlignment: 'center',
  },
  {
    name: t('SBL_STOCK_NAME'),
    key: 'stkname',
    width: '15%',
    theadAlignment: 'center',
    tbodyAlignment: 'center',
  },
  {
    name: t('SBL_AVAILABLE_'),
    key: 'avishr',
    width: '15%',
    theadAlignment: 'end',
    tbodyAlignment: 'end',
  },
  {
    name: t('SBL_SBL_RATE'),
    key: 'lonrate',
    width: '10%',
    theadAlignment: 'end',
    tbodyAlignment: 'end',
  },
  {
    name: t('SBL_FEE_RATE'),
    key: 'feerate',
    width: '10%',
    theadAlignment: 'end',
    tbodyAlignment: 'end',
  },
  {
    name: t('SBL_COMMENTS'),
    key: 'mark',
    width: 'auto',
    theadAlignment: 'start',
    tbodyAlignment: 'start',
  },
];

const database = ref([]);
const nowDataList = ref([]);
const totalDataLength = ref(0);
const page = ref(1);
const pageSize = ref(10);
const pageSizeList = [10, 20, 50, 100];

const changePageSize = (inputPageSize) => {
  currentPageSize.value = inputPageSize;
  changePage();
};

const changePage = (eventString) => {
  const filteredData = [];
  let brkArrHasEmpty = false;
  let stkArrHasEmpty = false;
  const brkArr = brkidInput.value.split(' ').filter((brkItem) => {
    if (brkItem !== '') {
      return brkItem;
    }
    brkArrHasEmpty = true;
    return false;
  });
  const stkArr = stkInput.value.split(' ').filter((brkItem) => {
    if (brkItem !== '') {
      return brkItem;
    }
    stkArrHasEmpty = true;
    return false;
  });
  // 如果都沒有值, 直接回傳全部資料
  if (brkArr.length === 0 && stkArr.length === 0) {
    // 單位代號名稱,股票代號名稱皆為空陣列
    filteredData.push(...database.value);
    // 如果單位有值, 股票沒有值,則判斷brkArrHasEmpty是否為true
  } else if (brkArr.length !== 0 && stkArr.length === 0) {
    // 單位代號名稱有值,股票代號名稱為空陣列
    if (!brkArrHasEmpty) {
      // 單位代號名稱陣列沒有空白
      for (let i = 0; i < brkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          return (
            (item.brkid.includes(brkArr[i]) || item.brkname.includes(brkArr[i])) &&
            !filteredData.find((filterItem) => filterItem.brkid === item.brkid)
          );
        });
        filteredData.push(...pushArr);
      }
    } else {
      // 單位代號名稱陣列有空白
      for (let i = 0; i < brkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          return (
            item.brkid.includes(brkArr[i]) ||
            (item.brkname.includes(brkArr[i]) &&
              !filteredData.find((filterItem) => filterItem.brkid === item.brkid))
          );
        });
        filteredData.push(...pushArr);
      }
      const otherData = database.value.filter((item) => {
        return !brkArr.find((brkItem) => brkItem === item.brkid || brkItem === item.brkname);
      });
      filteredData.push(...otherData);
    }
    // 如果股票有值, 單位沒有值,stkArrHasEmpty
  } else if (brkArr.length === 0 && stkArr.length !== 0) {
    // 單位代號名稱為空陣列,股票代號名稱有值
    if (!stkArrHasEmpty) {
      // 股票代號名稱沒有空白
      for (let i = 0; i < stkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          return (
            (item.stkno.includes(stkArr[i]) || item.stkname.includes(stkArr[i])) &&
            !filteredData.find((filterItem) => filterItem.uni_id === item.uni_id)
          );
        });
        filteredData.push(...pushArr);
      }
      filteredData.sort((a, b) => a.brkid - b.brkid);
    } else {
      // 股票代號名稱有空白
      // 儲存獲取到的股票代號，以避免股票名稱無法被判斷
      const stkUniqueIdArr = [];
      for (let i = 0; i < stkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          if (item.stkno.includes(stkArr[i]) || item.stkname.includes(stkArr[i])) {
            stkUniqueIdArr.push(item.uni_id);
            return true;
          }
        });

        filteredData.push(...pushArr);
      }

      filteredData.sort((a, b) => a.brkid - b.brkid);
      const otherData = database.value.filter((item) => {
        return !stkUniqueIdArr.find((brkItem) => brkItem === item.brkid + item.stkno);
      });
      filteredData.push(...otherData);
    }
  } else if (brkArr.length !== 0 && stkArr.length !== 0) {
    // 單位代號名稱有值, 股票代號名稱有值;

    if (!brkArrHasEmpty && !stkArrHasEmpty) {
      // 如果都不是空字串
      // 單位代號及股票代號名稱陣列都沒有空白
      const tempArr = [];
      for (let i = 0; i < brkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          return item.brkid.includes(brkArr[i]) || item.brkname.includes(brkArr[i]);
        });
        tempArr.push(...pushArr);
      }
      for (let i = 0; i < stkArr.length; i++) {
        const pushArr = tempArr.filter((item) => {
          return (
            (item.stkno.includes(stkArr[i]) || item.stkname.includes(stkArr[i])) &&
            !filteredData.find((filterItem) => filterItem.uni_id === item.uni_id)
          );
        });
        filteredData.push(...pushArr);
      }
    } else if (brkArrHasEmpty && !stkArrHasEmpty) {
      // 單位代號名稱陣列有空白,股票代號名稱陣列沒有空白
      const tempArr = [];
      for (let i = 0; i < stkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          return item.stkno.includes(stkArr[i]) || item.stkname.includes(stkArr[i]);
        });
        tempArr.push(...pushArr);
      }
      const filterUniId = [];
      for (let i = 0; i < brkArr.length; i++) {
        const pushArr = tempArr.filter((item) => {
          if (
            (item.brkid.includes(brkArr[i]) || item.brkname.includes(brkArr[i])) &&
            !filteredData.find((filterItem) => filterItem.uni_id === item.uni_id)
          ) {
            filterUniId.push(item.uni_id);
            return true;
          }
        });
        filteredData.push(...pushArr);
      }
      const otherData = tempArr.filter((item) => {
        return !filterUniId.find((brkItem) => brkItem === item.uni_id);
      });
      filteredData.push(...otherData);

      // brkid由小到大，brkid相同的情況下，stkno由小到大
      filteredData.sort((a, b) => {
        if (a.brkid === b.brkid) {
          return a.stkno.localeCompare(b.stkno);
        }
        return a.brkid - b.brkid;
      });
    } else if (!brkArrHasEmpty && stkArrHasEmpty) {
      // 單位代號名稱陣列沒有空白,股票代號名稱陣列有空白
      const tempArr = [];
      const filterUniId = [];
      for (let i = 0; i < brkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          return item.brkid.includes(brkArr[i]) || item.brkname.includes(brkArr[i]);
        });
        tempArr.push(...pushArr);
      }
      for (let i = 0; i < stkArr.length; i++) {
        const pushArr = tempArr.filter((item) => {
          if (
            (item.stkno.includes(stkArr[i]) || item.stkname.includes(stkArr[i])) &&
            !filteredData.find((filterItem) => filterItem.uni_id === item.uni_id)
          ) {
            filterUniId.push(item.uni_id);
            return true;
          }
        });
        filteredData.push(...pushArr);
      }
      const otherData = tempArr.filter((item) => {
        return !filterUniId.find((brkItem) => brkItem === item.uni_id);
      });
      otherData.sort((a, b) => {
        if (a.brkid === b.brkid) {
          return a.stkno.localeCompare(b.stkno);
        }
        return a.brkid - b.brkid;
      });
      filteredData.push(...otherData);
    } else {
      // 單位代號名稱陣列有空白,股票代號名稱陣列有空白
      const tempArr = [];
      const filterUniId = [];
      for (let i = 0; i < brkArr.length; i++) {
        const pushArr = database.value.filter((item) => {
          return item.brkid.includes(brkArr[i]) || item.brkname.includes(brkArr[i]);
        });
        tempArr.push(...pushArr);
      }
      for (let i = 0; i < stkArr.length; i++) {
        const pushArr = tempArr.filter((item) => {
          if (
            (item.stkno.includes(stkArr[i]) || item.stkname.includes(stkArr[i])) &&
            !filteredData.find((filterItem) => filterItem.uni_id === item.uni_id)
          ) {
            filterUniId.push(item.uni_id);
            return true;
          }
        });
        filteredData.push(...pushArr);
      }
      const otherData = database.value.filter((item) => {
        return !filterUniId.find((brkItem) => brkItem === item.uni_id);
      });

      otherData.sort((a, b) => {
        if (a.brkid === b.brkid) {
          return a.stkno.localeCompare(b.stkno);
        }
        return a.brkid - b.brkid;
      });
      filteredData.push(...otherData);
    }
  }

  if (eventString === 'filter') {
    currentPage.value = 1;
  }
  // 在originData.value加入index
  const filteredData2 = filteredData.map((item, index) => {
    return {
      ...item,
      index: index + 1,
    };
  });
  totalDataLength.value = filteredData2.length;
  nowDataList.value = filteredData2.slice(
    (currentPage.value - 1) * currentPageSize.value,
    currentPage.value * currentPageSize.value
  );
};

const PAGE_COUNT_LIMIT = computed(() => {
  const transformPage = 5; // 從哪一頁開始變化
  const limit = 9; // 限制顯示的頁數
  let start;
  if (currentPage.value <= transformPage) {
    start = 1;
  } else {
    start = Math.max(currentPage.value - 4, 1); // 確保 start 不會小於 1
  }
  let end;
  if (currentPage.value + limit > pageCount.value) {
    start = Math.max(pageCount.value - 9, 1); // 確保 start 不會小於 1
    end = pageCount.value;
  } else if (start === 1) {
    end = Math.min(limit, pageCount.value); // 確保 end 不會超過 pageCount.value
  } else {
    end = currentPage.value + limit - 5;
  }
  return Array.from({ length: Math.max(end - start + 1, 0) }, (_, index) => start + index);
});

const { currentPage, currentPageSize, pageCount, isFirstPage, isLastPage, prev, next } =
  useOffsetPagination({
    total: totalDataLength, // 資料總數量
    page,
    pageSize,
    onPageChange: changePage, // 當頁數改變時
    onPageSizeChange: changePage, // 當每頁顯示筆數改變時
  });

const getStockOriginData = async () => {
  try {
    const res = await getStockSblsBrk();
    [database.value] = res.msgArray;
    database.value = database.value.map((item, index) => {
      return {
        ...item,
        uni_id: item.brkid + item.stkno,
      };
    });
    totalDataLength.value = database.value.length;
    changePage();
  } catch (error) {
    console.error(error);
  }
};

let intervalGetStockOriginData = null;
onMounted(async () => {
  try {
    getStockOriginData();
    intervalGetStockOriginData = setInterval(() => {
      getStockOriginData();
    }, 5000);
  } catch (error) {
    console.error(error);
  }
});
onUnmounted(() => {
  debouncedChangePage.cancel();
  clearInterval(intervalGetStockOriginData);
});
</script>

<template>
  <div class="">
    <p
      class="mb-24 fs-32 fw-medium text-custom-black"
      style="letter-spacing: 2.82px"
    >
      {{ $t('SBL_AVAILABLE_VOLUME') }}
    </p>

    <div>
      <div class="mb-16">
        <div class="d-flex justify-content-start align-content-center">
          <div class="d-flex justify-content-start align-items-center ps-0 me-40">
            <span class="me-2 fs-18 text-custom-black lh-sm">{{ $t('SBL_INPUTBROKER') }}</span>
            <div
              class=""
              style="width: 413px"
            >
              <el-input
                v-model="brkidInput"
                :placeholder="$t('SBL_MULTIPLE_STOCK2')"
              >
                <template #suffix>
                  <el-icon
                    class="text-primary el-input__icon"
                    size="large"
                  >
                    <SearchIcon />
                  </el-icon>
                </template>
              </el-input>
            </div>
          </div>
          <div class="d-flex justify-content-start align-items-center ps-0">
            <span class="me-2 fs-18 text-custom-black lh-sm">{{ $t('SBL_INPUTSTOCK') }}</span>
            <div
              class=""
              style="width: 413px"
            >
              <el-input
                v-model="stkInput"
                :placeholder="$t('SBL_MULTIPLE_STOCK3')"
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
        </div>
      </div>
    </div>
    <p class="">{{ $t('SBL_MULTIPLE_STOCK') }}</p>
    <div class="rounded-4 bg-white pb-c4">
      <table
        class="table mb-c8 table-hover"
        data-toggle="table"
        data-fixed-columns="true"
      >
        <thead>
          <tr>
            <th
              v-for="headItem in SBLBrokersTheadList"
              :key="headItem.key"
              class="pt-c24"
              scope="col"
              :style="{ width: headItem.width, textAlign: headItem.theadAlignment }"
            >
              {{ headItem.name }}
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="bodyItem in nowDataList"
            :key="bodyItem.stkno + bodyItem.brkid"
          >
            <td
              v-for="headItem in SBLBrokersTheadList"
              :key="headItem.key"
              :style="{ textAlign: headItem.tbodyAlignment }"
            >
              <span v-if="headItem.key === 'index'">
                {{ bodyItem.index }}
              </span>
              <span v-if="headItem.key === 'brkid'">
                {{ bodyItem.brkid }} {{ bodyItem.brkname }}
              </span>
              <span
                v-if="headItem.key === 'stkno'"
                class="text-primary"
              >
                <router-link :to="{ name: 'DetailSblInquiry', query: { ch: bodyItem?.stkno } }">
                  {{ bodyItem.stkno }}
                </router-link>
              </span>
              <span
                v-if="headItem.key === 'stkname'"
                class="text-primary"
              >
                <router-link :to="{ name: 'DetailSblInquiry', query: { ch: bodyItem.stkno } }">
                  {{ bodyItem.stkname }}
                </router-link>
              </span>
              <span v-if="headItem.key === 'avishr'">
                {{
                  isNaN(Number(bodyItem.avishr)) ? '0' : Number(bodyItem.avishr).toLocaleString()
                }}
              </span>
              <span v-if="headItem.key === 'lonrate'">
                {{ bodyItem.lonrate }}
              </span>
              <span v-if="headItem.key === 'feerate'">
                {{ bodyItem.feerate }}
              </span>
              <span v-if="headItem.key === 'mark'">
                {{ bodyItem.mark }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="d-flex justify-content-between align-items-center mt-c12 flex-wrap">
      <div class="d-flex justify-content-start align-items-center w-100">
        <div class="me-2">
          <span class="text-secondary text-nowrap">
            {{ t('GROUP_NO_PRE_PAGE') }}
          </span>
          <button
            id="pageSizeSelect"
            class="btn bg-white btn-outline-line-gray text-secondary dropdown-toggle py-0 px-8"
            type="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            <span class="me-16">{{ pageSize }}</span>
          </button>
          <ul
            class="dropdown-menu text-primary"
            aria-labelledby="pageSizeSelect"
            data-bs-auto-close="true"
          >
            <li
              v-for="size in pageSizeList"
              :key="size"
            >
              <button
                class="dropdown-item"
                type="button"
                @click="changePageSize(size)"
              >
                {{ size }}
              </button>
            </li>
          </ul>
        </div>
        <div class="text-secondary text-nowrap">
          <span>{{ t('GROUP_TOTAL_RECORDS') }}{{ totalDataLength }}</span>
        </div>
      </div>
      <div class="w-100 d-flex justify-content-end">
        <ul class="d-flex gap-c16 mb-0 justify-content-center align-items-center">
          <li>
            <button
              class="btn btn-outline-primary py-1 px-c24 fs-16 lh-sm"
              style="letter-spacing: 1.41px"
              type="button"
              :disabled="isFirstPage"
              @click="currentPage = 1"
            >
              <!--  -->
              {{ t('FIRST_PAGE') }}
            </button>
          </li>
          <li>
            <button
              class="btn btn-outline-primary py-1 px-c24 fs-16 lh-sm"
              style="letter-spacing: 1.41px"
              type="button"
              :disabled="isFirstPage"
              @click="prev"
            >
              <!--  -->
              {{ t('PREV_PAGE') }}
            </button>
          </li>

          <li
            v-for="item in PAGE_COUNT_LIMIT"
            :key="item"
          >
            <button
              class="btn fs-16 py-c4 px-c10 lh-sm"
              :class="{
                'btn-brand-tertiary': item === page,
                'btn-outline-brand-tertiary': item !== page,
              }"
              :style="item === page ? 'cursor: default' : 'cursor: pointer'"
              type="button"
              @click="currentPage = item"
            >
              {{ item }}
            </button>
          </li>
          <li>
            <button
              class="btn btn-outline-primary py-1 px-c24 fs-16 lh-sm"
              style="letter-spacing: 1.41px"
              type="button"
              :disabled="isLastPage"
              @click="next"
            >
              <!--   -->
              {{ t('NEXT_PAGE') }}
            </button>
          </li>
          <li>
            <button
              class="btn btn-outline-primary py-1 px-c24 fs-16 lh-sm"
              style="letter-spacing: 1.41px"
              type="button"
              :disabled="isLastPage"
              @click="currentPage = pageCount"
            >
              {{ t('LAST_PAGE') }}
            </button>
          </li>
        </ul>
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
