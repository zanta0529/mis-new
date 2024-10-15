package com.ecloudlife.util;


import java.net.*;
import java.io.*;
//import java.sql.*;
import java.util.*;

import java.text.SimpleDateFormat;
import java.text.*;
import java.math.BigDecimal;
import java.nio.ByteBuffer;


public class Utility extends TimerTask {
    public static String conf_path = "";
    public static long uuid = System.currentTimeMillis();
    public static long nuid = System.nanoTime();
    public Utility() {
    }

    public void run() {
        System.out.println(Utility.getDateTimeStr() + " Run");
    }

    public static String getTimeFormatTrans(String srcDateTimeStr ,String srcDateType, String srcZone,String dstDateType, String dstZone){
        String rt = srcDateTimeStr;
        try {
            TimeZone timeZoneSrc = TimeZone.getTimeZone(srcZone);
            TimeZone timeZoneDst = TimeZone.getTimeZone(dstZone);
            SimpleDateFormat sdf = new SimpleDateFormat(srcDateType);
            sdf.setTimeZone(timeZoneSrc);
            java.util.Date date = sdf.parse(srcDateTimeStr);
            DateFormat dateFormatter = new SimpleDateFormat(dstDateType);
            dateFormatter.setTimeZone(timeZoneDst);
            rt=(dateFormatter.format(date));

        } catch (ParseException ex) {
        }

        return rt;
    }

    public static void main(String[] args) {
      String a = "123456789012345";
      String c = a.substring(a.length()-12,a.length());
      System.out.println(c);
      a= Utility.filiterString(a,"xxxxx");
      System.out.println(a);
      long ct = System.currentTimeMillis();
      ct = ct-(60L*60L*24L*1000L*30L);
      System.out.println(Utility.getDateStr(ct));

      System.out.println(intStr2String("1234",6));
      String aa[] = "axx|b11|c00|fxx".split("\\|");
      System.out.println(aa.length);
      String bb[] = "1.otc.tw".split("\\.");
      System.out.println(bb.length);
      /*
        long st = System.currentTimeMillis();
        String cDay = Utility.getDateTimeStr().substring(0, 14);

        System.out.println(System.currentTimeMillis());
        String usaDate = Utility.getTimeFormatTrans(cDay, "Asia/Taipei",
                                                    "America/New_York");
        System.out.println(System.currentTimeMillis());
        System.out.println(usaDate);
        System.out.println(cDay);
        try {
        TimeZone timeZoneSrc = TimeZone.getTimeZone("America/New_York");
        SimpleDateFormat sdf = new  SimpleDateFormat("yyyyMMddHH:mm:ss");
        sdf.setTimeZone(timeZoneSrc);
        java.util.Date ss = sdf.parse("2010091512:59:09");

            System.out.println(st);
            System.out.println(System.currentTimeMillis());
            System.out.println(ss.getTime());
            System.out.println(System.currentTimeMillis()-ss.getTime());
        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
       */
    }

//--------------------------------------------------------------//

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

    public static String getFullyDateTimeStr() {
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
        timeStr += nowStr.substring(20, 23);
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


//--------------------------------------------------------------//

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
        String datStr = hhmmssnn.subSequence(0, 2) + ":" +
                        hhmmssnn.subSequence(2, 4) + ":" +
                        hhmmssnn.subSequence(4, 6) +
                        "." + hhmmssnn.subSequence(6, 8);
        java.sql.Time ti = java.sql.Time.valueOf(datStr);
        return ti;
    }


//--------------------------------------------------------------//

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

    public static byte[] intStr2Bytes(String intStr, int size) {
        byte[] retData = new byte[size];
        byte[] srcData = intStr.getBytes();
        if (retData.length == srcData.length)
            retData = srcData;
        else if (retData.length > srcData.length) {
            for (int i = 0; i < retData.length; i++)
                retData[i] = (byte) '0';
            for (int i = srcData.length; i > 0; i--) {
                retData[(retData.length - srcData.length) + i -
                        1] = srcData[i - 1];
            }
        } else if (retData.length < srcData.length)
            for (int i = srcData.length; i > 0; i--) {
                if (i <= retData.length)
                    retData[i -
                            1] = srcData[(srcData.length - retData.length) + i -
                                 1];
            }
        return retData;
    }

//--------------------------------------------------------------//

