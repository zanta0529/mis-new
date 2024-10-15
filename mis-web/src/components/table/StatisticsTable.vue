<script setup>
const { t } = useI18n();
const props = defineProps({
  tableData: {
    type: Array,
    default: () => [],
  },
  title: {
    type: String,
    default: '',
  },
  type: {
    type: String,
    default: 'tse',
  },
});
const currentDateTime = getTime(13, 33, 0);
</script>
<template>
  <div class="container bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="row pt-2 pb-3 px-13">
      <div class="col-6 fs-7 fw-medium">{{ t('MARKET_SUMMARY_STATISTICS') }}</div>
      <div class="col-6 d-flex justify-content-end">
        {{ title }} {{ t('CURRENT_TIME') }}: {{ currentDateTime }}
      </div>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col"></div>
      <div class="col">{{ t('MARKET_SUMMARY_OVERALL_MARKET') }}</div>
      <div
        v-if="type === 'tse'"
        class="col"
      >
        {{ t('MARKET_SUMMARY_SECURITIES_TIDX') }}
      </div>
      <div
        v-if="type === 'otc'"
        class="col"
      >
        {{ t('MARKET_SUMMARY_SECURITIES_OIDX') }}
      </div>

      <div
        v-if="type !== 'otc'"
        class="col"
      >
        {{ t('MARKET_SUMMARY_SECURITIES_TIB') }}
      </div>
      <div class="col">{{ t('MARKET_SUMMARY_FUNDS') }}</div>
      <div class="col">{{ t('MARKET_SUMMARY_CALL_WARRANES') }}</div>
      <div class="col">{{ t('MARKET_SUMMARY_PUT_WARRANTS') }}</div>
    </div>
    <div
      v-for="item in tableData"
      :key="item.n"
      class="row py-7 px-13 border-top justify-content-between"
    >
      <div class="col">{{ t(item.n) }}</div>
      <div class="col">{{ isNaN(Number(item.tz)) ? '-' : Number(item.tz).toLocaleString() }}</div>
      <div class="col">{{ isNaN(Number(item.fz)) ? '-' : Number(item.fz).toLocaleString() }}</div>
      <div
        v-if="type !== 'otc'"
        class="col"
      >
        {{ isNaN(Number(item.nz)) ? '-' : Number(item.nz).toLocaleString() }}
      </div>
      <div class="col">{{ isNaN(Number(item.sz)) ? '-' : Number(item.sz).toLocaleString() }}</div>
      <div class="col">{{ isNaN(Number(item.cz)) ? '-' : Number(item.cz).toLocaleString() }}</div>
      <div class="col">{{ isNaN(Number(item.bz)) ? '-' : Number(item.bz).toLocaleString() }}</div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
