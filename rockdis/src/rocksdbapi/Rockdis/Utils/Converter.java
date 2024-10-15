package rocksdbapi.Rockdis.Utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class Converter {

    public static Logger logger = LoggerFactory.getLogger(Converter.class);

    public static byte[][] stringArrayToByteArrays(String... stringArray){

        logger.debug(" Trigger Converter : string Array to byte arrays.");
        byte[][] byteArrays = new byte[stringArray.length][];

        for (int i = 0; i < stringArray.length; i++) {

            logger.debug(" The string be converted is {}", stringArray[i]);
            byteArrays[i] = stringArray[i].getBytes();
        }

        return byteArrays;
    }

    public static List<String> byteArrayListToStringList(List<byte[]> byteArrayList){
        List<String> stringList = new ArrayList<>();

        for (byte[] bytes : byteArrayList) {
            if (bytes == null){
                break;
            }
            stringList.add(new String(bytes));
        }
        return stringList;
    }

    public static Map<String, String> byteMapToStringMap(Map<byte[], byte[]> byteMap){
        Map<String, String> stringMap = new ConcurrentSkipListMap<>();
        for (Map.Entry<byte[], byte[]> byteMapEntry : byteMap.entrySet()){
            stringMap.put(new String(byteMapEntry.getKey()), new String(byteMapEntry.getValue()));
        }
        return stringMap;
    }

    public static Map<byte[], byte[]> stringMapToByteArrayMap(Map<String, String> stringMap){
        Map<byte[], byte[]> byteArrayMap = new HashMap<>();
        for (Map.Entry<String, String> stringEntry : stringMap.entrySet()){
            byteArrayMap.put( stringEntry.getKey().getBytes(), stringEntry.getValue().getBytes());
        }
        return byteArrayMap;
    }

    public static List<byte[]> byteKeyHashSetToByteArrayList(ByteKeyHashSet finalByteKeyHashSet){
        // Convert ByteBuffer to byte[]
        logger.debug(" *** ByteKeyHashSet ======================");
        logger.debug("ByteKeyHashSet size : {}", finalByteKeyHashSet.size());
        List<byte[]> byteArrayList = new LinkedList<>();
        Iterator<ByteBuffer> byteKeyHashSetIterator = finalByteKeyHashSet.iterator();
        while(byteKeyHashSetIterator.hasNext()){
            ByteBuffer tmpByteBuffer = byteKeyHashSetIterator.next();
            byte[] byteArray = new byte[tmpByteBuffer.remaining()];
            tmpByteBuffer.get(byteArray, 0, byteArray.length);
            byteArrayList.add(byteArray);
            logger.debug(new String(byteArray));
        }
        return byteArrayList;
    }
}
