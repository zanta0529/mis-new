package com.ecloudlife.util;

import java.util.Hashtable;

public class UserSession {
  String sid = "";
  int startCnt = 5;
  long ct = System.currentTimeMillis();
  Hashtable<String,SubSession> subscriptionHash = new Hashtable<String,SubSession>();

  class SubSession{
    long latestTime = -1;
    int queryCnt = 0;
  }

  public UserSession(String sid){
    this.sid = sid;
  }

  public void setLatestMillis(String key,long ct){
    SubSession ss = subscriptionHash.get(key);
    if(ss==null) ss = new SubSession();
    else {
      ss.latestTime = ct;
    }
    subscriptionHash.put(key,ss);
  }

  public void setCount(String key){
    SubSession ss = subscriptionHash.get(key);
    if(ss!=null){
       ss.queryCnt++;
    }
  }

  public long getLatestMillis(String key){
    long ct = -1;
    SubSession ss = subscriptionHash.get(key);
    if(ss!=null){
      if(ss.queryCnt < this.startCnt){
      } else {
        ct = ss.latestTime;
      }

    } else {
      setLatestMillis(key, ct);
    }
    //System.out.println("getLatestMillis : "+key+" "+subscriptionHash.size()+" "+ct);
    return ct;
  }

  public void resetLatestMillis(String key){
    subscriptionHash.remove(key);
  }

  public void resetAllLatestMillis(){
    //subscriptionHash = new Hashtable<String,Long>();
    subscriptionHash.clear();
  }

  public static void main(String[] args) {
    UserSession usersession = new UserSession("");
  }
}
