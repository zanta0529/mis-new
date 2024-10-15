package com.github.tonivade.resp.CachingSystem;

import com.github.benmanes.caffeine.cache.*;
import com.github.tonivade.resp.RespServerContext;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redisapi.RedisInNetty;
import redisapi.Util.CollectionUtil;
import rocksdbapi.Rockdis.RockdisProtocol;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

//import static Rockdis.RockdisProtocol.combineDBKeyWithKeyField;
//import static rocksdbapi.Rockdis.RockdisProtocol.combineDBKeyWithKeyField;
import static rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation.byteArrayToInt;

public class LargeCachePoolSystem {

    //
    private static LargeCachePoolSystem instance = null;
    public Cache<String, Object> cache;

//    public Cache<String, Object> cacheForFlush;

    public ConcurrentSkipListMap<String, Object> cacheForFlushMap;

    private static RespServerContext respServerContext;
    private final Logger LOGGER = LoggerFactory.getLogger(LargeCachePoolSystem.class);

    /**
     * 當 LCPS Cache 準備被清除的時候，會將此變數設為 true，並且觸發 invalidateAll。
     * 當完成
     */
    public AtomicBoolean manualCleanAll = new AtomicBoolean(false);

    private AtomicInteger flushFromLCPSSize = new AtomicInteger(0);

    /**
     * 當 Cache 從 Waiting Cache 轉移到 LCPS時，會將此變數設為 true，此時 redis-interface 收指令的時候
     * 會等待，等待所有資料從 Waiting Cache 移轉到 LCPS後，這個數值才會設定為 false。
     * redis-interface 的 set 指令就會往LCPS寫入資料。
     */
    public AtomicBoolean transitCache = new AtomicBoolean(false);
    public int clearCount = 0;

    public AtomicBoolean finishedtraverseLCPS = new AtomicBoolean(false);

