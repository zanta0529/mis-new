package com.proco.cache;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.aredis.cache.AsyncHandler;
import org.aredis.cache.AsyncRedisClient;
import org.aredis.cache.AsyncRedisConnection;
import org.aredis.cache.AsyncRedisFactory;
import org.aredis.cache.JavaHandler;
import org.aredis.cache.RedisCommand;
import org.aredis.cache.RedisCommandInfo;

import com.proco.util.Utility;

public class MultiAsyncRedisClient extends AbstractMultiAsyncRedisClient {
	AtomicLong atomAddCount = new AtomicLong(0L);
	AtomicLong atomCount = new AtomicLong(0L);
	private AsyncRedisConnection [] cons;
	private AsyncRedisClient aredis0;
	private AtomicInteger index;
	ExecutorService executor;
	// count specifies the number of connections to create to redis server
	public MultiAsyncRedisClient(String redis,String auth, int count, ExecutorService executor) {
		this.executor = executor;
		index = new AtomicInteger(count - 1);
		cons = new AsyncRedisConnection[count];
		for(int i = 0; i < count; i++) {
			AsyncRedisFactory f = new AsyncRedisFactory(null);
	        JavaHandler jj = new JavaHandler();
	        jj.setStringCompressionThreshold(1048576*5);
	        f.setDataHandler(jj);
	        if(auth!=null) {
	        	f.setAuth(redis, auth);
	        }
			cons[i] = (AsyncRedisConnection) f.getClient(redis);
		}
		AsyncRedisFactory f = new AsyncRedisFactory(null);
        JavaHandler jj = new JavaHandler();
        jj.setStringCompressionThreshold(1048576*5);
        f.setDataHandler(jj);
        if(auth!=null) {
        	f.setPoolSize(5);
        	f.setAuth(redis, auth);
        }
		aredis0 = f.getClient(redis);
	}
	
