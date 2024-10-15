package com.ecloudlife.cass.logicutil;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ecloudlife.util.ConfigData;
import com.ecloudlife.util.Utility;
import com.proco.datautil.StockDateList;
import com.proco.datautil.StockTimeLine;

public class StocksTimelineManager {

	public static String rdate0 = "";
	public static java.util.concurrent.ConcurrentHashMap<String,Long> querySet = new java.util.concurrent.ConcurrentHashMap<String,Long>();
	public static java.util.concurrent.ConcurrentHashMap<String,String> lastChangeTime = new java.util.concurrent.ConcurrentHashMap<String,String>();
	public static java.util.concurrent.ConcurrentHashMap<String,org.json.JSONObject> cacheObject = new java.util.concurrent.ConcurrentHashMap<String,org.json.JSONObject>();

	static java.util.concurrent.atomic.AtomicBoolean updating = new java.util.concurrent.atomic.AtomicBoolean(false);
	
	
	public static void clear() {
		querySet.clear();
		lastChangeTime.clear();
		cacheObject.clear();
		System.out.println(Utility.getFullyDateTimeStr()+ " StocksTimelineManager.reset:"+StocksTimelineManager.rdate0);
	}
	
	public static JSONObject getStocksTimeline(String rdate, String fqy) {
		long a = System.currentTimeMillis()+(ConfigData.timelineCacheHours*60*60*1000);
		querySet.put(fqy, a);
		String lastTimeKey = rdate +"_"+ fqy;
		
		JSONObject js = cacheObject.get(lastTimeKey);
		
		return js;
	}
	
	
	public static void updateStocksTimeline() {
		if(!updating.getAndSet(true)) {
			try {
				Set<Map.Entry<String,Long>> ents = querySet.entrySet();
				for(Map.Entry<String,Long> ent : ents) {
					Long execTime = ent.getValue();
					if(System.currentTimeMillis() > execTime) continue;
					exec(ent.getKey());
				}	
			} finally {
				updating.set(false);
			}		
		}
	}
	
	
	public static void exec(String fqy) {
		long queryMillis = 0;
		String ex="tse";
		if(fqy==null)  fqy = "1";
		String date = Utility.getDateStr();
		String time = null;
		if(time==null) {
			long qMillis = System.currentTimeMillis();
			long decMillis = qMillis%(Utility.parseInt(fqy)*60000);
			qMillis = qMillis - decMillis;

			time = Utility.getTimeStr(qMillis);
			time = time.substring(0,4)+"00";
		}
		
		int t = Utility.parseInt(time);
		if(t>133000 && t<=133500){
			time = "133500";
		}		
		
		String rdate = ExLastTradeDate.getLastTradeDate(null, ex);
		if(rdate!=null) {
			if(!rdate.equals(rdate0)) {
				StocksTimelineManager.clear();
				rdate0 = rdate;
			}
		}
		
		long qtime = 0;
		if(date.equals(rdate)){
			long baseMillis = Utility.getDateFormatTransMillis(date);
		    long dataMillis = Utility.getDateTimeFormatTransMillis(date+time) - baseMillis;
			qtime = 99999999L- dataMillis;
		}

		StockTimeLine stl = new StockTimeLine(ex, rdate, "tse_t00.tw");
		String tline = stl.getZrange(qtime);
		if(tline==null) return;
		org.json.JSONObject js1 = new org.json.JSONObject(tline);
		
		String indexTime = js1.optString("t");
		if(indexTime.equals("")) return;
		
		String lastTimeKey = rdate +"_"+ fqy;
		
		
		String indexTime0 = lastChangeTime.get(lastTimeKey);
		if(indexTime0==null) indexTime0 = "";
		if(indexTime0.equals(indexTime)) {
			System.out.println(Utility.getFullyDateTimeStr()+" StocksTimelineManager.exec_skip--------------->:"+lastTimeKey+"="+indexTime+" - "+indexTime0);
			return;
		}
		
		System.out.println(Utility.getFullyDateTimeStr()+" StocksTimelineManager.exec--------------->:"+lastTimeKey+"="+indexTime+" - "+indexTime0);
		
		try {
			JSONObject js = exec0(ex, date, rdate, fqy, time, queryMillis);			
			cacheObject.put(lastTimeKey, js);
			lastChangeTime.put(lastTimeKey, indexTime);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static JSONObject exec0(String ex, String date, String rdate, String fqy, String time,long queryMillis) throws Exception {
		org.json.JSONObject js = new org.json.JSONObject();
		js.put("req",date+time);
		js.put("now",Utility.getDateStr()+Utility.getTimeStr());

		long qtime = 0;
		if(date.equals(rdate)){
			long baseMillis = Utility.getDateFormatTransMillis(date);
		    long dataMillis = Utility.getDateTimeFormatTransMillis(date+time) - baseMillis;
			qtime = 99999999L- dataMillis;
		}

		Vector<String> keys = new Vector<String>();
		Vector<String> ckeys = new Vector<String>();
		//2454.tw_tse_20130531_B
		StockDateList sdl = new StockDateList(rdate);

		List<Map.Entry<String,String>> hcols_0 = sdl.get();
		queryMillis+=sdl.getQueryTime();


		org.json.JSONArray ja = new JSONArray();

		for(Map.Entry<String,String> col : hcols_0){
			String key = "";
			 if(col.getKey().length()==7 || col.getKey().length()==8 || col.getKey().length()==9)
				 key = ex+"_"+col.getKey();
			 else continue;
			  
			  StockTimeLine stl = new StockTimeLine(ex, rdate, key);
			  String tline = stl.getZrange(qtime);
			  if(tline==null) continue;
			  org.json.JSONObject js1 = new org.json.JSONObject(tline);
			  
		  String v = "0";
		  String y = "0";
		  String z = "0";
		  if(!js1.optString("v").equals("")){
		    v = js1.optString("v");
		  }
		  if(!js1.optString("y").equals("")){
		    y = js1.optString("y");
		  }
		  if(!js1.optString("z").equals("")){
		    z = js1.optString("z");
		  }
		  js1 = exec1(js1);

		  if(!js1.optString("%").equals("")){
		    String tt = js1.optString("%");
		    if(tt.equals("13:35:00")){
		      js1.put("%","13:30:00");
		    }
		  }
		  queryMillis+=stl.getQueryTime();
		  ja.put(js1);
		}

		js.put("stocks",ja);
		js.put("size",ja.length());
		js.put("queryMillis",queryMillis/1000);
		
		return js;
	}

	public static JSONObject exec1(org.json.JSONObject js) throws Exception {
	  String v = "0";
	  String y = "0";
	  String z = "0";
	  if(!js.optString("v").equals("")){
	    v = js.optString("v");
	  }
	  if(!js.optString("y").equals("")){
	    y = js.optString("y");
	  }
	  if(!js.optString("z").equals("")){
	    z = js.optString("z");
	  } else {
	    z = y;
	  }
	  if(z.equals("0")) {
		  //z = y;
		  //js.put("z", z);
		  js.remove("z");
		  js.put("change","+0.0000");
		  js.put("percent","0.00");
	  }  else {
		  java.math.BigDecimal vol = new java.math.BigDecimal(v);
		  java.math.BigDecimal yest = new java.math.BigDecimal(y);
		  java.math.BigDecimal last = new java.math.BigDecimal(z);
		  java.math.BigDecimal chg = last.subtract(yest);
		  //java.math.BigDecimal pre = chg.divide(yest,5,java.math.RoundingMode.HALF_EVEN).multiply(new java.math.BigDecimal(10000).divide(new java.math.BigDecimal(100),2,java.math.RoundingMode.HALF_EVEN));
		  //pre = pre.divide(new java.math.BigDecimal(1),2,java.math.RoundingMode.DOWN);
		  java.math.BigDecimal pre = chg.divide(yest,5,java.math.RoundingMode.DOWN).multiply(new java.math.BigDecimal(10000).divide(new java.math.BigDecimal(100),2,java.math.RoundingMode.DOWN));
		  pre = pre.divide(new java.math.BigDecimal(1),2,java.math.RoundingMode.HALF_UP);

		  vol = vol.divide(new java.math.BigDecimal(100),0,java.math.RoundingMode.FLOOR);
		  String outString = "";

		  String cstr = chg.toString();
		  String pstr = pre.toString();
		  if(!cstr.startsWith("-") && !cstr.equals("0.00")) cstr = "+" + cstr;
		  if(!pstr.startsWith("-") && !pstr.equals("0.00")) pstr = "+" + pstr;

		  js.put("change",cstr);
		  js.put("percent",pstr);
	  }



	return js;
	}
	
	
	  public static void main(String[] args) {
		  Thread th = new Thread(){
			  public void run(){
				  while(true){
					  StocksTimelineManager.updateStocksTimeline();
					  Utility.sleep(10000L);	
				  }
			  }
		  };	
		  th.start();
	  }	
	
	
}
