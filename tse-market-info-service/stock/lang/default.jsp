<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%
String WEB_TITLE="臺灣證券交易所-基本市況報導網站";
String WEB_DESCRIPTION="臺灣股票市場之大盤走勢、五秒行情、現貨類股行情揭示";

//MENU
String MENU_HOME="首頁";

String MENU_MARKET_SUMMARY="大盤資訊";
String MENU_MARKET_TWSE="集中市場";
String MENU_MARKET_TIP="臺灣指數公司<br />指數";
String MENU_MARKET_GTSM="上櫃市場";
String MENU_MARKET_EMERGING="興櫃市場";
String MENU_MARKET_FUTURE="期貨市場";
String MENU_MARKET_CHART="大盤走勢";
String MENU_MARKET_CHART_FUTURE="期貨走勢";
String MENU_CHART="走勢圖";


String MENU_SECTOR_GROUP="現貨類股行情";
String MENU_SECTOR_FUTURE="期貨商品行情";
String MENU_FIVE_BEST="最佳五檔";
String MENU_CATEGORIES="各項專區";
String MENU_BLOCK_TRADE="鉅額揭示";
String MENU_ODD_LOT_TRADE="盤後零股揭示";
String MENU_SBL_INQUIRY="借券查詢";
String MENU_ANNOUNCEMENT="市場公告 (公開資訊觀測站)";
String MENU_SPECIAL_TRADE="其他交易";

String MENU_FUTURE_CAT1="指數類契約報價";
String MENU_FUTURE_CAT2="股票類契約報價";
String MENU_FUTURE_CAT3="商品類契約報價";
String MENU_FUTURE_CAT4="利率類契約報價";
String MENU_FUTURE_CAT5="匯率類契約報價";
String MENU_FUTURE_CAT6="ETF類契約報價";
String MENU_FUTURE_BLOCK="鉅額交易";

//CATEGORIES
String MENU_ETF="ETF行情";
String MENU_ETN="ETN行情";

String MENU_TIB="上市創新板";
//String MENU_PSB="興櫃戰略新板";

String MENU_FOREIGN="海外企業";
String MENU_TDR="存託憑證";
String MENU_CATEGORIES_ETF_TWSE="集中市場ETF";
String MENU_CATEGORIES_ETF_GTSM="上櫃市場ETF";
String MENU_CATEGORIES_ETF_NAV="集中市場ETF<br>單位變動及<br>淨值揭露";
String MENU_CATEGORIES_ETF_NAV_O="櫃買市場ETF<br>單位變動及<br>淨值揭露";

String MENU_CATEGORIES_ETN_TWSE="集中市場ETN";
String MENU_CATEGORIES_ETN_GTSM="上櫃市場ETN";
String MENU_CATEGORIES_ETN_NAV="集中市場ETN<br>單位變動及<br>指標價值揭露";
String MENU_CATEGORIES_ETN_NAV_O="櫃買市場ETN<br>單位變動及<br>指標價值揭露";


String MENU_CATEGORIES_FOREIGN_TWSE="海外企業第一<BR>上市交易";
String MENU_CATEGORIES_FOREIGN_GTSM="海外企業第一<BR>上櫃交易";
String MENU_CATEGORIES_TDR_TWSE="集中市場存託憑證";
String MENU_CATEGORIES_TDR_GTSM="上櫃市場存託憑證";
String MENU_CATEGORIES_WARRANTS="權證行情";
String MENU_CATEGORIES_POSTPONED_SECURITIES="暫緩收盤";
String MENU_CATEGORIES_POSTPONED_OPENING="暫緩開盤";

//BLOCK TRADE
String MENU_BLOCKTRADE_TWSE="集中市場";
String MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_NONPAIRED="單一證券<br />逐筆交易";
String MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_PAIRED="單一證券<br />配對交易";
String MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_NONPAIRED="股票組合<br />逐筆交易";
String MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_PAIRED="股票組合<br />配對交易";
String MENU_BLOCKTRADE_TWSE_PRICE_LIMITS="鉅額交易<br />漲跌停價格";

String MENU_BLOCKTRADE_GTSM="上櫃市場";
String MENU_BLOCKTRADE_GTSM_SINGLE_SECURITY_NONPAIRED="單一證券<br />逐筆交易";
String MENU_BLOCKTRADE_GTSM_SINGLE_SECURITY_PAIRED="單一證券<br />配對交易";
String MENU_BLOCKTRADE_GTSM_BASKET_OF_STOCK_NONPAIRED="股票組合<br />逐筆交易";
String MENU_BLOCKTRADE_GTSM_BASKET_OF_STOCK_PAIRED="股票組合<br />配對交易";
String MENU_BLOCKTRADE_GTSM_PRICE_LIMITS="鉅額交易<br />漲跌停價格";

//SBL INQUIRY
String MENU_SBL_MARKET_QUOTES="借券中心個股<br>借券成交查詢";
String MENU_SBL_REAL_TIME_AVAILABLE="借券賣出可用餘額";
String MENU_SBL_AVAILABLE_VOLUME="證券商或證金公司<BR>可出借證券資訊";


