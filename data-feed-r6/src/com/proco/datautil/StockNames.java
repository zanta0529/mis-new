package com.proco.datautil;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import org.aredis.cache.RedisCommandInfo;

import com.proco.cache.RedisManager;
import com.proco.util.Utility;


public class StockNames extends RedisModelBase {
	public String columnPath = "stocknames";
	
	public StockNames(String date,String a) {
		super(date, a);
		this.rowkeyString = a;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
	}

	public StockNames(String key, String date, NavigableMap<String, String> dataHash) {
		super(date, dataHash);
		// TODO Auto-generated constructor stub
		this.rowkeyString = key;
		this.redisKeyString = columnPath+"_"+date+"_"+rowkeyString;
		this.hashcode = (redisKeyString).hashCode();
		if(this.hashcode<0) this.hashcode = this.hashcode -(this.hashcode*2);
		chk = true;
	}
	
	
	public static void prepardStockNames(List<RedisCommandInfo> rinfos,String exchange,String symbol,String cname, String date){
		String ename = "";
		String ch = symbol;
		if(ch.endsWith(".tw")) ch = ch.substring(0,ch.length()-3);
		
		
		StockNameStore sns = new StockNameStore(date,ch);
		List<Map.Entry<String, String>> sList = sns.get();
		for(Map.Entry<String, String> hcol : sList){
			if(hcol.getKey().equals("en")){
				ename = (String)hcol.getValue();
				ename = ename.toUpperCase();
			}
		}
		prepardStockNames(rinfos,exchange, symbol, cname, ename,  date);
	}	

	public static void prepardStockNames(List<RedisCommandInfo> rinfos,String exchange,String symbol,String cname,String ename, String date){
		StringBuffer sb = new StringBuffer(exchange).append('_').append(symbol);
		
		java.util.TreeMap<String, String> dataHash = new java.util.TreeMap<String,String>();
		dataHash.put(sb.toString(),date);
		StringBuffer subKey0 = new StringBuffer();
		for(char ch : symbol.toCharArray()){
			if(ch=='.') break;
			subKey0 = subKey0.append(ch);
			StockNames sn = new StockNames(subKey0.toString(),date,dataHash);
			//System.out.println(subKey0.toString());
			sn.insertCF(rinfos);
		}
		boolean first = true;
		while (cname.length() > 0) {
			StringBuffer subKey1 = new StringBuffer();
			for (char ch : cname.toCharArray()) {
				if (ch == '.')
					break;
				subKey1 = subKey1.append(ch);
				if (first || Utility.parseInt(subKey1.toString()) == 0){
					StockNames sn = new StockNames(subKey1.toString(),date, dataHash);
					//System.out.println(subKey1.toString());
					sn.insertCF(rinfos);
		        }
			}
			cname = cname.substring(1, cname.length());
			first = false;
		}
		
		first = true;
		String[] enames = ename.split(" ");
		for(String en : enames){
			while (en.length() > 0) {
				StringBuffer subKey1 = new StringBuffer();
				for (char ch : en.toCharArray()) {
					if (ch == '.')
						break;
					subKey1 = subKey1.append(ch);
					if (first || Utility.parseInt(subKey1.toString()) == 0) {
						StockNames sn = new StockNames(subKey1.toString(),date, dataHash);
						//System.out.println(subKey1.toString());
						sn.insertCF(rinfos);
					}
		        }
				en = en.substring(1, en.length());
				first = false;
			}
		}


	}
	
	public static void main(String[] args) {
		RedisManager.init();
		String ex = "tse";
		String symbol = "2454.tw";
		String cname = "聯發科";
		String ename = "MEDIA TEK";
		String date = "20121130";
		//prepardStockNames(ex,symbol,cname,ename,date);
	}	
}
