package com.ecloudmobile.logicBean;

import java.util.Hashtable;
import java.io.File;

import com.ecloudmobile.datafeed.IOFilePolling;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockIoFile;
import com.ecloudlife.util.Utility;
import com.ecloudlife.util.ConfigData;
import org.json.JSONObject;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class WK_TH30 implements Runnable {
    PreparedFile pm;
    String base = "TH30";
    int prefix_V1 = 50;
    int prefix_V2 = 60;
  public WK_TH30(PreparedFile pm){
    this.pm = pm;
  }

  public void run(){
    try {
      java.io.File ioFile = new File(pm.getFilePath());
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();
      pm.setFsize(size);
      if(size < prefix_V1){
        Utility.closeInputStream(bai);
        Utility.closeInputStream(fis);
        return;
      }
      String header = Utility.readInputStreamToString(bai, prefix_V1, ConfigData.gwEncode);
      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);
      
      String ioFile0 = Utility.cleanString(ioFile.getName());
      String header0 = Utility.cleanString(header);
      
      System.out.println(ioFile0+" header:"+header0);
      String chkStr = header.substring(header.length()-12,header.length());
      if(chkStr.trim().equals("")) {
        System.out.println(ioFile0+" run_v2");
        run_v2();
      } else {
        System.out.println(ioFile0+" run_v2");
        run_v2();
      }

    }
    catch (Exception ex) {
      pm.setReturnMesaage(base+" Check File Version Error : "+ex.toString());
      ex.printStackTrace(System.out);
    }
  }

  public void run_v1(){
    try {
      java.io.File ioFile = new File(pm.getFilePath());
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();
      pm.setFsize(size);
      if(size < prefix_V1){
        Utility.closeInputStream(bai);
        Utility.closeInputStream(fis);
        return;
      }
      String header = Utility.readInputStreamToString(bai, prefix_V1, ConfigData.gwEncode).trim();
      //System.out.println("header: "+header);
      JSONObject j = new JSONObject();
      int count = 0;
      do{
        JSONObject j1 = new JSONObject();
        String stkno= Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String stkname = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String rslpr= Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String fllpr= Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String refpr= Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String lmthdat= Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String filler= Utility.readInputStreamToString(bai, 12, ConfigData.gwEncode).trim();
        //System.out.println("H30="+stkno+":"+stkname+"\t"+rslpr);
        j1.put("stkno",stkno);
        j1.put("stkname",stkname);
        j1.put("rslpr",rslpr);
        j1.put("fllpr",fllpr);
        j1.put("refpr",refpr);
        j1.put("lmthdat",lmthdat);
        j1.put("filler",filler);
        j.put(stkno,j1);
        count++;
      } while(bai.available() >= prefix_V1);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵 run_v1");
    	  return;
      }

      StockIoFile qsio = new StockIoFile(IOFilePolling.currentDate,base);
      Map.Entry<String, String> ent = qsio.get(ioNames[1]);
      //ioName = Utility.filiterString(ioName,".dat");
      String rtmessage = (ioNames[1]+" Size : "+count+ " File Size:"+size);
      System.out.println(Utility.cleanString(rtmessage));
      if(ent==null){
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        qsio.delete();
        //協助TH33進行清盤動作
        StockIoFile qsit33 = new StockIoFile(IOFilePolling.currentDate,"TH33");
        qsit33.delete();

        //協助TH28/OH28進行清盤動作
        //StockIoFile qsio28 = new StockIoFile("OH28");
        //qsio28.delete(ks,null);
        //StockIoFile qsit28 = new StockIoFile("TH28");
        //qsit28.delete(ks,null);

      }

      String key = ioNames[1];

      TreeMap<String,String> dhash = new TreeMap<String,String>();
      dhash.put(key,j.toString());
      StockIoFile sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
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

  public void run_v2(){
    try {
      java.io.File ioFile = new File(pm.getFilePath());
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();
      pm.setFsize(size);
      if(size < prefix_V2){
        Utility.closeInputStream(bai);
        Utility.closeInputStream(fis);
        return;
      }
      String header = Utility.readInputStreamToString(bai, prefix_V2, ConfigData.gwEncode).trim();
      //System.out.println("header: "+header);
      JSONObject j = new JSONObject();
      int count = 0;
      do{
        JSONObject j1 = new JSONObject();
        String stkno= Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
        String stkname = Utility.readInputStreamToString(bai, 16, ConfigData.gwEncode).trim();
        String rslpr= Utility.readInputStreamToString(bai, 9, ConfigData.gwEncode).trim();
        String fllpr= Utility.readInputStreamToString(bai, 9, ConfigData.gwEncode).trim();
        String refpr= Utility.readInputStreamToString(bai, 9, ConfigData.gwEncode).trim();
        String lmthdat= Utility.readInputStreamToString(bai, 8, ConfigData.gwEncode).trim();
        String filler= Utility.readInputStreamToString(bai, 3, ConfigData.gwEncode).trim();
        //System.out.println("H30="+stkno+":"+stkname+"\t"+rslpr);
        j1.put("stkno",stkno);
        j1.put("stkname",stkname);
        j1.put("rslpr",rslpr);
        j1.put("fllpr",fllpr);
        j1.put("refpr",refpr);
        j1.put("lmthdat",lmthdat);
        j1.put("filler",filler);
        j.put(stkno,j1);
        count++;
      } while(bai.available() >= prefix_V2);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵 run_v2");
    	  return;
      }

      StockIoFile qsio = new StockIoFile(IOFilePolling.currentDate,base);
      Map.Entry<String, String> ent = qsio.get(ioNames[1]);
      //ioName = Utility.filiterString(ioName,".dat");
      String rtmessage = (ioNames[1]+" Size : "+count+ " File Size:"+size);
      System.out.println(Utility.cleanString(rtmessage));
      if(ent==null){
        qsio = new StockIoFile(IOFilePolling.currentDate,base);
        qsio.delete();
        //協助TH33進行清盤動作
        StockIoFile qsit33 = new StockIoFile(IOFilePolling.currentDate,"TH33");
        qsit33.delete();

        //協助TH28/OH28進行清盤動作
        //StockIoFile qsio28 = new StockIoFile("OH28");
        //qsio28.delete(ks,null);
        //StockIoFile qsit28 = new StockIoFile("TH28");
        //qsit28.delete(ks,null);

      }

      String key = ioNames[1];

      TreeMap<String,String> dhash = new TreeMap<String,String>();
      dhash.put(key,j.toString());
      StockIoFile sio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
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

}
