import { createApp } from 'vue';
import './assets/scss/all.scss';
import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import HighchartsVue from 'highcharts-vue';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
import VueGtag from 'vue-gtag';
import routes from './router';
import i18n from './languages/i18n';
import 'bootstrap';
import 'bootstrap-icons/font/bootstrap-icons.css';

// 局部引入element plus & icons
import App from './App.vue';

// 關掉開發者工具警告log
// const isDevMode = import.meta.env.DEV;
// const customWarnHandler = (msg, vm, trace) => {
//   if (isDevMode) {
//     console.warn(`Custom warning: ${msg} ${trace}`);
//   }
// };

// 引入 gtag
const vueGTagSettings = {
  config: { id: 'G-F4L5BYPQDJ' },
};

const pinia = createPinia();
const app = createApp(App);
// app.config.warnHandler = customWarnHandler;
app
  .use(VueGtag, vueGTagSettings, routes)
  .use(routes)
  .use(pinia.use(piniaPluginPersistedstate))
  .use(HighchartsVue, { tagName: 'charts' })
  .use(i18n)
  .mount('#app');
Object.entries(ElementPlusIconsVue).forEach(([key, component]) => {
  app.component(key, component);
});