	public boolean checkClients(boolean pong) {
		boolean rt = false;
		int c = 0;
		for(AsyncRedisConnection aredis : cons) {
			c++;
			try {
				String cvals = (String)aredis.submitCommand(RedisCommand.PING).get().getResult();
				if(cvals==null) {
					System.out.println(Utility.getFullyDateTimeStr()+" "+c+" check redis client error. PING="+cvals);
					rt=false;
				} else if(cvals.equals("PONG")){
					//System.out.println(Utility.getFullyDateTimeStr()+" "+c+" check redis client ok. PING="+cvals);
					rt = true;
				} else if(cvals.equals("OK") && !pong){
					//System.out.println(Utility.getFullyDateTimeStr()+" "+c+" check redis client ok. PING="+cvals);
					rt = true;
				} else if(cvals.equals("0") && !pong){
					//System.out.println(Utility.getFullyDateTimeStr()+" "+c+" check redis client ok. PING="+cvals);
					rt = true;
				} else {
					System.out.println(Utility.getFullyDateTimeStr()+" "+c+" check redis client error. PING="+cvals);
					rt=false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(Utility.getFullyDateTimeStr()+" "+c+" check redis client error. "+e.toString());
				rt = false;
			} 
			if(!rt) break;
		}
		if(!rt) return rt;
		try {
			String cvals = (String)aredis0.submitCommand(RedisCommand.PING).get().getResult();
			if(cvals==null) {
				System.out.println(Utility.getFullyDateTimeStr()+" check redis client error. PING="+cvals);
				rt=false;
			} else if(cvals.equals("PONG")){
				//System.out.println(Utility.getFullyDateTimeStr()+" check redis client ok. PING="+cvals);
				rt = true;
			} else if(cvals.equals("OK") && !pong){
				//System.out.println(Utility.getFullyDateTimeStr()+" check redis client ok. PING="+cvals);
				rt = true;
			} else if(cvals.equals("0") && !pong){
				//System.out.println(Utility.getFullyDateTimeStr()+" check redis client ok. PING="+cvals);
				rt = true;
			} else {
				System.out.println(Utility.getFullyDateTimeStr()+" check redis client error. PING="+cvals);
				rt=false;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(Utility.getFullyDateTimeStr()+" check redis client error. "+e.toString());
			rt = false;
		} 
		
		return rt;
	}

	private int getNextIndex() {
		for (;;) {
            int current = index.get();
            int next = current + 1;
            if (next >= cons.length) {
            	next = 0;
            }
            if (index.compareAndSet(current, next)) {
                return next;
            }
        }
	}

	@Override
	public Future<RedisCommandInfo[]> submitCommands(
			RedisCommandInfo[] commands,
			AsyncHandler<RedisCommandInfo[]> completionHandler,
			boolean requireFutureResult, boolean isSyncCallback) {
		int connectionIndex = getNextIndex();
		Future<RedisCommandInfo[]> futureResult = cons[connectionIndex].submitCommands(commands, completionHandler, requireFutureResult, isSyncCallback);
		return futureResult ;
	}

	@Override
	public Future<RedisCommandInfo> submitCommand(RedisCommandInfo command,
			AsyncHandler<RedisCommandInfo> completionHandler,
			boolean requireFutureResult, boolean isSyncCallback) {
		int connectionIndex = getNextIndex();
		Future<RedisCommandInfo> futureResult = cons[connectionIndex].submitCommand(command, completionHandler, requireFutureResult, isSyncCallback);
		return futureResult ;
	}
	
	
	@Override
	public void sendCommand(final RedisCommand paramRedisCommand,final Object... paramVarArgs) {
		atomAddCount.addAndGet(1);
		atomCount.addAndGet(1);
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					int connectionIndex = getNextIndex();
					cons[connectionIndex].sendCommand(paramRedisCommand,paramVarArgs);					  
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					atomCount.addAndGet(-1);
				}
			}
		});
	}
	
	
	//@Override
	//public Future<RedisCommandInfo[]> submitCommands(final RedisCommandInfo[] paramArrayOfRedisCommandInfo){
	//	return aredis0.submitCommands(paramArrayOfRedisCommandInfo);
	//}
	
	@Override
	public Future<RedisCommandInfo[]> submitCommands(final RedisCommandInfo[] paramArrayOfRedisCommandInfo){
		atomAddCount.addAndGet(1);
		atomCount.addAndGet(1);
		executor.submit(new Runnable() {
			@Override
				public void run() {
				try {
					int connectionIndex = getNextIndex();
					cons[connectionIndex].
					submitCommands(paramArrayOfRedisCommandInfo);
				} catch (Exception ex) {
					ex.printStackTrace();	
				} finally {
					atomCount.addAndGet(-1);
				}
			}
		});
		return null;
	}
	
	public Future submitCommands2(final RedisCommandInfo[] paramArrayOfRedisCommandInfo){
		atomAddCount.addAndGet(1);
		atomCount.addAndGet(1);
		Future aa = executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					int connectionIndex = getNextIndex();
					cons[connectionIndex].
					submitCommands(paramArrayOfRedisCommandInfo);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					atomCount.addAndGet(-1);
				}   
			}
			   
		});
		return aa;
	}
	
	public Future submitCommands(final int code, final RedisCommandInfo[] paramArrayOfRedisCommandInfo){
		atomAddCount.addAndGet(1);
		atomCount.addAndGet(1);
		Future aa = executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					int connectionIndex = code%cons.length;
					cons[connectionIndex].
					submitCommands(paramArrayOfRedisCommandInfo);
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					atomCount.addAndGet(-1);
				}
			}
		});
		return aa;
	}

	@Override
	public Future<RedisCommandInfo> submitCommand(RedisCommand command, Object... params){
		return aredis0.submitCommand(command,params);
	}
	
	public long size() {
		return atomCount.get();
	}
	
	public void add(long i) {
		 atomCount.addAndGet(i);
	}
	
	public static void main(String args[]) {
        AsyncRedisClient aredis = new MultiAsyncRedisClient("localhost:10001",null, 32, null);
		Object[] vals;
		try {
			vals = (Object[])aredis.submitCommand(RedisCommand.ZREVRANGE, "c_sz.20181224.000001_t", "0", "0").get().getResult();
			if(vals!=null && vals.length==1){
		        	org.json.JSONObject js = new org.json.JSONObject(vals[0].toString());
				System.out.println(js.toString());
				
				
				String val = (String) aredis.submitCommand(RedisCommand.GET, "sz").get().getResult();
				System.out.println(val);
				
			} else {
				System.out.println(vals);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
        /*
        // Use sendCommand instead of submitCommand when you are not interested in the Return value
        aredis.sendCommand(RedisCommand.SETEX, "hello", "300", "world");
        // You can also save Java Objects as values like in the below command. Whereas String values are
        // stored as UTF-8 Java Objects are Serialized and Stored. Both are GZipped if the data length is
        // more than 1 Kb. This is done by the default Data Handler.
        aredis.sendCommand(RedisCommand.SETEX, "java_date", "300", new Date());
        // Now retrieve and check the values we have stored
        Future<RedisCommandInfo> future = aredis.submitCommand(RedisCommand.GET, "hello");
        // In the below commented lines the key "hello" is passed as a byte array which also
        // works. Keys can be passed as byte arrays if they cannot be easily encoded as java Strings
        // byte [] keyBytes = "hello".getBytes();
        // Future<RedisCommandInfo> future = aredis.submitCommand(RedisCommand.GET, keyBytes);
        Future<RedisCommandInfo> future1 = aredis.submitCommand(RedisCommand.GET, "java_date");
        try {
            // Whenever possible fetch the results together after submitting the commands as it
            // is done here. this increases the pipeline size resulting in better performance
            String val = (String) future.get().getResult();
            System.out.println("Got back val = " + val);
            Date currentDate = (Date) future1.get().getResult();
            System.out.println("Got back date = " + currentDate);
        }
        catch(Exception e) {
            e.printStackTrace();
        }*/
    }

	@Override
	public void flush() {
		// TODO Auto-generated method stub
		for(AsyncRedisConnection con : cons) {
			try {
				con.getConnection().requiresFlush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() {
		for(AsyncRedisConnection con : cons) {
			try {
				con.getConnection().close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		AsyncRedisConnection con0 = (AsyncRedisConnection)aredis0;
		try {
			con0.getConnection().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public long getCommandCount() {
		// TODO Auto-generated method stub
		return atomAddCount.get();
	}

	@Override
	public Future submitMap2Hashset(final int code,final String baseKey,final Map<String, Object> infoHash2) {
		// TODO Auto-generated method stub
		atomAddCount.addAndGet(1);
		atomCount.addAndGet(1);
		Future aa = executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					boolean inst = false;
					java.util.ArrayList<String> quoteList = new java.util.ArrayList<String>();
					quoteList.add(baseKey);
					Set<String> keys = infoHash2.keySet();
					for(String key : keys) {
						Object val = infoHash2.get(key);
						if(val == null) continue;
						if(val instanceof String) {
							quoteList.add(key);
							quoteList.add((String)val);
							inst = true;
						}
						
					}
					
					if(inst) {
						final String[] aab = new String[quoteList.size()];
						quoteList.toArray(aab);
						RedisCommandInfo[] rinfo = new RedisCommandInfo[]{new RedisCommandInfo(RedisCommand.HMSET, aab)};
						int connectionIndex = code%cons.length;
						cons[connectionIndex].
						submitCommands(rinfo);					
					}					
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					atomCount.addAndGet(-1);
				}
			}
		});
		return aa;		
	}

	@Override
	public int worker() {
		// TODO Auto-generated method stub
		return this.cons.length;
	}

}