//SIDE VIEW
String SIDEVIEW_GET_STOCK="個股行情查詢";
String SIDEVIEW_SEARCH="查詢";
String SIDEVIEW_SEARCH_ODD="個股查詢";
String SIDEVIEW_INPUTCODE="請輸入股票代號或名稱：";
String SIDEVIEW_TWSE="集中市場大盤";
String SIDEVIEW_GTSM="上櫃股票大盤";
String SIDEVIEW_FUTURES="臺指期";

String SIDEVIEW_LINK="相關網站";
String SIDEVIEW_LINK0="臺灣證券交易所";
String SIDEVIEW_LINK1="公開資訊觀測站";
String SIDEVIEW_LINK2="證券櫃檯買賣中心";
String SIDEVIEW_LINK3="網路資訊商店";
String SIDEVIEW_LINK0_URL="https://www.twse.com.tw/";
String SIDEVIEW_LINK1_URL="https://mops.twse.com.tw/";
String SIDEVIEW_LINK2_URL="https://www.tpex.org.tw/";
String SIDEVIEW_LINK3_URL="https://eshop.twse.com.tw/";

String SIDEVIEW_ANNOUN_TITLE="重要公告提醒";
String SIDEVIEW_ANNOUN_CONTENT="為提供更為豐富、便捷及人性化之即時行情查詢服務，本公司於102年7月1日起推出「新版」基本市況報導網站平台，原「舊版」功能已於103年7月31日下午3時下線，不再提供服務。新版網址：<a href='https://mis.twse.com.tw'>https://mis.twse.com.tw</a>，懇請您繼續支持與愛護。";

String SIDEVIEW_ANNOUN_TITLE1="加強提醒投資人注意上市公司相關資訊";
String SIDEVIEW_ANNOUN_CONTENT1="自九十五年四月十七日起，「ETF 行情」選項取消 ETF 成份股報價，只揭示 ETF 個股資訊。近期發現媒體以廣告形式刊登特定上市公司財務、業務相關資訊，為避免誤導投資人判斷，請投資人多加利用「公開資訊觀測站」，查詢由上市公司所發布之重大訊息，審慎評估謹慎投資。";


//FOOTER
String FOOTER_DISCLAIMER="使用條款";
String FOOTER_PRIVACY="隱私權保護說明";
String FOOTER_NOTE="臺灣證券交易所已盡力為本網站提供正確可靠之資訊及最好之服務，若因任何資料之不正確或疏漏所衍生之損害或損失，本公司不負法律責任。<br />" +
"本網站資料不得作為商業使用，引用時，請註明資料來源，並請確保資料之完整性，不得任意增刪。<br />" +
"<span style='font-size:12px;'><a href='https://www.twse.com.tw/zh/products/vendors' target='_blank'>臺灣證券交易所簽約資訊公司</a>亦提供相關資訊</span><br />" +
"本站最佳瀏覽解析度為1024*768以上，建議使用Google Chrome、Safari、FireFox或Internet Explorer 9.0以上版本(請關閉相容性功能)瀏覽器。";


//MARKET SUMMARY
String MARKET_INDEX="指數";

String MARKET_TSE_INDEX_TITLE="集中市場大盤資訊";
String MARKET_OTC_INDEX_TITLE="上櫃市場大盤資訊";

String MARKET_INDEX_INDICES="價格指數";
String MARKET_INDEX_STATISTICS="統計資訊";

String MARKET_SUMMARY_TRADES="成交統計";
String MARKET_SUMMARY_ORDERS="委託統計";

String MARKET_SUMMARY_OVERALL_MARKET="整體市場";
String MARKET_SUMMARY_SECURITIES_TIDX="上市股票";
String MARKET_SUMMARY_SECURITIES_TIB="上市創新板股票";
String MARKET_SUMMARY_SECURITIES_OIDX="上櫃股票";
//String MARKET_SUMMARY_SECURITIES_PSB="興櫃戰略新板股票";
String MARKET_SUMMARY_FUNDS="基金";
String MARKET_SUMMARY_CALL_WARRANES="認購權證	";
String MARKET_SUMMARY_PUT_WARRANTS="認售權證";

String MARKET_SUMMARY_TRADE_VALUE="成交金額";
String MARKET_SUMMARY_TRADE_VOLUME="成交數量";
String MARKET_SUMMARY_TRANSACTION="成交筆數";

String MARKET_SUMMARY_OVERALL_BID_VOLUME="總委買數量";
String MARKET_SUMMARY_OVERALL_BID_ORDERS="總委買筆數";
String MARKET_SUMMARY_OVERALL_ASK_VOLUME="總委賣數量";
String MARKET_SUMMARY_OVERALL_ASK_ORDERS="總委賣筆數";
String MARKET_SUMMARY_BID_VOLUME_AT_LIMIE_UP="漲停委買數量";
String MARKET_SUMMARY_BID_ORDERS_AT_LIMIE_UP="漲停委買筆數";
String MARKET_SUMMARY_ASK_VOLUME_AT_LIMIE_UP="漲停委賣數量";
String MARKET_SUMMARY_ASK_ORDERS_AT_LIMIE_UP="漲停委賣筆數";
String MARKET_SUMMARY_BID_VOLUME_AT_LIMIE_DOWN="跌停委買數量";
String MARKET_SUMMARY_BID_ORDERS_AT_LIMIE_DOWN="跌停委買筆數";
String MARKET_SUMMARY_ASK_VOLUME_AT_LIMIE_DOWN="跌停委賣數量";
String MARKET_SUMMARY_ASK_ORDERS_AT_LIMIE_DOWN="跌停委賣筆數";
String MARKET_SUMMARY_NOTE_TIDX="<ol><li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                           "<li>除境外指數股票型基金及外國股票第二上市（櫃）外，餘交易單位皆為千股。</li>"+
                           "<li>「基金」為受益憑證、受益證券及 ETF 之合計。</li>"+
                           "<li>「整體市場」為上市股票、上市創新板股票、基金、認購權證、認售權證及其他有價證券之合計。</li>"+
                           "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li></ol>";