    public static void str2Byte(String str, byte[] bt, int start, int size) {
        for (int i = 0; i < size; i++) {
            if (i < str.length())
                bt[start + i] = (byte) str.charAt(i);
            else
                bt[start + i] = (byte) ' ';
        }
        return;
    }

//--------------------------------------------------------------//

    public static byte[] str2Bytes(String str, int size) {
        byte[] retData = new byte[size];
        byte[] srcData = str.getBytes();
        if (retData.length == srcData.length)
            retData = srcData;
        else if (retData.length > srcData.length) {
            for (int i = 0; i < retData.length; i++)
                retData[i] = (byte) ' ';
            for (int i = srcData.length; i > 0; i--) {
                retData[(retData.length - srcData.length) + i -
                        1] = srcData[i - 1];
            }
        } else if (retData.length < srcData.length)
            for (int i = srcData.length; i > 0; i--) {
                if (i <= retData.length)
                    retData[i - 1] = srcData[i - 1];
            }
        return retData;

    }

//--------------------------------------------------------------//

    public static byte[] bytes2BytesZero(byte[] bytes, int size) {
        byte[] retData = new byte[size];
        byte[] srcData = bytes;
        if (retData.length == srcData.length)
            retData = srcData;
        else if (retData.length > srcData.length) {
            for (int i = 0; i < retData.length; i++)
                retData[i] = 0x00;
            for (int i = srcData.length; i > 0; i--) {
                retData[(retData.length - srcData.length) + i -
                        1] = srcData[i - 1];
            }
        } else if (retData.length < srcData.length)
            for (int i = srcData.length; i > 0; i--) {
                if (i <= retData.length)
                    retData[i - 1] = srcData[i - 1];
            }
        return retData;

    }

//--------------------------------------------------------------//
    public static byte[] bytes2BytesSpec(byte[] bytes, int size) {
        byte[] retData = new byte[size];
        byte[] srcData = bytes;
        if (retData.length == srcData.length)
            retData = srcData;
        else if (retData.length > srcData.length) {
            for (int i = 0; i < retData.length; i++)
                retData[i] = (byte) ' ';
            for (int i = 0; i < srcData.length; i++) {
                retData[i] = srcData[i];
            }
        } else if (retData.length < srcData.length)
            for (int i = srcData.length; i > 0; i--) {
                if (i <= retData.length)
                    retData[i - 1] = srcData[i - 1];
            }
        return retData;
    }

    /*
        public static byte[] bytes2BytesSpec(byte[] bytes, int size) {
            byte[] retData = new byte[size];
            byte[] srcData = bytes;
            if (retData.length == srcData.length)
                retData = srcData;
            else if (retData.length > srcData.length) {
                for (int i = 0; i < retData.length; i++)
                    retData[i] = (byte) ' ';
                for (int i = srcData.length; i > 0; i--) {
                    retData[(retData.length - srcData.length) + i -
                            1] = srcData[i - 1];
                }
            } else if (retData.length < srcData.length)
                for (int i = srcData.length; i > 0; i--) {
                    if (i <= retData.length)
                        retData[i - 1] = srcData[i - 1];
                }
            return retData;
        }
     /*
//--------------------------------------------------------------//

         public static byte[] splitBytes(byte[] bytes, int start, int end) {
             byte[] retData = new byte[0];
             if (start < end) {
                 retData = new byte[end - start];
                 for (int i = start, j = 0; i < end; i++, j++) {
                     retData[j] = bytes[i];
                 }
             }
             return retData;
         }

//--------------------------------------------------------------
      /*
       public static String splitBytes2String(byte[] bytes, int start, int end) {
              byte[] retData = new byte[0];
              if ((start < end) && (bytes.length >= end)) {
                  retData = new byte[end - start];
                  for (int i = start, j = 0; i < end; i++, j++) {
                      retData[j] = bytes[i];
                  }
              }
              return new String(retData);
          }
       */
//--------------------------------------------------------------//

      public static String splitBytes2String(byte[] bytes, int start, int end) {
          return splitBytes2String(bytes, start, end, "UTF-8");
      }

    public static String splitBytes2String(byte[] bytes, int start, int end,
                                           String enc) {
        byte[] retData = new byte[0];
        if (start < end) {
            int length = end - start;
            retData = new byte[length];
            System.arraycopy(bytes, start, retData, 0, length);
        }
        try {
            return new String(retData, enc);
        } catch (UnsupportedEncodingException ex) {
            return new String(retData);
        }
    }

//--------------------------------------------------------------//

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

//--------------------------------------------------------------//