    private LargeCachePoolSystem(RespServerContext respServerContext){

        LOGGER.debug("Enable Caffeine Cache.");
        this.respServerContext = respServerContext;
        System.out.println(" Cache Evict 時間 設定為 : " + respServerContext.getCachEvictTimePeriod());
        
        final ConcurrentSkipListMap<String, Long> cacheForTimeMap = new ConcurrentSkipListMap<String, Long>();
        
        Cache<String, Object> cache0 = Caffeine.newBuilder()
                // 當 key 被 remove 時，會觸發這個 listener
                .removalListener((String key, Object value, RemovalCause cause) ->
                {
                    LOGGER.debug("啟動 Key Remove! Key : {}, Value : {}, Cause : {}", key, value, cause);

                    // 寫到 RocksDB內
                    String rocksCommand = (String)((Map) value).get("rockscommand");

                    // 當狀態不是 replaced 且 rocksCoomand 有值則進行 RocksDB寫入
                    if ( (!cause.equals(RemovalCause.REPLACED) && !cause.equals(RemovalCause.EXPLICIT)) && rocksCommand != null && !manualCleanAll.get()){
//                        System.out.println("啟動 Key Remove! Key : " + key + ", Value : " + value + ", Cause : " + cause + ", RocksCommand : " + rocksCommand);
                        if (respServerContext.getSafemode()) {
                            System.out.println("找出已經啟動 Safe Mode 但是還是發生 expired 寫入的 Key值");
                            System.out.println("確認 Key與Value的值 : " + key + ", Value : " + value + ", Cause : " + cause + ", RocksCommand : " + rocksCommand);
                        }
                        cacheForTimeMap.remove(key);
                        insertIntoDB(key, value, rocksCommand);
                    }

                    if (cause.equals(RemovalCause.EXPLICIT) && !manualCleanAll.get() ) {
                        // 一般情況下刪除 key值
//                        System.out.println("沒有進行刪除，理論上這裡不應該顯示，有問題的 Key :" + key);
//                        System.out.println("刪除的 Command : " + rocksCommand);
//
                        cacheForTimeMap.remove(key);
                    	removeKeysFromDB(key);
                    }


                    if (manualCleanAll.get()) {
                        // 當執行 flushall 時，會將 manualCleanAll 設為 true
                        // 這裡的行為透過cleanCache的寫入達成
//                        insertIntoDB(key, value, rocksCommand);
                        int executedClearanceCount = flushFromLCPSSize.incrementAndGet();
//                        System.out.println("Cache 裡面的 Count 總共有 : " + clearCount +
//                                " ,已經完成的Flush From LCPS Size Count : " + executedClearanceCount +
//                                " ,Finshed Traverse LCPS 的狀態 : " + finishedtraverseLCPS.get());
                    }

//                    if ( ((cause.equals(RemovalCause.EXPLICIT) || cause.equals(RemovalCause.REPLACED))) && manualCleanAll.get()) {
//
//                    }

                })
                // Caffeien中不會對evcit的元素立刻清除，但弱吞吐量太小，就必須請System幫忙處理
                .scheduler(Scheduler.forScheduledExecutorService(Executors.newScheduledThreadPool(1)))
                .expireAfterWrite(respServerContext.getCachEvictTimePeriod()/1000, TimeUnit.SECONDS)
                .maximumSize(Long.MAX_VALUE)
                .build();
        
        if(RedisInNetty.flush) {
            final LargeCachePoolSystem lp = this;
            cache = new CacheWriter<String,Object>(cache0,cacheForTimeMap,new CacheWriter.WriteListener() {
    			@Override
    			public void onWrite(Object key, Object value) {
    				// TODO Auto-generated method stub
    				if(key instanceof String && value instanceof Map) {
    					String rocksCommand = (String)((Map) value).get("rockscommand");
    					if(rocksCommand!=null) {
    						if(!rocksCommand.equals("zadd")) insertIntoDB((String)key, value, rocksCommand); //排除 zadd	
    					}				
    				}
    			}
            }, respServerContext.getCachEvictTimePeriod()*5 );        	
        } else cache = cache0;

        cacheForFlushMap = new ConcurrentSkipListMap<>();

    }
    synchronized static public LargeCachePoolSystem getInstance(RespServerContext respServerContext) {
        if (instance == null) {
            instance = new LargeCachePoolSystem(respServerContext);
        }
        return instance;
    }

    // 判斷 key 是否存在於 cache中
    public boolean isKeyExistInLCPS(String key) {
        return cache.getIfPresent(key) != null;
    }

    public void putToWaitingCache(String key, Object fieldValueMap) {
        // 架構幾乎跟 putToLCPS 一樣，但是沒有讀取 RocksDB的操作
        if ( ((Map)fieldValueMap).get("setCommandNoFiled") != null) {
            LOGGER.debug("This is set command.");
            // 把資料寫入 Cache
            cacheForFlushMap.put(key, fieldValueMap);
        } else {

            Object LCPSResult = cacheForFlushMap.get(key);
            if (key.equals("stockquote_20231123_otc_62574.tw_20231123")) {
                System.out.println("[LargeCachePoolSystem]Key " + key + " in cache , Value : " + LCPSResult);
                System.out.println("===");
                System.out.println();
            }
            NavigableMap<String, String> newFieldValueMap = new ConcurrentSkipListMap<>();
            if (LCPSResult != null) {
                newFieldValueMap.putAll((Map<String, String>) LCPSResult);
            }
            newFieldValueMap.putAll((Map<String, String>) fieldValueMap);
            cacheForFlushMap.put(key, newFieldValueMap);
        }

        // 把 cacheForFlushMap 走訪印出
//        cacheForFlushMap.forEach((k, v) -> {
//            System.out.println("[cacheForFlushMap]Key : " + k );
//            System.out.println("[cacheForFlushMap]Value : " + v );
//        });
    }

