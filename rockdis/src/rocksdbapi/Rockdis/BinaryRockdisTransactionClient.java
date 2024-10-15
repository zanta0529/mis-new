package rocksdbapi.Rockdis;

import rocksdbapi.Rockdis.Models.MemberScoreVO;
import rocksdbapi.Rockdis.Models.RockdisResultObject;
import rocksdbapi.Rockdis.RockdisProtocol.Command;
import rocksdbapi.Rockdis.RockdisProtocol.Keyword;
import org.rocksdb.RocksDBException;

import java.util.List;
import java.util.Map;

public class BinaryRockdisTransactionClient extends RockdisConnection {

    BinaryRockdisTransactionClient(){

    }

    BinaryRockdisTransactionClient(final String dbPath) throws RocksDBException {
        super(dbPath, true);
    }

    public void multi() throws RocksDBException {
        transaction = transactionDB.beginTransaction(writeOptions);
    }

    public void exec() throws RocksDBException {
        transaction.commit();
    }

    public void close() throws RocksDBException {

        if ( transaction != null ){
            transaction.close();
        }
        transactionDB.closeE();
    }

    public void rollback() throws RocksDBException {

    }

    public byte[] set(byte[] key, byte[] value) throws RocksDBException {
        RockdisResultObject resultObject = sendTransactionCommand(Command.SET, enableTransaction, key, value);
        if ( resultObject == null) {
            return "FAILED".getBytes();
        }
        return "SUCCEED".getBytes();
    }

    public byte[] getForUpdate(byte[] key) throws RocksDBException {
        RockdisResultObject resultObject = sendTransactionCommand(Command.GETFORUPDATE, enableTransaction, key);
        if ( resultObject == null ) {
            return "FAILED".getBytes();
        }
        return resultObject.getResult();
    }

    public byte[] get(byte[] key) throws RocksDBException {
        RockdisResultObject resultObject = sendTransactionCommand(Command.GET, enableTransaction, key);
        if ( resultObject == null ) {
            return "FAILED".getBytes();
        }
        return resultObject.getResult();
    }

    public byte[] hget(byte[] key, byte[] field) throws RocksDBException{
        return sendTransactionCommand(Command.HGET, enableTransaction, key, field).getResult();
    }

    public List<byte[]> hmget(byte[] key, byte[]... fields) throws RocksDBException {
        return sendTransactionCommand(Command.HMGET, enableTransaction, key, fields).getResultList();
    }

    public Map<byte[], byte[]> hgetAll(byte[] key) throws RocksDBException {
        return sendTransactionCommand(Command.HGETALL, enableTransaction, key).getFieldValueMap();
    }

    public List<byte[]> getKeys(byte[] key) throws RocksDBException {
        return sendTransactionCommand(Command.KEYS, enableTransaction, key).getResultList();
    }

    //TODO WTF is Failed and succeed string/byte[] show at this moment?  Are you fucking sure this is OK?
    public byte[] hset(byte[] key, byte[] field, byte[] value) throws RocksDBException {
        RockdisResultObject resultObject = sendTransactionCommand(Command.HSET, enableTransaction, key, field, value);
        if ( resultObject == null ) {
            return "FAILED".getBytes();
        }
        return "SUCCEED".getBytes();
    }

    public byte[] hmset(byte[] key, Map<byte[], byte[]> hash) throws RocksDBException {
        RockdisResultObject resultObject = sendTransactionCommand(Command.HMSET, enableTransaction, key, hash);
        if ( resultObject == null ) {
            return "FAILED".getBytes();
        }
        return "SUCCEED".getBytes();
    }

    public Integer hincrBy(byte[] key, byte[] field, int value) throws RocksDBException {
        return sendTransactionCommand(Command.HINCRBY, enableTransaction, key, field, value).getIntValue();
    }

    public Integer delete(byte[] key) throws RocksDBException {
        return sendTransactionCommand(Command.DELETE, enableTransaction, key).getIntValue();
    }

    public Integer zadd(byte[] key, Integer score, byte[] member) throws RocksDBException {
        return sendZCommand(Command.ZADD, key, score, member, true).getIntValue();
    }

    public Integer zadd(List<MemberScoreVO> memberScoreVOList) throws RocksDBException {
        return sendZCommand(Command.ZADD, memberScoreVOList, true).getIntValue();
    }

    public List<byte[]> zrange(byte[] key, int start, int end) throws RocksDBException {
        return sendZCommand(Command.ZRANGE, key, start, end, Keyword.NORMAL, true).getResultList();
    }

    public List<byte[]> zrangeWithScores(byte[] key, int start, int end) throws RocksDBException {
        return sendZCommand(Command.ZRANGE, key, start, end, Keyword.WITHSCORES, true).getResultList();
    }

    public List<byte[]> zrevrange(byte[] key, int start, int stop) throws RocksDBException {
        return sendZCommand(Command.ZREVRANGE, key, start, stop, Keyword.NORMAL, true).getResultList();
    }

    public List<byte[]> zrevrangeWithScores(byte[] key, int start, int stop) throws RocksDBException {
        return sendZCommand(Command.ZREVRANGE, key, start, stop, Keyword.WITHSCORES, true).getResultList();
    }

    public Integer zcard(byte[] key) throws RocksDBException {
        return null;
    }

    public Integer zrem(byte[] key, byte[] member) throws RocksDBException {
        return sendZCommand(Command.ZREM, key, member, true).getIntValue();
    }

    public Integer zrem(byte[] key, byte[]... memberArray) throws RocksDBException {
        return sendZCommand(Command.ZREM, true, key, memberArray).getIntValue();
    }

    public Integer zremrangebyscore(byte[] key, int min, int max) throws RocksDBException{
        return sendZCommand(Command.ZREMRANGEBYSCORE, key, min, max).getIntValue();
    }

    public Integer zremrangebyrank(byte[] key, int start, int stop) throws RocksDBException{
        return sendZCommand(Command.ZREMRANGEBYRANK, key, start, stop).getIntValue();
    }
}
