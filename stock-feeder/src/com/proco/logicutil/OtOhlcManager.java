package com.proco.logicutil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Vector;

import org.aredis.cache.RedisCommandInfo;

import com.proco.datautil.StockNames;
import com.proco.datautil.StockOhlc;
import com.proco.datautil.StockTradeMinute;
import com.proco.io.ConfigData;
import com.proco.util.ExecutorManager;
import com.proco.util.Utility;


public class OtOhlcManager {
	public static boolean draw = true;
	public static boolean replay = false;
	BigDecimal pvolume = null;
	String currTime = "";
	String currFullTime = "";
	StockOhlc procssingOod = null;
	NavigableMap<String, String> minuteHash = new java.util.concurrent.ConcurrentSkipListMap<>();
	
	public OtOhlcManager(){

	}

	public void proc(final ExecutorManager threadPool,final int hashcode, final NavigableMap<String, String> tickHash) {
		if(!draw) return;
		Runnable ohlcTh = new Runnable() {
			public void run() {
				List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
				setTrade(ks ,tickHash);
				StockNames.exec(hashcode,ks);
			}
		};
		threadPool.submit(ohlcTh, hashcode);
	}
	  
	private void setTrade(final List<RedisCommandInfo> ks ,final NavigableMap<String, String> tickHash) {
	    //Thread ohlcTh = new Thread() {
		//public void run() {
		String ch = tickHash.get("@");
		String date = tickHash.get("^");
		String time = tickHash.get("%");
		String ex = tickHash.get("#");
		String z = tickHash.get("z");
		String v = tickHash.get("v");
		if(v==null) v = "0";
		if(v!=null && z!=null && date !=null && ch !=null && ex!=null && time !=null){
			if (ex == null) return;
			if (ex.indexOf("|") == -1) return;
			ex = ex.split("\\|")[0];
			if (ex.indexOf(".") == -1) return;
			ex = ex.split("\\.")[1];
			if(ConfigData.ohlcMode==1)
				setTradeM(ks,ex, ch, z, v, time, Utility.parseInt(date));
			else
				setTrade(ks,ex, ch, z, v, time, Utility.parseInt(date));
		}
	      //}
	    //};
	    ///ohlcTh.run();
	    //ohlcTh.start();
	}

