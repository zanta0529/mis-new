package redisapi.Util;

import rocksdbapi.Rockdis.Models.MemberScoreVO;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation.intToByteArray;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * <p> Since: 2023/12/14 </p>
 *
 * @author kkw
 * @version 1.0
 */
public class CollectionUtil {

    /**
     * 用來將 Map轉換為List，按照 Map的Value轉換成 Integer進行排序
     * 若 map 原先儲存   map.put("one", "1");
     *                 map.put("nine", "9");
     *                 map.put("two", "2");
     *
     * 則轉換後的List為 ["one", "1", "two", "2", "nine", "9"]
     * @param map
     * @return
     */
    public static List<byte[]> convertMapToListOrderByValue(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().equals("rockscommand"))
                .sorted(Comparator.comparingInt(entry -> Integer.parseInt(entry.getValue())))
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .map(str -> str.getBytes(StandardCharsets.UTF_8))
                .collect(Collectors.toList());
    }

    public static List<MemberScoreVO> convertMapToList(String key, Map<String, String> map) {
        List<MemberScoreVO> memberScoreVOList = new LinkedList<>();
        for (Map.Entry entry : map.entrySet()) {
            memberScoreVOList.add(new MemberScoreVO(key.getBytes(),
                                                    Integer.parseInt(entry.getValue().toString()),
                                                    entry.getKey().toString().getBytes()));
        }
        return memberScoreVOList;
    }

    /**
     * 傳入一個 Key與 Value都是String 的 Map，將其轉換成 Key與 Value都是 byte[] 的 Map。
     * 其中Value必須透過透書處理，因為Value儲存的時候是整數因此不可以直接透過 getBytes()轉換。
     * @param stringMap
     * @return
     */
    public static Map<byte[], byte[]> convertStringMapToByteArrayMap(Map<String, String> stringMap) {
        Map<byte[], byte[]> byteArrayMap = new HashMap<>();
        if(stringMap!=null) if(!stringMap.isEmpty())
        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            if (entry.getKey().equals("rockscommand")) {
                continue;
            }
            byteArrayMap.put(entry.getKey().getBytes(), intToByteArray(Integer.parseInt(entry.getValue())));
        }
        return byteArrayMap;
    }

//    public static List<String> filterList(List<String> originalList, int start, int end) {
//        List<String> resultList = new ArrayList<>();
//
//        for (int i = 0; i < originalList.size(); i += 2) {
//            // 檢查奇數元素是否介於 start 與 end 之間
//            if (isInRange(originalList.get(i), start, end)) {
//                resultList.add(originalList.get(i));
//                resultList.add(originalList.get(i + 1));
//            }
//        }
//
//        return resultList;
//    }
//
//    private static boolean isInRange(String numberString, int start, int end) {
//        try {
//            int number = Integer.parseInt(numberString);
//            return number >= start && number <= end;
//        } catch (NumberFormatException e) {
//            // 處理字串轉換成整數的例外
//            return false;
//        }
//    }
}
