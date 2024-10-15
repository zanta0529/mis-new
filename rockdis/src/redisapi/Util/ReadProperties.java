package redisapi.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/10/26
 */

public class ReadProperties {

    public static String rockdisIpAddress = "localhost";
    public static int rockdisPort = 5566;
    private static Properties properties;
    private static boolean enableProperties = true;
    private static String persistenceMode = "rw";
    private static String primaryDir = "/tmp/rockdisTemp/";
    private static String secondaryDir = "/tmp/secondTemp/";
    private static boolean isEnableCrossDayDebugMode = false;
    private static boolean isEnableCrossDayMechanism = true;
    private static boolean isEnableAuth = true;
    private static String authPassword = "admin";
    private static boolean enableCacheSystem = false;
    private static int cachePeriod = 2000;
    private static int worker = Runtime.getRuntime().availableProcessors() * 2;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadProperties.class);
    private static final int cacheEvictTimePeriod = 15000;

    public static void readFile() {
        try {
            FileInputStream fileInputStream = new FileInputStream("rockdis.properties");
            properties = new Properties();
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            enableProperties = false;
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getRockdisIpAddress() {
        if (enableProperties && properties.getProperty("ip") != null) {
            return properties.getProperty("ip");
        } else {
            return rockdisIpAddress;
        }
    }

    public static int getRockdisPort() {
        if (enableProperties && properties.getProperty("port") != null) {
            return Integer.parseInt(properties.getProperty("port"));
        } else {
            return rockdisPort;
        }
    }
    
    public static int getRockdisWorker() {
        if (enableProperties && properties.getProperty("worker") != null) {
            return Integer.parseInt(properties.getProperty("worker"));
        } else {
            return worker;
        }
    }

    public static String getPersistenceMode() {
        if (enableProperties && properties.getProperty("persistence_mode") != null) {
            return properties.getProperty("persistence_mode");
        } else {
            return persistenceMode;
        }
    }

    public static String getPrimaryDir() {
        if (enableProperties && properties.getProperty("primary_dir") != null) {
            return properties.getProperty("primary_dir");
        } else {
            return primaryDir;
        }
    }

    public static String getSecondaryDir() {
        if (enableProperties && properties.getProperty("secondary_dir") != null) {
            return properties.getProperty("secondary_dir");
        } else {
            return secondaryDir;
        }
    }

    public static boolean isEnableCrossDayDebugMode(){
        if (enableProperties && properties.getProperty("enable_cross_day_debug_mode") != null) {
            return Boolean.parseBoolean(properties.getProperty("enable_cross_day_debug_mode"));
        } else {
            return isEnableCrossDayDebugMode;
        }
    }

    public static boolean isEnableCrossDayMechanism(){
        if (enableProperties && properties.getProperty("enable_cross_day_mechanism") != null) {
            return Boolean.parseBoolean(properties.getProperty("enable_cross_day_mechanism"));
        } else {
            return isEnableCrossDayMechanism;
        }
    }

    public static boolean isEnableAuth() {
        if (enableProperties && properties.getProperty("enable_auth") != null) {
            return Boolean.parseBoolean(properties.getProperty("enable_auth"));
        } else {
            return isEnableAuth;
        }
    }

    public static String getAuthString() {
        if (enableProperties && properties.getProperty("auth_password") != null) {
            return properties.getProperty("auth_password");
        } else {
            return authPassword;
        }
    }

    public static boolean enableCacheSystem() {
        if (enableProperties && properties.getProperty("enable_cache_system") != null) {
            return Boolean.parseBoolean(properties.getProperty("enable_cache_system"));
        } else {
            return enableCacheSystem;
        }
    }

    @Deprecated
    public static int getCachePeriod() {
        if (enableProperties && properties.getProperty("cache_period") != null) {
            return Integer.parseInt(properties.getProperty("cache_period"));
        } else {
            return cachePeriod;
        }
    }

    public static int getCacheEvictTimePeriod() {
        if (enableProperties && properties.getProperty("cache_evict_time_period") != null) {
            return Integer.parseInt(properties.getProperty("cache_evict_time_period"));
        } else {
            return cacheEvictTimePeriod;
        }
    }
}
