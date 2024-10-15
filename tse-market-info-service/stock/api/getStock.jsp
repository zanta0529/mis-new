<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"   errorPage="/api/error.jsp" %><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);

String ex = Utility.getHttpParameter(request,"ex"); // request.getParameter("ex");
String is = Utility.getHttpParameter(request,"i"); // request.getParameter("i");
String it = Utility.getHttpParameter(request,"it"); // request.getParameter("it");
String io = Utility.getHttpParameter(request,"io"); // request.getParameter("io");
String ip = Utility.getHttpParameter(request,"ip"); // request.getParameter("ip");
String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String name = Utility.getHttpParameter(request,"n"); // request.getParameter("n");

if(ex==null && is==null&& io==null&& ip==null&& it==null && ch==null&& name==null){
	JSONObject j = new JSONObject();
	j.put("rtcode","9999");
	j.put("rtmessage","參數不足");
	out.print(j.toString());
	return;
}

String ex_key = "st_"+ex+"_"+is+"."+it+"."+io+"."+ip+"."+ch+"."+date+"."+name;
Long ct0 = StockInfoManager.resCacheTime.get(ex_key);
if(ct0!=null){
  if((System.currentTimeMillis()-ct0.longValue())<30000){
    JSONObject j1 = StockInfoManager.resCacheObject.get(ex_key);
    if(j1!=null){
      j1.put("cachedAlive",(System.currentTimeMillis()-ct0.longValue()));
      j1.put("exKey",ex_key);
      out.print(j1.toString());
      return;
    }
  } else {
    StockInfoManager.resCacheTime.remove(ex_key);
    StockInfoManager.resCacheObject.remove(ex_key);
  }
}


if(ex==null) ex = "tse";
if(date==null) date = ExLastTradeDate.getLastTradeDate(null,ex);

List<Map<String,String>> rList = new Vector<Map<String,String>>();
if(ch!=null){
  String key0 = "tse_"+ch+"_"+date;
  String key1 = "otc_"+ch+"_"+date;
  String key2 = "taifex_"+ch+"_"+date;
  java.util.Vector<String> keys = new Vector<String>();
  keys.add(key0);
  keys.add(key1);
  keys.add(key2);
  StockDetail sd0 = new StockDetail(date,key0);
  StockDetail sd1 = new StockDetail(date,key1);
  StockDetail sd2 = new StockDetail(date,key2);
  rList.add(sd0.getMap());
  rList.add(sd1.getMap());
  rList.add(sd2.getMap());

  //rList = sd.getRowKeys(ks,keys);
} else {
   /*
   java.util.Hashtable<String,String> aa = new Hashtable<String,String>();
   if(date!=null)
   aa.put("d",date);
   if(name!=null){
	   name = new String(name.getBytes("ISO_8859_1"),"UTF-8");
	   aa.put("n",name);
   }
   if(ex!=null)
   aa.put("ex",ex);
   if(is!=null)
   aa.put("i",is);
   if(it!=null)
   aa.put("it",it);
   if(io!=null)
   aa.put("io",io);
   if(ip!=null)
   aa.put("ip",ip);
   if(ch!=null)
   aa.put("ch",ch);
   StockDetail sd = new StockDetail(date,key0);
   rList = sd.getIndex(ks,aa,null,null,null,null); */
}
JSONArray ja = new JSONArray();
for(int i = 0 ; i < rList.size() ; i++){

   JSONObject j1 = new JSONObject();
   Map<String,String> row = rList.get(i);

//   j1.put("key",row.getKey());

   String tKey=row.get("key");
   if(tKey==null) continue;
   if(tKey.indexOf(".tw_20")!=-1)
       j1.put("key",tKey.substring(0,tKey.length()-9));
   else
       j1.put("key",tKey);

   Set<String> keys = row.keySet();
   for(String key : keys){
	   String val = row.get(key);
	   if(val!=null)
	   j1.put(key,val);
   }
    //System.out.println(hc.getName().toString() + " " + val);
	//rt.put(hc.getName().toString(), val);
  
  ja.put(j1);
}

   JSONObject jqueryTime = new JSONObject();
   jqueryTime.put("stockDetail",0);
   jqueryTime.put("totalMicroTime",0);

   JSONObject j = new JSONObject();
   j.put("rtcode","0000");
   j.put("rtmessage","OK");
   j.put("msgArray", ja);
   j.put("queryTime", jqueryTime);
   out.print(j.toString());
   if(!ja.isEmpty()){
     StockInfoManager.resCacheTime.put(ex_key, new Long(System.currentTimeMillis()));
     StockInfoManager.resCacheObject.put(ex_key,j);
   }
   if(true) return;
%>
