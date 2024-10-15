<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.ecloudlife.util.UserSession" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %>

<%@ include file="../../common-utils.jsp" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

// FIX : Session Fixation
session = changeSessionIdentifier(request);

String sessionStr = "UserSession";
//long user_delay = 0;
String ex_ch = Utility.getHttpParameter(request,"ex_ch"); // request.getParameter("ex_ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String lang = Utility.getHttpParameter(request,"lang"); // request.getParameter("lang");
String delay = Utility.getHttpParameter(request,"delay"); // request.getParameter("delay");
String referer = purifyString(request.getHeader("Referer"), "", 200);
if(referer==null) referer = "";
if(delay==null) delay = "0";
if(lang==null)
lang = (String)session.getAttribute("lang");
if(lang==null) {
      //lang = request.getHeader("Accept-Language");
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

if(ex_ch==null){
   JSONObject j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","參數不足");
   out.print(j.toString());
   return;
}
String ex_key = "if_"+ex_ch+"_"+lang+"."+date;
Long ct0 = OddInfoManager.resCacheTime.get(ex_key);
if(ct0!=null){
  if((System.currentTimeMillis()-ct0.longValue())>3000){
      runProc(false,ex_key, ex_ch, date,lang,delay,referer, session,out);
  }
  JSONObject j1 = OddInfoManager.resCacheObject.get(ex_key);
  if(j1!=null){
    j1.put("cachedAlive",(System.currentTimeMillis()-ct0.longValue()));
    j1.put("exKey",ex_key);
    out.print(j1.toString());
    return;
  } else {
  }
}
runProc(true,ex_key, ex_ch, date,lang,delay,referer, session,out);
if(true) return;

%><%!

public static void runProc(final boolean sync0,final String ex_key, final String ex_ch,
final String date,final String lang,final String delay,
final String referer,final HttpSession session, final JspWriter out) throws Exception {

if(OddInfoManager.execJspSet.contains(ex_key)) return;
else OddInfoManager.execJspSet.add(ex_key);

Runnable run = new Runnable(){
   public void run(){
     try{
       long user_delay = 0;
       String sessionStr = "UserSession";

       //if(date==null) date = ExLastTradeDate.getLastTradeDate(ks,"tse");
       //date = "20130206";
       //System.out.println("========="+date);
       String[] chs = ex_ch.split("\\|");

       List <String> XmIndex = new Vector <String> ();
       XmIndex.add("IX0103");
       XmIndex.add("IX0108");
       XmIndex.add("IX0109");
       XmIndex.add("IX0125");
       XmIndex.add("IR0129");

       String sessionKey = "";
       List<String> keys = new Vector<String>();
       List<String> symbols = new Vector<String>();
       Hashtable<String,String> dateHash = new Hashtable<String,String>();
       Hashtable<String,Map<String,String>> symbolsRows = new Hashtable<String,Map<String,String>>();
       for(int i = 0 ; i < chs.length ; i++){
         String rdate = "";
         String key = chs[i];
         String[] subkeys = key.split("_");
         if(subkeys.length>=2){
           //if(date!=null) rdate = date;
           //else
           {
             String ex = subkeys[0];
             rdate = dateHash.get(ex);
             if(rdate==null) rdate = ExLastTradeDate.getLastTradeDate(null,ex);
             dateHash.put(ex,rdate);
           }
           //System.out.println("1------>chs[i]:"+chs[i]+" date:"+date+" rdate:"+rdate+" "+key.split("_").length);
           key = subkeys[0]+"_"+subkeys[1]+"_"+rdate;
         }
         //System.out.println("2------>"+key+" "+date);
         /*
         if(key.split("_").length==2){
           if(date!=null) rdate = date;
           else {
             String ex = key.split("_")[0];
             rdate = dateHash.get(ex);
             if(rdate==null) rdate = ExLastTradeDate.getLastTradeDate(ks,ex);
             dateHash.put(ex,rdate);
           }
           key = chs[i]+"_"+rdate;
         }*/
         //if(!lang.equals("zh-tw"))
         {
           String symbo = Utility.filiterString(key.split("_")[1],".tw");
           symbols.add(symbo);
        	
           StockNameStore sns = new StockNameStore(rdate,symbo);
           symbolsRows.put(symbo, sns.getMap());

         }
         keys.add(key);
         sessionKey+=(key+"|");
       }
       //if(!lang.equals("zh-tw"))
       {
         //StockNameStore sns = new StockNameStore("");
         //symbolsRows = sns.getRowKeysHash(ks,symbols);
       }

       UserSession us = null;
       us = (UserSession)session.getAttribute(sessionStr);
       if(us==null) us = new UserSession(session.getId());

       long startTime = us.getLatestMillis(sessionKey);
       List<OddInfo> infoList = OddInfoManager.getOddInfoList(keys,sessionKey,us,30000,Utility.parseLong(delay));
       //System.out.println("infoList==========:"+infoList.size());
       JSONArray ja = new JSONArray();

       long tlong2 = us.getLatestMillis(sessionKey);
       for(int inx = 0 ; inx < infoList.size() ; inx++){

         OddInfo sinfo = infoList.get(inx);
         if(sinfo==null) continue;
         //System.out.println("sinfo==========:"+sinfo);
         long tlong = sinfo.getChange();
         if(tlong2 <tlong){
           tlong2 = tlong;
         }
         //System.out.println("infoList_tlong==========:"+tlong);
         List<Map.Entry<String,String>> cols = sinfo.getOddDetail();
         JSONObject j1 = new JSONObject();
         //System.out.println("infoList_cols==========:"+cols.size());
         // StockDetail
         for (int j = 0; j < cols.size(); j++) {
       		Map.Entry<String,String> hc = cols.get(j);
           String val = "";
           String name = "";
           val = hc.getValue();
           name = hc.getKey();
           if(name.equals("it")){
             user_delay = OddInfoManager.getUserDelayMillis(val);
           }
           j1.put(hc.getKey(),val);
         }
         // StockQuote
         List<Map.Entry<String,String>> schs = sinfo.getOddQuote();
         //System.out.println("infoList_schs==========:"+schs.size());
         for (int i = 0; i < schs.size(); i++) {
        	 Map.Entry<String,String> hc = (Map.Entry)schs.get(i);
           String val = "";
           String name = "";
           val = hc.getValue().toString();
           name = hc.getKey().toString();
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
         String ch = j1.optString("ch");
		 if (!ch.equals("")) {
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
                 String rch = (String)hcol;
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
         j1.remove("tk0");
         j1.remove("tk1");
         String[] tickKey = new String[]{tickKey0,tickKey1};
         String[] treads = sinfo.getTrade(tickKey);
         if(treads.length==2){
           //j1.put("t",treads[0]);
           j1.put("tv",treads[1]);
         } else {
           j1.put("tv","-");
         }*/
         //added by format 20
         String zz = j1.optString("z","0");
         String ss = j1.optString("s","0");
         String idx = j1.optString("i","0");
         //if(zz.equals("0") && ss.equals("0")){
           if(idx.equals("oidx.tw") || idx.equals("tidx.tw")){
             if(ss.equals("0") && zz.equals("0")){
               //j1.put("z","-");
               j1.put("s","-");
               j1.put("tv","-");
             }
           } else
           if(ss.equals("0")){
             //j1.put("z","-");
             j1.put("s","-");
             j1.put("tv","-");
           }
           //String hh = j1.optString("h","0");
           //String oo = j1.optString("o","0");
           //String ll = j1.optString("l","0");
           //if(hh.equals("0.0000")||hh.equals("0.00")) j1.put("h","-");
           //if(oo.equals("0.0000")||oo.equals("0.00")) j1.put("o","-");
           //if(ll.equals("0.0000")||ll.equals("0.00")) j1.put("l","-");
           //ended by format 20
           String c = j1.optString("c");
           if(c.equals("")){
             j1.put("c",ch);
           }
           for (int i = 0; i < XmIndex.size(); i++) {
             if (XmIndex.get(i).equals(c)) {
               j1.put("xm", "1");
             }
           }

           ja.put(j1);
         }

         JSONObject jqueryTime = new JSONObject();
         jqueryTime.put("sysDate",Utility.getDateStr());
         jqueryTime.put("sysTime",Utility.getSQLTimeStr());
         jqueryTime.put("showChart",OddInfoManager.isShowChart());
         jqueryTime.put("oddInfo",OddInfoManager.getQueryMillisTime());
         jqueryTime.put("oddInfoItem",OddInfoManager.getProductSize());
         //jqueryTime.put("sessionKey",sessionKey);
         jqueryTime.put("sessionStr",sessionStr);
         jqueryTime.put("sessionFromTime",startTime);
         jqueryTime.put("sessionLatestTime",us.getLatestMillis(sessionKey));
         JSONObject j = new JSONObject();
         j.put("rtcode","0000");
         j.put("rtmessage","OK");
         j.put("msgArray", ja);
         j.put("queryTime", jqueryTime);
         j.put("referer", referer);
         {
           long delays = user_delay;
           if(delays==0) delays =5000;
           j.put("userDelay", delays);
         }
         if(sync0)out.print(j.toString());
         us.setLatestMillis(sessionKey,tlong2);
         us.setCount(sessionKey);
         session.setAttribute(sessionStr,us);
         if(!ja.isEmpty()){
           OddInfoManager.resCacheTime.put(ex_key, new Long(System.currentTimeMillis()));
           OddInfoManager.resCacheObject.put(ex_key,j);
         } else {
           OddInfoManager.resCacheTime.remove(ex_key);
           OddInfoManager.resCacheObject.remove(ex_key);
         }
         OddInfoManager.execJspSet.remove(ex_key);

       } catch (Exception ex){}
     }
};

if(sync0) run.run();
else {
  OddInfoManager.jspUpdate.submit(run);
}

}
%>
