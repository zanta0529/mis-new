<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.UserSession" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="java.text.SimpleDateFormat" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String d1_prefix = "00000000235959";
String ex = Utility.getHttpParameter(request,"ex"); // request.getParameter("ex");
String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String fqyStr = Utility.getHttpParameter(request,"fqy"); // request.getParameter("d");

if (!( ch.equals("FRMSA.tw") ||  ch.equals("t00.tw") || ch.equals("o00.tw") ))
{
//    Thread.currentThread().sleep(50);
//    out.print("參數不足");
    Thread.currentThread().sleep(60);
//    response.sendRedirect("http://1.1.1.1");
    return;
}

int fqy = Utility.parseInt(fqyStr);
if(fqy==0) fqy=1;
long user_delay = 15000;
long patch = OtOhlcManager.getOhlcPatchTime();//60000 ;

if(ex==null &&  ch==null){
	JSONObject j = new JSONObject();
	j.put("rtcode","9999");
	j.put("rtmessage","參數不足");
	out.print(j.toString());
	return;
}
patch = patch * fqy;

if(ex==null) ex = "tse";
if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);
long lastlong = -1;


String date0 = date;
String date1 = date;
while (date0.length() < 14)
      date0 += "0";

while (date1.length() < 14)
      date1 += ""+d1_prefix.charAt(date1.length());

long tlong0 = 0;
long tlong1 = 0;

try {
   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
   tlong0 = sdf.parse(date0).getTime();
   tlong1 = sdf.parse(date1).getTime();
} catch (Exception ex1) {
   JSONObject j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","時間參數有誤");
   out.print(j.toString());
   return;
}

String lastDatetime = Utility.getDateTimeStr(tlong0).substring(0,14);
if(!Utility.getDateStr().equals(date1.substring(0,8))){ //結束日非當日
   lastDatetime = Utility.getDateTimeStr(tlong1).substring(0,14);
}

OtOhlcManager om = new OtOhlcManager();
JSONArray ja = new JSONArray();
JSONArray ja1 = getOhlcManager(om, lastDatetime, ex, ch, tlong0, tlong1, fqy, patch, lastlong);
if(ch.equals("FRMSA.tw")){
JSONArray jaTse = getOhlcManager(om, lastDatetime, "tse", "t00.tw", tlong0, tlong1, fqy, patch, lastlong);
JSONArray jaOtc = getOhlcManager(om, lastDatetime, "otc", "o00.tw", tlong0, tlong1, fqy, patch, lastlong);
for(int i = 0 ; i < ja1.length() && i < jaTse.length() && i <  jaOtc.length() ; i++ ){
   JSONObject j1 = ja1.optJSONObject(i);
   JSONObject jTse = jaTse.optJSONObject(i);
   JSONObject jOtc = jaOtc.optJSONObject(i);
   String sTse = jTse.optString("s");
   String sOtc = jOtc.optString("s");
   long s = Utility.parseLong(sTse)+Utility.parseLong(sOtc);
   j1.put("s",String.valueOf(s));
   ja.put(j1);
}
} else ja = ja1;

String last = "";
if(!ja.isEmpty()){
	JSONObject js0 = ja.optJSONObject(ja.length()-1);
	if(js0!=null){
		last = js0.optString("c","");
	}
}

String key = ex+"_"+ ch+ "_"+date;

StockDetail sd = new StockDetail(key);
java.util.List<Map.Entry<String, String>> itd = sd.get();
java.util.List<Map.Entry<String, String>> itq = null;
if(!last.equals("")){
	key = ex+"_"+ ch+ "_"+date+"_"+last;
	StockQuoteIndex sq = new StockQuoteIndex(key); 
	itq = sq.get();
} 
if(itq==null || itq.isEmpty()){
	key = ex+"_"+ ch+ "_"+date;
	StockQuote sq = new StockQuote(key);
	itq = sq.get();	
}


JSONArray jaInfo = new JSONArray();
{
	JSONObject j1 = new JSONObject();
	for(int inx = 0 ; inx < itd.size() ; inx++){
		Map.Entry<String, String> row = itd.get(inx);
		j1.put(row.getKey(), row.getValue());
	}
	for(int inx = 0 ; inx < itq.size() ; inx++){
		Map.Entry<String, String> row = itq.get(inx);
		j1.put(row.getKey(), row.getValue());
	}
   jaInfo.put(j1);
}

