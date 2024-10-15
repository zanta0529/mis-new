package com.ecloudmobile.datafeed;
import java.lang.reflect.*;
import java.util.*;

import org.json.*;
import com.ecloudlife.util.*;
import com.proco.datautil.StockIoFileLog;

public class PreparedFile {

  String workCode = "";
  String filePath = "";
  String returnCode = "9999";
  String returnMesaage = "";
  JSONObject filelogDetail = new JSONObject();
  boolean success = false;
  //public static java.util.Hashtable<String,Long> fileHash = new Hashtable<String,Long>();
  static String current_date = Utility.getDateStr();
  int fsize = 0;

  public PreparedFile(String workCode, String filePath) {
    this.workCode = workCode;
    this.filePath = filePath;
    String cdate = Utility.getDateStr();
    if(!current_date.equals(cdate)){
      //fileHash = new Hashtable<String,Long>();
      current_date = cdate;
    }
    try {
      filelogDetail.put("workCode", workCode);
      filelogDetail.put("filePath", filePath);
      filelogDetail.put("date", current_date);
    }
    catch (JSONException ex) {
    }
  }

  public int execLogic() {
    int err = 0 ;
    try {

      Class autoClass = null;
      Constructor autoConstructor = null;
      Class[] paramTypes = {
          Class.forName(this.getClass().getName())};
      Object[] param = {this};
      autoClass = Class.forName("com.ecloudmobile.logicBean.WK_" + workCode);
      autoConstructor = autoClass.getConstructor(paramTypes);
      filelogDetail.put("stimestamp", System.currentTimeMillis());
      Runnable svc = (Runnable) autoConstructor.newInstance(param);
      svc.run();
      filelogDetail.put("etimestamp", System.currentTimeMillis());
      writePreparedLog();
    }
    catch (ClassNotFoundException e) {
      System.out.println(e.toString());
      e.printStackTrace(System.out);
      err = 1 ;
    }
    catch (NoSuchMethodException e) {
      System.out.println(e.toString());
      e.printStackTrace(System.out);
      err = 1 ;
    }
    catch (InvocationTargetException e) {
      System.out.println(e.toString());
      e.printStackTrace(System.out);
      err = 1 ;
    }
    catch (InstantiationException e) {
      System.out.println(e.toString());
      e.printStackTrace(System.out);
      err = 1 ;
    }
    catch (IllegalAccessException e) {
      System.out.println(e.toString());
      e.printStackTrace(System.out);
      err = 1 ;
    }
    catch (Exception e) {
      System.out.println(e.toString());
      e.printStackTrace(System.out);
      err = 1 ;
    }
    if(err==1)
        this.returnCode = "9999";
    return err;
  }

  public String getFilePath() {
    return filePath;
  }

  public String getWorkCode() {
    return workCode;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getReturnCode() {
    return returnCode;
  }

  public String getReturnMesaage() {
    return returnMesaage;
  }

  public int getFsize() {
    return fsize;
  }

  public void setReturnCode(String returnCode) {
    this.returnCode = returnCode;
    try {
      filelogDetail.put("rtcode", returnCode);
    }
    catch (JSONException ex) {
    }
  }

  public void setSuccess(boolean success) {
    this.success = success;
    try {
      filelogDetail.put("success", success);
    }
    catch (JSONException ex) {
    }

  }

  public void setReturnMesaage(String returnMesaage) {
    this.returnMesaage = returnMesaage;
    try {
      filelogDetail.put("rtmessage", returnMesaage);
    }
    catch (JSONException ex) {
    }

  }

  public void setFsize(int fsize) {
    this.fsize = fsize;
  }

  private void writePreparedLog(){
    String rtcode = filelogDetail.optString("rtcode");
    if(rtcode==null) return;
    if(rtcode.equals("")) return;

    String date = filelogDetail.optString("date");
    String timestamp = filelogDetail.optString("stimestamp");
    String work = filelogDetail.optString("workCode");
    String path = filelogDetail.optString("filePath");

    System.out.println(work+" "+path+" "+rtcode);
    if (date.equals("") || timestamp.equals("") || work.equals("") ||
        path.equals("")) {
      return;
    }
    String key = timestamp+"_"+workCode+"_"+path;
    TreeMap<String, String> dataHash = new TreeMap<String,String>();
    dataHash.put(key,filelogDetail.toString());
    StockIoFileLog sl = new StockIoFileLog(date,date,dataHash);
    sl.insertCF();
  }

  public static void main0(String[] args) {
    //new ConfigData();
    String code = "TH33";
    String fpath = "iofile/T/H33/";
    if(args.length==2){
      code = args[0];
      fpath = args[1];
    }

    if(args.length==3){
      ConfigData.cassHost = args[0];
      code = args[1];
      fpath = args[2];
    }

    java.io.File pFile = new java.io.File(fpath);
    if(pFile.isDirectory()){
      java.io.File[] pFiles = pFile.listFiles();
      for(int i = 0 ; i < pFiles.length ; i++){

        String key = new StringBuilder(code).append(',').append(pFiles[i].toString()).toString();
        if(IOFileProcessLog.hasIOFileProcessLog(key,String.valueOf(pFiles[i].length()))) continue;
        System.out.println("has io : "+Utility.cleanString(key)+":"+String.valueOf(pFiles[i].length()));
        PreparedFile preparedfile = new PreparedFile(code,pFiles[i].toString());
        preparedfile.execLogic();
        if(preparedfile.success){
          String ct = String.valueOf(System.currentTimeMillis());
          IOFileProcessLog.setIOFileProcessLog(key,String.valueOf(preparedfile.getFsize()));
          System.out.println("set io : "+Utility.cleanString(key)+":"+preparedfile.getFsize());
        }
      }
      IOFileProcessLog.store();
    } else {
      PreparedFile preparedfile = new PreparedFile(code,fpath);
      preparedfile.execLogic();
    }

    /*
    PreparedFile preparedfile = new PreparedFile("OH30","iofile/O/H30/000R-20121004-0007450779R.dat");
    preparedfile.execLogic();
    PreparedFile preparedfile1 = new PreparedFile("TH30","iofile/T/H30/000R-20121004-0008015620R.dat");
    preparedfile1.execLogic();
    PreparedFile preparedfile2 = new PreparedFile("TH05","iofile/T/H05/000R-20121012-0008110026R.dat");
    preparedfile2.execLogic();
    PreparedFile preparedfile3 = new PreparedFile("TH28","iofile/T/H28/000R-20121012-0008013154R.dat");
    preparedfile3.execLogic();
    PreparedFile preparedfile4 = new PreparedFile("TH29","iofile/T/H29/000R-20121012-0014460512R.dat");
    preparedfile4.execLogic();
    PreparedFile preparedfile5 = new PreparedFile("TH33","iofile/T/H33/000R-20121012-0009005712R.dat");
    preparedfile5.execLogic();
    PreparedFile preparedfile6 = new PreparedFile("TH08","iofile/T/H08/000R-20121012-0007455605R.dat");
    preparedfile6.execLogic();
    */
  }

}