String MARKET_SUMMARY_NOTE_OIDX="<ol><li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                           "<li>除境外指數股票型基金及外國股票第二上市（櫃）外，餘交易單位皆為千股。</li>"+
                           "<li>「基金」為受益憑證、受益證券及 ETF 之合計。</li>"+
                           "<li>「整體市場」為上櫃股票、基金、認購權證、認售權證及其他有價證券之合計。</li>"+
                           "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li></ol>";
String MARKET_SUMMARY_BONDS="債券指數";
String MARKET_SUMMARY_BONDS2="[跨市場]指數";
String MARKET_SUMMARY_BONDS3="報酬指數";


//CONTENT
String LATEST_PRICE="成交價";
String PRICE_CHANGE="漲跌價差<BR>(百分比)";
String PRICE_CHANGE_2="漲跌價差(百分比)";
String TRADE_VOLUME="成交量";
String ACC_TRADE_VOLUME="累積<BR>成交量";
String REFERENCE_PRICE="試算參考<BR>成交價";
String REFERENCE_VOLUME="試算參考<BR>成交量";
String REFERENCE_PRICE_ODD="試算參考成交價";
String REFERENCE_VOLUME_ODD="試算參考成交量";
String BID_PRICE="揭示<BR>買價";
String BID_VOLUME="揭示<BR>買量";
String ASK_PRICE="揭示<BR>賣價";
String ASK_VOLUME="揭示<BR>賣量";
String OPEN_PRICE="開盤";
String HIGHEST_PRICE="當日最高";
String LOWEST_PRICE="當日最低";
String PREVIOUS_CLOSE="昨收";
String CURRENT_TIME="撮合時間";
String U_PRICE="漲停價";
String W_PRICE="跌停價";
String UNIT_STR="(元，交易單位)";
String FIBEST_PRICE="價格";
String COMMENT="說明";
String PRINT_STRING="列印";

String FIBEST_BID_PRICE="買進價格";
String FIBEST_BID_VOLUME="買進數量";
String FIBEST_ASK_PRICE="賣出價格";
String FIBEST_ASK_VOLUME="賣出數量";

String LATEST_TRADE_PRICE="最近<BR>成交價";
String PRE_TRADE_VOLUME="當盤<BR>成交量";
String TIME="揭示時間";
String TRADE_TIME="成交時間";
String REFERENCE_TIME="試算時間";

//盤中零股
String QUOTES_NON_PAIRED_TRADE="切換為整股交易行情";
String ODD_TRADE="切換為盤中零股行情";
String QUOTES_QUOTATION="整股交易行情";
String ODD_QUOTATION="盤中零股行情";

//SBL
String SBL_FIXED_RATE_TRANSACTION="定價交易";
String SBL_ACC_TRADE_DAY_VOLUME="當日總成交量";
String SBL_QUEUED_LENDING_VOLUME="尚可出借數量";
String SBL_QUEUED_BORROWING_VOLUME="尚待借券數量";

String SBL_10_RECALL_NOTICE="10 日前通知提前還券";
String SBL_3_RECALL_NOTICE="3 日前通知提前還券";
String SBL_1_RECALL_NOTICE="1 日前通知提前還券";

String SBL_BID_OFFER_TRANSACTION="競價交易";
String SBL_BID_OFFER_TRANSACTION_NOTICE="(10 日前通知提前還券)";
String SBL_BID_OFFER_TRANSACTION_NOTICE_3="(3 日前通知提前還券)";
String SBL_BID_OFFER_TRANSACTION_NOTICE_1="(1 日前通知提前還券)";
String SBL_MATCHED_VOLUME="當筆成交量";
String SBL_MATCHED_RATE="當筆成交費率";
String SBL_FIVE_BEST_BIDS_AND_ASKS="成交價量及最佳五檔價量資訊揭示";

String SBL_BORROWING_RATE="借券費率(%)";
String SBL_BORROWING_VOLUME="借券數量";
String SBL_LENDING_RATE="出借費率(%)";
String SBL_LENDING_VOLUME="出借數量";
String SBL_STOCK="請選擇股票代號：";
String SBL_CUSTOMIZED_TRANSACTION="議借交易";
String SBL_MULTIPLE_STOCK="輸入出借單位代號/名稱或個股代號/名稱，並按[enter]鍵，多筆請以空白鍵隔開";

