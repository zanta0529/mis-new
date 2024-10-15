<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp" %>

<%@ page import="java.util.*" %>
<%@ include file="view/headerView.jsp" %>
<%@ include file="view/menuView.jsp" %>

<%
if (request.getMethod().equals("POST")) {
    Thread.currentThread().sleep(1300);
    response.sendRedirect("http://1.1.1.6");
    return;
}
%>
<!--
<script type="text/javascript">
    var uvOptions = {};
    (function() {
        var uv = document.createElement('script'); uv.type = 'text/javascript'; uv.async = true;
        uv.src = ('https:' == document.location.protocol ? 'https://' : 'http://') + '535gt.willmobile.com/stock/js/twse_chart.jsp?lang=<%=lang%>';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(uv, s);
    })();
</script>
-->
<div id="content" align="center">
	<font id="chartTitle" style="font-weight:bold" size="4"></font>  <font id="chartTitle2" size="4" ></font><BR>
	<%=UPDATE_TIME0%> <label id="userDelay" ></label> <%=UPDATE_TIME1%>
	<h5>

	<table width="100%" border=0>
	<tr align="center">
		<td width="50%" align="center">
			<font size="4" color="#d03b45"><%=INDEX_TWSE_CHART%></font>  <label id="INDEX_TWSE_TIME"></label>
			<table width="100%">
				<tr>
					<td align="right">
						<div id="twseChart" style="height:322px;width:440px;"></div>
					</td>
				</tr>
                <tr>
                    <td>
                        <table width="80%" border=0 align='center'>
        				
            				<tr>
            					<td align="right"><font color="0000ff"><%=INDEX_TAIEX%></font></td>
            					<td align="right"><label id="INDEX_TWSE"></label></td>
            					<td colspan="2"><label id="INDEX_TWSE_DIFF"></label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="0000ff"><%=INDEX_HIGH%></font></td>
            					<td align="right"><label id="INDEX_TWSE_HIGH">-</label></td>
            					<td width="50px"></td>
            					<td align="left"><font color="0000ff"><%=INDEX_LOW%></font></td>
            					<td align="right"><label id="INDEX_TWSE_LOW">-</label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="ff0000"><%=INDEX_VALUE%></font></td>
            					<td align="right" ><label id="INDEX_TWSE_VALUE">-</label></td>
            					<td width="50px"></td>
            					<td align="left" colspan="2"><font color="ff0000" ><%=INDEX_UNIT%></font></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="ff0000"><%=INDEX_VOLUME%></font></td>
            					<td align="right"><label id="INDEX_TWSE_VOLUME">-</label></td>
            					<td width="50px"></td>
            					<td align="left"><font color="ff0000"><%=INDEX_TRANS%></font></td>
            					<td align="right"><label id="INDEX_TWSE_TRANS">-</label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="921038"><%=INDEX_BID_VOLUME%></font></td>
            					<td align="right"><label id="INDEX_TWSE_BID_VOLUME">-</label></td>
            					<td width="50px"></td>
            					<td align="left"><font color="921038"><%=INDEX_BID_ORDERS%></font></td>
            					<td align="right"><label id="INDEX_TWSE_BID_ORDERS">-</label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="006b89"><%=INDEX_ASK_VOLUME%></font></td>
            					<td align="right"><label id="INDEX_TWSE_ASK_VOLUME">-</label></td>
            					<td width="50px"></td>
            					<td align="left"><font color="006b89"><%=INDEX_ASK_ORDERS%></font></td>
            					<td align="right"><label id="INDEX_TWSE_ASK_ORDERS">-</label></td>
            				</tr>
            			</table>
			</table>
		</td>
		<!--<td width="1px" style="background-color:e0e0e0"></td>-->
		<td width="50%" align="center">
			<font size="4" color="#d03b45"><%=INDEX_OTC_CHART%></font>  <label id="INDEX_OTC_TIME"></label>
			
			<table width="100%" border=0>
				<tr>
					<td>
						<div id="otcChart" style="height:322px;width:440px;"></div>
					</td>
				</tr>
                
                <tr>
                    <td>
                        <table width="80%" border=0 align='center'>
            				<tr>
            					<td align="right"><font color="0000ff"><%=INDEX_GTSM%></font></td>
            					<td align="right"><label id="INDEX_OTC"></td>
            
            					<td colspan="2"><label id="INDEX_OTC_DIFF"></label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="0000ff"><%=INDEX_HIGH%></font></td>
            					<td align="right"><label id="INDEX_OTC_HIGH">-</label></td>
            					<td width="50px"></td>
            					<td align="left" ><font color="0000ff"><%=INDEX_LOW%></font></td>
            					<td align="right"><label id="INDEX_OTC_LOW">-</label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="ff0000"><%=INDEX_VALUE%></font></td>
            					<td align="right"><label id="INDEX_OTC_VALUE">-</label></td>
            					<td width="50px"></td>
            					<td align="left" colspan="2"><font color="ff0000"><%=INDEX_UNIT%></font></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="ff0000"><%=INDEX_VOLUME%></font></td>
            					<td align="right"><label id="INDEX_OTC_VOLUME">-</label></td>
            					<td width="50px"></td>
            					<td align="left"><font color="ff0000"><%=INDEX_TRANS%></font></td>
            					<td align="right"><label id="INDEX_OTC_TRANS">-</label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="921038"><%=INDEX_BID_VOLUME%></font></td>
            					<td align="right"><label id="INDEX_OTC_BID_VOLUME">-</label></td>
            					<td width="50px"></td>
            					<td align="left"><font color="921038"><%=INDEX_BID_ORDERS%></font></td>
            					<td align="right"><label id="INDEX_OTC_BID_ORDERS">-</label></td>
            				</tr>
            				<tr>
            					<td align="right"><font color="006b89"><%=INDEX_ASK_VOLUME%></font></td>
            					<td align="right"><label id="INDEX_OTC_ASK_VOLUME">-</label></td>
            					<td width="50px"></td>
            					<td align="left"><font color="006b89"><%=INDEX_ASK_ORDERS%></font></td>
            					<td align="right"><label id="INDEX_OTC_ASK_ORDERS">-</label></td>
            				</tr>
            			</table>
                    </td>
                </tr>
