<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ include file="view/headerView.jsp" %>
<%@ include file="view/menuView.jsp" %>
<div id="content" >
<center><h2><%=MENU_SBL_AVAILABLE_VOLUME%></h2></center>
<form><%=SBL_INPUTBROKER %><input id="sblBrokerNo" />   <%=SBL_INPUTSTOCK%><input id="sblStockNo" /></form>
<%=SBL_MULTIPLE_STOCK%>
<BR>
<table id="sblBrkTable" class="one-column-emphasis"  style="width:100%;">
		<thead>
			<th></th>
			<th><%=SBL_LENDING_FROM%></th>
			<th><%=SBL_STOCK_CODE%></th>
			<th><%=SBL_STOCK_NAME%></th>
			<th><%=SBL_AVAILABLE_%></th>
			<th><%=SBL_SBL_RATE%></th>
			<th><%=SBL_FEE_RATE%></th>
			<th><%=SBL_COMMENTS%></th>
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
	</select>｜
	<%=GROUP_TOTAL_RECORDS%><label id="totlaRecords"></label>
</div>
</div>
<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
