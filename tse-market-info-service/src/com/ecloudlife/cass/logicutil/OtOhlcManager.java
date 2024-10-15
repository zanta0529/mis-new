package com.ecloudlife.cass.logicutil;

import java.math.BigDecimal;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ecloudlife.util.ConfigData;
import com.ecloudlife.util.Utility;
import com.proco.cache.JedisManager;
import com.proco.datautil.StockOhlc;
import com.proco.datautil.StockTradeDate;
import com.proco.datautil.StockTradeMinute;


public class OtOhlcManager {
	
  public static java.util.concurrent.ConcurrentSkipListMap<String, Vector<OtOhlcData>> ohlcMap = new java.util.concurrent.ConcurrentSkipListMap<String, Vector<OtOhlcData>>();

  BigDecimal pvolume = null;
  String currTime = "";
  StockOhlc procssingOod = null;
  long queryTime = -1;

  public OtOhlcManager(){

  }

  public static long getOhlcPatchTime(){
    int c = ConfigData.ohlcPatchTime;
    if(c==-1) c=0;
    //return 60000;
    return c;
  }



    public Vector getOhclList(String product) {
      Vector pList = new Vector();// (Vector) ohclHashList.get(product);
      return pList;
    }

    public Vector<OtOhlcData> getOHLC(String ex, String ch, long start, long end, int space) {
    	
    	String ckey = ex+","+ch+","+start+","+end+","+space;
    	this.queryTime += 1;
    	
    	Vector<OtOhlcData> result = ohlcMap.get(ckey);
    	if(result!=null) return result;
    	
    	
    	long spacemillis = space * 60000;

    	String startDate = Utility.getDateStr(start);
    	String endDate = Utility.getDateStr(end);
    	StockTradeDate std = new StockTradeDate(ex);
    	String sdate = std.get();

    	///System.out.println(Utility.cleanString("getOHLC.sdate----->"+sdate));
    	
    	java.util.Vector<String> oKeys = new Vector<String>();
    	//for(int i = 0 ; i < tradedays.size() ; i++)	
    	{
      
        //String sdate = (String)col.getName();
    		java.lang.StringBuilder subKey = new StringBuilder(ch).append('_').append(sdate);
    		StockTradeMinute stm = new StockTradeMinute(sdate,subKey.toString());
    		List<Map.Entry<String,String>> trademins = stm.get();
    		this.queryTime += stm.getQueryTime();
    		for(int j = 0 ; j < trademins.size() ; j++){
    			Map.Entry mol = trademins.get(j);
    			if(mol.getKey().equals("key")) continue;
    			String olcKey = (String)mol.getValue();
    			//System.out.println("getOHLC.olcKey----->"+olcKey);
    			oKeys.add(olcKey);
    		}
    	}

    	Hashtable<String, String> eq = new Hashtable<String, String> ();
    	eq.put("ex", ex);
    	eq.put("ch", ch);
    	Hashtable<String, String> gt = new Hashtable<String, String> ();
    	gt.put("tlong", start + "");
    	Hashtable<String, String> lt = new Hashtable<String, String> ();
    	lt.put("tlong", end + "");
      
    	Vector<OtOhlcData> sList = new Vector<OtOhlcData>();
    	Hashtable<String,OtOhlcData> sHash = new Hashtable<String,OtOhlcData>();
    	String lastVolume = "0";	
    	String lastDate = "";
      
    	for(String olcKey : oKeys) {
    		StockOhlc sd = new StockOhlc(ex,sdate,olcKey);
    		Map<String, String> itd = sd.getMap();
    		//System.out.println("getOHLC.StockOhlc----->"+itd.toString());
    		
    		
    		String subVolume = "0";
    		String hctlong = itd.get("tlong");
    		String hcs = itd.get("s");
    		String hcv = itd.get("v");
    		String hco = itd.get("o");
    		String hch = itd.get("h");
    		String hcl = itd.get("l");
    		String hcz = itd.get("z");
    	  
          //Split DateTime
    		long millis = Utility.parseLong(hctlong);
    		millis = Utility.splitTimeSpace(millis,spacemillis);
    		String datetime = Utility.getDateTimeStr(millis);
    		String date = datetime.substring(0,8);

          //Check Change Date
    		if(lastDate.equals("")) lastDate = date;
    		if(!lastDate.equals(date)){
    			lastDate = date;
    			lastVolume = "0";
    		}
          
          ///System.out.println(Utility.cleanString("OtOhlcManager : "+olcKey+" "+hcs+" "+hcv));
          if(hcs==null && hcv==null) continue;
    		if(hcs!=null && hcv!=null){
    			String s_val = hcs;
    			String v_val = hcv;
    			BigDecimal s_dec = new BigDecimal(s_val);
    			BigDecimal v_dec = new BigDecimal(v_val);
    			subVolume = v_dec.subtract(s_dec).toString();
    			//System.out.println("OtOhlcManager0 : "+subVolume+" = "+v_dec+" - "+s_dec);
    		} else {
    			String s_val = lastVolume;
    			String v_val = hcv;
    			BigDecimal s_dec = new BigDecimal(s_val);
    			BigDecimal v_dec = new BigDecimal(v_val);
    			subVolume = v_dec.subtract(s_dec).toString();
            //System.out.println("OtOhlcManager1 : "+subVolume+" = "+v_dec+" - "+s_dec);
    		}
    		lastVolume = hcv;

    		BigDecimal o_dec = new BigDecimal(hco);	
    		BigDecimal h_dec = new BigDecimal(hch);
    		BigDecimal l_dec = new BigDecimal(hcl);
          	BigDecimal c_dec = new BigDecimal(hcz);
          	BigDecimal v_dec = new BigDecimal(hcv);
          	BigDecimal s_dec = new BigDecimal(subVolume);          
          
          //Build OHLC
          	OtOhlcData od = sHash.get(datetime);	
          	if(od==null){ //Build New OHLC Data
          		od = new OtOhlcData(millis,datetime.substring(0,8),datetime.substring(8,12),o_dec,v_dec);
          		od.setTrade(h_dec,v_dec);
          		od.setTrade(l_dec,v_dec);	
          		od.setTrade(c_dec,v_dec);
          		od.setSubvolume(s_dec);
          		sList.add(od);
          		sHash.put(datetime,od);
          	} else { //Marge OHLC Data
          		od.setTrade(o_dec,v_dec);	
          		od.setTrade(h_dec,v_dec);
          		od.setTrade(l_dec,v_dec);
          		od.setTrade(c_dec,v_dec);
          		od.setSubvolume(s_dec);
          	}
    	  
    	}

    	ohlcMap.put(ckey, sList);
    	return sList;
    }

