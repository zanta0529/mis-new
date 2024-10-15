package com.ecloudmobile.logicBean;

import java.io.File;
import com.ecloudmobile.datafeed.PreparedFile;
import com.proco.datautil.StockNameStore;

import java.io.*;
import com.ecloudlife.util.ConfigData;


public class WK_TICH implements Runnable {

  PreparedFile pm;
  String base = "TICH";
  int prefix = 19;

  public WK_TICH(PreparedFile pm) {
    this.pm = pm;
  }

  public void run() {
    try {
      java.io.File ioFile = new File(pm.getFilePath());
      java.io.FileInputStream fis = new java.io.FileInputStream(ioFile);
      java.io.BufferedInputStream bai = new java.io.BufferedInputStream(fis);
      int size = fis.available();
      pm.setFsize(size);
      boolean rt = StockNameStore.insertWarrantFullName(pm.getFilePath(),ConfigData.mstkEncode);
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
