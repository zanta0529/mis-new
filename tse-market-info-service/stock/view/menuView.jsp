<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%
String urlstr_1 = "en";
String urlstr_2 = "EN";
String blockQuoteExt = "en.html";

if ( ! lang.equals("en_us")) {
    urlstr_1 = "";
    urlstr_2 = "Future";
    blockQuoteExt = ".html";
}

String cname = null;
String ename = null;
if (com.ecloudlife.util.ConfigData.production != null && com.ecloudlife.util.ConfigData.production.equals("0")) {
    cname = "基本市況報導網站-測試環境";
    ename = "Market Information System-Testing";
} else {
    cname = "基本市況報導網站";
    ename = "Market Information System";
}
%>

<body>

<div id="sysDialog" title="系統公告">
    <ul style='padding: 10px; line-height: 20px;'>
        <li id="announceCHT"></li>
        <li id="announceENG"></li>
    </ul>
</div>

<script type="text/javascript">
$(document).ready(function() {
    $.getJSON("data/mis_announcement.txt", function(data) {
        data = JSON.parse(filterXSS(JSON.stringify(data)));
        if (data.rtcode == "0000") {
            if (typeof(data.message) != "undefined" && typeof(data.startTime) != "undefined" && typeof(data.endTime) != "undefined") {               
                var today = new Date();
                var startDate = data.startTime;
                var endDate   = data.endTime;

                if ((Date.parse(today)).valueOf() >= (Date.parse(startDate)).valueOf() &&
                   (Date.parse(today)).valueOf() <= (Date.parse(endDate)).valueOf()) {
                    $("#sysDialog #announceCHT").text(data.message.cht);
                    $("#sysDialog #announceENG").text(data.message.eng);
                    $("#sysDialog").show();
                }
            } else {
                console.log("No announcemnet data!");
            }
        }
    });
});
</script>

    <div id="container">
        <div id="holder" class="clearfix">
            <table id="logo">
                <tr>
                    <td valign="top">
                        <h1><%=cname %></h1>
                        <h1><%=ename %></h1>
                    </td>
<%--
                    <td align="right" valign="top" width="100%">
                        <a href="https://323go.twse.com.tw/" target="_blank">
                            <img src="banner/20200323.gif" title="逐筆交易擬真活動網頁" width="500px" height="80px" border="0" alt=""/>
                        </a>
