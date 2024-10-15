<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="java.util.Calendar" %>
<%@ page import="com.ecloudlife.util.Utility" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ include file="view/headerView.jsp" %>
<%@ include file="view/menuView.jsp" %>

<%
if (request.getMethod().equals("POST")) {
    Thread.currentThread().sleep(1300);
    return;
}

String DATE_FORMAT = "yyyyMMdd";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); 
c1.add(Calendar.DATE, -365);

String techD1   = sdf.format(c1.getTime());
String techD0   = sdf.format(c1.getTime());
String stock    = purifyString(Utility.getHttpParameter(request,"stock"), "1101", 16);
String goback   = purifyString(Utility.getHttpParameter(request,"goback"), "0", 1);


if (stock.indexOf("TXF") >= 0) {
    stock = stock.replace("tse", "taifex");
}
%>
<script type="text/javascript">
    var techD0 = <%=techD0 %>;
    var techD1 = <%=techD1 %>;
</script>



<div id="content">
    <div id="CNT_TRADE_NOTE" style="text-align: center; color: red; font-size: 16px; margin-bottom: 8px"><%=CNT_TRADE_NOTE%></div>
    <div>
        <button id="print" style="float: center;"><%=PRINT_STRING%></button>
        <% if (goback.equals("1")) { %>
            <button id="goback" style="float: center;"><%=BACK_BUTTON%></button>
        <% } %>
    </div>
    <button id='btnChangeToOdd' style='color:red; margin-left: 570px; display: none;'><%=ODD_TRADE %></button>
    <button id='btnChangeToContinues' style='color:red; margin-left: 570px; display: none;'><%=QUOTES_NON_PAIRED_TRADE %></button>
    <div style="float: right" id="userDelayView">  
        <%=UPDATE_TIME0%>
        <label id="userDelay0"></label> 
        <%=UPDATE_TIME1%>
    </div>

    <br />
  
    <label id="<%=stock%>_n" class="title"></label>
    <label id="title_note" class="title"></label>
    <label id='labContinuesQuotation' style='color:red; display: none;'>(<%=QUOTES_QUOTATION %>)</label>
    <label id='labOddQuotation' style='color:red; display: none;'>(<%=ODD_QUOTATION %>)</label>
    <label class="title" style="float: right;"><%=UNIT_STR%></label>

    <div class="ui-widget-content" style="width: 100%; background: #ffffff; align: center">
        <table width="100%" id="hor-minimalist-a" style="border-collapse: collapse;">
            <thead>
                <tr style="vertical-align: top; text-align: center;">
                    <th id='currentTime' style='display: none;'><%=TRADE_TIME%></th>
                    <th id='latestTradePrice' style='display: none;'><%=LATEST_PRICE%></th>
                    <th id='priceChange' style='display: none;'><%=PRICE_CHANGE%></th>
                    <th id='preTradeVolume' style='display: none;'><%=PRE_TRADE_VOLUME%></th>

                    <th><%=ACC_TRADE_VOLUME%></th>
                    <th id='refPrice'><%=REFERENCE_PRICE%></th>
                    <th id='refVolume'><%=REFERENCE_VOLUME%></th>
                    <!-- 
                    <th id='bidPrice' style='display: none;'><%=BID_PRICE%></th>
                    <th id='bidVolume' style='display: none;'><%=BID_VOLUME%></th>
                    <th id='askPrice' style='display: none;'><%=ASK_PRICE%></th>
                    <th id='askVolume' style='display: none;'><%=ASK_VOLUME%></th>
                    -->
                    <th id='thOpenPrice'><%=OPEN_PRICE%></th>
                    <th id='thHightPrice'><%=HIGHEST_PRICE%></th>
                    <th id='thLowestPrice'><%=LOWEST_PRICE%></th>
                    <th><%=COMMENT%></th>
<%--
                    <th><%=PREVIOUS_CLOSE%></th>
