<script setup>
let tabComponent = null;

const tseTab = ref(null);
const otcTab = ref(null);

const { t } = useI18n();
const store = useGetDetailDataStore();
const { getData } = store;
const detailData = ref({});

const changeTabType = async (value) => {
  tabType.value = value;

  if (value === 'tse') {
    detailData.value.describe = 'CNT_TRADE_NOTE';
  } else if (value === 'otc') {
    detailData.value.describe = 'CNT_TRADE_NOTE1';
  }
  await fetchData();
};

const route = useRoute();
const router = useRouter();
const marketDetailData = ref([]);
const title = ref('');
const list = ref([]);
const { id } = route.query;
const { ch } = route.query;
const ex = computed(() => {
  return route.query.ex;
});
const tabType = ref('tse');
const type = ref('');
watch(
  () => route.query.id,
  (newId, oldId) => {
    if (newId !== oldId) {
      location.reload();
    }
  }
);

watch(
  () => route.fullPath,
  (newRoute, oldRoute) => {
    // vue router如果在同一個路由之下不會重新渲染
    // 因此必須透過偵測路由變化來重新渲染

    location.reload();
  }
);
const fetchData = async () => {
  try {
    if (tabType.value === 'tse') {
      await fetchStockInfoData(id, ch);
    } else {
      await fetchOddInfoData(id, ch);
    }
  } catch (error) {
    console.error(error.message);
  }
};
const fetchStockInfoData = async (fetchId, fetchCh) => {
  if (!fetchCh) {
    try {
      const stockInfoData = await getStockInfo(fetchId);
      const { msgArray } = useRefactorDetailData(stockInfoData.msgArray);
      if (msgArray.length > 0) {
        marketDetailData.value = msgArray;
        list.value = msgArray[0].recordList;
        if (msgArray[0]?.c && msgArray[0]?.n) {
          title.value = `${msgArray[0].c} ${msgArray[0].n}`; // 更新 title
        }
      } else {
        marketDetailData.value = [];
      }

      // list.value = [
      //   { a: '218.9000', f: '1', b: '0.00', g: '-', color: '#e05e5e' },
      //   { a: '218.8500', f: '11', b: '-', g: '-', color: '#e05e5e' },
      //   { a: '218.8000', f: '193', b: '-', g: '-', color: '#e05e5e' },
      //   { a: '218.7500', f: '263', b: '-', g: '-', color: '#e05e5e' },
      //   { a: '218.7000', f: '37', b: '-', g: '-', color: '#e05e5e' },
      //   { a: '0.00', f: '-', b: '218.6500', g: '2', color: '#e05e5e' },
      //   { a: '-', f: '-', b: '218.6000', g: '1', color: '#e05e5e' },
      //   { a: '-', f: '-', b: '218.5500', g: '2', color: '#e05e5e' },
      //   { a: '-', f: '-', b: '218.5000', g: '4', color: '#e05e5e' },
      //   { a: '-', f: '-', b: '218.4000', g: '2', color: '#e05e5e' },
      // ];
    } catch (error) {
      console.error(error.message);
    }
  } else {
    try {
      const data = await getStock(fetchCh);
      const msgData = data.msgArray[0];

      if (msgData) {
        const stockInfoData = await getStockInfo(`${msgData.ex}_${msgData.ch}`);
        const { msgArray } = useRefactorDetailData(stockInfoData.msgArray);
        marketDetailData.value = msgArray;
        list.value = msgArray[0].recordList;

        if (msgArray[0]?.c && msgArray[0]?.n) {
          title.value = `${msgArray[0].c} ${msgArray[0].n}`; // 更新 title
        }
      } else {
        // alert('查無股票名稱或代號');
        marketDetailData.value = [];
      }
    } catch (error) {
      console.error(error.message);
    }
  }
};
const fetchOddInfoData = async (fetchId, fetchCh) => {
  if (!fetchCh) {
    try {
      const oddInfoData = await getOddInfo(fetchId);
      const { msgArray } = useRefactorDetailData(oddInfoData.msgArray);
      marketDetailData.value = msgArray;
      list.value = msgArray[0].recordList;
    } catch (error) {
      console.error(error.message);
    }
  } else {
    const data = await getStock(fetchCh);
    const msgData = data.msgArray[0];
    if (msgData) {
      const stockInfoData = await getOddInfo(`${msgData.ex}_${msgData.ch}`);
      const { msgArray } = useRefactorDetailData(stockInfoData.msgArray);
      marketDetailData.value = msgArray;
      list.value = msgArray[0].recordList;
    } else {
      marketDetailData.value = [];
    }
  }
};

