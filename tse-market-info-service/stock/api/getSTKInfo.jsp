<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp"%>

<%@ page import="java.io.BufferedReader"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.FileReader"%>
<%@ page import="java.io.InputStreamReader"%>
<%@ page import="java.security.SecureRandom"%>
<%@ page import="java.util.*"%>
<%@ page import="org.json.*"%>
<%@ page import="com.ecloudlife.util.Utility"%>
<%@ page import="com.ecloudlife.util.UserSession"%>
<%@ page import="com.proco.datautil.*"%>
<%@ page import="com.ecloudlife.cass.logicutil.*"%>

<%@ include file="../../common-utils.jsp"%>

<%
//response.setHeader("Access-Control-Allow-Origin","*");

if (request.getMethod().equals("POST")) {
    Thread.currentThread().sleep(1300);
    response.sendRedirect("http://1.1.1.6");
    return;
}

String timekey = Utility.getHttpParameter(request, "_");
if ((timekey == null) || timekey.endsWith("000")) {
    return;
}

//Block IP

String denyIP_List = "";
long denyIP_Time = 0L;
String thisIP = "";
try {
    denyIP_List = (String)application.getAttribute("denyIP_List");
    denyIP_Time = (Long)application.getAttribute("denyIP_Time");
} catch (Exception e) {
    denyIP_List = "";
    denyIP_Time = 0L;
}

if ((System.currentTimeMillis() - denyIP_Time) > 1000 * 60 * 1) {
    BufferedReader reader = null;
    try {
        application.setAttribute("denyIP_Time", System.currentTimeMillis());
        String txtFilePath = getServletConfig().getServletContext().getRealPath("/api/denyIP.txt");
        reader = new BufferedReader(new FileReader(txtFilePath));
        StringBuilder sb = new StringBuilder();
        String line;
        int cutTo = 0;
        while ((line = reader.readLine()) != null) {
            if (line.indexOf("#") >= 0) {
                cutTo = line.indexOf("#");
            } else if (line.indexOf("//") >= 0) {
                cutTo = line.indexOf("//");
            } else {
                cutTo = line.length();
            }

            sb.append(line.substring(0, cutTo).trim() + "\n");
        }
        application.setAttribute("denyIP_List", sb.toString());
    } catch (Exception e) {
        application.setAttribute("denyIP_List", "");
    }
    finally {
        if (reader != null) {
            reader.close();
        }
    }
}

thisIP = (String) session.getAttribute("thisIP");
if (thisIP == null) {
    thisIP = purifyString(request.getHeader("x-forwarded-for"), "", 50);
    if (thisIP == null || thisIP.length() == 0 || "unknown".equalsIgnoreCase(thisIP)) {
        thisIP = purifyString(request.getHeader("Proxy-Client-IP"), "", 50);
    }
    if (thisIP == null || thisIP.length() == 0 || "unknown".equalsIgnoreCase(thisIP)) {
        thisIP = purifyString(request.getHeader("WL-Proxy-Client-IP"), "", 50);
    }
    if (thisIP == null || thisIP.length() == 0 || "unknown".equalsIgnoreCase(thisIP)) {
        thisIP = purifyString(request.getRemoteAddr(), "", 50);
    }
    session.setAttribute("thisIP", thisIP);
}

if ((denyIP_List.length()) > 0 && denyIP_List.indexOf(thisIP) >= 0) {
    Thread.currentThread().sleep(1000);
    response.sendRedirect("http://1.1.1.5");
    return;
}
// Block IP
%>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires", 0);
String sessionStr = "UserSession";
long user_delay = 5000;
String ex_ch = Utility.getHttpParameter(request, "ex_ch"); // request.getParameter("ex_ch");
String date = Utility.getHttpParameter(request, "d"); // request.getParameter("d");
//String lang = Utility.getHttpParameter(request,"lang"); // request.getParameter("lang");
String delay = Utility.getHttpParameter(request, "delay"); // request.getParameter("delay");
String referer = purifyString(request.getHeader("Referer"), "", 100);
String cp = Utility.getHttpParameter(request, "cp"); //request.getParameter("cp");
String lang = Utility.getHttpParameter(request, "lang");
if (lang == null) {
    lang = (String) session.getAttribute("lang");
}

