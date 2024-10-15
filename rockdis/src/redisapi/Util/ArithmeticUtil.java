package redisapi.Util;

import rocksdbapi.Rockdis.RockdisProtocol;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;

import java.util.*;
import java.util.regex.Pattern;

import static rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation.byteArrayToInt;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static com.github.tonivade.resp.protocol.RedisToken.string;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/2
 */

public class ArithmeticUtil {

    public static Integer SafeStringToInt(SafeString safeString){
        return Integer.parseInt(safeString.toString());
    }

    public static Double SafeStringToDouble(SafeString safeString) {
        return Double.parseDouble(safeString.toString());
    }

    public static List<RedisToken> stringListToRedisTokenList(List<String> inputList){
        List<RedisToken> returnList = new ArrayList<>();

        for (int i = 0; i < inputList.size(); i++) {
            returnList.add(string(inputList.get(i).toString()));
        }
        return returnList;
    }

    public static List<RedisToken> byteArrayListToRedisTokenList(List<byte[]> inputList) {
        List<RedisToken> returnList = new ArrayList<>();

        for (int i = 0; i < inputList.size(); i++) {
            returnList.add(string(new String(inputList.get(i))));
        }
        return returnList;
    }

    public static List<RedisToken> primitiveMapToRedisTokenList(Map<String, String> inputMap) {
        List<RedisToken> returnList = new ArrayList<>();
        for (Map.Entry entry : inputMap.entrySet()) {
            returnList.add(string(entry.getKey().toString()));
            returnList.add(string(entry.getValue().toString()));
        }
        return returnList;
    }

    public static boolean isParameterEven(int requestLength) {
        return (requestLength - 2) % 2 == 0;
    }

    public static boolean canCastStringToInt(String input) {

//        System.out.println("input : " + input);

        try {
            return Double.parseDouble(input) % 1 == 0;

        } catch (NumberFormatException exception) {
            return false;
        }
    }

    public static boolean isParameterPositiveNumber(String input) {
        return Integer.parseInt(input) >= 0;
    }

    public static boolean isParameterPositiveNumber(Double number) {
        return number >= 0;
    }

    public static boolean isParameterPositiveInteger(String input) {
        String regex = "\\d+"; // 正則表達式，檢查是否為數字
        return Pattern.matches(regex, input) && !input.contains(".") && Integer.parseInt(input) > 0;
    }

    public static int roundDoubleNumberToInt(Double number) {
        return (int) Math.round(number);
    }

    /**
     * This method is for zrange and zrevrange command.
     * Before execute command we have to check the parameter is valid.
     * zrange and zrevrange can take two type, which is 'COMMAND START STOP'
     * and 'COMMAND START STOP WITHSCORES'. So this method first check START
     * and STOP this two parameter should be integer. If user provide fourth
     * parameter, then  check WITHSCORES parameter should match the 'withscores'
     * string.
     * @param request
     * @return RedisToken, Null
     */
    public static RedisToken checkParameter(Request request) {

        int parameterSize = request.getLength();
        SafeString key = request.getParam(1);
        SafeString start = request.getParam(2);
        SafeString stop = request.getParam(3);

        if (!canCastStringToInt(start.toString()) || !canCastStringToInt(stop.toString())) {
            return error("ERR value is not an integer or out of range");
        }

        switch (parameterSize) {
            case 4:

                break;
            case 5:
                String WITHSCORES = request.getParam(4).toString().toLowerCase();
                if (!Arrays.equals(WITHSCORES.getBytes(), RockdisProtocol.Keyword.WITHSCORES.raw)) {
                    return error("ERR syntax error, Maybe you want to type WITHSCORES");
                }

                break;
            default:
                return error("ERR syntax error, parameter over 5.");
        }
        return null;
    }

    public static RedisToken checkByScoreParameter(Request request) {

        int parameterSize = request.getLength();
        SafeString key = request.getParam(1);
        SafeString start = request.getParam(2);
        SafeString stop = request.getParam(3);

        if (!canCastStringToInt(start.toString()) || !canCastStringToInt(stop.toString())) {
            return error("ERR value is not an integer or out of range");
        }
        switch (parameterSize) {
            case 4:

                break;
            case 5:
                String WITHSCORES = request.getParam(4).toString().toLowerCase();
                if (!Arrays.equals(WITHSCORES.getBytes(), RockdisProtocol.Keyword.WITHSCORES.raw)) {
                    return error("ERR syntax error, Maybe you want to type WITHSCORES");
                }
                break;
            case 7:
                String LIMIT = request.getParam(4).toString().toLowerCase();
                if (!Arrays.equals(LIMIT.getBytes(), RockdisProtocol.Keyword.LIMIT.raw)) {
                    return error("ERR syntax error, Maybe you want to type LIMIT");
                }
                break;
            case 8:
            	WITHSCORES = request.getParam(4).toString().toLowerCase();
            	if(Arrays.equals(WITHSCORES.getBytes(), RockdisProtocol.Keyword.WITHSCORES.raw)) {
            		LIMIT = request.getParam(5).toString().toLowerCase();
            		if(!Arrays.equals(LIMIT.getBytes(), RockdisProtocol.Keyword.LIMIT.raw)) {
            			return error("ERR syntax error, Maybe you want to type LIMIT");
            		}
            	} else {
                	LIMIT = request.getParam(4).toString().toLowerCase();
                    if (Arrays.equals(LIMIT.getBytes(), RockdisProtocol.Keyword.LIMIT.raw)) {
                    	WITHSCORES = request.getParam(7).toString().toLowerCase();
                        if (!Arrays.equals(WITHSCORES.getBytes(), RockdisProtocol.Keyword.WITHSCORES.raw)) {
                            return error("ERR syntax error, Maybe you want to type WITHSCORES");
                        }
                    }
                    else return error("ERR syntax error, Maybe you want to type LIMIT or WITHSCORES");             		
            	} 

                break;
            default:
                return error("ERR syntax error, parameter over 7.");
        }
        return null;
    }    
    
    
    /**
     * This method is for zrange and zrevrange which use 'WITHSCORES' to get member-score list.
     * When user use 'ZADD KEY SCORE MEMBER', we store SCORE by {@link rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation#intToByteArray(int)}.
     * So before we prompt member-score list to user. We have to convert SCORE to int {@link rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation#byteArrayToInt(byte[])}
     * , and store in a byte[] type. Finally prompt to user.
     * @param list
     * @return
     */
    public static List<byte[]> changeEvenElementFromByteArrayToInt(List<byte[]> list) {
        List<byte[]> returnedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i % 2 != 0) {
                returnedList.add(Integer.toString(byteArrayToInt(list.get(i))).getBytes());
            } else {
                returnedList.add(list.get(i));
            }
        }
        return returnedList;
    }

    public static boolean checkScoreIsPositiveInteger(Request request) {
        // 1. 傳入的 request 不用判斷前兩個參數
        // 2. 開始判斷之後的參數，每兩個一組，第一個參數是 score，第二個參數是 member，score要判斷是不是正整數
        // 開始
        for (int i = 2; i < request.getLength() - 1; i += 2) {
            if (!canCastStringToInt(request.getParam(i).toString())) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkScoreIsPositiveInteger(LinkedList<String> list){
        for (int i = 2; i < list.size() - 1; i += 2) {
            if (!canCastStringToInt(list.get(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(canCastStringToInt("1.1"));
        System.out.println(canCastStringToInt("123"));

        LinkedList list = new LinkedList();
        list.add("123214123");
        list.add("zadd");
        list.add("1.2");
        list.add("member");
        System.out.println(checkScoreIsPositiveInteger(list));

    }
}