/*

Vector<OtOhlcData> sList = om.getOHLC(ks,ex,ch,tlong0,tlong1,fqy);

JSONArray ja = new JSONArray();
OtOhlcData od_close = null;

for(int i = 0 ; i < sList.size() ; i++){
   OtOhlcData od = sList.get(i);
   JSONObject j1 = new JSONObject();
   String hour = od.getTime().substring(0,2);
   String mins = od.getTime().substring(2,4);
   if(hour.equals("13") && Utility.parseInt(mins) >= 29){
     if(od_close==null) od_close = od;
     else {
        //od.setTrade(od_close.getCurrent(),od_close.getVolume());
	od.setSubvolume(od_close.getSubvolume());
	od_close = od;
     }
     continue;
   }
   String currDatetime = "";//Utility.getDateTimeStr(od.getTlong()).substring(0,14);
   //j1.put("d",od.getDate());
   //j1.put("t",hour+":"+mins);
   //j1.put("o",od.getOpen().toString());
   //j1.put("h",od.getHigh().toString());
   //j1.put("l",od.getLow().toString());
   j1.put("c",od.getCurrent().toString());
   //j1.put("v",od.getVolume().toString());
   j1.put("s",od.getSubvolume().toString());
   j1.put("t",String.valueOf((od.getTlong()+patch)));



   if(i==sList.size()-1 && lastlong !=-1){
     j1.put("ts", Utility.getTimeStr(lastlong));
     currDatetime = Utility.getDateTimeStr(lastlong).substring(0,14);
   } else {
     j1.put("ts", Utility.getTimeStr((od.getTlong()+patch)));
     currDatetime = Utility.getDateTimeStr((od.getTlong()+patch)).substring(0,14);
   }
   //System.out.println("i="+i+"   sList.size():"+(sList.size()-1)+"   c:"+currDatetime+"    l:"+lastDatetime +"   "+Utility.getTimeStr(lastlong));
   if(!currDatetime.equals(lastDatetime)) ja.put(j1);
   lastDatetime = currDatetime;
}
if(od_close!=null){
   OtOhlcData od = od_close;
   JSONObject j1 = new JSONObject();
   //j1.put("d",od.getDate());
   //j1.put("t",od.getTime().substring(0,2)+":"+od.getTime().substring(2,4));

   //j1.put("o",od.getOpen().toString());
   //j1.put("h",od.getHigh().toString());
   //j1.put("l",od.getLow().toString());
   j1.put("c",od.getCurrent().toString());
   //j1.put("v",od.getVolume().toString());
   j1.put("s",od.getSubvolume().toString());
   j1.put("t",String.valueOf(od.getTlong()));
   lastDatetime = Utility.getDateTimeStr(od.getTlong()).substring(0,14);
   ja.put(j1);
}*/
//------------------------------------Statis
//UserSession us = new UserSession("0000000000");
key = ex+"_"+date;
//StockStatisManager ssm = StockStatisManager.getStockStatisList(key,us,30000,0);
JSONObject j1 = new JSONObject();
if(ch.equals("FRMSA.tw")){
  j1 = getStatics("tse_"+date);
  JSONObject j2 = getStatics("otc_"+date);
  java.util.Iterator it = j1.keys();
  while(it.hasNext()){
    String name = (String)it.next();
    if(name.startsWith("t")){
      String tt = j1.optString(name);
      String to = j2.optString(name);
      long s = Utility.parseLong(tt)+Utility.parseLong(to);
      j1.put(name,String.valueOf(s));
    }

  }
} else j1 = getStatics(key);


/*
{
List<HColumn<String,String>> cols = StockStatisManager.getStatisListNow(key); //ssm.getStatisList(); //ss.get(ks,"","",100);
   j1.put("key",key);
   //tz,tv,tr,t4,t3,t2,t1
   for (int j = 0; j < cols.size(); j++) {
     HColumn hc = (HColumn)cols.get(j);
     String val= hc.getValue().toString();
     String name = hc.getName().toString();
     if(name.startsWith("t") && name.length()==2)
        j1.put(name,val);
        //System.out.println(hc.getName().toString() + " " + val);
	//rt.put(hc.getName().toString(), val);
  }
}*/



   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("StockOhlc",om.getQueryTime());
   jqueryTime.put("stockDetail",sd.getQueryTime());
   jqueryTime.put("stockQuote",0);
   jqueryTime.put("static",StockStatisManager.getQueryMicroTime());
   jqueryTime.put("totalMicroTime",sd.getQueryTime()+om.getQueryTime()+StockStatisManager.getQueryMicroTime());

   JSONObject j = new JSONObject();
   j.put("ex",ex);
   j.put("ch",ch);
   j.put("lastDatetime",lastDatetime);
   j.put("size",ja.length());
   j.put("frequency",fqy);
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("ohlcArray", ja);
   j.put("infoArray", jaInfo);
   j.put("staticObj", j1);
   j.put("queryTime", jqueryTime);
   j.put("userDelay", user_delay);
   j.put("lastIndex", last);
   out.print(j.toString());
   if(true) return;

