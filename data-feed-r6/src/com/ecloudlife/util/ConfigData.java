package com.ecloudlife.util;

import com.ecloudlife.util.Utility;
import com.proco.cache.RedisManager;

import java.io.RandomAccessFile;
import java.util.Hashtable;
import com.ecloudlife.util.ConfigData;

public class ConfigData {

  public static String processLog = "iofile.log";
  public static String mstkEncode = "MS950";
  public static String mstkPath = "MSTK";
  public static String mstkLog = "mstk.log";
  public static String warrPath = "MSTK";
  public static String warrLog = "warr.log";

  //public static String oibrPath = "OIBR";
  //public static String oibrLog = "oibr.log";
  //public static String tichPath = "TICH";
  //public static String tichLog = "tich.log";

  public static String cassHost = "";//"202.168.206.146:9160";
  public static String cassAuth = "";//"202.168.206.146:9160";
  public static int maxActive = 3;
  public static int delay = 10000;
  public static int connectTimeout = 60000;
  public static String keyspace = "";//"stockspace";
  public static String cluster = "";//"Test Cluster";
  public static String gwEncode = "";//"MS950";
  static Hashtable lengthMap = ConfigData.getFunIDList();

  static {
    maxActive = Utility.parseInt(getFunction("maxActive",String.valueOf(maxActive)));
    delay = Utility.parseInt(getFunction("delay",String.valueOf(delay)));
    connectTimeout = Utility.parseInt(getFunction("connectTimeout",String.valueOf(connectTimeout)));
    cassHost = getFunction("cassHost",cassHost);
    cassAuth = getFunction("cassAuth",cassAuth);
    keyspace = getFunction("keyspace",keyspace);
    cluster = getFunction("cluster",cluster);
    gwEncode = getFunction("gwEncode",gwEncode);
    mstkPath = getFunction("mstkPath",mstkPath);
    mstkLog = getFunction("mstkLog",mstkLog);
    warrPath = getFunction("warrPath",warrPath);
    warrLog = getFunction("warrLog",warrLog);

    //oibrPath = getFunction("oibrPath",oibrPath);
    //oibrLog = getFunction("oibrLog",oibrLog);
    //tichPath = getFunction("tichPath",tichPath);
    //tichLog = getFunction("tichLog",tichLog);



    mstkEncode = getFunction("mstkEncode",mstkEncode);
    processLog = getFunction("processLog",processLog);
    
    if(!cassAuth.equals("")) RedisManager.initAuthPath(cassAuth);
    RedisManager.redis = cassHost;
    RedisManager.init(maxActive);
  }

  public ConfigData() {
    maxActive = Utility.parseInt(getFunction("maxActive", String.valueOf(maxActive)));
    delay = Utility.parseInt(getFunction("delay", String.valueOf(delay)));
    connectTimeout = Utility.parseInt(getFunction("connectTimeout", String.valueOf(connectTimeout)));
    cassHost = getFunction("cassHost", cassHost);
    keyspace = getFunction("keyspace", keyspace);
    cluster = getFunction("cluster", cluster);
    gwEncode = getFunction("gwEncode", gwEncode);
    mstkPath = getFunction("mstkPath",mstkPath);
    mstkLog = getFunction("mstkLog",mstkLog);
    warrPath = getFunction("warrPath",warrPath);
    warrLog = getFunction("warrLog",warrLog);
    //oibrPath = getFunction("oibrPath",oibrPath);
    //oibrLog = getFunction("oibrLog",oibrLog);
    //tichPath = getFunction("tichPath",tichPath);
    //tichLog = getFunction("tichLog",tichLog);
    mstkEncode = getFunction("mstkEncode",mstkEncode);
    processLog = getFunction("processLog",processLog);
  }


  public static String getFunction(String key , String value){
      String valueNew = (String) lengthMap.get(key);
      if(valueNew != null){
          value = valueNew;
      }
      return value;
  }

  public static Hashtable getFunIDList() {
    Hashtable lengthMap = new Hashtable();
    try {
      String conf_path = "DataFeed.conf";

      java.io.RandomAccessFile bis = new RandomAccessFile(conf_path, "r");
      lengthMap = new Hashtable();
      String tmpData = "";
      String key = "";
      String data = "";

      byte[] confData = new byte[(int)bis.length()];
      bis.read(confData);
      bis.close();
      String[] commands = new String(confData,"Big5").split("\n");

      for (int i = 0 ; i < commands.length ; i++) {
          tmpData = Utility.filiterString(commands[i],"\r");
          if(tmpData.indexOf("=") != -1) {
              key = (tmpData.substring(0, tmpData.indexOf("="))).trim();
              data = (tmpData.substring(tmpData.indexOf("=") + 1,
                      tmpData.length())).trim();

              System.out.println(key + "=" + data);
              lengthMap.put(key, data);
          }
      }
      bis.close();
    }
    catch (Exception ex) {
      System.out.println("ListEX:" + ex.toString());
    }

    return lengthMap;
  }

  //public static final String certPath = "D:\\projects\\tse\\StockInfoPushSystem\\cert";
}
