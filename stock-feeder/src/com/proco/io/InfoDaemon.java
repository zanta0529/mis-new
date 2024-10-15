package com.proco.io;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.proco.cache.RedisManager;
import com.proco.codec.StockInboundHandler;
import com.proco.exec.ProcF01;
import com.proco.exec.ProcF02;
import com.proco.exec.ProcF06;
import com.proco.exec.ProcF09;
import com.proco.exec.ProcF13;
import com.proco.exec.ProcF19;
import com.proco.exec.ProcF20;
import com.proco.exec.ProcF22;
import com.proco.exec.ProcF23;
import com.proco.exec.ProcF99;
import com.proco.exec.ProcL01;
import com.proco.exec.ProcL20;
import com.proco.exec.ProcStockCategory;
import com.proco.exec.ProcStockEngName;
import com.proco.exec.ProcStockName;
import com.proco.exec.ProcStockSnapshot;
import com.proco.util.DateManager;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;

public class InfoDaemon {

	final public static String launcher_version = "0.7";
	final public static String front_version = "1.06";
	final public static String version = "20240717_TC6";
	public static String confFile = "StockFeeder.conf";
	public static java.util.concurrent.atomic.AtomicInteger clientCount = new java.util.concurrent.atomic.AtomicInteger(0);
	static boolean run = false;
	static int thePort = 7001;
	public static int maxActive = 24;
	