    public static String fillString(String dataStr, String key, int length) {
        if (dataStr.length() > length)
            dataStr = dataStr.substring(0, length);
        else {
            for (int i = dataStr.length(); i < length; i += key.length()) {
                dataStr += key;
            }
            dataStr = dataStr.substring(0, length);
        }
        return dataStr;
    }

//--------------------------------------------------------------//

    public static byte[] string2Bytes(String dataStr, int size, String enc) throws
            Exception {
        byte[] retBytes;
        if (dataStr == null) {
            dataStr = "";
            for (int i = 0; i < size; i++) {
                dataStr += " ";
            }
            retBytes = dataStr.getBytes();
        } else {
            retBytes = dataStr.getBytes(enc);
        }
        return Utility.bytes2BytesSpec(retBytes, size);
    }

//--------------------------------------------------------------//

    public static byte[] string2Bytes(String dataStr, int size) throws
            Exception {
        byte[] retBytes;
        if (dataStr == null) {
            dataStr = "";
            for (int i = 0; i < size; i++) {
                dataStr = dataStr + " ";
            }
            retBytes = dataStr.getBytes();
        } else {
            retBytes = dataStr.getBytes();
        }
        return Utility.bytes2BytesSpec(retBytes, size);

    }

//--------------------------------------------------------------//

    public static String truncNull(InputStream is, int len) throws Exception {
        int nullFlag = 0;
        char a;
        String sResult = "";

        for (int i = 0; i < len; i++) {
            if ((a = (char) is.read()) == 0x00)
                nullFlag = 1;
            if (nullFlag > 0)
                sResult += ' ';
            else
                sResult += a;
        }

        return sResult;
    }

//--------------------------------------------------------------//

    public static byte[] getHtml(URL url) throws IOException {
        //System.out.println("URL:" + url.toString());
        java.io.ByteArrayOutputStream bao = new java.io.ByteArrayOutputStream();
        InputStream is = url.openStream();
        while (true) {
            int i = is.read();
            if (i == -1)
                break;
            bao.write(i);
        }
        bao.close();
        return bao.toByteArray();
    }


    public static String HTMLTransfer(String html) {

        int sIndex = 0;
        StringBuffer tempHtml = new StringBuffer();
        int eIndex = 0;
        while ((sIndex = html.indexOf("http://", sIndex)) != -1) {

            int temp1 = html.indexOf(" ", sIndex);
            int temp2 = html.indexOf("\n", sIndex);
            //tempHtml.append("temp1 "+temp1+"**");
            //tempHtml.append("temp2 "+temp2+"**");
            if (temp1 > temp2 && temp2 != -1 || temp1 == -1) {
                eIndex = temp2;
            }
            if (temp1 < temp2 && temp1 != -1 || temp2 == -1) {
                eIndex = temp1;
            }

            //tempHtml.append( "["+sIndex+","+eIndex+"]");
            if (sIndex < eIndex) {

                tempHtml.append(html.substring(0, sIndex));
                tempHtml.append("<a href=\"" + html.substring(sIndex, eIndex) +
                                "\" target=\"_blank\">");
                tempHtml.append(html.substring(sIndex, eIndex) + "</a>");
                //tempHtml.append(html.substring(eIndex));
                html = html.substring(eIndex);
                sIndex = 0;
                eIndex = 0;
            } else {

                break;
            }
        }
        tempHtml.append(html.substring(eIndex));
        html = tempHtml.toString();
        html = html.replaceAll("\n", "<BR>");
        return html;
    }

    public static String getRandomIntStr(int length) {
        String rt = "";
        while (rt.length() < length) {
            Random rd = new Random();
            rt += (rd.nextLong() + System.currentTimeMillis()) + "";
            rt = Utility.filiterString(rt, "-");
        }
        rt = rt.substring(0, length);
        return rt;
    }

    public synchronized static long getNuid() {
        long ct = System.nanoTime();
        if (ct <= Utility.nuid) {
            ct = Utility.nuid;
            ct++;
        }
        Utility.nuid = ct;
        return ct;
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

    public synchronized static String getUuid(int added) {
        long ct = System.currentTimeMillis();
        if (ct <= Utility.uuid) {
            ct = Utility.uuid;
            ct += added;
        }
        Utility.uuid = ct;
        return Utility.get10radix60(ct);
    }

    public static boolean chkPhoneNumber(String phno) {
        boolean ok = false;
        if (phno != null) {
            ok = true;
            for (int i = 0; (i < phno.length() && ok); i++) {
                int code = (int) phno.charAt(i);
                if ((int) '0' <= code && (int) '9' >= code) {

                } else if (code == (int) '-') {

                } else if (code == (int) '#') {

                } else
                    ok = false;

            }

        }
        return ok;
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

    public static String getSQLDateTimeStr() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.
                currentTimeMillis());
        return now.toString();
    }

