<script setup>
const goSearchButton = ref(null);
const AppBannerRef = inject('AppBannerRef');

const goSearch = () => {
  AppBannerRef.value.BannerBar.scrollIntoView({ behavior: 'smooth', block: 'start' });
  // 確保滾動完成後才執行focus 不然常常會滾過頭
  setTimeout(() => {
    AppBannerRef.value.stockInput.focus();
  }, 500);
};

const checkScroll = throttle(() => {
  if (window.scrollY > 540) {
    goSearchButton.value.style.display = 'block';
  } else {
    goSearchButton.value.style.display = 'none';
  }
}, 200);

onMounted(() => {
  window.addEventListener('scroll', checkScroll, { passive: false });
});

onUnmounted(() => {
  window.removeEventListener('scroll', checkScroll, { passive: false });
});
</script>

<template>
  <div>
    <!-- 其他的組件和元素 -->
    <button
      id="goSearchButton"
      ref="goSearchButton"
      type="button"
      class="position-fixed"
      @click="goSearch"
    >
      <i class="bi bi-search"></i>
    </button>
  </div>
</template>

<style scoped>
#goSearchButton {
  display: none;
  bottom: 104px;
  right: 40px;
  width: 48px;
  height: 48px;
  text-align: center;
  font-size: 20px;
  border-radius: 50%;
  z-index: 1;
  background-color: #0066a0;
  color: #fff;
  transition:
    background-color 0.2s,
    color 0.2s;
  &:hover {
    border: 1px solid #0066a0;
    background-color: #fff;
    color: #0066a0;
  }
}
</style>
