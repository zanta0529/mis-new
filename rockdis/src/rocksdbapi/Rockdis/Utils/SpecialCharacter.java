package rocksdbapi.Rockdis.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author kkw
 * @project rocksdb
 * @date 2020/12/23
 */

public class SpecialCharacter {

    static String specialCharacter = "*?[]";
    static byte[] starByteArray = "*".getBytes();
    static byte[] questionMarkByteArray = "?".getBytes();

    private static Logger logger = LoggerFactory.getLogger(SpecialCharacter.class);

    public static boolean shouldFullyScan(byte[] byteArray){
        //Process Key, Determine full scan or Not to full scan
        // If first character of key is * or ? , then DB should Full table scan.
        // Set isFullyScan = true
        byte[] firstByteArrayOfKey = Arrays.copyOfRange(byteArray, 0, 1);
        return Arrays.equals(firstByteArrayOfKey, starByteArray) ||
                Arrays.equals(firstByteArrayOfKey, questionMarkByteArray);
    }

    public static byte[] getSearchPrefix(byte[] byteArray) {
        // search prefix is "" means each key should determine equals key.
        // If key not start with * or ? then this method start to process each key.
        // traverse the each character of key when encounter special character which are *?[]
        // If encounter special character then set search prefix before special character.
        byte[] searchPrefix = null;

        if ( shouldFullyScan(byteArray)){
            return "".getBytes();
        }

        for (int i = 0; i < byteArray.length; i++) {
            String eachString = new String(Arrays.copyOfRange(byteArray, i, i + 1));
            logger.debug("Each String is : {}", eachString);
            if (specialCharacter.contains(eachString)) {
                // Find special character
                searchPrefix = Arrays.copyOfRange(byteArray, 0, i);
                break;
            }
        }

        if (searchPrefix == null) {
            return byteArray;
        }

        return searchPrefix;
    }

    public static String MappingNormalStringToJavaRegex(byte[] byteArray){
        // Process special character: *
        //  * is 42  //  ^ is 94
        //  ] is 93  //  \ is 92
        //  [ is 91  //  ? is 63
        String searchFullString = "";
        for (int i = 0; i < byteArray.length; i++) {
            byte[] currentByte = Arrays.copyOfRange(byteArray, i, i + 1);
            if ( (byteArray[i] == 42 || byteArray[i] ==63) && i>=1 && byteArray[i-1] == 92){
                // 那就是 \\* 要當做正常字串
                searchFullString = searchFullString.concat(new String(currentByte));
            } else if ((byteArray[i] == 42 || byteArray[i] ==63) && i>=1 &&byteArray[i-1] != 92){
                // 前面要加上 .
                searchFullString = searchFullString.concat(".");
                searchFullString = searchFullString.concat(new String(currentByte));
            } else if ((byteArray[i] == 42 || byteArray[i] ==63) && i==0){
                searchFullString = searchFullString.concat(".");
                searchFullString = searchFullString.concat(new String(currentByte));
            }else if (byteArray[i] == 63) {
                searchFullString = searchFullString.concat(".");
                searchFullString = searchFullString.concat(new String(currentByte));
            } else {
                searchFullString = searchFullString.concat(new String(currentByte));
            }
        }
        return searchFullString;
    }

















}
