export const useGetStockNamesStore = defineStore('StockNames', {
  state: () => ({
    stockNameList: [],
    loading: false,
    options: [],
  }),
  getters: {
    getStockNameList: (state) => state.stockNameList,
  },
  actions: {
    async doGetStockNames(qryStr) {
      if (qryStr) {
        this.loading = true;
        try {
          const data = await getStockNames(qryStr);
          if (data.rtcode == '0000') {
            this.stockNameList = data.datas;
          } else {
            this.options = [];
          }
        } catch (error) {
          console.error(error);
        }
      }
    },
  },
});
