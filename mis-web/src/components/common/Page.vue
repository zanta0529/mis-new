<script setup>
const { t } = useI18n();
const props = defineProps({
  // 當前頁碼
  currentPage: {
    type: Number,
    default: 1,
  },
  // 資料總筆數(totalCount)
  totalCount: {
    type: Number,
  },
  // 每頁顯示筆數(count)
  pageSize: {
    type: Number,
  },
});

const emit = defineEmits(['emitPaginateAndFetch']);
const current = ref(1);
const showPrevMore = ref(false);
const showNextMore = ref(false);
watch(
  () => props.currentPage,
  (val) => {
    current.value = val;
  }
);

// 總頁數
const pageCount = computed(() => {
  return Math.ceil(props.totalCount / props.pageSize);
});
// 頁碼顯示數量
const pageDisplayCount = 10;

// 由父層觸發更新 currentPage為1
defineExpose({
  setCurrentPage(res) {
    current.value = res;
    return current.value;
  },
});

const pagers = computed(() => {
  let showPrevMore = false;
  let showNextMore = false;
  // 中間點
  const halfPageCount = (pageDisplayCount - 1) / 2; // 4.5
  // 大於10頁的話
  if (pageCount.value > pageDisplayCount) {
    // 當前點擊位置>5.5 (當前位置在6(含)以後)
    if (current.value > pageDisplayCount - halfPageCount) {
      showPrevMore = true; // 大於中間頁碼
    }
    // 當前點擊位置<總頁數(16-10=6) (當前位置在6以前)
    if (current.value < pageCount.value - halfPageCount) {
      showNextMore = true; // 小於等於中間頁碼
    }
  }
  const array = [];

  // (尾巴)
  if (showPrevMore && !showNextMore) {
    const startPage = pageCount.value - (pageDisplayCount - 1);
    for (let i = startPage; i <= pageCount.value; i++) {
      array.push(i);
    }
    // 當前頁碼小於6 (前面)
  } else if (!showPrevMore && showNextMore) {
    for (let i = 1; i < pageDisplayCount; i++) {
      array.push(i);
    }
    // 當前頁碼大於等於6 (中間)
  } else if (showPrevMore && showNextMore) {
    // offset: 取當前頁碼的前後數量
    const offset = Math.floor(pageDisplayCount / 2) - 1;
    // 取頭尾頁碼
    for (let i = current.value - offset; i <= current.value + offset; i++) {
      array.push(i);
    }
    // 總頁數不超過頁碼顯示數量
  } else {
    for (let i = 1; i <= pageCount.value; i++) {
      array.push(i);
    }
  }
  return array;
});

// 第一頁
const firstPage = () => {
  if (current.value >= 1) {
    current.value = 1;
    emit('emitPaginateAndFetch', current.value);
  }
};

// 最末頁
const lastPage = () => {
  if (current.value < pageCount.value) {
    current.value = pageCount.value;
    emit('emitPaginateAndFetch', current.value);
  }
};

const switchPage = (value) => {
  if (value > 0) {
    current.value = Math.min(current.value + 1, pageCount.value);
  } else {
    current.value = Math.max(current.value - 1, 1);
  }
  emit('emitPaginateAndFetch', current.value);
};

const jumpPage = (page) => {
  current.value = page;
  emit('emitPaginateAndFetch', current.value);
};

watchEffect(() => {
  const halfPageCount = (pageDisplayCount - 1) / 2;
  showPrevMore.value = false;
  showNextMore.value = false;
  if (pageCount.value > pageDisplayCount) {
    // 如果當前頁碼>最大頁碼數 則最大頁碼數的一半向前顯示頁碼
    if (current.value > pageDisplayCount - halfPageCount) {
      showPrevMore.value = true;
    }

    // 如果當前頁碼<總頁碼數 减去 最大页码按钮数的一半则显示向后展开按钮
    if (current.value < pageCount.value - halfPageCount) {
      showNextMore.value = true;
    }
  }
});
</script>

<template>
  <nav aria-label="Page navigation example">
    <ul class="pagination d-flex justify-content-end">
      <!-- 第一頁 -->
      <li
        class="page-item"
        :class="{ disabled: current === 1 }"
        @click.prevent="firstPage()"
      >
        <a
          href=""
          class="page-link me-2 rounded py-1 px-2 border-primary"
        >
          {{ t('FIRST_PAGE') }}
        </a>
      </li>

      <!-- 上一頁 -->
      <li
        class="page-item"
        :class="{ disabled: current === 1 }"
        @click.prevent="switchPage(-1)"
      >
        <a
          href=""
          class="page-link me-2 rounded py-1 px-2 border-primary"
        >
          {{ t('PREV_PAGE') }}
        </a>
      </li>

      <!-- 頁碼 -->
      <li
        v-for="pager in pagers"
        :key="pager"
        class="page-item"
        :class="{ active: current === pager }"
        @click.prevent="jumpPage(pager)"
      >
        <a
          href=""
          class="page-link rounded py-1 border-primary"
          :style="{ 'margin-left': pager !== 1 ? '8px' : '' }"
        >
          {{ pager }}
        </a>
      </li>

      <li
        v-if="showNextMore"
        class="pagination__quicknext more"
      >
        <i class="iconfont icon-24gf-ellipsis"></i>
      </li>

      <!-- 下一頁 -->
      <li
        class="page-item"
        :class="{ disabled: current === pageCount }"
      >
        <a
          href=""
          class="page-link ms-2 rounded py-1 px-2 border-primary"
          @click.prevent="switchPage(1)"
        >
          {{ t('NEXT_PAGE') }}
        </a>
      </li>

      <!-- 最後一頁 -->
      <li
        class="page-item"
        :class="{ disabled: current === pageCount }"
        @click.prevent="lastPage()"
      >
        <a
          class="page-link ms-2 rounded py-1 px-2 border-primary"
          href=""
        >
          {{ t('LAST_PAGE') }}
        </a>
      </li>
    </ul>
  </nav>
</template>

<style lang="scss">
$brand-tertiary: #134372;
$background: #f7f7f7;
$disabled: #e9ecef;
.active > .page-link {
  background-color: $brand-tertiary;
}
.page-link {
  background-color: $background;
}
.disabled > .page-link {
  background-color: $disabled;
}
</style>
