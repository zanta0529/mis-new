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

String date = Utility.getHttpParameter(request,"d"); // request.getParameter("d");
String ch = Utility.getHttpParameter(request,"ch"); // request.getParameter("ch");
String lang = Utility.getHttpParameter(request,"lang"); // request.getParameter("lang");

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

if(ch==null){
   JSONObject j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","參數不足");
   out.print(j.toString());
   return;
}

//運算交易資訊

JSONObject j = new JSONObject();
StockIoFile sioTh05 = new StockIoFile(date,"TH05");
List<Map.Entry<String,String>> sList = sioTh05.get();
for(int i = 0 ; i < sList.size() ; i++){
	Map.Entry<String,String> hcol = sList.get(i);
   	if(hcol.getKey().equals("key")) continue;
   	//if(hcol.getKey().length() < 10) continue;
   	//System.out.println(hcol.getKey()+" TH05======>>>"+hcol.getValue());
   	
    JSONObject j1 = new JSONObject(hcol.getValue());
    if( j1.has("msgObject")){
	    j= j1.getJSONObject("msgObject");
    }
}

if(!j.has(ch)){
   j = new JSONObject();
   j.put("rtcode","9999");
   j.put("rtmessage","輸入商品不在借券範圍。");
   out.print(j.toString());
   return;
} else {
   j = j.getJSONObject(ch);
   if(!lang.equals("zh-tw")){
      String stk = j.getString("stkno");
      StockNameStore sns = new StockNameStore(date,stk);
      Map<String,String> syList = sns.getMap();
      String en = syList.get("en");
      if(en!=null) j.put("stkname",en);
   }
}

StockIoFile sio = new StockIoFile(date,"TH08");
sList = sio.get();
Hashtable<String,JSONObject> h08Hash = new Hashtable<String,JSONObject>();
for(int i = 0 ; i < sList.size() ; i++){
   Map.Entry<String,String> hcol = sList.get(i);
   if(hcol.getKey().equals("key")) continue;
   if(hcol.getKey().length() < 10) continue;
   if(!hcol.getKey().startsWith(ch+"_")) continue;
   System.out.println(hcol.getKey()+" TH08======>>>"+hcol.getValue());
   JSONObject j1 = new JSONObject(hcol.getValue());
   String tradetype = j1.getString("tradetype");
   String returnopt = j1.getString("returnopt");
   JSONObject j0 = h08Hash.get(tradetype+returnopt);
   if(j0==null) {
      j1.put("totmatchqty","0");
      j0 = j1;
   }
   String totmatchqty = "";
   if(j0.has("totmatchqty")){
      totmatchqty = j0.getString("totmatchqty");
   }
   if(!totmatchqty.equals("")){
      totmatchqty = ""+(Utility.parseInt(totmatchqty)+Utility.parseInt(j1.getString("matchqty")));
   } else {
      totmatchqty = ""+(Utility.parseInt(j1.getString("matchqty")));
   }
   j1.put("totmatchqty",totmatchqty);
   h08Hash.put(tradetype+returnopt,j1);

}

JSONObject sFA = h08Hash.get("FA");
JSONObject sF3 = h08Hash.get("F3");
JSONObject sF1 = h08Hash.get("F1");
JSONObject sCA = h08Hash.get("CA");
JSONObject sC3 = h08Hash.get("C3");
JSONObject sC1 = h08Hash.get("C1");
JSONObject sN = h08Hash.get("N");

JSONObject j1 = new JSONObject();
j1.put("FA",getFHeaderSummary(sFA));
j1.put("F3",getFHeaderSummary(sF3));
j1.put("F1",getFHeaderSummary(sF1));
j1.put("CA",getCHeaderSummary(sCA));
j1.put("CA_5",getCSblsItems(sCA));
j1.put("C3",getCHeaderSummary(sC3));
j1.put("C3_5",getCSblsItems(sC3));
j1.put("C1",getCHeaderSummary(sC1));
j1.put("C1_5",getCSblsItems(sC1));
if(sN!=null){
   JSONObject j2 = new JSONObject();
   j2.put("totmatchqty",sN.getString("totmatchqty"));
   j2.put("time",Utility.getSQLTimeStr(sN.getString("time")));
   j1.put("N", j2);
} else {
   JSONObject j2 = new JSONObject();
   j2.put("totmatchqty","0");
   j2.put("time","");
   j1.put("N", j2);
}
j1.put("detail",j);
out.print(j1.toString());


%><%!
public static JSONObject getFHeaderSummary(JSONObject ss) throws Exception{
JSONArray jaFA = new JSONArray();
JSONObject s1 = new JSONObject();
String time = "";
if(ss!=null){
   int matchqty = Utility.parseInt(ss.getString("matchqty"));
   int totdebitqty = Utility.parseInt(ss.getString("totdebitqty"));
   int totcreditqty = Utility.parseInt(ss.getString("totcreditqty"));

   String a1 = Utility.parseInt(ss.getString("totmatchqty"))+"";
   String a2 = totcreditqty+"";
   String a3 = totdebitqty+"";
   String a4 = "3.5";

   if(matchqty>0){
      a4 = Utility.parseV9StrToX7Str(ss.getString("matchfare"),2);
   } else if(totdebitqty>0){
      a4 = Utility.parseV9StrToX7Str(ss.getString("matchfare"),2);
   } else if(totcreditqty>0){
      a4 = Utility.parseV9StrToX7Str(ss.getString("matchfare"),2);
   }
   jaFA.put(a1);
   jaFA.put(a2);
   jaFA.put(a3);
   jaFA.put(a4);
   time = ss.getString("time");
   time = Utility.getSQLTimeStr(time);
} else {
   jaFA.put("0");
   jaFA.put("");
   jaFA.put("");
   jaFA.put("");
}
s1.put("detail",jaFA);
s1.put("time",time);
return s1;
}