<!--
				<tr>
					<td align="right"><font color="0000ff"><%=INDEX_GTSM%></font></td>
					<td align="right"><label id="INDEX_OTC"></td>

					<td colspan="2"><label id="INDEX_OTC_DIFF"></label></td>
				</tr>
				<tr>
					<td align="right"><font color="0000ff"><%=INDEX_HIGH%></font></td>
					<td align="right"><label id="INDEX_OTC_HIGH">-</label></td>
					<td width="50px"></td>
					<td align="left" ><font color="0000ff"><%=INDEX_LOW%></font></td>
					<td align="right"><label id="INDEX_OTC_LOW">-</label></td>
				</tr>
				<tr>
					<td align="right"><font color="ff0000"><%=INDEX_VALUE%></font></td>
					<td align="right"><label id="INDEX_OTC_VALUE">-</label></td>
					<td width="50px"></td>
					<td align="left" colspan="2"><font color="ff0000"><%=INDEX_UNIT%></font></td>
				</tr>
				<tr>
					<td align="right"><font color="ff0000"><%=INDEX_VOLUME%></font></td>
					<td align="right"><label id="INDEX_OTC_VOLUME">-</label></td>
					<td width="50px"></td>
					<td align="left"><font color="ff0000"><%=INDEX_TRANS%></font></td>
					<td align="right"><label id="INDEX_OTC_TRANS">-</label></td>
				</tr>
				<tr>
					<td align="right"><font color="921038"><%=INDEX_BID_VOLUME%></font></td>
					<td align="right"><label id="INDEX_OTC_BID_VOLUME">-</label></td>
					<td width="50px"></td>
					<td align="left"><font color="921038"><%=INDEX_BID_ORDERS%></font></td>
					<td align="right"><label id="INDEX_OTC_BID_ORDERS">-</label></td>
				</tr>
				<tr>
					<td align="right"><font color="006b89"><%=INDEX_ASK_VOLUME%></font></td>
					<td align="right"><label id="INDEX_OTC_ASK_VOLUME">-</label></td>
					<td width="50px"></td>
					<td align="left"><font color="006b89"><%=INDEX_ASK_ORDERS%></font></td>
					<td align="right"><label id="INDEX_OTC_ASK_ORDERS">-</label></td>
				</tr>
-->				
			</table>
		</td>
	</tr>
	</table>
	<%=INDEX_NOTE0%><BR>
	<%=INDEX_NOTE%>
	<BR>



	</h5>
<!--
<div id="twsechart" style="width:230;height:115" ></div>
-->
</div>
<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
