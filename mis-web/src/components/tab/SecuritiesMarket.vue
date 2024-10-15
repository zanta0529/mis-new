<script setup>
const { setInfoData, infoData } = useChartInfoData();

const setChartData = (key, val) => {
  setInfoData(key, val);
};
</script>

<template>
  <div class="d-flex justify-content-around">
    <chart-mis-ohlc-TSE @stock-info-data="setChartData"></chart-mis-ohlc-TSE>
    <chart-mis-ohlc-OTC @stock-info-data="setChartData"></chart-mis-ohlc-OTC>
  </div>
  <div class="d-flex justify-content-around">
    <twse-chart-info :data="infoData.tse"></twse-chart-info>
    <twse-chart-info :data="infoData.otc"></twse-chart-info>
  </div>
</template>

<style lang="scss" scoped></style>
