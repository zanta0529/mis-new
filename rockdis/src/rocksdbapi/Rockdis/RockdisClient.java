package rocksdbapi.Rockdis;

import rocksdbapi.Rockdis.Utils.Converter;
import org.rocksdb.CompressionType;
import org.rocksdb.RocksDBException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class RockdisClient extends BinaryRockdisClient implements Commands{


    // Deal With RocksDB init
    RockdisClient(){

    }

    RockdisClient(final String dbPath) throws RocksDBException {
        super(dbPath);
    }

    RockdisClient(final String dbPath, CompressionType compressionType) throws RocksDBException {
        super(dbPath, compressionType);
    }

    public RockdisClient(final String dbPath, final String secondaryInstanceDirectory) throws RocksDBException {
        super(dbPath, secondaryInstanceDirectory);
    }


    @Override
    public String set(String key, String value) throws RocksDBException {

        return set(key.getBytes(), value.getBytes());
    }

    @Override
    public String get(String key) throws RocksDBException {

        return isByteArrayNull(get(key.getBytes()));
    }

    @Override
    public String hget(String key, String field) throws RocksDBException {

        return isByteArrayNull(hget(key.getBytes(), field.getBytes()));
    }

    @Override
    public String hset(String key, String field, String value) throws RocksDBException {

        return hset(key.getBytes(), field.getBytes(), value.getBytes());
    }

    @Override
    public List<String> hmget(String key, String... fields) throws RocksDBException {
        byte[][] byteArrays = Converter.stringArrayToByteArrays(fields);
        List<byte[]> hmgetByteListArray = hmget(key.getBytes(), byteArrays);

        return Converter.byteArrayListToStringList(hmgetByteListArray);
    }

    @Override
    public String hmset(String key, Map<String, String> hash) throws RocksDBException {

        return hmset(key.getBytes(), Converter.stringMapToByteArrayMap(hash));
    }

    @Override
    public Integer hdel(String key, String... fields) throws RocksDBException {
        byte[][] byteArrays = Converter.stringArrayToByteArrays(fields);
        return hdel(key.getBytes(), byteArrays);
    }

    @Override
    public Integer zrem(String key, String... fields) throws RocksDBException {
        byte[][] byteArrays = Converter.stringArrayToByteArrays(fields);
        return zrem(key.getBytes(), byteArrays);
    }

    @Override
    public Map<String, String> hgetAll(String key) throws RocksDBException {
        Map<byte[], byte[]> byteArrayMap = hgetall(key.getBytes());

        return Converter.byteMapToStringMap(byteArrayMap);
    }

    @Override
    public List<String> getKeys(String key) throws RocksDBException {
        List<byte[]> listArray = getKeys(key.getBytes());
        return Converter.byteArrayListToStringList(listArray);
    }

    @Override
    public Integer hincrBy(String key, String field, int value) throws RocksDBException {
        return hincrBy(key.getBytes(), field.getBytes(), value);
    }

    @Override
    public Integer delete(String key) throws RocksDBException {
        return delete(key.getBytes());
    }

    @Override
    public Integer zadd(String key, Integer score, String member) throws RocksDBException {
        return zadd(key.getBytes(), score, member.getBytes());
    }

    @Override
    public List<String> zrange(String key, int start, int end) throws RocksDBException {
        List<byte[]> resultList = zrange(key.getBytes(), start, end);
        List<String> resultString = new ArrayList<>();
        for (byte[] element : resultList) {
            resultString.add(new String(element));
        }
        return resultString;
    }

    @Override
    public List<String> zrevrange(String key, int start, int stop) throws RocksDBException {
        List<byte[]> resultList = zrevrange(key.getBytes(), start, stop);
        List<String> resultString = new ArrayList<>();
        for (byte[] element : resultList) {
            resultString.add(new String(element));
        }
        return resultString;
    }

    @Override
    public Integer zrem(String key, String member) throws RocksDBException {
        return zrem(key.getBytes(), member.getBytes());
    }

    private String isByteArrayNull(byte[] byteArray){

        if ( byteArray == null ){
            return null;
        } else {
            return new String(byteArray);
        }
    }

	@Override
	public long[] insertIntoDB(ConcurrentSkipListMap<String, Object> patchIntoDbMap) throws RocksDBException {
		return writeBatchIntoDB(patchIntoDbMap);
		
	}
}