    public Map<String, String> putToLCPS(String key, Object fieldValueMap) {

        // 目前 filedValueMap 只會有想種類型一種是 String, 另外一種是 Map<>

        // 判斷是否為 set 指令
        if ( ((Map)fieldValueMap).get("setCommandNoFiled") != null) {
            LOGGER.debug("This is set command.");
            // 把資料寫入 Cache
            cache.put(key, fieldValueMap);
            return null;
        }

        Map<String, String> dataFromDB = null;
        Object LCPSResult = cache.getIfPresent(key);
        if(LCPSResult==null) {   //if (! isKeyExistInLCPS(key)) {
            LOGGER.debug("LCPS doesn't have this key : {}", key);
            // Cache 沒有 Key 的原因有兩種，一種是從來沒寫入過，另一種是Cache 被 evict 掉了
            // Get data from RocksDB using this key
            try {
                dataFromDB = getDataFromDB(key.toString(), fieldValueMap);
                if (dataFromDB != null) {
                    LOGGER.debug("Get data from RocksDB, Key : {}, Value : {}", key, dataFromDB);
                    if (key.equals("stockquote_20231123_otc_62574.tw_20231123")) {
                        System.out.println("[LargeCachePoolSystem]Get data from RocksDB, Key : " + key + ", Value : " + dataFromDB);
                        System.out.println("===");
                        System.out.println();
                    }
                    dataFromDB.putAll((Map<String,String>)fieldValueMap);
                    if(!dataFromDB.isEmpty()) cache.put(key, dataFromDB);
//                    System.out.println("Cache info: " + cache.getIfPresent(key));
                } else {
                    if (key.equals("stockquote_20231123_otc_62574.tw_20231123")) {
                        System.out.println("[LargeCachePoolSystem]Even in RocksDB has no data of this key : " + key);
                        System.out.println("===");
                        System.out.println();
                    }
                }
            } catch (RocksDBException e) {
                e.printStackTrace();
            }
        } else {
            // 有這個Key代表目前此key有其他的field value pair，因此傳入的 filedValueMap 要跟原本的合併
            LOGGER.debug("LCPS has this key : {}", key);
            //Object LCPSResult = cache.getIfPresent(key);
            if (key.equals("stockquote_20231123_otc_62574.tw_20231123")) {
                System.out.println("[LargeCachePoolSystem]Key " + key + " in cache , Value : " + LCPSResult);
                System.out.println("===");
                System.out.println();
            }
            /*
            NavigableMap<String, String> newFieldValueMap = new ConcurrentSkipListMap<>();
            newFieldValueMap.putAll((Map<String, String>) LCPSResult);
            newFieldValueMap.putAll((Map<String, String>) fieldValueMap);
            */
            NavigableMap<String, String> newFieldValueMap = (NavigableMap<String, String>)LCPSResult;
            newFieldValueMap.putAll((Map<String, String>) fieldValueMap);
            cache.put(key, newFieldValueMap);
            ((Map<String, String>) fieldValueMap).clear();
//            System.out.println("Cache info: " + cache.getIfPresent(key));
        }
        return dataFromDB;
    }


    public Object getFromLCPS(String key) {
        // 因為 cache 從 Caffeien換成 concurrent hash map 所以 get 要換
//        LOGGER.debug("Get Data from Large Cache Pool System, Key : {}, Value : {}", key, cache.getIfPresent(key));
        return cache.getIfPresent(key);
    }


    public Object getAllFromLCPS(String key) {
        LOGGER.debug("Invoke getAll from LCPS.");
        LOGGER.debug("Get Data from Large Cache Pool System, Key : {}", key);
        return cache.getIfPresent(key);
    }


