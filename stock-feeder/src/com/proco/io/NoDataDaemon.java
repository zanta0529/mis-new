package com.proco.io;

import java.net.*;
import java.io.*;
import java.util.*;

import com.proco.util.Utility;

import java.text.*;

public class NoDataDaemon {


  static boolean run = false;
  public static void main(String[] args0) {
    int thePort = 7001;
    
    //args0 = new String[] {"7002","r","aaaa"};
    Utility.writeStringArray("args", args0);
    String[] args = Utility.readStringArray("args");
    if(args==null) args = new String[0];
    
    
    
    if(args.length>=1){
      thePort = Utility.parseInt(args[0]);
    }
    
    
    try {
        if(!run){
          run = true;
          System.out.println("====Stock Info Gateway .01 ========20100927");

          ServerSocket ss;
          Socket theConnection;

          ss = new ServerSocket(thePort);
          System.out.println("Config Data : ");
          //System.out.println("balanceIp > " + ConfigData.balanceIp );
          System.out.println("Listening for connections on port :" +
                             ss.getLocalPort());

          //Start AutoReportDaemon
          //AutoReportDaemon ard = new AutoReportDaemon();
          //ard.start();

          //ReInit Process

          while (true) {
            theConnection = ss.accept();
            System.out.println("Connection established with " + theConnection);
            DaemonThread3 it = new DaemonThread3(theConnection);
            it.start();
          }
        }
      }
      catch (Exception ex) {
            System.out.println("Exception"+ ex.toString());
      }
  }
}

class DaemonThread3 extends Thread {

  InputStream is;
  OutputStream os;
  Socket theConnection;
  public long stamp = System.currentTimeMillis();
  //DaemonTimer dt ;
  String uid;

  public DaemonThread3(Socket theConnection) throws IOException {

      this.theConnection = theConnection;
      uid = Utility.getUuid();
      System.out.println(uid + "_" +theConnection.getInetAddress().toString()+"_"+
                         Utility.getSQLDateTimeStr() +
                         "_Connect =============================");
      this.is = theConnection.getInputStream();
      this.os = (theConnection.getOutputStream());
      //this.dt = new DaemonTimer(this, uid);
  }

   public void run()  {
   try {
     boolean read = true;
     String custIP =  theConnection.getInetAddress().toString();
     custIP = custIP.substring(custIP.indexOf("/")+1,custIP.length());
     //dt.start();
     uid = uid +"_"+custIP;
     int count = 0 ;
     long current = System.currentTimeMillis();
     //FileOutputStream fos = new FileOutputStream("xxxxx.bin");
     //java.io.BufferedOutputStream bos = new BufferedOutputStream(fos);
     //java.io.BufferedInputStream bis = new BufferedInputStream(is);
     while (true) {
       //int i = bis.read();
       //if(i==-1)break;
       //bos.write(i);

       final GeneralPacket generalpacket2 = new GeneralPacket(is);
       if (!generalpacket2.ok)
         break;
       count++;

       Thread execPacket = new Thread(){
         public void run(){
          try {
            String a = new String(generalpacket2.getRawData(), "UTF-8");
            //if(a.indexOf(":2.tse")!=-1 || a.indexOf(":4.tse")!=-1)
            //if(a.indexOf("2.tse")!=-1 || a.indexOf("4.tse")!=-1|| a.indexOf("3.tse")!=-1 || a.indexOf("2.otc")!=-1 || a.indexOf("4.otc")!=-1|| a.indexOf("3.otc")!=-1)
            // if(a.indexOf("6.otc")!=-1||a.indexOf("6.tse")!=-1)//
         // if(a.indexOf("22.tse")!=-1||a.indexOf("23.tse")!=-1)
          //if(a.indexOf("22.otc")!=-1||a.indexOf("23.otc")!=-1)
        //if(a.indexOf(":2.otc.tw")!=-1 )
        if(a.indexOf("IX0189.tw")!=-1)
        //if(a.indexOf("^")!=-1)
        //if(a.indexOf(":t00.tw")!=-1 && (a.indexOf(":2.tse.tw")!=-1||a.indexOf(":3.tse.tw")!=-1))
          //if(a.indexOf("#:23.")!=-1)
           //if(a.indexOf("5820.tw")!=-1) if(a.indexOf("#:23.")!=-1 || a.indexOf("#:22.")!=-1)
           // if(a.indexOf("IX0163.tw")!=-1)
           //if((a.indexOf("10.otc")!=-1||a.indexOf("10.tse")!=-1))
           //if(a.indexOf("t00.tw")!=-1 || a.indexOf("o00.tw")!=-1)
            //if(a.indexOf("@:1101.tw")!=-1)
           //if(a.indexOf("ip:0")==-1)
            //if((a.indexOf("1.otc")!=-1 || a.indexOf("2.otc")!=-1 || a.indexOf("4.otc")!=-1))
           // if((a.indexOf("1.tse")!=-1 || a.indexOf("2.tse")!=-1 || a.indexOf("4.tse")!=-1))
           // if((a.indexOf("3.tse")!=-1 ))
            //if(a.indexOf("1.tse")!=-1  || a.indexOf("1.otc")!=-1 )
        	//if(a.indexOf("99.tse")!=-1  || a.indexOf("99.otc")!=-1 )
            //if((a.indexOf(":2.otc")!=-1 || a.indexOf(":3.otc")!=-1))
            //if(a.indexOf("bp:3")!=-1 )
              System.out.println(a);
          }
          catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
          }
         }
       };
       execPacket.run();

       if(System.currentTimeMillis() - current > 2000){
         current = System.currentTimeMillis() ;
         //System.out.println("Count : "+count+" "+StockInfo.stockHash.size());
       }
     }
     System.out.println("Break!!");
     Utility.closeInputStream(is);
     Utility.closeOutputStream(os);
     //Utility.closeOutputStream(bos);
     //Utility.closeOutputStream(fos);
     Utility.closeSocket(theConnection);

   }
   catch (Exception e) {
     System.out.println("DaemonThread Exception: ");
     e.printStackTrace();
     close(uid);
   }
 }

 public void close(String uid) {
     //dt.close();
     //AutoReport.removeConnects(os);
     Utility.closeInputStream(is);
     Utility.closeOutputStream(os);
     Utility.closeSocket(theConnection);
     System.out.println(uid +" Closed!");
 }
}


