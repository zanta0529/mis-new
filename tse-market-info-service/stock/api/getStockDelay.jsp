<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.ecloudlife.util.UserSession" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="java.text.SimpleDateFormat" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
 
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("ex");
String ex = Utility.getHttpParameter(request,"ex"); // request.getParameter("ex");
String ip = Utility.getHttpParameter(request,"ip"); // request.getParameter("ex");
 
if(ex==null ||ip==null ){
   JSONObject j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","參數不足");
   out.print(j.toString());
   return;
}

String ex_key = "sd_"+ex+"_"+ip+"."+date;
Long ct0 = StockInfoManager.resCacheTime.get(ex_key);
if(ct0!=null){
  if((System.currentTimeMillis()-ct0.longValue())<15000){
    JSONObject j1 = StockInfoManager.resCacheObject.get(ex_key);
    if(j1!=null){
      j1.put("cachedAlive",(System.currentTimeMillis()-ct0.longValue()));
      j1.put("exKey",ex_key);
      out.print(j1.toString());
      return;
    }
  } else {
    StockInfoManager.resCacheTime.remove(ex_key);
    StockInfoManager.resCacheObject.remove(ex_key);
  }
}
 
if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);
String key = date+"_"+ex+"_"+ip;
 
StockDelay sa = new StockDelay(ex,date,key);
List<Map.Entry<String, String>> cols = sa.get();
org.json.JSONObject js = new org.json.JSONObject();
for(Map.Entry<String, String> col : cols){
//if (!(col.getName().equals("otc_5490.tw_20150630") &&  col.getValue().equals("5")))
js.put(col.getKey(),col.getValue());
}
js.remove("key"); 
 
 
int nowTime = Integer.parseInt(new SimpleDateFormat("HHmm").format(Calendar.getInstance().getTime()));
//nowTime = 1330;
String userDelay="999999";
if ( (nowTime >= 857  &  nowTime <= 900 ) || ( nowTime >= 1328  &&  nowTime <= 1330 ))
    userDelay = String.valueOf(StockInfoManager.getUserDelayMillis("od"));

   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
//   j.put("nowTime",nowTime);
   j.put("userDelay",userDelay);
   j.put("result", js);
   out.print(j.toString());
   if(!js.isEmpty()){
     StockInfoManager.resCacheTime.put(ex_key, new Long(System.currentTimeMillis()));
     StockInfoManager.resCacheObject.put(ex_key,j);
   }   
   if(true) return;
 
%>