    public int delFromWaitingCache(String key, String[] fieldArray, RockdisProtocol.Command rocksCommand) {
        LOGGER.debug("Invoke del from Waiting Cache.");
        LOGGER.debug("Delete data from Waiting Cache, Key : {}", key);
//        int deleteCount = 0;

        Object LCPSResult = cacheForFlushMap.get(key);
        switch (rocksCommand) {
            case DELETE:
                // 刪除 key
                if (cacheForFlushMap.get(key) != null) {
                    // 在 Cache 內有這個 Key，把它刪除
                    cacheForFlushMap.remove(key);
                    LOGGER.debug("This key: {} has been deleted.", key);
                    return 1;
                }
//                else {
                    // 在 Cache 內沒有這個 Key，但DB後面還有這個 Key
//                    LOGGER.debug("This key: {} doesn't exist in Waiting Cache.", key);
//                    LOGGER.debug("嘗試刪除 RocksDB 內的 key.");
//                    return removeKeysFromDB(key, rocksCommand);
//                }
            case HDEL:
                // 刪除 Waiting Cache 內 Key 的 field資訊
                if (LCPSResult != null) {
                    for (String field : fieldArray) {
                        ((Map<String, String>) LCPSResult).remove(field);
                    }
                }
                return 0;
            case ZREM:
                // 刪除 Waiting Cache 內 Key 的 field資訊
                if (LCPSResult != null) {
                    for (String member : fieldArray) {
                        ((Map<String, String>) LCPSResult).remove(member);
                    }
                }
                return 0;

            default:
                LOGGER.debug("沒有任何作用的 Command : {}", rocksCommand);
                return 0;
        }
    }


    public int delFromLCPS(String key, String[] fieldArray, RockdisProtocol.Command rocksCommand) {
        LOGGER.debug("Invoke del from LCPS.");
        LOGGER.debug("Delete data from Large Cache Pool System, Key : {}", key);
        LOGGER.debug("Delete data from Large Cache Pool System, RocksCommand : {}", rocksCommand);
//        int deleteCount = 0;

        Object LCPSResult = cache.getIfPresent(key);

        switch (rocksCommand) {
            case DELETE:
                // 刪除 key
                if (cache.getIfPresent(key) != null) {
                    cache.invalidate(key);
                    LOGGER.debug("This key: {} has been deleted.", key);
                    return 1;
                } else {
                    LOGGER.debug("This key: {} doesn't exist in LCPS.", key);
                    LOGGER.debug("嘗試刪除 RocksDB 內的 key.");
                    return removeKeysFromDB(key);
                }
            case HDEL:
                // 刪除 LCPS Cache 內 Key 的 field

                if (LCPSResult != null) {
                    for (String s : fieldArray) {
                        ((Map<String, String>) LCPSResult).remove(s);
                    }
                }
                // 刪除 RocksDB 儲存的 Key 的 field
                try {
                    respServerContext.getRockdisInstance().hdel(key, fieldArray);
                } catch (RocksDBException e) {
                    LOGGER.error("RocksDBException occurred when hdel, Key : {}, Field : {}", key, fieldArray);
                    throw new RuntimeException(e);
                }

                return 0;

            case ZREM:
                // 刪除 LCPS Cache 內 Key 的 field

                if (LCPSResult != null) {
                    for (String s : fieldArray) {
                        ((Map<String, String>) LCPSResult).remove(s);
                    }
                }
                // 刪除 RocksDB 儲存的 Key 的 field
                try {
                    respServerContext.getRockdisInstance().zrem(key, fieldArray);
                } catch (RocksDBException e) {
                    LOGGER.error("RocksDBException occurred when zrem, Key : {}, Field : {}", key, fieldArray);
                    throw new RuntimeException(e);
                }

                return 0;


            default:
                LOGGER.debug("沒有任何作用的 Command : {}", rocksCommand);
                return 0;

        }
    }

    private Map<String, String> getDataFromDB(String key, Object fieldValueMap) throws RocksDBException {
        // todo 這裡要改成從DB拿資料

        String executedCommand = (String) ((Map)fieldValueMap).get("rockscommand");
        Map<String, String> dataFromDB = null;

        // 這段程式應該不會用到，先暫時註解
//        if (executedCommand == null) {
//            return dataFromDB;
//        }

        if (executedCommand != null &&
                (executedCommand.equals("zadd") ||
                executedCommand.equals("zrange") ||
                executedCommand.equals("zrevrange") ||
                executedCommand.equals("zrangebyscore")
                )
        ) {
            dataFromDB = convertListToMap(
                            respServerContext.getSecondRockdisInstance().zrangeWithScores(key.getBytes(), 0, -1)
                        );
        } else {
            dataFromDB = respServerContext.getSecondRockdisInstance().hgetAll(key);
        }


        return dataFromDB;
    }

