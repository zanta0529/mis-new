<script setup>
const { t, locale } = useI18n();
const dropDownList = ref([]);
const title = ref('');
const selectValue = ref('');
const stkno = ref('');
const stockSbls = ref([]);
const data = ref([]);
const FA = ref([]);
const F3 = ref([]);
const F1 = ref([]);
const CA = ref([]);
const CA_5 = ref([]);
const C3 = ref([]);
const C3_5 = ref([]);
const C1 = ref([]);
const C1_5 = ref([]);

const fetchDropDownList = async (inputCh, select) => {
  try {
    if (inputCh) {
      const DropDownData = await getStockSblsList();
      dropDownList.value = DropDownData.msgArray;
      const findStkno = dropDownList.value.find((item) => item.stkno === inputCh);
      if (findStkno) {
        selectValue.value = `${findStkno?.stkname}(${findStkno?.stkno})`;
      } else {
        selectValue.value = '';
      }
      const res = await getStockSbls(inputCh);
      title.value = `${res.detail.stkname}(${res.detail.stkno})`;
      updateData(res);
    } else if (select) {
      const res = await getStockSbls(select);
      updateData(res);
    } else {
      const DropDownData = await getStockSblsList();
      if (DropDownData !== undefined && DropDownData.msgArray.length > 0) {
        dropDownList.value = DropDownData.msgArray;
        selectValue.value = `${dropDownList.value[0]?.stkname}(${dropDownList.value[0]?.stkno})`;
        if (dropDownList.value) {
          const res = await getStockSbls(dropDownList.value[0].stkno);
          updateData(res);
        }
      } else {
        dropDownList.value = [];
        selectValue.value = '';
      }
    }
  } catch (error) {
    console.error('Error fetching data:', error.message);
  }
};

const updateData = (res) => {
  stockSbls.value = res;
  FA.value = res.FA.detail;
  F3.value = res.F3.detail;
  F1.value = res.F1.detail;
  FA.value.unshift(t('SBL_10_RECALL_NOTICE'));
  F3.value.unshift(t('SBL_3_RECALL_NOTICE'));
  F1.value.unshift(t('SBL_1_RECALL_NOTICE'));
  CA.value = res.CA.detail;
  CA_5.value = res.CA_5;
  C3.value = res.C3.detail;
  C3_5.value = res.C3_5;
  C1.value = res.C1.detail;
  C1_5.value = res.C1_5;
  updateDataArray();
};

const updateDataArray = () => {
  data.value = [
    [{ ...FA.value }, { ...F3.value }, { ...F1.value }],
    [{ ...CA.value }],
    [
      { df: '', dq: '', cf: CA_5.value.cf1, cq: CA_5.value.cq1 },
      { df: '', dq: '', cf: CA_5.value.cf2, cq: CA_5.value.cq2 },
      { df: '', dq: '', cf: CA_5.value.cf3, cq: CA_5.value.cq3 },
      { df: '', dq: '', cf: CA_5.value.cf4, cq: CA_5.value.cq4 },
      { df: '', dq: '', cf: CA_5.value.cf5, cq: CA_5.value.cq5 },
      { df: CA_5.value.df1, dq: CA_5.value.dq1, cf: '', cq: '' },
      { df: CA_5.value.df2, dq: CA_5.value.dq2, cf: '', cq: '' },
      { df: CA_5.value.df3, dq: CA_5.value.dq3, cf: '', cq: '' },
      { df: CA_5.value.df4, dq: CA_5.value.dq4, cf: '', cq: '' },
      { df: CA_5.value.df5, dq: CA_5.value.dq5, cf: '', cq: '' },
    ],
    [{ ...C3.value }],
    [
      { df: '', dq: '', cf: C3_5.value.cf1, cq: C3_5.value.cq1 },
      { df: '', dq: '', cf: C3_5.value.cf2, cq: C3_5.value.cq2 },
      { df: '', dq: '', cf: C3_5.value.cf3, cq: C3_5.value.cq3 },
      { df: '', dq: '', cf: C3_5.value.cf4, cq: C3_5.value.cq4 },
      { df: '', dq: '', cf: C3_5.value.cf5, cq: C3_5.value.cq5 },
      { df: C3_5.value.df1, dq: C3_5.value.dq1, cf: '', cq: '' },
      { df: C3_5.value.df2, dq: C3_5.value.dq2, cf: '', cq: '' },
      { df: C3_5.value.df3, dq: C3_5.value.dq3, cf: '', cq: '' },
      { df: C3_5.value.df4, dq: C3_5.value.dq4, cf: '', cq: '' },
      { df: C3_5.value.df5, dq: C3_5.value.dq5, cf: '', cq: '' },
    ],
    [{ ...C1.value }],
    [
      { df: '', dq: '', cf: C1_5.value.cf1, cq: C1_5.value.cq1 },
      { df: '', dq: '', cf: C1_5.value.cf2, cq: C1_5.value.cq2 },
      { df: '', dq: '', cf: C1_5.value.cf3, cq: C1_5.value.cq3 },
      { df: '', dq: '', cf: C1_5.value.cf4, cq: C1_5.value.cq4 },
      { df: '', dq: '', cf: C1_5.value.cf5, cq: C1_5.value.cq5 },
      { df: C1_5.value.df1, dq: C1_5.value.dq1, cf: '', cq: '' },
      { df: C1_5.value.df2, dq: C1_5.value.dq2, cf: '', cq: '' },
      { df: C1_5.value.df3, dq: C1_5.value.dq3, cf: '', cq: '' },
      { df: C1_5.value.df4, dq: C1_5.value.dq4, cf: '', cq: '' },
      { df: C1_5.value.df5, dq: C1_5.value.dq5, cf: '', cq: '' },
    ],
    { ...stockSbls.value.N },
  ];
};