    public static Vector<OtOhlcData> getOHLC0(String ex, String ch, long start, long end, int space) {
    	
    	long spacemillis = space * 60000;

    	String startDate = Utility.getDateStr(start);
    	String endDate = Utility.getDateStr(end);
    	StockTradeDate std = new StockTradeDate(ex);
    	String sdate = std.get();
    	///System.out.println(Utility.cleanString("getOHLC.sdate----->"+sdate));
    	
    	java.util.Vector<String> oKeys = new Vector<String>();
    	//for(int i = 0 ; i < tradedays.size() ; i++)	
    	{
      
        //String sdate = (String)col.getName();
    		java.lang.StringBuilder subKey = new StringBuilder(ch).append('_').append(sdate);
    		StockTradeMinute stm = new StockTradeMinute(sdate,subKey.toString());
    		List<Map.Entry<String,String>> trademins = stm.get();
    		for(int j = 0 ; j < trademins.size() ; j++){
    			Map.Entry mol = trademins.get(j);
    			if(mol.getKey().equals("key")) continue;
    			String olcKey = (String)mol.getValue();
    			//System.out.println("getOHLC.olcKey----->"+olcKey);
    			oKeys.add(olcKey);
    		}
    	}

    	Hashtable<String, String> eq = new Hashtable<String, String> ();
    	eq.put("ex", ex);
    	eq.put("ch", ch);
    	Hashtable<String, String> gt = new Hashtable<String, String> ();
    	gt.put("tlong", start + "");
    	Hashtable<String, String> lt = new Hashtable<String, String> ();
    	lt.put("tlong", end + "");
      
    	Vector<OtOhlcData> sList = new Vector<OtOhlcData>();
    	Hashtable<String,OtOhlcData> sHash = new Hashtable<String,OtOhlcData>();
    	String lastVolume = "0";	
    	String lastDate = "";
      
    	for(String olcKey : oKeys) {
    		StockOhlc sd = new StockOhlc(ex,sdate,olcKey);
    		Map<String, String> itd = sd.getMap();
    		//System.out.println("getOHLC.StockOhlc----->"+itd.toString());
    		
    		
    		String subVolume = "0";
    		String hctlong = itd.get("tlong");
    		String hcs = itd.get("s");
    		String hcv = itd.get("v");
    		String hco = itd.get("o");
    		String hch = itd.get("h");
    		String hcl = itd.get("l");
    		String hcz = itd.get("z");
    	  
          //Split DateTime
    		long millis = Utility.parseLong(hctlong);
    		millis = Utility.splitTimeSpace(millis,spacemillis);
    		String datetime = Utility.getDateTimeStr(millis);
    		String date = datetime.substring(0,8);

          //Check Change Date
    		if(lastDate.equals("")) lastDate = date;
    		if(!lastDate.equals(date)){
    			lastDate = date;
    			lastVolume = "0";
    		}
          
          ///System.out.println(Utility.cleanString("OtOhlcManager : "+olcKey+" "+hcs+" "+hcv));
          if(hcs==null && hcv==null) continue;
    		if(hcs!=null && hcv!=null){
    			String s_val = hcs;
    			String v_val = hcv;
    			BigDecimal s_dec = new BigDecimal(s_val);
    			BigDecimal v_dec = new BigDecimal(v_val);
    			subVolume = v_dec.subtract(s_dec).toString();
    			//System.out.println("OtOhlcManager0 : "+subVolume+" = "+v_dec+" - "+s_dec);
    		} else {
    			String s_val = lastVolume;
    			String v_val = hcv;
    			BigDecimal s_dec = new BigDecimal(s_val);
    			BigDecimal v_dec = new BigDecimal(v_val);
    			subVolume = v_dec.subtract(s_dec).toString();
            //System.out.println("OtOhlcManager1 : "+subVolume+" = "+v_dec+" - "+s_dec);
    		}
    		lastVolume = hcv;

    		BigDecimal o_dec = new BigDecimal(hco);	
    		BigDecimal h_dec = new BigDecimal(hch);
    		BigDecimal l_dec = new BigDecimal(hcl);
          	BigDecimal c_dec = new BigDecimal(hcz);
          	BigDecimal v_dec = new BigDecimal(hcv);
          	BigDecimal s_dec = new BigDecimal(subVolume);          
          
          //Build OHLC
          	OtOhlcData od = sHash.get(datetime);	
          	if(od==null){ //Build New OHLC Data
          		od = new OtOhlcData(millis,datetime.substring(0,8),datetime.substring(8,12),o_dec,v_dec);
          		od.setTrade(h_dec,v_dec);
          		od.setTrade(l_dec,v_dec);	
          		od.setTrade(c_dec,v_dec);
          		od.setSubvolume(s_dec);
          		sList.add(od);
          		sHash.put(datetime,od);
          	} else { //Marge OHLC Data
          		od.setTrade(o_dec,v_dec);	
          		od.setTrade(h_dec,v_dec);
          		od.setTrade(l_dec,v_dec);
          		od.setTrade(c_dec,v_dec);
          		od.setSubvolume(s_dec);
          	}
    	  
    	}

    	
    	return sList;
    }
    
