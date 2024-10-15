<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page
import="org.json.*"%><%@ page
import="com.ecloudlife.util.*"%><%@ page
import="com.ecloudlife.cass.logicutil.*"%><%@ page
import="java.text.*"%><%

   boolean ok = true;
   if(ok){
	   JSONObject j = new JSONObject();
	   j.put("rtcode","0000");
	   j.put("rtmessage","OK");
	   j.put("showchart",com.ecloudlife.cass.logicutil.StockInfoManager.isShowChart());
	   out.print(j.toString());
   }
   if(true) return;

%>
