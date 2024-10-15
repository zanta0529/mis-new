<%@ page contentType="text/html; charset=UTF-8" errorPage="/api/error.jsp" %>
<%@ page import="com.ecloudlife.util.Utility" %>
<%@ page import="com.proco.datautil.*" %>
<%@ page import="com.ecloudlife.cass.logicutil.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.json.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String d1_prefix = "00000000235959";
String ex = Utility.getHttpParameter(request,"ex"); // request.getParameter("ex");
String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String fqyStr = Utility.getHttpParameter(request,"fqy"); // request.getParameter("d");
int fqy = Utility.parseInt(fqyStr);
if(fqy==0) fqy=1;
long user_delay = 15000;
long patch = OtOhlcManager.getOhlcPatchTime();//60000 ;
patch = patch * fqy;

if(ex==null) ex = "tse";
if(ch==null) ch = "t00.tw";

if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);

JSONArray jaInfo = new JSONArray();
JSONObject js = getStockInfo("tse_t00.tw_"+date);
JSONObject js2 = new JSONObject();
String tmp ;
String indx_y ;
String indx_t ;
int a_i,b_i;
float a_f,b_f,diff_f;
int tse_v,otc_v;

indx_y = js.optString("y");js2.put("tse_y",String.valueOf(indx_y));
indx_t = js.optString("z");js2.put("tse_z",String.valueOf(indx_t));
tmp = js.optString("h");js2.put("tse_h",String.valueOf(tmp));
tmp = js.optString("l");js2.put("tse_l",String.valueOf(tmp));
//tmp = js.optString("v");js2.put("tse_v",String.valueOf(tmp));

if( ! indx_t.equals("")) {
    BigDecimal current = new BigDecimal(indx_t.trim());
    BigDecimal base = new BigDecimal(indx_y.trim());

    BigDecimal diff = getIndexDiff(current, base);
    BigDecimal percentage = calculatePercentage(diff, base);

    js2.put("tse_d", diff);
    js2.put("tse_p", percentage);
}

tmp = js.optString("d");js2.put("d",String.valueOf(tmp));
tmp = js.optString("t");js2.put("t",String.valueOf(tmp));

tmp=getStatics("tse_"+date).optString("tz");
//js2.put("tse_v",String.valueOf(tmp));

tse_v=(int)Math.floor(Float.parseFloat(tmp)/1000000);
js2.put("tse_v",""+tse_v);

ex = "otc";
if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);


JSONObject js_o = getStockInfo("otc_o00.tw_"+date);
indx_y = js_o.optString("y");js2.put("otc_y",String.valueOf(indx_y));
indx_t = js_o.optString("z");js2.put("otc_z",String.valueOf(indx_t));
tmp = js_o.optString("h");js2.put("otc_h",String.valueOf(tmp));
tmp = js_o.optString("l");js2.put("otc_l",String.valueOf(tmp));
//tmp = js_o.optString("v");js2.put("otc_v",String.valueOf(tmp));

if( ! indx_t.equals("")) {
    BigDecimal current = new BigDecimal(indx_t.trim());
    BigDecimal base = new BigDecimal(indx_y.trim());

    BigDecimal diff = getIndexDiff(current, base);
    BigDecimal percentage = calculatePercentage(diff, base);

    js2.put("otc_d", diff);
    js2.put("otc_p", percentage);
}

tmp=getStatics("otc_"+date).optString("tz");

otc_v=(int)Math.floor(Float.parseFloat(tmp)/1000000);
js2.put("otc_v",""+otc_v);

ex = "tse";

if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);


JSONObject js_f = getStockInfo("tse_FRMSA.tw_"+date);
//if(ch.equals("FRMSA.tw"))
//{

	 JSONObject jt = getStockInfo("tse_t00.tw_"+date);
	 JSONObject jo = getStockInfo("otc_o00.tw_"+date);
	 String tv = jt.optString("v");
	 String ov = jo.optString("v");
//	 long v = Utility.parseLong(tv)+Utility.parseLong(ov);
//	 js2.put("fms_v",String.valueOf(v));
//	 js2.put("fms_v",String.valueOf(v));
	 js2.put("fms_v",""+(tse_v+otc_v));
//}
indx_y = js_f.optString("y");js2.put("fms_y",String.valueOf(indx_y));
indx_t = js_f.optString("z");js2.put("fms_z",String.valueOf(indx_t));

if( ! indx_t.equals("")) {
    tmp = js_f.optString("h");js2.put("fms_h",String.valueOf(tmp));
    tmp = js_f.optString("l");js2.put("fms_l",String.valueOf(tmp));

    BigDecimal current = new BigDecimal(indx_t.trim());
    BigDecimal base = new BigDecimal(indx_y.trim());

    BigDecimal diff = getIndexDiff(current, base);
    BigDecimal percentage = calculatePercentage(diff, base);

    js2.put("fms_d", diff);
    js2.put("fms_p", percentage);
}

