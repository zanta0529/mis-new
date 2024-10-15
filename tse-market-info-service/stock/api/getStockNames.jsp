<%@ page contentType="text/html; charset=UTF-8" errorPage="/api/error.jsp"%>

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

String name = Utility.getHttpParameter(request,"n"); // request.getParameter("n");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String lang = Utility.getHttpParameter(request,"lang"); // request.getParameter("lang");
if(lang==null)
lang = (String)session.getAttribute("lang");
if(lang==null) {
      lang = purifyString(request.getHeader("Accept-Language"), "zh-tw", 5);
      if(lang==null) lang = "zh-TW";
      lang = lang.toLowerCase();
      if(lang.indexOf("zh-tw")!=-1) lang = "zh-tw";
      else if(lang.indexOf(",")!=-1){
         lang = lang.split(",")[0];
      }
}
lang=lang.split(",")[0];
lang = lang.replaceAll("_","-");

if(name==null){
   JSONObject j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","參數不足");
   out.print(j.toString());
   return;
}
String name1 = new String(name.getBytes("ISO_8859_1"),"UTF-8");
if(!name1.startsWith("?")) name = name1;
name = name.toUpperCase();

String ckey = name+"_"+lang+"_"+date;
JSONObject cjs = com.ecloudlife.cass.logicutil.StockCategory.stnameHash.get(ckey);
if(cjs!=null) {
  out.print(cjs.toString());
  if(true) return;
}

if(date==null) date = ExLastTradeDate.getLastTradeDate(null,"tse");
StockNames sn = new StockNames(date,name);

List<String> symbols = new Vector<String>();
Hashtable<String,Map<String,String>> symbolsRows = new Hashtable<String,Map<String,String>>();
Vector<String> stockStrs = new Vector<String>();
Vector<String> stockStrs5 = new Vector<String>();
List<Map.Entry<String,String>> snList0 = sn.get();

//for()

for(Map.Entry<String,String> hcol : snList0 ){
   //tse_2454.tw_20121127
   if(hcol.getKey().equals("key")) continue;
   if(hcol.getValue().equals(date) && (hcol.getKey().length()==11)){
      stockStrs.add(hcol.getKey()+"_"+date);
   } else {
      stockStrs5.add(hcol.getKey()+"_"+date);
   }
}

stockStrs.addAll(stockStrs5);
JSONArray ja = new JSONArray();
for(String key : stockStrs){
	String symbo = Utility.filiterString(key.split("_")[1], ".tw");
	StockNameStore sns = new StockNameStore(date,symbo);
	Map<String, String> symbolRow = sns.getMap();
	if(symbolRow.isEmpty()) continue;
	
	JSONObject j1 = new JSONObject();
	j1.put("key",key);
	j1.put("c",symbo);
	if(!lang.equals("zh-tw")){
		String en = symbolRow.get("en");
		j1.put("n",en);
	} else {
		String cn = symbolRow.get("cn");
		j1.put("n",cn);		
	}
	   ja.put(j1);
}

JSONObject j = new JSONObject();
j.put("rtcode","0000");
j.put("rtmessage","Success");
j.put("datas",ja);
j.put("build",Utility.getDateTimeStr(System.currentTimeMillis()));
if(!ja.isEmpty()){
  com.ecloudlife.cass.logicutil.StockCategory.stnameHash.put(ckey,j);
}

out.print(j.toString());
if(true) return;
%>
