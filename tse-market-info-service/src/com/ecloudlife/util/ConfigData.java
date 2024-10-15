package com.ecloudlife.util;

public class ConfigData {
  public static int cassMaxActive = -1;//32;
  public static int connectTimeout = -1;//60000;
  public static String[] cassHosts = null;//"202.168.206.146:9160,202.168.206.147:9160,202.168.206.148:9160,202.168.206.149:9160";
  public static String cassPasswd = null;//"wm27430382";


  //manager
  public static int ohlcPatchTime = -1;

  public static int timelineCacheHours = 3;
  public static String clearTime = null; //cache reset time
  public static String production = null;
  public static String siteName = null;
}
