<%@ include file="view/headerView.jsp"%>
<%@ include file="view/menuView.jsp"%>

<div id="content" align="center">
    <font id="chartTitle" style="font-weight: bold" size="4"></font> <font id="chartTitle2" size="4"></font><BR>
    <%=UPDATE_TIME0%>
    <label id="userDelay"></label>
    <%=UPDATE_TIME1%>
    <h5>
        <table width="50%" border="0">
            <tr align="center">
                <td width="50%" align="center" valign="top"><font size="4" color="#d03b45"><%=INDEX_FUTURES%>&nbsp;<%=MENU_CHART%></font><label
                    id="INDEX_TX_TIME"></label>
                    <table width="100%" border="0">
                        <tr>
                            <td>
                                <div id="futuresChart" style="height: 322px; width: 454px;" class="clearfix"></div>
                            </td>
                        </tr>
                    </table>
                    <table width="80%" border="0">
                        <tr>
                            <td align="right"><font color="0000ff"><%=INDEX_FUTURES%></font></td>
                            <td align="right"><label id="INDEX_FUTURES"></label></td>
                            <td colspan="2"><label id="INDEX_TX_DIFF"></label></td>
                        </tr>
                        <tr>
                            <td align="right"><font color="0000ff"><%=INDEX_HIGH%></font></td>
                            <td align="right"><label id="INDEX_TX_HIGH">-</label></td>
                            <td width="50px"></td>
                            <td align="left"><font color="0000ff"><%=INDEX_LOW%></font></td>
                            <td align="right"><label id="INDEX_TX_LOW">-</label></td>
                        </tr>
                        <tr>
                            <td align="right"><font color="ff0000"><%=INDEX_VOLUME%></font></td>
                            <td align="right"><label id="INDEX_TX_VOLUME">-</label></td>
                            <td width="50px"></td>
                        </tr>
                        <tr>
                            <td align="right"><font color="921038"><%=INDEX_BID_VOLUME%></font></td>
                            <td align="right"><label id="INDEX_TX_BID_VOLUME">-</label></td>
                            <td width="50px"></td>
                            <td align="left"><font color="921038"><%=INDEX_BID_ORDERS%></font></td>
                            <td align="right"><label id="INDEX_TX_BID_ORDERS">-</label></td>
                        </tr>
                        <tr>
                            <td align="right"><font color="006b89"><%=INDEX_ASK_VOLUME%></font></td>
                            <td align="right"><label id="INDEX_TX_ASK_VOLUME">-</label></td>
                            <td width="50px"></td>
                            <td align="left"><font color="006b89"><%=INDEX_ASK_ORDERS%></font></td>
                            <td align="right"><label id="INDEX_TX_ASK_ORDERS">-</label></td>
                        </tr>
                    </table></td>
                <td width="50%" align="center" valign="top"><font size="4" color="#d03b45"><%=INDEX_IX0126%>&nbsp;<%=MENU_CHART%></font><label
                    id="INDEX_IX0126_TIME"></label>
                    <table width="100%" border="0">
                        <tr>
                            <td>
                                <div id="ix0126Chart" style="height: 322px; width: 454px;" class="clearfix"></div>
                            </td>
                        </tr>
                    </table>
                    <table width="80%" border="0">
                        <tr>
                            <td align="right"><font color="0000ff"><%=INDEX_IX0126%></font></td>
                            <td align="right"><label id="IX0126"></label></td>
                            <td colspan="2"><label id="INDEX_IX0126_DIFF"></label></td>
                        </tr>
                        <tr>
                            <td align="right"><font color="0000ff"><%=INDEX_HIGH%></font></td>
                            <td align="right"><label id="INDEX_IX0126_HIGH">-</label></td>
                            <td width="50px"></td>
                            <td align="left"><font color="0000ff"><%=INDEX_LOW%></font></td>
                            <td align="right"><label id="INDEX_IX0126_LOW">-</label></td>
                        </tr>
                        <%--
                <tr>
                    <td align="right"><font color="ff0000"><%=INDEX_VALUE%></font></td>
                    <td align="right"><label id="INDEX_IX0126_VALUE">-</label></td>
                    <td width="50px"></td>
                    <td align="left" colspan="2"><font color="ff0000"><%=INDEX_UNIT%></font></td>
                </tr>
                <tr>
                    <td align="right"><font color="ff0000"><%=INDEX_VOLUME%></font></td>
                    <td align="right"><label id="INDEX_IX0126_VOLUME">-</label></td>
                    <td width="50px"></td>
                    <td align="left"><font color="ff0000"><%=INDEX_TRANS%></font></td>
                    <td align="right"><label id="INDEX_IX0126_TRANS">-</label></td>
                </tr>
                <tr>
                    <td align="right"><font color="921038"><%=INDEX_BID_VOLUME%></font></td>
                    <td align="right"><label id="INDEX_IX0126_BID_VOLUME">-</label></td>
                    <td width="50px"></td>
                    <td align="left"><font color="921038"><%=INDEX_BID_ORDERS%></font></td>
                    <td align="right"><label id="INDEX_IX0126_BID_ORDERS">-</label></td>
                </tr>
                <tr>
                    <td align="right"><font color="006b89"><%=INDEX_ASK_VOLUME%></font></td>
                    <td align="right"><label id="INDEX_IX0126_ASK_VOLUME">-</label></td>
                    <td width="50px"></td>
                    <td align="left"><font color="006b89"><%=INDEX_ASK_ORDERS%></font></td>
                    <td align="right"><label id="INDEX_IX0126_ASK_ORDERS">-</label></td>
                </tr>
--%>
                    </table></td>
            </tr>
        </table>
    </h5>
</div>
<%@ include file="view/sideView.jsp"%>
<%@ include file="view/footerView.jsp"%>