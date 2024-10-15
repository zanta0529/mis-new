package com.ecloudlife.cass.logicutil;

import com.ecloudlife.util.InitSchedule;
import com.ecloudlife.util.Utility;
import com.proco.cache.JedisManager;
import com.proco.datautil.StockDetail;
import com.proco.datautil.StockNameStore;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

public class StockCategory {

  public static java.util.concurrent.ConcurrentHashMap<String, JSONObject>
      stnameHash = new java.util.concurrent.ConcurrentHashMap<String, JSONObject> ();
  public static java.util.concurrent.ConcurrentHashMap<String, List<Map<String, String>>>
  	cateHash = new java.util.concurrent.ConcurrentHashMap<String, List<Map<String, String>>> ();

  public static java.util.concurrent.ConcurrentHashMap<String, Map<String, String>>
  	symbolsRows = new java.util.concurrent.ConcurrentHashMap<String, Map<String, String>> ();  
 
  public static java.util.concurrent.ConcurrentHashMap<String, Map<String, String>>
	detailRows = new java.util.concurrent.ConcurrentHashMap<String, Map<String, String>> ();  

  
  public static JSONObject queryConf0 = new JSONObject();

  public static List getCategoryIndex1(String ex,String key,String value,String dt, String info){
	  return getCategoryIndex1(ex, key, value, dt, info, true);
  }

  public static List getCategoryIndex1(String ex,String key,String value,String dt, String info, boolean cache){
    String ikey = new StringBuilder(ex).append('_').append(key).append('_').
        append(value).append('_').append(dt).toString();
    
    long ct = System.currentTimeMillis();
    boolean q = false;
    
    List<Map<String, String>> rList = cateHash.get(ikey);
    if(rList==null || !cache){
    	q = true;
    	rList = new ArrayList<Map<String, String>>();
    	com.proco.datautil.StockCategory std = new com.proco.datautil.StockCategory(dt,ikey);
  		List<Map.Entry<String, String>> zz = std.get();
	
  		for (int i = 0; i < zz.size(); i++) {
  			Map.Entry<String, String> col = zz.get(i);
  			String ckey = col.getValue();
  			Map<String, String> det = detailRows.get(ckey);
  			if(det==null || det.isEmpty()) {
  				StockDetail sd = new StockDetail(ckey);
  				det = sd.getMap();
  				detailRows.put(ckey, det);
  			}
  			if(!det.isEmpty()) rList.add(det);
  		}    	
  		if (!key.equals("ip") && rList!=null && !rList.isEmpty()) //暫緩或延後須即時，不 cache
  			cateHash.put(ikey, rList);
    }
    //System.out.println(Utility.getFullyDateTimeStr()+" StockCategory.getCategoryIndex1:"+ikey+" c:"+rList.size() +" q:"+q+" cache:"+cache+" "+(System.currentTimeMillis()-ct)+"ms "+" info:"+info);
    return rList;
  }

