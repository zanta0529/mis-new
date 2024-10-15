<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>

<%@ page import="org.json.*"%>

<%
// 缺少 X-Frame-Options
response.setHeader("X-Frame-Options", "DENY");

// 缺少 Content-Security-Policy
response.setHeader("Content-Security-Policy", "child-src http: https:");

// 缺少 HTTP Strict-Transport-Security (HSTS)
response.setHeader("Strict-Transport-Security", "max-age=31536000;includeSubDomains");

// 缺少 X-Content-Type-Options
response.setHeader("X-Content-Type-Options", "nosniff");
response.setHeader("X-XSS-Protection", "1; mode=block");

// 發現可高速緩存的 SSL
response.setHeader("Cache-Control", "no-store");
response.setHeader("Pragma", "no-cache");
response.setDateHeader("Expires", 0);

if (exception != null) {
    System.out.println(exception.getMessage());
    exception.printStackTrace();
}

// log: 200 60 0ms
response.setStatus(200);

JSONObject je = new JSONObject();
je.put("rtcode", "0000");
je.put("userDelay", 5000);
je.put("rtmessage", "Information Data Not Found");
%>

<%=je.toString()%>
