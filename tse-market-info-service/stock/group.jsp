<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="com.ecloudlife.util.Utility"%>
<%@ include file="view/headerView.jsp"%>
<%@ include file="view/menuView.jsp"%>
<%
    String bp       = purifyString(Utility.getHttpParameter(request,"bp"), "0", 2);
    String ex       = purifyString(Utility.getHttpParameter(request,"ex"), "tse", 5);
    String ind      = purifyString(Utility.getHttpParameter(request,"ind"), "01", 5);
    String currPage = purifyString(Utility.getHttpParameter(request,"currPage"), "0", 5);
    String type     = purifyString(Utility.getHttpParameter(request,"type"), "all", 20);
%>

<div id="content">

<% if (ind != null && ( ! ind.equals("TIDX") &&  ! ind.equals("OIDX"))) { %>
    <div id="CNT_TRADE_NOTE" style="text-align: center; color: red; font-size: 16px; margin-bottom: 8px"><%=CNT_TRADE_NOTE%></div>
<% } %>

<% if (type.equals("warrant")) { %>
    <select id="tseWarrantSelect" class="cSelect"><option ><%=GROUP_TWSE_WARRANT_SELECT%></option></select>
    <select id="otcWarrantSelect" class="cSelect"><option ><%=GROUP_GTSM_WARRANT_SELECT%></option></select>
    <br><br>
<% } %>

<% if (type.equals("all")) { %>
    <select id="tseSelect" class="cSelect"><option ><%=GROUP_TWSE_SELECT%></option></select>
    <select id="otcSelect" class="cSelect"><option ><%=GROUP_GTSM_SELECT%></option></select>
    <br><br>
<% } %>

<div class="groupDiv">
<%
    if (ind != null && (ind.equals("TIDX") || ind.equals("OIDX"))) {
        String subTitle = (ind.equals("TIDX")) ? MARKET_TSE_INDEX_TITLE : MARKET_OTC_INDEX_TITLE;        
%>
        <center>
            <label id="marketIndexDate"></label>
            <label id="marketIndexName"><%=subTitle%></label>
            <br />
            <article>
                <div id="navbar">
                    <ul class="doc-nav">
                        <li><a href="#INDICES"><%=MARKET_INDEX_INDICES %></a></li>
                        <li><a href="#anchorGroup2"><%=MARKET_SUMMARY_BONDS3 %></a></li>
                        <li><a href="#STATISTICS"><%=MARKET_INDEX_STATISTICS %></a></li>
                    </ul>
                </div>
            </article>
        </center>
    <% } %>
    <span id="INDICES"></span>
    <button id='btnChangeToOdd' style='color:red; margin-left: 570px; display: none;'><%=ODD_TRADE %></button>
    <button id='btnChangeToContinues' style='color:red; margin-left: 570px; display: none;'><%=QUOTES_NON_PAIRED_TRADE %></button>
    <div style="float: right" id="userDelayView"> 
        <%=UPDATE_TIME0%> <label id="userDelay0" ></label> <%=UPDATE_TIME1%>
    </div>
    <br />
    <label class="marketName"></label>
    <label class="indName" id="indName">
<%
    if (ind != null) {
        if (ind.equals("B0")) {
            out.println(MENU_ETF);
        } else if (ind.equals("Q0")) {
            if (ex.equals("tse")) {
                out.println(TWSE_FOREIGN);
            } else {
                out.println(GTSM_FOREIGN);
            }
        } else if (ind.equals("E0")) {
            out.println(MENU_TDR);
        }
    }

    if (ind != null && (ind.equals("TIDX") || ind.equals("OIDX"))) {
        out.println(MARKET_INDEX_INDICES);
    }

    if(bp.equals("03")){
        if (ex.equals("tse")) {
            out.println(TIB);
        } else {
            //移除興櫃戰略新板
            //out.println(PSB);
        }
    }
