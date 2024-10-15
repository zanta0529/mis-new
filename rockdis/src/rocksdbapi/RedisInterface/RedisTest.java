package rocksdbapi.RedisInterface;

import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisTest {

    public static final byte ASTERISK_BYTE = '*';
    public static final byte DOLLAR_BYTE = '$';


    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        // 由高位到低位
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (bytes[i] & 0x000000FF) << shift;// 往高位游
        }
        return value;
    }




    public static void main(String[] args) {


        String value = "12345678987654321";
        byte[] valueByteArray = value.getBytes();
        int arrayLength = valueByteArray.length;

        byte[] resultArray = new byte[arrayLength + 1];


        resultArray[0] = ASTERISK_BYTE;
        //System.arraycopy(ASTERISK_BYTE, 0, resultArray, 0, 1);
        System.arraycopy(valueByteArray, 0, resultArray, 1, arrayLength);
        System.out.println(new String(resultArray));


        // Has filed and value
        String field2 = "112233445566778899";
        String value2 = "gg88gg88gg88gg88";
        byte[] filedByteArray = field2.getBytes();
        byte[] value2ByteArray = value2.getBytes();

        int filedByteArrayLength = filedByteArray.length;
        int value2ByteArrayLength = value2ByteArray.length;
        byte[] resultArray2 = new byte[1 + 4 + filedByteArrayLength + value2ByteArrayLength];
        resultArray2[0] = DOLLAR_BYTE;
        System.out.println(filedByteArrayLength);
        System.arraycopy(intToByteArray(filedByteArrayLength), 0, resultArray2, 1, intToByteArray(filedByteArrayLength).length);
        System.arraycopy(filedByteArray, 0, resultArray2, 5, filedByteArrayLength);
        System.arraycopy(value2ByteArray, 0, resultArray2, 5+filedByteArrayLength, value2ByteArrayLength);
        System.out.println(new String(resultArray2));


        byte[] getInt = Arrays.copyOfRange(resultArray2, 1, 5);
        int offset = byteArrayToInt(getInt);
        System.out.println(offset);

        byte[] returnField = Arrays.copyOfRange(resultArray2, 1+4, 1+4+offset);
        System.out.println(new String(returnField));

//
//        Jedis jedis = new Jedis();
//        jedis.set("events/city/rome", "127.0.0.1");
//        String cacheResponse = jedis.get("events/city/rome");
//
//        String result = jedis.get("mykey");
//        System.out.println(result);
//
//        // 使用 hset與 hget
//        jedis.hset("user#1", "name", "Peter");
//        jedis.hset("user#1", "job", "politician");
//
//        String name = jedis.hget("user#1", "name");
//        System.out.println("Name is : " + name);
//        String job = jedis.hget("user#1", "job");
//        System.out.println("Job is : " + job);
//
//        List<String> userValues = jedis.hmget("user#1", "name", "job" );
//        for ( String value : userValues){
//            System.out.println(" Fields' value : " + value);
//        }
//
//
//        // 使用 hmset與 hmget
//        Map<String, String> user2 = new HashMap<>();
//        user2.put("name","John");
//        user2.put("job","teacher");
//        jedis.hmset("user#2", user2 );
//        List<String> user2Values = jedis.hmget("user#2", "name", "job" );
//        for ( String value : user2Values){
//            System.out.println(" Fields' value : " + value);
//        }
//
//
//        // 使用 hgetAll
//        Map<String, String> fields = jedis.hgetAll("user#1");
//
//        for ( Map.Entry<String, String> field : fields.entrySet()){
//            System.out.println("Key : " + field.getKey() + " , value : " + field.getValue());
//        }
//
//        // 使用 hincrby
//        jedis.hincrBy("user#1", "point", 1);

    }
}