//SBL
String SBL_STOCK_CODE="股票代碼";
String SBL_REAL_TIME_AVAILABLE_VOL="借券賣出可用餘額(股)";
String SBL_LAST_MODIFY="最後更新時間";
String SBL_LENDING_FROM="出借單位代號/名稱";
String SBL_STOCK_NAME="股票名稱";
String SBL_AVAILABLE_="可出借股數";
String SBL_SBL_RATE="借券參考費率";
String SBL_FEE_RATE="手續費參考費率";
String SBL_COMMENTS="備註欄";
String SBL_INPUTBROKER="出借單位代號或名稱：";
String SBL_INPUTSTOCK="出借個股代號或名稱：";

String SBL_INPUTSTOCK0="出借個股代號：";
String SBL_MULTIPLE_STOCK0="輸入個股代號，並按[enter]鍵，多筆請以空白鍵隔開";

//BLOCK TRADE
String BLOCKTRADE_TWSE_SINGLE_SECURITY_NONPAIRED="集中市場鉅額逐筆交易單一證券行情揭示";
String BLOCKTRADE_TWSE_SINGLE_SECURITY_PAIRED="集中市場鉅額配對交易單一證券行情揭示";
String BLOCKTRADE_TWSE_BASKET_OF_STOCK_NONPAIRED="集中市場鉅額逐筆交易股票組合行情揭示";
String BLOCKTRADE_TWSE_BASKET_OF_STOCK_PAIRED="集中市場鉅額配對交易股票組合行情揭示";
String BLOCKTRADE_TWSE_PRICE_LIMITS="集中市場鉅額交易有價證券漲跌停價格";

String BLOCKTRADE_GTSM_SINGLE_SECURITY_NONPAIRED="上櫃市場鉅額逐筆交易單一證券行情揭示";
String BLOCKTRADE_GTSM_SINGLE_SECURITY_PAIRED="上櫃市場鉅額配對交易單一證券行情揭示";
String BLOCKTRADE_GTSM_BASKET_OF_STOCK_NONPAIRED="上櫃市場鉅額逐筆交易股票組合行情揭示";
String BLOCKTRADE_GTSM_BASKET_OF_STOCK_PAIRED="上櫃市場鉅額配對交易股票組合行情揭示";
String BLOCKTRADE_GTSM_PRICE_LIMITS="上櫃市場鉅額交易有價證券漲跌停價格";

//ODDTRADE
String ODDTRADE_BEST_BID_PRICE="揭示<BR>買價";
String ODDTRADE_BEST_ASK_PRICE="揭示<BR>賣價";
String ODDTRADE_TRADE_PRICE="零股<BR>成交價";
String ODDTRADE_TRADE_VOLUME="零股<BR>成交股數";
String ODDTRADE_TIME="揭示時間<BR>1425~1430 為試算";
String ODDTRADE_SUBTITLE="盤後零股交易個股收盤前五分鐘試算行情揭示";
String ODDTRADE_NOTE="<ol><li>零股交易買賣時間為下午 1:40 至 2:30，並於下午 2:30 以集合競價方式一次撮合成交。</li>"+
					"<li>收盤前 5 分鐘（下午 2:25 至 2:30），約每 30 秒揭露試算之最佳一檔買、賣價格，以供投資人參考。</li>"+
                    "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                    "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                    "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li></ol>";

String ODDTRADE_UNIT="(元，股)";

//ALL ETF NAV
String FOREIGN_CURRENCY="外幣交易";
String NEW_TAIWAN_DOLLAR="新台幣交易";

String ALL_ETF_TITLE="ETF發行單位變動及淨值揭露專區";
String ALL_ETF_TITLE_MK1="集中市場";
String ALL_ETF_TITLE_MK2="櫃買市場";
String ALL_ETF_ID="ETF代號/名稱";
String ALL_ETF_NAME="ETF名稱";
String ALL_ETF_ISSUED="已發行受益權<br />單位數";
String ALL_ETF_DIFF="與前日已發行<br>受益單位差異數";
String ALL_ETF_LAST_PRICE="成交價";
String ALL_ETF_NAV="投信或<br>總代理人<br>預估淨值";
String ALL_ETF_NAV_DIFF="預估<br>折溢價<br>幅度";
String ALL_ETF_LAST_NAV="前一營業日<br>單位淨值";
String ALL_ETF_DATE="資料日期";
String ALL_ETF_TIME="資料時間";
String ALL_ETF_LINK="投信公司<br>網頁連結";

String ALL_ETF_FB="最佳五檔";
String ALL_ETF_URL="查詢淨值";
String ALL_ETF_TYPE1="【國內成分證券ETF】";
String ALL_ETF_TYPE2="【標的指數或商品位於亞洲時區之ETF】";
String ALL_ETF_TYPE3="【標的指數或商品位於歐美時區之ETF】";
String ALL_ETF_TYPE4="【全球時區ETF】";

String ALL_ETF_MEMO0="(註)";
String ALL_ETF_MEMO1="(註1)";
String ALL_ETF_MEMO2="(註2)";
String ALL_ETF_MEMO3="(註3)";
String ALL_ETF_MEMO4="(註4)";