%><%!

public static JSONObject getStatics(String key) throws Exception {

//StockStatisManager ssm = StockStatisManager.getStockStatisList(key,us,30000,0);
JSONObject j1 = new JSONObject();
//if(ssm!=null)
{
List<Map.Entry<String,String>> cols = StockStatisManager.getStatisListNow(key); //ssm.getStatisList(); //ss.get(ks,"","",100);
   j1.put("key",key);
   //tz,tv,tr,t4,t3,t2,t1
   for (int j = 0; j < cols.size(); j++) {
	 Map.Entry<String,String> hc = cols.get(j);
     String val= hc.getValue().toString();
     String name = hc.getKey().toString();
     if(name.startsWith("t") && name.length()==2)
        j1.put(name,val);
        //System.out.println(hc.getName().toString() + " " + val);
	//rt.put(hc.getName().toString(), val);
  }
}
//patch rockdis 資料結構和原本結構的不同處

if(!j1.has("tv")) j1.put("tv", "0");
if(!j1.has("tz")) j1.put("tz", "0");
if(!j1.has("tr")) j1.put("tr", "0");
return j1;
}

public static JSONArray getOhlcManager(OtOhlcManager om,String lastDatetime,String ex,String ch,long tlong0,long tlong1,int fqy,long patch,long lastlong) throws Exception{

Vector<OtOhlcData> sList = om.getOHLC(ex,ch,tlong0,tlong1,fqy);
JSONArray ja = new JSONArray();
OtOhlcData od_close = null;

System.out.println("---->"+sList.size());

for(int i = 0 ; i < sList.size() ; i++){
   OtOhlcData od = sList.get(i);
   JSONObject j1 = new JSONObject();
   String hour = od.getTime().substring(0,2);
   String mins = od.getTime().substring(2,4);
   if(hour.equals("13") && Utility.parseInt(mins) >= 29){
     if(od_close==null) od_close = od;
     else {
        //od.setTrade(od_close.getCurrent(),od_close.getVolume());
	od.setSubvolume(od_close.getSubvolume());
	od_close = od;
     }
     continue;
   }
   String currDatetime = "";//Utility.getDateTimeStr(od.getTlong()).substring(0,14);
   //j1.put("d",od.getDate());
   //j1.put("t",hour+":"+mins);
   //j1.put("o",od.getOpen().toString());
   //j1.put("h",od.getHigh().toString());
   //j1.put("l",od.getLow().toString());
   j1.put("c",od.getCurrent().toString());
   //j1.put("v",od.getVolume().toString());
   j1.put("s",od.getSubvolume().toString());
   j1.put("t",String.valueOf((od.getTlong()+patch)));



   if(i==sList.size()-1 && lastlong !=-1){
     j1.put("ts", Utility.getTimeStr(lastlong));
     currDatetime = Utility.getDateTimeStr(lastlong).substring(0,14);
   } else {
     j1.put("ts", Utility.getTimeStr((od.getTlong()+patch)));
     currDatetime = Utility.getDateTimeStr((od.getTlong()+patch)).substring(0,14);
   }
   //System.out.println("i="+i+"   sList.size():"+(sList.size()-1)+"   c:"+currDatetime+"    l:"+lastDatetime +"   "+Utility.getTimeStr(lastlong));
   if(!currDatetime.equals(lastDatetime)) ja.put(j1);
   lastDatetime = currDatetime;
}
if(od_close!=null){
   OtOhlcData od = od_close;
   JSONObject j1 = new JSONObject();
   //j1.put("d",od.getDate());
   //j1.put("t",od.getTime().substring(0,2)+":"+od.getTime().substring(2,4));

   //j1.put("o",od.getOpen().toString());
   //j1.put("h",od.getHigh().toString());
   //j1.put("l",od.getLow().toString());
   j1.put("c",od.getCurrent().toString());
   //j1.put("v",od.getVolume().toString());
   j1.put("s",od.getSubvolume().toString());
   j1.put("t",String.valueOf(od.getTlong()));
   lastDatetime = Utility.getDateTimeStr(od.getTlong()).substring(0,14);
   ja.put(j1);
}

return ja;
}

%>
