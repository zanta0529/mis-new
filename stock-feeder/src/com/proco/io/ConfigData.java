package com.proco.io;

import java.io.RandomAccessFile;
import java.util.Hashtable;
import com.proco.util.Utility;


public class ConfigData {
	public static String hostname = "";
	public static int ohlcMode = 0;
	public static int saveTickTime = 0;
	public static String cassHost = "";
	public static String cassPass = "";
	public static int maxActive = -1;
	public static int port = -1;
	public static int connectTimeout = 60000;
	public static String f20LogSymbol = "";
	static Hashtable lengthMap = ConfigData.getFunIDList();
	public static boolean timeline = true;
	public static boolean datafeed = true;
	public static String timelineIO = "RR,FB,BS,";

	static {
		port = Utility.parseInt(getFunction("port",String.valueOf(port)));
		maxActive = Utility.parseInt(getFunction("maxActive",String.valueOf(maxActive)));
	    connectTimeout = Utility.parseInt(getFunction("connectTimeout",String.valueOf(connectTimeout)));
	    cassHost = getFunction("cassHost",cassHost);
	    cassPass = getFunction("cassPass",cassPass);
	    ohlcMode = Utility.parseInt(getFunction("ohlcMode",String.valueOf(ohlcMode)));
	    saveTickTime = Utility.parseInt(getFunction("saveTickTime",String.valueOf(saveTickTime)));
	    hostname = getFunction("hostname",hostname);
	    f20LogSymbol = getFunction("f20LogSymbol",f20LogSymbol);
	    timeline = Utility.parseInt(getFunction("timeLine",String.valueOf(0)))>0;
	    datafeed = Utility.parseInt(getFunction("dataFeed",String.valueOf(1)))>0;
	    timelineIO = getFunction("timelineIO",timelineIO);
	    
	    if(!timeline && !datafeed) datafeed = true;
	}

	public static String getFunction(String key , String value){
		String valueNew = (String) lengthMap.get(key);
		if(valueNew != null){
			value = valueNew;
		}
		if (value.indexOf(".YYYYMMDD") != -1) {
			value = value.replaceAll(".YYYYMMDD", "." + Utility.getDateStr());
		}

		return value;
	}


	public static Hashtable getFunIDList() {
		Hashtable lengthMap = new Hashtable();
		try {
			String conf_path = InfoDaemon.confFile;

			java.io.RandomAccessFile bis = new RandomAccessFile(conf_path, "r");
			lengthMap = new Hashtable();
			String tmpData = "";
			String key = "";
			String data = "";

			byte[] confData = new byte[(int)bis.length()];
			bis.read(confData);
			bis.close();
			String[] commands = new String(confData,"Big5").split("\n");

			for (int i = 0 ; i < commands.length ; i++) {
				tmpData = Utility.filiterString(commands[i],"\r");
				if(tmpData.indexOf("=") != -1) {
					key = (tmpData.substring(0, tmpData.indexOf("="))).trim();
					data = (tmpData.substring(tmpData.indexOf("=") + 1,
	                      tmpData.length())).trim();

					System.out.println(key + "=" + data);
					lengthMap.put(key, data);
				}
			}
			bis.close();
	    }
	    catch (Exception ex) {
	    	System.out.println("ListEX:" + ex.toString());
	    }

	    return lengthMap;
	}
}
