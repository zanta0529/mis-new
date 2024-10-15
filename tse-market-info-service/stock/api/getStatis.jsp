<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="com.ecloudlife.util.Utility"%>
<%@ page import="com.ecloudlife.util.UserSession"%>
<%@ page import="com.proco.datautil.*"%>
<%@ page import="com.ecloudlife.cass.logicutil.*"%>
<%@ page import="org.json.*"%>
<%@ page import="java.util.*"%>

<%@ include file="../../common-utils.jsp" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String ex = Utility.getHttpParameter(request, "ex"); //request.getParameter("ex");
String date = Utility.getHttpParameter(request, "d"); //request.getParameter("d");
String delay = Utility.getHttpParameter(request, "delay"); //request.getParameter("delay");

String timekey = Utility.getHttpParameter(request, "_");
if ((timekey == null) || (Math.abs(System.currentTimeMillis() - Long.parseLong(timekey)) > 90000000)) {
    JSONObject j = new JSONObject();
    j.put("rtcode", "9999");
    j.put("rtmessage", "發生錯誤，請重新整理網頁。");
    out.print(j.toString());
    return;
}

if (delay == null) {
    delay = "0";
}
if (ex == null) {
    JSONObject j = new JSONObject();
    j.put("rtcode", "9999");
    j.put("rtmessage", "參數不足");
    out.print(j.toString());
    return;
}

if (date == null) {
    date = ExLastTradeDate.getLastTradeDate(null, ex);
}

if (date.equals("")) {
    JSONObject j = new JSONObject();
    j.put("rtcode", "9999");
    j.put("rtmessage", "參數不足");
    out.print(j.toString());
    return;
}

String key = ex + "_" + date;
/*
UserSession us = (UserSession) session.getAttribute("UserSession");
if (us == null) {
    us = new UserSession(session.getId());
}
*/
long qt = System.nanoTime();
//long startTime = us.getLatestMillis(key);
long user_delay = StockInfoManager.getUserDelayMillis("t");
if (user_delay < 5000) {
    user_delay = 5000;
}

List<Map.Entry<String,String>> cols = StockStatisManager.getStatisListNow(key);

JSONObject j1 = new JSONObject();
j1.put("key", key);
for (int j = 0; j < cols.size(); j++) {
	Map.Entry<String,String> hc = cols.get(j);
    String val = hc.getValue().toString();
    j1.put(hc.getKey().toString(), val);
}

qt = (System.nanoTime() - qt) / 1000;

//session.setAttribute("UserSession", us);

JSONObject jqueryTime = new JSONObject();
jqueryTime.put("stockStatis", StockStatisManager.getQueryMicroTime());
jqueryTime.put("stockStatisItem", StockStatisManager.getProductSize());
jqueryTime.put("sessionKey", key);
jqueryTime.put("stockStatis", qt);

JSONObject j = new JSONObject();
j.put("rtcode", "0000");
j.put("rtmessage", "OK");
j.put("detail", j1);
j.put("queryTime", jqueryTime);
j.put("userDelay", user_delay);

out.print(j.toString());
/*
us.setCount(key);
session.setAttribute("UserSession", us);
*/
if (true) {
    return;
}
%>
