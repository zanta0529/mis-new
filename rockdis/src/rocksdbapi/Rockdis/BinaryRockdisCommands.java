package rocksdbapi.Rockdis;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import rocksdbapi.Rockdis.Models.MemberScoreVO;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public interface BinaryRockdisCommands {


    String set(byte[] key, byte[] value) throws RocksDBException;

    byte[] get(byte[] key) throws RocksDBException;

    String hset(byte[] key, byte[] field, byte[] value) throws RocksDBException;

    byte[] hget(byte[] key, byte[] field) throws RocksDBException;

    String hmset(byte[] key, Map<byte[], byte[]> hash) throws RocksDBException;

    List<byte[]> hmget(byte[] key, byte[]... fields) throws RocksDBException;

    Map<byte[], byte[]> hgetAll(byte[] key) throws RocksDBException;

    List<byte[]> getKeys(byte[] key) throws RocksDBException;

    Integer hincrBy(byte[] key, byte[] field, int value) throws RocksDBException;

    void compactRange() throws RocksDBException;

    void tryCatchUpWithPrimary() throws RocksDBException;
    
    void close() throws RocksDBException;

    boolean isClosed();
    RocksDB getDB();
    AtomicLong getIteratorCount() ;

    Integer delete(byte[] key) throws RocksDBException;

    Integer zadd(byte[] key, Integer score, byte[] member) throws RocksDBException;

    Integer zadd(List<MemberScoreVO> memberScoreVOList) throws RocksDBException;

    List<byte[]> zrange(byte[] key, int start, int stop) throws RocksDBException;

    List<byte[]> zrangeWithScores(byte[] key, int start, int stop) throws RocksDBException;

    List<byte[]> zrevrange(byte[] key, int start, int stop) throws RocksDBException;

    List<byte[]> zrevrangeWithScores(byte[] key, int start, int stop) throws RocksDBException;

    Integer zcard(byte[] key) throws RocksDBException;

    List<byte[]> zrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException;

    List<byte[]> zrevrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException;

    Integer zrem(byte[] key, byte[] member) throws RocksDBException;

    Integer zrem(byte[] key, byte[]... members) throws RocksDBException;

    Integer zremrangebyscore(byte[] key, int min, int max) throws RocksDBException;

    Integer zremrangebyrank(byte[] key, int start, int stop) throws RocksDBException;
}
