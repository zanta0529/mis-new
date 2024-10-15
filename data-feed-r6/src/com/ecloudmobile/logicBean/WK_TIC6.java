package com.ecloudmobile.logicBean;

import java.io.File;

import com.ecloudmobile.datafeed.IOFilePolling;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockIoFile;

import com.ecloudlife.util.Base64;
import com.ecloudlife.util.Utility;

import java.util.TreeMap;



public class WK_TIC6 implements Runnable {

  PreparedFile pm;
  String base = "TIC6";
  int prefix = 0;

  public WK_TIC6(PreparedFile pm) {
    this.pm = pm;
  }

  public void run() {
    try {
      java.io.File ioFile = new File(pm.getFilePath());

      String[] ioNames = ioFile.getName().split("-");
      if(ioNames.length!=3){
    	  String name0 = Utility.cleanString(ioFile.getName());
    	  System.out.println("Err: 輸入檔案名稱"+name0+"有誤，無法建立主鍵");
    	  return;
      }
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      int size = fis.available();
      byte[] rawData = new byte[size];
      fis.read(rawData);
      fis.close();
      String insertString = Base64.encodeBytes(rawData);

      TreeMap<String,String> dhash = new TreeMap<String,String>();
      dhash.put(ioNames[1],insertString);
      StockIoFile qsio = new StockIoFile(IOFilePolling.currentDate,base,dhash);
      boolean rt = qsio.insertCF();

      pm.setFsize(size);
      pm.setSuccess(rt);
      if(rt){
        pm.setReturnCode("0000");
      } else {
        pm.setReturnMesaage(base+" Insert Error.");
      }
      System.out.println(base+": "+rt);
    }
    catch (Exception ex) {
      pm.setReturnMesaage(base+" Insert Error : "+ex.toString());
    }

  }
}
