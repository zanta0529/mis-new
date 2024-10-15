<script setup></script>
<template>
  <div class="manual-table bg-white border px-13 py-4 text-black my-3 fs-14">
    <ol>
      <slot></slot>
    </ol>
  </div>
</template>

<style lang="scss" scoped>
.manual-table {
  border-radius: 16px;
}
ol {
  list-style: none;
  counter-reset: my-counter;
  letter-spacing: 1.23px;
  padding-left: 0;
  margin-bottom: 0;
}
</style>
