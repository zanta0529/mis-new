import { createI18n } from 'vue-i18n';
import EN from './langs/en.json';
import ZHHANT from './langs/zhHant.json';

const messages = {
  en: { ...EN },
  zhHant: { ...ZHHANT },
};

const i18n = createI18n({
  legacy: false, // 要把 legacy 設為 false，才可以使用 Composition API
  locale: 'zhHant', // 設定預設語系
  fallbackLocale: 'zhHant', // 當找不到對應的語系時，使用的語系
  globalInjection: true,
  messages,
  warnHtmlMessage: false,
});

export default i18n;
