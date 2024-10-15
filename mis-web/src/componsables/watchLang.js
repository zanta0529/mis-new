export const useWatchLang = (fetchData) => {
  watch(
    () => i18n.global.locale.value,
    async (newValue, oldValue) => {
      if (newValue !== oldValue) {
        await fetchData();
      }
    }
  );
};
