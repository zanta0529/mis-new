<script setup>
const props = defineProps({
  color: {
    type: String,
  },
  mark: {
    type: Boolean,
    default: false,
  },
});
</script>
<template>
  <li :class="{ 'list-mark': mark }">
    <div class="d-inline-flex">
      <span
        v-if="color"
        class="color"
        :style="{ backgroundColor: color }"
      ></span>
      <slot></slot>
    </div>
  </li>
</template>

<style lang="scss" scoped>
li {
  display: flex;
  text-indent: 0;
  vertical-align: middle;
  margin-bottom: 16px;
  counter-increment: my-counter;
  &::before {
    content: counter(my-counter) '.';
    flex-shrink: 0;
    margin-right: 4px;
  }
  &:last-child {
    margin-bottom: 0;
  }
  &.list-mark {
    counter-increment: my-counter;
    &::before {
      content: '註' counter(my-counter) '：';
      flex-shrink: 0;
    }
  }
}
.color {
  display: inline-block;
  width: 60px;
  height: 20px;
  border-radius: 4px;
  margin-right: 16px;
}
</style>
