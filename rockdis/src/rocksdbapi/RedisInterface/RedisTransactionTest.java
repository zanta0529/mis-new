package rocksdbapi.RedisInterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class RedisTransactionTest {

    private static Logger logger = LoggerFactory.getLogger(RedisTransactionTest.class);

    public static void main(String[] args) throws InterruptedException {
        Jedis jedis = new Jedis("127.0.0.1");

        logger.debug("Set key:hello, value:world  into redis");
        jedis.set("hello", "world");

        logger.debug("Get key:hello, value is : {}", jedis.get("hello"));
        logger.debug("Start Use MULTI method");

        Transaction t = jedis.multi();
        logger.debug("Now use transaction to insert key:hello1,value:world1");
        t.set("hello1", "world1");
        logger.debug("Use transaction object to get key:hello1, value is : {}", t.get("hello1"));
//        logger.debug("Now use non-transaction object to get key:hello1, value is : {}", jedis.get("hello1"));

        logger.debug("Now use exec() method to commit the change.");
        t.exec();
        logger.debug("Use transaction object to get key:hello1, value is : {}", t.get("hello1"));
        logger.debug("Now use non-transaction object to get key:hello1, value is : {}", jedis.get("hello1"));





//        System.out.println("======");
//        System.out.println(jedis.get("data_value"));
//        Transaction transaction = jedis.multi();
//        Response<Long> result = transaction.incr("data_value");
//
//        //雖然在transaction 執行完incr後，實際上不會真的去執行動作
//        //因此result.get 會顯示  Response XXXX
//        //直到Client執行了 exec()後才會真的處理動作
//        System.out.println(result);
//        Response<Long> result2 = transaction.incr("data_value");
//        //Thread.sleep(10000);
//
//        System.out.println("======");
//        /*
//        exec() 執行後 會記錄所有的變更
//         */
//        List<Object> response = transaction.exec();
//
//        for (Object value : response) {
//            System.out.println(value);
//        }
//
//        System.out.println("=====");
//        System.out.println(result.get());



//        jedis.set("key1", "value1");
//        System.out.println(jedis.get("key1"));
//        Transaction transaction = jedis.multi();
//        transaction.set("key1","value2");
//        System.out.println(transaction.get("key1"));
//        System.out.println(jedis.get("key1"));
//        transaction.exec();
//        System.out.println(transaction.get("key1"));
//
//        System.out.println(jedis.get("key1"));

    }
}