String ALL_ETF_MEMO1_H="註1：";
String ALL_ETF_MEMO2_H="註2：";
String ALL_ETF_MEMO3_H="註3：";
String ALL_ETF_MEMO4_H="註4：";

String ALL_ETF_MEMO1_STR="已發行受益權單位數：已發行受益權單位數係前二營業日確定之已發行之受益權單位數，加計前一日初級市場申購單位減去贖回單位之淨申贖單位而得，惟初級市場申購贖回有複審機制，故非最終確定數據，僅提供投資人參考。";
String ALL_ETF_MEMO2_STR="投信或總代理人預估淨值：<ol>" +
                         "<li>預估淨值係以前一營業日基金庫存投資部位，導入標的之前一營業日收盤價，套用已發行受益權單位數後，據以估算基金預估淨值。" +
                         "<li>標的指數或商品交易地區位於亞洲時區、歐美時區及全球時區者，其交易時間若與臺灣集中市場交易時段未重疊，則無法提供即時預估淨值，故此預估淨值在臺灣集中市場交易時間內，將不會跳動。" +
                         "<li>預估淨值欄位計算方式詳見投信公司網頁。</li></ol>";
String ALL_ETF_MEMO3_STR="預估折溢價幅度：計算公式為【((成交價/預估淨值)-1)×100】%之結果。";
String ALL_ETF_MEMO4_STR="前一營業日單位淨值：標的指數或商品於基金淨值結出前，該欄位將以「未結出」文字呈現。";

String ALL_ETF_NOTE="<ol><li><span style='color: red; font-weight: bold;'>本網頁相關資訊，包含受益憑證單位數、差異數、成交價、預估淨值或前一營業日淨值均由 ETF 發行人或總代理人提供</span>，"+
                    "本公司僅透過本網頁傳遞予投資人參考，而上述預估淨值之相關計算說明，仍應以投信公司網站公告為準，"+
                    "本公司網頁僅提供原則性說明供投資人參考。本公司對數據資料概不負責，對其更新性、"+
                    "準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。</li>"+
                    "<li>ETF 證券代號第六碼為 K、M、S、V、C 者，表示該 ETF 以外幣交易。</li></ol>";

String ALL_ETF_NOTE_O="<ol><li><span style='color: red; font-weight: bold;'>本網頁相關資訊，包含受益憑證單位數、差異數、成交價、預估淨值或前一營業日淨值均由 ETF 發行人或總代理人提供</span>，"+
                      "本公司僅透過本網頁傳遞予投資人參考，而上述預估淨值之相關計算說明，仍應以投信公司網站公告為準，"+
                      "本公司網頁僅提供原則性說明供投資人參考。本公司對數據資料概不負責，對其更新性、"+
                      "準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。</li>"+
                      "<li>ETF 證券代號第六碼為 K、M、S、V、C 者，表示該 ETF 以外幣交易。</li></ol>";


//ALL ETN NAV
String ALL_ETN_TITLE="ETN發行單位變動及指標價值揭露專區";
String ALL_ETN_TITLE_MK1="集中市場";
String ALL_ETN_TITLE_MK2="櫃買市場";
String ALL_ETN_ID="ETN代號/名稱";
String ALL_ETN_NAME="ETN名稱";
String ALL_ETN_ISSUED="已發行單位數";
String ALL_ETN_DIFF="與前日已發行單位<br>差異數";
String ALL_ETN_LAST_PRICE="成交價";
String ALL_ETN_NAV="發行人<br>預估指標價值";
String ALL_ETN_NAV_DIFF="預估<br>折溢價<br>幅度";
String ALL_ETN_LAST_NAV="前一營業日<br>指標價值";
String ALL_ETN_DATE="資料日期";
String ALL_ETN_TIME="資料時間";
String ALL_ETN_LINK="發行公司<br>網頁連結";


String ALL_ETN_FB="最佳五檔";
String ALL_ETN_URL="查詢指標價值";

String ALL_ETN_TYPE1="【國內成分證券ETN】";
String ALL_ETN_TYPE2="【標的指數或商品位於亞洲時區之ETN】";
String ALL_ETN_TYPE3="【標的指數或商品位於歐美時區之ETN】";
String ALL_ETN_TYPE4="【全球時區ETN】";

String ALL_ETN_MEMO1="(註1)";
String ALL_ETN_MEMO2="(註2)";

String ALL_ETN_MEMO1_H="註1：";
String ALL_ETN_MEMO2_H="註2：";

String ALL_ETN_MEMO1_STR="發行人預估指標價值：<ol><li>標的指數或商品交易地區位於亞洲時區、歐美時區及全球時區者，其交易時間若與臺灣集中市場交易時段未重疊，則無法提供即時預估指標價值，故此預估指標價值在臺灣集中市場交易時間內，將不會跳動。</li>" +
                         "<li>預估指標價值計算方式詳見發行人公司網頁。</li></ol>";
String ALL_ETN_MEMO2_STR="預估折溢價幅度之計算公式為【((成交價/預估指標價值)-1)×100】% 之結果。";

