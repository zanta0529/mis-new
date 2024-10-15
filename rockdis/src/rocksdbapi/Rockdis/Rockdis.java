package rocksdbapi.Rockdis;

import org.rocksdb.CompressionType;
import org.rocksdb.RocksDBException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class Rockdis extends BinaryRockdis {
    public Rockdis(String dbPath) throws RocksDBException {
        super(dbPath);
    }

    public Rockdis(String dbPath, CompressionType compressionType) throws RocksDBException {
        super(dbPath, compressionType);
    }

    public Rockdis(String dbPath, String secondaryInstanceDirectory) throws RocksDBException {
        super(dbPath, secondaryInstanceDirectory);
    }


    public String set(String key, String value) throws RocksDBException {
        return rockdisClient.set(key, value);
    }

    public String get(String key) throws RocksDBException {
        if (rockdisClient.get(key) == null) {
            return "null";
        } else {
            return rockdisClient.get(key);
        }
    }

    public String hget(String key, String field) throws RocksDBException {
        if (rockdisClient.hget(key, field) == null) {
            return "null";
        } else {
            return rockdisClient.hget(key, field);
        }
    }

    public String hset(String key, String field, String value) throws RocksDBException {
        return rockdisClient.hset(key, field, value);
    }

    public List<String> hmget(String key, String... fields) throws RocksDBException {
        return rockdisClient.hmget(key, fields);
    }

    public String hmset(String key, Map<String, String> hash) throws RocksDBException {
        return rockdisClient.hmset(key, hash);
    }

    public Map<String, String> hgetAll(String key) throws RocksDBException {
        return rockdisClient.hgetAll(key);
    }

    public List<String> getKeys(String key) throws RocksDBException {
        return rockdisClient.getKeys(key);
    }

    public Integer hincrBy(String key, String field, int value) throws RocksDBException {
        return rockdisClient.hincrBy(key, field, value);
    }

    public void tryCatchUpWithPrimary() throws RocksDBException {
        rockdisClient.tryCatchUpWithPrimary();
    }

    /**
     * If delete key action is succeed, then return how many keys deleted. Otherwise, return 0.
     * @param key
     * @return boolean true or false
     * @throws RocksDBException
     */
    public Integer delete(String key) throws RocksDBException {
        return rockdisClient.delete(key);
    }

    public Integer hdel(String key, String... fields) throws RocksDBException {
        return rockdisClient.hdel(key, fields);
    }

    public Integer zrem(String key, String... fields) throws RocksDBException {
        return rockdisClient.zrem(key, fields);
    }
    
	public long[] insertIntoDB(ConcurrentSkipListMap<String, Object> patchIntoDbMap) throws RocksDBException {
		return rockdisClient.insertIntoDB(patchIntoDbMap);
	}
    
}
