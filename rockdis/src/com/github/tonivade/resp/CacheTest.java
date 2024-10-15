package com.github.tonivade.resp;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * <p> Since: 2023/10/19 </p>
 *
 * @author kkw
 * @version 1.0
 */
public class CacheTest {


    public static void main(String[] args) throws InterruptedException {
        Cache<String, String> cache = Caffeine.newBuilder()
                .scheduler(Scheduler.forScheduledExecutorService(Executors.newScheduledThreadPool(1)))
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .maximumSize(5)
//                .evictionListener((String key, String value, RemovalCause cause) -> {
//                    callHello(key, value);
//                })
//                        System.out.println("[EVICT]Key : " + key + " , Value : " + value + " , Cause : " + cause))
                .removalListener((String key, String value, RemovalCause cause) -> {
                        callHello(key, value, cause);
                        })
                .build();


        cache.put("1", "1");

        Map<String, String> cacheMap = cache.asMap();
        for (Map.Entry<String, String> entry : cacheMap.entrySet()) {

            Thread.sleep(6000);

            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
            cache.invalidate(entry.getKey());

        }

        // show all record in cache
//        cache.asMap().forEach((k, v) -> {
//            System.out.println("Key : " + k + " , Value : " + v);
//        });


//        cache.put("1", "1");
//        while (true) {
//            try {
//                Thread.sleep(1000);
//                System.out.println("Key : " + "1" + " , Value : " + cache.getIfPresent("1"));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

    }

    private static void callHello(String key, String value, RemovalCause cause) {
        System.out.println("hello, key : " + key + " , value : " + value + " , cause :" + cause);
    }


}
