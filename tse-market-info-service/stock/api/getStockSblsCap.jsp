<%@ page contentType="text/html; charset=UTF-8"%><%@ page import="com.ecloudlife.util.Utility"%><%@ page
    import="com.proco.datautil.*"%><%@ page import="com.ecloudlife.cass.logicutil.*"%><%@ page 
    import="org.json.*"%><%@ page import="java.util.*"
    errorPage="/api/error.jsp"%>
<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
long user_delay = 5000;
String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
   JSONArray ja = new JSONArray();
   if(date==null) date = ExLastTradeDate.getLastTradeDate(null,"tse");   
   
   
   StockIoFile sio = new StockIoFile(date,"TH28");
   List<Map.Entry<String,String>> sList = sio.get();
   java.util.Vector<String> stknoList = new Vector<String>();
   java.util.Hashtable<String,JSONObject> stknoHash = new Hashtable<String,JSONObject>();
   for(int i = 0 ; i < sList.size() ; i++){
	  Map.Entry<String,String> hcol = sList.get(i);
      if(hcol.getKey().length()==8) continue;
      if(hcol.getKey().equals("key")) continue;
      JSONObject j1 = new JSONObject(hcol.getValue());
      String stk = j1.optString("stkno");
      String timeStr = j1.optString("txtime");
      if(timeStr.equals("")){
        j1.put("ts",0);
      } else {
        timeStr = timeStr.replaceAll(":","");
        timeStr = timeStr.replaceAll("\\.","");
        j1.put("ts",Utility.parseInt(timeStr));
      }

      stknoList.add(stk);
      stknoHash.put(stk,j1);
      //ja.put(j1);
   }
  sio = new StockIoFile(date,"OH28");
  sList = sio.get();
  for(int i = 0 ; i < sList.size() ; i++){
	  Map.Entry<String,String> hcol = sList.get(i);
      if(hcol.getKey().length()==8) continue;
      if(hcol.getKey().equals("key")) continue;
      JSONObject j1 = new JSONObject(hcol.getValue());
      String stk = j1.optString("stkno");
      String timeStr = j1.optString("txtime");
      if(timeStr.equals("")){
        j1.put("ts",0);
      } else {
        timeStr = timeStr.replaceAll(":","");
        timeStr = timeStr.replaceAll("\\..","");
        j1.put("ts",Utility.parseInt(timeStr));
      }

      stknoList.add(stk);
      stknoHash.put(stk,j1);
      //ja.put(j1);
   }
   Object[] stks = stknoList.toArray();
   Arrays.sort(stks);
   for(Object stk : stks)
     ja.put(stknoHash.get(stk));

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
   out.print(j.toString());
   if(true) return;

%>