    private Map<String, String> convertListToMap(List<byte[]> result) {
        Map<String, String> resultMap = new ConcurrentSkipListMap<>();
        for (int i = 0; i < result.size(); i += 2) {
            resultMap.put(
                    new String(result.get(i)),
                    Integer.toString(byteArrayToInt(result.get(i+1))));
        }
        return resultMap;
    }

    private void insertIntoDB(String key, Object value, String rocksCommand) {
        // todo 這裡要改成從DB拿資料
        LOGGER.debug("啟動 DB 寫入程序.");
        switch (rocksCommand) {
            case "set":
                try {
                    // foreach value in valueMap
                    NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                    // 刪除 rockscommand
                    valueMap.remove("rockscommand");
                    // 將資料寫入 RocksDB
                    respServerContext.getRockdisInstance().set(key, valueMap.get("setCommandNoFiled"));
                } catch (RocksDBException e) {
                    LOGGER.error("RocksDBException occurred when set, Key : {}, Value : {}", key, value);
                    throw new RuntimeException(e);
                }
                break;
            case "hset":
                try {
                    // foreach value in valueMap
                    NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                    // 刪除 rockscommand
                    valueMap.remove("rockscommand");
                    // 將資料寫入 RocksDB
                    while (!valueMap.isEmpty()) {
                        Map.Entry entry = valueMap.pollFirstEntry();
                        if (entry == null) {
                            continue;
                        }
                        respServerContext.getRockdisInstance().hset(key, (String) entry.getKey(), (String) entry.getValue());
                    }
                } catch (RocksDBException e) {
                    LOGGER.error("RocksDBException occurred when hset, Key : {}, Value : {}", key, value);
                    throw new RuntimeException(e);
                }
                break;
            case "hmset":
                try {
                    // foreach value in valueMap
                    NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                    // 刪除 rockscommand
                    valueMap.remove("rockscommand");
                    // 將資料寫入 RocksDB
//                    System.out.println("Execute HMSET!");
                    respServerContext.getRockdisInstance().hmset(key, valueMap);
//                    while (!valueMap.isEmpty()) {
//                        Map.Entry entry = valueMap.pollFirstEntry();
//                        if (entry == null) {
//                            continue;
//                        }
//
//                    }
                } catch (RocksDBException e) {
                    LOGGER.error("RocksDBException occurred when hmset, Key : {}, Value : {}", key, value);
                    throw new RuntimeException(e);
                }
                break;

            case "zadd":

                NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                valueMap.remove("rockscommand");

                try {
                    respServerContext.getRockdisInstance().zadd(CollectionUtil.convertMapToList(key, valueMap));
                } catch (RocksDBException e) {
                    LOGGER.error("RocksDBException occurred when zadd, Key : {}, Value : {}", key, value);
                    throw new RuntimeException(e);
                }
                break;
            default:
                LOGGER.debug("Command 不起任何寫入也沒有任何作用 : {}", rocksCommand);
                break;
        }
    }

