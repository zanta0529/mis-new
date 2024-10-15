package rocksdbapi.RedisInterface;

import rocksdbapi.Rockdis.Rockdis;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RedisZaddTest {
    
    private static Logger logger = LoggerFactory.getLogger(RedisZaddTest.class);
    
    
    public static void main(String[] args) throws RocksDBException {

        Rockdis rockdis = new Rockdis("/tmp/zadd-test");
        Options options = new Options();

        String key = "books";
        double book_one_score = 8;
        String book_one_name = "java";

        double book_two_score = 19;
        String book_two_name = "python";

        double book_three_score = 7.6;
        String book_three_name = "C++";

        rockdis.hset(key, Double.toString(book_one_score), book_one_name);
        rockdis.hset(key, Double.toString(book_two_score), book_two_name);
        rockdis.hset(key, Double.toString(book_three_score), book_three_name);

        for( Map.Entry<String, String> map : rockdis.hgetAll("books").entrySet()) {
            logger.info("Score : {}", map.getKey());
            logger.info("Key : {}", String.valueOf(map.getValue()));
        }

        String key2 = "book2";
        final Map<String, String> insertMap = new HashMap<String, String>();
        insertMap.put(Double.toString(book_one_score), book_one_name);
        insertMap.put(Double.toString(book_two_score), book_two_name);
        insertMap.put(Double.toString(book_three_score), book_three_name);

        rockdis.hmset(key2, insertMap);
        for( Map.Entry<String, String> map : rockdis.hgetAll("book2").entrySet()) {
            logger.info("Score : {}", map.getKey());
            logger.info("Key : {}", String.valueOf(map.getValue()));
        }

        /*
        使用補0方法可以做到嗎
         */
        String fullfilZero = "fullfilZero";
        String zbook_one_score = "/0000081";
        String zbook_one_name = "java";

        String zbook_two_score = "/0000007";
        String zbook_two_name = "python";

        String zbook_three_score = "/0001009";
        String zbook_three_name = "c++";
        rockdis.hset(fullfilZero, zbook_one_score, zbook_one_name);
        rockdis.hset(fullfilZero, zbook_two_score, zbook_two_name);
        rockdis.hset(fullfilZero, zbook_three_score, zbook_three_name);
        rockdis.hset(fullfilZero, "-0000001", zbook_one_name);


        for( Map.Entry<String, String> map : rockdis.hgetAll("fullfilZero").entrySet()) {
            logger.info("Score : {}", map.getKey());
            logger.info("Value : {}", String.valueOf(map.getValue()));
        }

        int padZero = 5;
        int padZero2 = 5000;

        String padZeroStr = String.format("%010d", padZero);
        String padZero2Str = String.format("%010d", Integer.MAX_VALUE);

        System.out.println(padZeroStr);
        System.out.println(padZero2Str);

//        /*
//        Integer 轉成字串
//         */
//        String mathKey = "math";
//        int scorePositive = 5;
//        int scoreNegative = -1;
//        String positive = "positive";
//        String negative = "negative";
//
//        rockdis.hset(mathKey.getBytes(), negative.getBytes(), "+1".getBytes());
//        rockdis.hincrBy(mathKey, negative, scoreNegative);
    }
}
