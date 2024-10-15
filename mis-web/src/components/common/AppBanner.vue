<script setup>
const router = useRouter();
const { t, locale } = useI18n();
const detailDataStore = useGetDetailDataStore();
const { setData } = detailDataStore;
const detailData = ref({
  titleType: 'SEARCH_RESULTS',
  describe: 'CNT_TRADE_NOTE',
});

const updateDetailDataInStore = () => {
  setData(detailData.value);
};
const stockInfo = ref([]);
const stockInput = ref(null);
const BannerBar = ref(null);
defineExpose({ stockInput, BannerBar });

const processApiResponse = (res, name, title) => {
  if (res.infoArray && res.infoArray.length > 0) {
    const data = res.infoArray[0];
    const existingEntryIndex = stockInfo.value.findIndex((entry) => entry.name === name);
    if (existingEntryIndex !== -1) {
      const existingEntry = stockInfo.value[existingEntryIndex];
      Object.assign(existingEntry, { data });
      procIndexPage(existingEntry);
    } else {
      const newEntry = {
        data,
        title,
        name,
        pColor: '',
        diff: '',
        zHtmlStr: '',
      };
      stockInfo.value.push(newEntry);
      procIndexPage(newEntry);
    }
  }
};

const procIndexPage = (entry) => {
  const { data } = entry;
  const z = Number.parseFloat(data.z);
  const y = Number.parseFloat(data.y);

  if (!isNaN(z) && !isNaN(y)) {
    const diff = z - y;
    const diffPre = ((diff / y) * 100).toFixed(2);
    const newColor = getColor(diff);
    const status = diff > 0 ? 'upper' : 'lower';
    Object.assign(entry, {
      pColor: newColor,
      diff: Math.abs(diff).toFixed(2),
      zHtmlStr: `(${diffPre} %)`,
      status,
    });
  }
};

const getColor = (diff) => {
  const isUpper = diff > 0;
  const isZhHant = locale.value === 'zhHant';
  if (isUpper) {
    return isZhHant ? '#e05e5e' : '#2c812d';
  }
  return isZhHant ? '#2c812d' : '#e05e5e';
};

const getChatApi = async () => {
  try {
    let nowDate;
    const resTSE = await getMisOhlcTSE();
    const resFRMSA = await getMisOhlcFRMSA();
    const resOTC = await getMisOhlcOTC();
    const resFutures = await getFuturesChart();

    if (resTSE.infoArray[0].d) {
      nowDate = resTSE.infoArray[0].d;
      getNowDate(nowDate);
    } else {
      nowDate = resOTC.infoArray[0].d;
      getNowDate(nowDate);
    }

    const dataIndex = [
      { res: resTSE, name: '發行量加權股價指數', key: 'INDEX_TAIEX' },
      { res: resFRMSA, name: '寶島股價指數', key: 'INDEX_FRMSAEX' },
      { res: resOTC, name: '櫃買指數', key: 'INDEX_GTSM' },
      { res: resFutures, name: '臺指期', key: 'INDEX_FUTURES' },
    ];
    dataIndex.forEach((item) => {
      processApiResponse(item.res, item.name, item.key);
    });
  } catch (error) {
    console.error(error);
  }
};

const bannerStockInfoIntervalID = ref(null);

const bannerStockInfoSetInterval = () => {
  bannerStockInfoIntervalID.value = setInterval(() => {
    getChatApi();
  }, 5000);
};
const stopInterval = () => {
  clearInterval(bannerStockInfoIntervalID.value);
};

const thisYear = ref('');
const thisMonth = ref('');
const today = ref('');

const getNowDate = (date) => {
  thisYear.value = date.substring(0, 4);
  thisMonth.value = date.substring(4, 6);
  today.value = date.substring(6, 8);
};

const selectStockModel = ref(null);
const selectStockOptions = ref([]);
const loading = ref(false);

const remoteSearchMethod = async (query) => {
  if (query) {
    loading.value = true;
    try {
      const res = await getStockNames(query);
      if (res?.rtcode == '0000') {
        selectStockOptions.value = res.datas.map((item) => {
          const key = item.key.replace(/_\d+$/, '');
          return {
            label: `${item.n}[${item.c}]`,
            value: key,
          };
        });
      } else {
        selectStockOptions.value = [];
      }
    } catch (error) {
      console.error(error);
    } finally {
      loading.value = false;
    }
  }
};

