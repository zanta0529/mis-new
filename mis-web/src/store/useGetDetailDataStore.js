export const useGetDetailDataStore = defineStore('getDetailDataStore', () => {
  const data = ref({
    titleType: '',
    describe: '',
    showTab: true,
    showGoBack: false,
  })
  const getData = computed(() => data.value);
  const setData = (newData) => {
    data.value = newData;
  }
  return {
    data,
    getData,
    setData
  }
}, {
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'detailData',
        storage: localStorage,
      },
    ],
  },
});