if (referer == null) {
    referer = "/";
} else {
    referer = referer.replaceAll("<", "").replaceAll(">", "").replaceAll("(?i)script", "").replaceAll("(?i)alert", "").replaceAll("'", "");
}

String signed = (String) session.getAttribute("signed");

String itemsORG[] = (String[]) session.getAttribute("itemsORG");
String itemsNEW[] = (String[]) session.getAttribute("itemsNEW");
String itemsREG[] = (String[]) session.getAttribute("itemsREG");

//回傳總長度：
//26 - 未經首頁，直讀資料
//27 - 網頁過久未切換
//28 - 頻繁抓取的次數超限
//29 - 盲目抓取股票代號 (tse_1101 otc_1101)
//30 - 一次抓取超過 100 檔
//31 - 帶日期抓歷史資料

if (signed == null) {
    JSONObject j = new JSONObject();
    j.put("id", "0");
    j.put("rtcode", "4444");
    out.print(j.toString());
    return;
}

//換頁-紀錄JSP網頁停留時間
if (cp != null && cp.equals("1")) {
    session.setAttribute("last_jsp_time", System.currentTimeMillis());
}

long last_jsp_time = (Long) session.getAttribute("last_jsp_time"); //上一次headviewer.jsp網頁更新時間
long last_indx_qry_time = (Long) session.getAttribute("last_indx_qry_time"); //上一次抓取指數報價時間

long rush_cnt = 0l;
long timeStamp_last = 0l;
long timeStamp_this = System.currentTimeMillis();

/*
if(session.getAttribute("NOCheckSession") == null)
{
    if ( (System.currentTimeMillis()-last_jsp_time) > 1000*60* 30) //網頁未操作時間
    {
        session.removeAttribute("signed");
        JSONObject j = new JSONObject();
        j.put("id","10");
        j.put("rtcode","4444");
        out.print(j.toString());
        return;
    }
}
*/

if (!(ex_ch == null)) {
    if (ex_ch.indexOf(".tw_20") != -1) // 阻擋參數帶日期，取得歷史資料
    {
        session.removeAttribute("signed");
        JSONObject j = new JSONObject();
        j.put("id", "100000");
        //j.put("rtcode","4444");
        j.put("rtcode", "0001");
        out.print(j.toString());
        return;
    }

    if (session.getAttribute("timeStamp_last") == null) //Session第一次抓取
    {
        session.setAttribute("timeStamp_last", timeStamp_this);
        session.setAttribute("rush_cnt", 0l);
    } else {
        timeStamp_last = (Long) session.getAttribute("timeStamp_last");
        rush_cnt = (Long) (session.getAttribute("rush_cnt"));
        if (rush_cnt > 20) {
    session.removeAttribute("signed");
    JSONObject j = new JSONObject();
    j.put("id", "100");
    //j.put("rtcode","4444");
    j.put("rtcode", "0001");
    out.print(j.toString());
    session.setAttribute("rush_cnt", 5l);
    return;
        }

        if ((timeStamp_this - timeStamp_last) <= 50) //兩次間距過短
        {
    rush_cnt = rush_cnt + 1;
    //session.setAttribute("rush_cnt", rush_cnt+1);
    if (timeStamp_this > timeStamp_last)
        session.setAttribute("timeStamp_last", timeStamp_this);

    if (rush_cnt > 6) //前幾次暫時不擋
    {
        session.setAttribute("rush_cnt", rush_cnt);
        if (timeStamp_this > timeStamp_last)
            session.setAttribute("timeStamp_last", timeStamp_this);
        JSONObject j = new JSONObject();
        j.put("rtcode", "0000");
        j.put("rtmessage", "0");
        j.put("userDelay", (new SecureRandom().nextInt(10) + 1) * 1000);
        out.print(j.toString());
        Thread.currentThread().sleep(60);
        return;
    }
        } else if ((timeStamp_this - timeStamp_last) >= 3000) //冷卻機制
        {
    if (rush_cnt > 0)
        rush_cnt = rush_cnt - 1;
        }
        session.setAttribute("rush_cnt", rush_cnt);
        if (timeStamp_this > timeStamp_last)
    session.setAttribute("timeStamp_last", timeStamp_this);
    }
}

