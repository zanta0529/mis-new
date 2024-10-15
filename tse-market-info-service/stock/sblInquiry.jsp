<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="com.ecloudlife.util.Utility"%>

<%@ include file="view/headerView.jsp"%>
<%@ include file="view/menuView.jsp"%>

<script >
<%
String stock = purifyString(Utility.getHttpParameter(request, "stock"), "", 10);
if(stock != null && ! stock.isEmpty()) {
%>
	stock="<%=stock%>";
	initStock("<%=stock%>");
<%
}
%>

	$(document).ready(function() 
	{ 
		$.getJSON(apiBaseUrl+"resetSession.jsp");
		$("#print_btn").click(function()
		{
			window.print();
				
		});
	});
	
</script>	
<div id="content">
	
	<div  class="ui-widget-content"  style="width:100%;background:#ffffff;align:center">
		<table width=100% >
			<tr>
				<td width="50%"><label id="stkname"></label>(<label id="stkno"></label>)</td>
				<td align="right" width="50%"><%=SBL_STOCK%><select id="stock"><option>--------------------</option></select>
				<button id="print_btn"><%=PRINT_BUTTON%></button></td>
			</tr>
		</table>
<%--
		<div>
		<div style="display:inline-block;">
			<label id="stkname"></label>(<label id="stkno"></label>)
		</div> 
		<div style="display:inline-block;float:right">
			<%=SBL_STOCK%><select id="stock"></select>
		</div>
		</div>