	public static void main(String[] args0) {

	    
	    //args0 = new String[] {"7002","r","aaaa"};
	    Utility.writeStringArray("args", args0);
	    String[] args = Utility.readStringArray("args");
	    if(args==null) args = new String[0];
	    
	    
	    
	    if(args.length>=1){
	    	thePort = Utility.parseInt(args[0]);
	    }
	    
	    Thread mainThread = new Thread() {
	    	@SuppressWarnings("resource")
			public void run() {
	    	    try {
	    	        if(!run){
	    	          run = true;
	    	          System.out.println("====Stock Info Gateway "+front_version+" ======== "+version+" Timeline:"+ConfigData.timeline+" Datafeed:"+ConfigData.datafeed);

	    	          System.out.println("Config Data : ");
	    	          //System.out.println("balanceIp > " + ConfigData.balanceIp );


	    	          String cassHost = ConfigData.cassHost;
	    	          String cassPass = ConfigData.cassPass;
	    	          int port0 = ConfigData.port;

	    	          int maxActive0 = ConfigData.maxActive;
	    	          if(port0>0) thePort = port0;
	    	          if(maxActive0>0) maxActive = maxActive0;
	    	          if(!cassHost.equals("")) RedisManager.redis = cassHost;
	    	          if(!cassPass.equals("")) {
	    	        	  RedisManager.initAuthPath(cassPass);
	    	          }
	    	          
	    	          System.out.println("Port : "+thePort );	
	    	          System.out.println("Redis : "+RedisManager.redis );	
	    	          System.out.println("Redis Conf Path: "+RedisManager.authPath );
	    	          System.out.println("MaxActive: "+maxActive );
	    			
	    	          
	    	          
	    	          RedisManager.init(maxActive);
	    	          ExecutorManager threadPool = new ExecutorManager(maxActive);
	    	          
	    	          DateManager.getExchangeDate("tse");
	    	          DateManager.getExchangeDate("otc");
	    	          	    	          
	    	          if(ConfigData.timeline) {
	    	        	  ProcL01.init(threadPool);
	    	        	  ProcL20.init(threadPool);
	    	          }  
	    	          if(ConfigData.datafeed) {
	    	        	  ProcStockCategory.init(1);
	    	        	  ProcStockName.init(1);
	    	        	  ProcStockSnapshot.init();
	    	        	  
	    	        	  ProcStockEngName.init(1);
	    	        	  ProcF01.init(threadPool);
	    	        	  ProcF02.init(threadPool);
	    	        	  ProcF06.init(threadPool);

	    	              ProcF09.init(threadPool);
	    	              ProcF13.init(threadPool);
	    	              ProcF19.init(threadPool);
	    	              ProcF20.init(threadPool);
	    	              ProcF22.init(threadPool);
	    	              ProcF23.init(threadPool);	       	
	    	          }
	    	          if(ConfigData.datafeed || ConfigData.timeline) {
	    	        	  ProcF99.init(threadPool); 
	    	          }
	    	          	    	          
	    	          //Start AutoReportDaemon
	    	          //AutoReportDaemon ard = new AutoReportDaemon();
	    	          //ard.start();

	    	          //Start Server Daemon
	    	          ServerSocket ss;
	    	          Socket theConnection;
	    	          ss = new ServerSocket(thePort);
	    	          
	    	          System.out.println("Listening for connections on port :" + ss.getLocalPort());
	    	          while (true) {
	    	        	  theConnection = ss.accept();
	    	        	  System.out.println("Connection established with " + theConnection);
	    	        	  DaemonThread2 it = new DaemonThread2(theConnection);
	    	        	  it.start();
	    	          }
	    	        }
	    	    }
	    	    catch (Exception ex) {
	    	    	System.out.println("Exception"+ ex.toString());
	    	    }	    		
	    	}
	    };
	    mainThread.start();
	    
    	while(true){
    		try {
				Thread.sleep(10000);
				String tradeDate = "t:"+DateManager.getExchangeDate("tse");
  	          	tradeDate += ",o:"+DateManager.getExchangeDate("otc");
				System.out.println(Utility.getFullyDateTimeStr()+" "+tradeDate+" StockInHandler:"+ StockInboundHandler.decodeCount.get()+" RedisCmd:"+ RedisManager.getCommandCount() +" RedisQueue="+RedisManager.size()+" Snapshot:"+ProcStockSnapshot.execCount.getAndSet(0)+" StockCategory="+ProcStockCategory.execCount.get()+" StockNameInx="+ProcStockName.execCount.get()+" Datafeed:"+ProcF01.uaHash.size()+" Timeline:"+ProcL01.uaHash.size()+" TL0:"+ProcL20.exec0.get()+" TL1:"+ProcL20.exec1.get());
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}

	}
}

class DaemonThread2 extends Thread {
	InputStream sis;
	InputStream is;
	OutputStream os;
	Socket theConnection;
	public long stamp = System.currentTimeMillis();
	  //DaemonTimer dt ;
	String uid;
	public DaemonThread2(Socket theConnection) throws IOException {
		this.theConnection = theConnection;
		uid = Utility.getUuid();
	    InfoDaemon.clientCount.addAndGet(1);
	    System.out.println(uid + "_" +theConnection.getInetAddress().toString()+"_"+
	                         Utility.getSQLDateTimeStr() +
	                         "_Connect =============================:"+ InfoDaemon.clientCount.get());
	    
	    this.sis = theConnection.getInputStream();
	    this.is = new BufferedInputStream(sis,10485760);
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
			boolean ok = true;
			try {
				DataInputStream dis = new DataInputStream(is);
				while (ok) {
					int lf = 0;
					int i = dis.read();
					if (i == 0xf0) {
						int type = dis.read();
						int length = dis.readShort();
						//System.out.println(type + " - " + this.length);
						byte[] buffer = new byte[length];
						if (type == 0) {
							dis.read(buffer);
							lf = dis.read();
						} else if (type == 1) {
							dis.read(buffer);
							lf = dis.read();
		                 
							StockInboundHandler.decodeBeeData(InfoDaemon.clientCount.get(), buffer);
						}
					}
					if(lf==0xf1)
						ok = true; 
		            if(i == -1)
		            	break;
				}

			} catch (Exception ex) {
				System.out.println("Decode Err: "+ok);
				System.out.println(ex);
			}
			
		    System.out.println(uid + "_" +theConnection.getInetAddress().toString()+"_"+
                    Utility.getSQLDateTimeStr() +
                    "_Break =============================:"+ InfoDaemon.clientCount.addAndGet(-1));			
		    
			Utility.closeInputStream(is);
			Utility.closeInputStream(sis);
			Utility.closeOutputStream(os);
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

