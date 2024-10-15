package redisapi;


import redisapi.Util.ReadProperties;

import com.github.tonivade.resp.RespServer;
import com.github.tonivade.resp.Util.ChannelStrategy.Strategy;

import org.rocksdb.RocksDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redisapi.RockdisMappingCommand.*;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class RedisInNetty {

    private static Logger logger = LoggerFactory.getLogger(RedisInNetty.class);
    public static boolean debug = false;
    public static boolean flush = false;
    
    public static void main(String[] args) {
    	for(String arg : args) {
    		if(arg.toLowerCase().equals("debug")) debug = true;
    		if(arg.toLowerCase().equals("flush")) flush = true;
    	}

//        CommandSuite commandSuite = new CommandSuite();
//        commandSuite.addCommand(RockdisMappingCommand.SetCommand.class, "set");

        CustomCommand customCommand = new CustomCommand();
        customCommand.addCommand(ConfigCommand.class);
        customCommand.addCommand(AuthCommand.class);
        customCommand.addCommand(SetCommand.class);
        customCommand.addCommand(GETCommand.class);
        customCommand.addCommand(HSetCommand.class);
        customCommand.addCommand(HGetCommand.class);
        customCommand.addCommand(HMSetCommand.class);
        customCommand.addCommand(HMGetCommand.class);
        customCommand.addCommand(HGetAllCommand.class);
        customCommand.addCommand(hincrByCommand.class);
        customCommand.addCommand(ZAddCommand.class);
//        customCommand.addCommand(ZCardCommand.class);
        customCommand.addCommand(ZRangeCommand.class);
        customCommand.addCommand(ZRevrangeCommand.class);
        customCommand.addCommand(ZRemCommand.class);
//        customCommand.addCommand(KeyCommand.class);

        customCommand.addCommand(DelCommand.class);
        customCommand.addCommand(HDelCommand.class);
        customCommand.addCommand(PingCommand.class);
        customCommand.addCommand(FlushAllCommand.class);


        customCommand.addCommand(ZRangeByScoresCommand.class);
//        customCommand.addCommand(ZRevrangeByScoreCommand.class);
//        customCommand.addCommand(ZRemrangeByScoreCommand.class);
//        customCommand.addCommand(ZRemrangeByRankCommand.class);



        ReadProperties.readFile();
        System.out.println("=====================================================================");
        System.out.println("=");
        System.out.println("=            SHOW CONFIGURATION");
        System.out.println("=            Start Time:"+getFullyDateTimeStr());
        System.out.println("=");
        System.out.println("=====================================================================");
        System.out.println("Listen ip Address is : " + cleanString(ReadProperties.getRockdisIpAddress()));
        System.out.println("Listen port is : " + ReadProperties.getRockdisPort());
        System.out.println("Persistence Mode is : "+ cleanString(ReadProperties.getPersistenceMode()));
        System.out.println("Flush Mode is : "+ RedisInNetty.flush);
        System.out.println("Directory of primary rockdis : "+ cleanString(ReadProperties.getPrimaryDir()));
        System.out.println("Directory of secondary rockdis : "+ cleanString(ReadProperties.getSecondaryDir()));
        System.out.println("Worker : "+ ReadProperties.getRockdisWorker());
        System.out.println("");
//        System.out.println("Enable of CROSS DAY DEBUG ? : "+ ReadProperties.isEnableCrossDayDebugMode());
//        System.out.println("Enable of CROSS DAY MECHANISM ? : "+ ReadProperties.isEnableCrossDayMechanism());
//        if (ReadProperties.isEnableCrossDayDebugMode() && ReadProperties.isEnableCrossDayMechanism()) {
//            System.out.println("  You can not set CROSS_DAY_DEBUG and CROSS_DAY_MECHANISM true at the same time.");
//            System.out.println("  You set CROSS_DAY_DEBUG is true, so system set CROSS_DAY_MECHANISM is false by default.");
//        }
        System.out.println("Enable System.out connected/disconnected/receive ? : "+ debug);
        System.out.println("Enable auth? : "+ ReadProperties.isEnableAuth());
        if (ReadProperties.isEnableAuth()) {
            System.out.println("    authorization string is : "+ cleanString(ReadProperties.getAuthString()));
        }
        System.out.println("Enable Cache System? : "+ ReadProperties.enableCacheSystem());

        if (ReadProperties.enableCacheSystem()) {
            System.out.println("    evict time period is : "+ ReadProperties.getCacheEvictTimePeriod());
        }

//        System.out.println("Cache system period : "+ ReadProperties.getCachePeriod());
        RocksDB.Version version = RocksDB.rocksdbVersion();
        if(version!=null)
        System.out.println("RocksDB Version : "+RocksDB.rocksdbVersion().toString());
        System.out.println("HPRdb Version : 20240619_C6");
        System.out.println("近期更新log: ");
        System.out.println("            [20240619] 調整 LargeCachePoolSystem.insertIntoDB 寫入時檢查 null");
        System.out.println("            [20240611] 新增 rocksdb put event 寫入 WAL 機制，支持 rsync 備份， command line 需配置 flush");
        System.out.println("            [20240428] 調整 rocksdb_iterator 使用 try-with-resource 機制");
        System.out.println("            [20240427] fixed HGETALL 資料庫有數據但回傳空數據的可能性");
        System.out.println("            [20240422] fixed ZRANGEBYSCORE 有 NullPointException 的可能性");
        System.out.println("            [20240422] 新增 Config 指令預設輸出");
        System.out.println("            [20240420] fixed HMSET/ZADD/AUTH 有 NullPointException 的可能性");
        System.out.println("            [20240409] fixed checkmarx 高風險, 中風險");
        System.out.println("            [20240407] WriteOptions/WriteBatch 同步調整");
        System.out.println("            [20240406] 換日檔名讀取機制調整，略過結尾非數字的檔名");
        System.out.println("            [20240316] fixed checkmarx 高風險, 中風險");
        System.out.println("            [20240314] remove InteractMainClass");
        System.out.println("            [20240314] remove IntegrationTest");
        System.out.println("            [20240304] rockdis.properties 新增 worker 設置指令處理執行緒");
        System.out.println("            [20240226] 修正 PING 沒檢查 auth");
        System.out.println("            [20240214] RedisInNetty Command line 加上 debug 才允許 System.out 輸出");
        System.out.println("            [20240206] System.out 輸出 connected/disconnected/receive");
        System.out.println("            [20240203] 修正 FLUSHALL 沒檢查 auth");
        System.out.println("            [20240202] 升級 netty lib 至 4.1.106, 支援 jdk11 ");
        System.out.println("            [20240127] 升級 jedis lib 至 5.0.1, 支援 jdk11 ");
        System.out.println("            [20240111] 嘗試處理 to many opefile 問題");
        System.out.println("            [20240111] 解決當日資料庫檔案超過十筆無法遞增問題");
        System.out.println("            [20240109] zrangebyscore 增加 limit 支援");
        System.out.println("            [20231224] 提供 詳細的 error log 輸出");
        System.out.println("            [20231223] 提供 zrangebyscore 指令");
        System.out.println("            [20231223] 不要拋出RocksDBException 改為LOGGER.error輸出");
        System.out.println("            [20231221] 修改 rocksdb版本由 7 改為 6.13.3");

        System.out.println("提供的指令有：");

        List<String> commandString = new ArrayList<>(customCommand.getAllCommands().keySet());
        Collections.sort(commandString);
        for (String command : commandString) {
            System.out.println("            " + cleanString(command));
        }
        System.out.println("");
        System.out.println("=====================================================================");
        System.out.println("=            END OF CONFIGURATION");
        System.out.println("=====================================================================");
        RespServer server = RespServer.builder()
                .host(ReadProperties.getRockdisIpAddress()).port(ReadProperties.getRockdisPort())
                .persistenceDir(ReadProperties.getPrimaryDir(), ReadProperties.getSecondaryDir())
                .enableCrossDayDebug(ReadProperties.isEnableCrossDayDebugMode())
                .enableCrossDayMechanism(ReadProperties.isEnableCrossDayMechanism())
                .isAuthEnabled(ReadProperties.isEnableAuth()).getAuthString(ReadProperties.getAuthString())
                .enableCacheSystem(ReadProperties.enableCacheSystem())
                .setCacheEvictTimePeriod(ReadProperties.getCacheEvictTimePeriod())
                .commands(customCommand).build();

        // Use redisapi.ShutdownHook to listen unusal event
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(server));
        // Start Server
        server.start();
    }
    public static String getFullyDateTimeStr() {
        java.sql.Timestamp now = new java.sql.Timestamp(System.
                currentTimeMillis());
        String nowStr = now.toString();
        while (nowStr.length() < 23)
            nowStr = nowStr + "0";
        //System.out.println(nowStr);

        String timeStr = nowStr.substring(0, 4);
        timeStr += nowStr.substring(5, 7);
        timeStr += nowStr.substring(8, 10);
        timeStr += nowStr.substring(11, 13);
        timeStr += nowStr.substring(14, 16);
        timeStr += nowStr.substring(17, 19);
        //timeStr += nowStr.substring(20, 23);
        return timeStr;
    }    

	static String writeMark = "";
	public static void writeStringArray(String index , String[] strings) {
		if(strings==null || index==null) return;
		if(writeMark.equals("")) writeMark= String.valueOf(System.nanoTime());
		String inxBasePath = index+"_"+writeMark+".tmp";
		
		String inxBasePath0 = RedisInNetty.cleanString(inxBasePath);
		try {
			FileOutputStream baos = new FileOutputStream(inxBasePath0);
			for(String line : strings) {
				baos.write((line+"\n").getBytes());
			}
			baos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static String cleanString(String aString) {
		if (aString == null) return null;
		String cleanString = "";
		cleanString = aString.replaceAll("[^\\w\\s]", "");
		return cleanString;
	}
}
