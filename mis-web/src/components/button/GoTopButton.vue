<script setup>
const goTopButton = ref(null);

const goTop = () => {
  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  });
};

const checkScroll = throttle(() => {
  if (window.scrollY > 150) {
    goTopButton.value.style.display = 'block';
  } else {
    goTopButton.value.style.display = 'none';
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
      id="goTopButton"
      ref="goTopButton"
      type="button"
      class="position-fixed"
      @click="goTop"
    >
      <i class="bi bi-arrow-up"></i>
    </button>
  </div>
</template>

<style scoped>
#goTopButton {
  display: none;
  bottom: 40px;
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
