<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"  errorPage="/api/error.jsp" %><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
long user_delay = 5000;

String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String lang =  Utility.getHttpParameter(request,"lang"); //request.getParameter("lang");
if(lang==null)
lang = (String)session.getAttribute("lang");
if(lang==null) {
      lang = request.getHeader("Accept-Language");
      if(lang==null) lang = "zh-TW";
      lang = lang.toLowerCase();
      if(lang.indexOf("zh-tw")!=-1) lang = "zh-tw";
      else if(lang.indexOf(",")!=-1){
         lang = lang.split(",")[0];
      }
}
lang=lang.split(",")[0];
lang = lang.replaceAll("_","-");

if(date==null) date = ExLastTradeDate.getLastTradeDate(null,"tse");

Hashtable<String,Map<String,String>> symbolsRows = new Hashtable<String,Map<String,String>>();

   JSONArray ja = new JSONArray();
   JSONArray ja2 = new JSONArray();

   java.util.Hashtable<String,String> stockHash = new Hashtable<String,String>();;
   StockIoFile sioTh08 = new StockIoFile(date,"TH08");
   List<Map.Entry<String,String>> sList = sioTh08.get();
   for(int i = 0 ; i < sList.size() ; i++){
	   Map.Entry<String,String> hcol = sList.get(i);
	   if(hcol.getKey().equals("key")) continue;
	   String name = hcol.getKey();
	   if(name.indexOf("_")!=-1){
		   name = name.split("_")[0];
		   stockHash.put(name,"");
	   }
   }

   StockIoFile sio = new StockIoFile(date,"TH05");
   sList = sio.get();
   for(int i = 0 ; i < sList.size() ; i++){
	  Map.Entry<String,String> hcol = sList.get(i);
	  if(hcol.getKey().equals("key")) continue;
      JSONObject j1 = new JSONObject(hcol.getValue());
      //ja.put(j1);
      ja2 = j1.getJSONArray("msgArray");
   }

if(!lang.equals("zh-tw")){
   List<String> symbols = new Vector<String>();
   for(int i = 0 ; i < ja2.length() ; i++){
      JSONObject js = ja2.getJSONObject(i);
      if(!js.has("stkno")) continue;
      String stkno = js.getString("stkno");
      if(stockHash.get(stkno)==null) continue;
      symbols.add(stkno);
      StockNameStore sns = new StockNameStore(date,stkno);
      symbolsRows.put(stkno, sns.getMap());
   }

}

   for(int i = 0 ; i < ja2.length() ; i++){
      JSONObject js = ja2.getJSONObject(i);
      if(js.has("stkno")){
         String stkno = js.getString("stkno");
	 if(stockHash.get(stkno)!=null){
            if(!lang.equals("zh-tw")){
	       Map<String,String> symbolRow = symbolsRows.get(stkno);
	       if(symbolRow==null) continue;
	       String hcol = symbolRow.get("en");
	       if(hcol==null) continue;
               js.put("stkname",hcol);
	    }
	    ja.put(js);
	 }
      }
   }

   user_delay = StockInfoManager.getUserDelayMillis("sb");
   if (user_delay < 5000) {
       user_delay = 5000;
   }
   JSONObject j = new JSONObject();
   j.put("size",ja.length());
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("msgArray", ja);
   j.put("queryTime", sio.getQueryTime());
   j.put("userDelay", user_delay);
   out.print(j.toString());
   if(true) return;

%>