    public static boolean chkDateStr(String dateStr) {
        boolean chk = true;

        try {
            Integer.parseInt(dateStr);
            if (dateStr.length() != 8) {
                chk = false;
            } else {
                int y = Integer.parseInt(dateStr.substring(0, 4));
                int m = Integer.parseInt(dateStr.substring(4, 6));
                int d = Integer.parseInt(dateStr.substring(6, 8));
                if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 ||
                    m == 12) {
                    if (d < 1 || d > 31) {
                        chk = false;
                    }
                } else if (m == 2 || m == 4 || m == 6 || m == 9 || m == 11) {
                    if (d < 1 || d > 30) {
                        chk = false;
                    }
                    if ((y % 4) == 0 && m == 2) {
                        if (d < 1 || d > 29) {
                            chk = false;
                        }
                    } else if ((y % 4) != 0 && m == 2) {
                        if (d < 1 || d > 28) {
                            chk = false;
                        }
                    }
                } else {
                    chk = false;
                }
            }
        } catch (NumberFormatException ex) {
            chk = false;
        }
        return chk;
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

    public static long parseLong(String longStr) {
        if (longStr == null)
            longStr = "";
        longStr = longStr.trim();
        longStr = Utility.filiterString(longStr, ",");
        long rt = 0;
        try {
            rt = Long.parseLong(longStr);
        } catch (NumberFormatException ex) {
            rt = 0;
        }
        return rt;
    }

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
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

    public static String parseX7StrToV99Long(String num, int nine, int v9) {
        long rt = 0;

        if (num.startsWith("+"))
            num = num.substring(1, num.length());
        if (num.indexOf(".") == -1) {
            rt = Utility.parseLong(num);
            for (int i = 0; i < v9; i++)
                rt *= 10;
            num = rt + "";
        } else {

            int point = num.length() - 1 - num.lastIndexOf(".");
            num = Utility.filiterString(num, ".");
            rt = Utility.parseLong(num);
            if (point <= v9) {
                for (int i = point; i < v9; i++)
                    rt *= 10;
                num = rt + "";
            } else {
                int split = v9 - point;
                num = num.substring(0, num.length() - split);
                num = num.replaceAll(".", "");
            }
        }
        num = new String(Utility.intStr2Bytes(num, (nine + v9)));
        return num;
    }

    public static String parseX7StrToV9Str(String num) {
        String rt = "";
        if (num.indexOf(".") != -1) {
            String ft = num.substring(0, num.indexOf("."));
            String bk = num.substring(num.indexOf(".") + 1, num.length());
            while (bk.length() < 2)
                bk += "0";
            rt = ft + bk;
        } else
            rt = num + "00";
        return rt;
    }

    public static String parseV9StrToX7Str(String num) {

        if (num.equals("-1") || num.equals("-5") || num.equals("-9"))
            return num;
        else {
            String rt = "0.00";
            try {
                if (num.length() >= 2) {
                    rt = num.substring(num.length() - 2, num.length());
                    rt = num.substring(0, num.length() - 2) + "." + rt;
                    if (rt.charAt(0) == '.')
                        rt = "0" + rt;
                } else if (num.length() == 1) {
                    rt = "0.0" + num;
                }

            } catch (Exception ex) {
            } while (rt.length() < 7)
                rt = " " + rt;
            return rt;
        }
    }

    public static String getEndDate(String dateStr) {
        String rt = "31";
        int mon = Utility.parseInt(dateStr.substring(4, 6));
        int yr = Utility.parseInt(dateStr.substring(0, 4));
        if (mon == 02) {
            if ((yr % 4) == 0)
                rt = "29";
            else
                rt = "28";
        } else if (mon < 7 && (mon % 2) == 1) {
            rt = "31";
        } else if (mon > 7 && (mon % 2) == 0) {
            rt = "31";
        } else {
            rt = "30";
        }
        return rt;
    }

    public static InputStream newInputStream(final ByteBuffer buf) {
        return new InputStream() {
            public synchronized int read() throws IOException {
                if (!buf.hasRemaining()) {
                    return -1;
                }
                return buf.get();
            }

            public synchronized int read(byte[] bytes, int off, int len) throws
                    IOException {
                // Read only what's left
                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            }
        };
    }

