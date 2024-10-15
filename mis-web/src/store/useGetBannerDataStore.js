export const useGetBannerDataStore = defineStore('getApiData', () => {
  const stockInfo = ref([]);

  const processApiResponse = (apiResponse, name, title) => {
    if (apiResponse.infoArray && apiResponse.infoArray.length > 0) {
      const data = apiResponse.infoArray[0];

      const existingEntryIndex = stockInfo.value.findIndex((entry) => entry.name === name);
      if (existingEntryIndex !== -1) {
        const existingEntry = stockInfo.value[existingEntryIndex];
        Object.assign(existingEntry, { data });
        procIndexPage(existingEntry);
      } else {
        const newEntry = {
          data,
          title,
          name,
          pColor: '',
          diff: '',
          zHtmlStr: '',
        };
        stockInfo.value.push(newEntry);
        procIndexPage(newEntry);
      }
    }
  };

  const procIndexPage = (entry) => {
    const data = entry.data;
    if (typeof data?.z != 'undefined') {
      let diff = (data?.z - data?.y).toFixed(2);
      let diffPre = (Math.round((diff / data?.y) * 10000) / 100).toFixed(2);
      let newColor = '#000000';
      newColor = diff > 0 ? '#e05e5e' : '#2c812d';
      entry.pColor = newColor;
      entry.diff = Math.abs(diff);
      entry.zHtmlStr = `(${diffPre} %)`;
    }
  };

  const getChatApi = async () => {
    try {
      const resTSE = await getMisOhlcTSE();
      processApiResponse(resTSE, '發行量加權股價指數', 'INDEX_TAIEX');

      const resFRMSA = await getMisOhlcFRMSA();
      processApiResponse(resFRMSA, '寶島股價指數', 'INDEX_FRMSAEX');

      const resOTC = await getMisOhlcOTC();
      processApiResponse(resOTC, '櫃買指數', 'INDEX_GTSM');

      const resFutures = await getFuturesChart();
      processApiResponse(resFutures, '臺指期034', 'INDEX_FUTURES_34');
    } catch (error) {
      console.error(error);
    }
  };

  const intervalId = ref(null);
  const updateDataPeriodically = () => {
    intervalId.value = setInterval(async () => {
      await getChatApi();
    }, 5000);
  };
  const stopUpdateDataPeriodically = () => {
    clearInterval(intervalId.value);
  };

  return {
    stockInfo,
    getChatApi,
    getTSEData: () => getMisOhlcTSE(),
    updateDataPeriodically,
    stopUpdateDataPeriodically,
  };
});
