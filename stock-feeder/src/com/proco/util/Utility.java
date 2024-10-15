package com.proco.util;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TimeZone;

import io.netty.channel.epoll.EpollEventLoopGroup;

public class Utility {

    public static String conf_path = "";
    public static long uuid = System.currentTimeMillis();
    public static long nuid = System.nanoTime();
	
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
    }
    
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

    
    public static long ckeckLong(String longStr) {
        long rt = -1 ;
        try {
            rt = Long.parseLong(longStr);
        } catch (Exception ex) {
            rt = -1 ;
        }
        return rt ;
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

    
    public static String getDateStr(long ct) {
        java.sql.Timestamp now = new java.sql.Timestamp(ct);
        java.lang.StringBuilder nowStr = new StringBuilder(now.toString());

        //String nowStr = now.toString();
        while (nowStr.length() < 23)
            nowStr = nowStr.append("0");
        //System.out.println(nowStr);
        java.lang.StringBuilder timeStr = new StringBuilder(nowStr.substring(0, 4));
        timeStr.append(nowStr.substring(5, 7)).append(nowStr.substring(8, 10));
        //String timeStr = nowStr.substring(0, 4);
        //timeStr += nowStr.substring(5, 7);
        //timeStr += nowStr.substring(8, 10);
        //timeStr += nowStr.substring(11, 13);
        //timeStr += nowStr.substring(14, 16);
        //timeStr += nowStr.substring(17, 19);
        //timeStr += nowStr.substring(20, 22);
        return timeStr.toString();
    }

    public static String getDateTimeStr(long ct) {
        java.sql.Timestamp now = new java.sql.Timestamp(ct);
        String nowStr = now.toString();
        while (nowStr.length() < 23)
            nowStr = nowStr + "0";
        //System.out.println(nowStr);

        String timeStr = nowStr.substring(0, 4);
        timeStr += nowStr.substring(5, 7);
        timeStr += nowStr.substring(8, 10);
        timeStr += nowStr.substring(11, 13);
        timeStr += nowStr.substring(14, 16);
        timeStr += nowStr.substring(17, 19);
        timeStr += nowStr.substring(20, 22);
        return timeStr;
    }

    public static String getDateTimeStr() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.
                currentTimeMillis());
        String nowStr = now.toString();
        while (nowStr.length() < 23)
            nowStr = nowStr + "0";
        //System.out.println(nowStr);

        String timeStr = nowStr.substring(0, 4);
        timeStr += nowStr.substring(5, 7);
        timeStr += nowStr.substring(8, 10);
        timeStr += nowStr.substring(11, 13);
        timeStr += nowStr.substring(14, 16);
        timeStr += nowStr.substring(17, 19);
        timeStr += nowStr.substring(20, 22);
        return timeStr;
    }

    public static String getSQLDateTimeStr(String yyyymmddhhmmssnn) {
        String yyyymmdd = yyyymmddhhmmssnn.substring(0, 8);
        String hhmmssnn = yyyymmddhhmmssnn.substring(8, 16);
        String datStr = hhmmssnn.subSequence(0, 2) + ":" +
                        hhmmssnn.subSequence(2, 4) + ":" +
                        hhmmssnn.subSequence(4, 6) +
                        "." + hhmmssnn.subSequence(6, 8);
        datStr = yyyymmdd.subSequence(0, 4) + "-" +
                 yyyymmdd.subSequence(4, 6) + "-" + yyyymmdd.subSequence(6, 8) +
                 " " +
                 datStr;

        return datStr;
    }

    public static String getSQLDateTimeStr(long millis) {
      String yyyymmddhhmmssnn = Utility.getDateTimeStr(millis);
        String yyyymmdd = yyyymmddhhmmssnn.substring(0, 8);
        String hhmmssnn = yyyymmddhhmmssnn.substring(8, 16);
        String datStr = hhmmssnn.subSequence(0, 2) + ":" +
                        hhmmssnn.subSequence(2, 4) + ":" +
                        hhmmssnn.subSequence(4, 6) +
                        "." + hhmmssnn.subSequence(6, 8);
        datStr = yyyymmdd.subSequence(0, 4) + "-" +
                 yyyymmdd.subSequence(4, 6) + "-" + yyyymmdd.subSequence(6, 8) +
                 " " +
                 datStr;

        return datStr;
    }    
    
  //--------------------------------------------------------------//

    public static String getRocDateStr() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.
                currentTimeMillis());
        String timeStr = now.toString().substring(0, 4);
        timeStr += now.toString().substring(5, 7);
        timeStr += now.toString().substring(8, 10);
        return getRocDateStr(timeStr);
    }


    public static String getRocDateStr(String yyyymmdd) {

        String timeStr = yyyymmdd.substring(0, 4);
        timeStr = "" + (Utility.parseInt(timeStr) - 1911);
        timeStr = timeStr + "/" + yyyymmdd.substring(4, 6);
        timeStr = timeStr + "/" + yyyymmdd.substring(6, 8);
        return timeStr;
    }

    public static String getDateStr() {
        long ct = System.currentTimeMillis();
        return getDateStr(ct);
    }

    public static java.sql.Date getDate(String yyyymmdd) {
        String datStr = yyyymmdd.subSequence(0, 4) + "-" +
                        yyyymmdd.subSequence(4, 6) + "-" +
                        yyyymmdd.subSequence(6, 8);
        java.sql.Date now = java.sql.Date.valueOf(datStr);
        return now;
    }

    public static String getSQLDateStr(String yyyymmdd) {
        String datStr = yyyymmdd.subSequence(0, 4) + "-" +
                        yyyymmdd.subSequence(4, 6) + "-" +
                        yyyymmdd.subSequence(6, 8);
        return datStr;
    }

    public static int getDateRange(java.util.Date d1, java.util.Date d2) {
        long day1 = d1.getTime();
        long day2 = d2.getTime();
        long ran = day1 - day2;
        int day = (int) (((ran / 1000) / 60) / 60) / 24;
        return day;
    }    
    
    public static String getTimeStr(long ct) {
        java.sql.Timestamp now = new java.sql.Timestamp(ct);
        String nowStr = now.toString();
        while (nowStr.length() < 23)
            nowStr = nowStr + "0";
        //System.out.println(nowStr);

        String timeStr = "";
        timeStr += nowStr.substring(11, 13);
        timeStr += nowStr.substring(14, 16);
        timeStr += nowStr.substring(17, 19);
        return timeStr;
    }

    public static String getTimeStr() {
        return getTimeStr(System.
                          currentTimeMillis());
    }

    public static String getSQLTimeStr() {
        String hhmmssnn = getTimeStr();
        String datStr = hhmmssnn.subSequence(0, 2) + ":" +
                        hhmmssnn.subSequence(2, 4) + ":" +
                        hhmmssnn.subSequence(4, 6);
        //if (hhmmssnn.length() < 8)
        //    datStr += ".00";
        //else
        //    datStr += "." + hhmmssnn.subSequence(6, 8);
        return datStr;
    }

    public static String getSQLTimeStr(String hhmmssnn) {
        String datStr = hhmmssnn.subSequence(0, 2) + ":" +
                        hhmmssnn.subSequence(2, 4) + ":" +
                        hhmmssnn.subSequence(4, 6);
        if (hhmmssnn.length() < 8)
            datStr += ".00";
        else
            datStr += "." + hhmmssnn.subSequence(6, 8);
        return datStr;
    }

    public static java.sql.Time getTime(String hhmmssnn) {
      if(hhmmssnn.length()<6) return null;

      StringBuilder sb = new java.lang.StringBuilder(hhmmssnn.subSequence(0, 2));
      sb = sb.append(':').append(hhmmssnn.subSequence(2, 4)).append(':').append(
          hhmmssnn.subSequence(4, 6));
      if(hhmmssnn.length()==8){
        //sb = sb.append('.').append(hhmmssnn.subSequence(6,8));
      } else {
        //sb = sb.append(".00");
      }
          //

      String datStr = sb.toString();
      //System.out.print(sb.toString());
      java.sql.Time ti = java.sql.Time.valueOf(datStr);
      return ti;
    }

    public static byte[] readInputStreamToBytes(InputStream is, int size) {
        byte[] tmpData = new byte[0];
        try {
            tmpData = new byte[size];
            is.read(tmpData);
        } catch (Exception ex) {
        }
        return tmpData;
    }

    public static String readInputStreamToString(InputStream is, int size,
                                                 String enc) {
        String rt = "";
        try {
            byte[] tmpData = new byte[size];
            for (int i = 0; i < size; i++) {
                tmpData[i] = (byte) is.read();
            }
            rt = new String(tmpData, enc);
        } catch (Exception ex) {
        }
        return rt;
    }

    public static String readInputStreamToIntString(InputStream is, int size,
            String enc) {
        String rt = "";
        try {
            byte[] tmpData = new byte[size];
            is.read(tmpData);
            rt = new String(tmpData, enc);
            rt = "" + Utility.parseInt(rt);
        } catch (Exception ex) {
        }
        return rt;
    }

    public static long readInputStreamToLong(InputStream is, int size) {
        long rt = 0;
        try {
            byte[] tmpData = new byte[size];
            is.read(tmpData);
            rt = Utility.parseLong(new String(tmpData));
        } catch (Exception ex) {
        }
        return rt;
    }

    public static int readInputStreamToInt(InputStream is, int size) {
        int rt = 0;
        try {
            byte[] tmpData = new byte[size];
            is.read(tmpData);
            rt = Utility.parseInt(new String(tmpData));
        } catch (Exception ex) {
        }
        return rt;
    }    
    
	static String writeMark = "";
	public static void writeStringArray(String index , String[] strings) {
		if(strings==null || index==null) return;
		if(writeMark.equals("")) writeMark= String.valueOf(System.nanoTime());
		String inxBasePath = index+"_"+writeMark+".tmp";
		
		String inxBasePath0 = Utility.cleanString(inxBasePath);
		try {
			FileOutputStream baos = new FileOutputStream(inxBasePath0);
			for(String line : strings) {
				baos.write((line+"\n").getBytes());
			}
			baos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String[] readStringArray(String index) {
		if(index==null) return null;
		if(writeMark.equals("")) return null;
		String inxBasePath = index+"_"+writeMark+".tmp";
		String inxBasePath0 = Utility.cleanString(inxBasePath);
		
		
		List<String> _SH_KJY0 = new ArrayList<String>();
		try {
			File inx = new File(inxBasePath0);
			FileInputStream fos = new FileInputStream(inx);
			byte[] rawdata = new byte[fos.available()];
			fos.read(rawdata);
			fos.close();
		
			String rawString = new String(rawdata,"UTF-8");
			BufferedReader br = new BufferedReader(new CharArrayReader(rawString.toCharArray()));
			while(true){
				String line = br.readLine();
				if(line==null || line.equals("")) break;
				_SH_KJY0.add(line);
			}
			Path path = inx.toPath();
			Files.deleteIfExists(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		String[] ret = new String[_SH_KJY0.size()];
		_SH_KJY0.toArray(ret);
		return ret;
	}  
	
    public static void closeSocket(Socket sk) {
        try {
            sk.close();
        } catch (Exception ex) {
            //ex.printStackTrace(System.out);
        }
    }

    public static void closeInputStream(InputStream sk) {
        try {
            sk.close();
        } catch (Exception ex) {
        }
    }

    public static void closeOutputStream(OutputStream sk) {
        try {
            sk.close();
        } catch (Exception ex) {
        }
    }	
	
    public static String getSQLDateTimeStr() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.
                currentTimeMillis());
        return now.toString();
    }	

    public static long get60radix10(String str) {
        long rt = 0;
        if (str == null)
            str = "";
        for (int i = str.length() - 1, j = 0; i >= 0; i--, j++) {
            long uni = (int) str.charAt(i);
            if (uni >= 48 && uni <= 57) {
                uni = uni - 48;
            } else if (uni >= 65 && uni <= 89) {
                uni = uni - 55;
            } else if (uni >= 97 && uni <= 121) {
                uni = uni - 62;
            }
            long base = 1;
            for (int k = 0; k < j; k++)
                base *= 60;
            rt += uni * base;
        }
        return rt;
    }

    public static String get10radix60(long num) {
        String rt = "";
        long tmp = num;
        long sh = 0;
        long cnt = 0;
        long base = 60;
        while (tmp != 0) {
            tmp = num / base;
            sh = num % base;
            num = tmp;
            if (sh >= 0 && sh <= 9) {
                sh = sh + 48;
            } else if (sh >= 10 && sh <= 34) {
                sh = sh + 55;
            } else if (sh >= 34 && sh <= 59) {
                sh = sh + 62;
            }

            rt = (char) sh + rt;
        }
        return rt;
    }	
	    
    public synchronized static String getUuid() {
        long ct = System.currentTimeMillis();
        if (ct <= Utility.uuid) {
            ct = Utility.uuid;
            ct++;
        }
        Utility.uuid = ct;
        return Utility.get10radix60(ct);
    }	
	
	public static String cleanString(String aString) {
		if (aString == null) return null;
		String cleanString = "";
		cleanString = aString.replaceAll("[^\\w\\s]", "");
		return cleanString;
	}
	 

    public static long getSQLTimeFormatTransMillis(String hh_mm_ss){
	    java.util.Date dateObj = new Date();
	    try {
	      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	      dateObj = sdf.parse(hh_mm_ss.toString());
	    }
	    catch (ParseException ex1) {
	    	return 0;
	    }
	    return dateObj.getTime();
	}
	
    public static long getDateFormatTransMillis(String yyyyMMdd){
    	long rt = 0;
        try {
        	String yyyyMMddHHmmss = yyyyMMdd+"000000";
    	TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(tz);
        java.util.Date date = sdf.parse(yyyyMMddHHmmss);
        rt = date.getTime();
        } catch (ParseException ex) {
        }
    	return rt;
    }
	
    public static long getDateTimeFormatTransMillis(String yyyyMMddHHmmss){
    	long rt = 0;
        try {
    	TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(tz);
        java.util.Date date = sdf.parse(yyyyMMddHHmmss);
        rt = date.getTime();
        } catch (ParseException ex) {
        }
    	return rt;
    }	
    
}
