package com.ecloudlife.cass.logicutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.proco.datautil.StockDetail;

public class StockNewboard {
  public static java.util.concurrent.ConcurrentHashMap<String, List<Map<String, String>>>
      cateHash = new java.util.concurrent.ConcurrentHashMap<String, List<Map<String, String>>> ();

  public static List getNewboardIndex1(String ex, String key, String value,
                                       String dt) {

    String ikey = new StringBuilder(ex).append('_').append(key).append('_').
        append(value).append('_').append(dt).toString();
    List<Map<String, String>> rList = cateHash.get(ikey);
    
    if (rList == null) {
    	rList = new ArrayList<Map<String, String>>();
    	com.proco.datautil.StockNewboard std = new com.proco.datautil.StockNewboard(ex, dt,ikey);
    	List<Map.Entry<String, String>> zz = std.get();
    	
    	for (int i = 0; i < zz.size(); i++) {
    		Map.Entry<String, String> col = zz.get(i);
    		//zkeys.add( (String) col.getValue());
    		StockDetail sd = new StockDetail(col.getValue());
    		Map<String, String> det = sd.getMap();
    		if(!det.isEmpty()) rList.add(det);
    	}    	
    	if (!key.equals("ip")) //暫緩或延後須即時，不 cache
    		cateHash.put(ikey, rList);
    }
    return rList;
  }
}
