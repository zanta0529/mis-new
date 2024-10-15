<%@ include file="view/headerView.jsp" %>
<%@ include file="view/menuView.jsp" %>
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
	<font id="chartTitle" style="font-weight:bold"  size="4"></font>  <font id="chartTitle2" size="4" ></font><BR>
	<%=UPDATE_TIME0%> <label id="userDelay" ></label> <%=UPDATE_TIME1%>
	<h5>
	
	<table width="50%">	
	<tr align="center">
		<td width="50%" align="center">
			<font size="4" color="#d03b45"><%=INDEX_FRMSA_CHART%></font>  <label id="INDEX_FRMSA_TIME"></label>
			<table width="100%">	
				<tr>
					<td>
						<div id="frmsaChart" style="height:322px ;" class="clearfix"></div>
					</td>
				</tr>
			</table>
			<table width="80%">
				<tr>
					<td align="right"><font color="0000ff"><%=INDEX_FRMSAEX%></font></td>
					<td align="right"><label id="INDEX_FRMSA"></label></td>
					<td colspan="2"><label id="INDEX_FRMSA_DIFF"></label></td>
				</tr>	    
				<tr>
					<td align="right"><font color="0000ff"><%=INDEX_HIGH%></font></td>
					<td align="right"><label id="INDEX_FRMSA_HIGH">-</label></td>
					<td width="50px"></td>
					<td align="left"><font color="0000ff"><%=INDEX_LOW%></font></td>
					<td align="right"><label id="INDEX_FRMSA_LOW">-</label></td>
				</tr>	
				<tr>
					<td align="right"><font color="ff0000"><%=INDEX_VALUE%></font></td>
					<td align="right" ><label id="INDEX_FRMSA_VALUE">-</label></td>
					<td width="50px"></td>
					<td align="left" colspan="2"><font color="ff0000" ><%=INDEX_UNIT%></font></td>
				</tr>	
				<tr>
					<td align="right"><font color="ff0000"><%=INDEX_VOLUME%></font></td>
					<td align="right"><label id="INDEX_FRMSA_VOLUME">-</label></td>
					<td width="50px"></td>
					<td align="left"><font color="ff0000"><%=INDEX_TRANS%></font></td>
					<td align="right"><label id="INDEX_FRMSA_TRANS">-</label></td>
				</tr>	
				<tr>
					<td align="right"><font color="921038"><%=INDEX_BID_VOLUME%></font></td>
					<td align="right"><label id="INDEX_FRMSA_BID_VOLUME">-</label></td>
					<td width="50px"></td>
					<td align="left"><font color="921038"><%=INDEX_BID_ORDERS%></font></td>
					<td align="right"><label id="INDEX_FRMSA_BID_ORDERS">-</label></td>
				</tr>	
				<tr>
					<td align="right"><font color="006b89"><%=INDEX_ASK_VOLUME%></font></td>
					<td align="right"><label id="INDEX_FRMSA_ASK_VOLUME">-</label></td>
					<td width="50px"></td>
					<td align="left"><font color="006b89"><%=INDEX_ASK_ORDERS%></font></td>
					<td align="right"><label id="INDEX_FRMSA_ASK_ORDERS">-</label></td>
				</tr>	
			</table>	
		</td>
		<!--<td width="1px" style="background-color:e0e0e0"></td>-->
		
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