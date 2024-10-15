<script setup>
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  titleType: 'MARKET_TSE_INDEX_TITLE',
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
const currentTime = true;
const tableShowCurrentTime = true;

const fetchData = async () => {
  try {
    const data = await getCategory('tse', 'TIDX');

    // 檢查是否成功取得 category 數據
    if (!data || !data.msgArray) {
      throw new Error('Failed to fetch category data');
    }

    const categoryList = data.msgArray.reduce((result, current) => {
      const newResult = `${result}${current.ex}_${current.ch}|`;
      return newResult;
    }, '');

    const stockInfoData = await getStockInfo(categoryList);

    // 檢查是否成功取得 stockInfo 數據
    if (!stockInfoData || !stockInfoData.msgArray) {
      throw new Error('Failed to fetch stockInfo data');
    }

    const { msgArray } = useCalculateValue(stockInfoData.msgArray);
    marketTableData.value = msgArray;

    const indices = [0, 56, 92];
    const [priceIndex, rewardIndex, newIndex] = indices.map((start, idx, array) =>
      idx === array.length - 1
        ? marketTableData.value.slice(start)
        : marketTableData.value.slice(start, array[idx + 1])
    );
    priceIndexData.value = priceIndex;
    rewardIndexData.value = rewardIndex;
    indexData.value = newIndex;

    const statisDetail = await getStatis('tse');
    const { dealList } = useDealStaticData(statisDetail.detail, 'tse');
    dealStatisData.value = dealList.value;
    const { entrustList } = useEntrustStatisData(statisDetail.detail, 'tse');
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
    <p class="fs-2 text-black fw-medium">{{ t('MARKET_TSE_INDEX_TITLE') }}</p>
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

    <!-- type( 上市:tse / 指數:index )-->
    <market-table
      id="price"
      :table-data="priceIndexData"
      type="tse"
      :title="t('PRICE_INDEX')"
      :current-time="currentTime"
    ></market-table>
    <!-- 上市/報酬指數 -->
    <market-table
      id="reward"
      :table-data="rewardIndexData"
      type="tse"
      :title="t('MARKET_SUMMARY_BONDS3')"
      :current-time="currentTime"
    ></market-table>
    <!-- 跨市場指數 -->
    <market-table
      :table-data="indexData"
      type="cross"
      :title="t('MARKET_SUMMARY_BONDS2')"
      :table-show-current-time="tableShowCurrentTime"
    ></market-table>
    <!-- 統計資訊 -->
    <statistics-table
      id="statis"
      :table-data="dealStatisData"
      :title="t('MARKET_SUMMARY_TRADES')"
      type="tse"
    ></statistics-table>
    <!-- 統計資訊 -->
    <statistics-table
      :table-data="entrustStatisData"
      :title="t('MARKET_SUMMARY_ORDERS')"
      type="tse"
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
