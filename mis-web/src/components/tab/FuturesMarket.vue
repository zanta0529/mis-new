<script setup>
const { setInfoData, infoData } = useChartInfoData();

const setChartData = (key, val) => {
  setInfoData(key, val);
};
</script>

<template>
  <div class="d-flex justify-content-around">
    <chart-Futures @stock-info-data="setChartData"></chart-Futures>
    <chart-mis-ohlc-IX0126 @stock-info-data="setChartData"></chart-mis-ohlc-IX0126>
  </div>
  <div class="d-flex justify-content-around">
    <twse-chart-info :data="infoData.futures"></twse-chart-info>
    <twse-chart-info :data="infoData.ix0126"></twse-chart-info>
  </div>
</template>

<style lang="scss" scoped></style>
