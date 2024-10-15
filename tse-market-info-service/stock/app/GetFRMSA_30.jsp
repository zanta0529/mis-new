<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.ecloudlife.util.UserSession" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %><%

response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String ex = Utility.getHttpParameter(request,"ex");
String fqy = Utility.getHttpParameter(request,"f"); // request.getParameter("d");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String time = Utility.getHttpParameter(request,"t"); // request.getParameter("d");
long queryMillis = 0;
if(ex==null) ex="tse";
if(fqy==null)  fqy = "1";
if(date==null) date = Utility.getDateStr();
if(time==null) {
  long qMillis = System.currentTimeMillis();
  long decMillis = qMillis%(Utility.parseInt(fqy)*60000);
  qMillis = qMillis - decMillis;

  time = Utility.getTimeStr(qMillis);
  time = time.substring(0,4)+"00";
}

  int t = Utility.parseInt(time);
  if(t>133000 && t<=133500){
    time = "133500";
  }

String rdate = ExLastTradeDate.getLastTradeDate(null, ex);
org.json.JSONObject js = new org.json.JSONObject();
js.put("req",date+time);
js.put("now",Utility.getDateStr()+Utility.getTimeStr());

long qtime = 0;
if(date.equals(rdate)){
	long baseMillis = Utility.getDateFormatTransMillis(date);
    long dataMillis = Utility.getDateTimeFormatTransMillis(date+time) - baseMillis;
	qtime = 99999999L- dataMillis;
}


Vector<String> keys = new Vector<String>();
keys.add(ex+"_FRMSA.tw");

org.json.JSONArray ja = new JSONArray();

for(String key : keys){
  StockTimeLine stl = new StockTimeLine(ex, rdate, key);
  String tline = stl.getZrange(qtime);
  if(tline==null) continue;
  org.json.JSONObject js1 = new org.json.JSONObject(tline);
  String v = "0";
  String y = "0";
  String z = "0";
  if(!js1.optString("v").equals("")){
    v = js1.optString("v");
  }
  if(!js1.optString("y").equals("")){
    y = js1.optString("y");
  }
  if(!js1.optString("z").equals("")){
    z = js1.optString("z");
  }
  js1 = exec(js1, v, y, z);

  if(!js1.optString("%").equals("")){
    String tt = js1.optString("%");
    if(tt.equals("13:35:00")){
      js1.put("%","13:30:00");
    }
  }
  queryMillis+=stl.getQueryTime();
  ja.put(js1);
}

js.put("index",ja);
js.put("size",ja.length());
js.put("queryMillis",queryMillis/1000);
%><%=(js.toString())%><%!

public static JSONObject exec(org.json.JSONObject js,String v,String y,String z) throws Exception {
java.math.BigDecimal vol = new java.math.BigDecimal(v);
java.math.BigDecimal yest = new java.math.BigDecimal(y);
java.math.BigDecimal last = new java.math.BigDecimal(z);
java.math.BigDecimal chg = last.subtract(yest);
//java.math.BigDecimal pre = chg.divide(yest,5,java.math.RoundingMode.HALF_EVEN).multiply(new java.math.BigDecimal(10000).divide(new java.math.BigDecimal(100),2,java.math.RoundingMode.HALF_EVEN));
//pre = pre.divide(new java.math.BigDecimal(1),2,java.math.RoundingMode.DOWN);
java.math.BigDecimal pre = chg.divide(yest,5,java.math.RoundingMode.DOWN).multiply(new java.math.BigDecimal(10000).divide(new java.math.BigDecimal(100),2,java.math.RoundingMode.DOWN));
pre = pre.divide(new java.math.BigDecimal(1),2,java.math.RoundingMode.HALF_UP);

vol = vol.divide(new java.math.BigDecimal(100),0,java.math.RoundingMode.FLOOR);
String outString = "";

String cstr = chg.toString();
String pstr = pre.toString();
if(!cstr.startsWith("-")) cstr = "+" + cstr;
if(!pstr.startsWith("-")) pstr = "+" + pstr;

js.put("change",cstr);
js.put("percent",pstr);

return js;
}


%>
