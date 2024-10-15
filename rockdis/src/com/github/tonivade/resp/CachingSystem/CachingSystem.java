package com.github.tonivade.resp.CachingSystem;


import com.github.tonivade.resp.CachingSystem.DAO.RockdisVO;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author kkw
 */

public class CachingSystem {

    // Singleton Pattern
    private static CachingSystem instance = null;
    private CachingSystem(){}
    synchronized static public CachingSystem getInstance() {
        if (instance == null) {
            instance = new CachingSystem();
        }
        return instance;
    }

    public enum Action {
        SET, HSET, HMSET, DEL
    }
    private NavigableMap<String, String> setHashMap = new ConcurrentSkipListMap<>();
    private NavigableMap<String, String> hSetHashMap = new ConcurrentSkipListMap<>();
    private NavigableMap<String, String> hMSetHashMap = new ConcurrentSkipListMap<>();
    private NavigableMap<String, String> delHashMap = new ConcurrentSkipListMap<>();

    public void put(Action type, String key, String value){
        switch (type){
            case SET:
                setHashMap.put(key, value);
                break;
            case HSET:
                hSetHashMap.put(key, value);
                break;
            case HMSET:
                hMSetHashMap.put(key, value);
                break;
            case DEL:
                delHashMap.put(key, "");
        }
    }

    public void del(Action type, String key) {
        setHashMap.remove(key);
        hSetHashMap.remove(key);
        hMSetHashMap.remove(key);
    }

    public NavigableMap<String, String> getSetHashMap() {
        return setHashMap;
    }

    public NavigableMap<String, String> gethSetHashMap() {
        return hSetHashMap;
    }

    public NavigableMap<String, String> getHMSetHashMap() {
        return hMSetHashMap;
    }

    public NavigableMap<String, String> getDelHashMap() {
        return delHashMap;
    }


}