if (referer == null)
    referer = "";
else
    session.setAttribute("referer", referer);

if (delay == null)
    delay = "0";

if (lang == null) {
    lang = request.getHeader("Accept-Language");
    if (lang == null)
        lang = "zh-TW";
    lang = lang.toLowerCase();
    if (lang.indexOf("zh-tw") != -1)
        lang = "zh-tw";
    else if (lang.indexOf(",") != -1) {
        lang = lang.split(",")[0];
    }
    session.setAttribute("lang", "zh_tw");
}

lang = lang.split(",")[0];
lang = lang.replaceAll("_", "-");

if (ex_ch == null) {
    JSONObject j = new JSONObject();
    j.put("rtcode", "9999");
    j.put("rtmessage", "參數不足");
    out.print(j.toString());
    return;
}

//if(date==null) date = ExLastTradeDate.getLastTradeDate(ks,"tse");
//date = "20130206";
//System.out.println("========="+date);
String[] chs = ex_ch.split("\\|");

//找出  去除市場別以後，重複出現相同股票代號的查詢
//ETF 0061 會查詢2次，須排除
String[] chs_check = ex_ch.replaceAll(".tw", "").replaceAll("tse_", "").replaceAll("otc_", "").replaceAll("0061", "").split("\\|");
for (int ii = 0; ii < chs_check.length; ii++) {
    for (int jj = ii + 1; jj < chs_check.length; jj++) {
        if (chs_check[ii].equals(chs_check[jj])) {
    session.removeAttribute("signed");
    JSONObject j = new JSONObject();
    j.put("id", "1000");
    //j.put("rtcode","4444");
    j.put("rtcode", "0001");
    out.print(j.toString());
    return;
        }
    }
}

if (chs.length > 100) {
    session.removeAttribute("signed");
    JSONObject j = new JSONObject();
    j.put("id", "10000");
    //j.put("rtcode","4444");
    j.put("rtcode", "0001");
    out.print(j.toString());
    return;
}

String sessionKey = "";
Boolean pageReload = false;

List<String> keys = new Vector<String>();
List<String> symbols = new Vector<String>();
Hashtable<String, String> dateHash = new Hashtable<String, String>();
Hashtable<String, Map<String, String>> symbolsRows = new Hashtable<String, Map<String, String>>();
for (int i = 0; i < chs.length; i++) {
    String rdate = "";
    String key = chs[i];
    if (key.split("_").length == 2) {
        if (date != null)
    rdate = date;
        else {
    String ex = key.split("_")[0];
    rdate = dateHash.get(ex);
    if (rdate == null)
        rdate = ExLastTradeDate.getLastTradeDate(null, ex);
    dateHash.put(ex, rdate);
        }
        key = chs[i] + "_" + rdate;
    }
    //if(!lang.equals("zh-tw"))
    {
        String symbo = Utility.filiterString(key.split("_")[1], ".tw");
        StockNameStore sns = new StockNameStore(rdate,symbo);
        symbolsRows.put(symbo, sns.getMap());
        symbols.add(symbo);
    }
    keys.add(key);
    sessionKey += (key + "|");
}


UserSession us = null;
us = (UserSession) session.getAttribute(sessionStr);
if (us == null)
    us = new UserSession(session.getId());

long startTime = us.getLatestMillis(sessionKey);
//List<StockInfo> infoList = StockInfoManager.getStockInfoList(keys,startTime,30000);
List<StockInfo> infoList = StockInfoManager.getStockInfoList(keys, sessionKey, us, 30000, Utility.parseLong(delay));
//System.out.println("infoList==========:"+infoList.size());
JSONArray ja = new JSONArray();

