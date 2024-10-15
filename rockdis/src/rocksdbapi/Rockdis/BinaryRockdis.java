package rocksdbapi.Rockdis;

import org.rocksdb.CompressionType;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdbapi.Rockdis.Models.MemberScoreVO;
import rocksdbapi.Rockdis.Params.RockdisParams;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BinaryRockdis implements BinaryRockdisCommands {

    public static Logger logger = LoggerFactory.getLogger(BinaryRockdis.class);
    protected RockdisClient rockdisClient;

    public BinaryRockdis(final String dbPath) throws RocksDBException {
        logger.debug("Use Primary Instance mode.");
        rockdisClient = new RockdisClient(dbPath);
    }

    public BinaryRockdis(final String dbPath, final CompressionType compressionType) throws RocksDBException {
        logger.debug("Use Primary Instance mode.");
        rockdisClient = new RockdisClient(dbPath, compressionType);
    }

    public BinaryRockdis(final String dbPath, final String secondaryInstanceDirectory) throws RocksDBException {
        logger.debug("Use Secondary Instance mode.");
        rockdisClient = new RockdisClient(dbPath, secondaryInstanceDirectory);
    }

    @Override
    public String set(byte[] key, byte[] value) throws RocksDBException {
        return rockdisClient.set(key, value);
    }

    @Override
    public byte[] get(byte[] key) throws RocksDBException {
        return rockdisClient.get(key);
    }

    @Override
    public String hset(byte[] key, byte[] field, byte[] value) throws RocksDBException {
        rockdisClient.hset(key, field, value);
        return RockdisParams.SUCCESSFUL;
    }

    @Override
    public byte[] hget(byte[] key, byte[] field) throws RocksDBException {
        return rockdisClient.hget(key, field);
    }

    @Override
    public String hmset(byte[] key, Map<byte[], byte[]> hash) throws RocksDBException {
        rockdisClient.hmset(key, hash);
        return RockdisParams.SUCCESSFUL;
    }

    @Override
    public List<byte[]> hmget(byte[] key, byte[]... fields) throws RocksDBException {

        return rockdisClient.hmget(key, fields);
    }

    @Override
    public Map<byte[], byte[]> hgetAll(byte[] key) throws RocksDBException {
        return rockdisClient.hgetall(key);
    }

    @Override
    public List<byte[]> getKeys(byte[] key) throws RocksDBException {
        return rockdisClient.getKeys(key);
    }

    @Override
    public Integer hincrBy(byte[] key, byte[] field, int value) throws RocksDBException {
        return rockdisClient.hincrBy(key, field, value);
    }

    @Override
    public void tryCatchUpWithPrimary() throws RocksDBException {
        rockdisClient.tryCatchUpWithPrimary();
    }

    @Override
    public void close() throws RocksDBException {
        rockdisClient.close();
    }

    @Override
    public boolean isClosed() {
        return !rockdisClient.db.isOwningHandle();
    }

    @Override
    public RocksDB getDB() {
        return rockdisClient.db;
    }


    @Override
    public AtomicLong getIteratorCount() {
        return rockdisClient.getIteratorCount();
    }

    /**
     * If delete key action is succeed, then return how many keys deleted. Otherwise, return 0.
     * @param key
     * @return boolean true or false
     * @throws RocksDBException
     */
    @Override
    public Integer delete(byte[] key) throws RocksDBException {
        return rockdisClient.delete(key);
    }

    /**
     * This method provide ZADD operation which is similar to redis ZADD.
     * The difference between Rockdis and redis is Rockdis do not provide
     * negative score and floating number of score. Rockdis only provide
     * Integer score insert.
     * @param key
     * @param score
     * @param member
     * @return Integer 1,0
     *          1 means execute successfully, 0 means do not change
     * @throws RocksDBException
     * @throws ArithmeticException
     */
    @Override
    @Deprecated
    public Integer zadd(byte[] key, Integer score, byte[] member) throws RocksDBException {
        return rockdisClient.zadd(key, score, member);
    }


    @Override
    public Integer zadd(List<MemberScoreVO> memberScoreVOList) throws RocksDBException {
        return rockdisClient.zadd(memberScoreVOList);
    }

    @Override
    public List<byte[]> zrange(byte[] key, int start, int stop) throws RocksDBException {
        return rockdisClient.zrange(key, start, stop);
    }

    @Override
    public List<byte[]> zrangeWithScores(byte[] key, int start, int stop) throws RocksDBException {
        return rockdisClient.zrangeWithScores(key, start,stop);
    }

    @Override
    public List<byte[]> zrevrange(byte[] key, int start, int stop) throws RocksDBException {
        return rockdisClient.zrevrange(key, start, stop);
    }

    @Override
    public List<byte[]> zrevrangeWithScores(byte[] key, int start, int stop) throws RocksDBException {
        return rockdisClient.zrevrangeWithScores(key, start, stop);
    }

    @Override
    public Integer zcard(byte[] key) throws RocksDBException {
        return rockdisClient.zcard(key);
    }

    /**
     * This method provide ZRANGEBYSCORE redis-like operation.
     * Rockdis ZRANGEBYSCORE only provide Integer number range scan.
     * @param key
     * @param rangeStart
     * @param rangeEnd
     * @return
     * @throws RocksDBException
     * @throws ArithmeticException
     */
    @Override
    public List<byte[]> zrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException {
        return rockdisClient.zrangebyscore(key, rangeStart, rangeEnd);
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException {
        return rockdisClient.zrevrangebyscore(key, rangeStart, rangeEnd);
    }

    @Deprecated
    @Override
    public Integer zrem(byte[] key, byte[] member) throws RocksDBException {
        return rockdisClient.zrem(key, member);
    }

    @Override
    public Integer zrem(byte[] key, byte[]... member) throws RocksDBException {
        return rockdisClient.zrem(key, member);
    }

    @Override
    public Integer zremrangebyscore(byte[] key, int min, int max) throws RocksDBException {
        return rockdisClient.zremrangebyscore(key, min, max);
    }

    @Override
    public Integer zremrangebyrank(byte[] key, int start, int stop) throws RocksDBException {
        return rockdisClient.zremrangebyrank(key, start, stop);
    }

	@Override
	public void compactRange() throws RocksDBException {
		rockdisClient.compactRange();
		
	}


}