    public static void updateStockOHLC() {
    	//String ckey = ex+"|"+ch+"|"+start+"|"+end+"|"+space;
    	Map.Entry<String,Vector<OtOhlcData>> ent = ohlcMap.firstEntry();
    	int updated_cnt = 0;
    	String lastSymbol = "";
    	long queryMillisTime = System.currentTimeMillis();
    	while(true) {
    		if(ent==null) break;
    		String ckey = ent.getKey();
    		String[] keys = ckey.split(",");
    		if(keys.length==5) {
    			String ex = keys[0];
    			String ch = keys[1];
    			String startStr = keys[2];
    			String endStr = keys[3];
    			String spaceStr = keys[4];
    			long start = Utility.parseLong(startStr);
    			long end = Utility.parseLong(endStr);
    			int space = Utility.parseInt(spaceStr);
    			Vector<OtOhlcData> rse = getOHLC0(ex, ch, start, end, space);
    			if(rse!=null) {
    				ohlcMap.put(ckey, rse);
    				updated_cnt++;
    				lastSymbol = ckey;
    			}
    		}
    		ent = ohlcMap.higherEntry(ckey);
    	}   
    	queryMillisTime = System.currentTimeMillis() - queryMillisTime;
        System.out.println(Utility.getFullyDateTimeStr()+" OtOhlcManager.updateStockOHLC : "+updated_cnt+" "+lastSymbol+" queryMillisTime:"+queryMillisTime+" JedisManager.getCheckTime:"+JedisManager.getCheckTime()+" JedisManager.isConnected:"+JedisManager.isConnected());
    	//ohlcMap.put(ckey, sList);
    }
    

    public long getQueryTime() {
    	return queryTime;
    }
    
    public static void main(String[] args) {
    	Thread th = new Thread(){
    		public void run(){
    			while(true){
    				OtOhlcManager.updateStockOHLC();
    				Utility.sleep(10000L);	
    			}
    		}
    	};	
    	th.start();
    }    
}
