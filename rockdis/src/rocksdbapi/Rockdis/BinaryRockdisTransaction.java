package rocksdbapi.Rockdis;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdbapi.Rockdis.Models.MemberScoreVO;
import rocksdbapi.Rockdis.Params.RockdisParams;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class BinaryRockdisTransaction implements BinaryRockdisTransactionCommands, BinaryRockdisCommands{
    
    private static Logger logger = LoggerFactory.getLogger(BinaryRockdisTransaction.class);
    protected BinaryRockdisTransactionClient rockdisClient;

    public BinaryRockdisTransaction(final String dbPath) throws RocksDBException {
        logger.debug("Use Transaction Instance mode.");
        rockdisClient = new BinaryRockdisTransactionClient(dbPath);
    }

    @Override
    public void multi() throws RocksDBException {
        rockdisClient.multi();
    }

    @Override
    public void exec() throws RocksDBException {
        rockdisClient.exec();
    }

    @Override
    public void rollback() throws RocksDBException {
        rockdisClient.rollback();
    }

    @Override
    public String set(byte[] key, byte[] value) throws RocksDBException {
        return new String(rockdisClient.set(key, value));
    }

    @Override
    public byte[] get(byte[] key) throws RocksDBException {
        return rockdisClient.get(key);
    }

    @Override
    public byte[] getForUpdate(byte[] key) throws RocksDBException {
        return rockdisClient.getForUpdate(key);
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
        return rockdisClient.hgetAll(key);
    }

    @Override
    public List<byte[]> getKeys(byte[] key) throws RocksDBException {
        return rockdisClient.getKeys(key);
    }

    @Override
    public Integer hincrBy(byte[] key, byte[] field, int value) throws RocksDBException {
        return rockdisClient.hincrBy(key, field, value);
    }

    //TODO This method should be refactored! It's belong to DB operation.
    @Override
    public void tryCatchUpWithPrimary() throws RocksDBException {

    }

    //TODO This method should be refactored! It's belong to DB operation.
    @Override
    public void close() throws RocksDBException {
        rockdisClient.close();

    }

    @Override
    public boolean isClosed() {
        return !rockdisClient.transactionDB.isOwningHandle();
    }

    @Override
    public RocksDB getDB() {
        return rockdisClient.transactionDB;
    }

    @Override
    public AtomicLong getIteratorCount() {
        return rockdisClient.getIteratorCount();
    }

    /**
     * If delete key action is succeed, then return true. Otherwise return false.
     * @param key
     * @return boolean true or false
     * @throws RocksDBException
     */
    @Override
    public Integer delete(byte[] key) throws RocksDBException {
        return rockdisClient.delete(key);
    }

    @Override
    public Integer zadd(byte[] key, Integer score, byte[] member) throws RocksDBException {
        return rockdisClient.zadd(key, score, member);
    }

    @Override
    public Integer zadd(List<MemberScoreVO> memberScoreVOList) throws RocksDBException {
        return rockdisClient.zadd(memberScoreVOList);
    }

    @Override
    public List<byte[]> zrange(byte[] key, int start, int end) throws RocksDBException {
        return rockdisClient.zrange(key, start, end);
    }

    @Override
    public List<byte[]> zrangeWithScores(byte[] key, int start, int stop) throws RocksDBException {
        return rockdisClient.zrangeWithScores(key, start, stop);
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
        return null;
    }

    @Override
    public List<byte[]> zrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException {
        return null;
    }

    @Override
    public List<byte[]> zrevrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException {
        return null;
    }

    @Override
    public Integer zrem(byte[] key, byte[] member) throws RocksDBException {
        return rockdisClient.zrem(key, member);
    }

    @Override
    public Integer zrem(byte[] key, byte[]... members) throws RocksDBException {
        return rockdisClient.zrem(key, members);
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
