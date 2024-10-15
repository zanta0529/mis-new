package com.ecloudmobile.datafeed;

import com.ecloudlife.util.Utility;
import java.io.*;
import java.io.*;
import com.ecloudlife.util.ConfigData;

public class IOFileProcessLog {

  static java.util.Properties invProp = new java.util.Properties();

  static {
    load();
  }

  public static void load(){
    try {
      java.io.FileInputStream fis = new java.io.FileInputStream(
          ConfigData.processLog);
      invProp.load(fis);
      Utility.closeInputStream(fis);
    }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex) {
    }
  }

  public synchronized static void store(){
    try {
      java.io.FileOutputStream fos = new java.io.FileOutputStream(
          ConfigData.processLog);
      invProp.store(fos, null);
      Utility.closeOutputStream(fos);
    }
    catch (FileNotFoundException ex) {
    }
    catch (IOException ex) {
    }
  }

  public static int getIOFileProcessLogSize(){
    return invProp.size();
  }

  public static void setIOFileProcessLog(String key,String value){
    String val = invProp.getProperty(key);
    if(val==null) invProp.setProperty(key,value);
    else {
      if(!value.equals(val)) invProp.setProperty(key,value);
    }
  }

  public static boolean hasIOFileProcessLog(String key,String value){
    String val = invProp.getProperty(key);
    if(val==null) return false;
    return value.equals(val);
  }

  public static void main(String[] args){
    setIOFileProcessLog("aa","222");
    setIOFileProcessLog("aa","221");
    setIOFileProcessLog("aa","223");
    store();
  }

}