jaInfo.put(js2);

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
   j.put("rtmessage","time param err");
   out.print(j.toString());
   return;
}


String lastDatetime = Utility.getDateTimeStr(tlong0).substring(0,14);
if(!Utility.getDateStr().equals(date1.substring(0,8))){
   lastDatetime = Utility.getDateTimeStr(tlong1).substring(0,14);
}

OtOhlcManager om = new OtOhlcManager();
JSONArray ja = new JSONArray();
JSONArray ja1 = getOhlcManager(om, lastDatetime, ex, ch, tlong0, tlong1, fqy, patch);
if(ch.equals("FRMSA.tw")){
JSONArray jaTse = getOhlcManager(om, lastDatetime, "tse", "t00.tw", tlong0, tlong1, fqy, patch);
JSONArray jaOtc = getOhlcManager(om, lastDatetime, "otc", "o00.tw", tlong0, tlong1, fqy, patch);
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


   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("StockOhlc",om.getQueryTime());
   jqueryTime.put("totalMicroTime",om.getQueryTime());
   jqueryTime.put("stockDetail",0L);
   jqueryTime.put("stockQuote",0L);
   jqueryTime.put("totalMicroTime",0L);

   JSONObject j = new JSONObject();
//   j.put("ex",ex);
//   j.put("ch",ch);
//   j.put("lastDatetime",lastDatetime);
//   j.put("size",ja.length());
//   j.put("frequency",fqy);
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
//   j.put("ohlcArray", ja);
   j.put("infoArray", jaInfo);
//   j.put("queryTime", jqueryTime);
//   j.put("userDelay", user_delay);
   out.print(j.toString());
   if(true) return;

%>

<%!
public static BigDecimal getIndexDiff(BigDecimal current, BigDecimal base) {
    return current.subtract(base);
}

public static BigDecimal calculatePercentage(BigDecimal diff, BigDecimal base) {
    return diff.divide(base, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.DOWN);
}

public static JSONArray getOhlcManager(OtOhlcManager om,String lastDatetime,String ex,String ch,long tlong0,long tlong1,int fqy,long patch) throws Exception{

Vector<OtOhlcData> sList = om.getOHLC(ex,ch,tlong0,tlong1,fqy);
JSONArray ja = new JSONArray();
OtOhlcData od_close = null;

return ja;
}


public static JSONObject getStatics(String key) throws Exception {
//StockStatisManager ssm = StockStatisManager.getStockStatisList(key,us,30000,0);
JSONObject j1 = new JSONObject();
//if(ssm!=null)
List<Map.Entry<String,String>> cols = StockStatisManager.getStatisListNow(key); 
//ssm.getStatisList(); 
//ss.get(ks,"","",100);
   j1.put("key",key);
   //tz,tv,tr,t4,t3,t2,t1
   for (int j = 0; j < cols.size(); j++) {
	  Map.Entry<String,String> hc = (Map.Entry)cols.get(j);
      String val= hc.getValue().toString();
      String name = hc.getKey().toString();
      if(name.startsWith("tz") && name.length()==2)
      j1.put(name,val);
      //System.out.println(hc.getName().toString() + " " + val);
      //rt.put(hc.getName().toString(), val);
   }
   return j1;
}

public static JSONObject getStockInfo(String key) throws Exception {
	JSONObject j1 = new JSONObject();
	StockDetail sd = new StockDetail(key);
	StockQuote sq = new StockQuote(key);

	 Map<String, String> sdMap = sd.getMap();
	 Map<String, String> sqMap = sq.getMap();
	 sdMap.putAll(sqMap);
	 
	 Set<Map.Entry<String,String>> ents = sdMap.entrySet();
	 for(Map.Entry<String,String> ent : ents){
		 j1.put(ent.getKey(), ent.getValue());
	 }
	 
	return j1;
}


/*
public static JSONObject getStockInfo(Keyspace ks,StockDetail sd,StockQuote sq,List<String> keys) throws Exception{

java.util.List<Row<String, String, String>> itd = sd.getRowKeys(ks,keys);
java.util.List<SuperRow<String, String, String, String>> itq = sq.getRowKeys(ks,keys);

JSONObject j1 = new JSONObject();
for(int inx = 0 ; inx < itd.size() ; inx++){
   Row<String, String, String> row = itd.get(inx);
   List cols = row.getColumnSlice().getColumns();

   // StockDetail
   for (int j = 0; j < cols.size(); j++) {
       HColumn hc = (HColumn) cols.get(j);
       String val = "";
       val = hc.getValue().toString();
           String name = hc.getName().toString();
       j1.put(name,val);
           if(name.equals("it")){
             //user_delay = StockInfoManager.getUserDelayMillis(val);
           }
   }
   // StockQuote
   if(inx < itq.size()){
     SuperRow<String, String, String, String> srow = itq.get(inx);
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
     }
   }
  }
  return j1;
}
*/
%>