public static JSONObject getCHeaderSummary(JSONObject ss) throws Exception{
JSONObject s1 = new JSONObject();
JSONArray jaFA = new JSONArray();
String time = "";
if(ss!=null){
   String a1 = Utility.parseInt(ss.getString("totmatchqty"))+"";
   String a2 = Utility.parseInt(ss.getString("totcreditqty"))+"";
   String a3 = Utility.parseInt(ss.getString("totdebitqty"))+"";
   String a4 = Utility.parseInt(ss.getString("matchqty"))+"";
   String a5 = Utility.parseV9StrToX7Str(ss.getString("matchfare"),2);
   time = ss.getString("time");
   time = Utility.getSQLTimeStr(time);
   jaFA.put(a1);
   jaFA.put(a2);
   jaFA.put(a3);
   jaFA.put(a4);
   jaFA.put(a5);
} else {
   jaFA.put("0");
   jaFA.put("");
   jaFA.put("");
   jaFA.put("");
   jaFA.put("");
}
s1.put("detail",jaFA);
s1.put("time",time);
return s1;
}

public static JSONObject getCSblsItems(JSONObject ss) throws Exception{
JSONArray jaFA = new JSONArray();
JSONObject s1 = new JSONObject();
String time = "";
if(ss!=null){
   String cf1 = ss.getString("creditfare1");
   String cq1 = ss.getString("creditqty1");
   String cf2 = ss.getString("creditfare2");
   String cq2 = ss.getString("creditqty2");
   String cf3 = ss.getString("creditfare3");
   String cq3 = ss.getString("creditqty3");
   String cf4 = ss.getString("creditfare4");
   String cq4 = ss.getString("creditqty4");
   String cf5 = ss.getString("creditfare5");
   String cq5 = ss.getString("creditqty5");

   String df1 = ss.getString("debitfare1");
   String dq1 = ss.getString("debitqty1");
   String df2 = ss.getString("debitfare2");
   String dq2 = ss.getString("debitqty2");
   String df3 = ss.getString("debitfare3");
   String dq3 = ss.getString("debitqty3");
   String df4 = ss.getString("debitfare4");
   String dq4 = ss.getString("debitqty4");
   String df5 = ss.getString("debitfare5");
   String dq5 = ss.getString("debitqty5");
   time = ss.getString("time");
   time = Utility.getSQLTimeStr(time);

   s1.put("cf1",Utility.parseV9StrToX7Str(cf1,2));
   s1.put("cq1",Utility.parseInt(cq1)+"");
   s1.put("cf2",Utility.parseV9StrToX7Str(cf2,2));
   s1.put("cq2",Utility.parseInt(cq2)+"");
   s1.put("cf3",Utility.parseV9StrToX7Str(cf3,2));
   s1.put("cq3",Utility.parseInt(cq3)+"");
   s1.put("cf4",Utility.parseV9StrToX7Str(cf4,2));
   s1.put("cq4",Utility.parseInt(cq4)+"");
   s1.put("cf5",Utility.parseV9StrToX7Str(cf5,2));
   s1.put("cq5",Utility.parseInt(cq5)+"");
   s1.put("df1",Utility.parseV9StrToX7Str(df1,2));
   s1.put("dq1",Utility.parseInt(dq1)+"");
   s1.put("df2",Utility.parseV9StrToX7Str(df2,2));
   s1.put("dq2",Utility.parseInt(dq2)+"");
   s1.put("df3",Utility.parseV9StrToX7Str(df3,2));
   s1.put("dq3",Utility.parseInt(dq3)+"");
   s1.put("df4",Utility.parseV9StrToX7Str(df4,2));
   s1.put("dq4",Utility.parseInt(dq4)+"");
   s1.put("df5",Utility.parseV9StrToX7Str(df5,2));
   s1.put("dq5",Utility.parseInt(dq5)+"");
} else {
   s1.put("cf1","");
   s1.put("cq1","");
   s1.put("cf2","");
   s1.put("cq2","");
   s1.put("cf3","");
   s1.put("cq3","");
   s1.put("cf4","");
   s1.put("cq4","");
   s1.put("cf5","");
   s1.put("cq5","");
   s1.put("df1","");
   s1.put("dq1","");
   s1.put("df2","");
   s1.put("dq2","");
   s1.put("df3","");
   s1.put("dq3","");
   s1.put("df4","");
   s1.put("dq4","");
   s1.put("df5","");
   s1.put("dq5","");
}
s1.put("time",time);
return s1;
}

%>
