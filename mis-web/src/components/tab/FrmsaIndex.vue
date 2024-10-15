<script setup>
const { setInfoData, infoData } = useChartInfoData();

const setChartData = (key, val) => {
  setInfoData(key, val);
};
</script>

<template>
  <div class="d-flex justify-content-around">
    <chart-mis-ohlc-FRMSA @stock-info-data="setChartData"></chart-mis-ohlc-FRMSA>
  </div>
  <div class="d-flex justify-content-around">
    <twse-chart-info :data="infoData.frmsa"></twse-chart-info>
  </div>
</template>

<style lang="scss" scoped></style>
