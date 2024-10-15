<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.util.Utility" %><%@ page
import="com.proco.datautil.*" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="org.json.*" %><%@ page
import="java.util.*"  errorPage="/api/error.jsp" %><%!
    static java.util.concurrent.ConcurrentHashMap<String, JSONObject> resCacheObject =  new java.util.concurrent.ConcurrentHashMap<String, JSONObject>();
    static java.util.concurrent.ConcurrentHashMap<String, Long> resCacheTime =  new java.util.concurrent.ConcurrentHashMap<String, Long>();
%><%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
String lang =  Utility.getHttpParameter(request,"lang"); //request.getParameter("lang");
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
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
}
lang=lang.split(",")[0];
lang = lang.replaceAll("_","-");
if(date==null) date = ExLastTradeDate.getLastTradeDate(null,"tse");

   String ex_key  = "slbs_brk_" + "_" + lang +"_"+ date ;
   Long ct0 = resCacheTime.get(ex_key);
   if (ct0 != null) {
       JSONObject j1 = resCacheObject.get(ex_key);
       if ((System.currentTimeMillis() - ct0.longValue()) < 15000) {
           if (j1 != null) {
               j1.put("cachedAlive", (System.currentTimeMillis() - ct0.longValue()));
               j1.put("exKey",ex_key);
               out.print(j1.toString());
               return;
           }
       } else {
           if (j1 != null) {
               j1.put("cachedAlive", (System.currentTimeMillis() - ct0.longValue()));
               j1.put("exKey",ex_key);
               final String ex_key0 = ex_key;
               final String date0 = date;
               final String lang0 = lang;
               Thread th = new Thread(){
               	public void run(){
               		exec(ex_key0, date0, lang0);
               	}
               };
               th.start();
               out.print(j1.toString());
               return;
           }   
       	//StockInfoManager.resCacheTime.remove(ex_key);
        //StockInfoManager.resCacheObject.remove(ex_key);
       }
   }
   
   JSONObject j = exec(ex_key, date, lang);
   out.print(j.toString());
   if(true) return;

%><%!

public static JSONObject exec(String ex_key, String date, String lang){
	   Hashtable<String,Map<String,String>> symbolsRows = new Hashtable<String,Map<String,String>>();

	   JSONArray ja = new JSONArray();
	   StockIoFile sio = new StockIoFile(date,"TH33");
	   List<Map.Entry<String,String>> sList = sio.get();


	    Vector<String> symbolset = new Vector<String>();
	    Hashtable<String,JSONObject> symbols = new Hashtable<String,JSONObject>();
	    Vector<String> colSortList = new Vector<String>();
	    Hashtable<String, Map.Entry<String, String>>
	        colHash = new Hashtable<String, Map.Entry<String, String>> ();

	    for (Map.Entry<String, String> hcol : sList) {
	      if(hcol.getKey().equals("key")) continue;
	      try {
	        JSONObject j1 = new JSONObject(hcol.getValue());
	        JSONArray ja1 = j1.getJSONArray("sbrk");
	        String kk = "";
	        for (int i = 0; i < ja1.length(); i++) {
	          JSONObject j2 = ja1.getJSONObject(i);
	          String stkno = j2.getString("stkno");
	          kk = j2.getString("brkid")+"_"+hcol.getKey();
	          symbols.put(j2.getString("brkid")+"_"+stkno, j2);
	          symbolset.add(stkno);
	        
	          {
	       	   StockNameStore sns = new StockNameStore(date,stkno);
	       	   symbolsRows.put(stkno, sns.getMap());
	       	   //symbolsRows = sns.getRowKeysHash(ks,symbolset);
	          }
	        
	        }
	        colHash.put(kk, hcol);
	        colSortList.add(kk);
	      }
	      catch (JSONException ex1) {
	      }
	    }



	   Object[] cols = symbols.keySet().toArray(); //colSortList.toArray();
	   Arrays.sort(cols);
	   //for(HColumn<String,String> hcol : sList){
	  JSONArray ja2 = new JSONArray();
	   for(Object col : cols){

	     JSONObject j2 = symbols.get(col);
	     String stkno = j2.getString("stkno");
	     j2 = symbols.remove(j2.getString("brkid")+"_"+stkno);
	     if(j2==null) continue;

	     if(!lang.equals("zh-tw")){
	       Map<String,String> symbolRow = symbolsRows.get(stkno);
	       if(symbolRow==null) continue;
	       String hcolx = symbolRow.get("en");
	       if(hcolx==null) continue;
	       j2.put("stkname",hcolx);
	     }
	     ja2.put(j2);
	   }

	      /*
	      if(!lang.equals("zh-tw")){
	         for(int i = 0 ; i < ja1.length() ; i++){
	            JSONObject j2 = ja1.getJSONObject(i);
		    String stkno = j2.getString("stkno");
		    Row<String,String,String> symbolRow = symbolsRows.get(stkno);
		    if(symbolRow==null) continue;
		    HColumn hcolx = symbolRow.getColumnSlice().getColumnByName("en");
		    if(hcolx==null) continue;
		    j2.put("stkname",hcolx.getValue());
		    ja2.put(j2);
	         }
	      } else {
	         ja2 = ja1;
	      }*/

	      ja.put(ja2);

	   //okok
	   long user_delay = 5000;
	   user_delay = StockInfoManager.getUserDelayMillis("sb");
	   if (user_delay < 5000) {
	       user_delay = 5000;
	   }
	   JSONObject j = new JSONObject();
	   j.put("size",ja.length());
	   j.put("rtcode","0000");
	   j.put("rtmessage","OK");
	   j.put("msgArray", ja);
	   j.put("queryTime", sio.getQueryTime());
	   j.put("userDelay", user_delay);
       if (!ja.isEmpty()) {
           resCacheTime.put(ex_key, new Long(System.currentTimeMillis()));
           resCacheObject.put(ex_key,j);
       }
	   return j;
}

%>
