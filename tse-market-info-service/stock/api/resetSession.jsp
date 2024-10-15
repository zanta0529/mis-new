<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.ecloudlife.util.UserSession" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %>

<%@ include file="../../common-utils.jsp" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

//String sessionStr = "UserSession";
String sessionKey = "";
UserSession us = (UserSession)session.getAttribute("UserSession");
if(us==null){
   us = new UserSession(session.getId());
   session.setAttribute("UserSession",us);
}
UserSession us1 = (UserSession)session.getAttribute("UserSession1");
if(us1==null){
   us1 = new UserSession("_"+session.getId());
   session.setAttribute("UserSession1",us);
}
us1.resetAllLatestMillis();
us.resetAllLatestMillis();

%><%
   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   out.print(j.toString());
   if(true) return;
%>
