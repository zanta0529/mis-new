package com.ecloudmobile.logicBean;

import java.util.Hashtable;
import java.util.Vector;
import java.io.File;

import com.ecloudmobile.datafeed.IOFilePolling;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockIoFile;
import com.ecloudlife.util.Utility;
import com.ecloudlife.util.ConfigData;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.*;

public class WK_TH33 implements Runnable {
    PreparedFile pm;
    String base = "TH33";
    int prefix = 150;
  public WK_TH33(PreparedFile pm){
    this.pm = pm;
  }

  public void run(){
    try {
      java.io.File ioFile = new File(pm.getFilePath());
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();
      pm.setFsize(size);
      if(size < prefix){
        Utility.closeInputStream(bai);
        Utility.closeInputStream(fis);
        return;
      }
      //System.out.println("header: "+header);
      JSONArray ja = new JSONArray();
      int count = 0;
      do{
        JSONObject j1 = new JSONObject();

        /*
        String stkno = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String brkid =Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
        String brkname =Utility.readInputStreamToString(bai, 16, ConfigData.gwEncode).trim();
        String stkname =Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String avishr  =Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
        String lonrate =Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String feerate =Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
        String mark =Utility.readInputStreamToString(bai, 40, ConfigData.gwEncode).trim();
        String filler =Utility.readInputStreamToString(bai, 54, ConfigData.gwEncode).trim();
        */

       String stkno = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
       String brkid =Utility.readInputStreamToString(bai, 4, ConfigData.gwEncode).trim();
       String brkname =Utility.readInputStreamToString(bai, 16, ConfigData.gwEncode).trim();
       String stkname =Utility.readInputStreamToString(bai, 16, ConfigData.gwEncode).trim();
       String avishr  =Utility.readInputStreamToString(bai, 14, ConfigData.gwEncode).trim();
       String lonrate =Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
       String feerate =Utility.readInputStreamToString(bai, 5, ConfigData.gwEncode).trim();
       String mark =Utility.readInputStreamToString(bai, 40, ConfigData.gwEncode).trim();
       String filler =Utility.readInputStreamToString(bai, 44, ConfigData.gwEncode).trim();

        j1.put("stkno",stkno);
        j1.put("brkid",brkid);
        j1.put("brkname",brkname);
        j1.put("stkname",stkname);
        j1.put("avishr", Utility.parseInt(avishr)+"");
        j1.put("lonrate",Utility.parseV9StrToX7Str(lonrate,2));
        j1.put("feerate",Utility.parseV9StrToX7Str(feerate,2));
        j1.put("mark",mark);
        j1.put("filler",filler);
        ja.put(j1);
        count++;
        //System.out.println(stkno+": "+brkid+" "+brkname);
      } while(bai.available() >= prefix);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵");
        return;
      }

      StockIoFile qsio = new StockIoFile(IOFilePolling.currentDate,base);
      List<Map.Entry<String, String>> sList = qsio.get();
      //ioName = Utility.filiterString(ioName,".dat");
      String rtmessage = (ioNames[1]+" Size : "+count+ " File Size:"+size );
      System.out.println(Utility.cleanString(rtmessage));
      if(sList.size()==0){
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        qsio.delete();
      }

      String key = ioNames[1]+"_"+ioNames[2];

      //String key = ioFile.getName();

      TreeMap<String,String> dhash = new TreeMap<String,String>();
      JSONObject j = new JSONObject();
      j.put("sbrk",ja);
      dhash.put(key,j.toString());
      StockIoFile sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
      //sio.delete(CassConnectionPool.getKeyspace(),null);
      boolean ok = sio.insertCF();
      pm.setSuccess(ok);
      if (ok) {
        pm.setReturnCode("0000");
        pm.setReturnMesaage(base+" Insert "+rtmessage);
      }
      else {
        pm.setReturnMesaage(base + " Insert Error.");
      }

      System.out.println(base+": "+ok);

    }
    catch (Exception ex) {
      pm.setReturnMesaage(base+" Insert Error : "+ex.toString());
      ex.printStackTrace(System.out);
    }
  }

  public static void main(String[] args) {
    PreparedFile.main0(new String[]{"TH33","D:\\projects\\jog\\TseR6DataFeed\\iofile\\r6data"});
/*
    StockIoFile qsio = new StockIoFile("TH33");
    Keyspace ks = CassConnectionPool.getKeyspace();
    List<HColumn<String, String>> sList = qsio.get(ks, "20130603_0","20130603_Z", 100);
    Hashtable<String,JSONObject> symbols = new Hashtable<String,JSONObject>();
    Vector<String> colSortList = new Vector<String>();
    Hashtable<String, HColumn<String, String>>
        colHash = new Hashtable<String, HColumn<String, String>> ();

    for (HColumn<String, String> hcol : sList) {
      try {
        JSONObject j1 = new JSONObject(hcol.getValue());
        JSONArray ja1 = j1.getJSONArray("sbrk");
        String kk = "";
        for (int i = 0; i < ja1.length(); i++) {
          JSONObject j2 = ja1.getJSONObject(i);
          String stkno = j2.getString("stkno");
          kk = j2.getString("brkid")+"_"+hcol.getName();
          symbols.put(j2.getString("brkid")+"_"+stkno, j2);
        }
        colHash.put(kk, hcol);
        colSortList.add(kk);
      }
      catch (JSONException ex1) {
      }
    }

    Object[] cols = colSortList.toArray();
    java.util.Arrays.sort(cols);
    //for(HColumn<String,String> hcol : sList){
    int count = 0;
    for(Object col : cols){
      HColumn<String, String> hcol = colHash.get(col);
      try {
        JSONObject obj = new JSONObject(hcol.getValue());
        JSONArray ja = new JSONArray();
        JSONArray ja1 = obj.optJSONArray("sbrk");
        for (int i = 0; i < ja1.length(); i++) {
          JSONObject j2 = ja1.getJSONObject(i);
          String stkno = j2.getString("stkno");
          j2 = symbols.remove(j2.getString("brkid")+"_"+stkno);
          if(j2!=null) ja.put(j2);
        }

        count = count + ja.length();
        System.out.println(hcol.getName());
        System.out.println(hcol.getValue());
        System.out.println(ja.length());
      }
      catch (JSONException ex) {
      }
    }



    System.out.println("count:"+count);
*/



  }
}