	public void setTradeM(final List<RedisCommandInfo> ks ,String ex, String ch,String last,String volume, String time, int date){
		if(time.indexOf(".")!=-1) time = time.substring(0,time.indexOf("."));
		currFullTime = time;
		
		java.sql.Time ti = java.sql.Time.valueOf(currFullTime);
		String stdPervFullTime = (new java.sql.Time(ti.getTime()-60000)).toString();
		stdPervFullTime = stdPervFullTime.substring(0, stdPervFullTime.lastIndexOf(":"));
		if (time.lastIndexOf(":") != -1) {
			time = time.substring(0, time.lastIndexOf(":"));
		}

		StockOhlc ood = null;
		if(this.currTime.equals(time) && this.procssingOod != null){
			ood = this.procssingOod;

		} /*
	      else if (currFullTime.endsWith(":00") &&
	               (!currFullTime.endsWith("13:33:00") &&
	                !currFullTime.endsWith("13:31:00")) && this.procssingOod != null) {
	        if(this.procssingOod.getTime().equals(stdPervFullTime)){
	          BigDecimal bVolume = new BigDecimal(volume);
	          BigDecimal bLast = new BigDecimal(last);
	          //if(ch.startsWith("t00.tw") || ch.startsWith("o00.tw"))
	          //System.out.println(ch+" "+currTime+" = currFullTime ==== "+currFullTime+" "+bLast+" "+bVolume);
	          this.procssingOod.setTrade(bLast, bVolume);
	          this.procssingOod.insertCF(CassConnectionPool.getKeyspace());
	          //if(ch.startsWith("t00.tw") || ch.startsWith("o00.tw"))
	          //System.out.println(ch+" "+currTime+" "+procssingOod.getTime()+" = currFullTime update ==== "+currFullTime+" "+procssingOod.getCurrent()+" "+procssingOod.getVolume());
	          pvolume = new BigDecimal(bVolume.toString());
	        }

	        this.procssingOod = null;
	        ood = StockOhlc.getStockOhlc(CassConnectionPool.getKeyspace(), ex, ch,
	                                     String.valueOf(date), time);

	      } */
		else {
			this.procssingOod = null;
	        ood = StockOhlc.getStockOhlc(ex, ch,
	                                     String.valueOf(date), time);
		}
	      //OtOhlcData ood = (OtOhlcData)sHash.get(date+time);
	      //

	      BigDecimal bLast = new BigDecimal(last);
	      BigDecimal bVolume = new BigDecimal(volume);
	      //if(ch.startsWith("t00.tw") || ch.startsWith("o00.tw"))
	      //    System.out.println(ch+" "+time+"　"+currFullTime+" data:="+bLast+" "+volume+" pvolume:"+pvolume);
	      //System.out.println(time+" pvolume="+bLast+" "+volume+" "+pvolume);
	      /*
	      if(replay && ood!=null){
	        if(ood.getReplay()==0){
	          ood = null;
	        }
	      }*/

	      if (ood == null) {
	    	  if (pvolume == null)
	    		  ood = new StockOhlc(ex, ch, String.valueOf(date), time, bLast, bVolume);
	    	  else
	    		  ood = new StockOhlc(ex, ch, String.valueOf(date), time, bLast, bVolume,
	    				  pvolume);

	        NavigableMap<String, String> dateHash = new java.util.concurrent.ConcurrentSkipListMap<>();	
	        dateHash.put(String.valueOf(ood.getTlong()),ood.rowkeyString);
	        String stkey = (new StringBuffer(ch).append("_").append(date)).toString();
	        StockTradeMinute stm = new StockTradeMinute(stkey,String.valueOf(date),dateHash);
	        stm.insertCF(ks);
	        ood.setReplay(1);
	        //ood.insertCF(CassConnectionPool.getKeyspace());
	        //sHash.put(date+time, ood);
	        //sList.add(ood);
	      }
	      else {
	        ood.setTrade(bLast, bVolume);
	        //ood.insertCF(CassConnectionPool.getKeyspace());
	        //sList.add(ood);
	      }

	      ood.insertCF(ks);
	      pvolume = new BigDecimal(bVolume.toString());

	      this.procssingOod = ood;
	      this.currTime = time;
	      //捨棄過期資料 (保留最近 queue_record 筆)
	      /*
	        while(sList.size() >  Utility.parseInt(queue_record)){
	          OtOhlcData ood2 = (OtOhlcData)sList.remove(0);
	          sHash.remove(ood2.getDate()+ood2.getTime());
	        }*/
	    }

