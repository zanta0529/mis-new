<script setup>
const { locale } = useI18n();
const show = ref(false);
const showAnnouncement = ref(false);
const messageCht = ref('');
const messageEng = ref('');
let lastScrollPosition = 0;

const handleScroll = () => {
  const currentScrollPosition = window.scrollY;
  if (currentScrollPosition > lastScrollPosition) {
    showAnnouncement.value = false;
  } else {
    showAnnouncement.value = true;
  }

  lastScrollPosition = currentScrollPosition;
};

onMounted(() => {
  window.addEventListener('scroll', handleScroll, { passive: false });

  // 加載公告信息
  const misAnnouncement = async () => {
    const data = await getMisAnnouncement();
    if (data.rtcode == '0000') {
      if (
        typeof data.message != 'undefined' &&
        typeof data.startTime != 'undefined' &&
        typeof data.endTime != 'undefined'
      ) {
        const today = new Date();
        const startDate = data.startTime;
        const endDate = data.endTime;

        if (
          Date.parse(today).valueOf() >= Date.parse(startDate).valueOf() &&
          Date.parse(today).valueOf() <= Date.parse(endDate).valueOf()
        ) {
          messageCht.value = data.message.cht;
          messageEng.value = data.message.eng;

          show.value = true;
          showAnnouncement.value = true;
        }
      } else {
        show.value = false;
      }
    }
  };
  misAnnouncement();
});

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll);
});
</script>

<template>
  <transition
    v-if="show"
    name="fade"
  >
    <div
      v-if="showAnnouncement"
      class="container-fluid bg-brand-tertiary"
    >
      <div
        v-if="showAnnouncement"
        id="sysDialog"
        class="row mx-auto my-0 text-white text-center justify-content-center align-items-center py-3"
        title="系統公告"
      >
        <p
          v-if="locale === 'zhHant'"
          class="m-0"
        >
          {{ messageCht }}
        </p>
        <p
          v-if="locale === 'en'"
          class="m-0 responsive-text"
        >
          {{ messageEng }}
        </p>
      </div>
    </div>
  </transition>
</template>

<style lang="scss" scoped>
.fade-enter-active,
.fade-leave-active {
  transition: 0.5s;
  opacity: 1;
}

.fade-enter,
.fade-leave-to {
  transition: 0.5s;
  opacity: 0;
}
@media (max-width: 1500px) {
  .responsive-text {
    white-space: normal; /* 允許文字換行 */
  }
}
</style>
