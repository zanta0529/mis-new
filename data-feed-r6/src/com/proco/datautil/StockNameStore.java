package com.proco.datautil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.aredis.cache.RedisCommandInfo;

import com.ecloudlife.util.Utility;
import com.ecloudmobile.datafeed.IOFilePolling;

public class StockNameStore extends RedisModelBase {
	public String columnPath = "stocknamestore";
	
	public StockNameStore(String date,String a) {
		super(date, a);
		this.rowkeyString = a;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}

	public StockNameStore(String key, String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		 chk = true;
		 this.rowkeyString = key;
		 this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		 this.hashcode = (redisKeyString).hashCode();
		 if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);	
	}
	
	public static boolean checkLog(String pathOfMSTK,String logOfMSTK){
		File mstk = new File(pathOfMSTK);
		File log = new File(logOfMSTK);
		if(!mstk.isFile())
			return false;
		long last = mstk.lastModified();
		long size = mstk.length();
		String ctLog = String.valueOf(last)+","+String.valueOf(size);
		FileOutputStream fos_log = null;
		FileInputStream fis_log = null;
		try {
			if (!log.isFile()) { //Creat New Log
				fos_log = new FileOutputStream(logOfMSTK);
				fos_log.write(ctLog.getBytes());
		        Utility.closeOutputStream(fos_log);
		        return true;
			}
			fis_log = new FileInputStream(logOfMSTK);
			byte[] raw = new byte[fis_log.available()];
			fis_log.read(raw);
			Utility.closeInputStream(fis_log);
			String prCtLog = new String(raw);
			if(prCtLog.equals(ctLog)){
		        return false;
			} else {
				fos_log = new FileOutputStream(logOfMSTK);
		        fos_log.write(ctLog.getBytes());
		        Utility.closeOutputStream(fos_log);
		        return true;
			}
		}
		catch (Exception ex) {
			System.out.println(Utility.getSQLTimeStr() +" checkLog Error : "+logOfMSTK);
			ex.printStackTrace(System.out);
			return false;
		}
	}

	static String date = Utility.getDateStr();
	public static void insertMSTK(String pathOfMSTK,String encode){
		try {
			//String date = Utility.getDateStr();
			java.io.FileInputStream fis = new java.io.FileInputStream(pathOfMSTK);
			java.io.BufferedReader dis = new BufferedReader(new InputStreamReader(fis,encode));
			int count = 0;
			List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
			while (true) {
				String line = dis.readLine();
				if (line == null)
					break;
				if (count == 0 && line.length() > 8) {
					if(!IOFilePolling.fixedDate.equals("")) date = IOFilePolling.fixedDate;
					else
						date = line.substring(line.length() - 8, line.length());
					count++;
					continue;
		        }
		        if (count == 1) {
		        	count++;
		        	continue;
		        }
		        String[] names = line.split(",");
		        if (names.length < 5)
		        	continue;
		        count++;

		        TreeMap<String, String> dhash = new TreeMap<String, String> ();
		        dhash.put("cf", names[1]);
		        dhash.put("cn", names[2]);
		        dhash.put("ef", names[3]);
		        dhash.put("en", names[4]);
		        //get row if store has data
		        /*
		        StockNameStore sns_e = new StockNameStore(names[0]);
		        List<HColumn> cols = sns_e.get(ks);
		        for(HColumn col : cols){
		          if(col.getName().equals("cf")){
		            String ren = (String)col.getValue();
		            if(!ren.equals(names[1])){
		              dhash.remove("cf");
		            }
		          }
		          if(col.getName().equals("ef")){
		            String ren = (String)col.getValue();
		            if(!ren.equals(names[1])){
		              dhash.remove("ef");
		            }
		          }
		        }*/
		        StockNameStore sns = new StockNameStore(names[0],date, dhash);
		        sns.insertCF(ks);
		        System.out.println(Utility.cleanString(date + " " + names[0] + ":" + names[1] + ","+ names[2] + ","+ names[3] + ","+ names[4]));
			}	
		      //System.out.println(count);

			
			if(!ks.isEmpty()) {
				
				java.util.TreeMap<String,String> snapshot = new java.util.TreeMap<String, String>();
				snapshot.put("sns", "1");
				
				SystemStatus ss = new SystemStatus("all",date,snapshot);
				ss.insertCF(ks);
				
			}
			
			StockNameStore.exec(0, ks);
			
		}
		catch (Exception ex) {
			System.out.println(Utility.getSQLTimeStr() +" insertMSTK Error : ");
			ex.printStackTrace(System.out);
		}	
	}		

	public static void insertWarrantRef(String pathOfWarrant,String encode){
		try {
			//String date = Utility.getDateStr();
			java.io.FileInputStream fis = new java.io.FileInputStream(pathOfWarrant);
			java.io.BufferedReader dis = new BufferedReader(new InputStreamReader(fis,encode));
			int count = 0;
			List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
			while (true) {
				String line = dis.readLine();
				if (line == null)
					break;
				if (count == 0 && line.length() > 8) {
					if(!IOFilePolling.fixedDate.equals("")) date = IOFilePolling.fixedDate;
					else
						date = line.substring(line.length() - 8, line.length());
					count++;
					continue;
		        }
		        if (count == 1) {
		        	count++;
		        	continue;
		        }
		        String[] names = line.split(",");
		        if (names.length < 3) continue;
		        count++;
		        TreeMap<String, String> dhash = new TreeMap<String, String> ();
		        if(names[1].equals("IX0001"))names[1] = "t00";
		        dhash.put("rch", names[1]);
		        dhash.put("rcn", names[2]);
		        StockNameStore sns_e = new StockNameStore(date,names[1]);
		        List<Map.Entry<String, String>> cols = sns_e.get();
		        String ren = names[1];
		        for(Map.Entry<String, String> col : cols){
		        	if(col.getKey().equals("en")){
		        		ren = (String)col.getValue();
		        		dhash.put("ren", ren);
		        	}
		        }

		        StockNameStore sns = new StockNameStore(names[0],date, dhash);
		        boolean rt = sns.insertCF(ks);
		        System.out.println(Utility.cleanString(date + " " + names[0] + ":" + names[1] + " "+ names[2]+" "+ren+" "+rt));
			}
			StockNameStore.exec(0, ks);
			System.out.println(Utility.cleanString(date)+":"+count);
		}
		catch (Exception ex) {
			System.out.println(Utility.getSQLTimeStr() +" insertWarrantRef Error : ");
			ex.printStackTrace(System.out);
		}
	}

	public static boolean insertWarrantFullName(String pathOfWarrantName,String encode){
		boolean rt = false;
		try {
			//String date = Utility.getDateStr();
			if(!IOFilePolling.fixedDate.equals("")) date = IOFilePolling.fixedDate;
			java.io.FileInputStream fis = new java.io.FileInputStream(pathOfWarrantName);
			java.io.BufferedInputStream bis = new BufferedInputStream(fis);
			List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
			while(true){
				int i = bis.read();
		        if(i == -1) break;
		        String wrnStkno = Utility.readInputStreamToString(bis,5,encode);
		        wrnStkno = (char)i+wrnStkno.trim();
		        String wrnFull =  Utility.readInputStreamToString(bis,50,encode).trim();
		        Utility.readInputStreamToBytes(bis,24);
		        TreeMap<String, String> dhash = new TreeMap<String, String> ();
		        dhash.put("cf", wrnFull);

		        StockNameStore sns = new StockNameStore(wrnStkno,date, dhash);
		        rt = sns.insertCF(ks);
		        System.out.println(Utility.cleanString(Utility.getSQLTimeStr()+" " +wrnStkno+" "+wrnFull+" "+rt));
			}
			StockNameStore.exec(0, ks);
		}
		catch (Exception ex) {
			System.out.println(Utility.getSQLTimeStr() +" insertWarrantFullName("+pathOfWarrantName+") Error : ");
			ex.printStackTrace(System.out);
		}
		return rt;
	}
	
}
