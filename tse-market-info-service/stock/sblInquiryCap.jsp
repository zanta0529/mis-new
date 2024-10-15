<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp" %>

<%@ include file="view/headerView.jsp"%>
<%@ include file="view/menuView.jsp"%>

<div id="content">
<center><h2><%=MENU_SBL_REAL_TIME_AVAILABLE%></h2></center>
<%=SBL_INPUTSTOCK0%><input id="sblStockNo" /> <%=SBL_MULTIPLE_STOCK0%>
<BR>
<center>
	
</center>
<table id="sblCapTable" class="one-column-emphasis"  style="width:100%;">
		<thead>
			<th></th>
			<th id="stockSort"><%=SBL_STOCK_CODE%></th>
			<th width="30%" id="volSort"><%=SBL_REAL_TIME_AVAILABLE_VOL%></th>
			<th id="timeSort"><%=SBL_LAST_MODIFY%></th>
			<th width="30%"></th>
		</thead>
		<tbody>
		</tbody>	
</table>	
<div id="Pagination" class="pagination"></div>
<div style="float:right">
	<%=GROUP_NO_PRE_PAGE%>
	<select id="prePage">
		<option value="0">10</option>
		<option value="1">20</option>
		<option value="2">50</option>
		<option value="3">100</option>
	</select> |
	<%=GROUP_TOTAL_RECORDS%><label id="totlaRecords"></label>
</div>
</div>
<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
