export function useDealStaticData(obj) {
  const dealList = ref([]);

  dealList.value.push(
    // tz:整體市場 fz:上市股票 nz:上市創新版股票 sz:基金 cz:認購權證	bz:認售權證
    {
      n: 'MARKET_SUMMARY_TRADE_VALUE',
      tz: obj.tz,
      fz: obj.fz,
      nz: obj.nz,
      sz: obj.sz,
      cz: obj.cz,
      bz: obj.bz,
    },
    {
      n: 'MARKET_SUMMARY_TRADE_VOLUME',
      tz: obj.tv,
      fz: obj.fv,
      nz: obj.nv,
      sz: obj.sv,
      cz: obj.cv,
      bz: obj.bv,
    },
    {
      n: 'MARKET_SUMMARY_TRANSACTION',
      tz: obj.tr,
      fz: obj.fr,
      nz: obj.nr,
      sz: obj.sr,
      cz: obj.cr,
      bz: obj.br,
    }
  );

  return {
    dealList,
  };
}
