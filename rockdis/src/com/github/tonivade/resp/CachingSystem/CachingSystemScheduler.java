package com.github.tonivade.resp.CachingSystem;

//import Rockdis.Rockdis;
import rocksdbapi.Rockdis.Rockdis;
import com.github.tonivade.resp.RespServerContext;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

//import static Rockdis.RockdisProtocol.combineDBKeyWithKeyField;
import static rocksdbapi.Rockdis.RockdisProtocol.combineDBKeyWithKeyField;

/**
 * @author kkw
 *
 */

public class CachingSystemScheduler {

    private long schedulerPeriod;
    private static RespServerContext respServerContext;

    // Singleton Pattern
    private static CachingSystemScheduler instance = null;
    private CachingSystemScheduler(RespServerContext respServerContext, long schedulerPeriod){
        this.respServerContext = respServerContext;
        this.schedulerPeriod = schedulerPeriod;
    }
    synchronized static public CachingSystemScheduler getInstance(RespServerContext respServerContext,
                                                                  long schedulerPeriod) {
        if (instance == null) {
            instance = new CachingSystemScheduler(respServerContext, schedulerPeriod);
        }
        return instance;
    }


//    public void triggerScheduler(){
//        SetScheduler();
//        HSetScheduler();
//        HMsetScheduler();
//        DelScheduler();
//    }

    void SetScheduler(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new RockdisSet(), 1000, schedulerPeriod);
    }

    void HSetScheduler(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new RockdisHSet(), 1000, schedulerPeriod);
    }

    void HMsetScheduler(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new RockdisHMSet(), 1000, schedulerPeriod);
    }

    void DelScheduler(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new RockdisDel(), 1000, schedulerPeriod);
    }

    public static class RockdisSet extends TimerTask {
        private final Logger LOGGER = LoggerFactory.getLogger(RockdisSet.class);
        @Override
        public void run() {
            // todo 暫時註解，避免 log過多
//            LOGGER.debug("Cache System is enabled. Set persistence scheduler start to work. ");
            long startTime = System.currentTimeMillis();
            NavigableMap<String, String> setHashMap = CachingSystem.getInstance().getSetHashMap();
            while (!setHashMap.isEmpty()) {
                Map.Entry entry = setHashMap.pollFirstEntry();
                if (entry == null) {
                    continue;
                }
                try {
                    String insertKey = entry.getKey().toString();
                    String insertValue = entry.getValue().toString();
                    Rockdis rockdis = respServerContext.getRockdisInstance();
                    LOGGER.debug("[Caching System] Start to set into primary db, Primary @[Address] : " + rockdis.toString());
                    rockdis.set(insertKey, insertValue);
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
            // todo 暫時註解，避免 log過多
//            LOGGER.debug("Execution time of Set persistence is : {} ms", endTime);
        }
    }

    public static class RockdisHSet extends TimerTask {
        private final Logger LOGGER = LoggerFactory.getLogger(RockdisHSet.class);
        @Override
        public void run() {
//            LOGGER.debug("Cache System is enabled. HSet persistence scheduler start to work. ");
            long startTime = System.currentTimeMillis();
            NavigableMap<String, String> hSetHashMap = CachingSystem.getInstance().gethSetHashMap();
            while (!hSetHashMap.isEmpty()) {
                Map.Entry entry = hSetHashMap.pollFirstEntry();
                System.out.println("Entry is : " + entry);
                if (entry == null) {
                    continue;
                }
                try {
                    String insertKey = entry.getKey().toString();
                    String insertValue = entry.getValue().toString();
                    Rockdis rockdis = respServerContext.getRockdisInstance();
                    LOGGER.debug("[Caching System] Start to HSet into primary db, Primary @[Address] : " + rockdis.toString());
                    rockdis.set(insertKey, insertValue);
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
//            LOGGER.debug("Execution time of HSet persistence is : {} ms", endTime);
        }
    }

    public static class RockdisHMSet extends TimerTask {
        private final Logger LOGGER = LoggerFactory.getLogger(RockdisHMSet.class);
        @Override
        public void run() {
//            LOGGER.debug("Cache System is enabled. HMset persistence scheduler start to work. ");
//            System.out.println("Cache System is enabled. HMset persistence scheduler start to work. ");
            long startTime = System.currentTimeMillis();
            NavigableMap<String,String> hMSetHashMap = CachingSystem.getInstance().getHMSetHashMap();
            while ( !hMSetHashMap.isEmpty() ) {

                Map.Entry entry = hMSetHashMap.pollFirstEntry();
                if ( entry == null ) {
                    continue;
                }
                try {
                    String insertKey = (String)entry.getKey();
                    String insertValue = (String)entry.getValue();
                    Rockdis rockdis = respServerContext.getRockdisInstance();
//                    if (insertKey.contains("stockquote_20230810_tse_2330.tw_20230810")){
//                        System.out.println("[CachingSystemScheduler] HMSet Key : " + insertKey.toString() + " , Value : " + insertValue.toString());
//                        System.out.println("===");
//                        System.out.println();
//                    }
                    if (insertKey.equals("stockquote_20230810_otc_62574.tw_20230810$t")) {
                        System.out.println("[HMSET Scheduler] Start to HMSet into primary db, Primary @[Address] : , Key : " + insertKey + " , value : " + insertValue +" .");
                    }
//                    LOGGER.debug("[Caching System] Start to HMSet into primary db, Primary @[Address] : , Key : {}. value : {}" + rockdis.toString(), insertKey, insertValue);
                    rockdis.set(insertKey, insertValue);
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
//            System.out.println("Execution time of HMSet persistence is :" + endTime + " ms");
//            LOGGER.debug("Execution time of HMSet persistence is : {} ms", endTime);
        }
    }

    public static class RockdisDel extends TimerTask {
        private final Logger LOGGER = LoggerFactory.getLogger(RockdisDel.class);
        @Override
        public void run() {
//            LOGGER.debug("Cache System is enabled. Del persistence scheduler start to work. ");
            long startTime = System.currentTimeMillis();
            NavigableMap<String,String> delHashMap = CachingSystem.getInstance().getDelHashMap();
            while ( !delHashMap.isEmpty() ) {
                Map.Entry entry = delHashMap.pollFirstEntry();
                if ( entry == null ) {
                    continue;
                }
                try {
                    String deletedKey = (String)entry.getKey();
                    LOGGER.debug("Should be deleted key is {}", deletedKey);
                    Rockdis rockdis = respServerContext.getRockdisInstance();
                    LOGGER.debug("[Caching System] Start to Del data in primary db, Primary @[Address] : " + rockdis.toString());
                    rockdis.delete(deletedKey);
                } catch (RocksDBException e) {
                    e.printStackTrace();
                }
            }
            long endTime = System.currentTimeMillis() - startTime;
//            LOGGER.debug("Execution time of DEL persistence is : {} ms", endTime);
        }
    }

}
