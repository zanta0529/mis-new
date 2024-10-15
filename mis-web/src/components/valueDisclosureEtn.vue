<script setup>
const { t } = useI18n();
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  showGoBack: true,
});

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

const scrollToTable = (id) => {
  const element = document.getElementById(id);

  if (element) {
    element.scrollIntoView({
      block: 'start',
      behavior: 'smooth',
    });
  }
};

const newData = ref({});
const router = useRouter();

let tabType = '1';
const changeTabType = async (value) => {
  tabType = value;
  if (value === '1') {
    router.push(`/various-areas/etn-price/indicator-disclosure-etn`);
    await fetchData();
  } else {
    router.push(`/various-areas/etn-price/value-disclosure-etn`);
  }
};

// 表頭資料
const theadData = computed(() => {
  return [
    { name: t('ALL_ETN_ID'), key: 'n', width: 'auto', theadAlignment: 'start' },
    { name: t('ALL_ETN_ISSUED'), key: 'c', width: '12%', theadAlignment: 'end' },
    { name: t('ALL_ETN_DIFF_N'), key: 'd', width: '10%', theadAlignment: 'end' },
    { name: t('ALL_ETN_LAST_PRICE'), key: 'e', width: '7%', theadAlignment: 'end' },
    { name: t('ALL_ETN_NAV_N'), key: 'f', mark: '1', width: '9%', theadAlignment: 'end' },
    { name: t('ALL_ETF_NAV_DIFF_N'), key: 'g', mark: '2', width: '9%', theadAlignment: 'end' },
    { name: t('ALL_ETN_LAST_NAV_N'), key: 'h', width: '9%', theadAlignment: 'end' },
    { name: t('ALL_ETN_LINK_N'), key: '', width: '7.5%', theadAlignment: 'end' },
    { name: t('ALL_ETN_TIME'), key: '', width: '15%', theadAlignment: 'end' },
  ];
});

// 成交價、發行人預估指標價值、預估折溢價幅度、前一營業日指標價值，回傳的資料不一定是小數點後兩位，有可能超過，所以要做格式化
const formatDetail = (val) => {
  const valueStr = val.toString();
  let formatValue = valueStr;

  if (valueStr.includes('.')) {
    const [integerPart, decimalPart] = valueStr.split('.');
    if (decimalPart.length > 2) {
      formatValue = `${integerPart}.${decimalPart.substring(0, 2)}`;
    }
  }
  return formatValue;
};

const fetchData = async () => {
  try {
    const idArr = [];
    const tableData = [];
    const categoryRes = await getCategory('otc', 'B1');

    if (categoryRes.msgArray) {
      categoryRes.msgArray.forEach((element) => {
        idArr.push({
          id: element.ch.substring(0, element.ch.indexOf('.')),
          name: element.n,
          ex: element.ex,
          ch: element.ch,
        });
      });
    }
    const etfDataRes = await getETNData();
    if (etfDataRes.a1) {
      etfDataRes.a1.forEach((e) => {
        if (e.msgArray) {
          e.msgArray.forEach((originalDetail) => {
            const formattedDetail = {
              ...originalDetail,
              e: formatDetail(originalDetail.e),
              f: formatDetail(originalDetail.f),
              g: formatDetail(originalDetail.g),
              h: formatDetail(originalDetail.h),
            };

            const foundCategory = idArr.find((category) => category.id === formattedDetail.a);

            if (foundCategory) {
              const detail = {
                ...formattedDetail,
                n: foundCategory.name,
                ex: foundCategory.ex,
                ch: foundCategory.ch,
                refURL: e.refURL,
              };
              tableData.push(detail);
            }
          });
        }
      });
    }

    // 整理表格資料
    const tmp = {};
    idArr.forEach((e) => {
      const pushData = tableData.filter((item) => item.a === e.id);
      if (pushData.length > 0) {
        if (!tmp[pushData[0].k]) {
          tmp[pushData[0].k] = [];
        }
        tmp[pushData[0].k].push(pushData[0]);
      }
    });
    newData.value = tmp;
  } catch (error) {
    console.error('Error fetching data:', error.message);
  }
};

const formatDateString = (yyyymmdd) => {
  if (!yyyymmdd) return '';
  const year = yyyymmdd.substring(0, 4);
  const month = yyyymmdd.substring(4, 6);
  const day = yyyymmdd.substring(6, 8);
  return `${year}/${month}/${day}`;
};

useWatchLang(fetchData);

let nIntervId;
const updateDataPeriodically = () => {
  nIntervId = setInterval(() => {
    fetchData();
  }, 15000);
};

onMounted(async () => {
  updateDetailDataInStore();
  await fetchData();
  updateDataPeriodically();
});

