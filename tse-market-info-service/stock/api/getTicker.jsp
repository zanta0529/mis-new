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

String ex_ch = Utility.getHttpParameter(request,"ex_ch"); // request.getParameter("ex_ch");
String ex = Utility.getHttpParameter(request,"ex"); // request.getParameter("ex");
String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String pt = Utility.getHttpParameter(request,"pt"); // request.getParameter("pt");
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

if(pt==null) pt = "B"; //B=競價 O=零股 F=定價

if( (ex_ch==null && (ch==null || ex == null))){
	JSONObject j = new JSONObject();
	j.put("rtcode","9999");
	j.put("rtmessage","參數不足");
	out.print(j.toString());
	return;
}

if(ex_ch!= null){
   if(ex_ch.indexOf("_")==-1){
	JSONObject j = new JSONObject();
	j.put("rtcode","9999");
	j.put("rtmessage","參數有誤");
	out.print(j.toString());
        return;
   }
   ch = ex_ch.split("_")[1];
   ex = ex_ch.split("_")[0];
}
if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);


long tlong0 = 0;
long tlong1 = 0;

//tse_2454.tw_20130531'
StockTickerIndex sd0 = new StockTickerIndex(date,ex+"_"+ch+"_"+date);
List<Map.Entry<String,String>> hcols = sd0.get();

JSONArray ja = new JSONArray();
for(Map.Entry<String, String> col : hcols){
	StockTickerInfo st = new StockTickerInfo(date,col.getKey());
	Map<String, String> itq = st.getMap();
    JSONObject j1 = new JSONObject();
    boolean inst = true;
	Set<Map.Entry<String,String>> ents = itq.entrySet();
	for(Map.Entry<String,String> ent : ents){
		String name = ent.getKey();
		String val = ent.getValue();
		j1.put(name, val);
	}
	if(inst)ja.put(j1);
}



   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("StockTickerIndex",sd0.getQueryTime());
   jqueryTime.put("StockTickerInfo",0);
   jqueryTime.put("totalMicroTime",0);

   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("msgArray", ja);
   j.put("queryTime", jqueryTime);
   out.print(j.toString());
   if(true) return;
%>
