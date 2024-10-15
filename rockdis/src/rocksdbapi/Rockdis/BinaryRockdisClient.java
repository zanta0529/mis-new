package rocksdbapi.Rockdis;

import rocksdbapi.Rockdis.Models.MemberScoreVO;
import rocksdbapi.Rockdis.RockdisProtocol.Command;
import rocksdbapi.Rockdis.RockdisProtocol.Keyword;
import org.rocksdb.CompressionType;
import org.rocksdb.RocksDBException;

import java.util.List;
import java.util.Map;

import static rocksdbapi.Rockdis.RockdisProtocol.Keyword.WITHSCORES;

public class BinaryRockdisClient extends RockdisConnection{


    // Deal With RocksDB init
    BinaryRockdisClient(){

    }
    BinaryRockdisClient(final String dbPath) throws RocksDBException {
        super(dbPath);
    }

    BinaryRockdisClient(final String dbPath, CompressionType compressionType) throws RocksDBException {
        super(dbPath, compressionType);
    }

    BinaryRockdisClient(final String dbPath, final String secondaryInstanceDirectory) throws RocksDBException {
        super(dbPath, secondaryInstanceDirectory);
    }

    public String set(byte[] key, byte[] value) throws RocksDBException {
        sendCommand(RockdisProtocol.Command.SET, key, value);

        return "SUCCEED";
    }

    public byte[] get(byte[] key) throws RocksDBException {

        return sendCommand(RockdisProtocol.Command.GET, key).getResult();
    }

    public byte[] hget(byte[] key, byte[] field) throws RocksDBException{

        return sendCommand(RockdisProtocol.Command.HGET, key, field).getResult();
    }

    public String hset(byte[] key, byte[] field, byte[] value) throws RocksDBException{
        sendCommand(RockdisProtocol.Command.HSET, key, field, value);
        return "SUCCEED";
    }

    public List<byte[]> hmget(byte[] key, byte[]... fields) throws RocksDBException{
        return sendCommand(RockdisProtocol.Command.HMGET, key, fields).getResultList();
    }

    public String hmset(byte[] key, Map<byte[], byte[]> hash) throws RocksDBException{
        sendCommand(RockdisProtocol.Command.HMSET, key, hash);
        return "SUCCEED";
    }

    public Integer hdel(byte[] key, byte[]... fields) throws RocksDBException{
        return sendCommand(RockdisProtocol.Command.HDEL, key, fields).getIntValue();
    }

    public Map<byte[], byte[]> hgetall(byte[] key) throws RocksDBException {
        return sendCommand(RockdisProtocol.Command.HGETALL, key).getFieldValueMap();

    }

    public List<byte[]> getKeys(byte[] key)throws RocksDBException{
        return sendCommand(RockdisProtocol.Command.KEYS, key).getResultList();
    }

    public Integer hincrBy(byte[] key, byte[] field, int value) throws RocksDBException {

        return sendCommand(RockdisProtocol.Command.HINCRBY, key, field, value).getIntValue();
    }

    public Integer delete(byte[] key) throws RocksDBException {

        return sendCommand(RockdisProtocol.Command.DELETE, key).getIntValue();
    }

    public Integer zadd(byte[] key, Integer score, byte[] member) throws RocksDBException {
        return sendZCommand(Command.ZADD, key, score, member).getIntValue();
    }

    public Integer zadd(List<MemberScoreVO> memberScoreVOList) throws RocksDBException {
        return sendZCommand(Command.ZADD, memberScoreVOList).getIntValue();
    }

    public List<byte[]> zrange(byte[] key, int start, int stop) throws RocksDBException {
        return sendZCommand(Command.ZRANGE, key, start, stop, Keyword.NORMAL).getResultList();
    }

    public List<byte[]> zrangeWithScores(byte[] key, int start, int stop) throws RocksDBException {
        return sendZCommand(Command.ZRANGE, key, start, stop, WITHSCORES).getResultList();
    }

    public List<byte[]> zrevrange(byte[] key, int start, int stop) throws RocksDBException {
        return sendZCommand(Command.ZREVRANGE, key, start, stop, Keyword.NORMAL).getResultList();
    }

    public List<byte[]> zrevrangeWithScores(byte[] key, int start, int stop) throws RocksDBException {
        return sendZCommand(Command.ZREVRANGE, key, start, stop, WITHSCORES).getResultList();
    }

    public Integer zcard(byte[] key) throws RocksDBException {
        return sendZCommand(Command.ZCARD, key).getMemberCount();
    }

    public List<byte[]> zrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException {
        return sendZCommand(Command.ZRANGEBYSCORE, key, rangeStart, rangeEnd, Keyword.NORMAL).getResultList();
    }

    public List<byte[]> zrevrangebyscore(byte[] key, Integer rangeStart, Integer rangeEnd) throws RocksDBException {
        return sendZCommand(Command.ZREVRANGEBYSCORE, key, rangeStart, rangeEnd, Keyword.NORMAL).getResultList();
    }

    @Deprecated
    public Integer zrem(byte[] key, byte[] member) throws RocksDBException {
        return sendZCommand(Command.ZREM, key, member).getIntValue();
    }

    public Integer zrem(byte[] key, byte[]... memberArray) throws RocksDBException {
        return sendZCommand(Command.ZREM, key, memberArray).getIntValue();
    }

    public Integer zremrangebyscore(byte[] key, int min, int max) throws RocksDBException {
        return sendZCommand(Command.ZREMRANGEBYSCORE, key, min, max).getIntValue();
    }

    public Integer zremrangebyrank(byte[] key, int start, int stop) throws RocksDBException {
        return sendZCommand(Command.ZREMRANGEBYRANK, key, start, stop).getIntValue();
    }
    /*
    這段程式碼應該用的到，如果要針對HashMap進行 Key sorted的時候就需要了
     */

/*
    public List<byte[]> zrange(byte[] key) throws RocksDBException {
        Map<byte[], byte[]> resultMap = sendZCommand(Command.ZRANGE, key).getFieldValueMap();
        TreeMap<byte[], byte[]> sortedresultMap = new TreeMap<>(new Comparator<byte[]>() {
            @Override
            public int compare(byte[] o1, byte[] o2) {
                for (int i = 0, j = 0; i < o1.length && j < o2.length; i++, j++) {
                    int a = (o1[i] & 0xff);
                    int b = (o2[j] & 0xff);
                    if (a != b) {
                        return a - b;
                    }
                }
                return o1.length - o2.length;
            }
        });
        sortedresultMap.putAll(resultMap);
        List<byte[]> sortedList = new ArrayList<>();
        for (Map.Entry<byte[],byte[]> entry : sortedresultMap.entrySet()) {
            sortedList.add(entry.getValue());
        }
        return sortedList;
    }
*/
}
