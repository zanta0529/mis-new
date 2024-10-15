<%@ page contentType="text/html; charset=UTF-8" %>

<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>


<%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.ecloudlife.util.UserSession" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %><%


response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
String sessionStr = "UserSession";
long user_delay = 0;
String ex_ch = Utility.getHttpParameter(request,"ex_ch"); // request.getParameter("ex_ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String lang = Utility.getHttpParameter(request,"lang"); // request.getParameter("lang");
String delay = Utility.getHttpParameter(request,"delay"); // request.getParameter("delay");
String referer = request.getHeader("Referer");


    lang = (String)session.getAttribute("lang");


if(referer==null) referer = "";
if(delay==null) delay = "0";
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
      session.setAttribute("lang","zh_tw");
}
lang=lang.split(",")[0];
lang = lang.replaceAll("_","-");
if(ex_ch==null){
   JSONObject j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","參數不足");
   out.print(j.toString());
   return;
}

//if(date==null) date = ExLastTradeDate.getLastTradeDate(ks,"tse");
//date = "20130206";
//System.out.println("========="+date);
String[] chs = ex_ch.split("\\|");


String sessionKey = "";
List<String> keys = new Vector<String>();
List<String> symbols = new Vector<String>();
Hashtable<String,String> dateHash = new Hashtable<String,String>();
Hashtable<String,Map<String,String>> symbolsRows = new Hashtable<String,Map<String,String>>();
for(int i = 0 ; i < chs.length ; i++){
  String rdate = "";
  String key = chs[i];
  if(key.split("_").length==2){
    if(date!=null) rdate = date;
    else {
      String ex = key.split("_")[0];
      rdate = dateHash.get(ex);
      if(rdate==null) rdate = ExLastTradeDate.getLastTradeDate(null,ex);
      dateHash.put(ex,rdate);
    }
    key = chs[i]+"_"+rdate;
  }
  //if(!lang.equals("zh-tw"))
  {
      String symbo = Utility.filiterString(key.split("_")[1], ".tw");
      StockNameStore sns = new StockNameStore(rdate,symbo);
      symbolsRows.put(symbo, sns.getMap());
      symbols.add(symbo);
  }
  keys.add(key);
  sessionKey+=(key+"|");
}


UserSession us = null;
us = (UserSession)session.getAttribute(sessionStr);
if(us==null) us = new UserSession(session.getId());

long startTime = us.getLatestMillis(sessionKey);
//List<StockInfo> infoList = StockInfoManager.getStockInfoList(keys,startTime,30000);
List<StockInfo> infoList = StockInfoManager.getStockInfoList(keys,sessionKey,us,30000,Utility.parseLong(delay));
//System.out.println("infoList==========:"+infoList.size());
JSONArray ja = new JSONArray();

