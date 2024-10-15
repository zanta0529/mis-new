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

<div id="content">

<% if(ind!=null && (!ind.equals("TIDX") && !ind.equals("OIDX"))){ %>
    <div id="CNT_TRADE_NOTE" style="text-align: center;color: red;font-size: 16px;margin-bottom: 8px"><%=CNT_TRADE_NOTE%></div>
<% } %>

<div style="float:right" id="userDelayView"> 
	<%=UPDATE_TIME0%> <label id="userDelay0" ></label> <%=UPDATE_TIME1%>
</div>
<BR>
<div class="groupDiv">
	<%
	if(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX")))
	{
		String subTitle="";
		if(ind.equals("TIDX"))
		{
			subTitle=MARKET_TSE_INDEX_TITLE;	
		}
		
		if(ind.equals("OIDX"))
		{
			subTitle=MARKET_OTC_INDEX_TITLE;	
		}
		
	%>
		<center>
			<label id="marketIndexName"><%=subTitle%></label>
			<BR>
			[<a href="#INDICES"><%=MARKET_INDEX_INDICES%></a>][<a href="#STATISTICS"><%=MARKET_INDEX_STATISTICS%></a>]
		</center>
	<%}
	%>
	<span id="INDICES"></span>
	<label id="marketName"></label> <label id="indName">
	<%
		if(ind!=null)
		{	
			if(ind.equals("B0"))
			{
				out.println(MENU_ETF);	
			}
		    else if(ind.equals("B1"))
			{
				out.println(MENU_ETN);	
			}

		}
		if(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX"))  )
		{ 
			out.println(MARKET_INDEX_INDICES);
		}
	%>
		
		</label>
		<%if(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX"))  )
		{
		%>
		<label id="marketIndexTime"></label><label id="marketIndexStr">(<%=GROUP_TIMESTR%></label>
		<%}else{%>
		<label class="title" style="float:right;"><%=UNIT_STR%></label>
			<%}%>
	<BR>
	<table id="group" class="newspaper-a" >
		<thead>
            <tr>
			<th></th>
			<%if(type!=null && type.equals("postponed")) { %>
			<th></th>
			<%}%>
			<th><%=LATEST_PRICE%></th>
			<th><%=PRICE_CHANGE%></th>
            			
			<%if(!(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX")  ))){%>
			<th align="center"><%=TRADE_VOLUME%></th>
			<th align="center"><%=ACC_TRADE_VOLUME%></th>
            <th align="center"><%=REFERENCE_PRICE%></th>
            <th align="center"><%=REFERENCE_VOLUME%></th>
<!--            
			<th align="center"><%=BID_PRICE%></th>
			<th align="center"><%=BID_VOLUME%></th>
			<th align="center"><%=ASK_PRICE%></th>
			<th align="center"><%=ASK_VOLUME%></th>
-->
			<%}%>

			<%if(!(ind!=null && (ind.equals("TIDX") || ind.equals("OIDX")  ))){%>
			<th><%=CURRENT_TIME%></th>
			
			<%}%>
			<%=(ind!=null && ind.equals("B0"))? "<th>"+GROUP_ETF_ESTIMATED+"</th>":""%>
			<%=(ind!=null && ind.equals("B1"))? "<th>"+GROUP_ETN_ESTIMATED+"</th>":""%>
			</tr>
		</thead>
		<tbody>
		</tbody>
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
<BR>
<BR>
<BR>
		<div class="ui-widget-content" style="background:#d3d3d3;align:center;">
			<%
				if(ex!=null && ex.equals("tse") && ind.equals("B0"))
				{
					out.println(GROUP_NOTE_ETF);
				}
				else if(ex!=null && ex.equals("otc") && ind.equals("B0"))
				{
					out.println(GROUP_NOTE_OTC_ETF);
				}
				else if(ex!=null && ex.equals("tse") && ind.equals("B1"))
				{
					out.println(GROUP_NOTE_OTC_ETN);
				}
				else if(ex!=null && ex.equals("otc") && ind.equals("B1"))
				{
					out.println(GROUP_NOTE_OTC_ETN);
				}
			
			%>
		</div>
<BR>

</div>

<a href="#" id="scroll-to-top" class="active">↑</a>

<script>
	var type="<%=type%>";
	$( "#tdrDialog" ).dialog({
      autoOpen: false,
      show: {
        effect: "blind",
        duration: 300
      },
      hide: {
        effect: "explode",
        duration: 300
      }
    });
	loadPageChangeFunction();
<%if(ex==null && ind==null&& !(type!=null && type.equals("postponed")) ){%>
    currPage="<%=currPage%>";
	loadGroupList();
<%}else if((type!=null && type.equals("postponed")) ){%>	
    currPage="<%=currPage%>";
	loadPostponedStockList();
<%}else{%>
	ex="<%=ex%>";
	ind="<%=ind%>";
    currPage="<%=currPage%>";
	loadStockList();
	if(ind==("E0"))
	{
		$( "#tdrDialog" ).dialog( "open");	
	}
	if(ind=="TIDX" || ind=="OIDX")
	{
		loadSummaryMarket();
	}
<%}%>
/*
$(function() {
    $( "linkTip" ).tooltip();
  });*/
</script>
<%@ include file="view/sideView.jsp" %>
<%@ include file="view/footerView.jsp" %>