long tlong2 = us.getLatestMillis(sessionKey);
for (int inx = 0; inx < infoList.size(); inx++) {

    StockInfo sinfo = infoList.get(inx);

    if (sinfo == null)
        continue;

    //System.out.println("sinfo==========:"+sinfo);
    long tlong = sinfo.getChange();
    if (tlong2 < tlong) {
        tlong2 = tlong;
    }
    //System.out.println("infoList_tlong==========:"+tlong);
    List<Map.Entry<String,String>> cols = sinfo.getStockDetail();
    JSONObject j1 = new JSONObject();
    //System.out.println("infoList_cols==========:"+cols.size());
    // StockDetail
    for (int j = 0; j < cols.size(); j++) {
    	Map.Entry<String,String> hc = cols.get(j);
        String val = "";
        String name = "";
        val = hc.getValue().toString();
        name = hc.getKey().toString();
        if (name.equals("it")) {
    user_delay = StockInfoManager.getUserDelayMillis(val);
        }
        if (user_delay == 0)
    user_delay = 5000;

        int pp = -1;
        pp = Arrays.asList(itemsORG).indexOf(hc.getKey().toString());
        if (pp > -1) {
    switch (val.length()) {
    case 1:
        j1.put(itemsNEW[pp], itemsREG[pp] + val + itemsREG[pp]);
        break;
    case 2:
        j1.put(itemsNEW[pp], val.substring(0, 1) + itemsREG[pp] + val.substring(1) + itemsREG[pp]);
        break;
    default:
        j1.put(itemsNEW[pp], val.substring(0, 3) + itemsREG[pp] + val.substring(3) + itemsREG[pp]);
    }
        } else
    j1.put(hc.getKey().toString(), val);
    }
    // StockQuote
    List<Map.Entry<String,String>>  schs = sinfo.getStockQuote();
    //System.out.println("infoList_schs==========:"+schs.size());
    for (int i = 0; i < schs.size(); i++) {
    	Map.Entry<String,String> hc = schs.get(i);
        String val = "";
        String name = "";
        val = hc.getValue().toString();
        name = hc.getKey().toString();

        int pp = -1;
        pp = Arrays.asList(itemsORG).indexOf(name);
        if (pp > -1) {
    switch (val.length()) {
    case 1:
        j1.put(itemsNEW[pp], itemsREG[pp] + val + itemsREG[pp]);
        break;
    case 2:
        j1.put(itemsNEW[pp], val.substring(0, 1) + itemsREG[pp] + val.substring(1) + itemsREG[pp]);
        break;
    default:
        j1.put(itemsNEW[pp], val.substring(0, 3) + itemsREG[pp] + val.substring(3) + itemsREG[pp]);
    }
        } else
    j1.put(name, val);

    }
    //Bid Time
    String bidTime = j1.optString("bt");
    if (bidTime == null)
        bidTime = "";

    if (!bidTime.equals("")) {
        j1.put("t", bidTime);
        j1.remove("bt");
        //out.println("taa1"+bidTime);
    }
    //out.println("taa2");
    //Lang
    String ch = j1.getString("ch");
    if (ch != null) {
        ch = Utility.filiterString(ch, ".tw");
        Map<String, String> symbolRow = symbolsRows.get(ch);
        if (!lang.equals("zh-tw")) {
    if (symbolRow != null) {
        String hcol = symbolRow.get("en");
        if (hcol != null) {
            j1.put("n", hcol);
        }
        hcol = symbolRow.get("ef");
        if (hcol != null) {
            j1.put("nf", hcol);
        }
        hcol = symbolRow.get("enu");
        if (hcol != null) {
            j1.put("nu", hcol);
        }
        hcol = symbolRow.get("rch");
        if (hcol != null) {
            String rch = hcol;
            j1.put("rch", rch);
            hcol = symbolRow.get("ren");
            if (hcol != null) {
                j1.put("rn", hcol);
            } else
                j1.put("rn", rch);
        }
    }
        } else {
    if (symbolRow != null) {
        String hcol = symbolRow.get("cnu");
        if (hcol != null) {
            j1.put("nu", hcol);
        }
        hcol = symbolRow.get("cf");
        if (hcol != null) {
            j1.put("nf", hcol);
        }
        hcol = symbolRow.get("rch");
        if (hcol != null) {
            String rch = (String) hcol;
            j1.put("rch", rch);
            hcol = symbolRow.get("rcn");
            if (hcol != null) {
                j1.put("rn", hcol);
            } else
                j1.put("rn", rch);
        }
    }
        }
    }
    //StockTicker
    String tickKey0 = j1.optString("tk0");
    String tickKey1 = j1.optString("tk1");
    if (tickKey0 == null)
        tickKey0 = "";
    if (tickKey1 == null)
        tickKey1 = "";
    String[] tickKey = new String[] { tickKey0, tickKey1 };
    String[] treads = sinfo.getTrade(tickKey);
    if (treads.length == 2) {
        //j1.put("t",treads[0]);
        j1.put("tv", treads[1]);
    } else {
        j1.put("tv", "-");
    }
    String c = j1.optString("c");
    if (c.equals("")) {
        j1.put("c", ch);
    }

    bidTime = j1.optString("t");
    if (bidTime != null) {
        if (!bidTime.equals("13:30:00") && !bidTime.equals("13:33:00") && !bidTime.equals("14:30:00")) //有任一檔未收盤
    pageReload = true;
    }

    ja.put(j1);

} //for(int inx = 0 ; inx < infoList.size() ; inx++){

