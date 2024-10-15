<script setup>
const route = useRoute();
const router = useRouter();
const { locale } = useI18n();

const MENU_LIST = computed(() => {
  const getRoutes = router.getRoutes();
  const list = getRoutes.find((e) => e.path === '/');
  return list && list.children
    ? list.children.filter((child) => child.meta && child.meta.title)
    : [];
});

const changeLocale = () => {
  const nowLocale = localStorage.getItem('lang');

  if (nowLocale === 'en') {
    locale.value = 'zhHant';
    localStorage.setItem('lang', 'zhHant');
  } else {
    locale.value = 'en';
    localStorage.setItem('lang', 'en');
  }

  router.push({ path: route.path, query: { ...route.query, lang: locale.value } }).then(() => {
    router.go(0);
  });
};
</script>

<template>
  <div class="position-relative z-2">
    <nav
      class="navbar navbar-expand navbar-light bg-white"
      :class="[locale === 'zhHant' ? 'px-0 px-3xl-4 px-4xl-5 ' : ' px-0']"
    >
      <div class="container-fluid">
        <router-link to="/index">
          <div class="d-flex flex-column text-primary fw-bold">
            <span :class="[locale === 'zhHant' ? 'fs-18 fs-3xl-20 fs-4xl-24' : ' fs-12 fs-3xl-20']">
              基本市況報導網站
            </span>
            <span
              :class="[
                locale === 'zhHant' ? 'fs-12 fs-3xl-14  fs-4xl-20' : ' fs-10 fs-xl-10 fs-3xl-16',
              ]"
            >
              Market Information System
            </span>
          </div>
        </router-link>
        <div
          class="collapse navbar-collapse"
          :class="[
            locale === 'zhHant'
              ? 'justify-content-start ms-1 ms-2xl-3 ms-3xl-2 ms-4xl-5 '
              : ' justify-content-start ',
          ]"
        >
          <ul class="navbar-nav">
            <template
              v-for="level1Menu in MENU_LIST"
              :key="level1Menu.path"
            >
              <!-- 沒有子選單 市場公告 -->
              <li
                v-if="!level1Menu.submenu && level1Menu.beforeEnter"
                class="nav-link p-0"
                :class="[
                  { active: level1Menu.children },
                  locale === 'zhHant' ? 'fs-14 fs-2xl-18 fs-3xl-18 ' : ' fs-10 fs-3xl-20',
                ]"
              >
                <router-link
                  v-if="locale == 'zhHant'"
                  :to="{ name: level1Menu.name }"
                  class="d-block pt-c14 px-c10 pb-c12 fw-bold text-black"
                >
                  <span>
                    {{ $t(level1Menu.meta.title) }}
                  </span>
                  <i class="bi bi-box-arrow-up-right text-primary ms-2"></i>
                </router-link>
              </li>
              <!-- 沒有子選單 現貨類股行情 最佳五檔 -->
              <li
                v-else-if="!level1Menu.submenu"
                class="nav-link p-0 d-flex"
                :class="[locale === 'zhHant' ? 'fs-14 fs-2xl-18 fs-3xl-18 ' : ' fs-12 fs-3xl-14']"
              >
                <router-link
                  class="d-block pt-c14 px-c10 pb-c12 fw-bold text-black d-flex align-items-end"
                  :to="{ name: level1Menu.name }"
                >
                  <span v-html="$t(level1Menu.meta.title)"></span>
                </router-link>
              </li>

              <!-- 有子選單 -->
              <li
                v-else
                class="nav-link dropdown p-0"
              >
                <div class="d-block pt-c14 px-c10 pb-c12 fs-14 fw-bold text-black">
                  <span
                    :class="[
                      locale === 'zhHant' ? 'fs-14 fs-2xl-18 fs-3xl-18 ' : ' fs-12 fs-3xl-14',
                    ]"
                  >
                    {{ $t(level1Menu.meta.title) }}
                  </span>
                  <i class="bi bi-chevron-down ms-c8"></i>
                </div>
                <!-- 第一層 -->
                <ul
                  v-if="level1Menu.children"
                  class="dropdown-menu custom-dropdown-menu-width"
                >
                  <template
                    v-for="level2Menu in level1Menu.children"
                    :key="level2Menu.name"
                  >
                    <!-- 子選單 第一層 內部連結 沒有子選單 -->
                    <li
                      v-if="!level2Menu.submenu && !level2Menu.beforeEnter"
                      class="dropdown-item text-black fs-14 fs-3xl-16"
                    >
                      <router-link
                        class="d-block pt-c14 px-c10 pb-c12"
                        :to="{ name: level2Menu.name }"
                      >
                        <span v-html="$t(level2Menu.meta.title)"></span>
                      </router-link>
                    </li>
                    <!-- 第一層 有子選單 -->
                    <li
                      v-if="level2Menu.children && level2Menu.submenu"
                      class="dropdown-item text-black fs-14 fs-3xl-16 d-flex justify-content-between align-items-center pt-c14 px-c10 pb-c12 text-black"
                    >
                      <span
                        class=""
                        v-html="$t(level2Menu.meta.title)"
                      ></span>
                      <i class="bi bi-chevron-right text-primary ms-2"></i>
                      <ul
                        v-if="level2Menu.children"
                        class="dropdown-menu dropdown-submenu custom-dropdown-menu-width"
                      >
                        <template
                          v-for="level3Menu in level2Menu.children"
                          :key="level3Menu.name"
                        >
                          <!-- 第二層 沒有子選單 -->
                          <li
                            v-if="!level3Menu.submenu"
                            class="dropdown-item text-black fs-14 fs-3xl-16"
                          >
                            <router-link
                              class="d-block pt-c14 px-c10 pb-c12 text-black"
                              :to="{ name: level3Menu.name }"
                            >
                              <span v-html="$t(level3Menu.meta.title)"></span>
                            </router-link>
                          </li>
                          <!-- 第二層有子選單 -->
                          <li
                            v-if="level3Menu.submenu"
                            class="dropdown-item text-black fs-14 fs-3xl-16 d-flex justify-content-between align-items-center pt-c14 px-c10 pb-c12 text-black"
                          >
                            <span v-html="$t(level3Menu.meta.title)"></span>
                            <i class="bi bi-chevron-right text-primary ms-2"></i>

                            <ul
                              v-if="level3Menu.children"
                              class="dropdown-menu dropdown-submenu custom-dropdown-menu-width"
                            >
                              <template
                                v-for="level4Menu in level3Menu.children"
                                :key="level4Menu.name"
                              >
                                <li class="dropdown-item text-black fs-14 fs-3xl-16">
                                  <router-link
                                    class="d-flex justify-content-between align-items-center pt-c14 px-c10 pb-c12 text-black"
                                    :to="{ name: level4Menu.name }"
                                  >
                                    <span v-html="$t(level4Menu.meta.title)"></span>
                                    <i class="bi bi-box-arrow-up-right text-primary ms-2"></i>
                                  </router-link>
                                </li>
                              </template>
                            </ul>
                          </li>
                        </template>
                      </ul>
                    </li>
                    <!-- 第一層  外部連結 -->
                    <li
                      v-if="level2Menu.beforeEnter"
                      class="dropdown-item text-black fs-14 fs-3xl-16"
                    >
                      <router-link
                        :to="{ name: level2Menu.name }"
                        class="d-flex justify-content-between align-items-center pt-c14 px-c10 pb-c12 text-black"
                      >
                        <span v-html="$t(level2Menu.meta.title)"></span>
                        <i class="bi bi-box-arrow-up-right text-primary ms-2"></i>
                      </router-link>
                    </li>
                  </template>
                </ul>
              </li>
            </template>
          </ul>
        </div>
        <button
          type="button"
          class="btn btn-link text-decoration-none btn-link-primary fw-bold fs-14 fs-3xl-18 py-8 px-3 ms-9 ms-xl-0"
          @click="changeLocale()"
        >
          {{ $t('LOCALE_LANGUAGE') }}
        </button>
      </div>
    </nav>
    <div class="w-100 position-absolute top-100 z-1">
      <announcement />
    </div>
  </div>
</template>

<style lang="scss" scoped>
$primary: #0066a0;

.custom-dropdown-menu-width {
  // min-width: auto;
}
.dropdown {
  position: relative;
}
.dropdown:hover > .dropdown-menu {
  display: block;
  position: absolute;
  top: 100%;
  left: 7px;
}
.dropdown-menu li {
  position: relative;
}
.dropdown-menu .dropdown-submenu {
  display: none;
  position: absolute;
  left: calc(100% - 10px);
  top: -7px;
}
.dropdown-menu .dropdown-submenu-left {
  right: 100%;
  left: auto;
}
.dropdown-menu > li:hover > .dropdown-submenu {
  display: block;
}
.dropdown-item {
  // padding: 16px 16px;
  padding: 0;
}
.dropdown-item:hover {
  // color: $primary;
}
.dropdown-item:active {
  background-color: var(--bs-dropdown-link-hover-bg); /* 或使用 'initial' */
  color: initial;
}
</style>