const router = useRouter();
const route = useRoute();
const ch = ref(route.query.ch);

watch(locale, (newValue, oldValue) => {
  if (newValue !== oldValue) {
    if (ch.value) {
      fetchDropDownList(ch.value, undefined);
    } else {
      fetchDropDownList();
    }
  }
});

const targetValue = async (value, stkno) => {
  title.value = '';
  router.push(`/detail-sblInquiry?ch=${stkno}`);
  ch.value = stkno;
  selectValue.value = `${value}(${stkno})`;
  await fetchDropDownList(undefined, stkno);
};

const print = () => {
  window.print();
};

let intervalGetFetchDropDownList = null;
onMounted(() => {
  fetchDropDownList(ch.value);
  intervalGetFetchDropDownList = setInterval(() => {
    fetchDropDownList(ch.value);
  }, 5000);
});
onUnmounted(() => {
  clearInterval(intervalGetFetchDropDownList);
});
</script>

<template>
  <div>
    <div class="d-flex justify-content-between align-items-end mb-32">
      <span class="fs-2 text-black">{{ t('SBL_MARKET_QUOTES') }}</span>
    </div>
    <div class="d-flex justify-content-between align-items-center">
      <span
        v-if="title"
        class="fs-7 text-black"
      >
        {{ title }}
      </span>
      <span
        v-else
        class="fs-7 text-black"
      >
        {{ selectValue }}
      </span>
      <div class="d-flex align-items-center">
        <span class="fs-7 text-black mx-2">{{ t('SBL_STOCK') }}</span>
        <div class="dropdown">
          <button
            id="dropdownMenuButton1"
            class="btn bg-white btn-outline-line-gray text-primary dropdown-toggle"
            type="button"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            {{ selectValue }}
          </button>
          <ul
            class="dropdown-menu text-primary"
            aria-labelledby="dropdownMenuButton1"
          >
            <li
              v-for="item in dropDownList"
              :key="item.stkno"
            >
              <button
                class="dropdown-item"
                :disabled="`${item?.stkname}(${item?.stkno})` === selectValue"
                @click="targetValue(item?.stkname, item.stkno)"
              >
                {{ item?.stkname }} {{ `(${item.stkno})` }}
              </button>
            </li>
          </ul>
        </div>
        <button
          type="button"
          class="btn btn-primary px-4 ms-3"
          @click="print()"
        >
          {{ t('PRINT_BUTTON') }}
        </button>
      </div>
    </div>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>{{ t('SBL_FIXED_RATE_TRANSACTION') }}</span>
      <span>{{ t('GROUP_TIMESTR') }} {{ stockSbls.FA?.time }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col"></div>
      <div class="col">{{ t('SBL_ACC_TRADE_DAY_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_LENDING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_BORROWING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_BORROWING_RATE') }}</div>
    </div>
    <template v-if="data">
      <div
        v-for="item in data[0]"
        class="row py-7 px-13 justify-content-between"
      >
        <div class="col">{{ item['0'] || '-' }}</div>
        <div class="col">{{ item['1'] || '-' }}</div>
        <div class="col">{{ item['2'] || '-' }}</div>
        <div class="col">{{ item['3'] || '-' }}</div>
        <div class="col">{{ item['4'] || '-' }}</div>
      </div>
    </template>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>{{ t('SBL_BID_OFFER_TRANSACTION') }} {{ t('SBL_BID_OFFER_TRANSACTION_NOTICE') }}</span>
      <span>{{ t('GROUP_TIMESTR') }} {{ stockSbls.CA?.time }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col">{{ t('SBL_ACC_TRADE_DAY_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_LENDING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_BORROWING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_MATCHED_VOLUME') }}</div>
      <div class="col">{{ t('SBL_MATCHED_RATE') }}</div>
    </div>
    <template v-if="data">
      <div
        v-for="item in data[1]"
        class="row py-7 px-13 justify-content-between"
      >
        <div class="col">{{ item['0'] || '-' }}</div>
        <div class="col">{{ item['1'] || '-' }}</div>
        <div class="col">{{ item['2'] || '-' }}</div>
        <div class="col">{{ item['3'] || '-' }}</div>
        <div class="col">{{ item['4'] || '-' }}</div>
      </div>
    </template>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>{{ t('SBL_FIVE_BEST_BIDS_AND_ASKS') }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col">{{ t('SBL_BORROWING_RATE') }}</div>
      <div class="col">{{ t('SBL_BORROWING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_LENDING_RATE') }}</div>
      <div class="col">{{ t('SBL_LENDING_VOLUME') }}</div>
    </div>
    <template v-if="data">
      <div
        v-for="item in data[2]"
        class="row py-7 px-13 justify-content-between"
      >
        <div class="col">{{ item.df1 || '-' }}</div>
        <div class="col">{{ item.dq1 || '-' }}</div>
        <div class="col">{{ item.cf5 || '-' }}</div>
        <div class="col">{{ item.cq5 || '-' }}</div>
      </div>
    </template>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>
        {{ t('SBL_BID_OFFER_TRANSACTION') }} {{ t('SBL_BID_OFFER_TRANSACTION_NOTICE_3') }}
      </span>
      <span>{{ t('GROUP_TIMESTR') }} {{ stockSbls.C3?.time }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col">{{ t('SBL_ACC_TRADE_DAY_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_LENDING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_BORROWING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_MATCHED_VOLUME') }}</div>
      <div class="col">{{ t('SBL_MATCHED_RATE') }}</div>
    </div>
    <template v-if="data">
      <div
        v-for="item in data[3]"
        class="row py-7 px-13 justify-content-between"
      >
        <div class="col">{{ item['0'] || '-' }}</div>
        <div class="col">{{ item['1'] || '-' }}</div>
        <div class="col">{{ item['2'] || '-' }}</div>
        <div class="col">{{ item['3'] || '-' }}</div>
        <div class="col">{{ item['4'] || '-' }}</div>
      </div>
    </template>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>{{ t('SBL_FIVE_BEST_BIDS_AND_ASKS') }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col">{{ t('SBL_BORROWING_RATE') }}</div>
      <div class="col">{{ t('SBL_BORROWING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_LENDING_RATE') }}</div>
      <div class="col">{{ t('SBL_LENDING_VOLUME') }}</div>
    </div>
    <template v-if="data">
      <div
        v-for="item in data[4]"
        class="row py-7 px-13 justify-content-between"
      >
        <div class="col">{{ item.df || '-' }}</div>
        <div class="col">{{ item.dq || '-' }}</div>
        <div class="col">{{ item.cf || '-' }}</div>
        <div class="col">{{ item.cq || '-' }}</div>
      </div>
    </template>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>
        {{ t('SBL_BID_OFFER_TRANSACTION') }} {{ t('SBL_BID_OFFER_TRANSACTION_NOTICE_1') }}
      </span>
      <span>{{ t('GROUP_TIMESTR') }} {{ stockSbls.C1?.time }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col">{{ t('SBL_ACC_TRADE_DAY_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_LENDING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_QUEUED_BORROWING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_MATCHED_VOLUME') }}</div>
      <div class="col">{{ t('SBL_MATCHED_RATE') }}</div>
    </div>
    <template v-if="data">
      <div
        v-for="item in data[5]"
        class="row py-7 px-13 justify-content-between"
      >
        <div class="col">{{ item['0'] || '-' }}</div>
        <div class="col">{{ item['1'] || '-' }}</div>
        <div class="col">{{ item['2'] || '-' }}</div>
        <div class="col">{{ item['3'] || '-' }}</div>
        <div class="col">{{ item['4'] || '-' }}</div>
      </div>
    </template>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>{{ t('SBL_FIVE_BEST_BIDS_AND_ASKS') }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col">{{ t('SBL_BORROWING_RATE') }}</div>
      <div class="col">{{ t('SBL_BORROWING_VOLUME') }}</div>
      <div class="col">{{ t('SBL_LENDING_RATE') }}</div>
      <div class="col">{{ t('SBL_LENDING_VOLUME') }}</div>
    </div>
    <template v-if="data">
      <div
        v-for="item in data[6]"
        class="row py-7 px-13 justify-content-between"
      >
        <div class="col">{{ item.df || '-' }}</div>
        <div class="col">{{ item.dq || '-' }}</div>
        <div class="col">{{ item.cf || '-' }}</div>
        <div class="col">{{ item.cq || '-' }}</div>
      </div>
    </template>
  </div>

  <div class="container-fluid bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="d-flex justify-content-between align-items-center pt-2 pb-3 px-13 fs-7 fw-medium">
      <span>{{ t('SBL_CUSTOMIZED_TRANSACTION') }}</span>
      <span>{{ t('GROUP_TIMESTR') }} {{ data[7]?.time }}</span>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col">{{ t('SBL_ACC_TRADE_DAY_VOLUME') }}</div>
    </div>
    <template v-if="data">
      <div class="row py-7 px-13 justify-content-between">
        <div class="col">{{ data[7]?.totmatchqty || '-' }}</div>
      </div>
    </template>
  </div>
</template>

<style lang="scss" scoped>
.dropdown-menu {
  max-height: 250px;
  overflow-y: auto;
}
.empty-text {
  font-size: 18px;
  color: #e05e5e;
  text-align: center;
  letter-spacing: 1.59px;
  margin: 28px 0;
}
</style>