	  public void setTrade(final List<RedisCommandInfo> ks ,String ex, String ch,String last,String volume, String time, int date){
	      if(time.indexOf(".")!=-1) time = time.substring(0,time.indexOf("."));
	      currFullTime = time;

	      java.sql.Time ti = java.sql.Time.valueOf(currFullTime);
	      String stdPervFullTime = (new java.sql.Time(ti.getTime()-60000)).toString();
	      stdPervFullTime = stdPervFullTime.substring(0, stdPervFullTime.lastIndexOf(":"));
	      if (time.lastIndexOf(":") != -1) {
	        time = time.substring(0, time.lastIndexOf(":"));
	      }

	      StockOhlc ood = null;
	      if(this.currTime.equals(time) && this.procssingOod != null){
	        ood = this.procssingOod;

	      }
	      else if (currFullTime.endsWith(":00") &&
	               (!currFullTime.endsWith("13:33:00") &&
	                !currFullTime.endsWith("13:31:00")) && this.procssingOod != null) {
	        if(this.procssingOod.getTime().equals(stdPervFullTime)){
	          BigDecimal bVolume = new BigDecimal(volume);
	          BigDecimal bLast = new BigDecimal(last);
	          //if(ch.startsWith("t00.tw") || ch.startsWith("o00.tw"))
	          //System.out.println(ch+" "+currTime+" = currFullTime ==== "+currFullTime+" "+bLast+" "+bVolume);
	          this.procssingOod.setTrade(bLast, bVolume);
	          this.procssingOod.insertCF(ks);
	          //if(ch.startsWith("t00.tw") || ch.startsWith("o00.tw"))
	          //System.out.println(ch+" "+currTime+" "+procssingOod.getTime()+" = currFullTime update ==== "+currFullTime+" "+procssingOod.getCurrent()+" "+procssingOod.getVolume());
	          pvolume = new BigDecimal(bVolume.toString());
	        }

	        this.procssingOod = null;
	        ood = StockOhlc.getStockOhlc( ex, ch, String.valueOf(date), time);

	      }
	      else {
	        this.procssingOod = null;
	        ood = StockOhlc.getStockOhlc( ex, ch,
	                                     String.valueOf(date), time);
	      }
	      //OtOhlcData ood = (OtOhlcData)sHash.get(date+time);
	      //

	      BigDecimal bLast = new BigDecimal(last);
	      BigDecimal bVolume = new BigDecimal(volume);
	      //if(ch.startsWith("t00.tw") || ch.startsWith("o00.tw"))
	      //    System.out.println(ch+" "+time+"　"+currFullTime+" data:="+bLast+" "+volume+" pvolume:"+pvolume);
	      //System.out.println(time+" pvolume="+bLast+" "+volume+" "+pvolume);
	      /*
	      if(replay && ood!=null){
	        if(ood.getReplay()==0){
	          ood = null;
	        }
	      }*/

	      if (ood == null) {
	        if (pvolume == null)
	          ood = new StockOhlc(ex, ch, String.valueOf(date), time, bLast, bVolume);
	        else
	          ood = new StockOhlc(ex, ch, String.valueOf(date), time, bLast, bVolume,
	                              pvolume);

	        minuteHash.put(String.valueOf(ood.getTlong()),ood.rowkeyString);
	        String stkey = (new StringBuffer(ch).append("_").append(date)).toString();
	        StockTradeMinute stm = new StockTradeMinute(stkey,String.valueOf(date),minuteHash);
	        stm.insertCF(ks);
	        ood.setReplay(1);
	        //ood.insertCF(CassConnectionPool.getKeyspace());
	        //sHash.put(date+time, ood);
	        //sList.add(ood);
	      }
	      else {
	        ood.setTrade(bLast, bVolume);
	        //ood.insertCF(CassConnectionPool.getKeyspace());
	        //sList.add(ood);
	      }

	      ood.insertCF(ks);
	      pvolume = new BigDecimal(bVolume.toString());

	      this.procssingOod = ood;
	      this.currTime = time;
	      //捨棄過期資料 (保留最近 queue_record 筆)
	      /*
	        while(sList.size() >  Utility.parseInt(queue_record)){
	          OtOhlcData ood2 = (OtOhlcData)sList.remove(0);
	          sHash.remove(ood2.getDate()+ood2.getTime());
	        }*/
	    }

	    public Vector getOhclList(String product) {
	      Vector pList = new Vector();// (Vector) ohclHashList.get(product);
	      return pList;
	    }

	    public static void main(String[] args) {
	      OtOhlcManager ss = new OtOhlcManager();
	      /*
	         ss.setTrade("","t00.tw","8318.77","2251","09:00:15",20111010);
	         ss.setTrade("","t00.tw","8325.68","2512","09:00:30",20111010);
	         ss.setTrade("","t00.tw","8328.90","2715","09:00:45",20111010);
	         ss.setTrade("","t00.tw","8330.98","2898","09:01:00",20111010);
	         ss.setTrade("","t00.tw","8334.44","3244","09:01:15",20111010);
	         ss.setTrade("","t00.tw","8330.10","3553","09:01:30",20111010);
	         ss.setTrade("","t00.tw","8331.53","3719","09:01:45",20111010);
	         ss.setTrade("","t00.tw","8332.13","3890","09:02:00",20111010);
	         ss.setTrade("","t00.tw","8328.39","4191","09:02:45",20111010);
	         ss.setTrade("","t00.tw","8327.80","4468","09:02:30",20111010);
	         ss.setTrade("","t00.tw","8327.80","4617","09:02:30",20111010);*/
	      /*
	         ss.setTrade("","8069.tw","21.50","70285","13:23:47",20111010);
	         ss.setTrade("","8069.tw","21.35","70888","13:24:07",20111010);
	         ss.setTrade("","8069.tw","21.55","70998","13:24:27",20111010);
	         ss.setTrade("","8069.tw","21.50","71065","13:24:47",20111010);
	         ss.setTrade("","8069.tw","21.45","105928","13:30:00",20111010);
	*/

	         Vector ooList = ss.getOhclList("t00.tw");
	         for(int i = 0 ; i < ooList.size() ; i++){
	             StockOhlc ood = (StockOhlc)ooList.get(i);
	             System.out.println(ood.getDate()+" "+ ood.getTime()+" = "+ood.getOpen().toString()+","+ood.getHigh().toString()+","+ood.getCurrent().toString()+","+ood.getLow().toString()+","+ood.getVolume().toString());
	         }
	    }

	}
