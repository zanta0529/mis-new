<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="java.text.SimpleDateFormat" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

long patch = 0;
String d1_prefix = "00000000235959";
String ex = Utility.getHttpParameter(request,"ex"); // request.getParameter("ex");
String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String fqyStr = Utility.getHttpParameter(request,"fqy"); // request.getParameter("d");
String patchStr = Utility.getHttpParameter(request,"p"); // request.getParameter("d");

if (!( ch.equals("FRMSA.tw") ||  ch.equals("t00.tw") || ch.equals("o00.tw") ))
{
    Thread.currentThread().sleep(60);
    return;
}

int fqy = Utility.parseInt(fqyStr);
if(fqy==0) fqy=1;
long user_delay = 15000;

if(patchStr==null)
   patch = OtOhlcManager.getOhlcPatchTime();//60000 ;
else patch = Utility.parseInt(patchStr);

if(ex==null &&  ch==null){
	JSONObject j = new JSONObject();
	j.put("rtcode","9999");
	j.put("rtmessage","參數不足");
	out.print(j.toString());
	return;
}
patch = patch * fqy;

if(ex==null) ex = "otc";
if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);

List<String> keys = new Vector<String>();
  String key = ex+"_"+ ch+ "_"+date;
  keys.add(key);



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

OtOhlcManager om = new OtOhlcManager();
Vector<OtOhlcData> sList = om.getOHLC(ex,ch,tlong0,tlong1,fqy);

String lastDatetime = Utility.getDateTimeStr(tlong0).substring(0,14);
if(!Utility.getDateStr().equals(date1.substring(0,8))){ //結束日非當日
   lastDatetime = Utility.getDateTimeStr(tlong1).substring(0,14);
}

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

   //j1.put("d",od.getDate());
   //j1.put("t",hour+":"+mins);
   //j1.put("o",od.getOpen().toString());
   //j1.put("h",od.getHigh().toString());
   //j1.put("l",od.getLow().toString());
   j1.put("c",od.getCurrent().toString());
   //j1.put("v",od.getVolume().toString());
   j1.put("s",od.getSubvolume().toString());
   j1.put("t",String.valueOf((od.getTlong()+patch)));
   lastDatetime = Utility.getDateTimeStr(od.getTlong()).substring(0,14);
   ja.put(j1);
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

   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("StockOhlc",om.getQueryTime());
   jqueryTime.put("totalMicroTime",om.getQueryTime());


   JSONObject j = new JSONObject();
   j.put("ex",ex);
   j.put("ch",ch);
   j.put("lastDatetime",lastDatetime);
   j.put("size",sList.size());
   j.put("frequency",fqy);
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("ohlcArray", ja);
   j.put("queryTime", jqueryTime);
   j.put("userDelay", user_delay);
   out.print(j.toString());
   if(true) return;

%>
