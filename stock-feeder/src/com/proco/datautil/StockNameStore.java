package com.proco.datautil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.aredis.cache.RedisCommandInfo;

import com.proco.cache.RedisManager;
import com.proco.io.ConfigData;
import com.proco.util.LineInputStream;
import com.proco.util.Utility;

public class StockNameStore extends RedisModelBase {
	public String columnPath = "stocknamestore";
	static String date0 = Utility.getDateStr();
	
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

	public static void main(String[] args0) {
		//insertWarrantRef("WARRANT.CSV","MS950");
		
		//args0 = new String[] {"20240422"};
		
		Utility.writeStringArray("name", args0);
		String[] args = Utility.readStringArray("name");
		if(args==null) args = new String[0];
			  
			  
		String basePath ="";
		if(args.length==1){
			date0 = args[0];
		} else date0 = Utility.getDateStr();
		
		/*
		if(args.length==1){
			basePath = args[0];
			if(!basePath.endsWith("/")){
		         basePath = basePath +"/";
			}
		}*/
        String cassHost = ConfigData.cassHost;
        String cassPass = ConfigData.cassPass;
        int port0 = ConfigData.port;

        int maxActive = 1;
        if(!cassHost.equals("")) RedisManager.redis = cassHost;
        if(!cassPass.equals("")) {
      	  RedisManager.initAuthPath(cassPass);
        }
        System.out.println("Insert Date : "+date0 );	
        System.out.println("Redis : "+RedisManager.redis );	
        System.out.println("Redis Conf Path: "+RedisManager.authPath );
        System.out.println("Active: "+maxActive );
        
        
        RedisManager.init(maxActive);
		
		
		String basePath0 = Utility.cleanString(basePath);
		insertEtfIndexDetail(basePath0);
		
		Utility.sleep(5000);
		System.exit(0);

		//insertWarrantFullName("TICH","MS950");
		//insertWarrantFullName("OIBR","MS950");
	}

	public static void insertEtfIndexDetail(String date1,String inxBasePath){
		date0 = date1;
		insertEtfIndexDetail(inxBasePath);
	}
	
