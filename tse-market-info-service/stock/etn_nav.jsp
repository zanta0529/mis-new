<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="com.ecloudlife.util.Utility"%>

<%@ include file="view/headerView.jsp"%>
<%@ include file="view/menuView.jsp"%>

<%
    String ex = purifyString(Utility.getHttpParameter(request, "ex"), "tse", 5);
    String ind="B1";
%>

<div id="content">
    <p align="center">
        <label id="indName"> <%
	    out.println(ALL_ETN_TITLE+"<br>");
		if (ex.equals("otc")){
		    out.println("<a href='etn_nav.jsp?ex=tse'>" + ALL_ETN_TITLE_MK1 + "</a>&nbsp;|&nbsp;" + ALL_ETN_TITLE_MK2);
		} else {
		    out.println(ALL_ETN_TITLE_MK1 + "&nbsp;|&nbsp;<a href='etn_nav.jsp?ex=otc'>" + ALL_ETN_TITLE_MK2 + "</a>");
		}
	%>
        </label>
    </p>

    <div class="ui-widget-content" style="background: #d3d3d3; align: center;">
        <%
    		if (ex.equals("tse")) {
    		    out.println(ALL_ETN_NOTE);
    		} else {
    		    out.println(ALL_ETN_NOTE_O);
    		}
    	%>
    </div>

    <article>
        <div id="navbar">
            <ul class="doc-nav">
                <li><a href="#anchor1"><%=ALL_ETN_TYPE1 %></a></li>
                <li><a href="#anchor2"><%=ALL_ETN_TYPE2 %></a></li>
                <li><a href="#anchor3"><%=ALL_ETN_TYPE3 %></a></li>
                <li><a href="#anchor4"><%=ALL_ETN_TYPE4 %></a></li>
            </ul>
        </div>
    </article>

    <div style="float: right" id="userDelayView">
        <%=UPDATE_TIME0%>
        <label id="userDelay0"></label>
        <%=UPDATE_TIME1%>&nbsp;&nbsp;<%=UNIT_STR%>
    </div>


    <%-- ETN Type 1 --%>

    <span id="INDICES"></span>

    <h2 id="anchor1"><%=ALL_ETN_TYPE1%></h2>

    <table id="group1" class="newspaper-a">
        <thead>
            <tr>
                <!-- <th align="center"><%=ALL_ETN_ID%> <br> (<%=ALL_ETN_FB%>/<%=ALL_ETN_URL%>) </th> -->
                <th align="center"><%=ALL_ETN_ID%></th>
                <th align="center"><%=ALL_ETN_ISSUED%></th>
                <th align="center"><%=ALL_ETN_DIFF%></th>
                <th align="center"><%=ALL_ETN_LAST_PRICE%></th>
                <th align="center"><%=ALL_ETN_NAV%><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETN_MEMO1_STR %>"><%=ALL_ETN_MEMO1%></a></th>
                <th align="center"><%=ALL_ETN_NAV_DIFF%><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETN_MEMO2_STR %>"><%=ALL_ETN_MEMO2%></a></th>
                <th align="center"><%=ALL_ETN_LAST_NAV%></th>
                <th align="center"><%=ALL_ETN_LINK%></th>
                <th align="center"><%=ALL_ETN_TIME%></th>
            </tr>

        </thead>
        <tbody>
        </tbody>
    </table>

    <%-- ETN Type 2 --%>
    <br>
    <h2 id="anchor2"><%=ALL_ETN_TYPE2%></h2>
    <table id="group2" class="newspaper-a">
        <thead>
            <tr>
                <th align="center"><%=ALL_ETN_ID%></th>
                <th align="center"><%=ALL_ETN_ISSUED%></th>
                <th align="center"><%=ALL_ETN_DIFF%></th>
                <th align="center"><%=ALL_ETN_LAST_PRICE%></th>
                <th align="center"><%=ALL_ETN_NAV%><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETN_MEMO1_STR %>"><%=ALL_ETN_MEMO1%></a></th>
                <th align="center"><%=ALL_ETN_NAV_DIFF%><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETN_MEMO2_STR %>"><%=ALL_ETN_MEMO2%></a></th>
                <th align="center"><%=ALL_ETN_LAST_NAV%></th>
                <th align="center"><%=ALL_ETN_LINK%></th>
                <th align="center"><%=ALL_ETN_TIME%></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>


    <%-- ETN Type 3 --%>
    <br>
    <h2 id="anchor3"><%=ALL_ETN_TYPE3%></h2>
    <table id="group3" class="newspaper-a">
        <thead>
            <tr>
                <th align="center"><%=ALL_ETN_ID%></th>
                <th align="center"><%=ALL_ETN_ISSUED%></th>
                <th align="center"><%=ALL_ETN_DIFF%></th>
                <th align="center"><%=ALL_ETN_LAST_PRICE%></th>
                <th align="center"><%=ALL_ETN_NAV%><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETN_MEMO1_STR %>"><%=ALL_ETN_MEMO1%></a></th>
                <th align="center"><%=ALL_ETN_NAV_DIFF%><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETN_MEMO2_STR %>"><%=ALL_ETN_MEMO2%></a></th>
                <th align="center"><%=ALL_ETN_LAST_NAV%></th>
                <th align="center"><%=ALL_ETN_LINK%></th>
                <th align="center"><%=ALL_ETN_TIME%></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <%-- ETN Type 4 --%>
    <br>
    <h2 id="anchor4"><%=ALL_ETN_TYPE4%></h2>
    <table id="group4" class="newspaper-a">
        <thead>
            <tr>
                <th align="center"><%=ALL_ETN_ID%></th>
                <th align="center"><%=ALL_ETN_ISSUED%></th>
                <th align="center"><%=ALL_ETN_DIFF%></th>

                <th align="center"><%=ALL_ETN_LAST_PRICE%></th>
                <th align="center"><%=ALL_ETN_NAV%><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETN_MEMO1_STR %>"><%=ALL_ETN_MEMO1%></a></th>

                <th align="center"><%=ALL_ETN_NAV_DIFF%><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETN_MEMO2_STR %>"><%=ALL_ETN_MEMO2%></a></th>
                <th align="center"><%=ALL_ETN_LAST_NAV%></th>
                <th align="center"><%=ALL_ETN_LINK%></th>
                <th align="center"><%=ALL_ETN_TIME%></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <table id="groupM4">
        <tr>
            <td align="center" valign="top" nowrap width="0" id="memo1"><%=ALL_ETN_MEMO1_H%></td>
            <td align="left"><%=ALL_ETN_MEMO1_STR%></td>
        </tr>
        <tr>
            <td align="center" valign="top" nowrap width="0" id="memo2"><%=ALL_ETN_MEMO2_H%></td>
            <td align="left"><%=ALL_ETN_MEMO2_STR%></td>
        </tr>
    </table>
</div>

<a href="#" id="scroll-to-top" class="active">↑</a>

<script type="text/javascript">
    $("#popupDialog").dialog({
        autoOpen: true,
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
    
	// loadPageChangeFunction();

	ex="<%=ex%>";
	ind="<%=ind%>";

    loadStockList();
</script>

<%@ include file="view/sideView.jsp"%>
<%@ include file="view/footerView.jsp"%>
