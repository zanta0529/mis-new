package com.github.tonivade.resp.CachingSystem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author kkw
 *
 */

public class InorderSystem {

    // Singleton Pattern
    private static InorderSystem instance = null;
    private static Map<String, Long> inorderMap;
    private InorderSystem(){
        inorderMap = new ConcurrentHashMap<>();
    }
    synchronized static public InorderSystem getInstance() {
        if (instance == null) {
            instance = new InorderSystem();
        }
        return instance;
    }

    public Map<String,Long> getInorderMap(){
        return inorderMap;
    }

    public void putTimestamp(String key, Long timestamp) {
        inorderMap.put(key, timestamp);
    }

    public Long getTimestamp(String key) {
        return inorderMap.get(key);
    }
}