    public static void closeRandomAccessFile(java.io.RandomAccessFile raf) {
        if (raf != null) {
            try {
                raf.close();
            } catch (IOException ex) {
            }
        }
    }

    public static void touchFile(String fileStr) {
        java.io.RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(fileStr, "rw");
            raf.seek(raf.length());
            raf.write(new byte[0]);
        } catch (Exception ex) {
        }
        closeRandomAccessFile(raf);
    }

    public static String parseV9StrToX7Str(String num, int v9) {
        String first = "";
        if (num.startsWith("+")) {
            //first = num.substring(0,1);
            num = num.substring(1, num.length());
        } else if (num.startsWith("-")) {
            first = num.substring(0, 1);
            num = num.substring(1, num.length());
        }

        String rt = "0.00";
        num = "" + Utility.parseLong(num);
        //System.out.println(num.length());
        try {
            if (num.length() >= v9) {
                rt = num.substring(num.length() - v9, num.length());
                rt = num.substring(0, num.length() - v9) + "." + rt;
                if (rt.charAt(0) == '.')
                    rt = "0" + rt;
            } else if (num.length() == 1) {
                rt = "0.0" + num;
            } else {
                int add = v9 - num.length();
                rt = "0.";
                for (int i = 0; i < add; i++)
                    rt += "0";
                rt = rt + num;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (rt.length() - rt.indexOf(".") > 2) {
            rt = rt.substring(0, rt.indexOf(".") + 3);
        }
        rt = first + rt;
        if (rt.endsWith(".")) {
            rt = rt.substring(0, rt.length() - 1);
        }
        return rt;
    }

    public static long getEmuInsq(String dseq) {
        long insq = 0;
        if (dseq == null)
            dseq = "";
        if (dseq.length() == 5) {
            String split = "9";
            byte[] a = Utility.intStr2Bytes(((int) dseq.charAt(0)) + "", 3);
            split = split + new String(a) + dseq.substring(1, 5);
            insq = Utility.parseLong(split);
        }
        return insq;
    }

    public static boolean checkEventTime(String shhmmss, String ehhmmss,
                                    String chhmmss) {
        int start = Utility.parseInt(shhmmss);
        int end = Utility.parseInt(ehhmmss);;
        int cur = Utility.parseInt(chhmmss);;
        boolean rt = false;
        if (start > end) {
            if (cur >= start || cur <= end)
                rt = true;
        } else {
            if (cur >= start && cur <= end)
                rt = true;
        }
        return rt;
    }

    public static long getTimeFormatTransMillis(String srcDateTimeStr , String srcZone, String dstZone){
        long rt = 0;
        try {

            //if(srcDateTimeStr.length()>14) srcDateTimeStr = srcDateTimeStr.substring(0,14);
            //System.out.println(srcDateTimeStr);
            TimeZone timeZoneSrc = TimeZone.getTimeZone(srcZone);
            TimeZone timeZoneDst = TimeZone.getTimeZone(dstZone);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(timeZoneSrc);
            java.util.Date date = sdf.parse(srcDateTimeStr);
            DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
            dateFormatter.setTimeZone(timeZoneDst);
            String srt=(dateFormatter.format(date));


            sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            java.util.Date date1 = sdf.parse(srcDateTimeStr);
            java.util.Date date2 = sdf.parse(srt);
            rt = date2.getTime() - date1.getTime();

        } catch (ParseException ex) {
        }

        return rt;
    }

    public static String getTimeFormatTrans(String srcDateTimeStr , String srcZone, String dstZone){
        String rt = srcDateTimeStr;
        try {
            TimeZone timeZoneSrc = TimeZone.getTimeZone(srcZone);
            TimeZone timeZoneDst = TimeZone.getTimeZone(dstZone);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setTimeZone(timeZoneSrc);
            java.util.Date date = sdf.parse(srcDateTimeStr);
            DateFormat dateFormatter = new SimpleDateFormat("yyyyMMddHHmmss");
            dateFormatter.setTimeZone(timeZoneDst);
            rt=(dateFormatter.format(date));

        } catch (ParseException ex) {
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
	
	public static String cleanString(String aString) {
		if (aString == null) return null;
		String cleanString = "";
		cleanString = aString.replaceAll("[^\\w\\s]", "");
		return cleanString;
	}
	
}


