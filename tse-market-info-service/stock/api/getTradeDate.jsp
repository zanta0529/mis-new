<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %>

<%@ include file="../../common-utils.jsp" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String ex = purifyString(Utility.getHttpParameter(request,"ex"),null,10);
String is = purifyString(Utility.getHttpParameter(request,"i"),null,10);
String ch = purifyString(Utility.getHttpParameter(request,"ch"),null,10);
String date = purifyString(Utility.getHttpParameter(request,"d"),null,10);
String name = purifyString(Utility.getHttpParameter(request,"n"),null,10);
String size = purifyString(Utility.getHttpParameter(request,"size"),"1",10);

if(size == null) size = "1";

if(ex!=null && ch==null){
   if(ex.equals("otc")) ch = "o00.tw";
   if(ex.equals("tse")) ch = "t00.tw";
} else if(ex==null && ch==null){
   ch = "t00.tw";
   ex = "tse";
}


java.util.Hashtable<String,String> aa = new Hashtable<String,String>();
if(ex!=null)
aa.put("ex",ex);
if(is!=null)
aa.put("i",is);
if(ch!=null)
aa.put("ch",ch);
if(date!=null)
aa.put("d",date);
if(name!=null)
aa.put("n",name);

int limit = Utility.parseInt(size);
StockDetail sd = new StockDetail("");
JSONArray ja = new JSONArray();

date = ExLastTradeDate.getLastTradeDate(null,ex);
JSONObject j1 = new JSONObject();
j1.put("ex",ex);
j1.put("d",date);
ja.put(j1);


/*
List<Row<String,String,String>> rList = sd.getIndex(ks,aa,null,null,null,null);

for(int i = rList.size()-1 ; (i >= 0) && limit!=0  ; i--,limit--){
   JSONObject j1 = new JSONObject();
   Row<String,String,String> row = rList.get(i);
   j1.put("ex",row.getColumnSlice().getColumnByName("ex").getValue().toString());
   j1.put("d",row.getColumnSlice().getColumnByName("d").getValue().toString());
  ja.put(j1);
}
*/
   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("stockDetail",sd.getQueryTime());
   jqueryTime.put("totalMicroTime",sd.getQueryTime());

   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("msgArray", ja);
   j.put("queryTime", jqueryTime);
   out.print(j.toString());
   if(true) return;
%>