	public static void insertEtfIndexDetail(String inxBasePath){
		
		List<RedisCommandInfo> ks = new ArrayList<RedisCommandInfo>();
		
		TreeMap<String,String> dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		StockNameStore sns = new StockNameStore("0050",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("0051",date0,dhash);
		sns.insertCF(ks);


		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		dhash.put("cnu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		sns = new StockNameStore("0052",date0,dhash);
		sns.insertCF(ks);


		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("0053",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("0054",date0,dhash);
		sns.insertCF(ks);


		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("0055",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("0056",date0,dhash);
		sns.insertCF(ks);


		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		dhash.put("cnu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		sns = new StockNameStore("0057",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		dhash.put("cnu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		sns = new StockNameStore("0058",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		dhash.put("cnu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		sns = new StockNameStore("0059",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("0060",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("0061",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("006201",date0,dhash);
		sns.insertCF(ks);
		
		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("006202",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("006203",date0,dhash);
		sns.insertCF(ks);
		

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://sitc.sinopac.com.tw/web/etf/tradeinfo_nav.aspx");
		dhash.put("cnu","http://sitc.sinopac.com.tw/web/etf/tradeinfo_nav.aspx");
		sns = new StockNameStore("006204",date0,dhash);
		sns.insertCF(ks);
		
		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert_en.aspx");
		dhash.put("cnu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		sns = new StockNameStore("006205",date0,dhash);
		sns.insertCF(ks);


		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.p-shares.com/en/page6.asp");
		dhash.put("cnu","http://www.p-shares.com/page6.asp");
		sns = new StockNameStore("006206",date0,dhash);
		sns.insertCF(ks);


		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.fhtrust.com.tw/funds/fund_ETF_RTnav_alarm.asp?QueryFund=ETF01");
		dhash.put("cnu","http://www.fhtrust.com.tw/funds/fund_ETF_RTnav_alarm.asp?QueryFund=ETF01");
		sns = new StockNameStore("006207",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert_en.aspx");
		dhash.put("cnu","http://etrade.fsit.com.tw/ETF/etf/realtime_fund_nav_advert.aspx");
		sns = new StockNameStore("006208",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.assetmanagement.hsbc.com.tw/etf/etf_message.asp?ETFOK=N");
		dhash.put("cnu","http://www.assetmanagement.hsbc.com.tw/etf/etf_message.asp?ETFOK=N");
		sns = new StockNameStore("0080",date0,dhash);
		sns.insertCF(ks);
		
		
		dhash = new TreeMap<String,String>();
		dhash.put("enu","http://www.assetmanagement.hsbc.com.tw/etf/etf_message.asp?ETFOK=N");
		dhash.put("cnu","http://www.assetmanagement.hsbc.com.tw/etf/etf_message.asp?ETFOK=N");
		sns = new StockNameStore("0081",date0,dhash);
		sns.insertCF(ks);

		dhash = new TreeMap<String,String>();
		dhash.put("enu","https://www.kgifund.com.tw/50_00.htm");
		dhash.put("cnu","https://www.kgifund.com.tw/50_00.htm");
		sns = new StockNameStore("008201",date0,dhash);
		sns.insertCF(ks);

		//Index En Name
		java.util.TreeMap<String,String> idxHash = new TreeMap<String,String>();
		idxHash.put("t00", "TAIEX");
		idxHash.put("TW50", "FTSE TWSE Taiwan 50 Index");
		idxHash.put("TWMC", "FTSE TWSE Taiwan Mid-Cap 100 Index");
		idxHash.put("TWIT", "FTSE TWSE Taiwan Technology Index");
		idxHash.put("TWEI", "FTSE TWSE Taiwan Eight Industries Index");
		idxHash.put("TWDP", "FTSE TWSE Taiwan Dividend+ Index");
		idxHash.put("EMP99", "Taiwan RAFI EMP 99 INDEX");
		idxHash.put("FRMSA", "Formosa Index");
		idxHash.put("CO101", "Taiwan CO 101 Index");
		idxHash.put("t001", "Non-Finance Sub-index");
		idxHash.put("t002", "Non-Electronics Sub-index");
		idxHash.put("t003", "Non-Finance Non-Electronics Sub-index");
		idxHash.put("t011", "Cement and Ceramic");
		idxHash.put("t031", "Plastic and Chemical");
		idxHash.put("t051", "Electrical");
		idxHash.put("t01", "Cement");
		idxHash.put("t02", "Food");
		idxHash.put("t03", "Plastic");
		idxHash.put("t04", "Textile");
		idxHash.put("t05", "Electric Machinery");
		idxHash.put("t06", "Electrical and Cable");
		idxHash.put("t07", "Chemical, Biotechnology, and Medical Care");
		idxHash.put("t21", "Chemical");
		idxHash.put("t22", "Biotechnology and Medical Care");
		idxHash.put("t08", "Glass and Ceramic");
		idxHash.put("t09", "Paper and Pulp");
		idxHash.put("t10", "Iron and Steel");
		idxHash.put("t11", "Rubber");
		idxHash.put("t12", "Automobile");
		idxHash.put("t13", "Electronics");
		idxHash.put("t24", "Semiconductor");
		idxHash.put("t25", "Computer and Peripheral Equipment");
		idxHash.put("t26", "Optoelectronic");
		idxHash.put("t27", "Communications and Internet");
		idxHash.put("t28", "Electronic Parts/Components");
		idxHash.put("t29", "Electronic Products Distribution");
		idxHash.put("t30", "Information Service");
		idxHash.put("t31", "Other Electronic");
		idxHash.put("t14", "Building Material and Construction");
		idxHash.put("t15", "Shipping and Transportation");
		idxHash.put("t16", "Tourism");
		idxHash.put("t17", "Finance and Insurance");
		idxHash.put("t18", "Trading and Consumers' Goods");
		idxHash.put("t23", "Oil, Gas and Electricity");
		idxHash.put("t20", "Other");

		idxHash.put("o00", "GTSM");
		idxHash.put("GTSM50", "GreTai 50 Index");
		idxHash.put("GAME", "Online Game Industry Index");
		idxHash.put("GTHD", "Gretai High Dividend Yield Index");
		idxHash.put("o13", "Electronic Industry Index");
		idxHash.put("o04", "Textile Industry Index");
		idxHash.put("o05", "Electric Machinery Industry Index");
		idxHash.put("o21", "Chemical Industry Index");
		idxHash.put("o22", "Biotechnology and Medical Care Industry Index");
		idxHash.put("o10", "Iron and Steel Industry Index");
		idxHash.put("o24", "Semiconductor Industry Index");
		idxHash.put("o25", "Computer and Peripheral Equipment Industry Index");
		idxHash.put("o26", "Optoelectronic Industry Index");
		idxHash.put("o27", "Optoelectronic Industry Index");
		idxHash.put("o28", "Electronic Parts and Components Industry Index");
		idxHash.put("o29", "Electronic Products Distribution Industry Index");
		idxHash.put("o30", "Information Service Industry Index");
		idxHash.put("o32", "Cultural and Creative Industry Index");
		idxHash.put("o14", "Building Material and Construction Industry Index");
		idxHash.put("o15", "Shipping and Transportation Industry Index");
		idxHash.put("o16", "Tourism Industry Index");
		idxHash.put("TWTBI", "GreTai Taiwan Treasury Benchmark Index");

		{
			int insert = 0;
			try {
				String inxBasePath0 = Utility.cleanString(inxBasePath);
		        java.io.FileInputStream fis = new java.io.FileInputStream(inxBasePath0+"etf.inx");
		        LineInputStream lis = new LineInputStream(fis);

		        while(true){
		        	byte[] a = lis.readLine();
		        	if(a==null) break;
		        	if (a.length == 0)
		        		break;
		        	String metaStr = new String(a,"UTF-8");
		        	String[] metas = metaStr.split(",",3);
		        	if(metas[0].equals("")) continue;

		        	dhash = new TreeMap<String,String>();
		        	dhash.put("cnu",metas[1]);
		        	dhash.put("enu",metas[2]);
		        	sns = new StockNameStore(metas[0],date0,dhash);
		        	boolean in = sns.insertCF(ks);
		        	if(in){
		        		insert++;
		        		//System.out.println(Utility.cleanString("Etfs " + metas[0] + "=curl:[" + metas[1] + "] eurl:[" + metas[2] + "] ==>" + in));
		        	}
		        }
		        System.out.println(Utility.cleanString("Etfs from Config File : "+inxBasePath+"etf.inx "+insert));
			}
			catch (Exception ex) {
				//insert = -1;
			}
		}

		{
			java.util.Vector<IndexMetadata> iList = new java.util.Vector<IndexMetadata>();
			try {
				String inxBasePath0 = Utility.cleanString(inxBasePath);
		        java.io.FileInputStream fis = new java.io.FileInputStream(inxBasePath0+"tse2.inx");
		        LineInputStream lis = new LineInputStream(fis);
		        while(true){
		        	byte[] a = lis.readLine();
		        	if (a.length == 0)
		        		break;
		          String metaStr = new String(a,"UTF-8");
		          String[] metas = metaStr.split(",",3);
		          iList.add(new IndexMetadata(metas[0], metas[1], metas[2]));
		        }
			}
			catch (Exception ex) {
			}
			if(iList.size()>0){

				for(int i = 0 ; i < iList.size() ; i++){
					IndexMetadata indexs43 = iList.get(i);
					idxHash.put(indexs43.symbol,indexs43.ename);
				}
				System.out.println("Indexs from Config File : "+inxBasePath+"tse2.inx "+iList.size());
			}
		}

		{
			java.util.Vector<IndexMetadata> iList = new java.util.Vector<IndexMetadata>();
			try {
				String inxBasePath0 = Utility.cleanString(inxBasePath);
				java.io.FileInputStream fis = new java.io.FileInputStream(inxBasePath0+"otc2.inx");
				LineInputStream lis = new LineInputStream(fis);
				while(true){
					byte[] a = lis.readLine();
					if (a.length == 0)
						break;
					String metaStr = new String(a,"UTF-8");
					String[] metas = metaStr.split(",",3);
					iList.add(new IndexMetadata(metas[0], metas[1], metas[2]));
				}
			}
			catch (Exception ex) {
			}
			if(iList.size()>0){

				for(int i = 0 ; i < iList.size() ; i++){
					IndexMetadata indexs43 = iList.get(i);
					idxHash.put(indexs43.symbol,indexs43.ename);
		        }
		        System.out.println("Indexs from Config File : "+inxBasePath+"otc2.inx "+iList.size());
			}
		}

		for (Map.Entry<String, String> ents : idxHash.entrySet()) {
			String key = ents.getKey();
			String value = ents.getValue();
			dhash = new TreeMap<String,String>();
			dhash.put("en",value);
			sns = new StockNameStore(key,date0,dhash);
			sns.insertCF(ks);
			//System.out.println("Insert from EngName : "+key+"="+value);
		}
		
		StockNames.exec(0,ks);
	}
	

	protected static class IndexMetadata {
		public String symbol;
		public String name;

		public String ename;

		public IndexMetadata(final String symbol, final String name, final String ename) {
			this.symbol = symbol;
			this.name = name;
			this.ename = ename;
		}
	}

	public static void insertWarrantRef(String pathOfWarrant,String encode){
		try {

			java.io.FileInputStream fis = new java.io.FileInputStream(pathOfWarrant);
			java.io.BufferedReader dis = new BufferedReader(new InputStreamReader(fis,encode));
			int count = 0;

			while (true) {
				String line = dis.readLine();
				if (line == null)
					break;
				if (count == 0 && line.length() > 8) {
					//date = line.substring(line.length() - 8, line.length());
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
		        StockNameStore sns_e = new StockNameStore(date0,names[1]);
		        List<Map.Entry<String, String>> cols = sns_e.get();
		        String ren = names[1];
		        for(Map.Entry<String, String> col : cols){
		        	if(col.getKey().equals("en")){
		        		ren = (String)col.getValue();
		        		dhash.put("ren", ren);
		        	}
		        }

		        StockNameStore sns = new StockNameStore(names[0],date0, dhash);
		        boolean rt = sns.insertCF();
		        //System.out.println(Utility.cleanString(date0 + " " + names[0] + ":" + names[1] + " "+ names[2]+" "+ren+" "+rt));
			}
		      //System.out.println(count);
		}
		catch (Exception ex) {
			System.out.println(Utility.getSQLTimeStr() +" insertWarrantRef Error : ");
			ex.printStackTrace(System.out);
		}
	}

	public static void insertWarrantFullName(String pathOfWarrantName,String encode){
		try {
			java.io.FileInputStream fis = new java.io.FileInputStream(pathOfWarrantName);
			java.io.BufferedInputStream bis = new BufferedInputStream(fis);

			while(true){
				int i = bis.read();
		        if(i == -1) break;
		        String wrnStkno = Utility.readInputStreamToString(bis,5,encode);
		        wrnStkno = (char)i+wrnStkno.trim();
		        String wrnFull =  Utility.readInputStreamToString(bis,40,encode).trim();
		        Utility.readInputStreamToBytes(bis,34);
		        TreeMap<String, String> dhash = new TreeMap<String, String> ();
		        dhash.put("cf", wrnFull);

		        StockNameStore sns = new StockNameStore(wrnStkno,date0, dhash);
		        boolean rt = sns.insertCF();
		        System.out.println(Utility.getSQLTimeStr()+" " +wrnStkno+" "+wrnFull+" "+rt);
			}
		}
		catch (Exception ex) {
			System.out.println(Utility.getSQLTimeStr() +" insertWarrantFullName("+pathOfWarrantName+") Error : ");
			ex.printStackTrace(System.out);
		}
	}
	
}
