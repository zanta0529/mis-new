<script setup>
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  titleType: 'MARKET_OTC_INDEX_TITLE',
  describe: 'CNT_TRADE_NOTE',
  showTab: true,
  showGoBack: true,
});

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

const scrollToTable = (id) => {
  const element = document.getElementById(id);

  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
};

const marketTableData = ref([]);
const priceIndexData = ref([]);
const rewardIndexData = ref([]);
const indexData = ref([]);
const dealStatisData = ref([]);
const entrustStatisData = ref([]);
const additionalIndexData = ref([]);

const fetchData = async () => {
  try {
    const data = await getCategory('otc', 'OIDX');

    // 檢查是否成功取得 category 數據
    if (!data || !data.msgArray) {
      throw new Error('Failed to fetch category data');
    }

    const categoryList = data.msgArray.reduce((accumulator, current) => {
      const result = `${accumulator}${current.ex}_${current.ch}|`;
      return result;
    }, '');

    const stockInfoData = await getStockInfo(categoryList);
    // 檢查是否成功取得 stockInfo 數據
    if (!stockInfoData || !stockInfoData.msgArray) {
      throw new Error('Failed to fetch stockInfo data');
    }

    const { msgArray } = useCalculateValue(stockInfoData.msgArray);
    marketTableData.value = msgArray;

    const indices = [0, 34, 35, 46];
    const [priceIndex, rewardIndex, newIndex, additionalIndex] = indices.map((start, i, array) => {
      if (i === array.length - 1) {
        return marketTableData.value.slice(start);
      }
      return marketTableData.value.slice(start, array[i + 1]);
    });

    priceIndexData.value = priceIndex;
    rewardIndexData.value = rewardIndex;
    indexData.value = newIndex;
    additionalIndexData.value = additionalIndex;

    const statisDetail = await getStatis('otc');
    const { dealList } = useDealStaticData(statisDetail.detail);
    dealStatisData.value = dealList.value;

    const { entrustList } = useEntrustStatisData(statisDetail.detail);
    entrustStatisData.value = entrustList.value;
  } catch (error) {
    // 處理錯誤，例如顯示錯誤訊息或進行其他適當的處理
    console.error('Error fetching data:', error.message);
    // 可以根據實際情況進行其他處理，例如顯示錯誤訊息給用戶
  }
};

useWatchLang(fetchData);

let nIntervId;
const updateDataPeriodically = () => {
  nIntervId = setInterval(() => {
    fetchData();
  }, 5000);
};
onMounted(() => {
  fetchData();
  updateDataPeriodically();
  updateDetailDataInStore();
});

onUnmounted(() => {
  clearInterval(nIntervId);
});
</script>
<template>
  <div>
    <p class="fs-2 text-black fw-medium">{{ t('MARKET_OTC_INDEX_TITLE') }}</p>
    <button
      type="button"
      class="btn btn-primary px-4"
      @click="scrollToTable('price')"
    >
      {{ t('PRICE_INDEX') }}
    </button>
    <button
      type="button"
      class="btn btn-primary px-4 mx-3"
      @click="scrollToTable('reward')"
    >
      {{ t('MARKET_SUMMARY_BONDS3') }}
    </button>
    <button
      type="button"
      class="btn btn-primary px-4"
      @click="scrollToTable('statis')"
    >
      {{ t('MARKET_SUMMARY_STATISTICS') }}
    </button>

    <!-- type( 上櫃:otc / 跨市場:cross / null )-->
    <market-table
      id="price"
      :table-data="priceIndexData"
      :current-time="true"
      type="otc"
      :title="t('PRICE_INDEX')"
    ></market-table>
    <!-- 債券指數 -->
    <market-table
      id="reward"
      :table-data="rewardIndexData"
      type="null"
      :current-time="true"
      :title="t('MARKET_SUMMARY_BONDS')"
    ></market-table>
    <!-- 報酬指數 -->
    <market-table
      id="reward"
      :table-data="indexData"
      type="otc"
      :current-time="true"
      :title="t('MARKET_SUMMARY_BONDS3')"
    ></market-table>
    <!-- 跨市場指數 -->
    <market-table
      :table-data="additionalIndexData"
      type="cross"
      :title="t('MARKET_SUMMARY_BONDS2')"
      :table-show-current-time="true"
    ></market-table>

    <statistics-table
      id="statis"
      :table-data="dealStatisData"
      :title="t('MARKET_SUMMARY_TRADES')"
      type="otc"
    ></statistics-table>
    <statistics-table
      :table-data="entrustStatisData"
      :title="t('MARKET_SUMMARY_ORDERS')"
      type="otc"
    ></statistics-table>

    <manual-table>
      <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED1') }}</manual-table-item>
      <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED2') }}</manual-table-item>
      <manual-table-item>{{ t('GROUP_NOTE_FOR_POSTPONED3') }}</manual-table-item>
      <manual-table-item>
        {{ t('GROUP_NOTE_FOR_POSTPONED4') }}
      </manual-table-item>
      <manual-table-item>
        {{ t('FIBEST_NOTE') }}
      </manual-table-item>
    </manual-table>
  </div>
</template>

<style lang="scss" scoped></style>