String ALL_ETN_NOTE="<span style='color: red; font-weight: bold;'>本網頁相關資訊，包含 ETN 單位數、差異數、成交價、預估指標價值或前一營業日指標價值均由 ETN 發行人提供</span>，本公司僅透過本網頁傳遞予投資人參考，而上述預估指標價值之相關計算說明，仍應以發行公司網站公告為準，本公司網頁僅提供原則性說明供投資人參考。本公司對數據資料概不負責，對其更新性、準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。";
String ALL_ETN_NOTE_O="<span style='color: red; font-weight: bold;'>本網頁相關資訊，包含 ETN 單位數、差異數、成交價、預估指標價值或前一營業日指標價值均由 ETN 發行人提供</span>，本公司僅透過本網頁傳遞予投資人參考，而上述預估指標價值之相關計算說明，仍應以發行公司網站公告為準，本公司網頁僅提供原則性說明供投資人參考。本公司對數據資料概不負責，對其更新性、準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。";


//GROUP
String GROUP_TWSE="上市類股";
String GROUP_GTSM="上櫃類股";
String GROUP_TWSE_SELECT=" = 請選擇上市類股 = ";
String GROUP_GTSM_SELECT=" = 請選擇上櫃類股 = ";
String GROUP_TWSE_WARRANT_SELECT=" = 請選擇上市權證 = ";
String GROUP_GTSM_WARRANT_SELECT=" = 請選擇上櫃權證 = ";
String GROUP_TOTAL_RECORDS="資料筆數:";
String GROUP_ALL_PRE_PAGE="全部顯示";
String GROUP_NO_PRE_PAGE="每頁顯示筆數：";
String GROUP_NOTE="<ol><li>本網站於逐筆交易時段揭示「五秒行情快照」，每五秒揭示最近一次撮合結果。</li>" +
                  "<li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                  "<li>除境外指數股票型基金及外國股票第二上市（櫃）外，餘交易單位皆為千股。</li>"+
                  "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                  "<li>ETF 證券代號第六碼為 K、M、S、V、C 者，表示該 ETF 以外幣交易。</li>"+
                  "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                  "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                  "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li>"+
                  "<li><span style='background-color:#D9FFFF'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為試算資訊。</li></ol>";
String GROUP_NOTE_FOR_POSTPONED="<ol><li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                                "<li>除境外指數股票型基金及外國股票第二上市（櫃）外，餘交易單位皆為千股。</li>"+
                                "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                                "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                                "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                                "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li></ol>";

String GROUP_NOTE_ETF="<ol><li>估計淨值或前一營業日淨值係由 ETF 發行人或總代理人提供，僅供參考。臺灣證券交易所對數據資料之內容概不負責，對其更新性、準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。</li>"+
                      "<li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                      "<li>各 ETF 之交易單位及升降幅度請參考各 <a href='https://www.twse.com.tw/zh/page/ETF/categories.html' target='_blank'><font color='#ff0000'>ETF 商品規格</font></a>。</li>"+
                      "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                      "<li>ETF 證券代號第六碼為 K、M、S、V、C 者，表示該 ETF 以外幣交易。</li>"+
                      "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>  顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                      "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>  顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                      "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>  顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li>"+
                      "<li><span style='background-color:#D9FFFF'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為試算資訊。</li></ol>";

String GROUP_NOTE_OTC_ETF="<ol><li>估計淨值或前一營業日淨值係由 ETF 發行人或總代理人提供，僅供參考。臺灣證券交易所對數據資料之內容概不負責，對其更新性、準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。</li>"+
                          "<li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                          "<li>各 ETF 之交易單位及升降幅度請參考各 <a href='https://www.tpex.org.tw/web/link/etf/etf02.php?l=zh-tw' target='_blank'><font color='#ff0000'>ETF 商品規格</font></a>。</li>"+
                          "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                          "<li>ETF 證券代號第六碼為 K、M、S、V、C 者，表示該 ETF 以外幣交易。</li>"+
                          "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                          "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                          "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li>"+
                          "<li><span style='background-color:#D9FFFF'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為試算資訊。</li></ol>";

String GROUP_NOTE_ETN="<ol><li>估計指標價值或前一營業日盤後公布之指標價值係由 ETN 發行人提供，僅供參考。臺灣證券交易所對數據資料之內容概不負責，對其更新性、準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。</li>"+
                      "<li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                      "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                      "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                      "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                      "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li>"+
                      "<li><span style='background-color:#D9FFFF'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為試算資訊。</li><ol>";

String GROUP_NOTE_OTC_ETN="<ol><li>估計指標價值或前一營業日盤後公布之指標價值係由 ETN 發行人提供，僅供參考。臺灣證券交易所對數據資料之內容概不負責，對其更新性、準確性或完整性亦不發表任何聲明，並明確表示不會就使用或引用資料所產生或因依賴該等資料而引起之任何損失承擔任何責任。</li>"+
                          "<li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                          "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                          "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                          "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                          "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li>"+
                          "<li><span style='background-color:#D9FFFF'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為試算資訊。</li></ol>";

String GROUP_NOTE_FOREIGN="<ol><li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                          "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                          "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                          "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                          "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li>"+
                          "<li><span style='background-color:#D9FFFF'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為試算資訊。</li></ol>";

