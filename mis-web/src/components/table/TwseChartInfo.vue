<script setup>
const { t } = useI18n();
const props = defineProps({
  data: {
    require: true,
    type: Object,
    default: () => ({}),
  },
});

const FORMAT_CHART_DATA = computed(() => props.data);
</script>

<template>
  <div
    v-if="FORMAT_CHART_DATA"
    class="w-50 mx-3 my-3 bg-white px-3 py-3 border rounded-3"
  >
    <div class="border-bottom">
      <div class="d-flex justify-content-between fw-bold mt-2">
        <p class="mx-2">{{ t(FORMAT_CHART_DATA.title) }}</p>
        <span
          class="me-2"
          :style="{ color: FORMAT_CHART_DATA.pColor }"
        >
          {{ FORMAT_CHART_DATA.z }} {{ FORMAT_CHART_DATA.zHtmlStr }}
        </span>
      </div>
      <div class="d-flex align-items-center my-2 mx-5 lh-lg justify-content-between">
        <div class="d-flex w-50 me-5 justify-content-between">
          <!-- 最高 -->
          <div class="w-50 text-primary">
            <span>{{ t('INDEX_HIGH') }}</span>
          </div>
          <div>
            <span>
              {{
                isNaN(Number(FORMAT_CHART_DATA.h)) ? '-' : Number(FORMAT_CHART_DATA.h).toFixed(2)
              }}
            </span>
          </div>
        </div>
        <!-- 最低 -->
        <div class="d-flex w-50 justify-content-between">
          <div class="w-50 text-primary">
            <span>{{ t('INDEX_LOW') }}</span>
          </div>
          <div>
            <span>
              {{
                isNaN(Number(FORMAT_CHART_DATA.l)) ? '-' : Number(FORMAT_CHART_DATA.l).toFixed(2)
              }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div
      v-if="
        FORMAT_CHART_DATA.title !== 'SIDEVIEW_FUTURES' && FORMAT_CHART_DATA.title !== 'INDEX_CHART'
      "
      class="d-flex align-items-center py-2 mx-5 lh-lg justify-content-between border-bottom"
    >
      <div class="d-flex w-50 me-5 justify-content-between">
        <div class="w-50 text-primary">
          <!-- 成交金額 -->
          <span style="color: #ea5967">{{ t('INDEX_VALUE') }}</span>
        </div>
        <div>
          <span>
            {{
              isNaN(Number(FORMAT_CHART_DATA.tz)) || FORMAT_CHART_DATA.tz === '0.00'
                ? '-'
                : FORMAT_CHART_DATA.tz.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
            }}
          </span>
        </div>
      </div>
      <div class="d-flex w-50 justify-content-between"></div>
    </div>

    <div
      v-if="FORMAT_CHART_DATA.title !== 'INDEX_CHART'"
      class="d-flex mx-5 border-bottom"
    >
      <div class="d-flex align-items-center w-50 my-2 me-5 lh-lg justify-content-between">
        <div class="w-50 text-primary">
          <!-- 成交數量 -->
          <span style="color: #ea5967">{{ t('INDEX_VOLUME') }}</span>
        </div>
        <div>
          <span>
            {{
              isNaN(Number(FORMAT_CHART_DATA.tv)) ||
              Number(FORMAT_CHART_DATA.tv).toLocaleString() == 0
                ? '-'
                : Number(FORMAT_CHART_DATA.tv).toLocaleString()
            }}
          </span>
        </div>
      </div>
      <template v-if="FORMAT_CHART_DATA.title !== 'SIDEVIEW_FUTURES'">
        <div class="d-flex align-items-center w-50 justify-content-between">
          <div class="w-50 text-primary">
            <!-- 成交數量-筆數 -->
            <span style="color: #ea5967">{{ t('INDEX_TRANS') }}</span>
          </div>
          <div>
            <span>
              {{
                isNaN(Number(FORMAT_CHART_DATA.tr)) ||
                Number(FORMAT_CHART_DATA.tr).toLocaleString() == 0
                  ? '-'
                  : Number(FORMAT_CHART_DATA.tr).toLocaleString()
              }}
            </span>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="d-flex w-50"></div>
      </template>
    </div>

    <div
      v-if="FORMAT_CHART_DATA.title !== 'INDEX_CHART'"
      class="d-flex mx-5 border-bottom"
    >
      <div class="d-flex w-50 align-items-center my-2 me-5 lh-lg justify-content-between">
        <div class="w-50 text-primary">
          <!-- 委買數量-->
          <span style="color: #921038">{{ t('INDEX_BID_VOLUME') }}</span>
        </div>
        <div>
          <span>
            {{
              isNaN(Number(FORMAT_CHART_DATA.t4))
                ? '-'
                : Number(FORMAT_CHART_DATA.t4).toLocaleString()
            }}
          </span>
        </div>
      </div>
      <div class="d-flex align-items-center w-50 justify-content-between">
        <div class="w-50 text-primary">
          <!-- 委買數量-筆數  -->
          <span style="color: #921038">{{ t('INDEX_BID_ORDERS') }}</span>
        </div>
        <div>
          <span>
            {{
              isNaN(Number(FORMAT_CHART_DATA.t2))
                ? '-'
                : Number(FORMAT_CHART_DATA.t2).toLocaleString()
            }}
          </span>
        </div>
      </div>
    </div>

    <div
      v-if="FORMAT_CHART_DATA.title !== 'INDEX_CHART'"
      class="d-flex mx-5 border-bottom"
    >
      <div class="d-flex w-50 align-items-center my-2 me-5 lh-lg justify-content-between">
        <div class="w-50 text-primary">
          <!-- 委賣數量 -->
          <span style="color: #006b89">{{ t('INDEX_ASK_VOLUME') }}</span>
        </div>
        <div>
          <span>
            {{
              isNaN(Number(FORMAT_CHART_DATA.t3))
                ? '-'
                : Number(FORMAT_CHART_DATA.t3).toLocaleString()
            }}
          </span>
        </div>
      </div>
      <div class="d-flex w-50 align-items-center justify-content-between">
        <div class="w-50 text-primary">
          <!-- 委賣數量-筆數 -->
          <span style="color: #006b89">{{ t('INDEX_ASK_ORDERS') }}</span>
        </div>
        <div>
          <span>
            {{
              isNaN(Number(FORMAT_CHART_DATA.t1))
                ? '-'
                : Number(FORMAT_CHART_DATA.t1).toLocaleString()
            }}
          </span>
        </div>
      </div>
    </div>
    <div
      v-if="
        FORMAT_CHART_DATA.title !== 'SIDEVIEW_FUTURES' && FORMAT_CHART_DATA.title !== 'INDEX_CHART'
      "
      class="mx-5 fs-6 my-3 text-secondary"
    >
      <span>{{ t('INDEX_NOTE0') }} {{ t('INDEX_NOTE') }}</span>
    </div>
  </div>
</template>

<style scoped>
.alert-message {
  color: yellow;
  background-color: #ce0000;
  top: 1;
  z-index: 100;
  padding: 10px;
  font-weight: bold;
  font-size: 18px;
}
.chart {
  width: auto;
  height: 375px;
}
</style>
