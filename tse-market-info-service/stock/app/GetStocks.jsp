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
if(fqy==null)  fqy = "10";
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

org.json.JSONObject js = StocksTimelineManager.getStocksTimeline(rdate, fqy);

if(js==null) js = exec0(ex, date, rdate,  fqy, time, queryMillis);//new org.json.JSONObject();

%><%=(js.toString())%><%!

public static JSONObject exec0(String ex, String date, String rdate, String fqy, String time,long queryMillis) throws Exception {
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
	Vector<String> ckeys = new Vector<String>();
	//2454.tw_tse_20130531_B
	StockDateList sdl = new StockDateList(rdate);

	List<Map.Entry<String,String>> hcols_0 = sdl.get();
	queryMillis+=sdl.getQueryTime();


	org.json.JSONArray ja = new JSONArray();

	for(Map.Entry<String,String> col : hcols_0){
		String key = "";
		 if(col.getKey().length()==7 || col.getKey().length()==8 || col.getKey().length()==9)
			 key = ex+"_"+col.getKey();
		 else continue;
		  
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
	  js1 = exec1(js1);

	  if(!js1.optString("%").equals("")){
	    String tt = js1.optString("%");
	    if(tt.equals("13:35:00")){
	      js1.put("%","13:30:00");
	    }
	  }
	  queryMillis+=stl.getQueryTime();
	  ja.put(js1);
	}

	js.put("stocks",ja);
	js.put("size",ja.length());
	js.put("queryMillis",queryMillis/1000);
	
	return js;
}

public static JSONObject exec1(org.json.JSONObject js) throws Exception {
  String v = "0";
  String y = "0";
  String z = "0";
  if(!js.optString("v").equals("")){
    v = js.optString("v");
  }
  if(!js.optString("y").equals("")){
    y = js.optString("y");
  }
  if(!js.optString("z").equals("")){
    z = js.optString("z");
  } else {
    z = y;
  }
  if(z.equals("0")) {
	  //z = y;
	  //js.put("z", z);
	  js.remove("z");
	  js.put("change","+0.0000");
	  js.put("percent","0.00");
  } else {
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
	  if(!cstr.startsWith("-") && !cstr.equals("0.00")) cstr = "+" + cstr;
	  if(!pstr.startsWith("-") && !pstr.equals("0.00")) pstr = "+" + pstr;

	  js.put("change",cstr);
	  js.put("percent",pstr);	  
  }


return js;
}
%>