String GROUP_NOTE_TDR="<ol><li>成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。</li>"+
                      "<li>臺灣存託憑證原股成交價資訊，請連結至 <a href='https://www.twse.com.tw/zh/page/focus/links.html#tdr' target='_blank'><font color='#ff0000'>臺灣存託憑證原股資訊</font></a></li>"+
                      "<li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                      "<li><span style='background-color:#aaa'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫停交易」。</li>"+
                      "<li><span style='background-color:#f93'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「恢復交易」。</li>"+
                      "<li><span style='background-color:#e7c1ff'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為「暫緩開盤或暫緩收盤」。</li>"+
                      "<li><span style='background-color:#D9FFFF'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span> 顏色區塊表示該檔股票目前為試算資訊。</li></ol>";

String GROUP_TIMESTR="撮合時間";
String GROUP_ETF_ESTIMATED="估計淨值<br />(註)";
String GROUP_ETF_0061_ESTIMATED="前一營業日<br />淨值 (註)";

String GROUP_ETN_ESTIMATED="估計指標價值<br />(註)";
String GROUP_ETN_0061_ESTIMATED="前一營業日<br />指標價值 (註)";

String GROUP_TDR_DIALOG_TITLE="存託憑證專區公告事項";
String GROUP_TDR_DIALOG_CONTENT="投資存託憑證請注意與原股掛牌交易價格之差異。<BR><BR>並請注意存託憑證流動性風險，原股發行人之財務業務風險，原股掛牌地之政治、經濟、社會變動、法令遵循等風險。";
String GROUP_WARRANT_TARGET="標的證券";


//FOREIGN

String TWSE_FOREIGN="海外第一上市";
String GTSM_FOREIGN="海外第一上櫃";

//
String TIB="上市創新板";
//String PSB="興櫃戰略新板";

//FIVE BEST BIDS AND ASKS OF UNEXECUTED ORDERS
String FIBEST_TITLE="成交價量及最佳五檔價量資訊揭示";
String FIBEST_NOTE = "<ol><li>開盤（08:30 至 09:00 及暫緩開盤期間）及收盤（13:25 至 13:30 及暫緩收盤期間）期間，揭露試算資訊，供投資人參考。</li>"+
                     "<li>本網站於逐筆交易時段揭示「五秒行情快照」，每五秒揭示最近一次撮合結果。</li>"+
                     "<li>ETF 證券代號第六碼為 K、M、S、V、C 者，表示該 ETF 以外幣交易。</li>";

String REFERENCE_ODD="盤中零股試算參考成交價量";
//marketchart
String MARKETCHART_TWSE="上市走勢";
String MARKETCHART_GTSM="上櫃走勢";


//INDEX
String INDEX_TWSE_CHART="集中市場大盤走勢圖";
String INDEX_OTC_CHART="上櫃股票大盤走勢圖";
String INDEX_FRMSA_CHART="寶島指數走勢圖";
String INDEX_TAIEX="加權指數";
String INDEX_FRMSAEX="寶島指數";
String INDEX_GTSM="櫃買指數";
String INDEX_FUTURES="臺指期";
String INDEX_IX0126="臺股期貨指數"; // IX0126
String INDEX_HIGH="最高";
String INDEX_LOW="最低";
String INDEX_VALUE="成交金額";
String INDEX_VOLUME="成交數量";
String INDEX_TRANS="筆數";
String INDEX_BID_VOLUME="委買數量";
String INDEX_BID_ORDERS="筆數";
String INDEX_ASK_VOLUME="委賣數量";
String INDEX_ASK_ORDERS="筆數";
String INDEX_UNIT="";

String INDEX_NOTE="成交值、成交量不含零股、鉅額、盤後定價、拍賣及標購。";
String INDEX_NOTE0="(億元、交易單位)";


//document
String CNT_TRADE_NOTE="本網站於整股交易時段揭示「五秒行情快照」，每五秒揭示最近一次撮合結果";
String GUIDE_TITLE="臺灣證券交易所「基本市況報導網站」使用條款";
String GUIDE_TITLE1="一、認知與同意";
String GUIDE_TITLE2="二、與第三人網站的連結";
String GUIDE_TITLE3="三、隱私權政策";
String GUIDE_TITLE4="四、系統中斷或故障";
String GUIDE_TITLE5="五、服務變更及通知";
String GUIDE_TITLE6="六、下載軟體或資料";
String GUIDE_TITLE7="七、資訊或建議";
String GUIDE_TITLE8="八、智慧財產權的保護";
String GUIDE_TITLE9="九、免責聲明";
String GUIDE_TITLE10="十、準據法及管轄";



