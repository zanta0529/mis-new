<script setup>
const detailDataStore = useGetDetailDataStore();
const detailData = ref({
  titleType: 'MENU_FIVE_BEST',
  describe: 'CNT_TRADE_NOTE',
  showTab: true,
});

const updateDetailDataInStore = () => {
  detailDataStore.data = detailData.value;
};

const router = useRouter();
onMounted(() => {
  updateDetailDataInStore();
  router.push(`/detail-item?id=tse_1101.tw&ex=tse`);
});
</script>
<template>
  <span></span>
</template>

<style lang="scss" scoped></style>
