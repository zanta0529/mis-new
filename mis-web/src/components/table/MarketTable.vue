<script setup>
const { t } = useI18n();
const props = defineProps(['tableData', 'type', 'title', 'currentTime', 'tableShowCurrentTime']);
</script>

<template>
  <div class="container bg-white border rounded rounded-3 pt-3 text-black my-3">
    <div class="row pt-2 pb-3 px-13">
      <div class="col-6 fs-7 fw-medium">
        <span v-if="type === 'otc'">[{{ t('FIBEST_OTC') }}]</span>
        <span v-else-if="type === 'tse'">[{{ t('FIBEST_TSE') }}]</span>
        <span v-else></span>
        {{ title }}
      </div>
      <div
        v-if="currentTime"
        class="col-6 d-flex justify-content-end"
      >
        {{ t('CURRENT_TIME') }}: {{ tableData[0]?.t }}
      </div>
    </div>
    <div class="row pb-7 px-13 border-bottom justify-content-between">
      <div class="col-3"></div>
      <div class="col">{{ t('MARKET_INDEX') }}</div>
      <div class="col-2">{{ t('PRICE_CHANGE_2') }}</div>
      <div class="col">{{ t('OPEN_PRICE') }}</div>
      <div class="col">{{ t('HIGHEST_PRICE') }}</div>
      <div class="col">{{ t('LOWEST_PRICE') }}</div>
      <div
        v-if="tableShowCurrentTime"
        class="col-1"
      >
        {{ t('CURRENT_TIME') }}:
      </div>
    </div>
    <div
      v-for="item in tableData"
      :key="item['@']"
      class="row py-7 px-13 border-top justify-content-between"
    >
      <div
        v-if="type === 'cross'"
        class="col-3 text-primary"
      >
        <router-link
          :to="{ name: 'DetailItem', query: { id: `${item.ex}_${item.ch}`, ex: `cross` } }"
        >
          {{ item.n }}
        </router-link>
      </div>
      <div
        v-else
        class="col-3 text-primary"
      >
        <router-link
          :to="{ name: 'DetailItem', query: { id: `${item.ex}_${item.ch}`, ex: `${item.ex}` } }"
        >
          {{ item.n }}
        </router-link>
      </div>
      <div
        class="col"
        :style="{ color: item.pColor }"
      >
        {{ item.z ? item.z : item.y }}
      </div>
      <div
        v-if="item.colorType == 'default'"
        class="col-2"
        :style="{ color: item.pColor }"
      >
        {{ isNaN(item.diff) || item.diff === undefined ? '-' : `${item.diff} ${item.zHtmlStr}` }}
      </div>
      <div
        v-else
        class="col-2"
        :style="{ color: item.pColor }"
      >
        <template
          v-if="item.colorType == 'downColor' && (!isNaN(item.diff) || item.diff !== undefined)"
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
          <i class="bi bi-caret-down-fill">
            {{
              isNaN(item.diff) || item.diff === undefined
                ? '-'
                : `${Math.abs(item.diff).toFixed(2)} ${item.zHtmlStr}`
            }}
          </i>
        </template>
      </div>
      <div class="col">{{ item.o }}</div>
      <div class="col">{{ item.h }}</div>
      <div class="col">{{ item.l }}</div>
      <div
        v-if="tableShowCurrentTime"
        class="col-1"
      >
        {{ item.t }}
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
