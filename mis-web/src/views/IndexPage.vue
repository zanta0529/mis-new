<script setup>
const { locale } = useI18n();
const AppBannerRef = ref(null);

provide('AppBannerRef', AppBannerRef);

onBeforeMount(() => {
  const nowLocale = localStorage.getItem('lang');
  if (nowLocale) {
    locale.value = nowLocale;
  } else {
    localStorage.setItem('lang', 'zhHant');
  }
});
</script>
<template>
  <div style="min-width: 1280px; background-color: #f7f7f7 !important">
    <GoTopButton />
    <GoSearchButton />
    <VHeader />
    <AppBanner ref="AppBannerRef" />
    <div class="container-2xl py-4 px-auto px-3xl-0">
      <RouterView />
    </div>
    <v-footer />
  </div>
</template>

<style lang="scss" scoped>
.common-layout {
  .el-container {
    .el-header,
    .el-main,
    .el-footer {
      padding-left: 0;
      padding-right: 0;
    }
  }
}
</style>
