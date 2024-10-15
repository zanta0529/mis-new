export function useChartInfoData() {
  const infoData = ref({
    tse: {
      title: 'INDEX_TAIEX',
    },
    otc: {
      title: 'INDEX_GTSM',
    },
    ix0126: {
      title: 'INDEX_CHART',
    },
    frmsa: {
      title: 'INDEX_FRMSAEX',
    },
    futures: {
      title: 'SIDEVIEW_FUTURES',
    },
  });

  const setInfoData = (key, val) => {
    const inputVal = val;
    if (inputVal.tz) {
      const valueStr = (inputVal.tz / 100000000).toString();
      let formatValue = valueStr;
      if (valueStr.includes('.')) {
        const [integerPart, decimalPart] = valueStr.split('.');
        if (decimalPart.length > 2) {
          formatValue = `${integerPart}.${decimalPart.substring(0, 2)}`;
        }
      }
      inputVal.tz = formatValue;
    }
    infoData.value[key] = {
      title: infoData.value[key].title,
      ...val,
    };
  };

  return {
    setInfoData,
    infoData,
  };
}
