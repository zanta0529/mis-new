<script setup>
/**
 * props 使用此表格元件需傳入以下參數
 *
 * @param  {Object}  theadData - 表頭資料
 * @param  {Object}  tableData - 表格資料
 * @param  {String}  title - 表格標題
 * @param  {String}  subtitle1 - 表格標題右邊小標
 * @param  {String}  subtitle - 表格右上文字
 * @param  {String}  type - 標題中括號內文
 * @param  {String}  note - 灰色括號內文
 * @param  {Boolean}  isShowTime - 是否顯示撮合時間
 * @param  {String}  backGroundColor - 表格顏色
 */
const props = defineProps({
  theadData: {
    type: Object,
    required: true,
  },
  tableData: {
    type: Object,
    default: () => {},
  },
  title: {
    type: String,
    default: '',
  },
  subtitle1: {
    type: String,
    default: '',
  },
  subtitle: {
    type: String,
    default: '',
  },
  type: {
    type: String,
    default: '',
  },
  note: {
    type: String,
    default: '',
  },
  isShowTime: {
    type: Boolean,
    default: false,
  },
  backGroundColor: {
    type: String,
    default: '',
  },
  width: {
    type: String,
    default: '',
  },
  subtitleIntervalTime: {
    type: String,
    default: '',
  },
});

const { t, locale } = useI18n();

const currentDateTime = getTime(13, 33, 0);

const scrollToMarkSection = () => {
  const element = document.getElementById('mark');
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
};
</script>
<template>
  <section class="table-wrap">
    <!-- 表格標題 -->
    <div class="title-wrap">
      <div class="d-flex align-items-center justify-content-between">
        <div class="title">
          <span
            v-if="type"
            class="me-2"
          >
            [{{ type }}]
          </span>
          <template v-if="title">{{ title }}&nbsp;</template>
          <template v-if="subtitle1">
            <span class="text-custom-red">{{ subtitle1 }}</span>
          </template>
        </div>
        <div
          v-if="note"
          class="title-note"
        >
          ({{ note }})
        </div>
      </div>
      <div>
        <div v-if="subtitleIntervalTime">
          {{ subtitleIntervalTime }}
          <!-- 隔x秒自動更新 -->
        </div>
        <div
          v-if="subtitle"
          class="subtitle"
        >
          {{ subtitle }}
        </div>
        <div
          v-if="isShowTime"
          class="current-time"
        >
          {{ t('CURRENT_TIME') }} {{ currentDateTime }}
        </div>
      </div>
    </div>
    <div>
      <!-- 表格 -->
      <table class="table table-hover mt-2 mb-4">
        <!-- 表頭 -->
        <thead>
          <tr>
            <th
              v-for="thead in theadData"
              :key="`key_${thead}.name`"
              style="vertical-align: top"
              :style="{ width: thead.width, textAlign: thead.theadAlignment }"
              class="py-1"
              :class="[locale === 'en' ? 'fs-12' : 'fs-16']"
            >
              {{ thead.name }}
              <span
                v-if="thead.mark"
                class="mark d-block"
                @click="scrollToMarkSection"
              >
                ({{ t('TABLE_MEMO') }}{{ thead.mark }})
              </span>
            </th>
          </tr>
        </thead>

        <!-- 表格內容 -->
        <tbody>
          <template v-if="tableData?.length">
            <tr
              v-for="(row, index) in tableData"
              :key="row.n"
            >
              <td
                v-for="thead in theadData"
                :key="`key_${row.n}_${thead.name}`"
                :style="{
                  backgroundColor: row.background,
                  verticalAlign: 'middle',
                  textAlign: thead.theadAlignment,
                  width: thead.width,
                }"
              >
                <slot
                  name="cell"
                  :row="row"
                  :thead="thead"
                  :index="index"
                >
                  <!-- 預設插槽內容 -->
                  <span>
                    {{ row[thead.key] }}
                  </span>
                </slot>
              </td>
            </tr>
          </template>

          <!-- 空值畫面 -->
          <template v-else>
            <tr>
              <td :colspan="theadData.length">
                <p class="empty-text">{{ t('TABLE_NO_DATA') }}</p>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style lang="scss" scoped>
.table-wrap {
  background-color: #fff;
  border-radius: 16px;
  border: solid 1px #dee2e6;
  overflow: hidden;
  padding-top: 24px;
  margin-top: 16px;
  margin-bottom: 16px;
  color: #4a4a4a;
}
.table {
  // display: block;
  // overflow-x: auto;
  white-space: nowrap;
}
.title-wrap {
  display: flex;
  justify-content: space-between;
  padding-left: 12px;
  padding-right: 12px;
  @media (min-width: 1440px) {
    padding-left: 40px;
    padding-right: 40px;
  }
}
.title-box {
  display: flex;
}
.title {
  margin-right: 24px;
}
.title-note {
  color: #757575;
}

th {
  white-space: pre-line;
  padding: 12px;
  // min-width: 100px;
  &:first-child {
    @media (min-width: 1440px) {
      padding-left: 40px;
    }
  }
  &:last-child {
    @media (min-width: 1440px) {
      padding-right: 40px;
    }
  }
  &:not(:first-child) {
    text-align: right;
  }
}
td {
  padding: 8px 12px;
  &:first-child {
    @media (min-width: 1440px) {
      padding-left: 40px;
    }
  }
  &:last-child {
    @media (min-width: 1440px) {
      padding-right: 40px;
    }
  }
  &:not(:first-child) {
    text-align: right;
  }
}
.mark {
  color: #0066a0;
  background-color: initial;
  cursor: pointer;
}
.empty-text {
  font-size: 18px;
  color: #e05e5e;
  text-align: center;
  letter-spacing: 1.59px;
  margin: 28px 0;
}
</style>