const goBack = () => {
  router.go(-1);
};

const print = () => {
  window.print();
};

let nIntervId;
const updateDataPeriodically = () => {
  nIntervId = setInterval(() => {
    fetchData();
  }, 5000);
};
onMounted(() => {
  detailData.value = getData;
  tabType.value = route.query.type || 'tse';
  fetchData();
  updateDataPeriodically();

  const triggerTabList = Array.from(document.querySelectorAll('#changeTypeTab button'));
  triggerTabList.forEach((triggerEl) => {
    tabComponent = new Tab(triggerEl);
  });
});

onUnmounted(() => {
  clearInterval(nIntervId);
});
</script>
<template>
  <!-- 標題 -->
  <div class="d-flex flex-wrap justify-content-between align-items-end">
    <span
      v-if="detailData.titleType"
      class="fs-2 text-black"
    >
      {{ t(detailData.titleType) }}
    </span>
    <span
      v-if="detailData.describe"
      class="text-center text-secondary m-0 lh-lg"
    >
      {{ t(detailData.describe) }}
    </span>
  </div>
  <div class="container-fluid">
    <div class="row p-0">
      <!-- 列印列 -->
      <div class="p-0 d-flex justify-content-between mt-3 justify-content-start">
        <div>
          <button
            type="button"
            class="btn btn-primary px-4 mb-3 me-3"
            @click="print()"
          >
            {{ t('PRINT_BUTTON') }}
          </button>
          <button
            v-if="detailData.showGoBack"
            type="button"
            class="btn btn-primary px-4 mb-3"
            @click="goBack"
          >
            {{ t('BACK_BUTTON') }}
          </button>
        </div>
        <template
          v-if="
            marketDetailData[0]?.i !== 'tidx.tw' &&
            marketDetailData[0]?.i !== 'oidx.tw' &&
            marketDetailData[0]?.bp !== '3'
          "
        >
          <ul
            id="changeTypeTab"
            ref="changeTypeTab"
            class="nav nav-pills mb-3 p-0 justify-content-end col"
            role="tablist"
          >
            <li
              class="nav-item"
              role="presentation"
            >
              <button
                id="tseTab"
                ref="tseTab"
                class="nav-link nav-left"
                :class="{ active: tabType === 'tse' }"
                data-bs-toggle="pill"
                data-bs-target="#pills-home"
                type="button"
                role="tab"
                aria-controls="pills-home"
                aria-selected="true"
                @click="changeTabType('tse')"
              >
                {{ t('QUOTES_QUOTATION') }}
              </button>
            </li>
            <li
              class="nav-item"
              role="presentation"
            >
              <button
                id="otcTab"
                ref="otcTab"
                class="nav-link nav-right"
                :class="{ active: tabType === 'otc' }"
                data-bs-toggle="pill"
                data-bs-target="#pills-profile"
                type="button"
                role="tab"
                aria-controls="pills-profile"
                aria-selected="false"
                @click="changeTabType('otc')"
              >
                {{ t('ODD_QUOTATION') }}
              </button>
            </li>
          </ul>
        </template>
      </div>
      <div
        id="pills-tabContent"
        class="tab-content p-0"
      >
        <div
          id="pills-home"
          class="tab-pane fade"
          :class="{ 'show active': tabType === 'tse' }"
          role="tabpanel"
          aria-labelledby="tseTab"
        >
          <div class="container-fluid">
            <!-- top -->
            <div class="row justify-content-center bg-white text-black rounded-4 border">
              <div class="col-12">
                <div class="row pt-c24 px-c28 align-items-center">
                  <div class="col-8 fs-16 fw-medium">
                    <span v-if="ex === 'tse'">[{{ t('FIBEST_TSE') }}]</span>
                    <span v-else-if="ex === 'otc'">[{{ t('FIBEST_OTC') }}]</span>
                    <span v-else-if="type === 'tse_c'">[{{ t('FIBEST_TIB') }}]</span>
                    <span v-else-if="ex === 'cross'">[{{ t('FIBEST_XM') }}]</span>
                    {{ title }}
                    <span
                      v-if="marketDetailData[0]?.ts === '1'"
                      class="text-secondary fs-16 text-custom-red"
                    >
                      ({{ t('REFERENCE') }})
                    </span>
                    <span class="text-secondary fs-16 fw-light text-custom-red ms-c24">
                      ({{ t('QUOTES_QUOTATION') }})
                    </span>
                  </div>
                  <div
                    class="col-4 d-flex flex-column align-items-end justify-content-end text-primary fw-medium"
                  >
                    <p class="fw-normal mb-0 text-secondary">
                      {{ t('UPDATE_TIME0') }} 5 {{ t('UPDATE_TIME1') }}
                    </p>
                    <p class="mb-0">{{ t('UNIT_STR') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-12">
                <div class="row pt-c12 px-c28 pb-c12 border-bottom">
                  <div class="col text-center">{{ t('ACC_TRADE_VOLUME') }}</div>
                  <div class="col text-center">{{ t('REFERENCE_PRICE_ODD') }}</div>
                  <div class="col text-center">{{ t('REFERENCE_VOLUME_ODD') }}</div>
                  <div class="col text-center">{{ t('OPEN_PRICE') }}</div>
                  <div class="col text-center">{{ t('HIGHEST_PRICE') }}</div>
                  <div class="col text-center">{{ t('LOWEST_PRICE') }}</div>
                  <div class="col text-center">{{ t('COMMENT') }}</div>
                </div>
              </div>
              <div
                v-if="marketDetailData?.length !== 0"
                class="col-12"
              >
                <div
                  v-for="item in marketDetailData"
                  :key="item['@']"
                  class="row px-c28 pt-c12 pb-c12"
                >
                  <div class="col text-center">{{ item.v || '-' }}</div>
                  <div class="col text-center">
                    {{
                      item.ts == '0' || isNaN(Number(item.pz)) ? '-' : Number(item.pz).toFixed(2)
                    }}
                  </div>
                  <div class="col text-center">
                    {{ item.ts == '0' || isNaN(Number(item.ps)) ? '-' : Number(item.ps) }}
                  </div>
                  <div class="col text-center">
                    {{ isNaN(Number(item.o)) ? '-' : Number(item.o).toFixed(2) }}
                  </div>
                  <div class="col text-center">
                    {{ !isNaN(Number(item.h)) ? Number(item.h).toFixed(2) : '-' }}
                  </div>
                  <div class="col text-center">
                    {{ !isNaN(Number(item.l)) ? Number(item.l).toFixed(2) : '-' }}
                  </div>
                  <div v-if="false">
                    <!-- 說明欄位應該會在 暫停交易 的時候出現相關邏輯 -->
                    <!-- 但因為暫停交易本身不容易出現 -->
                    <!-- 未來開發者如果遇到需要調整的機會，請記得一併調整 -->
                    <!-- 相關邏輯請查閱舊網站ctrl.group.js -->
                  </div>

                  <div class="col text-center"></div>
                </div>
              </div>
              <div
                v-else
                class="col-12"
              >
                <p
                  class="text-center mb-0 fs-18 text-custom-red pt-c16 pb-c28"
                  style="letter-spacing: 1.41px"
                >
                  {{ t('TABLE_NO_DATA') }}
                </p>
              </div>
            </div>
            <div class="row justify-content-center mt-c12">
              <div class="col-12">
                <div class="row justify-content-between flex-nowrap">
                  <div class="left-side bg-white text-black rounded-4 border align-self-start">
                    <div class="row fw-medium px-c28 mb-c16 pt-c24">
                      <div class="col-12">
                        {{ t('FIBEST_TITLE') }}
                        <span
                          v-if="marketDetailData[0]?.ts === '1'"
                          class="text-secondary fs-6 text-custom-red"
                        >
                          ({{ t('REFERENCE') }})
                        </span>
                      </div>
                    </div>
                    <div class="row fw-normal px-c28 pb-c12 mb-c12 border-bottom">
                      <div class="col-3 text-center">{{ t('GROUP_TIMESTR') }}</div>
                      <div class="col-2 text-center">{{ t('LATEST_PRICE') }}</div>
                      <div class="col text-center">{{ t('PRICE_CHANGE_2') }}</div>
                      <div class="col-2 text-center">{{ t('TRADE_VOLUME') }}</div>
                    </div>
                    <template v-if="marketDetailData?.length !== 0">
                      <div
                        v-for="item in marketDetailData"
                        :key="item['@']"
                        class="row fw-normal px-c28 mb-c12"
                      >
                        <div class="col-3 text-center">{{ item.t || '-' }}</div>
                        <div
                          class="col-2 text-center"
                          :style="{ color: item.pColor }"
                        >
                          {{ !isNaN(Number(item.z)) ? Number(item.z).toFixed(2) : '-' }}
                        </div>
                        <div
                          v-if="item.colorType == 'default'"
                          class="col text-center"
                          :style="{ color: item.pColor }"
                        >
                          {{
                            isNaN(item.diff) || item.diff === undefined
                              ? '-'
                              : `${item.diff} ${item.zHtmlStr}`
                          }}
                        </div>
                        <div
                          v-else
                          class="col text-center"
                          :style="{ color: item.pColor }"
                        >
                          <template
                            v-if="
                              item.colorType == 'downColor' &&
                              (!isNaN(item.diff) || item.diff !== undefined)
                            "
                          >
                            <i class="bi bi-caret-up-fill">
                              {{
                                isNaN(item.diff) || item.diff === undefined
                                  ? '-'
                                  : `${Math.abs(item.diff).toFixed(2)} ${item.zHtmlStr}`
                              }}
                            </i>
                          </template>
                          <template v-else>
                            {{
                              isNaN(item.diff) || item.diff === undefined
                                ? '-'
                                : `${Math.abs(item.diff).toFixed(2)} ${item.zHtmlStr}`
                            }}
                          </template>
                        </div>
                        <div class="col-2 text-center">{{ item.s || '-' }}</div>
                      </div>
                    </template>
                    <div
                      v-else
                      class="col-12"
                    >
                      <p
                        class="text-center mb-0 fs-18 text-custom-red pb-c12"
                        style="letter-spacing: 1.41px"
                      >
                        {{ t('TABLE_NO_DATA') }}
                      </p>
                    </div>
                  </div>
                  <div
                    v-if="marketDetailData[0]?.it !== 't'"
                    class="right-side bg-white text-black rounded-4 border"
                  >
                    <div class="row fw-normal px-c28 pb-c12 pt-c24 border-bottom">
                      <div class="col-3 text-center">{{ t('FIBEST_BID_VOLUME') }}</div>
                      <div class="col-3 text-center">{{ t('FIBEST_BID_PRICE') }}</div>
                      <div class="col-3 text-center">{{ t('FIBEST_ASK_PRICE') }}</div>
                      <div class="col-3 text-center">{{ t('FIBEST_ASK_VOLUME') }}</div>
                    </div>
                    <template v-if="list.length !== 0">
                      <div
                        v-for="(item, index) in list"
                        :key="item['a'] + item['b'] + item['f'] + item['g'] + item['color']"
                        class="row py-c12 px-c28"
                        :class="{ 'border-bottom': index !== list.length - 1 }"
                      >
                        <div class="col-3 text-center">
                          {{ !isNaN(Number(item.g)) ? Number(item.g) : '-' }}
                        </div>
                        <div
                          class="col-3 text-center"
                          :style="{ color: item.b === '-' ? 'black' : item.color }"
                        >
                          <template v-if="!isNaN(item.b) && Number(item.b) === 0">
                            {{ $t('MARKET_ORDER') }}
                          </template>
                          <template v-else-if="!isNaN(Number(item.b)) && Number(item.b) > 0">
                            {{ Number(item.b).toFixed(2) }}
                          </template>
                          <template v-else>-</template>
                        </div>
                        <div
                          class="col-3 text-center"
                          :style="{ color: item.a === '-' ? 'black' : item.color }"
                        >
                          <template v-if="!isNaN(item.a) && Number(item.a) === 0">
                            {{ $t('MARKET_ORDER') }}
                          </template>
                          <template v-else-if="!isNaN(Number(item.a)) && Number(item.a) > 0">
                            {{ Number(item.a).toFixed(2) }}
                          </template>
                          <template v-else>-</template>
                        </div>
                        <div class="col-3 text-center">
                          <template v-if="!isNaN(Number(item.f))">
                            {{ Number(item.f) }}
                          </template>
                          <template v-else>-</template>
                        </div>
                      </div>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div
          id="pills-profile"
          ref="otcTab"
          class="tab-pane fade"
          :class="{ 'show active': tabType === 'otc' }"
          role="tabpanel"
          aria-labelledby="otcTab"
        >
          <div class="container-fluid">
            <div
              class="row justify-content-center bg-white text-black rounded-4 border align-items-center"
            >
              <div class="col-12">
                <div class="row pt-c24 px-c28 align-items-center">
                  <div class="col-8 fs-16 fw-medium">
                    <span v-if="ex === 'tse'">[{{ t('FIBEST_TSE') }}]</span>
                    <span v-else-if="ex === 'otc'">[{{ t('FIBEST_OTC') }}]</span>
                    <span v-else-if="type === 'tse_c'">[{{ t('FIBEST_TIB') }}]</span>
                    <span v-else-if="ex === 'cross'">[{{ t('FIBEST_XM') }}]</span>
                    {{ title }}
                    <span class="text-secondary fs-6 fw-light text-custom-red ms-c24">
                      ({{ t('ODD_QUOTATION') }})
                    </span>
                  </div>
                  <div
                    class="col-4 d-flex flex-column align-items-end justify-content-end text-primary fw-medium"
                  >
                    <p class="fw-normal mb-0 text-secondary">
                      {{ t('UPDATE_TIME0') }} 5 {{ t('UPDATE_TIME1') }}
                    </p>
                    <p class="mb-0">{{ t('UNIT_STR') }}</p>
                  </div>
                </div>
              </div>
              <div class="col-12">
                <div class="row pt-c12 px-c28 pb-c12 border-bottom align-items-center">
                  <div class="col text-center">{{ t('TRADE_TIME') }}</div>
                  <div class="col text-center">{{ t('LATEST_PRICE') }}</div>
                  <div
                    class="col-2 text-center"
                    v-html="t('PRICE_CHANGE')"
                  ></div>
                  <div
                    class="col-1 text-center"
                    v-html="t('PRE_TRADE_VOLUME')"
                  ></div>
                  <div
                    class="col text-center"
                    v-html="t('ACC_TRADE_VOLUME')"
                  ></div>
                  <div class="col text-center">{{ t('HIGHEST_PRICE_2') }}</div>
                  <div class="col text-center">{{ t('LOWEST_PRICE_2') }}</div>
                  <div class="col-1 text-center">{{ t('COMMENT') }}</div>
                </div>
              </div>
              <div
                v-if="marketDetailData?.length > 0"
                class="col-12"
              >
                <div
                  v-for="item in marketDetailData"
                  :key="item['@']"
                  class="row px-c28 pt-c12 pb-c12 bg-light-green"
                >
                  <div class="col text-center">{{ item.tt || '-' }}</div>
                  <div
                    class="col text-center"
                    :style="{ color: item.pColor }"
                  >
                    {{ !isNaN(Number(item.z)) ? Number(item.z).toFixed(2) : '-' }}
                  </div>
                  <div
                    v-if="item.colorType == 'default' && typeof item.z !== 'undefined'"
                    class="col-2 text-center"
                    :style="{ color: item.pColor }"
                  >
                    {{ `${item.diff} ${item.zHtmlStr}` }}
                  </div>
                  <div
                    v-else
                    class="col-2 text-center"
                    :style="{ color: item.pColor }"
                  >
                    <template v-if="item.colorType == 'downColor' && typeof item.z !== 'undefined'">
                      <i class="bi bi-caret-up-fill">
                        {{ `${Math.abs(item.diff).toFixed(2)} ${item.zHtmlStr}` }}
                      </i>
                    </template>
                    <template v-else-if="typeof item.z === 'undefined'">- -</template>
                    <template v-else>
                      <i class="bi bi-caret-down-fill">{{ `${item.diff} ${item.zHtmlStr}` }}</i>
                    </template>
                  </div>
                  <div class="col-1 text-center">
                    {{ !isNaN(Number(item.s)) ? Number(item.s).toFixed(0) : '-' }}
                  </div>
                  <div class="col text-center">
                    {{ !isNaN(Number(item.v)) ? Number(item.v).toFixed(0) : '-' }}
                  </div>
                  <div class="col text-center">
                    {{ !isNaN(Number(item.h)) ? Number(item.h).toFixed(2) : '-' }}
                  </div>
                  <div class="col text-center">
                    {{ !isNaN(Number(item.l)) ? Number(item.l).toFixed(2) : '-' }}
                  </div>
                  <div class="col-1 text-center">-</div>
                </div>
              </div>
              <div
                v-else
                class="col-12"
              >
                <div class="row pt-c12 pb-c12 px-c28 justify-content-between bg-light-green">
                  <div class="col">{{ '-' }}</div>
                  <div class="col">{{ '-' }}</div>
                  <div class="col-2">{{ '-' }}</div>
                  <div class="col-1">{{ '-' }}</div>
                  <div class="col">{{ '-' }}</div>
                  <div class="col">{{ '-' }}</div>
                  <div class="col">{{ '-' }}</div>
                  <div class="col-1">-</div>
                </div>
              </div>
            </div>
            <div class="row justify-content-center mt-c12">
              <div class="col-12">
                <div class="row justify-content-between flex-nowrap">
                  <div class="left-side bg-white text-black rounded-4 border align-self-start">
                    <div class="row fw-medium px-c28 mb-c16 pt-c24">
                      <div class="col-12">
                        {{ t('REFERENCE_ODD_TITLE') }}
                        <span class="text-secondary fs-16 ms-c24 fw-light text-custom-red">
                          ({{ t('REFERENCE_ODD_REFRESH_TIME') }})
                        </span>
                      </div>
                    </div>
                    <div class="row fw-normal px-c28 pb-c12 mb-c12 border-bottom">
                      <div class="col-4 text-center">{{ t('REFERENCE_TIME') }}</div>
                      <div class="col-4 text-center">{{ t('REFERENCE_PRICE_ODD') }}</div>
                      <div class="col-4 text-center">{{ t('REFERENCE_VOLUME_ODD') }}</div>
                    </div>
                    <template v-if="marketDetailData?.length !== 0">
                      <div
                        v-for="item in marketDetailData"
                        :key="item['@']"
                        class="row fw-normal px-c28 mb-c12"
                      >
                        <div class="col-4 text-center">{{ item.ts == '0' ? '-' : item.qt }}</div>
                        <div class="col-4 text-center">
                          {{
                            item.ts === '0' || isNaN(Number(item.pz))
                              ? '-'
                              : Number(item.pz).toFixed(2)
                          }}
                        </div>
                        <div class="col-4 text-center">
                          {{ item.ts == '0' || isNaN(Number(item.ps)) ? '-' : Number(item.ps) }}
                        </div>
                      </div>
                    </template>
                    <template v-else>
                      <div class="row px-c28 mb-c12">
                        <div class="col-4">{{ '-' }}</div>
                        <div class="col-4">{{ '-' }}</div>
                        <div class="col-4">{{ '-' }}</div>
                      </div>
                    </template>
                  </div>
                  <div
                    v-if="marketDetailData?.length !== 0"
                    class="right-side bg-white text-black rounded-4 border"
                  >
                    <div class="row fw-normal px-c28 pt-c24">
                      <div class="col-12 fs-16 fw-medium">
                        <span>{{ t('FIBEST_TITLE_2') }}</span>
                        <span class="text-custom-red fs-16 fw-light ms-c16">
                          ({{ t('REFERENCE_ODD_REFRESH_TIME') }})
                        </span>
                      </div>
                    </div>
                    <div class="row fw-normal px-c28 pb-c12 pt-c16 border-bottom">
                      <div class="col-3 text-center">{{ t('FIBEST_BID_VOLUME') }}</div>
                      <div class="col-3 text-center">{{ t('FIBEST_BID_PRICE') }}</div>
                      <div class="col-3 text-center">{{ t('FIBEST_ASK_PRICE') }}</div>
                      <div class="col-3 text-center">{{ t('FIBEST_ASK_VOLUME') }}</div>
                    </div>
                    <template v-if="marketDetailData?.length > 0">
                      <div
                        v-for="(item, index) in list"
                        :key="item['a'] + item['b'] + item['f'] + item['g'] + item['color']"
                        class="row py-c12 px-c28"
                        :class="{ 'border-bottom': index !== list.length - 1 }"
                      >
                        <div class="col-3 text-center">
                          {{ !isNaN(Number(item.g)) ? Number(item.g) : '-' }}
                        </div>
                        <div
                          class="col-3 text-center"
                          :style="{ color: item.b === '-' ? 'black' : item.color }"
                        >
                          <template v-if="!isNaN(item.b) && Number(item.b) === 0">
                            {{ $t('MARKET_ORDER') }}
                          </template>
                          <template v-else-if="!isNaN(Number(item.b)) && Number(item.b) > 0">
                            {{ Number(item.b).toFixed(2) }}
                          </template>
                          <template v-else>-</template>
                        </div>
                        <div
                          class="col-3 text-center"
                          :style="{ color: item.a === '-' ? 'black' : item.color }"
                        >
                          <template v-if="!isNaN(item.a) && Number(item.a) === 0">
                            {{ $t('MARKET_ORDER') }}
                          </template>
                          <template v-else-if="!isNaN(Number(item.a)) && Number(item.a) > 0">
                            {{ Number(item.a).toFixed(2) }}
                          </template>
                          <template v-else>-</template>
                        </div>
                        <div class="col-3 text-center">
                          <template v-if="!isNaN(Number(item.f))">
                            {{ Number(item.f) }}
                          </template>
                          <template v-else>-</template>
                        </div>
                      </div>
                    </template>
                    <template v-else>
                      <div
                        v-for="(item, index) in list"
                        :key="item + index"
                        class="row py-c12 px-c28"
                        :class="{ 'border-bottom': index !== list.length - 1 }"
                      >
                        <div class="col-3 text-center">{{ '-' }}</div>
                        <div class="col-3 text-center">{{ '-' }}</div>
                        <div class="col-3 text-center">{{ '-' }}</div>
                        <div class="col-3 text-center">{{ '-' }}</div>
                      </div>
                    </template>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <manual-table>
          <manual-table-item>
            {{ t('FIBEST_NOTE') }}
          </manual-table-item>
          <manual-table-item>{{ t('GROUP_NOTE') }}</manual-table-item>
          <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED5') }}</manual-table-item>
        </manual-table>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
$brand-tertiary: #134372;
$white: #ffffff;
$text-tertiary: #c8c8c8;

.left-side {
  width: 508px;
  @media (min-width: 1360px) {
    width: 542px;
  }
  @media (min-width: 1440px) {
    width: 572px;
  }
  @media (min-width: 1520px) {
    width: 603px;
  }
  @media (min-width: 1600px) {
    width: 635px;
  }
}
.right-side {
  width: 743px;
  @media (min-width: 1360px) {
    width: 794px;
  }
  @media (min-width: 1440px) {
    width: 836px;
  }
  @media (min-width: 1520px) {
    width: 882px;
  }
  @media (min-width: 1600px) {
    width: 929px;
  }
}
.nav-link {
  color: $text-tertiary;
  background-color: $white;
}
.empty-text {
  font-size: 18px;
  color: #e05e5e;
  text-align: center;
  letter-spacing: 1.59px;
  margin: 28px 0;
}
</style>