  public static long queryMillisTime = -1;
  public static int updated_cnt = -1;
  public static String date0 = "";
  public static void updateCategoryIndex() {
  	int updated_cnt0 = 0;
  	String lastSymbol = "";
  	long queryMillisTime0 = System.currentTimeMillis();
  	
  	String date = ExLastTradeDate.getLastTradeDate(null, "tse");
  	if(!date.equals(date0)) {
        InitSchedule.init("updateCategoryIndex Change Date:"+date0+"-->"+date+" ",true);
        StockCategory.stnameHash.clear();
        StockCategory.cateHash.clear();
  		date0=date;
  	}
  	
  	final JSONObject queryConf = queryConf0;
  	if(queryConf!=null && date!=null && !date.equals("")) {
  		Set<String> qkeys = queryConf.keySet();
  		for(String qkey : qkeys) {
  			JSONArray ja = queryConf.optJSONArray(qkey);
  			if(ja!=null) {
  				for(int inx = 0 ; inx < ja.length() ; inx++) {
  					JSONObject js0 = ja.getJSONObject(inx);
  					Set<String> sqkeys = js0.keySet();
  					for(String sqkey : sqkeys) {
  						String val = js0.optString(sqkey);
  						if(!val.equals("")) {
  							//long queryMillisTime0 = System.currentTimeMillis();
  							List<Map<String, String>> rList0 = getCategoryIndex1("tse",sqkey,val,date,"updateCategoryIndex",false);
  		                    for (Map<String, String> row : rList0) {
  		                        String ch = row.get("ch");
  		                        if(ch==null) continue;
  		                        if (ch.endsWith(".tw"))
  		                            ch = ch.substring(0, ch.length() - 3);  
  		                        
  		                        Map<String, String> snsMap = com.ecloudlife.cass.logicutil.StockCategory.symbolsRows.get(ch);
  		                        if(snsMap==null){
  		                            StockNameStore sns = new StockNameStore(date,ch);
  		                            snsMap = sns.getMap();   
  		                            com.ecloudlife.cass.logicutil.StockCategory.symbolsRows.put(ch, snsMap);
  		                        }  		                        
  		                    }
  							
  		                    List<Map<String, String>> rList1 = getCategoryIndex1("otc",sqkey,val,date,"updateCategoryIndex",false);
  		                    for (Map<String, String> row : rList1) {
  		                        String ch = row.get("ch");
  		                        if(ch==null) continue;
  		                        if (ch.endsWith(".tw"))
  		                            ch = ch.substring(0, ch.length() - 3);  		                        

  		                        Map<String, String> snsMap = com.ecloudlife.cass.logicutil.StockCategory.symbolsRows.get(ch);
		                        if(snsMap==null){
  		                            StockNameStore sns = new StockNameStore(date,ch);
  		                            snsMap = sns.getMap();   
  		                            com.ecloudlife.cass.logicutil.StockCategory.symbolsRows.put(ch, snsMap);
  		                        }
  		                    }
  		                    

  		                    updated_cnt0++;
  							lastSymbol = sqkey+"_"+val+"_"+date;
  							//queryMillisTime0 = System.currentTimeMillis() - queryMillisTime0;
  							//System.out.println(Utility.getFullyDateTimeStr()+" StockCategory.updateCategoryIndex : "+sqkey+" "+val+" "+date+" tse_map:"+rList0.size()+" queryMillisTime0:"+queryMillisTime0);
  						}
  					}
  				}			  
  			}
  		}
  	}
  	queryMillisTime0 = System.currentTimeMillis() - queryMillisTime0;
  	queryMillisTime = queryMillisTime0;
  	updated_cnt = updated_cnt0;
  	System.out.println(Utility.getFullyDateTimeStr()+" StockCategory.updateCategoryIndex:"+date+" "+updated_cnt0+" "+lastSymbol+" queryMillisTime:"+queryMillisTime+" queryConf:"+queryConf.length()+" cateHash:"+cateHash.size()+" symbolsRows:"+symbolsRows.size()+" JedisManager.getCheckTime:"+JedisManager.getCheckTime()+" JedisManager.isConnected:"+JedisManager.isConnected());
  }

  static long confMillis = 0;
  public static void setQueryConf(JSONObject queryConf,String info) {
	  if(StockCategory.queryConf0.isEmpty() && (System.currentTimeMillis()-confMillis)> 3600000L) {
		  confMillis = System.currentTimeMillis();
		  queryConf0 = queryConf;  
		  System.out.println(Utility.getFullyDateTimeStr()+" StockCategory.setQueryConf Init by "+info+": " +queryConf0.length()+" "+ queryConf0.toString());
	  }
  }
  
  public static void main(String[] args) {
  	Thread th = new Thread(){
		public void run(){
			while(true){
				try {
					StockCategory.updateCategoryIndex();
					Utility.sleep(120000L);						
				} catch(Exception ex) {	
				}
			}
		}
	};	
	th.start();
  }
}
