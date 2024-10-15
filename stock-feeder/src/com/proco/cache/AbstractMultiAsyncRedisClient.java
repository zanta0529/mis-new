package com.proco.cache;

import java.util.Map;
import java.util.concurrent.Future;

import org.aredis.cache.AbstractAsyncRedisClient;
import org.aredis.cache.AsyncHandler;
import org.aredis.cache.RedisCommandInfo;

public abstract class AbstractMultiAsyncRedisClient extends AbstractAsyncRedisClient {

	 public abstract long getCommandCount();
	 public abstract long size();

	 public abstract int worker();
	 public abstract Future submitCommands(final int code, final RedisCommandInfo[] paramArrayOfRedisCommandInfo);
	 public abstract Future submitMap2Hashset(final int code, final String key,  final Map<String,Object> paramMaps);
	 public abstract boolean checkClients(final boolean pong);
	 public abstract void flush();
	 public abstract void close();
}
