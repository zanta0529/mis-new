<script setup>
const { t } = useI18n();
const isModal = ref(null);

defineExpose({
  isModal,
});

const props = defineProps({
  title: { type: String, required: true },
  content: { type: String, required: true },
});

const emit = defineEmits(['close-modal']);

const closeModal = () => {
  emit('close-modal');
};

const SANITIZED_CONTENT = computed(() => {
  return props.content;
});
</script>
<template>
  <!-- Button trigger modal -->

  <!-- Modal -->
  <div
    id="exampleModal"
    ref="isModal"
    class="modal fade w-100"
    tabindex="-1"
    aria-labelledby="modalLabel"
    aria-hidden="true"
  >
    <div class="modal-dialog modal-lg modal-dialog-scrollable">
      <div
        class="modal-content pb-3"
        style="font-size: 20px"
      >
        <div class="modal-header">
          <h5
            id="modalLabel"
            class="modal-title"
          >
            {{ props.title }}
          </h5>
          <button
            type="button"
            class="btn-close"
            aria-label="Close"
            @click="closeModal"
          ></button>
        </div>
        <div
          class="modal-body fs-16"
          v-html="SANITIZED_CONTENT"
        ></div>
      </div>
    </div>
  </div>
</template>
<style lang="scss" scoped></style>
