package com.proco.datautil;

import com.ecloudlife.util.Utility;
import com.proco.cache.JedisManager;

public class StockTradeDate {
	public String columnPath = "";
	public String rowkeyString;
	public String redisKeyString;

	public StockTradeDate(String rowkeyString) {
		// TODO Auto-generated constructor stub
		 this.rowkeyString = rowkeyString;
		 this.redisKeyString = rowkeyString;	
	}
	
	public String get() {
		try {
			String date = JedisManager.getDateByMarket(redisKeyString);
			return date;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Utility.getDateStr();
	}	
}