%>
    </label>
        
        <%if(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX"))  )
        {%>
        <label id="marketIndexTime"></label><label class="marketIndexStr">(<%=GROUP_TIMESTR%></label>
        <%}else{%>
        <label id='labContinuesQuotation' style='color:red; display: none;'>(<%=QUOTES_QUOTATION %>)</label>
        <label id='labOddQuotation' style='color:red; display: none;'>(<%=ODD_QUOTATION %>)</label>
        <label class="title" style="float: right;"><%=UNIT_STR%></label>
        <%}%>
    <br>

    <table id="group" class="newspaper-a" style='display: none;'>
        <thead>
            <th></th>
            <%if(type!=null && type.equals("postponed")) { %>
            <th></th>
            <%}%>
            <%if(type!=null && type.equals("postponedOpening")) { %>
            <th></th>
            <%}%>
            <th><%=(ind!=null && ( ind.equals("TIDX") || ind.equals("OIDX") ) )? MARKET_INDEX:LATEST_PRICE%></th>
            <th><%=PRICE_CHANGE%></th>
            
            
            <%if(!(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX")  ))){%>
            <th align="center"><%=TRADE_VOLUME%></th>
            <th align="center"><%=ACC_TRADE_VOLUME%></th>
            <%if(!(type!=null && type.equals("postponed"))&& !(type!=null && type.equals("postponedOpening")) ){%>
            <th align="center"><%=REFERENCE_PRICE%></th>
            <th align="center"><%=REFERENCE_VOLUME%></th>
            <%}%>

            <%}%>
            <th><%=OPEN_PRICE%></th>        
            <th><%=HIGHEST_PRICE%></th>
            <th><%=LOWEST_PRICE%></th>
            
            <%--<th><%=PREVIOUS_CLOSE%></th>--%>
            <%if(!(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX")  ))){%>
            <%--<th><%=ACC_TRADE_VOLUME%></th>--%>
            <th><%=CURRENT_TIME%></th>
            
            <%}%>
            <%=(ind!=null && ind.equals("B0"))? "<th>"+GROUP_ETF_ESTIMATED+"</th>":""%>            
            <th>
                <%=((type!=null && type.equals("warrant")) ||(ind!=null  && (ind.equals("TIDX") || ind.equals("OIDX"))))?  "":COMMENT %>
                <%=(type!=null && type.equals("warrant"))? GROUP_WARRANT_TARGET:""%>
            </th>
        </thead>
        <tbody>
        </tbody>
    </table>
    <table id="group_odd" class="newspaper-odd" style='display: none;'>
        <thead>
            <th></th>
            <%if(type!=null && type.equals("postponed")) { %>
            <th></th>
            <%}%>
            <%if(type!=null && type.equals("postponedOpening")) { %>
            <th></th>
            <%}%>
            <th><%=(ind!=null && ( ind.equals("TIDX") || ind.equals("OIDX") ) )? MARKET_INDEX:LATEST_TRADE_PRICE%></th>
            <th><%=PRICE_CHANGE%></th>
            
            
            <%if(!(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX")  ))){%>
            <th align="center"><%=PRE_TRADE_VOLUME%></th>
            <th align="center"><%=ACC_TRADE_VOLUME%></th>
            <%if(!(type!=null && type.equals("postponed"))&& !(type!=null && type.equals("postponedOpening")) ){%>
            <th align="center"><%=REFERENCE_PRICE%></th>
            <th align="center"><%=REFERENCE_VOLUME%></th>
            <%}%>
 
            <th align="center"><%=BID_PRICE%></th>
            <th align="center"><%=BID_VOLUME%></th>
            <th align="center"><%=ASK_PRICE%></th>
            <th align="center"><%=ASK_VOLUME%></th>

            <%}%>
            <th id='thHightPrice'><%=HIGHEST_PRICE%></th>
            <th id='thLowestPrice'><%=LOWEST_PRICE%></th>
            
            <%--<th><%=PREVIOUS_CLOSE%></th>--%>
            <%if(!(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX")  ))){%>
            <%--<th><%=ACC_TRADE_VOLUME%></th>--%>
            <th><%=TIME%></th>
            
            <%}%>
            <%=(ind!=null && ind.equals("B0"))? "<th>"+GROUP_ETF_ESTIMATED+"</th>":""%>            
            <th>
                <%=((type!=null && type.equals("warrant")) ||(ind!=null  && (ind.equals("TIDX") || ind.equals("OIDX"))))?  "":COMMENT %>
                <%=(type!=null && type.equals("warrant"))? GROUP_WARRANT_TARGET:""%>
            </th>
        </thead>
        <tbody>
        </tbody>
    </table>

    <% //OTC 債券指數
    if(ind != null && ind.equals("OIDX")) { %>
        <br />
        <label class="indName"><%=MARKET_SUMMARY_BONDS%></label>
        <label class="marketIndexStr">(<%=GROUP_TIMESTR%><label style="float: right" class="marketIndexBondsTime"></label></label>
        <table id="group_bonds" class="newspaper-a">
            <thead>
                <tr>
                    <th></th>
                    <th><%=MARKET_INDEX%></th>
                    <th><%=PRICE_CHANGE%></th>
                    <th><%=OPEN_PRICE%></th>
                    <th><%=HIGHEST_PRICE%></th>
                    <th><%=LOWEST_PRICE%></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    <% } %>

    <% // TSE報酬指數
    if(ind!=null && (ind.equals("TIDX") )  ){%>
        <br />
        <span id="anchorGroup2"></span>
        <label class="marketName"></label><label class="indName"><%=MARKET_SUMMARY_BONDS3%></label>
            <label class="marketIndexStr">(<%=GROUP_TIMESTR%><label style="float:right" class="marketIndexBondsTime"></label></label>
        <table id="group_bonds3" class="newspaper-a">
            <thead>
                <th></th>
                <th><%=MARKET_INDEX%></th>
                <th><%=PRICE_CHANGE%></th>
                <th><%=OPEN_PRICE%></th>
                <th><%=HIGHEST_PRICE%></th>
                <th><%=LOWEST_PRICE%></th>
                <th></th>
            </thead>
            <tbody>

            </tbody>
        </table>
    <%}%>

    <% // OTC報酬指數    
    if(ind!=null && (ind.equals("OIDX") )  ){%>
        <br />
        <span id="anchorGroup2"></span>
        <label class="marketName"></label><label class="indName"><%=MARKET_SUMMARY_BONDS3%></label>
        <label class="marketIndexStr">(<%=GROUP_TIMESTR%><label style="float: right" class="marketIndexBondsTime"></label></label>
        <table id="group_bonds4" class="newspaper-a">
            <thead>
                <tr>
                    <th></th>
                    <th><%=MARKET_INDEX%></th>
                    <th><%=PRICE_CHANGE%></th>
                    <th><%=OPEN_PRICE%></th>
                    <th><%=HIGHEST_PRICE%></th>
                    <th><%=LOWEST_PRICE%></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    <% } %>
    <% // 跨市場指數
    if(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX"))  ){%>
        <br /> <label class="indName"><%=MARKET_SUMMARY_BONDS2%></label>
        <%--<label class="marketIndexStr">(<%=GROUP_TIMESTR%><label style="float:right" class="marketIndexBondsTime"></label></label>--%>
        <table id="group_bonds2" class="newspaper-a">
            <thead>
                <tr>
                    <th></th>
                    <th><%=MARKET_INDEX%></th>
                    <th><%=PRICE_CHANGE%></th>
                    <th><%=OPEN_PRICE%></th>
                    <th><%=HIGHEST_PRICE%></th>
                    <th><%=LOWEST_PRICE%></th>
                    <th><%=CURRENT_TIME%></th>
                </tr>
            </thead>
            <tbody>
            </tbody>
        </table>
    <%}%>
</div>
<div id="Pagination" class="pagination"></div>
<div style="float: right" id="Pagination1">
    <%=GROUP_NO_PRE_PAGE%>
    <select id="prePage">
        <option value="0">10</option>
        <option value="1">20</option>
        <option value="2">50</option>
        <option value="3">100</option>
    </select> | <%=GROUP_TOTAL_RECORDS%><label id="totlaRecords"></label>
</div>

    <%
    //if(ind==null )
    {%>
        <br>
        <br>
        <div class="ui-widget-content group-footer" style="background: #d3d3d3; align: center;">
            <%
            /*
            if(ex==null && ind==null&& !(type!=null && type.equals("postponed"))&& !(type!=null && type.equals("postponedOpening")) )
            {
                out.println(GROUP_NOTE);
            }
            else
            {
                out.println(GROUP_NOTE_FOR_POSTPONED);
            }    
            */    
            if( (type!=null && type.equals("postponed")) || (type!=null && type.equals("postponedOpening")) )
            {
                out.println(GROUP_NOTE_FOR_POSTPONED);
            }
            else if(ind.equals("B0"))
            {
                out.println(GROUP_NOTE_ETF);
            }
            else if(ind.equals("Q0"))
            {
                out.println(GROUP_NOTE_FOREIGN);
            }
            else if(ind.equals("E0"))
            {
                out.println(GROUP_NOTE_TDR);
            }
            else
            {
                out.println(GROUP_NOTE);
            }
            %>
        </div>
    
    <%}/*
    else if(ind!=null)
    {%>
        <br>
        <br>
        <div class="ui-widget-content group-footer" style="background: #d3d3d3; align: center;">
        <%
            if(ind.equals("B0"))
            {
                out.println(GROUP_NOTE_ETF);
            }
            else if(ind.equals("Q0"))
            {
                out.println(GROUP_NOTE_FOREIGN);
            }
            else if(ind.equals("E0"))
            {
                out.println(GROUP_NOTE_TDR);
            }
        %>
        </div>
    <%} */
    %>

<% if (ind != null && (ind.equals("TIDX") || ind.equals("OIDX"))) { %>
<div id="detail" class="detail">
    <span id="STATISTICS"></span>
    <label id="subtitle"><% if(ind!=null){ out.println(MARKET_INDEX_STATISTICS);} %></label>

    <div  class="ui-widget-content"  style="width: 100%; background: #ffffff; align: center">
        <div class="ui-state-highlight" >
            <%=MARKET_SUMMARY_TRADES%> (<%=CURRENT_TIME%>:<label  id="tradeTime" align="right"></label>)
        </div>
        <table  class="one-column-emphasis" style="width: 100%">
            <thead>
                <tr align="right">
                    <th></th>
                    <th><%=MARKET_SUMMARY_OVERALL_MARKET%></th>
                    <th>
                      <%
                      if(ind.equals("TIDX"))
                          out.println(MARKET_SUMMARY_SECURITIES_TIDX);
                      else
                          out.println(MARKET_SUMMARY_SECURITIES_OIDX);
                      %>
                    </th>
                      <%
                      // 移除興櫃戰略新板（參數n）
                      if(ind.equals("TIDX")) {
                          out.println("<th>" + MARKET_SUMMARY_SECURITIES_TIB + "</th>");
                      }
                      %>
                    <th><%=MARKET_SUMMARY_FUNDS%></th>
                    <th><%=MARKET_SUMMARY_CALL_WARRANES%></th>
                    <th><%=MARKET_SUMMARY_PUT_WARRANTS%></th>
                </tr>
            </thead>
            <tbody>                
<%
                String[] head = new String[] { MARKET_SUMMARY_TRADE_VALUE,MARKET_SUMMARY_TRADE_VOLUME,MARKET_SUMMARY_TRANSACTION };
                // 移除興櫃戰略新板（參數n）
                String[] seqArr1;
                if(ind.equals("TIDX")) {
                    seqArr1 = new String[] { "t","f","n","s","c","b" };
                } else {
                    seqArr1 = new String[] { "t","f","s","c","b" };
                }

                String[] seqArr2 = new String[] {"z","v","r" };
                
                for (int i = 0; i < seqArr2.length; i++) {
                    String temp = "<tr><td>"+head[i]+"</td>";
                    for (int j = 0; j < seqArr1.length; j++) {
                        temp += "<td id=\"" + seqArr1[j] + seqArr2[i] + "\" align='right'>-</td>";
                    }
                    temp += "</tr>";
                    out.println(temp);
                }
%>
            </tbody>
        </table>
    </div>

    <br />

    <div class="ui-widget-content"  style="width: 100%; background: #ffffff; align: center">
        <div class="ui-state-highlight" >
            <%=MARKET_SUMMARY_ORDERS%> (<%=CURRENT_TIME%>:<label  id="orderTime" align="right"></label>)
        </div>
        <table class="one-column-emphasis" style="width: 100%">
            <thead>
                <tr align="right">
                    <th></th>
                    <th><%=MARKET_SUMMARY_OVERALL_MARKET%></th>
                    <th>
                      <%
                      if(ind.equals("TIDX"))
                          out.println(MARKET_SUMMARY_SECURITIES_TIDX);
                      else
                          out.println(MARKET_SUMMARY_SECURITIES_OIDX);
                      %>
                    </th>
                      <%
                      // 移除興櫃戰略新板（參數n）
                      if(ind.equals("TIDX")) {
                          out.println("<th>" + MARKET_SUMMARY_SECURITIES_TIB + "</th>");
                      }
                      %>
                    <th><%=MARKET_SUMMARY_FUNDS%></th>
                    <th><%=MARKET_SUMMARY_CALL_WARRANES%></th>
                    <th><%=MARKET_SUMMARY_PUT_WARRANTS%></th>
                </tr>
            </thead>
            <tbody>
<%
                head = new String[] {
                        MARKET_SUMMARY_OVERALL_BID_VOLUME,MARKET_SUMMARY_OVERALL_BID_ORDERS,MARKET_SUMMARY_OVERALL_ASK_VOLUME,MARKET_SUMMARY_OVERALL_ASK_ORDERS,
                        MARKET_SUMMARY_BID_VOLUME_AT_LIMIE_UP,MARKET_SUMMARY_BID_ORDERS_AT_LIMIE_UP,MARKET_SUMMARY_ASK_VOLUME_AT_LIMIE_UP,MARKET_SUMMARY_ASK_ORDERS_AT_LIMIE_UP,
                        MARKET_SUMMARY_BID_VOLUME_AT_LIMIE_DOWN,MARKET_SUMMARY_BID_ORDERS_AT_LIMIE_DOWN,MARKET_SUMMARY_ASK_VOLUME_AT_LIMIE_DOWN,MARKET_SUMMARY_ASK_ORDERS_AT_LIMIE_DOWN
                };
                if(ind.equals("TIDX")) {
                    seqArr1 = new String[] {"t","s","n","f","c","b"};
                } else {
                    seqArr1 = new String[] {"t","s","f","c","b"};
                }

                seqArr2 = new String[] {"4","2","3","1","u4","u2","u3","u1","w4","w2","w3","w1"};
                
                for(int i = 0; i < seqArr2.length; i++) {
                    String temp = "<tr><td>" + head[i] + "</td>";
                    for(int j = 0; j < seqArr1.length; j++) {
                        temp += "<td id=\"" + seqArr1[j] + seqArr2[i] + "\" align='right'>-</td>";
                    }
                    temp += "</tr>";
                    out.println(temp);
                }                    
%>    
            </tbody>
        </table>
    </div>
    
    <% if (ind != null && (ind.equals("TIDX"))) { %>
        <br />
        <div class="" style="background: #d3d3d3; align: center; padding: 10px">
            <%=MARKET_SUMMARY_NOTE_TIDX%>
        </div>
    <% } %>
    <% if (ind != null && (ind.equals("OIDX"))) { %>
        <br />
        <div class="" style="background: #d3d3d3; align: center; padding: 10px">
            <%=MARKET_SUMMARY_NOTE_OIDX%>
        </div>
    <% } %>
</div>
<% } %>
</div>

<div id="tdrDialog" title="<%=GROUP_TDR_DIALOG_TITLE%>">
    <p align="left"><%=GROUP_TDR_DIALOG_CONTENT%></p>
</div>

<a href="#" id="scroll-to-top" class="active">↑</a>

<script type="text/javascript">
    var type = "<%=type%>";
    $("#tdrDialog").dialog({
        autoOpen: false,
        width: "30%",
        modal: false,
        show: {
            effect: "blind",
            duration: 300
        },
        hide: {
            effect: "explode",
            duration: 300
        },
        position: {
            my: "left top",
            at: "left top",
            of: window
        }
    });
    loadPageChangeFunction();

<%/*if(ex==null && ind==null&& !(type!=null && type.equals("postponed"))&& !(type!=null && type.equals("postponedOpening")) ){*/%>

<% if ((type != null && type.equals("postponed"))){ %>
    currPage = "<%=currPage%>";
    newLoadPostponedStockList('4');
<% } else if ((type != null && type.equals("postponedOpening"))) { %>
    currPage = "<%=currPage%>";
    newLoadPostponedStockList('5');
<% } else if ( ! (type.equals("fixed"))) { %>
    //不分市場的畫面
    ex = "<%=ex%>";
    ind = "<%=ind%>";
    currPage = "<%=currPage%>";
    loadGroupList();
<% } else { %>
    ex = "<%=ex%>";
    ind = "<%=ind%>";
    bp = "<%=bp%>";
    currPage = "<%=currPage%>";
    loadStockList();
    if (ind == ("E0")) {
        $("#tdrDialog").dialog("open");
    }
    if (ind == "TIDX" || ind == "OIDX") {
        loadSummaryMarket();
    }
<% } %>
</script>
<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
