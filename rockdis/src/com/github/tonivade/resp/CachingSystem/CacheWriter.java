package com.github.tonivade.resp.CachingSystem;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

public class CacheWriter<K,V> implements com.github.benmanes.caffeine.cache.Cache<K,V> {
	
	com.github.benmanes.caffeine.cache.Cache<K,V> baseCache;
	ConcurrentSkipListMap<K, Long> cacheForTimeMap;
	long flushPeriod = 10000L;
	WriteListener wl ;
	public CacheWriter(com.github.benmanes.caffeine.cache.Cache<K,V> baseCache0,ConcurrentSkipListMap<K, Long> cacheForTimeMap0,WriteListener wl, long flushPeriod){
		this.baseCache = baseCache0;
		this.cacheForTimeMap = cacheForTimeMap0;
		this.flushPeriod = flushPeriod;
		this.wl = wl;
		final CacheWriter wa = this;
		Thread th = new Thread() {
			public void run() {
				while(true) {
					long ct = System.currentTimeMillis();
					long[] a = wa.execute();
					ct = System.currentTimeMillis() - ct;
					System.out.println(getFullyDateTimeStr()+" CacheWriter:"+a[0]+" exec:"+a[1]+" last:"+a[2]+" flushPeriod:"+flushPeriod+" "+ct+"ms");
					try {
						Thread.sleep((wa.flushPeriod-ct));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		th.start();
	}

	@Override
	public V getIfPresent(Object paramObject) {
		// TODO Auto-generated method stub
		return baseCache.getIfPresent(paramObject);
	}

	@Override
	public V get(K paramK, Function<? super K, ? extends V> paramFunction) {
		// TODO Auto-generated method stub
		return baseCache.get(paramK, paramFunction);
	}

	@Override
	public Map<K, V> getAllPresent(Iterable<?> paramIterable) {
		// TODO Auto-generated method stub
		return baseCache.getAllPresent(paramIterable);
	}

	@Override
	public void put(K paramK, V paramV) {
		// TODO Auto-generated method stub
		baseCache.put(paramK, paramV);
		if(!cacheForTimeMap.containsKey(paramK))
			cacheForTimeMap.put(paramK, System.currentTimeMillis());
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> paramMap) {
		// TODO Auto-generated method stub
		baseCache.putAll(paramMap);
	}

	@Override
	public void invalidate(Object paramObject) {
		// TODO Auto-generated method stub
		cacheForTimeMap.remove(paramObject);
		baseCache.invalidate(paramObject);
	}

	@Override
	public void invalidateAll(Iterable<?> paramIterable) {
		// TODO Auto-generated method stub
		cacheForTimeMap.clear();
		baseCache.invalidateAll(paramIterable);
	}

	@Override
	public void invalidateAll() {
		// TODO Auto-generated method stub
		cacheForTimeMap.clear();
		baseCache.invalidateAll();
	}

	@Override
	public long estimatedSize() {
		// TODO Auto-generated method stub
		return baseCache.estimatedSize();
	}

	@Override
	public CacheStats stats() {
		// TODO Auto-generated method stub
		return baseCache.stats();
	}

	@Override
	public ConcurrentMap<K, V> asMap() {
		// TODO Auto-generated method stub
		return baseCache.asMap();
	}

	@Override
	public void cleanUp() {
		// TODO Auto-generated method stub
		cacheForTimeMap.clear();
		baseCache.cleanUp();
	}

	@Override
	public Policy<K, V> policy() {
		// TODO Auto-generated method stub
		return baseCache.policy();
	}
	
	@SuppressWarnings("unchecked")
	private long[] execute() {
		int c = 0;
		int k = 0;
		long l = 0;
		Set<K> keys = cacheForTimeMap.keySet();
		for(K key : keys) {
			k++;
			Long tl = cacheForTimeMap.get(key);
			if(tl==null) {
				cacheForTimeMap.remove(key);
				continue;
			}
			l = (System.currentTimeMillis()-tl);
			if(this.flushPeriod < l) {
				V val = this.asMap().get(key);
				if(val!=null) {
					try {
						this.wl.onWrite(key, val);
						c++;	
					} catch(Exception ex) {
						
					}
				}
				cacheForTimeMap.remove(key);
			}
		}
		return new long[]{k,c,l};
	}

	interface WriteListener<K, V> {
		void onWrite(K paramK, V paramV);
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
	
}
