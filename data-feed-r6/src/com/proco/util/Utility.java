package com.proco.util;

import java.util.Hashtable;

public class Utility {

    /*
    public static boolean checkEpoll(){
    	boolean rt = true;
    	EpollEventLoopGroup workerGroup = null;
			try{
				workerGroup = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors());
				System.out.println(getFullyDateTimeStr()+" Ckecking epoll env......ok.");
			} catch (Error err){
				System.out.println(getFullyDateTimeStr()+" Ckecking epoll env......fail: "+err.toString());
				rt = false;
			} catch (Exception err){
				System.out.println(getFullyDateTimeStr()+" Ckecking epoll env......fail: "+err.toString());
				rt = false;
			} finally {
				if(workerGroup!=null)
					workerGroup.shutdownGracefully();
			}
    	return rt;
    }*/
    
    public static String getFullyDateTimeStr() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.
                currentTimeMillis());
        String nowStr = now.toString();
        while (nowStr.length() < 23)
            nowStr = nowStr + "0";
        //System.out.println(nowStr);

        String timeStr = nowStr.substring(0, 4);
        timeStr += nowStr.substring(5, 7);
        timeStr += nowStr.substring(8, 10) +"-";
        timeStr += nowStr.substring(11, 13)+":";
        timeStr += nowStr.substring(14, 16)+":";
        timeStr += nowStr.substring(17, 19);
        return timeStr;
    }

    public static long parseLong(String longStr) {
        long rt = 0 ;
        try {
            rt = Long.parseLong(longStr);
        } catch (NumberFormatException ex) {
            rt = 0 ;
        }
        return rt ;
    }
    
    public static void sleep(int ct) {
    	try {
			Thread.sleep(ct);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static Hashtable<String,Long> uuTimeStampHash = new Hashtable<String,Long>();
    public synchronized static long getUniTimeStamp(String ch) {

        long ct = System.currentTimeMillis();
        Long pre = uuTimeStampHash.get(ch);
        if(pre!=null){
          if(ct<=pre.longValue())
             ct = pre.longValue()+1;
        }
        uuTimeStampHash.put(ch,new Long(ct));
        return (ct);
    }    
    
    public static int parseInt(String intStr) {
        if (intStr == null)
            intStr = "";
        intStr = intStr.trim();
        intStr = Utility.filiterString(intStr, ",");
        if (intStr.indexOf(".") != -1) {
            intStr = intStr.substring(0, intStr.indexOf("."));
        }
        int rt = 0;
        try {
            rt = Integer.parseInt(intStr);
        } catch (NumberFormatException ex) {
            rt = 0;
        }
        return rt;
    }
    
    public static String filiterString(String dataStr, String key) {
        while (dataStr.indexOf(key) != -1) {
            int end = dataStr.indexOf(key);
            String tmpStr = dataStr;
            tmpStr = tmpStr.substring(end + key.length(), tmpStr.length());
            dataStr = dataStr.substring(0, end);
            dataStr += tmpStr;
        }
        return dataStr;
    }    

    static String[] serialPrefix = new String[] {
            "", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000",
          "000000000", "0000000000"};
    public static String intStr2String(String intStr, int size) {
    	int fixsize = size - intStr.length();
        while (fixsize > 10) {
        	fixsize = fixsize - 10;
        	intStr = (new StringBuffer(serialPrefix[10]).append(intStr)).toString();
        }
        intStr = (new StringBuffer(serialPrefix[fixsize]).append(intStr)).toString();
        return intStr;
    }    
    
}
