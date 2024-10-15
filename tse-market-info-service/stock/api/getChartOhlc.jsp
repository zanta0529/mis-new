<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="com.ecloudlife.util.Utility"%>
<%@ page import="com.proco.datautil.*"%>
<%@ page import="com.ecloudlife.cass.logicutil.*"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@ page import="org.json.*"%>

<%@ include file="../../common-utils.jsp" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String d1_prefix = "00000000235959";
String ex = Utility.getHttpParameter(request,"ex"); // request.getParameter("ex");
String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String fqyStr = Utility.getHttpParameter(request,"fqy"); // request.getParameter("d");
//date="20190426";
int fqy = Utility.parseInt(fqyStr);
if (fqy==0) fqy = 1;
long user_delay = 15000;
long patch = OtOhlcManager.getOhlcPatchTime();//60000 ;
if (ex == null && ch == null) {
	JSONObject j = new JSONObject();
	j.put("rtcode", "9999");
	j.put("rtmessage", "參數不足");
	out.print(j.toString());
	return;
}

// 取得 Client 端 IP
String ipAddress = purifyString(request.getHeader("X-FORWARDED-FOR"), "", 20);
if (ipAddress == null || "".equals(ipAddress)) {
    ipAddress = request.getRemoteAddr();
}

// 僅證交所 OA 環境免判斷指數代碼
boolean isTwseOA = ("60.250.31.34".equals(ipAddress)) || ("61.222.7.34".equals(ipAddress)) || ("123.51.218.120".equals(ipAddress));
if ( ! isTwseOA) {
    if ( ! (ch.equals("FRMSA.tw") || ch.equals("t00.tw") || ch.equals("o00.tw") || ch.equals("CG100.tw") || ch.equals("TW50.tw"))) {
        Thread.currentThread().sleep(60);
        return;
    }
}

patch = patch * fqy;
if (ex == null) ex = "tse";

if (date == null) date = ExLastTradeDate.getLastTradeDate(null, ex);


String key = ex+"_"+ ch+ "_"+date;

JSONArray jaInfo = new JSONArray();
JSONObject js = getStockInfo(key);

if (ch.equals("FRMSA.tw")) {    
    JSONObject jt = getStockInfo("tse_t00.tw_"+date);
    JSONObject jo = getStockInfo("otc_o00.tw_"+date);
    String tv = jt.optString("v");
    String ov = jo.optString("v");
    
    long v = Utility.parseLong(tv)+Utility.parseLong(ov);
    js.put("v",String.valueOf(v));
}
jaInfo.put(js);

String date0 = date;
String date1 = date;
while (date0.length() < 14) {
    date0 += "0";
}

while (date1.length() < 14) {
    date1 += ""+d1_prefix.charAt(date1.length());
}

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

String lastDatetime = Utility.getDateTimeStr(tlong0).substring(0, 14);

if( ! Utility.getDateStr().equals(date1.substring(0,8))) { //結束日非當日
    lastDatetime = Utility.getDateTimeStr(tlong1).substring(0, 14);
}

OtOhlcManager om = new OtOhlcManager();
JSONArray ja = new JSONArray();
JSONArray ja1 = getOhlcManager(om, lastDatetime, ex, ch, tlong0, tlong1, fqy, patch);

if (ch.equals("FRMSA.tw")) {
    JSONArray jaTse = getOhlcManager(om, lastDatetime, "tse", "t00.tw", tlong0, tlong1, fqy, patch);
    JSONArray jaOtc = getOhlcManager(om, lastDatetime, "otc", "o00.tw", tlong0, tlong1, fqy, patch);
    for(int i = 0 ; i < ja1.length() && i < jaTse.length() && i <  jaOtc.length() ; i++ ) {
        JSONObject j1 = ja1.optJSONObject(i);
        JSONObject jTse = jaTse.optJSONObject(i);
        JSONObject jOtc = jaOtc.optJSONObject(i);
        String sTse = jTse.optString("s");
        String sOtc = jOtc.optString("s");
        long s = Utility.parseLong(sTse)+Utility.parseLong(sOtc);
        j1.put("s",String.valueOf(s));
        ja.put(j1);
    }
} else {
    ja = ja1;
}
    JSONObject jqueryTime = new JSONObject();
    jqueryTime.put("StockOhlc",om.getQueryTime());
    jqueryTime.put("totalMicroTime",om.getQueryTime());
    jqueryTime.put("stockDetail",0);
    jqueryTime.put("stockQuote",0);
    jqueryTime.put("totalMicroTime",0);
    
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
    j.put("queryTime", jqueryTime);
    j.put("userDelay", user_delay);
    j.put("clientIP", ipAddress);
    out.print(j.toString());
    if (true) return;
%>

<%!
public static JSONArray getOhlcManager(OtOhlcManager om,String lastDatetime,String ex,String ch,long tlong0,long tlong1,int fqy,long patch) throws Exception{

Vector<OtOhlcData> sList = om.getOHLC(ex,ch,tlong0,tlong1,fqy); //(String ex, String ch, long start, long end, int space)
JSONArray ja = new JSONArray();
OtOhlcData od_close = null;

for(int i = 0 ; i < sList.size() ; i++) {
    OtOhlcData od = sList.get(i);
    JSONObject j1 = new JSONObject();
    String hour = od.getTime().substring(0,2);
    String mins = od.getTime().substring(2,4);
    if (hour.equals("13") && Utility.parseInt(mins) >= 29) {
        if (od_close == null) {
            od_close = od;
        } else {
            //od.setTrade(od_close.getCurrent(),od_close.getVolume());
            od.setSubvolume(od_close.getSubvolume());
            od_close = od;
        }
        continue;
    }
    j1.put("c",od.getCurrent().toString());
    j1.put("s",od.getSubvolume().toString());
    j1.put("t",String.valueOf((od.getTlong()+patch)/100000));
    lastDatetime = Utility.getDateTimeStr(od.getTlong()).substring(0,14);
    ja.put(j1);
}

if (od_close != null) {
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
    j1.put("t",String.valueOf(od.getTlong()/100000));
    lastDatetime = Utility.getDateTimeStr(od.getTlong()).substring(0,14);
    ja.put(j1);
}
return ja;
}

public static JSONObject getStockInfo(String key) throws Exception{

StockDetail sd = new StockDetail(key);
StockQuote sq = new StockQuote(key);

java.util.List<Map.Entry<String, String>> itd = sd.get();
java.util.List<Map.Entry<String, String>> itq = sq.get();	
	

JSONObject j1 = new JSONObject();
for(int inx = 0 ; inx < itd.size() ; inx++){
	Map.Entry<String, String> row = itd.get(inx);
	j1.put(row.getKey(), row.getValue());
}
for(int inx = 0 ; inx < itq.size() ; inx++){
	Map.Entry<String, String> row = itq.get(inx);
	j1.put(row.getKey(), row.getValue());
}

  return j1;
}
%>
