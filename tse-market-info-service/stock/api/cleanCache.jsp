<%@ page contentType="text/html; charset=UTF-8"%><%@ page
import="org.json.*"%><%@ page
import="com.ecloudlife.util.*"%><%@ page
import="com.ecloudlife.cass.logicutil.*"%><%

  String reset = Utility.getHttpParameter(request,"reset"); // request.getParameter("lang");

  String rdate = "";
  
  JSONObject je = new JSONObject();
  je.put("version",InitSchedule.version);
  je.put("rtcode","0000");
  je.put("rtmessage","OK");
  if(reset!=null){
     je.put("stockNameClean",com.ecloudlife.cass.logicutil.StockCategory.stnameHash.size());
     com.ecloudlife.cass.logicutil.StockCategory.stnameHash.clear();
     rdate = InitSchedule.init("cleanCache",true);
  } else  rdate = InitSchedule.init("cleanCache",false);
  je.put("cate_symbols",com.ecloudlife.cass.logicutil.StockCategory.symbolsRows.size());
  je.put("cate_update",com.ecloudlife.cass.logicutil.StockCategory.updated_cnt);
  je.put("cate_millis",com.ecloudlife.cass.logicutil.StockCategory.queryMillisTime);
  je.put("date",rdate);
  
  out.print(je.toString());
  if(true) return;

%>