--%>
	</div>	
	
	<div  class="ui-widget-content"  style="width:100%;background:#ffffff;align:center">
		<div class="ui-state-highlight" >
			<%=SBL_FIXED_RATE_TRANSACTION%> (<%=CURRENT_TIME%>:<label  id="FA_TIME" align="right"></label>)
		</div>
		<table  class="one-column-emphasis" style="width:100%">
			<thead>
				<th></th>
				<th align="right"><%=SBL_ACC_TRADE_DAY_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_LENDING_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_BORROWING_VOLUME%></th>
				<th align="right"><%=SBL_BORROWING_RATE%></th>
			</thead>
			<tbody>
				<tr>
					<td><%=SBL_10_RECALL_NOTICE%></td>
					<td id="FA_0" align="right"></td>
					<td id="FA_1" align="right"></td>
					<td id="FA_2" align="right"></td>
					<td id="FA_3" align="right"></td>
				</tr>
				<tr>
					<td><%=SBL_3_RECALL_NOTICE%></td>
					<td id="F3_0" align="right"></td>
					<td id="F3_1" align="right"></td>
					<td id="F3_2" align="right"></td>
					<td id="F3_3" align="right"></td>
				</tr>		
				<tr>
					<td><%=SBL_1_RECALL_NOTICE%></td>
					<td id="F1_0" align="right"></td>
					<td id="F1_1" align="right"></td>
					<td id="F1_2" align="right"></td>
					<td id="F1_3" align="right"></td>
				</tr>				
			</tbody>	
		</table>	
	</div>	
	<BR>
	<div  class="ui-widget-content"  style="width:100%;background:#ffffff;align:center">
		<div class="ui-state-highlight" >
			<%=SBL_BID_OFFER_TRANSACTION%> <%=SBL_BID_OFFER_TRANSACTION_NOTICE%>(<%=CURRENT_TIME%>:<label  id="CA_TIME" align="right"></label>)
		</div>
		<table  class="one-column-emphasis" style="width:100%">
			<thead>
				<th align="right"><%=SBL_ACC_TRADE_DAY_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_LENDING_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_BORROWING_VOLUME%></th>
				<th align="right"><%=SBL_MATCHED_VOLUME%></th>
				<th align="right"><%=SBL_MATCHED_RATE%></th>
			</thead>
			<tbody>
				<tr>
					<td id="CA_0" align="right"></td>
					<td id="CA_1" align="right"></td>
					<td id="CA_2" align="right"></td>
					<td id="CA_3" align="right"></td>
					<td id="CA_4" align="right"></td>
				</tr>
			</tbody>	
		</table>
		<BR>	
		<div class="ui-state-highlight" >
			<%=SBL_FIVE_BEST_BIDS_AND_ASKS%>
		</div>
		<table  class="one-column-emphasis" style="width:100%">
			<thead>
				<th align="right"><%=SBL_BORROWING_RATE%></th>
				<th align="right"><%=SBL_BORROWING_VOLUME%></th>
				<th align="right"><%=SBL_LENDING_RATE%></th>
				<th align="right"><%=SBL_LENDING_VOLUME%></th>
			</thead>
			<tbody>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="CA5_CF5" align="right"></td>
					<td id="CA5_CQ5" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="CA5_CF4" align="right"></td>
					<td id="CA5_CQ4" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="CA5_CF3" align="right"></td>
					<td id="CA5_CQ3" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="CA5_CF2" align="right"></td>
					<td id="CA5_CQ2" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="CA5_CF1" align="right"></td>
					<td id="CA5_CQ1" align="right"></td>
				</tr>
				<tr>
					<td id="CA5_DF1" align="right"></td>
					<td id="CA5_DQ1" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="CA5_DF2" align="right"></td>
					<td id="CA5_DQ2" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="CA5_DF3" align="right"></td>
					<td id="CA5_DQ3" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="CA5_DF4" align="right"></td>
					<td id="CA5_DQ4" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="CA5_DF5" align="right"></td>
					<td id="CA5_DQ5" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
			</tbody>	
		</table>	
	</div>
	
	<BR>
	
	<div  class="ui-widget-content"  style="width:100%;background:#ffffff;align:center">
		<div class="ui-state-highlight" >
			<%=SBL_BID_OFFER_TRANSACTION%> <%=SBL_BID_OFFER_TRANSACTION_NOTICE_3%>(<%=CURRENT_TIME%>:<label  id="C3_TIME" align="right" ></label>)
		</div>
		<table  class="one-column-emphasis" style="width:100%">
			<thead>
				<th align="right"><%=SBL_ACC_TRADE_DAY_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_LENDING_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_BORROWING_VOLUME%></th>
				<th align="right"><%=SBL_MATCHED_VOLUME%></th>
				<th align="right"><%=SBL_MATCHED_RATE%></th>
			</thead>
			<tbody>
				<tr>
					<td id="C3_0" align="right"></td>
					<td id="C3_1" align="right"></td>
					<td id="C3_2" align="right"></td>
					<td id="C3_3" align="right"></td>
					<td id="C3_4" align="right"></td>
				</tr>
			</tbody>	
		</table>
		<BR>	
		<div class="ui-state-highlight" >
			<%=SBL_FIVE_BEST_BIDS_AND_ASKS%>
		</div>
		<table  class="one-column-emphasis" style="width:100%">
			<thead>
				<th align="right"><%=SBL_BORROWING_RATE%></th>
				<th align="right"><%=SBL_BORROWING_VOLUME%></th>
				<th align="right"><%=SBL_LENDING_RATE%></th>
				<th align="right"><%=SBL_LENDING_VOLUME%></th>
			</thead>
			<tbody>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C35_CF5" align="right"></td>
					<td id="C35_CQ5" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C35_CF4" align="right"></td>
					<td id="C35_CQ4" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C35_CF3" align="right"></td>
					<td id="C35_CQ3" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C35_CF2" align="right"></td>
					<td id="C35_CQ2" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C35_CF1" align="right"></td>
					<td id="C35_CQ1" align="right"></td>
				</tr>
				<tr>
					<td id="C35_DF1" align="right"></td>
					<td id="C35_DQ1" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C35_DF2" align="right"></td>
					<td id="C35_DQ2" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C35_DF3" align="right"></td>
					<td id="C35_DQ3" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C35_DF4" align="right"></td>
					<td id="C35_DQ4" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C35_DF5" align="right"></td>
					<td id="C35_DQ5" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
			</tbody>	
		</table>	
	</div>	
	
	<BR>
	
	<div  class="ui-widget-content"  style="width:100%;background:#ffffff;align:center">
		<div class="ui-state-highlight" >
			<%=SBL_BID_OFFER_TRANSACTION%> <%=SBL_BID_OFFER_TRANSACTION_NOTICE_1%>(<%=CURRENT_TIME%>:<label  id="C1_TIME" align="right" ></label>)
		</div>
		<table  class="one-column-emphasis" style="width:100%">
			<thead>
				<th align="right"><%=SBL_ACC_TRADE_DAY_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_LENDING_VOLUME%></th>
				<th align="right"><%=SBL_QUEUED_BORROWING_VOLUME%></th>
				<th align="right"><%=SBL_MATCHED_VOLUME%></th>
				<th align="right"><%=SBL_MATCHED_RATE%></th>
			</thead>
			<tbody>
				<tr>
					<td id="C1_0" align="right"></td>
					<td id="C1_1" align="right"></td>
					<td id="C1_2" align="right"></td>
					<td id="C1_3" align="right"></td>
					<td id="C1_4" align="right"></td>
				</tr>
			</tbody>	
		</table>
		<BR>	
		<div class="ui-state-highlight" >
			<%=SBL_FIVE_BEST_BIDS_AND_ASKS%>
		</div>
		<table  class="one-column-emphasis" style="width:100%">
			<thead>
				<th align="right"><%=SBL_BORROWING_RATE%></th>
				<th align="right"><%=SBL_BORROWING_VOLUME%></th>
				<th align="right"><%=SBL_LENDING_RATE%></th>
				<th align="right"><%=SBL_LENDING_VOLUME%></th>
			</thead>
			<tbody>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C15_CF5" align="right"></td>
					<td id="C15_CQ5" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C15_CF4" align="right"></td>
					<td id="C15_CQ4" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C15_CF3" align="right"></td>
					<td id="C15_CQ3" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C15_CF2" align="right"></td>
					<td id="C15_CQ2" align="right"></td>
				</tr>
				<tr>
					<td id=""></td>
					<td id=""></td>
					<td id="C15_CF1" align="right"></td>
					<td id="C15_CQ1" align="right"></td>
				</tr>
				<tr>
					<td id="C15_DF1" align="right"></td>
					<td id="C15_DQ1" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C15_DF2" align="right"></td>
					<td id="C15_DQ2" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C15_DF3" align="right"></td>
					<td id="C15_DQ3" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C15_DF4" align="right"></td>
					<td id="C15_DQ4" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
				<tr>
					<td id="C15_DF5" align="right"></td>
					<td id="C15_DQ5" align="right"></td>
					<td id=""></td>
					<td id=""></td>
				</tr>
			</tbody>	
		</table>	
	</div>
	
	<BR>
	<div  class="ui-widget-content"  style="width:100%;background:#ffffff;align:center">	
		<div class="ui-state-highlight" >
			<%=SBL_CUSTOMIZED_TRANSACTION%>(<%=CURRENT_TIME%>:<label  id="N_TIME" align="right" ></label>)
		</div>
		<table class="one-column-emphasis"  width="200" style="width:100%" >
			<thead>
				<th  align="right"><%=SBL_ACC_TRADE_DAY_VOLUME%></th>
				<th width="70%"></th>
			</thead>
			<tbody>
				<tr>
					<td id="N_qty" align="right"></td>
					<td></td>
				</tr>
			</tbody>
		</table>	
	</div>
</div>
<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
