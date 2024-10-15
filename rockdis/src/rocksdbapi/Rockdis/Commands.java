package rocksdbapi.Rockdis;

import org.rocksdb.RocksDBException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public interface Commands {

    String get(final String key) throws  RocksDBException;

    String set(final String key, final String value) throws RocksDBException;

    String hget(final String key, final String field) throws RocksDBException;

    String hset(final String key, final String field, final String value) throws RocksDBException;

    List<String> hmget (final String key, final String... fields) throws RocksDBException;

    String hmset(final String key, final Map<String, String> hash) throws RocksDBException;

    Integer hdel(final String key, final String... fields) throws RocksDBException;

    Integer zrem(String key, String... fields) throws RocksDBException;

    Map<String, String> hgetAll(final String key) throws RocksDBException;

    List<String> getKeys(final String key) throws RocksDBException;

    Integer hincrBy(final String key, final String field, int value) throws RocksDBException;

    Integer delete(final String key) throws RocksDBException;

    Integer zadd(String key, Integer score, String member) throws RocksDBException;

    List<String> zrange(String key, int start, int stop) throws RocksDBException;

    List<String> zrevrange(String key, int start, int stop) throws RocksDBException;

    Integer zrem(String key, String member) throws RocksDBException;

    long[] insertIntoDB(ConcurrentSkipListMap<String, Object> patchIntoDbMap) throws RocksDBException;
}
