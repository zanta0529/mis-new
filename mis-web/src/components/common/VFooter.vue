<script setup>
const { t } = useI18n();

let noteModal = null;
const isNoteModal = ref(null);
const modalTitle = ref('');
const modalContent = ref('');

const openNoteModal = (type) => {
  if (type === 'disclaimer') {
    modalTitle.value = t('GUIDE_TITLE');
    modalContent.value = t('GUIDE_CONTENT');
  } else {
    modalTitle.value = t('PRIVACY_TITLE');
    modalContent.value = t('PRIVACY_CONTENT');
  }
  noteModal.show();
};

const closeNoteModal = () => {
  noteModal.hide();
};

onMounted(() => {
  noteModal = new Modal(isNoteModal.value.isModal, {
    backdrop: 'static',
  });
});
</script>

<template>
  <div class="bg-light-gray">
    <div class="container-2xl">
      <div class="row justify-content-between align-items-start px-c20">
        <div class="col-4 py-3">
          <p class="lh-lg fw-bold mb-1">{{ t('FOOTER_ABOUT') }}</p>
          <span
            class="lh-lg"
            v-html="t('FOOTER_NOTE')"
          ></span>
        </div>
        <div class="col-3 py-3">
          <p class="lh-lg fw-bold mb-1">{{ t('SIDEVIEW_LINK') }}</p>
          <div class="text-primary">
            <div class="row row-cols-5 lh-lg">
              <a
                class="col-6"
                :href="t('SIDEVIEW_LINK0_URL')"
                target="_blank"
              >
                {{ t('SIDEVIEW_LINK0') }}
              </a>
              <a
                class="col-6"
                href="#"
                @click.prevent="openNoteModal('disclaimer')"
              >
                {{ t('FOOTER_DISCLAIMER') }}
              </a>
              <a
                class="col-6"
                :href="t('SIDEVIEW_LINK1_URL')"
                target="_blank"
              >
                {{ t('SIDEVIEW_LINK1') }}
              </a>
              <a
                class="col-6"
                href="#"
                @click.prevent="openNoteModal('privacy')"
              >
                {{ t('FOOTER_PRIVACY') }}
              </a>
            </div>
            <div class="row row-cols-5 lh-lg">
              <a
                class="col-6"
                :href="t('SIDEVIEW_LINK2_URL')"
                target="_blank"
              >
                {{ t('SIDEVIEW_LINK2') }}
              </a>
              <a
                class="col-6"
                href="https://suggestionbox.twse.com.tw/swsfront35/SWSF/SWSF01014.aspx"
                target="_blank"
              >
                {{ t('CONTACT_US') }}
              </a>
              <a
                class="col-6"
                :href="t('SIDEVIEW_LINK3_URL')"
                target="_blank"
              >
                {{ t('SIDEVIEW_LINK3') }}
              </a>
            </div>
          </div>
        </div>
        <div class="d-flex justify-content-end py-4 border-top">
          <p
            class="lh-lg m-0"
            v-html="t('FOOTER')"
          ></p>
        </div>
      </div>
    </div>
  </div>
  <NoteModal
    ref="isNoteModal"
    :title="modalTitle"
    :content="modalContent"
    @close-modal="closeNoteModal"
  />
</template>

<style lang="scss" scoped></style>
