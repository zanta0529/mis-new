<%@ page contentType="text/plain; charset=UTF-8" %><%@ page
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

String ex_ch = "tse_t00.tw"; // request.getParameter("ex_ch");
String ex = "tse"; // request.getParameter("ex");
String ch = "t00.tw"; // request.getParameter("ch");
String pt = "B"; // request.getParameter("pt");
String date = ExLastTradeDate.getLastTradeDate(null,"tse");

/*
SystemDaily sys = new SystemDaily(date);
List<HColumn<String,String>> inxList = sys.get(ks,"TaiexInx","TaiexInx",1);
if(inxList.size()>0) {
  HColumn<String,String> hols = inxList.get(0);
  org.json.JSONObject dataHash = new JSONObject(hols.getValue());

  String outString = "";
  outString="\""+ Utility.getSQLDateStr(date).replace('-','/')+"\",\r\n";
  outString+="\""+dataHash.get("time")+"\",\""+dataHash.get("inx")+"\",\""+dataHash.get("vol")+"\",\""+dataHash.get("chg")+"\",\""+dataHash.get("per")+"\",\""+dataHash.get("hi")+"\",\""+dataHash.get("lo")+"\",\""+dataHash.get("tot")+"\"";
  out.print(outString);
  if(true) return;

}*/

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



long tlong0 = 0;
long tlong1 = 0;



//tse_2454.tw_20130531'
StockTickerIndex sd0 = new StockTickerIndex(date,ex+"_"+ch+"_"+date);
List<Map.Entry<String, String>> hcols = sd0.get();

StockDetail sd = new StockDetail(ex+"_"+ch+"_"+date);
Map<String, String> sds = sd.getMap();

//System.out.println("==="+sds.size());
if(sds.isEmpty()) return;

String y = sds.get("y");
String z = sds.get("y");
String h = sds.get("y");
String l = sds.get("y");
String t = sds.get("t");
String v = "0";
String tv = "0";

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
        if(!val.endsWith(":00")&&!val.equals("09:00:10")){
       		inst = false;
	    }
	}
	if(inst)ja.put(j1);
}


//System.out.println("==="+ja.length());
if(ja.length()==0){

} else if (ja.length()>0){
   JSONObject j1 = ja.optJSONObject(0);
   v = j1.optString("v");
   z = j1.optString("z");
   t = j1.optString("t");
   h = j1.optString("h");
   l = j1.optString("l");
   StockStatisInfo ssi = new StockStatisInfo(date,"tse_"+date);
   List<Map.Entry<String, String>> cols = ssi.get();
   if(cols.size()==1){
     String jstr = cols.get(0).getValue();
     org.json.JSONObject js = null;
     try{
     js=new JSONObject(jstr);
     } catch(Exception ex2){
       js=new JSONObject();
     }
     tv = js.optString("tv");
   }
}
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

outString="\""+ Utility.getSQLDateStr(date).replace('-','/')+"\",\r\n";
outString+="\""+t+"\",\""+z+"\",\""+vol.toString()+"\",\""+cstr+"\",\""+pstr+"\",\""+h+"\",\""+l+"\",\""+tv+"\"";
if(ja.length()==0){
   if(!tradeData.equals("")){
      out.print(tradeData);
      return;
   }
} else {
   tradeData = outString;
}
out.print(outString);
if(true) return;
%><%!
static String tradeData = "";
%>