JSONObject jqueryTime = new JSONObject();

/*
for(int inx = 0 ; inx < infoList.size() ; inx++){
    StockInfo sinfo = infoList.get(inx);
    if(Collections.frequency(infoList, sinfo) < 1) result.add(s);
    jqueryTime.put("infoListSize",infoList.size());
}
    StockInfo sinfo = infoList.get(0);
    jqueryTime.put("infoListSize",Collections.frequency(infoList, sinfo));

    StockInfo sinfo = infoList.get(0);
    jqueryTime.put("infoListSize",Collections.frequency(infoList, sinfo));
    jqueryTime.put("sinfo",sinfo);
    jqueryTime.put("sinfo1","aa");

*/

jqueryTime.put("sysDate", Utility.getDateStr());
jqueryTime.put("sysTime", Utility.getSQLTimeStr());
jqueryTime.put("showChart", StockInfoManager.isShowChart());
jqueryTime.put("stockInfo", StockInfoManager.getQueryMillisTime());
jqueryTime.put("stockInfoItem", StockInfoManager.getProductSize());
jqueryTime.put("sessionKey", sessionKey);
jqueryTime.put("sessionStr", sessionStr);
jqueryTime.put("sessionFromTime", startTime);
jqueryTime.put("sessionLatestTime", us.getLatestMillis(sessionKey));
//jqueryTime.put("rush_cnt",session.getAttribute("rush_cnt"));

JSONObject j = new JSONObject();
//j.put("jsp_time",System.currentTimeMillis()-last_jsp_time);
//j.put("last_indx_qry_time",System.currentTimeMillis()-last_indx_qry_time);

j.put("rtcode", "0000");
j.put("rtmessage", "OK");
j.put("msgArray", ja);
j.put("queryTime", jqueryTime);
j.put("referer", referer);
if (referer.endsWith("oddTrade.jsp"))
    j.put("userDelay", StockInfoManager.getUserDelayMillis("od"));
else {
    j.put("userDelay", user_delay);
    //    pageReload = true;
    if (pageReload)
        j.put("userDelay2", user_delay);
    else
        j.put("userDelay2", 1000 * 300); //已收盤-減少 refresh 次數
}

out.print(j.toString());
us.setLatestMillis(sessionKey, tlong2);
us.setCount(sessionKey);
session.setAttribute(sessionStr, us);
session.setAttribute("last_indx_qry_time", System.currentTimeMillis());

if (true)
    return;
%>
