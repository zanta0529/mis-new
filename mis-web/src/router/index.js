import { createRouter, createWebHistory } from 'vue-router';

function openExternalLink(link) {
  window.open(link, '_blank');
  window.history.back();
}

const routes = [
  {
    path: '/',
    name: 'MisIndex',
    redirect: '/index',
    component: () => import('../views/IndexPage.vue'),
    meta: {
      name: 'MENU_HOME',
    },
    children: [
      {
        path: 'index',
        name: 'Index',
        component: () => import('../components/tab/SecuritiesMarket.vue'),
        meta: {
          name: 'MENU_HOME',
        },
      },
      {
        path: 'frmsa',
        name: 'Frmsa',
        component: () => import('../components/tab/FrmsaIndex.vue'),
        meta: {
          name: 'INDEX_FRMSA_CHART',
        },
      },
      {
        path: 'futures',
        name: 'Futures',
        component: () => import('../components/tab/FuturesMarket.vue'),
        meta: {
          name: 'MENU_MARKET_CHART_FUTURE',
        },
      },
      {
        path: 'detail-item',
        name: 'DetailItem',
        component: () => import('../components/table/DetailTableItem.vue'),
        meta: {
          name: '詳細頁',
        },
      },
      {
        path: 'detail-sblInquiry',
        name: 'DetailSblInquiry',
        component: () => import('../components/DetailSblInquiry.vue'),
        meta: {
          name: '借券中心個股借券成交查詢詳細頁',
        },
      },
      {
        path: 'market',
        name: 'Market',
        submenu: true,
        redirect: '/market/tse-market',
        meta: {
          title: 'MENU_MARKET_SUMMARY',
        },
        children: [
          {
            path: 'tse-market',
            name: 'TseMarket',
            submenu: false,
            component: () => import('../components/TseMarket.vue'),
            meta: {
              title: 'MENU_MARKET_TWSE',
            },
          },
          {
            path: 'taiwanindex',
            name: 'TaiwanIndex',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink = 'https://taiwanindex.com.tw/indexes/board/tip';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_MARKET_TIP',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'otc-market',
            name: 'OtcMarket',
            submenu: false,
            component: () => import('../components/OtcMarket.vue'),
            meta: {
              title: 'MENU_MARKET_GTSM',
            },
          },
          {
            path: 'tpex',
            name: 'Tpex',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink = 'https://mis.tpex.org.tw/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_MARKET_EMERGING',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'taifex',
            name: 'Taifex',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink = 'https://mis.taifex.com.tw/futures/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_MARKET_FUTURE',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'trend-chart',
            name: 'TrendChart',
            submenu: true,
            redirect: { name: 'Index' },
            component: () => import('../views/IndexPage.vue'),
            meta: {
              title: 'MENU_CHART',
            },
            children: [
              {
                path: 'trend-chart-index',
                name: 'TrendChartIndex',
                submenu: false,
                redirect: { name: 'Index' },
                component: () => import('../components/tab/SecuritiesMarket.vue'),
                meta: {
                  title: 'MENU_MARKET_CHART',
                },
              },
              {
                path: 'trend-chart-frmsa',
                name: 'TrendChartFrmsa',
                submenu: false,
                redirect: { name: 'Frmsa' },
                component: () => import('../components/tab/FrmsaIndex.vue'),
                meta: {
                  title: 'MENU_MARKET_CHART_FORMOSA',
                },
              },
              {
                path: 'trend-chart-futures',
                name: 'TrendChartFutures',
                submenu: false,
                redirect: { name: 'Futures' },
                component: () => import('../components/tab/FuturesMarket.vue'),
                meta: {
                  title: 'MENU_MARKET_CHART_FUTURE',
                },
              },
            ],
          },
        ],
      },
      {
        path: 'spot-stock',
        name: 'SpotStock',
        submenu: false,
        component: () => import('../components/SpotStock.vue'),
        meta: {
          title: 'MENU_SECTOR_GROUP',
        },
      },
      {
        path: 'regular-session',
        name: 'RegularSession',
        redirect: '/regular-session/futures-domestic',
        submenu: true,
        meta: {
          title: 'MENU_SECTOR_FUTURE',
        },
        children: [
          {
            path: 'futures-domestic',
            name: 'FuturesDomestic',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink =
                  'https://mis.taifex.com.tw/futures/RegularSession/EquityIndices/FuturesDomestic/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_FUTURE_CAT1',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'stock-products-futures',
            name: 'StockProductsFutures',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink =
                  'https://mis.taifex.com.tw/futures/RegularSession/StockProducts/Futures/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_FUTURE_CAT2',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'etf-products-futures',
            name: 'EtfProductsFutures',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink =
                  'https://mis.taifex.com.tw/futures/RegularSession/EtfProducts/Futures/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_FUTURE_CAT6',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'forex-products-futures',
            name: 'ForexProductsFutures',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink =
                  'https://mis.taifex.com.tw/futures/RegularSession/ForexProducts/Futures/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_FUTURE_CAT5',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'commodity-products-futures',
            name: 'CommodityProductsFutures',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink =
                  'https://mis.taifex.com.tw/futures/RegularSession/CommodityProducts/Futures/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_FUTURE_CAT3',
              stayOnCurrentPage: true,
            },
          },
          {
            path: 'huge-amount-transactions',
            name: 'HugeAmountTransactions',
            submenu: false,
            beforeEnter(to, from, next) {
              if (to.meta.stayOnCurrentPage) {
                const externalLink =
                  'https://mis.taifex.com.tw/futures/RegularSession/BlockTrade/BlockContSingle/';
                openExternalLink(externalLink);
                next(false);
              }
            },
            meta: {
              title: 'MENU_FUTURE_BLOCK',
              stayOnCurrentPage: true,
            },
          },
        ],
      },
      {
        path: 'fibest',
        name: 'Fibest',
        submenu: false,
        component: () => import('../components/Fibest.vue'),
        meta: {
          title: 'MENU_FIVE_BEST',
        },
      },
      {
        path: 'various-areas',
        name: 'VariousAreas',
        submenu: true,
        redirect: '/various-areas/etf-price',
        meta: {
          title: 'MENU_CATEGORIES',
        },
        children: [
          {
            path: 'etf-price',
            name: 'EtfPrice',
            submenu: true,
            redirect: '/various-areas/etf-price/tse-etf',
            meta: {
              title: 'MENU_ETF',
            },
            children: [
              {
                path: 'tse-etf',
                name: 'TseEtf',
                submenu: false,
                component: () => import('../components/TseEtf.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETF_TWSE',
                },
              },
              {
                path: 'otc-etf',
                name: 'OtcEtf',
                submenu: false,
                component: () => import('../components/OtcEtf.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETF_GTSM',
                },
              },
              {
                path: 'indicator-disclosure-etf',
                name: 'IndicatorDisclosureEtf',
                submenu: false,
                component: () => import('../components/IndicatorDisclosureEtf.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETF_NAV',
                },
              },
              {
                path: 'value-disclosure-etf',
                name: 'valueDisclosureEtf',
                submenu: false,
                component: () => import('../components/valueDisclosureEtf.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETF_NAV_O',
                },
              },
            ],
          },
          {
            path: 'etn-price',
            name: 'EtnPrice',
            submenu: true,
            redirect: '/various-areas/etf-price/tse-etn',
            meta: {
              title: 'MENU_ETN',
            },
            children: [
              {
                path: 'tse-etn',
                name: 'TseEtn',
                submenu: false,
                component: () => import('../components/TseEtn.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETN_TWSE',
                },
              },
              {
                path: 'otc-etn',
                name: 'OtcEtn',
                submenu: false,
                component: () => import('../components/OtcEtn.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETN_GTSM',
                },
              },
              {
                path: 'indicator-disclosure-etn',
                name: 'IndicatorDisclosureEtn',
                submenu: false,
                component: () => import('../components/IndicatorDisclosure.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETN_NAV',
                },
              },
              {
                path: 'value-disclosure-etn',
                name: 'valueDisclosureEtn',
                submenu: false,
                component: () => import('../components/valueDisclosureEtn.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_ETN_NAV_O',
                },
              },
            ],
          },
          {
            path: 'taiwan-innovation-board',
            name: 'TaiwanInnovationBoard',
            submenu: false,
            component: () => import('../components/TaiwanInnovationBoard.vue'),
            meta: {
              title: 'MENU_TIB',
            },
          },
          {
            path: 'overseas-enterprises',
            name: 'OverseasEnterprises',
            submenu: true,
            redirect: '/various-areas/listed-transactions',
            meta: {
              title: 'MENU_FOREIGN',
            },
            children: [
              {
                path: 'listed-transactions',
                name: 'ListedTransactions',
                submenu: false,
                component: () => import('../components/ListedTransactions.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_FOREIGN_TWSE',
                },
              },
              {
                path: 'otc-transactions',
                name: 'OtcTransactions',
                submenu: false,
                component: () => import('../components/OtcTransactions.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_FOREIGN_GTSM',
                },
              },
            ],
          },
          {
            path: 'depository-receipts',
            name: 'DepositoryReceipts',
            submenu: true,
            redirect: '/various-areas/depository-receipts',
            meta: {
              title: 'MENU_TDR',
            },
            children: [
              {
                path: 'tse-depository-receipts',
                name: 'TseDepositoryReceipts',
                submenu: false,
                component: () => import('../components/TseDepositoryReceipts.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_TDR_TWSE',
                },
              },
              {
                path: 'otc-depository-receipts',
                name: 'OtcDepositoryReceipts',
                submenu: false,
                component: () => import('../components/OtcDepositoryReceipts.vue'),
                meta: {
                  title: 'MENU_CATEGORIES_TDR_GTSM',
                },
              },
            ],
          },
          {
            path: 'warrants-quotes',
            name: 'WarrantsQuotes',
            submenu: false,
            component: () => import('../components/WarrantsQuotes.vue'),
            meta: {
              title: 'MENU_CATEGORIES_WARRANTS',
            },
          },
          {
            path: 'respite-open',
            name: 'RespiteOpen',
            submenu: false,
            component: () => import('../components/RespiteOpen.vue'),
            meta: {
              title: 'MENU_CATEGORIES_POSTPONED_OPENING',
            },
          },
          {
            path: 'respite-close',
            name: 'RespiteClose',
            submenu: false,
            component: () => import('../components/RespiteClose.vue'),
            meta: {
              title: 'MENU_CATEGORIES_POSTPONED_SECURITIES',
            },
          },
        ],
      },
      {
        path: 'other-transactions',
        name: 'OtherTransactions',
        submenu: true,
        redirect: '/index',
        meta: {
          title: 'MENU_SPECIAL_TRADE',
        },
        children: [
          {
            path: 'huge-amount-disclosure',
            name: 'HugeAmountDisclosure',
            submenu: true,
            redirect: '/index',
            meta: {
              title: 'MENU_BLOCK_TRADE',
            },
            children: [
              {
                path: 'tse-market-external',
                name: 'TseMarketExternal',
                submenu: true,
                redirect: '/index',
                meta: {
                  title: 'MENU_BLOCKTRADE_TWSE',
                },
                children: [
                  {
                    path: 'tse-securities-transaction-by-transaction',
                    name: 'TseSecuritiesTransactionByTransaction',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/L31.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_NONPAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'tse-securities-pairs-transactions',
                    name: 'TseSecuritiesPairsTransactions',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/L63.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_PAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'tse-stock-pairs-transactions',
                    name: 'TseStockPairsTransactions',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/L33.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_NONPAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'tse-stock-pairs-transactions',
                    name: 'TseStock-PairsTransactions',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/L65.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_PAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'tse-huge-amount-transactions-price-limit',
                    name: 'TseHugeAmountTransactionsPriceLimit',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/L500.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_TWSE_PRICE_LIMITS',
                      stayOnCurrentPage: true,
                    },
                  },
                ],
              },
              {
                path: 'market-external',
                name: 'MarketExternal',
                submenu: true,
                redirect: '/index',
                meta: {
                  title: 'MENU_BLOCKTRADE_GTSM',
                },
                children: [
                  {
                    path: 'osc-securities-transaction-by-transaction',
                    name: 'OscSecuritiesTransactionByTransaction',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/OTCL31.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_GTSM_SINGLE_SECURITY_NONPAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'osc-securities-pairs-transactions',
                    name: 'OscSecuritiesPairsTransactions',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/OTCL63.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_GTSM_SINGLE_SECURITY_PAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'osc-stock-pairs-transactions',
                    name: 'OscStockPairsTransactions',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/OTCL33.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_GTSM_BASKET_OF_STOCK_NONPAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'osc-stock-pairs-transactions',
                    name: 'OscStock-PairsTransactions',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/OTCL65.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_GTSM_BASKET_OF_STOCK_PAIRED',
                      stayOnCurrentPage: true,
                    },
                  },
                  {
                    path: 'osc-huge-amount-transactions-price-limit',
                    name: 'OscHugeAmountTransactionsPriceLimit',
                    submenu: false,
                    beforeEnter(to, from, next) {
                      if (to.meta.stayOnCurrentPage) {
                        const externalLink = 'https://clear.twse.com.tw/OTCL500.html';
                        openExternalLink(externalLink);
                        next(false);
                      }
                    },
                    meta: {
                      title: 'MENU_BLOCKTRADE_GTSM_PRICE_LIMITS',
                      stayOnCurrentPage: true,
                    },
                  },
                ],
              },
            ],
          },
          {
            path: 'odd-lots-disclosure',
            name: 'OddLotsDisclosure',
            submenu: false,
            component: () => import('../components/OddLotsDisclosure.vue'),
            meta: {
              title: 'MENU_ODD_LOT_TRADE',
            },
          },
        ],
      },
      {
        path: 'securities-inquiry',
        name: 'SecuritiesInquiry',
        submenu: true,
        redirect: '/securities-inquiry/sbl-inquiry',
        meta: {
          title: 'MENU_SBL_INQUIRY',
        },
        children: [
          {
            path: 'sbl-inquiry',
            name: 'SblInquiry',
            submenu: false,
            // redirect: '/securities-inquiry/sbl-inquiry?ch=0050',
            component: () => import('../components/SblInquiry.vue'),
            meta: {
              title: 'MENU_SBL_MARKET_QUOTES',
            },
          },
          {
            path: 'sbl-inquiry-cap',
            name: 'SblInquiryCap',
            submenu: false,
            component: () => import('../components/SblInquiryCap.vue'),
            meta: {
              title: 'MENU_SBL_REAL_TIME_AVAILABLE',
            },
          },
          {
            path: 'sbl-inquiry-brk',
            name: 'SblInquiryBrk',
            submenu: false,
            component: () => import('../components/SblInquiryBrk.vue'),
            meta: {
              title: `MENU_SBL_AVAILABLE_VOLUME`,
            },
          },
        ],
      },
      {
        path: 'announcement',
        name: 'Announcement',
        submenu: false,
        beforeEnter(to, from, next) {
          if (to.meta.stayOnCurrentPage) {
            const externalLink = 'https://mops.twse.com.tw/server-java/t39sb01?step=0';
            openExternalLink(externalLink);
            next(false);
          }
        },
        meta: {
          title: 'MENU_ANNOUNCEMENT',
          stayOnCurrentPage: true,
        },
      },
    ],
  },
  {
    path: '/twse_chart.html',
    name: 'WWW',
    component: () => import('../components/independentPage/TwseChart.vue'),
    meta: {
      name: 'WWW',
    },
  },
  {
    path: '/twse_chart_CG100.html',
    name: 'CG100',
    component: () => import('../components/independentPage/TwseChartCG100.vue'),
    meta: {
      name: 'CG100',
    },
  },
  {
    path: '/twse_chart_TW50.html',
    name: 'TW50',
    component: () => import('../components/independentPage/TwseChartTW50.vue'),
    meta: {
      name: 'TW50',
    },
  },
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (to.path === '/index') return null;
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        resolve({ el: '#router-top', top: 10, behavior: 'smooth' });
      }, 100);
    });
  },
});

router.beforeEach((to, from, next) => {
  // 證交所需求有英文版跟中文版兩種網址，所以這邊要判斷query是否有lang這個參數，如果沒有就預設中文版，是否切換語言寫在 IndexPage.vue，這邊只做判斷
  const langQuery = to.query.lang;
  let lang = localStorage.getItem('lang');

  if (!langQuery) {
    if (!lang) {
      lang = 'zhHant';
      localStorage.setItem('lang', lang);
    }
    next({ path: to.path, query: { ...to.query, lang } });
  } else {
    if (langQuery === 'en') {
      localStorage.setItem('lang', 'en');
    } else {
      localStorage.setItem('lang', 'zhHant');
    }
    next();
  }
});
export default router;
