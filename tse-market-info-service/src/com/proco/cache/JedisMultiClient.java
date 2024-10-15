package com.proco.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.proco.util.DateManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.resps.Tuple;

public class JedisMultiClient {

	private JedisPool m_pool;
	private int logCount = 0;
	private boolean isConnected = false;
	private Thread checkThread;
	public ConcurrentHashMap<String, String> marketToDateMap;
	public ConcurrentHashMap<String, String> previosMarketToDateMap;
	private String word;
	private long checkTime = 0L;
	
	public enum Status {
		RECONNECT, ALIVE, DEAD
	}

	public Status status = Status.DEAD;

	private String ip, port;

	public JedisMultiClient(String ip, String port,String word0) {
		if (ip == null && port == null) {
			return;
		}

		marketToDateMap = new ConcurrentHashMap<String, String>();
		previosMarketToDateMap = new ConcurrentHashMap<String, String>();

		this.ip = ip;
		this.port = port;

		if(word0!=null) if(!word0.equals("")) this.word = word0;
		
		initPool();
		initPreMarketDate();

		checkThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}


					try {
						if (!isConnected) {
							status = Status.RECONNECT;
							initPool();
						}
						initPreMarketDate();
					} catch (Exception e) {
						status = Status.DEAD;
						isConnected = false;
					} 
					logCount++;
				}

			}

		});
		checkThread.start();
	}

	public void initPreMarketDate() {

		Jedis jedis = null;
		String[] markets = new String[] {"tse","otc"};

		try {
			jedis = getJedis();
			if (jedis == null) {
				isConnected = false;
				status = Status.DEAD;
			}

			for (String market : markets) {
				String redisDate = jedis.get(market);
				if (redisDate != null) {
					previosMarketToDateMap.put(market, redisDate);
					DateManager.setExchangeDate(market, redisDate);
					this.checkTime = System.currentTimeMillis();
				}
				isConnected = true;
				status = Status.ALIVE;
			}
		} catch (Exception e) {
			isConnected = false;
			status = Status.DEAD;
		} finally {
			returnResource(jedis);
		}

	}

	private void initPool() {
		try {
			JedisPoolConfig poolConfig = new JedisPoolConfig();
			poolConfig.setMaxTotal(10240);
			poolConfig.setMaxIdle(2000);
			poolConfig.setMaxWaitMillis(500);
			poolConfig.setMinIdle(16);
			poolConfig.setTestWhileIdle(true);
			poolConfig.setMinEvictableIdleTimeMillis(100);
			poolConfig.setNumTestsPerEvictionRun(3);
			poolConfig.setTestOnBorrow(true);
			poolConfig.setBlockWhenExhausted(false);

			if (m_pool != null) {
				m_pool.close();
			}

			if (word != null) {
				try {
					m_pool = new JedisPool(poolConfig, ip, Integer.parseInt(port), 2000, word);	
					if (m_pool.getResource() == null) {
						return;
					}
				} catch (Exception e) {
					return;
				}
			} else {
				m_pool = new JedisPool(poolConfig, ip, Integer.parseInt(port));
			}
			status = Status.ALIVE;
		} catch (Exception e) {
		}
	}

	public void close() {
		if (m_pool != null) {
			m_pool.close();
		}
	}

	public boolean isConnected() {
		return isConnected;
	}

	public List<Tuple> getSortedSetByKey(String key) {
		if (Status.DEAD == status) {
			return null;
		}

		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.zrangeWithScores(key, 0, 1);
		} catch (Exception e) {
			isConnected = false;
			status = Status.DEAD;
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	public List<Tuple> getSortedSetByScore(String key, long score , int size) {
		if (Status.DEAD == status) {
			return null;
		}

		Jedis jedis = null;
		try {
			jedis = getJedis();
			//System.out.println("getSortedSetByScore-->"+key+" score:"+score+" size:"+size);
			return jedis.zrangeByScoreWithScores(key, String.valueOf(score) , String.valueOf(99999999) , 0, size);
		} catch (Exception e) {
			isConnected = false;
			status = Status.DEAD;
		} finally {
			returnResource(jedis);
		}
		return null;
	}
	
	
	public Map<String, String> getDataRowMapByKey(String key) {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			return jedis.hgetAll(key);
		} catch (Exception e) {
			isConnected = false;
			status = Status.DEAD;
		} finally {
			returnResource(jedis);
		}
		return null;
	}
	
	public String getDateByMarket(String market) {	
		if (marketToDateMap.get(market) != null) {
			return marketToDateMap.get(market);
		}
		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (jedis == null) {
				isConnected = false;
				status = Status.DEAD;
				return null;
			}
			String date = jedis.get(market);
			if (date != null && !"".equals(date.trim())) {
				marketToDateMap.put(market, date);
			}
			return date;
		} catch (Exception e) {
			isConnected = false;
			status = Status.DEAD;
			//LOG.error(e);
		} finally {
			returnResource(jedis);
		}
		return null;
	}

	/**
	 * 同步获取Jedis实例
	 * 
	 * @return Jedis
	 */
	public synchronized Jedis getJedis() {
		if (m_pool == null) {
			initPool();
		}
		Jedis jedis = null;
		try {
			if (m_pool != null) {
				// 从池中获取一个Jedis对象
				jedis = m_pool.getResource();
			}
		} catch (Exception e) {
		}

		return jedis;
	}

	public String getCurrentCacheDate(String market) {
		return this.marketToDateMap.get(market);
	}

	public boolean verifyDateAfterClear(String market) {

		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (jedis == null) {
				isConnected = false;
				status = Status.DEAD;
				return false;
			}
			String redisDate = jedis.get(market);
			if (redisDate == null) {
				return false;
			}
			String cacheDate = previosMarketToDateMap.get(market);
			if (cacheDate != null) {
				if (cacheDate.equals(redisDate)) {
					return false;
				} else {
					if (redisDate != null) {
						previosMarketToDateMap.put(market, redisDate);
					}
				}
			}
		} catch (Exception e) {
			isConnected = false;
			status = Status.DEAD;
			return false;
		} finally {
			returnResource(jedis);
		}
		return true;
	}

	public List<String> verifyDateAfterResetRedis() {
		Jedis jedis = null;
		try {
			jedis = getJedis();
			if (jedis == null) {
				isConnected = false;
				status = Status.DEAD;
				return null;
			}

			List<String> list = new ArrayList<String>();

			for (Entry<String, String> entry : this.previosMarketToDateMap.entrySet()) {
				String market = entry.getKey();
				String redisDate = jedis.get(market);
				String cacheDate = previosMarketToDateMap.getOrDefault(market, "");
				if (!cacheDate.equals(redisDate)) {
					if (redisDate != null) {
						previosMarketToDateMap.put(market, redisDate);
						list.add(market);
					}
				}
			}

			return list;
		} catch (Exception e) {
			isConnected = false;
			status = Status.DEAD;
			return null;
		} finally {
			returnResource(jedis);
		}
	}

	public void clear() {
		previosMarketToDateMap.putAll(marketToDateMap);
		this.marketToDateMap.clear();
	}

	/**
	 * 
	 * 释放jedis资源
	 * 
	 * @param jedis
	 * 
	 */

	public void returnResource(final Jedis jedis) {
		if (jedis != null && m_pool != null) {
			m_pool.returnResource(jedis);
		}
	}
	
	public long getCheckTime() {
		return this.checkTime;
	}
	
}