    public void cleanCache() {

        LOGGER.debug("啟動切換DB，進行 Cache 清除程序.");
        System.out.println("啟動切換DB，進行 Cache 清除程序.");
        manualCleanAll.set(true);
        flushFromLCPSSize.set(0);
        clearCount = 0;

        Map<String, Object> cacheMap = cache.asMap();

        finishedtraverseLCPS.set(false);
        // 走訪 LCPS Cache中的键值对，把元素寫入 RocksDB
        for (Map.Entry<String, Object> entry : cacheMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
//            System.out.println("Key: " + key);

            // 確認 command類型
            String rocksCommand = (String)((Map) value).get("rockscommand");
            if (rocksCommand != null && !respServerContext.getRockdisInstance().isClosed()) {
                insertIntoDB(key, value, rocksCommand);
            }
            cache.invalidate(entry.getKey());
            clearCount++;
        }
        finishedtraverseLCPS.set(true);

//        System.out.println("LCPS Cache 清除完成........");
//        System.out.println("LCPS Cache 數量 : " + clearCount);

        // 這裡會有問題，因為 invalidateAll 啟動的 removalListener EXPLICIT 是非同步的
        // 如果立刻把 manualCleanAll 設為 false，會有資料 invalidate啟動的時候，manualCleanAll 已經被設為 false
        // 導致他去執行 removeKeysFromDB 造成 kernel panic
//        cache.invalidateAll();
        System.out.println("Clear Count : " + clearCount);
        System.out.println("手動清除 Cache 完畢，設定 manualCleanAll 為 false.");

        if ( clearCount == 0 ) {
            // 若 LCPS內沒有任何元素，也要設定為 false，否則沒有辦法將 manualCleanAll 設為 false
            System.out.println("將 manualCleanAll 設為 false.");
            manualCleanAll.set(false);
            flushFromLCPSSize.set(0);
            clearCount = 0;

        } else {
            //啟動一新 Thread不斷去判斷是否完成
            new Thread(
                    () -> {
                        while (true) {

                            System.out.println("Cache 裡面的 Count 總共有 : " + clearCount +
                                    " ,已經完成的Flush From LCPS Size Count : " + flushFromLCPSSize.get() +
                                    " ,Finshed Traverse LCPS 的狀態 : " + finishedtraverseLCPS.get());

                            // 判斷 flushFromLCPSSize 是否大於等於 LCPS Cache 數量( clearCount )
                            if ( (flushFromLCPSSize.get() == clearCount && finishedtraverseLCPS.get()) ||
                                    (flushFromLCPSSize.get() > clearCount && finishedtraverseLCPS.get()) ) {
                                System.out.println("目前的 flushFromLCPSSize : " + flushFromLCPSSize.get());
                                System.out.println("LCPS Cache 數量 : " + clearCount);
                                System.out.println("將 manualCleanAll 設為 false.");
                                manualCleanAll.set(false);
                                flushFromLCPSSize.set(0);
                                clearCount = 0;
                                break;
                            }
                        }
                    }
            ).start();
        }
        while (manualCleanAll.get()) {
//            System.out.println(" manualCleanAll 尚未完成");
            continue;
        }

    }

    public void switchCacheFromWaitingToLCPS() {
        LOGGER.debug("把 Waiting Cache 資料轉移到 LCPS.");
        transitCache.set(true);
        while ( !cacheForFlushMap.isEmpty() ) {
            Map.Entry entry = cacheForFlushMap.pollFirstEntry();
            if (entry == null) {
                continue;
            }
            putToLCPS((String) entry.getKey(), entry.getValue());
        }
    }

    private int removeKeysFromDB(String key){
        LOGGER.debug("啟動 DB的Key值 刪除程序.");
        try {
            return respServerContext.getRockdisInstance().delete(key);
        } catch (RocksDBException e) {
            LOGGER.error("RocksDBException occurred when del, Key : {}", key);
            throw new RuntimeException(e);
        }
    }

    private int removeKeysFromDB(String key, RockdisProtocol.Command rocksCommand){

        switch (rocksCommand) {
            case DELETE:
                LOGGER.debug("啟動 DB的Key值 刪除程序.");
                try {
                    return respServerContext.getRockdisInstance().delete(key);
                } catch (RocksDBException e) {
                    LOGGER.error("RocksDBException occurred when del, Key : {}", key);
                    throw new RuntimeException(e);
                }
            default:
                LOGGER.debug("沒有任何作用的 Command : {}", rocksCommand);
                return 0;
        }
    }
}
