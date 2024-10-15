<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.ecloudlife.util.UserSession" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.text.SimpleDateFormat" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
long user_delay = 0;

String ex_ch = Utility.getHttpParameter(request,"ex_ch"); // request.getParameter("ex_ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String d1_prefix = "00000000235959";
String delay = Utility.getHttpParameter(request,"delay"); // request.getParameter("delay");
if(delay==null) delay = "0";

if(ex_ch==null){
   JSONObject j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","參數不足");
   out.print(j.toString());
   return;
}

if(date==null) date = ExLastTradeDate.getLastTradeDate(null,"tse");

String[] chs = ex_ch.split("\\|");

String sessionKey = "";
List<String> keys = new Vector<String>();
for(int i = 0 ; i < chs.length ; i++){
  String key = chs[i];
  if(key.split("_").length==2)
    key = chs[i]+"_"+date;
  keys.add(key);
  sessionKey+=(key+"|");
}


UserSession us = (UserSession)session.getAttribute("UserSession");
if(us==null){
   us = new UserSession(session.getId());
}
long startTime = us.getLatestMillis(sessionKey);
//List<StockInfo> infoList = StockInfoManager.getStockInfoList(keys,startTime,30000);
List<StockInfo> infoList = StockInfoManager.getStockInfoList(keys,sessionKey,us,30000,Utility.parseLong(delay));

JSONArray ja = new JSONArray();

long tlong2 = us.getLatestMillis(sessionKey);
for(int inx = 0 ; inx < infoList.size() ; inx++){
   StockInfo sinfo = infoList.get(inx);
   if(sinfo==null) continue;
   long tlong = sinfo.getChange();
   if(tlong2 <tlong){
      tlong2 = tlong;
   }
   List<Map.Entry<String,String>> cols = sinfo.getStockDetail();
   JSONObject j1 = new JSONObject();
   // StockDetail
   for (int j = 0; j < cols.size(); j++) {
	   Map.Entry<String,String> hc = cols.get(j);
      String val = "";
      String name = "";
      val = hc.getValue().toString();
      name = hc.getKey().toString();
      if(name.equals("it")){
         user_delay = StockInfoManager.getUserDelayMillis(val);
      }
   }
   // StockQuote
   List<Map.Entry<String,String>> schs = sinfo.getStockQuote();
   for (int i = 0; i < schs.size(); i++) {
	   Map.Entry<String,String> hc = schs.get(i);
      String val = "";
      val = hc.getValue().toString();
      j1.put(hc.getKey().toString(),val);
    }
   //StockTicker
    //StockTicker
    String tickKey0 = j1.optString("tk0");
    String tickKey1 = j1.optString("tk1");
    if(tickKey0==null) tickKey0 = "";
    if(tickKey1==null) tickKey1 = "";
    String[] tickKey = new String[]{tickKey0,tickKey1};
    String[] treads = sinfo.getTrade(tickKey);
    if(treads.length==2){
	    j1.put("t",treads[0]);
	    j1.put("tv",treads[1]);
    } else {
	    j1.put("tv","-");
    }
   ja.put(j1);
  }


   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("showChart",StockInfoManager.isShowChart());
   jqueryTime.put("stockInfo",StockInfoManager.getQueryMillisTime());
   jqueryTime.put("stockInfoItem",StockInfoManager.getProductSize());
   jqueryTime.put("sessionKey",sessionKey);
   jqueryTime.put("sessionFromTime",startTime);
   jqueryTime.put("sessionLatestTime",us.getLatestMillis(sessionKey));
   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("msgArray", ja);
   j.put("queryTime", jqueryTime);
   j.put("userDelay", user_delay);
   out.print(j.toString());
   us.setLatestMillis(sessionKey,tlong2);
   us.setCount(sessionKey);
   session.setAttribute("UserSession",us);
   if(true) return;
%>
