<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*" %><%@ page
import="java.text.SimpleDateFormat"   errorPage="/api/error.jsp" %><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String d1_prefix = "00000000235959";

String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String time = Utility.getHttpParameter(request,"t"); // request.getParameter("ch");

String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String sizeStr = Utility.getHttpParameter(request,"s"); // request.getParameter("s");
String indexStr = Utility.getHttpParameter(request,"i"); // request.getParameter("i");

int size = 20000;
int index = 0;
if(sizeStr!=null) size = Utility.parseInt(sizeStr);
if(indexStr!=null) index = Utility.parseInt(indexStr);
//String date0 = request.getParameter("d0");
//String date1 = request.getParameter("d1");

//if(date0!=null && date1==null){
//  date1 = date0;
//}
String pt = Utility.getHttpParameter(request,"pt"); // request.getParameter("pt");
if(pt==null) pt = "B"; //B=競價 O=零股 F=定價

if(date==null || time == null){
	JSONObject j = new JSONObject();
	j.put("rtcode","9999");
	j.put("rtmessage","參數不足");
	out.print(j.toString());
	return;
}

if(date==null) date = ExLastTradeDate.getLastTradeDate(null,"tse");

long tlong0 = 0;
long tlong1 = 0;


//2013053109:00:00
StockTickerTime sd0 = new StockTickerTime(date,date+time);


Vector<String> keys = new Vector<String>();
//2454.tw_tse_20130531_B

List<Map.Entry<String,String>> hcols = sd0.get();
JSONArray ja = new JSONArray();
for(Map.Entry<String,String> col : hcols){
   //keys.add(col.getName());
   //
      JSONObject j1 = new JSONObject(col.getValue());
          //StockTicker
    String tickKey0 = j1.optString("tk0");
    String tickKey1 = j1.optString("tk1");
    if(tickKey0==null) tickKey0 = "";
    if(tickKey1==null) tickKey1 = "";
    String[] tickKey = new String[]{tickKey0,tickKey1};
    String[] treads = StockInfo.getTrade(tickKey);
    if(treads.length==2){
	    //j1.put("t",treads[0]);
	    j1.put("tv",treads[1]);
    } else {
	    j1.put("tv","-");
    }
  //System.out.println("==="+col.getName());
  ja.put(j1);
}
/*
StockTickerInfo sq = new StockTickerInfo("");
java.util.List<SuperRow<String, String, String, String>> itq = sq.getRowKeys(ks,keys);


for(int inx = 0 ; inx < itq.size() ; inx++){

   JSONObject j1 = new JSONObject();
   // StockQuote
   SuperRow<String, String, String, String> srow = itq.get(inx);
   j1.put("Key",srow.getKey());
   List scols = srow.getSuperSlice().getSuperColumns();
   for (int j = 0; j < scols.size(); j++) {
        HSuperColumn shc = (HSuperColumn) scols.get(j);
        List schs = shc.getColumns();
        for (int i = 0; i < schs.size(); i++) {
          HColumn hc = (HColumn)schs.get(i);
	  String val = "";
	  val = hc.getValue().toString();
	  j1.put(hc.getName().toString(),val);
        }
	ja.put(j1);
   }
}
*/

   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("StockTickerTime",sd0.getQueryTime());


   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("msgArray", ja);
   j.put("queryTime", jqueryTime);
   out.print(j.toString());
   if(true) return;
%>
