package com.ecloudmobile.logicBean;

import com.ecloudmobile.datafeed.PreparedFile;
import com.ecloudlife.util.Utility;
import org.json.JSONArray;
import com.ecloudlife.util.ConfigData;
import java.io.File;
import org.json.JSONObject;


public class WK_TT82 implements Runnable {
    PreparedFile pm;
    String base = "TT82";
    int prefix = 100;
  public WK_TT82(PreparedFile pm){
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
      do{
        JSONObject j1 = new JSONObject();

        String trans = Utility.readInputStreamToString(bai, 1, ConfigData.gwEncode).trim();
        if (trans.equals("3")) {
          String stkno = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
          String stkname = Utility.readInputStreamToString(bai, 6, ConfigData.gwEncode).trim();
          System.out.println(Utility.cleanString(stkno+"-"+stkname));
        } else {
          Utility.readInputStreamToBytes(bai, 99);
        }

      } while(bai.available() >= prefix);

      Utility.closeInputStream(bai);
      Utility.closeInputStream(fis);

      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵");
        pm.setSuccess(false);
        pm.setReturnCode("9999");
        pm.setReturnMesaage(base+" 輸入檔案名稱"+name0+"有誤，無法建立主鍵");
        return;
      }
      /*
      Keyspace ks = CassConnectionPool.getKeyspace();
      StockIoFile qsio = new StockIoFile(base);
      List<HColumn<String, String>> sList = qsio.get(ks, ioNames[1]+"_0", ioNames[1]+"_Z", 1);
      //ioName = Utility.filiterString(ioName,".dat");
      System.out.println(ioNames[1]+" Size : "+sList.size());
      if(sList.size()==0){
        qsio = new StockIoFile(base);
        qsio.delete(ks,null);
      }

      String key = ioNames[1]+"_"+ioNames[2];

      //String key = ioFile.getName();

      Hashtable<String,String> dhash = new Hashtable<String,String>();
      JSONObject j = new JSONObject();
      j.put("sbrk",ja);
      dhash.put(key,j.toString());
      StockIoFile sio = new StockIoFile(base,dhash);
      //sio.delete(CassConnectionPool.getKeyspace(),null);
      boolean ok = sio.insertCF(ks);

      System.out.println(base+": "+ok);
      */
     pm.setSuccess(true);
     pm.setReturnCode("0000");
    }
    catch (Exception ex) {
      pm.setReturnMesaage(base+" Insert Error : "+ex.toString());
      ex.printStackTrace(System.out);
    }
  }

  public static void main(String args[]) {
    String code = "TT82";
    String fpath = "Data/TT82/000D-20121003-0021113973R.dat";
    PreparedFile preparedfile = new PreparedFile(code, fpath);
    preparedfile.execLogic();
  }
}