long tlong2 = us.getLatestMillis(sessionKey);
for(int inx = 0 ; inx < infoList.size() ; inx++){

   StockInfo sinfo = infoList.get(inx);
   if(sinfo==null) continue;
   //System.out.println("sinfo==========:"+sinfo);
   long tlong = sinfo.getChange();
   if(tlong2 <tlong){
      tlong2 = tlong;
   }
   //System.out.println("infoList_tlong==========:"+tlong);
   List<Map.Entry<String,String>> cols = sinfo.getStockDetail();
   JSONObject j1 = new JSONObject();
   //System.out.println("infoList_cols==========:"+cols.size());
   // StockDetail
   for (int j = 0; j < cols.size(); j++) {
	  Map.Entry<String,String> hc =  cols.get(j);
      String val = "";
      String name = "";
      val = hc.getValue().toString();
      name = hc.getKey().toString();
      if(name.equals("it")){
         user_delay = StockInfoManager.getUserDelayMillis(val);
      }
      j1.put(name,val);
   }
j1.remove("it");
j1.remove("i");
j1.remove("ip");
j1.remove("p");
j1.remove("ex");
j1.remove("u");
j1.remove("w");
   // StockQuote
   List<Map.Entry<String,String>> schs = sinfo.getStockQuote();
   //System.out.println("infoList_schs==========:"+schs.size());
   for (int i = 0; i < schs.size(); i++) {
	   Map.Entry<String,String> hc = schs.get(i);
      String val = "";
      String name = "";
      val = hc.getValue().toString();
      name = hc.getKey().toString();
      if (name.equals("o") || name.equals("h") || name.equals("l") || name.equals("z") || name.equals("t") || name.equals("y") )
      j1.put(name,val);
    }
    //Bid Time
    String bidTime = j1.optString("bt");
    if(bidTime==null)bidTime="";
    if(!bidTime.equals("")){
       j1.put("t",bidTime);
       j1.remove("bt");
    }
    //Lang
    String ch = j1.getString("ch");
    if(ch!=null){
       ch = Utility.filiterString(ch,".tw");
       Map<String,String> symbolRow = symbolsRows.get(ch);
       if(!lang.equals("zh-tw")){
	  if(symbolRow!=null){
         String hcol = symbolRow.get("en");
	     if(hcol!=null){
	        j1.put("n",hcol);
	     }
	     hcol = symbolRow.get("ef");
	     if(hcol!=null){
	        j1.put("nf",hcol);
	     }
             hcol = symbolRow.get("enu");
	     if(hcol!=null){
	        j1.put("nu",hcol);
	     }
             hcol = symbolRow.get("rch");
	     if(hcol!=null){
                String rch = hcol;
	        j1.put("rch",rch);
		hcol = symbolRow.get("ren");
		if(hcol!=null){
			j1.put("rn",hcol);
		} else j1.put("rn",rch);
	     }
	  }
       } else {
	  if(symbolRow!=null){
             String hcol = symbolRow.get("cnu");
	     if(hcol!=null){
	        j1.put("nu",hcol);
	     }
	     hcol = symbolRow.get("cf");
	     if(hcol!=null){
		     j1.put("nf",hcol);
	     }
             hcol = symbolRow.get("rch");
	     if(hcol!=null){
                String rch = hcol;
	        j1.put("rch",rch);
		hcol = symbolRow.get("rcn");
		if(hcol!=null){
			j1.put("rn",hcol);
		} else j1.put("rn",rch);
	     }
	  }
       }
    }
    //StockTicker
/*
    String tickKey0 = j1.optString("tk0");
    String tickKey1 = j1.optString("tk1");
    if(tickKey0==null) tickKey0 = "";
    if(tickKey1==null) tickKey1 = "";
    String[] tickKey = new String[]{tickKey0,tickKey1};
    String[] treads = sinfo.getTrade(tickKey);

    if(treads.length==2){
	    //j1.put("t",treads[0]);
	    j1.put("tv",treads[1]);
    } else {
	    j1.put("tv","-");
    }

    String c = j1.optString("c");
    if(c.equals("")){
      j1.put("c",ch);
    }
*/
    ja.put(j1);

   }

   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("sysDate",Utility.getDateStr());
   jqueryTime.put("sysTime",Utility.getSQLTimeStr());
//   jqueryTime.put("showChart",StockInfoManager.isShowChart());
//   jqueryTime.put("stockInfo",StockInfoManager.getQueryMicroTime());
//   jqueryTime.put("stockInfoItem",StockInfoManager.getProductSize());
//   jqueryTime.put("sessionKey",sessionKey);
//   jqueryTime.put("sessionStr",sessionStr);
//   jqueryTime.put("sessionFromTime",startTime);
//   jqueryTime.put("sessionLatestTime",us.getLatestMillis(sessionKey));
   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("msgArray", ja);
   j.put("queryTime", jqueryTime);
//   j.put("referer", referer);
   out.print(j.toString());
   us.setLatestMillis(sessionKey,tlong2);
   us.setCount(sessionKey);
   session.setAttribute(sessionStr,us);
   if(true) return;
%>