onUnmounted(() => {
  clearInterval(nIntervId);
});
</script>
<template>
  <div>
    <div class="d-flex justify-content-between">
      <p class="fs-2 text-black fw-medium">{{ t('ALL_ETN_TITLE') }}</p>
      <ul class="nav nav-pills mb-3 p-0 justify-content-end col-3">
        <li class="nav-item">
          <button
            id="tse-tab"
            class="nav-link nav-left"
            data-bs-toggle="pill"
            data-bs-target="#pills-home"
            type="button"
            role="tab"
            aria-controls="pills-home"
            aria-selected="true"
            @click="changeTabType('1')"
          >
            {{ t('ALL_ETN_TITLE_MK1') }}
          </button>
        </li>
        <li class="nav-item">
          <button
            id="odd-tab"
            class="nav-link nav-right active"
            data-bs-toggle="pill"
            data-bs-target="#pills-profile"
            type="button"
            role="tab"
            aria-controls="pills-profile"
            aria-selected="false"
            @click="changeTabType('2')"
          >
            {{ t('ALL_ETN_TITLE_MK2') }}
          </button>
        </li>
      </ul>
    </div>
    <manual-table>
      <li style="text-align: justify">
        <span class="text-custom-red fw-bold">{{ $t('ALL_ETN_NOTE') }}</span>
        <span>{{ $t('ALL_ETN_NOTE_1') }}</span>
      </li>
    </manual-table>

    <button
      type="button"
      class="btn btn-primary px-4 me-3 mb-3"
      @click="scrollToTable('domestic')"
    >
      {{ t('ALL_ETN_TYPE1') }}
    </button>
    <button
      type="button"
      class="btn btn-primary px-4 me-3 mb-3"
      @click="scrollToTable('asia')"
    >
      {{ t('ALL_ETN_TYPE2') }}
    </button>
    <button
      type="button"
      class="btn btn-primary px-4 me-3 mb-3"
      @click="scrollToTable('E&A')"
    >
      {{ t('ALL_ETN_TYPE3') }}
    </button>
    <button
      type="button"
      class="btn btn-primary px-4 me-3 mb-3"
      @click="scrollToTable('worldwide')"
    >
      {{ t('ALL_ETN_TYPE4') }}
    </button>

    <!-- 國內成分證券ETN -->
    <IndicatorDisclosureTable
      id="domestic"
      :thead-data="theadData"
      :table-data="newData['1']"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :type="t('ALL_ETN_TYPE1')"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETN_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETN_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETN_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF_N')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETN_LINK_N')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETN_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETN_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <!-- 標的指數或商品位於亞洲時區之ETN -->
    <IndicatorDisclosureTable
      id="asia"
      :thead-data="theadData"
      :table-data="newData['2']"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :type="t('ALL_ETN_TYPE2')"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETN_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETN_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETN_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF_N')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETN_LINK_N')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETN_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETN_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <!-- 標的指數或商品位於歐美時區之ETN -->
    <IndicatorDisclosureTable
      id="E&A"
      :thead-data="theadData"
      :table-data="newData['3']"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :type="t('ALL_ETN_TYPE3')"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETN_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETN_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETN_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF_N')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETN_LINK_N')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETN_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETN_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <!-- 全球時區ETN -->
    <IndicatorDisclosureTable
      id="worldwide"
      :thead-data="theadData"
      :table-data="newData['4']"
      :subtitle="t('UNIT_STR')"
      :subtitle-interval-time="t('UPDATE_TIME0') + ' 15 ' + t('UPDATE_TIME1')"
      :type="t('ALL_ETN_TYPE4')"
    >
      <template #cell="{ row, thead }">
        <span
          v-if="thead.name === t('ALL_ETN_ID')"
          class="text-primary"
        >
          <el-tooltip
            :enterable="false"
            effect="customized"
            :content="`${row.a}` + ' / ' + `${row.n}`"
            placement="top-start"
            :hide-after="10"
          >
            <router-link
              :to="{ name: 'DetailItem', query: { id: `${row.ex}_${row.ch}`, ex: `${row.ex}` } }"
            >
              <span
                class="truncate-container"
                style="vertical-align: middle"
              >
                {{ row.a }} / {{ row.n }}
              </span>
            </router-link>
          </el-tooltip>
        </span>
        <span v-if="thead.name === t('ALL_ETN_ISSUED')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETN_DIFF_N')">
          {{
            !isNaN(Number(row[thead.key]))
              ? Number(row[thead.key]).toLocaleString()
              : row[thead.key]
          }}
        </span>
        <span v-if="thead.name === t('ALL_ETF_NAV_DIFF_N')">
          {{ row.g }}
          <span v-if="row.g !== '-' && !isNaN(Number(row.g))">%</span>
        </span>
        <span v-if="thead.name === t('ALL_ETN_LINK_N')">
          <a
            :href="row.refURL"
            target="_blank"
            class="text-primary"
          >
            {{ t('ALL_ETN_LINK_STR') }}
          </a>
        </span>
        <span v-if="thead.name === t('ALL_ETN_TIME')">
          {{ formatDateString(row.i) }}-{{ row.j }}
        </span>
      </template>
    </IndicatorDisclosureTable>

    <!-- 備註 -->
    <manual-table id="mark">
      <manual-table-item mark>
        <div>
          {{ t('ALL_ETN_MEMO1_STR') }}
          <ol>
            <li>
              {{ t('ALL_ETF_MEMO2_STR2') }}
            </li>
            <li>{{ t('ALL_ETN_MEMO1_STR3') }}</li>
          </ol>
        </div>
      </manual-table-item>
      <manual-table-item mark>{{ t('ALL_ETN_MEMO2_STR') }}</manual-table-item>
    </manual-table>
  </div>
</template>

<style lang="scss" scoped>
$white: #ffffff;
$text-tertiary: #c8c8c8;
.nav-link {
  color: $text-tertiary;
  background-color: $white;
}
.truncate-container {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  width: 250px;
  display: inline-block;
  @media (min-width: 1440px) {
    max-width: 285px;
  }
  @media (min-width: 1600px) {
    max-width: 330px;
  }
}
</style>
<style>
.el-popper.is-customized {
  background-color: white;
  border: 1px solid #005688c3;
}

.el-popper.is-customized .el-popper__arrow::before {
  background-color: white;
  right: 0;
  border: 1px solid #005788;
}
</style>
