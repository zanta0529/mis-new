package com.github.tonivade.resp.CachingSystem.DAO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kkw
 *
 */

public class HMSetVO implements RockdisVO {

    private String key;
    private Map<String, String> hmsetMap;

    public HMSetVO(String key, Map<String, String> hmsetMap) {
        this.key = key;
        this.hmsetMap = hmsetMap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getHmsetMap() {
        return hmsetMap;
    }

    public void setHmsetMap(Map<String, String> hmsetMap) {
        this.hmsetMap = hmsetMap;
    }
}
