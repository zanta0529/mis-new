<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="com.ecloudlife.util.Utility"%>

<%@ include file="view/headerView.jsp"%>
<%@ include file="view/menuView.jsp"%>

<%
    String ex = purifyString(Utility.getHttpParameter(request, "ex"), "tse", 5);
    String ind = "B0";
%>

<div id="content">
    <p align="center">
        <label id="indName"><%=ALL_ETF_TITLE %><br /> <%
    if (ex.equals("otc")) {
        out.println("<a href='etf_nav.jsp?ex=tse'>" + ALL_ETF_TITLE_MK1 + "</a>&nbsp;|&nbsp;" + ALL_ETF_TITLE_MK2);
    } else {
        out.println(ALL_ETF_TITLE_MK1 + "&nbsp;|&nbsp;<a href='etf_nav.jsp?ex=otc'>" + ALL_ETF_TITLE_MK2 + "</a>");
    }
	%> </label>
    </p>

    <div class="ui-widget-content" style="background: #d3d3d3; align: center;">
        <%
    		if (ex.equals("tse")) {
    		    out.println(ALL_ETF_NOTE);
    		} else {
    		    out.println(ALL_ETF_NOTE_O);
    		}
    	%>
    </div>

    <article>
        <div id="navbar">
            <ul class="doc-nav">
                <li><a href="#anchor1"><%=ALL_ETF_TYPE1 %>-<%=NEW_TAIWAN_DOLLAR %></a></li>
                <li><a href="#anchor2"><%=ALL_ETF_TYPE2 %>-<%=NEW_TAIWAN_DOLLAR %></a></li>
                <li><a href="#anchor3"><%=ALL_ETF_TYPE3 %>-<%=NEW_TAIWAN_DOLLAR %></a></li>
                <li><a href="#anchor4"><%=ALL_ETF_TYPE4 %>-<%=NEW_TAIWAN_DOLLAR %></a></li>
            </ul>
        </div>
        <div id="navbar2">
            <ul class="doc-nav2">
                <li><a href="#anchor2K"><%=ALL_ETF_TYPE2 %>-<%=FOREIGN_CURRENCY %></a></li>
                <li><a href="#anchor3K"><%=ALL_ETF_TYPE3 %>-<%=FOREIGN_CURRENCY %></a></li>
                <li><a href="#anchor4K"><%=ALL_ETF_TYPE4 %>-<%=FOREIGN_CURRENCY %></a></li>
            </ul>
        </div>
    </article>

    <div style="float: right" id="userDelayView">
        <%=UPDATE_TIME0%>
        <label id="userDelay0"></label>
        <%=UPDATE_TIME1%>&nbsp;&nbsp;<%=UNIT_STR%>
    </div>

    <%-- ETF Type 1 --%>
    <span id="INDICES"></span>
    <h2 id="anchor1"><%=ALL_ETF_TYPE1 %>-<%=NEW_TAIWAN_DOLLAR %></h2>
    <table id="group1" class="newspaper-a">
        <thead>
            <tr align="center">
                <th><%=ALL_ETF_ID %></th>
                <th><%=ALL_ETF_ISSUED %><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETF_MEMO1_STR %>"><%=ALL_ETF_MEMO1%></a></th>
                <th><%=ALL_ETF_DIFF %></th>
                <th><%=ALL_ETF_LAST_PRICE %></th>
                <th><%=ALL_ETF_NAV %><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETF_MEMO2_STR %>"><%=ALL_ETF_MEMO2%></a></th>
                <th><%=ALL_ETF_NAV_DIFF %><br /><a class='emphasize' href="#memo3" title="<%=ALL_ETF_MEMO3_STR %>"><%=ALL_ETF_MEMO3%></a></th>
                <th><%=ALL_ETF_LAST_NAV %><br /><a class='emphasize' href="#memo4" title="<%=ALL_ETF_MEMO4_STR %>"><%=ALL_ETF_MEMO4%></a></th>
                <th><%=ALL_ETF_LINK %></th>
                <th><%=ALL_ETF_TIME %></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <%-- ETF Type 2 --%>
    <br />
    <h2 id="anchor2"><%=ALL_ETF_TYPE2 %>-<%=NEW_TAIWAN_DOLLAR %></h2>
    <table id="group2" class="newspaper-a">
        <thead>
            <tr align="center">
                <th><%=ALL_ETF_ID %></th>
                <th><%=ALL_ETF_ISSUED %><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETF_MEMO1_STR %>"><%=ALL_ETF_MEMO1%></a></th>
                <th><%=ALL_ETF_DIFF %></th>
                <th><%=ALL_ETF_LAST_PRICE %></th>
                <th><%=ALL_ETF_NAV %><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETF_MEMO2_STR %>"><%=ALL_ETF_MEMO2%></a></th>
                <th><%=ALL_ETF_NAV_DIFF %><br /><a class='emphasize' href="#memo3" title="<%=ALL_ETF_MEMO3_STR %>"><%=ALL_ETF_MEMO3%></a></th>
                <th><%=ALL_ETF_LAST_NAV %><br /><a class='emphasize' href="#memo4" title="<%=ALL_ETF_MEMO4_STR %>"><%=ALL_ETF_MEMO4%></a></th>
                <th><%=ALL_ETF_LINK %></th>
                <th><%=ALL_ETF_TIME %></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
    <br />
    <h2 id="anchor2K"><%=ALL_ETF_TYPE2 %>-<span class="emphasize"><%=FOREIGN_CURRENCY %></span></h2>
    <table id="group2K" class="newspaper-a">
        <thead>
            <tr align="center">
                <th><%=ALL_ETF_ID %></th>
                <th><%=ALL_ETF_ISSUED %><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETF_MEMO1_STR %>"><%=ALL_ETF_MEMO1%></a></th>
                <th><%=ALL_ETF_DIFF %></th>
                <th><%=ALL_ETF_LAST_PRICE %></th>
                <th><%=ALL_ETF_NAV %><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETF_MEMO2_STR %>"><%=ALL_ETF_MEMO2%></a></th>
                <th><%=ALL_ETF_NAV_DIFF %><br /><a class='emphasize' href="#memo3" title="<%=ALL_ETF_MEMO3_STR %>"><%=ALL_ETF_MEMO3%></a></th>
                <th><%=ALL_ETF_LAST_NAV %><br /><a class='emphasize' href="#memo4" title="<%=ALL_ETF_MEMO4_STR %>"><%=ALL_ETF_MEMO4%></a></th>
                <th><%=ALL_ETF_LINK %></th>
                <th><%=ALL_ETF_TIME %></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <%-- ETF Type 3 --%>
    <br />
    <h2 id="anchor3"><%=ALL_ETF_TYPE3 %>-<%=NEW_TAIWAN_DOLLAR %></h2>
    <table id="group3" class="newspaper-a">
        <thead>
            <tr align="center">
                <th><%=ALL_ETF_ID %></th>
                <th><%=ALL_ETF_ISSUED %><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETF_MEMO1_STR %>"><%=ALL_ETF_MEMO1%></a></th>
                <th><%=ALL_ETF_DIFF %></th>
                <th><%=ALL_ETF_LAST_PRICE %></th>
                <th><%=ALL_ETF_NAV %><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETF_MEMO2_STR %>"><%=ALL_ETF_MEMO2%></a></th>
                <th><%=ALL_ETF_NAV_DIFF %><br /><a class='emphasize' href="#memo3" title="<%=ALL_ETF_MEMO3_STR %>"><%=ALL_ETF_MEMO3%></a></th>
                <th><%=ALL_ETF_LAST_NAV %><br /><a class='emphasize' href="#memo4" title="<%=ALL_ETF_MEMO4_STR %>"><%=ALL_ETF_MEMO4%></a></th>
                <th><%=ALL_ETF_LINK %></th>
                <th><%=ALL_ETF_TIME %></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
    <br />
    <h2 id="anchor3K"><%=ALL_ETF_TYPE3 %>-<span class="emphasize"><%=FOREIGN_CURRENCY %></span></h2>
    <table id="group3K" class="newspaper-a">
        <thead>
            <tr align="center">
                <th><%=ALL_ETF_ID %></th>
                <th><%=ALL_ETF_ISSUED %><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETF_MEMO1_STR %>"><%=ALL_ETF_MEMO1%></a></th>
                <th><%=ALL_ETF_DIFF %></th>
                <th><%=ALL_ETF_LAST_PRICE %></th>
                <th><%=ALL_ETF_NAV %><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETF_MEMO2_STR %>"><%=ALL_ETF_MEMO2%></a></th>
                <th><%=ALL_ETF_NAV_DIFF %><br /><a class='emphasize' href="#memo3" title="<%=ALL_ETF_MEMO3_STR %>"><%=ALL_ETF_MEMO3%></a></th>
                <th><%=ALL_ETF_LAST_NAV %><br /><a class='emphasize' href="#memo4" title="<%=ALL_ETF_MEMO4_STR %>"><%=ALL_ETF_MEMO4%></a></th>
                <th><%=ALL_ETF_LINK %></th>
                <th><%=ALL_ETF_TIME %></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <%-- ETF Type 4 --%>
    <br />
    <h2 id="anchor4"><%=ALL_ETF_TYPE4 %>-<%=NEW_TAIWAN_DOLLAR %></h2>
    <table id="group4" class="newspaper-a">
        <thead>
            <tr align="center">
                <th><%=ALL_ETF_ID %></th>
                <th><%=ALL_ETF_ISSUED %><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETF_MEMO1_STR %>"><%=ALL_ETF_MEMO1%></a></th>
                <th><%=ALL_ETF_DIFF %></th>
                <th><%=ALL_ETF_LAST_PRICE %></th>
                <th><%=ALL_ETF_NAV %><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETF_MEMO2_STR %>"><%=ALL_ETF_MEMO2%></a></th>
                <th><%=ALL_ETF_NAV_DIFF %><br /><a class='emphasize' href="#memo3" title="<%=ALL_ETF_MEMO3_STR %>"><%=ALL_ETF_MEMO3%></a></th>
                <th><%=ALL_ETF_LAST_NAV %><br /><a class='emphasize' href="#memo4" title="<%=ALL_ETF_MEMO4_STR %>"><%=ALL_ETF_MEMO4%></a></th>
                <th><%=ALL_ETF_LINK %></th>
                <th><%=ALL_ETF_TIME %></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>
    <br />
    <h2 id="anchor4K"><%=ALL_ETF_TYPE4 %>-<span class="emphasize"><%=FOREIGN_CURRENCY %></span></h2>
    <table id="group4K" class="newspaper-a">
        <thead>
            <tr align="center">
                <th><%=ALL_ETF_ID %></th>
                <th><%=ALL_ETF_ISSUED %><br /><a class='emphasize' href="#memo1" title="<%=ALL_ETF_MEMO1_STR %>"><%=ALL_ETF_MEMO1%></a></th>
                <th><%=ALL_ETF_DIFF %></th>
                <th><%=ALL_ETF_LAST_PRICE %></th>
                <th><%=ALL_ETF_NAV %><br /><a class='emphasize' href="#memo2" title="<%=ALL_ETF_MEMO2_STR %>"><%=ALL_ETF_MEMO2%></a></th>
                <th><%=ALL_ETF_NAV_DIFF %><br /><a class='emphasize' href="#memo3" title="<%=ALL_ETF_MEMO3_STR %>"><%=ALL_ETF_MEMO3%></a></th>
                <th><%=ALL_ETF_LAST_NAV %><br /><a class='emphasize' href="#memo4" title="<%=ALL_ETF_MEMO4_STR %>"><%=ALL_ETF_MEMO4%></a></th>
                <th><%=ALL_ETF_LINK %></th>
                <th><%=ALL_ETF_TIME %></th>
            </tr>
        </thead>
        <tbody>
        </tbody>
    </table>

    <table id="groupM4">
        <tr>
            <td align="center" valign="top" nowrap width="0" id="memo1"><%=ALL_ETF_MEMO1_H %></td>
            <td align="left"><%=ALL_ETF_MEMO1_STR %></td>
        </tr>
        <tr>
            <td align="center" valign="top" nowrap width="0" id="memo2"><%=ALL_ETF_MEMO2_H %></td>
            <td align="left"><%=ALL_ETF_MEMO2_STR %></td>
        </tr>
        <tr>
            <td align="center" valign="top" nowrap width="0" id="memo3"><%=ALL_ETF_MEMO3_H %></td>
            <td align="left"><%=ALL_ETF_MEMO3_STR %></td>
        </tr>
        <tr>
            <td align="center" valign="top" nowrap width="0" id="memo4"><%=ALL_ETF_MEMO4_H %></td>
            <td align="left"><%=ALL_ETF_MEMO4_STR %></td>
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

    loadPageChangeFunction();

	ex = "<%=ex%>";
	ind = "<%=ind%>";
    loadStockList();
</script>

<%@ include file="view/sideView.jsp"%>
<%@ include file="view/footerView.jsp"%>