--%>

                </tr>
            </thead>
            <tbody>
                <tr id="fibestrow">
                    <td width="6%" class='oddObj' id="<%=stock%>_t_odd" align="center" style='display: none;'>-</td>
                    <td width="8%" class='oddObj' id="<%=stock%>_z_odd" align="center" style='display: none;'>-</td>
                    <td width="10%" id="<%=stock%>_diff_pre" align="center" style='display: none;'>
                      <label class='oddObj' id='<%=stock%>_diff_odd'>-</label>
                      <label class='oddObj' id='<%=stock%>_pre_odd'>-</label>
                    </td>
                    <td width="6%" class='oddObj' id="<%=stock%>_tv_odd" align="center" style='display: none;'>-</td>

                    <td width="6%" class='oddObj' id="<%=stock%>_v" align="center">-</td>
                    <td width="6%" class='oddObj' id="<%=stock%>_Reference_price" align="center">-</td>
                    <td width="6%" class='oddObj' id="<%=stock%>_Reference_volume" align="center">-</td>
                    <!-- 
                    <td width="6%" class='oddObj' id="<%=stock%>_b" align="center" style='display: none;'>-</td>
                    <td width="6%" class='oddObj' id="<%=stock%>_g" align="center" style='display: none;'>-</td>
                    <td width="6%" class='oddObj' id="<%=stock%>_a" align="center" style='display: none;'>-</td>
                    <td width="6%" class='oddObj' id="<%=stock%>_f" align="center" style='display: none;'>-</td>
                    -->
                    <td width="6%" class='oddObj' id="<%=stock%>_o" align="center">-</td>
                    <td width="6%" class='oddObj' id="<%=stock%>_h" align="center">-</td>
                    <td width="6%" class='oddObj' id="<%=stock%>_l" align="center">-</td>
                    
                    <td width="6%" class='oddObj' id="<%=stock%>_comment" align="center"></td>
<%--
                    <td id="<%=stock%>_y" align="center">-</td>
