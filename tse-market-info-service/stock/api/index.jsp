<%@ page contentType="text/html; charset=UTF-8" %><%@ page
import="com.ecloudlife.cass.logicutil.*" %><%@ page
import="com.ecloudlife.util.*" %><%@ page
import="com.proco.cache.JedisManager" %><%@ page
import="java.text.*" %><%@ page
import="java.util.*" %><%@ page
errorPage="/api/error.jsp" %>
<html>
<head>
<title>
Errpr 404
</title>
</head>
<body bgcolor="#ffffff">
<h1>
404
</h1>
</body>
</html>
<%!
public void jspInit() {
   ServletContext application = this.getServletContext();
   if(ConfigData.cassMaxActive == -1)
   ConfigData.cassMaxActive = Utility.parseInt(application.getInitParameter("cassMaxActive"));
   if(ConfigData.connectTimeout == -1)
   ConfigData.connectTimeout = Utility.parseInt(application.getInitParameter("connectTimeout"));
   if(ConfigData.cassHosts == null){
     ConfigData.cassHosts = application.getInitParameter("cassHosts").split("\\|");
   }

   if(ConfigData.cassPasswd == null)
   ConfigData.cassPasswd = application.getInitParameter("cassPasswd");
   if(ConfigData.clearTime == null)
   ConfigData.clearTime = application.getInitParameter("clearTime");

   String timelineCacheHours = application.getInitParameter("timelineCacheHours");
   if(timelineCacheHours!=null){
	   int hours = Utility.parseInt(timelineCacheHours);
	   if(hours>0) ConfigData.timelineCacheHours = hours;
   }
   
   if(ConfigData.production == null)
   ConfigData.production = application.getInitParameter("production");
   if(ConfigData.production == null) ConfigData.production = "1";

   if(ConfigData.siteName == null)
   ConfigData.siteName = application.getInitParameter("siteName");
   if(ConfigData.siteName == null) ConfigData.siteName = "";

   if(ConfigData.ohlcPatchTime == -1)
   ConfigData.ohlcPatchTime = Utility.parseInt(application.getInitParameter("ohlcPatchTime"));

   JedisManager.init(ConfigData.cassHosts[0],ConfigData.cassPasswd);
   StockInfoManager.main(new String[0]);
   OddInfoManager.main(new String[0]);
   StocksTimelineManager.main(new String[0]);
   OtOhlcManager.main(new String[0]);
   com.ecloudlife.cass.logicutil.StockCategory.main(new String[0]); 
   //StockStatisManager.main(new String[0]);
   System.out.println("TSE MIS System Start. "+InitSchedule.version);
   System.out.println("cassHosts:"+ConfigData.cassHosts[0]);
   System.out.println("cassMaxActive:"+ConfigData.cassMaxActive);
   System.out.println("connectTimeout:"+ConfigData.connectTimeout);
   System.out.println("timelineCacheHours:"+ConfigData.timelineCacheHours);
   System.out.println("TSE MIS System inited.");
   
   
   String rpath = application.getRealPath("/WEB-INF/"); //request.getRealPath("/WEB-INF/");
   System.out.println("Setup esapiConfig -----> org.owasp.esapi.resources="+rpath);
   System.setProperty("org.owasp.esapi.resources", rpath);  
   
   
   //ReInit Process
   String[] clearTimes = ConfigData.clearTime.split(",");
   for(String clearTime : clearTimes)
   try {
   SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
   long tomorrow = System.currentTimeMillis() + (86400000);
   long today = System.currentTimeMillis();

   //java.util.Date date1 = sdf.parse(Utility.getDateStr(today)+ConfigData.clearTime);
   java.util.Date date1 = sdf.parse(Utility.getDateStr(today)+clearTime);
   long stop =  date1.getTime();
   if(stop<today)
   date1 = sdf.parse(Utility.getDateStr(tomorrow)+clearTime);

   Timer timer1 = new Timer();
   InitSchedule etfup = new InitSchedule();
   timer1.schedule(etfup,date1, 24 * 60 * 60 * 1000);
   System.out.println("ReInit Cache Time is '"+date1.toString()+"'");
   } catch (Exception ex){
   }
/*
if(ConfigData.pTitle == null)
ConfigData.pTitle = application.getInitParameter("pTitle");
}
*/

}
%>
