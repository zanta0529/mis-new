<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp" %>

<%@ page import="com.ecloudlife.util.Utility" %>

<%@ include file="view/headerView.jsp" %>
<%@ include file="view/menuView.jsp" %>
<%
    String ex       = purifyString(Utility.getHttpParameter(request,"ex"),"tse",5);
    String ind      = purifyString(Utility.getHttpParameter(request,"ind"),"01",5);
    String currPage = purifyString(Utility.getHttpParameter(request,"currPage"),"0",5);
    String type     = purifyString(Utility.getHttpParameter(request,"type"),"all",20);
%>
<script>
	$(document).ready(function() 
	{ 
		if ($.browser.msie) 
		{
    	add_placeholder('txtStockId', '<%=SIDEVIEW_INPUTCODE%>');
   	}
  });	
</script>

<div id="content">    
  <div style="float:right" id="userDelayView"> 
    <%=UPDATE_TIME0%> <label id="userDelay0" ></label> <%=UPDATE_TIME1%>
  </div>

      <form id="formOdd">    
        <select id="tseSelect"><option value=''><%=GROUP_TWSE_SELECT%></option></select>
        <select id="otcSelect"><option value=''><%=GROUP_GTSM_SELECT%></option></select>&nbsp;&nbsp;    
        <span id="txtStockId_bar" style='position: relative;left:10px;top:20px'>&nbsp;</span>
        <input type="text" id="txtStockId" name="txtStockId" placeholder="<%=SIDEVIEW_INPUTCODE%>"  required>        
        <input type="submit" value="<%=SIDEVIEW_SEARCH_ODD%>" id="getStock"/>
      </form>

  <BR><BR>
  <div class="groupDiv">
    <label id="marketName"></label> <label id="indName"></label> <label id="subtitle"><%=ODDTRADE_SUBTITLE%></label>
    <label id="subtitle" style="float:right;"><%=ODDTRADE_UNIT%></label>
    <BR>
    <table class="newspaper-a" id="group">
      <thead>
        <th></th>
	    <th><%=OPEN_PRICE%></th>
	    <th><%=HIGHEST_PRICE%></th>
	    <th><%=LOWEST_PRICE%></th>
	    <th><%=LATEST_PRICE%></th>
	    <th><%=U_PRICE%></th>
	    <th><%=W_PRICE%></th>
	    <th><%=ODDTRADE_TIME%></th>
	    <th><%=ODDTRADE_BEST_BID_PRICE%></th>
	    <th><%=ODDTRADE_BEST_ASK_PRICE%></th>
	    <th><%=ODDTRADE_TRADE_PRICE%></th>
	    <th><%=ODDTRADE_TRADE_VOLUME%></th>
	  </thead>
	  <tbody></tbody>
    </table>
  </div>
  <div id="Pagination" class="pagination"></div>
  <div style="float:right">
    <%=GROUP_NO_PRE_PAGE%>
    <select id="prePage">
      <option value="0">10</option>
	  <option value="1">20</option>
	  <option value="2">50</option>
	  <option value="3">100</option>
    </select> |
    <%=GROUP_TOTAL_RECORDS%><label id="totlaRecords"></label>
  </div>
  <BR><BR><BR>
  <div class="ui-widget-content group-footer" style="background:#d3d3d3;align:center;">
    <%=ODDTRADE_NOTE%>		
  </div>
</div>
<script>
	ex="<%=ex%>";
	ind="<%=ind%>";
	currPage="<%=currPage%>";
	loadGroupList();
</script>
<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