String GUIDE_CONTENT1="臺灣證券交易所係依據本使用條款提供臺灣證券交易所網站服務（以下簡稱本網站）。當您使用臺灣證券交易所網站時，即表示您已閱讀、瞭解並同意接受本使用條款之所有內容。臺灣證券交易所有權於任何時間修改或變更本使用條款之內容，建議您隨時注意該等修改或變更。您於任何修改或變更後繼續使用臺灣證券交易所網站，視為您已閱讀、瞭解並同意接受該等修改或變更。";
String GUIDE_CONTENT2="本網站提供相關機構與資訊連結之服務，旨在便利您迅速獲得相關投資與研究資訊。您可能會因此連結至其他機構單位經營的網站，其他機構單位經營的網站均由各該機構單位自行負責，不屬臺灣證券交易所控制及負責範圍之內。";
String GUIDE_CONTENT3="關於您的個人資料以及其他特定資料依本網站「隱私權保護說明」受到保護與規範。";
String GUIDE_CONTENT4="本網站有時可能出現中斷或故障等現象，或許將造成您使用上的不便、資料喪失、錯誤或其他損失等情形。您於使用本網站時宜自行採取防護措施。臺灣證券交易所對於您因使用（或無法使用）本網站而造成的損害，不負任何賠償責任。";
String GUIDE_CONTENT5="臺灣證券交易所有權於任何時間點，不經通知隨時修改、暫時或永久停止繼續提供本網站部分或全部之服務。臺灣證券交易所得以包括但不限於：張貼於本網站網頁、依照您帳戶資料發出電子郵件，或其他方式作本網站服務內容變更之通知。您繼續使用本網站即視為您同意本網站所為之任何通知均視為送達。";
String GUIDE_CONTENT6="非依臺灣證券交易所同意之方式或經臺灣證券交易所同意者，禁止透過包括但不限於自動化裝置、指令碼、自動程式、蜘蛛程式、爬蟲程式或擷取程式等方式下載本網站之軟體或資料。臺灣證券交易所對於您使用本網站或經由本網站連結之其他網站而下載的軟體或資料，不負任何擔保責任。您應於下載前自行斟酌與判斷前述軟體或資料之合適性、有效性、正確性、完整性、及是否侵害他人權利，以免遭受損失（例如：造成您電腦系統受損、或儲存資料流失等）；臺灣證券交易所對於該等損失不負任何賠償責任。";
String GUIDE_CONTENT7="臺灣證券交易所對於您使用本網站或經由本網站連結之其他網站而取得之資訊或建議，不擔保其為完全正確無誤。您在做任何相關規劃與決定之前，仍應請教專業人員針對您的情況提意見，以符合您的個別需求。";
String GUIDE_CONTENT8="本網站所使用之軟體或程式、網站上所有內容，包括但不限於著作、圖片、檔案、資訊、資料、網站架構、網站畫面的安排、網頁設計，均由臺灣證券交易所或其他權利人依法擁有其智慧財產權。任何人除非事前取得台灣證券交易所或其他權利人之書面同意，不得逕自使用、修改、重製、公開播送、改作、散布、發行、公開發表、進行還原工程、解編或反向組譯。您如要引用或轉載本網站內容，請以適當方式清楚註明資料來源，並確保資料完整性，不得任意增刪。尊重智慧財產權是您應盡的義務。";
String GUIDE_CONTENT9="本網站旨在為廣大投資人提供正確可靠之資訊及最好之服務，作為投資研究的參考依據，若因任何資料之不正確或疏漏所衍生之損害或損失，臺灣證券交易所將不負法律責任。是否經由本網站使用下載或取得任何資料，應由您自行考量且自負風險，因任何資料之下載而導致您電腦系統之任何損壞或資料流失，您應負完全責任。";
String GUIDE_CONTENT10="本服務條款之解釋，適用中華民國法律。因使用本網站或與本服務條款相關之爭議，以台灣台北地方法院為第一審管轄法院。";

String PRIVACY_TITLE="隱私權保護說明";
String PRIVACY_CONTENT1="以下的隱私權保護政策適用於您在臺灣證券交易所網站活動時，所涉及的個人資料蒐集、運用與保護，但不適用於經由臺灣證券交易所網站連結至之其他網站。當您由臺灣證券交易所連結至其他網站進行活動時，這些網站有其個別的隱私權保護政策，其資料處裡措施不適用臺灣證券交易所隱私權保護說明，本網站不負任何連帶責任。";
String PRIVACY_CONTENT2="本公司網站並不會對於單純的瀏覽及檔案下載之行為，蒐集任何有關個人身份資料，而您瀏覽網站網頁或使用網站上查詢服務功能時，由伺服器會自行產生的相關日誌 (LOG) 紀錄，如上網設備的 IP 位址、連結網站時間、瀏覽器版本、瀏覽或使用的網頁資料等；其目的在於確認您的身分，以便於提供服務、或進行統計分析，進而提昇網站的使用效能，作為日後網站改進的參考。";
String PRIVACY_CONTENT3="本公司所取得關於您的個人資料，除事先經您本人同意或依照相關法律規定，本公司不會將這些個人資料提供給任何第三人，或移作其他目的使用。由於科技發展的迅速，本公司將會視需要修改網站上所提供的隱私權保護說明，以落實並保障您隱私權益。當本公司完成相關規範之修訂時，會儘速將其刊登於臺灣證券交易所網站，以提醒您注意。如有任何意見或建議，歡迎聯絡我們，本公司將竭誠為您服務。";


String CONTACT_US="聯絡我們";

String UPDATE_TIME0="隔";
String UPDATE_TIME1="秒自動更新";

String PRINT_BUTTON="列印";
String BACK_BUTTON="回上頁";

String BTN_ACCEPT="確認";
String MISNOTE_TIMEOUT="網頁停留過久，請重新進入。";
String MISNOTE_TITLE="歡迎使用 「基本市況報導網站」";

%>
