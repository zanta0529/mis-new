<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/api/error.jsp" %>

<%@ page import="java.util.Date" %>
<%@ page import="java.io.File" %>
<%@ page import="com.ecloudlife.util.UserSession" %>
<%@ page import="com.ecloudlife.util.Utility"%>

<%@ include file="../lang/default.jsp" %>
<%@ include file="../common-utils.jsp" %>

<%
    String jspName = request.getServletPath();
    if (jspName.endsWith("stock/") || jspName.endsWith("stock")) {
    	jspName = "index";
    } else {
    	jspName = jspName.substring(jspName.lastIndexOf("/") + 1, jspName.lastIndexOf("."));
    }
    
    //String versionCode = String.valueOf(new Date().getTime());
    String versionCode = "000K";
    
    String jspRealPath = getServletContext().getRealPath("/");
    String jsFileName = jspRealPath + "/ctrl/ctrl." + jspName + ".js";
    String jsLangFileName = "";
    String cssFileName = jspRealPath + "/css/style." + jspName + ".css";
    String lang = purifyString(Utility.getHttpParameter(request, "lang"), null, 5); //request.getParameter("lang");
    String parameter = request.getRequestURL().toString();
    parameter = (parameter.indexOf("?") == -1) ? "" : parameter.substring(parameter.indexOf("?"));
    
    if (lang != null) {
    	if (lang.indexOf("us") >= 0) {
    		lang = "en_us";
    	} else {
    		lang = "zh_tw";
    	}
    } else {
    	if (session.getAttribute("lang") == null) {
    		String tempLang = purifyString(request.getHeader("Accept-Language"), null, 8);
    		if (tempLang == null) {
    			tempLang = "zh_tw";
    		}
    		tempLang = tempLang.replace("-", "_");
    		if (tempLang.indexOf("zh") >= 0) {
    			lang = "zh_tw";
    		} else {
    			lang = "en_us";
    		}
    	} else {
    		lang = (String) session.getAttribute("lang");
    	}
    }
    lang = lang.replace("-", "_");
    session.setAttribute("lang", lang);
    session.setAttribute("key1", System.currentTimeMillis());
    
    String langJspFile = "lang/" + lang + ".jsp";    
    File jsFile = new File(jsFileName);    
    if (jsFile.exists()) {
    	jsFileName = "<script type='text/javascript' src='ctrl/ctrl." + jspName + ".js?" + versionCode + "'></script>";
    } else {
    	jsFileName = "";
    }
    
    File cssFile = new File(cssFileName);    
    if (cssFile.exists()) {    	
        cssFileName = "<link rel='stylesheet' type='text/css' href='css/style." + jspName + ".css?" + versionCode + "' />";
    } else {
    	cssFileName = "";
    }
%>
<%
    if (lang.equals("zh_tw")) {
				jsLangFileName = "ctrl.lang_zh_tw.js";
%>
<%@include file="../lang/zh_tw.jsp"%>
<%
    } else if (lang.equals("en_us")) {
				jsLangFileName = "ctrl.lang_en_us.js";
%>
<%@include file="../lang/en_us.jsp"%>
<%
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title><%=WEB_TITLE %></title>
<link rel="shortcut icon" href="favicon.ico" />
<link rel="Stylesheet" type="text/css" href="css/cupertino/jquery-ui.min.css" />
<link rel="stylesheet" type="text/css" href="css/superfish.css" />
<link rel="stylesheet" type="text/css" href="css/style.css?<%=versionCode %>" />
<link rel="stylesheet" type="text/css" href="css/searchbox.css" />
<link rel="stylesheet" type="text/css" href="css/ticker.css" />
<%
    if (!jspName.equals("index")) {
%>

<link rel="stylesheet" type="text/css" href="css/style.table.css" />
<link rel="stylesheet" type="text/css" href="css/pagination.css" />
<%
    }
%>
<%=cssFileName%>
<%
    if (!lang.equals("zh_tw")) {
%>
<link rel="stylesheet" type="text/css" href="css/style.en_us.css?<%=versionCode %>" />
<%
    }
%>

<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=G-F4L5BYPQDJ"></script>
<script>
    window.dataLayer = window.dataLayer || [];
    function gtag(){ dataLayer.push(arguments); }
    gtag('js', new Date());
    gtag('config', 'G-F4L5BYPQDJ');
</script>
<script type="text/javascript" src="js/jquery-3.7.1.min.js"></script>
<script type="text/javascript" src="js/jquery-ui.min.js"></script>
<script type="text/javascript" src="js/jquery.parsequery.min.js"></script>
<script type="text/javascript" src="js/superfish.js"></script>
<script type="text/javascript" src="js/xss.min.js"></script>

<script type="text/javascript">
var lang="<%=lang%>";
jQuery.browser={};(function(){jQuery.browser.msie=false; jQuery.browser.version=0;if(navigator.userAgent.match(/MSIE ([0-9]+)./)){ jQuery.browser.msie=true;jQuery.browser.version=RegExp.$1;}})();
</script>

<%
    if (!jspName.equals("index")) {
%>
<script type="text/javascript" src="js/hoverIntent.js"></script>
<script type="text/javascript" src="js/jquery.tablesorter.js"></script>
<script type="text/javascript" src="js/jquery.pagination.js"></script>

<%
    }
%>
<%
    //if( jspName.equals("index") || jspName.equals("fibest"))
    {
%>
<script type="text/javascript" src="js/highstock.js"></script>
<%
    }
    //out.println("jspName="+jspName);
%>

<script type="text/javascript" src="ctrl/<%=jsLangFileName%>?<%=versionCode %>"></script>
<script id='init' type="text/javascript" loadjson="1" src="ctrl/ctrl.init.js?<%=versionCode %>"></script>

<%=jsFileName%>

</head>
</html>