--%>
                </tr>
            </tbody>
        </table>
    </div>

    <br />
    <div id="divRefOdd">
        <label class="title5"><%=REFERENCE_ODD%></label>
        <label id="title_note_odd" class="title"></label>
        <label class="title"></label>
    </div>
    <table id='tabTradeOdd' width="100%" style="background: #ffffff;" class="ui-widget-content">
        <thead style="vertical-align: top;">
            <tr>
                <th align="center"><%=REFERENCE_TIME%></th>
                <th align="center"><%=REFERENCE_PRICE_ODD%></th>
                <th align="center"><%=REFERENCE_VOLUME_ODD%></th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td id="<%=stock%>_Reference_time_odd" align="center">-</td>
                <td id="<%=stock%>_Reference_price_odd" align="center">-</td>
                <td id="<%=stock%>_Reference_volume_odd" align="center">-</td>
            </tr>
        </tbody>
    </table>
    <div id="fiBestDivTitle">
        <label id="labTN" class="title5"><%=FIBEST_TITLE%></label>
        <label id="fibest_title_note" class="title"></label>
        <label class="title" style="float: right;"><%-- <%=UNIT_STR%> --%></label>
    </div>
    <table id='tabTrade' width="100%" style="background: #ffffff;" class="ui-widget-content">
        <thead style="vertical-align: top;">
            <tr>
                <th align="center" width="25%"><%=GROUP_TIMESTR%></th>
                <th align="center" width="25%"><%=LATEST_PRICE%></th>
                <th align="center" width="25%"><%=PRICE_CHANGE_2%></th>
                <th align="center" width="25%"><%=TRADE_VOLUME%></th>
            </tr>
        </thead>
        <tbody>
            <tr>            
                <td id="<%=stock%>_t" align="center">-</td>
                <td id="<%=stock%>_z" align="center">-</td>
                <td align="center">
                    <label id="<%=stock%>_diff">-</label>
                    <label id="<%=stock%>_pre">-</label>
                </td>
                <td id="<%=stock%>_tv" align="center">-</td>
            </tr>
        </tbody>
    </table>

    <br />

    <div style="background: #ffffff;" class="ui-widget-content" id="fiBestDiv">                    
        <table id="hor-minimalist-b">
            <thead>
                <tr>
                    <th align="center"><%=FIBEST_BID_VOLUME%></th>
                    <th align="center"><%=FIBEST_BID_PRICE%></th>                
                    <th align="center"><%=FIBEST_ASK_PRICE%></th>                
                    <th align="center"><%=FIBEST_ASK_VOLUME%></th>
                </tr>
            </thead>
            <tbody id="hor-minimalist-tb">
                <tr>
                    <td align="center" style="color: #000000">-</td>
                    <td align="center">-</td>
                    <td id="<%=stock%>_a4" align="center">-</td>
                    <td id="<%=stock%>_f4" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td align="center" style="color: #000000">-</td>
                    <td align="center">-</td>
                    <td id="<%=stock%>_a3" align="center">-</td>
                    <td id="<%=stock%>_f3" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td align="center" style="color: #000000">-</td>
                    <td align="center">-</td>
                    <td id="<%=stock%>_a2" align="center">-</td>
                    <td id="<%=stock%>_f2" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td align="center" style="color: #000000">-</td>
                    <td align="center">-</td>
                    <td id="<%=stock%>_a1" align="center">-</td>
                    <td id="<%=stock%>_f1" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td align="center" style="color: #000000">-</td>
                    <td align="center">-</td>
                    <td id="<%=stock%>_a0" align="center">-</td>
                    <td id="<%=stock%>_f0" align="center" style="color: #000000">-</td>
                </tr>
                <tr>
                    <td id="<%=stock%>_g0" align="center" style="color: #000000">-</td>
                    <td id="<%=stock%>_b0" align="center">-</td>
                    <td id="<%=stock%>_a0" align="center">-</td>
                    <td id="<%=stock%>_f0" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td id="<%=stock%>_g1" align="center" style="color: #000000">-</td>
                    <td id="<%=stock%>_b1" align="center">-</td>
                    <td id="<%=stock%>_a1" align="center">-</td>
                    <td id="<%=stock%>_f1" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td id="<%=stock%>_g2" align="center" style="color: #000000">-</td>
                    <td id="<%=stock%>_b2" align="center">-</td>
                    <td id="<%=stock%>_a2" align="center">-</td>
                    <td id="<%=stock%>_f2" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td id="<%=stock%>_g3" align="center" style="color: #000000">-</td>
                    <td id="<%=stock%>_b3" align="center">-</td>
                    <td id="<%=stock%>_a3" align="center">-</td>
                    <td id="<%=stock%>_f3" align="center" style="color: #000000">-</td>
                </tr>    
                <tr>
                    <td id="<%=stock%>_g4" align="center" style="color: #000000">-</td>
                    <td id="<%=stock%>_b4" align="center">-</td>
                    <td id="<%=stock%>_a4" align="center">-</td>
                    <td id="<%=stock%>_f4" align="center" style="color: #000000">-</td>
                </tr>
            </tbody>
        </table>
    </div>

    <br />
<%--
    <div id="stockChart" style="height: 310px; display: none; visibility: hidden;"></div>
    <div style="display: none; visibility: hidden;" id="chartview">
        <div class="ui-widget-content" style="width: 100%; background: #ffffff; align: center">
            {Realtime chart}
            <div style="display: none; visibility: hidden;"></div>
            {Historicak chart}
            <div id="techChart" style="width: 100%; height: 500px;"></div>
        </div>
        <br />
        <div class="ui-widget-content" style="background: #d3d3d3; align: center; padding: 10px">
            <%=GROUP_NOTE%>    
        </div>
    </div>
--%>
    <div class="ui-widget-content" style="background: #d3d3d3; align: center;">
        <%=FIBEST_NOTE%>
    </div>
</div>

<script type="text/javascript">
    $(document).ready(function() { 
        initStock("<%=stock%>");
    });
</script>

<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