--%>
                    </td>
                    <td align="right" valign="top" width="100%">
                        <button id="zh_tw">正體中文</button>
                        <button id="en_us">English</button>
                    </td>
                </tr>
            </table>
            <center>
                <div style="float: center">
                    <ul class="sf-menu">
                        <li><a class="top-a"><b><%=MENU_MARKET_SUMMARY%></b></a>
                            <div class="col2">
                                <ul class="colLeft">
                                    <li><a href="group.jsp?type=fixed&amp;ex=tse&amp;ind=TIDX"><%=MENU_MARKET_TWSE%></a></li>
                                    <li><a href="https://www.taiwanindex.com.tw/indexes/board/tip" target="_blank">
                                        <img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_MARKET_TIP%></a></li>
                                    <li><a href="group.jsp?type=fixed&amp;ex=otc&amp;ind=OIDX"><%=MENU_MARKET_GTSM%></a></li>
                                    <li><a href="https://mis.tpex.org.tw/<%=urlstr_1%>" target="_blank">
                                        <img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_MARKET_EMERGING%></a></li>
                                    <li><a href="https://mis.taifex.com.tw/futures/" target="_blank">
                                        <img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_MARKET_FUTURE%></a></li>
                                    <li><a href="index.jsp"><%=MENU_CHART%></a>
                                        <ul class="colSingle">
                                            <li><a href="frmsa.jsp?ex=otc&amp;ind=B0"><%=INDEX_FRMSAEX%></a></li>
                                            <li><a href="index.jsp?ex=tse&amp;ind=B0"><%=MENU_MARKET_CHART%></a></li>
                                            <li><a href="futures.jsp"><%=MENU_MARKET_CHART_FUTURE%></a></li>
                                        </ul>
                                    </li>
                                </ul>
                            </div>
                        </li>

                        <li><a class="top-a" href="group.jsp?ex=tse&amp;type=all&amp;ind=01"><b><%=MENU_SECTOR_GROUP%></b></a></li>

                        <li><a class="top-a"><b><%=MENU_SECTOR_FUTURE%></b></a>
                            <div class="col2">
                                <ul class="colLeft">
                                    <li><a href="https://mis.taifex.com.tw/futures/RegularSession/EquityIndices/FuturesDomestic/" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_FUTURE_CAT1%></a></li>
                                    <li><a href="https://mis.taifex.com.tw/futures/RegularSession/StockProducts/Futures/" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_FUTURE_CAT2%></a></li>
                                    <li><a href="https://mis.taifex.com.tw/futures/RegularSession/EtfProducts/Futures/" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_FUTURE_CAT6%></a></li>
                                    <li><a href="https://mis.taifex.com.tw/futures/RegularSession/ForexProducts/Futures/" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_FUTURE_CAT5%></a></li>
                                    <li><a href="https://mis.taifex.com.tw/futures/RegularSession/CommodityProducts/Futures/" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_FUTURE_CAT3%></a></li>
                                    <li><a href="https://mis.taifex.com.tw/futures/RegularSession/BlockTrade/BlockContSingle/" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_FUTURE_BLOCK%></a></li>
                                </ul>
                            </div>
                        </li><li><a class="top-a" href="fibest.jsp"><b><%=MENU_FIVE_BEST%></b></a></li>

                        <li><a class="top-a"><b><%=MENU_CATEGORIES%></b></a>
                            <div class="col1">
                                <ul class="colSingle">
                                    <li><a><%=MENU_ETF%></a>
                                        <ul class="colSingle">
                                            <li><a href="group_etf.jsp?ex=tse&amp;ind=B0"><%=MENU_CATEGORIES_ETF_TWSE%></a></li>
                                            <li><a href="group_etf.jsp?ex=otc&amp;ind=B0"><%=MENU_CATEGORIES_ETF_GTSM%></a></li>
                                            <li><a href="etf_nav.jsp?ex=tse"><%=MENU_CATEGORIES_ETF_NAV%></a></li>
                                            <li><a href="etf_nav.jsp?ex=otc"><%=MENU_CATEGORIES_ETF_NAV_O%></a></li>
                                        </ul></li>
                                    <li><a><%=MENU_ETN%></a>
                                        <ul class="colSingle">
                                            <li><a href="group_etf.jsp?ex=tse&amp;ind=B1"><%=MENU_CATEGORIES_ETN_TWSE%></a></li>
                                            <li><a href="group_etf.jsp?ex=otc&amp;ind=B1"><%=MENU_CATEGORIES_ETN_GTSM%></a></li>
                                            <li><a href="etn_nav.jsp?ex=tse"><%=MENU_CATEGORIES_ETN_NAV%></a></li>
                                            <li><a href="etn_nav.jsp?ex=otc"><%=MENU_CATEGORIES_ETN_NAV_O%></a></li>
                                        </ul></li>
                                    <li><a href="group.jsp?type=fixed&amp;ex=tse&amp;bp=03"><%=MENU_TIB%></a></li><!-- @@ -->
                                    <li><a><%=MENU_FOREIGN%></a>
                                        <ul class="colSingle">
                                            <li><a href="group.jsp?type=fixed&amp;ex=tse&amp;ind=Q0"><%=MENU_CATEGORIES_FOREIGN_TWSE%></a></li>
                                            <li><a href="group.jsp?type=fixed&amp;ex=otc&amp;ind=Q0"><%=MENU_CATEGORIES_FOREIGN_GTSM%></a></li>
                                        </ul></li>
                                    <li><a><%=MENU_TDR%></a>
                                        <ul class="colSingle">
                                            <li><a href="group.jsp?type=fixed&amp;ex=tse&amp;ind=E0"><%=MENU_CATEGORIES_TDR_TWSE%></a></li>
                                            <li><a href="group.jsp?type=fixed&amp;ex=otc&amp;ind=E0"><%=MENU_CATEGORIES_TDR_GTSM%></a></li>
                                        </ul></li>
                                    <li><a href="group.jsp?type=warrant&amp;ex=tse&amp;ind=D1"><%=MENU_CATEGORIES_WARRANTS%></a></li>
                                    <li><a href="group.jsp?type=postponedOpening"><%=MENU_CATEGORIES_POSTPONED_OPENING%></a></li>
                                    <li><a href="group.jsp?type=postponed"><%=MENU_CATEGORIES_POSTPONED_SECURITIES%></a></li>
                                </ul>
                            </div></li>

                        <li><a class="top-a"><b><%=MENU_SPECIAL_TRADE%></b></a>
                            <div>
                                <ul>
                                    <li><a><%=MENU_BLOCK_TRADE%></a>
                                        <div>
                                            <ul>
                                                <li><a><%=MENU_BLOCKTRADE_TWSE%></a>
                                                    <ul>
                                                        <li><a href="https://clear.twse.com.tw/L31<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_NONPAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/L63<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_PAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/L33<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_NONPAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/L65<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_PAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/L500<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_PRICE_LIMITS%></a></li>
                                                    </ul></li>
                                                <li><a><%=MENU_BLOCKTRADE_GTSM%></a>
                                                    <ul>
                                                        <li><a href="https://clear.twse.com.tw/OTCL31<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_NONPAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/OTCL63<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_SINGLE_SECURITY_PAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/OTCL33<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_NONPAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/OTCL65<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_BASKET_OF_STOCK_PAIRED%></a></li>
                                                        <li><a href="https://clear.twse.com.tw/OTCL500<%=blockQuoteExt%>" target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<%=MENU_BLOCKTRADE_TWSE_PRICE_LIMITS%></a></li>
                                                    </ul></li>
                                            </ul>
                                        </div></li>
                                    <li><a href="oddTrade.jsp"><%=MENU_ODD_LOT_TRADE%></a></li>
                                </ul>
                            </div>
                        </li>
                        <li><a class="top-a"><b><%=MENU_SBL_INQUIRY%></b></a>
                            <div class="col1 right">
                                <ul class="colSingle">
                                    <li><a href="sblInquiry.jsp"><%=MENU_SBL_MARKET_QUOTES%></a></li>
                                    <li><a href="sblInquiryCap.jsp"><%=MENU_SBL_REAL_TIME_AVAILABLE%></a></li>
                                    <li><a href="sblInquiryBrk.jsp"><%=MENU_SBL_AVAILABLE_VOLUME%></a></li>
                                </ul>
                            </div>
                        </li>
                        <%if(!lang.equals("en_us")){%>
                        <li><a class="top-a" href="https://mops.twse.com.tw/server-java/t39sb01?step=0"
                            target="_blank"><img src="images/newpage.png" style="float: left; border: 0" alt=""/>&nbsp;<b><%=MENU_ANNOUNCEMENT%></b></a></li>
                        <%}%>
                    </ul>
                </div>
            </center>