const handleSelect = (query) => {
  updateDetailDataInStore();

  if (query) {
    const type = query.split('_')[0];
    selectStockModel.value = null;
    selectStockOptions.value = [];
    router.push(`/detail-item?id=${query}&ex=${type}`);
  }
};

onMounted(async () => {
  await getChatApi();
  bannerStockInfoSetInterval();
  updateDetailDataInStore();
});

onUnmounted(() => {
  stopInterval();
});
</script>
<template>
  <div class="w-100 banner-background d-flex justify-content-center align-items-end">
    <div class="w-100 bg-primary banner-stock-info-container position-relative">
      <div
        class="w-100 d-flex flex-wrap justify-content-center align-items-end banner-stock-info-row gap-2"
      >
        <div class="d-flex w-100 justify-content-center align-items-center gap-2 gap-2xl-4">
          <div
            v-for="item in stockInfo"
            :key="item.title"
            class="bg-white banner-stock-info-item rounded-4"
          >
            <div
              :class="
                locale === 'zhHant'
                  ? 'banner-stock-info-item-title'
                  : 'banner-stock-info-item-title-en'
              "
              class="d-flex justify-content-between align-items-center fw-medium mb-2 lh-1"
            >
              <p
                v-if="item.data.ex === 'taifex'"
                class="mb-0"
              >
                <template v-if="!item.data.z && item.data.z !== 0">
                  <span>{{ t('SIDEVIEW_FUTURES') }}</span>
                </template>
                <template v-else>
                  <span v-if="locale === 'zhHant' && item.data.z">{{ item.data.n }}</span>
                  <span v-else>{{ item.data.c }}</span>
                </template>
              </p>
              <p
                v-else
                class="mb-0"
              >
                {{ $t(item.title) }}
              </p>
              <p
                v-if="item?.data?.z"
                class="mb-0 fw-2xl-semibold"
                :style="{ color: item.pColor }"
              >
                {{ item.data.z }}
              </p>
              <p
                v-else-if="item?.data?.ex !== 'taifex' && item?.data?.z === undefined"
                class="mb-0 fw-2xl-semibold"
                :style="{ color: item.pColor }"
              >
                {{ item.data.y }}
              </p>
              <p
                v-else-if="item?.data?.ex === 'taifex' && item?.data?.z === ''"
                class="mb-0 fw-2xl-semibold"
                :style="{ color: item.pColor }"
              ></p>
              <p
                v-else
                class="mb-0 fw-2xl-semibold"
                :style="{ color: item.pColor }"
              >
                -
              </p>
            </div>
            <div class="d-flex justify-content-between align-items-center fs-16 fw-medium mb-2">
              <p
                v-if="item?.data?.t"
                class="mb-0 text-black"
              >
                {{ item?.data?.t }}
              </p>
              <p
                v-else
                class="mb-0 text-black"
              >
                -
              </p>
              <p
                :style="{ color: item.pColor }"
                class="mb-0"
              >
                <i
                  v-if="item.status === 'upper'"
                  class="bi bi-caret-up-fill me-0"
                ></i>
                <i
                  v-else-if="item.status === 'lower'"
                  class="bi bi-caret-down-fill me-0"
                ></i>
                <i
                  v-else-if="item?.diff !== '' && item?.zHtmlStr !== ''"
                  class="bi bi-caret-down-fill me-0"
                ></i>
                {{ item.diff }} {{ item.zHtmlStr }}
              </p>
            </div>
            <div class="d-flex justify-content-between align-items-center mb-2 fw-medium fs-16">
              <p class="m-0 text-secondary">
                {{ $t('HIGHEST_PRICE') }}
              </p>
              <span :style="{ color: item.pColor }">{{ item.data.h || '-' }}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center mb-0 fw-medium fs-16">
              <p class="m-0 text-secondary">{{ $t('LOWEST_PRICE') }}</p>
              <span :style="{ color: item.pColor }">{{ item.data.l || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
      <div class="w-100 h-100 d-flex justify-content-center align-items-end">
        <div
          id="router-top"
          ref="BannerBar"
          class="d-flex justify-content-between align-items-end text-white tab-search-container"
        >
          <div
            :class="locale === 'zhHant' ? 'mb-c24' : 'mb-c12'"
            class="flex-xl-grow-1 flex-2xl-grow-0"
          >
            <p
              :class="locale === 'zhHant' ? 'fs-32' : 'fs-24'"
              class="mb-1 fw-medium lh-sm"
              style="letter-spacing: 6.67px"
            >
              <span v-if="locale === 'zhHant'">
                {{ thisYear + '年' }}
              </span>
              <span v-else>
                {{ thisYear }}
              </span>
            </p>
            <p
              v-if="locale === 'zhHant'"
              class="fs-20 fw-medium mb-0"
            >
              <span
                class=""
                style="letter-spacing: 10px"
              >
                {{ thisMonth + '月' }}
              </span>
              <span
                class=""
                style="letter-spacing: 10px"
              >
                {{ today + '日' }}
              </span>
            </p>
            <p
              v-else
              class="fs-20 fw-medium mb-0"
            >
              {{ thisMonth + ' / ' + today }}
            </p>
          </div>
          <div
            :class="locale === 'zhHant' ? 'gap-4 mx-c24' : 'gap-2 mx-c16'"
            class="text-white d-flex justify-content-center align-items-center"
          >
            <router-link
              to="/index"
              type="button"
              :class="
                locale === 'zhHant' ? 'pt-c20 pb-c44 px-c16 fs-18' : 'fs-14 pt-c20 pb-c28 px-3'
              "
              class="tab-btn rounded-4 rounded-bottom-0 text-center text-nowrap"
              style="letter-spacing: 1.59px"
            >
              {{ $t('INDEX_TITLE') }}
            </router-link>
            <router-link
              to="/frmsa"
              type="button"
              :class="
                locale === 'zhHant' ? 'pt-c20 pb-c44 px-c16 fs-18' : 'fs-14 pt-c20 pb-c28 px-3'
              "
              class="tab-btn rounded-4 rounded-bottom-0 text-center text-nowrap"
              style="letter-spacing: 1.59px"
            >
              {{ $t('FRMEA_TITLE') }}
            </router-link>
            <router-link
              to="/futures"
              type="button"
              :class="
                locale === 'zhHant' ? 'pt-c20 pb-c44 px-c16 fs-18' : 'fs-14 pt-c20 pb-c28 px-3'
              "
              class="tab-btn rounded-4 rounded-bottom-0 text-center text-nowrap"
              style="letter-spacing: 1.59px"
            >
              {{ $t('FUTURE_TITLE') }}
            </router-link>
          </div>
          <div
            style="width: 460px"
            :class="locale === 'zhHant' ? 'mb-c24' : 'mb-c12'"
          >
            {{ selectStockModel }}
            <el-select
              ref="stockInput"
              v-model="selectStockModel"
              :placeholder="$t('BANNER_SIDEVIEW_INPUTCODE')"
              filterable
              default-first-option
              remote
              :remote-method="remoteSearchMethod"
              :loading="loading"
              reserve-keyword
              clearable
              no-data-text="沒有資料"
              class="banner-select w-100"
              value-on-clear
              @change="handleSelect"
            >
              <el-option
                v-for="item in selectStockOptions"
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
    </div>
  </div>
</template>
<style lang="scss" scoped>
* {
  // outline: 1px solid #000;
}
.banner-background {
  background-image: url('@/assets/images/banner.jpg');
  background-size: cover;
  background-repeat: no-repeat;
  background-position: center;
  height: 549px;
}
.banner-stock-info-container {
  height: 166px;
}
.banner-stock-info-row {
  position: absolute;
  top: -105px;
  left: 0;
  @media (min-width: 1440px) {
    top: -123px;
  }
}
.banner-stock-info-item {
  width: 299px;

  padding: 16px 36px;
  @media (min-width: 1440px) {
    width: 320px;
  }
}
.banner-stock-info-item-title {
  font-size: 20px;
  @media (min-width: 1440px) {
    font-size: 24px;
  }
}
.banner-stock-info-item-title-en {
  font-size: 18px;
  @media (min-width: 1440px) {
    font-size: 20px;
  }
}
.tab-search-container {
  width: 1280px;
  padding: 0px 32px;
  @media (min-width: 1440px) {
    width: 1440px;
    padding: 0px 44px;
  }
  @media (min-width: 1600px) {
    width: 1600px;
    padding: 0px;
  }
}

.tab-btn:hover,
.tab-btn:focus,
.tab-btn.router-link-active {
  background-color: #f7f7f7;
  color: #0066a0;
}

.banner-select :deep(.el-select__wrapper),
.banner-select :deep(.el-input__wrapper) {
  flex-direction: row-reverse !important;
  border-radius: 12px !important;
  padding: 17px 13px 16px 16px !important;
  font-size: 16px !important;
}
</style>
