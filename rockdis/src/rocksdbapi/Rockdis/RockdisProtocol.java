package rocksdbapi.Rockdis;

import redisapi.DAO.ScoreMemberVO;
import rocksdbapi.Rockdis.Params.RockdisParams;
import com.google.common.primitives.UnsignedBytes;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.util.SafeEncoder;
import rocksdbapi.Rockdis.Models.*;
import rocksdbapi.Rockdis.Utils.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation.*;

public class RockdisProtocol {


    private RockdisProtocol() {
    }

    //private static final byte ASTERISK_BYTE = '*';
    private static final byte DOLLAR_BYTE = '$';
    private static final byte PERCENT_BYTE = '%';
    //private static final int fieldOffset = 4;
    private static final int fieldPrefixOffset = 1;
    private static final int separateScoreMemberOffset = 1;
    private static Logger logger = LoggerFactory.getLogger(RockdisProtocol.class);

    public static RockdisResultObject sendCommand(final Command command, byte[] params, RocksDB db, RockdisParams.Type type, RocksIterator iterator, RockdisConnection rockdisConnection) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (iterator == null || !db.isOwningHandle()) {
            System.out.println("[ERROR]DB在關閉連線時，還呼叫了 newIterator()");
            System.out.println("Command : " + command + ", params : " + new String(params));
            rockdisResultObject.setCommand(Command.ERROR);
            rockdisResultObject.setResult("null".getBytes());
            rockdisResultObject.setFieldValueMap(new HashMap<>());
            rockdisResultObject.setResultList(new ArrayList<>());
            rockdisResultObject.setIntValue(0);
            return rockdisResultObject;
        }

        switch (command) {
            case GET:
                rockdisResultObject.setCommand(Command.GET);
                rockdisResultObject.setResult(db.get(params));
                iterator.close();
                rockdisConnection.decreaseIteratorCount();
                logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());
                break;

            case HGETALL:
                rockdisResultObject.setCommand(Command.HGETALL);
                rockdisResultObject.setFieldValueMap(getValuesByKey(iterator, params));
                iterator.close();
                rockdisConnection.decreaseIteratorCount();
                logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());
                break;

            case KEYS:
                rockdisResultObject.setCommand(Command.KEYS);
                rockdisResultObject.setResultList(getFullKeys(iterator, params));
                iterator.close();
                rockdisConnection.decreaseIteratorCount();
                logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());
                break;

            case DELETE:
                rockdisResultObject.setCommand(Command.DELETE);
                logger.debug("Start to delete data. The Key is : {}", params);
                int deletedCounts = delValuesByKey(iterator, db, params);
                logger.debug("Deleted counts : {}", deletedCounts);
                rockdisResultObject.setIntValue(deletedCounts);
                iterator.close();
                rockdisConnection.decreaseIteratorCount();
                logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());
                break;
            default:
        }

        return rockdisResultObject;
    }

    public static RockdisResultObject sendCommand(final Command command, byte[] params, byte[] params2, RocksDB db) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(db)) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + command + " 方法");
            System.out.println("Command : " + command + ", params : " + new String(params) + ", params2 : " + new String(params2));
            return generateEmptyReturnObject();
        }

        if (Objects.requireNonNull(command) == Command.SET) {
            rockdisResultObject.setCommand(Command.SET);
            db.put(params, params2);
        }
        return rockdisResultObject;
    }

    public static RockdisResultObject sendCommand(final Command command, byte[] params, byte[] params2, RocksDB db, RocksIterator iterator, RockdisConnection rockdisConnection) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(db)) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + command + " 方法");
            System.out.println("Command : " + command + ", params : " + new String(params) + ", params2 : " + new String(params2));
            return generateEmptyReturnObject();
        }

        if (Objects.requireNonNull(command) == Command.HGET) {
            logger.debug("[Standard Operation] HGET Method");
            rockdisResultObject.setCommand(Command.HGET);
            rockdisResultObject.setResult(getValueByKeyField(iterator, params, params2));
            iterator.close();
            rockdisConnection.decreaseIteratorCount();
        }
        return rockdisResultObject;
    }

    public static RockdisResultObject sendCommand(final Command command, byte[] params, byte[] params2, byte[] params3, RocksDB db) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(db)) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + command + " 方法");
            System.out.println("Command : " + command + ", params : " + new String(params) + ", params2 : " + new String(params2));
            return generateEmptyReturnObject();
        }

        if (Objects.requireNonNull(command) == Command.HSET) {
            logger.debug(" Writing data into RocksDB, start hset method.");
            rockdisResultObject.setCommand(Command.HSET);
            db.put(combineDBKeyWithKeyField(params, params2), params3);
        }
        return rockdisResultObject;
    }

    public static RockdisResultObject sendCommand(RocksDB db, RockdisConnection rockdisConnection, final Command command, byte[] params, byte[]... params2) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(db)) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + command + " 方法");
            System.out.println("Command : " + command + ", params : " + new String(params) + ", params2 : " + Arrays.toString(params2));
            return generateEmptyReturnObject();
        }

        switch (command) {
            case HDEL:
                rockdisResultObject.setCommand(Command.HDEL);
                WriteBatch writeBatch = new WriteBatch();
                WriteOptions writeOptions = new WriteOptions();
                try {
                for (byte[] field : params2) {
                    byte[] rocksdbKey = combineDBKeyWithKeyField(params, field);

                    writeBatch.delete(rocksdbKey);
                }
                dbWrite(db,writeOptions, writeBatch);	//db.write(writeOptions, writeBatch);
                } finally {
                	writeOptions.close();
                	writeBatch.close();
                }
            default:
        }
        return rockdisResultObject;
    }


    public static RockdisResultObject sendCommand(RocksDB db, RocksIterator iterator, RockdisConnection rockdisConnection, final Command command, byte[] params, byte[]... params2) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(db)) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + command + " 方法");
            System.out.println("Command : " + command + ", params : " + new String(params) + ", params2 : " + Arrays.toString(params2));
            return generateEmptyReturnObject();
        }

        switch (command) {
            case HMGET:
                rockdisResultObject.setCommand(Command.HMGET);

                Map<byte[], Integer> originalOrderMap = new TreeMap<>(UnsignedBytes.lexicographicalComparator());
                for (int i = 0; i < params2.length; i++) {
                    originalOrderMap.put(params2[i], i);
                }

                // Retrieve fields from originalOrderMap and then store into an List<byte[]>
                List<byte[]> orderedFields = new LinkedList<>();
                for (Map.Entry<byte[], Integer> entry : originalOrderMap.entrySet()) {
                    logger.debug(" Key: {}, index: {}", new String(entry.getKey()), entry.getValue());
                    orderedFields.add(entry.getKey());
                }

                // Call singleParamDistractRefactor, and then get the return value
                List<byte[]> returnValueByteArray = getValuesByKeyFields(iterator, params, orderedFields);
                logger.debug(" Iterate the returned value byte array as following. ");
                for (byte[] bytes : returnValueByteArray) {
                    logger.debug(new String(bytes));
                }

                // Create new Array, in order to store values which would return to client
                byte[][] resultArray = new byte[returnValueByteArray.size()][];

                // Get ordered field index, and then put the returned value which from singleParamDistractRefactor into byte array
                for (Map.Entry<byte[], Integer> entry : originalOrderMap.entrySet()) {
                    int index = entry.getValue();
                    logger.debug(" Index : {} ", index);
                    resultArray[index] = returnValueByteArray.remove(0);
                }

                // Prepare List<byte[]> and return
                List<byte[]> resultListArray = new LinkedList<>();
                for (byte[] value : resultArray) {
                    logger.debug(new String(value));
                    resultListArray.add(value);
                }

                rockdisResultObject.setResultList(resultListArray);

                iterator.close();
                rockdisConnection.decreaseIteratorCount();
                break;

            default:
        }
        return rockdisResultObject;
    }

    public static RockdisResultObject sendCommand(RocksDB db, final Command command, byte[] params, Map<byte[], byte[]> params2) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(db)) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + command + " 方法");
            System.out.println("Command : " + command + ", params : " + new String(params) + ", params2 : " + params2.toString());
            return generateEmptyReturnObject();
        }

        if (Objects.requireNonNull(command) == Command.HMSET) {
            rockdisResultObject.setCommand(Command.HMSET);
            WriteBatch writeBatch = new WriteBatch();
            WriteOptions writeOptions = new WriteOptions();
            boolean write = false;
            byte[] rocksdbKey0 = new byte[0];
            byte[] rocksdbValue0 = new byte[0];
            try {
            	for (Map.Entry<byte[], byte[]> entry : params2.entrySet()) {
            		byte[] rocksdbKey = combineDBKeyWithKeyField(params, entry.getKey());
            		byte[] rocksdbValue = entry.getValue();

            		if (new String(rocksdbKey).contains("2330")) {
            			logger.debug(" Insert data into RocksDB, key : {}, value : {}", new String(rocksdbKey), new String(rocksdbValue));
            		}
            		rocksdbKey0 = rocksdbKey;
            		rocksdbValue0 = rocksdbValue;
            		if(rocksdbKey!=null && rocksdbValue!=null) {
            			writeBatch.put(rocksdbKey, rocksdbValue);
                		write = true; 
            		}
            	}
            	if(write) dbWrite(db,writeOptions, writeBatch);
            } catch(Exception ex) {
            	System.out.println(ex.toString()+" rocksdbKey:'"+new String(rocksdbKey0)+"' value="+new String(rocksdbValue0)); 
            } finally {
            	writeOptions.close();
            	writeBatch.close();
            }            
        }
        return null;
    }

    public static RockdisResultObject sendCommand(RocksDB db, RockdisParams.Type type, RocksIterator iterator, final RockdisProtocol.Command command, byte[] key, byte[] field, int value, RockdisConnection rockdisConnection) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(db)) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + command + " 方法");
            System.out.println("Command : " + command + ", key : " + new String(key) + ", field : " + new String(field) + ", value : " + value);
            return generateEmptyReturnObject();
        }

        if (Objects.requireNonNull(command) == Command.HINCRBY) {
            logger.debug(" Trigger Command : {} ", command);
            rockdisResultObject.setCommand(command);
            // Call HGET Method
            byte[] getByteArrayValue = sendCommand(Command.HGET, key, field, db, iterator, rockdisConnection).getResult();
            // 補上 hget 的 decrement
            rockdisConnection.getIteratorCount().incrementAndGet();
            // Change byte[] to int
            if (!Arrays.equals(getByteArrayValue, "null".getBytes())) {
                int intValue = Integer.parseInt(new String(getByteArrayValue));
                Integer result = intValue + value;
                logger.debug(" Integer value is : {}", result);
                rockdisResultObject.setIntValue(result);
                byte[] sendValue = result.toString().getBytes();
                // Set New value return to RocksDB
                sendCommand(Command.HSET, key, field, sendValue, db);

            } else {
                logger.debug("HINCRBY get value from hget is null.");
                logger.debug("Rockdis would init this key and field's value as : {}", value);
                rockdisResultObject.setIntValue(value);
                byte[] sendValue = String.valueOf(value).getBytes();
                sendCommand(Command.HSET, key, field, sendValue, db);
            }

            iterator.close();
            rockdisConnection.decreaseIteratorCount();
        }
        return rockdisResultObject;
    }

    public static RockdisResultObject sendCommand(RocksDbDataExchange rocksDbDataExchange, List<MemberScoreVO> memberScoreVOList) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(rocksDbDataExchange.getRocksDB())) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + rocksDbDataExchange.getCommand() + " 方法");
            System.out.println("Command : " + rocksDbDataExchange.getCommand() + ", params : " + memberScoreVOList.toString());
            return generateEmptyReturnObject();
        }
        switch (rocksDbDataExchange.getCommand()) {
            case ZADD:
                WriteBatch writeBatch = new WriteBatch();
                WriteOptions writeOptions = new WriteOptions();
                int executedCount = 0;
                try {
                for (MemberScoreVO memberScoreVO : memberScoreVOList) {
//                    System.out.println("ZADD 即將寫入的 Member Score VO : " + memberScoreVO.toString());
                    if (isScorePositive(memberScoreVO.getScore())) {
//                    String positiveIntegerSize = paddingZeroBecomePositiveIntegerSize(memberScoreVO.getScore());
                        byte[] scoreByteArray = intToByteArray(memberScoreVO.getScore());
                        writeBatch.put(combineDBKeyWithKeyField(memberScoreVO.getKey(),
                                                memberScoreVO.getMember()),
                                        scoreByteArray);

                        executedCount++;
//                    rockdisResultObject.setIntValue(memberScoreVO.getScore());
                    } else {
                        logger.error("[ERROR] Rockdis API haven't provide negative scores yet.");
                        throw new ArithmeticException("[ERROR] Rockdis API haven't provide negative scores yet.");
                    }
                }

                if (executedCount > 0) {
                	dbWrite(rocksDbDataExchange.getRocksDB(),writeOptions, writeBatch);//rocksDbDataExchange.getRocksDB().write(writeOptions, writeBatch);
                }
                rockdisResultObject.setIntValue(executedCount);
                } finally {
                	writeOptions.close();
                	writeBatch.close();
                }
                break;
            case ZREM:
                WriteBatch zremWriteBatch = new WriteBatch();
                WriteOptions zremWriteOptions = new WriteOptions();
                int zremExecutedCount = 0;
                try {
                for (MemberScoreVO memberScoreVO : memberScoreVOList) {
                    zremWriteBatch.delete(combineDBKeyWithKeyField(memberScoreVO.getKey(), memberScoreVO.getMember()));
                    zremExecutedCount++;
                }
                dbWrite(rocksDbDataExchange.getRocksDB(),zremWriteOptions, zremWriteBatch);//rocksDbDataExchange.getRocksDB().write(zremWriteOptions, zremWriteBatch);
                rockdisResultObject.setIntValue(zremExecutedCount);
                } finally {
                	zremWriteOptions.close();
                	zremWriteBatch.close();
                }
                break;
        }
        return rockdisResultObject;

    }

    public static RockdisResultObject sendCommand(RocksDbDataExchange rocksDbDataExchange, MemberScoreVO memberScoreVO, RockdisConnection rockdisConnection) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (isDBClosed(rocksDbDataExchange.getRocksDB())) {
            System.out.println("[ERROR]DB在關閉連線時，呼叫了 " + rocksDbDataExchange.getCommand() + " 方法");
            System.out.println("Command : " + rocksDbDataExchange.getCommand() + ", params : " + memberScoreVO.toString());
            return generateEmptyReturnObject();
        }

        switch (rocksDbDataExchange.getCommand()) {
            case ZADD:
                if (isScorePositive(memberScoreVO.getScore())) {
//                    String positiveIntegerSize = paddingZeroBecomePositiveIntegerSize(memberScoreVO.getScore());
                    byte[] scoreByteArray = intToByteArray(memberScoreVO.getScore());
                    rocksDbDataExchange.getRocksDB()
                            .put(combineDBKeyWithKeyField(memberScoreVO.getKey(),
                                    memberScoreVO.getMember()),
                                    scoreByteArray);

                    rockdisResultObject.setIntValue(1);
//                    rockdisResultObject.setIntValue(memberScoreVO.getScore());
                } else {
                    logger.error("[ERROR] Rockdis API haven't provide negative scores yet.");
                    throw new ArithmeticException("[ERROR] Rockdis API haven't provide negative scores yet.");
                }
                break;

            case ZREM:
                // ZREM 用來 delete zadd後 的資料，不用判斷 score 的正負值
                rocksDbDataExchange.getRocksDB().delete(combineDBKeyWithKeyField(memberScoreVO.getKey(), memberScoreVO.getMember()));
                rockdisResultObject.setIntValue(1);
                break;

            case ZREMRANGEBYSCORE:

                Integer min = memberScoreVO.getScoreRangeStart();
                Integer max = memberScoreVO.getScoreRangeEnd();
                int deleteCount = 0;

                if (!isScorePositive(memberScoreVO.getScoreRangeStart()) || !isScorePositive(memberScoreVO.getScoreRangeEnd())) {
                    throw new ArithmeticException("[ERROR] Rockdis API haven't provide negative scores yet.");
                }
                if (isStartScoreSmallerThanEndScore(memberScoreVO.getScoreRangeStart(), memberScoreVO.getScoreRangeEnd())) {

                    // Get 這個 key 有的所有 member 跟 score
                    Map<byte[], byte[]> getFullyMemberAndScoreMap = getValuesByKey(rocksDbDataExchange.getIterator(), memberScoreVO.getKey());
                    // 判斷每一個 member 的 score 有沒有落在區間，如果有就要 1.刪除該 member 2.count +1
                    for (Map.Entry<byte[],byte[]> entry : getFullyMemberAndScoreMap.entrySet()){
                        int score = byteArrayToInt(entry.getValue());
                        if (score >= min && score <= max){
                            rocksDbDataExchange.getRocksDB().delete(combineDBKeyWithKeyField(memberScoreVO.getKey(), entry.getKey()));
                            deleteCount ++;
                        }
                    }
                } else {
                    logger.error("[ERROR] Min score is larger than Max score.");
                }
                rockdisResultObject.setIntValue(deleteCount);
                break;

            case ZREMRANGEBYRANK:
                Integer startRank = memberScoreVO.getScoreRangeStart();
                Integer stopRank = memberScoreVO.getScoreRangeEnd();
                byte[] key = memberScoreVO.getKey();
                RocksIterator iterator = rocksDbDataExchange.getIterator();

                // 先處理都是正數的Rank
                int removeIndex = startRank;
                int removeCount = stopRank - startRank + 1;
                int executedCount = 0;


                iterator.seek(key);
                String iteratedKey = new String(iterator.key());
                if (!iteratedKey.startsWith(new String(key))) {
                    rockdisResultObject.setIntValue(0);
                    return rockdisResultObject;
                }

                // 先移動到 startRank 的位置
                for (int i=0; i<removeIndex; i++){
                    if (iterator.isValid() && isKeyFullyMatch(iterator.key(), key)){
                        iterator.next();
                    }
                }

                // 開始刪除 Key
                for (int i=0; i<removeCount; i++){
                    if (iterator.isValid() && isKeyFullyMatch(iterator.key(), key)){
                        rocksDbDataExchange.getRocksDB().delete(iterator.key());
                        executedCount++;
                        iterator.next();
                    }
                }

                rockdisResultObject.setIntValue(executedCount);
                break;
            case ZRANGE:
                List<byte[]> memberList = null;
                // Currently ZRANGE's keyword only has "WITHSCORES"
                byte[] keyword = memberScoreVO.getKeyword().raw;
                Map<byte[], byte[]> getDataByKeyFromDB = getValuesByKey(rocksDbDataExchange.getIterator(), memberScoreVO.getKey());
//                logger.debug("Iterator count : {}", rockdisConnection.getIteratorCount());
                rocksDbDataExchange.getIterator().close();
                rockdisConnection.decreaseIteratorCount();
                logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());
//                logger.debug("Iterator count : {}", rockdisConnection.getIteratorCount());
                // 如果 DB內沒有這個 key 的資訊，就要回傳空的 List
                if (getDataByKeyFromDB.isEmpty()) {
                    rockdisResultObject.setResultList(new ArrayList<>());
                    return rockdisResultObject;
                }


                if (Arrays.equals(keyword, Keyword.WITHSCORES.raw)) {

                    memberList = new MemberScorePairProcessing(getDataByKeyFromDB)
                            .sortedByScore(false)
                            .getMemberListWithScore(memberScoreVO);
                } else {

                    memberList = new MemberScorePairProcessing(getDataByKeyFromDB)
                            .sortedByScore(false)
                            .getMemberList(memberScoreVO, false);
                }
                rockdisResultObject.setResultList(memberList);
                rocksDbDataExchange.getIterator().close();
                break;
                
            case ZREVRANGE:
                Map<byte[], byte[]> zrevrangeData = getValuesByKey(rocksDbDataExchange.getIterator(), memberScoreVO.getKey());
                rocksDbDataExchange.getIterator().close();
                rockdisConnection.decreaseIteratorCount();
                logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());
                // 如果 DB內沒有這個 key 的資訊，就要回傳空的 List
                if (zrevrangeData.isEmpty()) {
                    rockdisResultObject.setResultList(new ArrayList<>());
                    return rockdisResultObject;
                }

                if (Arrays.equals(memberScoreVO.getKeyword().raw, Keyword.WITHSCORES.raw)) {

                    memberList = new MemberScorePairProcessing(zrevrangeData)
                            .sortedByScore(true)
                            .getMemberListWithScore(memberScoreVO);
                } else {

                    memberList = new MemberScorePairProcessing(zrevrangeData)
                            .sortedByScore(false)
                            .getMemberList(memberScoreVO, true);
                }
                rockdisResultObject.setResultList(memberList);
                rocksDbDataExchange.getIterator().close();
                break;

            case ZRANGEBYSCORE:
                List<byte[]> scoreRangeList = new ArrayList<>();
                if (!isScorePositive(memberScoreVO.getScoreRangeStart()) || !isScorePositive(memberScoreVO.getScoreRangeEnd())) {
                    throw new ArithmeticException("[ERROR] Rockdis API haven't provide negative scores yet.");
                }
                if (isStartScoreSmallerThanEndScore(memberScoreVO.getScoreRangeStart(), memberScoreVO.getScoreRangeEnd())) {
                    Map<byte[], byte[]> getMapFromDB = getValuesByKey(rocksDbDataExchange.getIterator(), memberScoreVO.getKey());

                    rocksDbDataExchange.getIterator().close();
                    rockdisConnection.decreaseIteratorCount();
                    logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());

                    // 如果 DB內沒有這個 key 的資訊，就要回傳空的 List
                    if (getMapFromDB.isEmpty()) {
                        rockdisResultObject.setResultList(new ArrayList<>());
                        return rockdisResultObject;
                    }

                    scoreRangeList = new MemberScorePairProcessing(getMapFromDB)
                            .sortedByScore(false)
                            .getMemberListByScore(memberScoreVO);
                } else {
                    logger.error("[ERROR] Start score is larger than end score.");
                }
                rockdisResultObject.setResultList(scoreRangeList);
                break;
            case ZREVRANGEBYSCORE:
                List<byte[]> zrevScoreRangeList = new ArrayList<>();
                if (!isScorePositive(memberScoreVO.getScoreRangeStart()) || !isScorePositive(memberScoreVO.getScoreRangeEnd())) {
                    throw new ArithmeticException("[ERROR] Rockdis API haven't provide negative scores yet.");
                }
                if (isStartScoreLagerThanEndScore(memberScoreVO.getScoreRangeStart(), memberScoreVO.getScoreRangeEnd())) {
                    Map<byte[], byte[]> getMapFromDB = getValuesByKey(rocksDbDataExchange.getIterator(), memberScoreVO.getKey());
                    rocksDbDataExchange.getIterator().close();
                    rockdisConnection.decreaseIteratorCount();
                    logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());

                    // 如果 DB內沒有這個 key 的資訊，就要回傳空的 List
                    if (getMapFromDB.isEmpty()) {
                        rockdisResultObject.setResultList(new ArrayList<>());
                        return rockdisResultObject;
                    }


                    zrevScoreRangeList = new MemberScorePairProcessing(getMapFromDB)
                            .sortedByScore(true)
                            .getMemberListByScoreDSC(memberScoreVO);
                } else {
                    logger.error("[ERROR] Start score is larger than end score.");
                }
                rockdisResultObject.setResultList(zrevScoreRangeList);
                break;

            case ZCARD:
                int recordCount = getValuesByKey(rocksDbDataExchange.getIterator(), memberScoreVO.getKey()).size();
                rocksDbDataExchange.getIterator().close();
                rockdisConnection.decreaseIteratorCount();
                logger.debug("清除 iterator, iterator count : {}", rockdisConnection.getIteratorCount());
                rockdisResultObject.setMemberCount(recordCount);
                // rockdisResultObject.setMemberCount(getMemberCount(rocksDbDataExchange.getIterator(), memberScoreVO.getKey()));
                break;
        }
        return rockdisResultObject;
    }

//    private static List<byte[]> retrieveListFromScoreRange(List<Map.Entry<byte[], byte[]>> sortedList, Integer scoreRangeStart, Integer scoreRangeEnd) {
//        List<byte[]> zrangeByScoreReturnList = new ArrayList<>();
//
//        // Determine how many record should return
//        Integer start = scoreRangeStart;
//        Integer end = scoreRangeEnd;
//
//        for (int i = 0; i < sortedList.size(); i++) {
//
//            int valueFromDB = byteArrayToInt(sortedList.get(i).getValue());
//
//            // Search Head
//            if (valueFromDB >= start) {
//                if (valueFromDB <= end) {
//                    zrangeByScoreReturnList.add(sortedList.get(i).getKey());
//                } else {
//                    break;
//                }
//            }
//        }
//        return zrangeByScoreReturnList;
//    }

//    private static List<byte[]> retrieveListFromScoreRangeDSC(List<Map.Entry<byte[], byte[]>> sortedList, Integer scoreRangeStart, Integer scoreRangeEnd) {
//        List<byte[]> zrangeByScoreReturnList = new ArrayList<>();
//
//        // Determine how many record should return
//        Integer start = scoreRangeStart;
//        Integer end = scoreRangeEnd;
//
//        for (int i = 0; i < sortedList.size(); i++) {
//
//            int valueFromDB = byteArrayToInt(sortedList.get(i).getValue());
//
//            // Search Head
//            if (valueFromDB <= start) {
//                if (valueFromDB >= end) {
//                    zrangeByScoreReturnList.add(sortedList.get(i).getKey());
//                } else {
//                    break;
//                }
//            }
//        }
//        return zrangeByScoreReturnList;
//    }

    public static RockdisResultObject sendTransactionCommand(RocksDBTransactionDataExchange transactionDataExchange, MemberScoreVO memberScoreVO) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();
        Transaction getTransaction;

        switch (transactionDataExchange.getCommand()) {
            case ZADD:
                logger.debug("[Transaction Operation] ZADD Method");
                rockdisResultObject.setCommand(Command.ZADD);

                if (isScorePositive(memberScoreVO.getScore())) {
//                    String positiveIntegerSize = paddingZeroBecomePositiveIntegerSize(memberScoreVO.getScore());
                    byte[] scoreByteArray = intToByteArray(memberScoreVO.getScore());
                    transactionDataExchange.getTransaction().put(combineDBKeyWithKeyField(memberScoreVO.getKey(), scoreByteArray),
                            memberScoreVO.getMember());
                    rockdisResultObject.setIntValue(memberScoreVO.getScore());
                } else {
                    logger.error("[ERROR] Rockdis API haven't provide negative scores yet.");
                    return null;
                }
                break;

            case ZRANGE:
                logger.debug("[Transaction Operation] ZRANGE Method");
                rockdisResultObject.setCommand(Command.ZRANGE);
                rockdisResultObject.setResultList(getMembersListByASC(transactionDataExchange.getIterator(), memberScoreVO.getKey()));
                transactionDataExchange.getIterator().close();
                break;

            case ZREVRANGE:
                logger.debug("[Transaction Operation] ZREVRANGE Method");
                rockdisResultObject.setCommand(Command.ZREVRANGE);
                rockdisResultObject.setResultList(getMemberListByDESC(transactionDataExchange.getIterator(), memberScoreVO.getKey()));
                transactionDataExchange.getIterator().close();
                break;

            default:
        }
        return rockdisResultObject;
    }

    public static RockdisResultObject sendTransactionCommand(RocksDBTransactionDataExchange transactionDataExchange, RockdisDataVO dataVO) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();
        Transaction getTransaction;

        switch (transactionDataExchange.getCommand()) {
            case GETFORUPDATE:
                logger.info("[Transaction Operation] GETFORUPDATE Method");
                rockdisResultObject.setCommand(Command.GETFORUPDATE);
                Transaction transaction = transactionDataExchange.getTransaction();
                byte[] result = transaction.getForUpdate(transactionDataExchange.getReadOptions(), dataVO.getKey(), true);
                rockdisResultObject.setResult(result);
                break;

            case GET:
                logger.info("[Transaction Operation] GET Method");
                rockdisResultObject.setCommand(Command.GET);
                getTransaction = transactionDataExchange.getTransaction();
                try {
                    byte[] getResult = getTransaction.get(transactionDataExchange.getReadOptions(), dataVO.getKey());
                    rockdisResultObject.setResult(getResult);
                } catch (NullPointerException e) {
                    logger.error("[ERROR] You don't need to use \"TRANSACTION Mode\".");
                    logger.error("[ERROR] Please call multi() or use standard Rockdis API.");
                }
                break;

            case HGET:
                logger.info("[Transaction Operation] HGet Method");
                rockdisResultObject.setCommand(Command.HGET);
                getTransaction = transactionDataExchange.getTransaction();

                //rockdisResultObject.setResult(singleParamDistract(getTransaction.getIterator(transactionDataExchange.getReadOptions()), params, params2));

                byte[] getResult = getTransaction.get(transactionDataExchange.getReadOptions(), combineDBKeyWithKeyField(dataVO.getKey(), dataVO.getFiled()));
                rockdisResultObject.setResult(getResult);
                break;

            case HMGET:
                logger.debug("[Transaction Operation] HMGET Method");
                rockdisResultObject.setCommand(Command.HMGET);
                getTransaction = transactionDataExchange.getTransaction();
                byte[][] prepareByteArrays = new byte[dataVO.getFieldArray().length][];
                for (int i = 0; i < dataVO.getFieldArray().length; i++) {
                    prepareByteArrays[i] = combineDBKeyWithKeyField(dataVO.getKey(), dataVO.getFieldArray()[i]);
                }

                logger.debug("Prepared Byte Arrays Size is : {}", prepareByteArrays.length);
                byte[][] resultByteArrays = getTransaction.multiGet(transactionDataExchange.getReadOptions(), prepareByteArrays);
                // Prepare List<byte[]> and return
                List<byte[]> resultListArray = new LinkedList<>();
                for (byte[] value : resultByteArrays) {
                    logger.debug(new String(value));
                    resultListArray.add(value);
                }
                rockdisResultObject.setResultList(resultListArray);
                break;

            case HGETALL:
                logger.info("[Transaction Operation] HGETALL Method");
                rockdisResultObject.setCommand(Command.GET);
                Transaction hGetAllTransaction = transactionDataExchange.getTransaction();
                RocksIterator hGetAllIterator = hGetAllTransaction.getIterator(transactionDataExchange.getReadOptions());
                rockdisResultObject.setFieldValueMap(getValuesByKey(hGetAllIterator, dataVO.getKey()));
                hGetAllIterator.close();
                break;

            case SET:
                logger.info("[Transaction Operation] Set Method");
                rockdisResultObject.setCommand(Command.SET);
                transactionDataExchange.getTransaction().put(dataVO.getKey(), dataVO.getValue());
                break;

            case HSET:
                logger.debug("[Transaction Operation] HSET Method");
                rockdisResultObject.setCommand(Command.HSET);
                transactionDataExchange.getTransaction().put(combineDBKeyWithKeyField(dataVO.getKey(), dataVO.getFiled()), dataVO.getValue());
                break;

            case HMSET:
                logger.debug("[Transaction Operation] HMSET Method");
                rockdisResultObject.setCommand(Command.HMSET);
                getTransaction = transactionDataExchange.getTransaction();
                /*
                Write Batch in Transaction
                 */

                for (Map.Entry<byte[], byte[]> entry : dataVO.getFieldValueMap().entrySet()) {
                    byte[] rocksdbKey = combineDBKeyWithKeyField(dataVO.getKey(), entry.getKey());
                    byte[] rocksdbValue = entry.getValue();
                    logger.debug(" [Transaction Operation] Pre-cache data into RocksDB, key : {}, value : {}", new String(rocksdbKey), new String(rocksdbValue));
                    getTransaction.getWriteBatch().put(rocksdbKey, rocksdbValue);
                }
                break;

            case HINCRBY:
                logger.debug("[Transaction Operation] HINCRBY Method");
                rockdisResultObject.setCommand(Command.HINCRBY);

                // Get Value From Key+Field
                byte[] getByteArrayValue = transactionDataExchange.getTransaction().get(transactionDataExchange.getReadOptions(), combineDBKeyWithKeyField(dataVO.getKey(), dataVO.getFiled()));
                // Change byte[] to int
                if (!Arrays.equals(getByteArrayValue, "null".getBytes())) {
                    int intValue = Integer.parseInt(new String(getByteArrayValue));

                    //Safe Add Section
                    Integer resultValue = MathOverflowPreventor.safeAdd(intValue, dataVO.getAddend());
                    //Integer result = intValue + params3;
                    logger.debug(" Integer value is : {}", resultValue);

                    byte[] sendValue = resultValue.toString().getBytes();
                    // Set New value return to RocksDB
                    transactionDataExchange.getTransaction().put(combineDBKeyWithKeyField(dataVO.getKey(), dataVO.getFiled()), sendValue);
                    rockdisResultObject.setIntValue(resultValue);
                } else {
                    logger.debug("HINCRBY get value from hget is null.");
                    rockdisResultObject.setIntValue(null);
                }
                break;

            case DELETE:
                logger.debug("[Transaction Operation] DELETE Method");
                rockdisResultObject.setCommand(Command.DELETE);
                transactionDataExchange.getTransaction().delete(dataVO.getKey());
                break;

            default:
                logger.error("[ERROR] Please Check your system. This section shouldn't be trigger.");

        }
        return rockdisResultObject;
    }

    public static enum Command {
        SET, GET, HSET, HGET, HMSET, HDEL, HMGET, HGETALL, KEYS, HINCRBY, DELETE, GETFORUPDATE,
        ZADD, ZRANGE, ZREVRANGE, ZCARD, ZRANGEBYSCORE, ZREVRANGEBYSCORE,
        ZREM, ZREMRANGEBYSCORE, ZREMRANGEBYRANK,
        ERROR;
        public final byte[] raw;

        Command() {
            raw = this.name().getBytes();
        }
    }

    public static enum Keyword {
        NORMAL, WITHSCORES, LIMIT;
        public final byte[] raw;

        Keyword() {
            raw = SafeEncoder.encode(this.name().toLowerCase());
        }
    }

    public static byte[] combineDBKeyWithKeyField(String key, String field){
        return combineDBKeyWithKeyField(key.getBytes(), field.getBytes());
    }

    public static byte[] combineDBKeyWithKeyField(byte[] key, byte[] field) {

        int keyLength = key.length;
        int fieldLength = field.length;
        byte[] combineArray = new byte[keyLength + fieldPrefixOffset + fieldLength];
        System.arraycopy(key, 0, combineArray, 0, keyLength);
        combineArray[keyLength] = DOLLAR_BYTE;
        System.arraycopy(field, 0, combineArray, keyLength + fieldPrefixOffset, fieldLength);

        if (new String(combineArray).contains("2330")) {
            logger.debug(" The string after combined is : {}", new String(combineArray));
        }

        return combineArray;
    }

    //TODO YOU MUST refactor this section while testing is succeed.
    private static byte[] getValueByKeyField(RocksIterator iterator, byte[] key, byte[] field) throws RocksDBException {

        List<byte[]> arrayList = new ArrayList<>();
        arrayList.add(field);
        List<byte[]> returnList = getValuesByKeyFields(iterator, key, arrayList);

        if (returnList.size() == 1) {
            return returnList.get(0);
        } else if (returnList.size() == 0) {
            return null;
        } else {
            logger.error("Wrong Size from DB");
            return null;
        }
    }

    private static List<byte[]> getValuesByKeyFields(RocksIterator iterator, byte[] key, List<byte[]> fieldsArrayList) throws RocksDBException {

        // 目前有幾個 field 要取得 value
        int fieldsArrayListSize = fieldsArrayList.size();
        List<byte[]> valuesArrayList = new LinkedList<>();
        logger.debug(" Fields Array List size is : {}", fieldsArrayListSize);

        int iteratorCount = 0;
        while (fieldsArrayList.size() > 0) {

            for (iterator.seek(key); iterator.isValid(); iterator.next()) {

                // 確保不會拿到別的key range
                String iteratedKey = new String(iterator.key());
                logger.debug("Key from RocksDB is : {} ", iteratedKey);
                if (!iteratedKey.startsWith(new String(key))) {
                    break;
                }

                if (fieldsArrayList.size() == 0) {
                    break;
                }
                // Distract key and value
                int keyLength = key.length;
                int filedLength = fieldsArrayList.get(0).length;
                int iteratedKeyLength = iteratedKey.length();

                if (iteratedKeyLength > keyLength) {
                    byte[] distractedField = Arrays.copyOfRange(iterator.key(), keyLength + fieldPrefixOffset, keyLength + fieldPrefixOffset + filedLength);
                    logger.debug(" The field after distracted : {}", new String(distractedField));

                    if (Arrays.equals(fieldsArrayList.get(0), distractedField)) {
                        logger.debug(" The returned value is : {}", new String(iterator.value()));
                        valuesArrayList.add(iterator.value());
                        fieldsArrayList.remove(0);
                    }
                }
            }

            if (fieldsArrayList.size() != 0) {
                logger.debug(" The returned value is : null");
                valuesArrayList.add("null".getBytes());
                fieldsArrayList.remove(0);

            }
            iteratorCount++;
        }

        logger.debug(" Total iterator count is : {}", iteratorCount);
        return valuesArrayList;
    }

    private static int delValuesByKey(RocksIterator iterator, RocksDB db, byte[] key) throws RocksDBException {
        int deleteCount = 0;


        // Batch Delete
        WriteBatch writeBatch = new WriteBatch();
        WriteOptions writeOptions = new WriteOptions();
        try {
        for (iterator.seek(key); iterator.isValid(); iterator.next()) {

            logger.debug("Current key retrieved from RocksDB -> key : {}", new String(iterator.key()));
            String seekedKeyInString = new String(key);
            String iteratedKey = new String(iterator.key());
            if (!iteratedKey.startsWith(new String(key))) {
                break;
            }
            int keyLength = key.length;
            int iteratedKeyLength = iterator.key().length;


            if (seekedKeyInString.equals(iteratedKey)) {
                // 刪除 Set 的資料
                logger.debug("Delete Key : {}, value : {}", new String(iterator.key()), new String(iterator.value()));
                writeBatch.delete(iterator.key());
//                db.delete(iterator.key());
                deleteCount++;
            } else {
                // 刪除 Hset, HMSet 的資料
                if (isContentOfKeyFullyMatch(iterator.key(), key)) {
                    logger.debug("Delete Key : {}, value : {}", new String(iterator.key()), new String(iterator.value()));
                    writeBatch.delete(iterator.key());
//                    db.delete(iterator.key());
                    deleteCount++;
                } else {
                    break;
                }
            }
        }
        dbWrite(db,writeOptions, writeBatch);	//db.write(writeOptions, writeBatch);
        }finally {
        	writeOptions.close();
        	writeBatch.close();        	
            iterator.close();        	
        }
        return deleteCount;
    }

    private static Map<byte[], byte[]> getValuesByKey(RocksIterator iterator, byte[] key) {

        Map<byte[], byte[]> fieldValueMap = new ConcurrentSkipListMap<>(((o1, o2) -> {
            for (int i = 0; i < Math.min(o1.length, o2.length); i++) {
                int diff = o1[i] - o2[i];
                if (diff != 0) {
                    return diff;
                }
            }
            return o1.length - o2.length;
        })
        );

        for (iterator.seek(key); iterator.isValid(); iterator.next()) {

            logger.debug(new String(iterator.key()));

            String iteratedKey = new String(iterator.key());
            if (!iteratedKey.startsWith(new String(key))) {
                break;
            }
            int keyLength = key.length;
            int iteratedKeyLength = iterator.key().length;

            // 這個 Key 當初 set的時候並沒有 field欄位，所以從iterator拿出來的key 會跟搜尋的key一樣長度
            if (keyLength == iteratedKeyLength) {
                fieldValueMap.put(iterator.key(), iterator.value());
            } else {
                if (!isKeyFullyMatch(iterator.key(), key)) {
                    break;
                }
                int fieldLength = iteratedKeyLength - keyLength - fieldPrefixOffset;  // 3
                byte[] distractedField = Arrays.copyOfRange(iterator.key(), keyLength + fieldPrefixOffset, keyLength + fieldPrefixOffset + fieldLength);
                // todo 這裡要解決亂碼的問題！
                fieldValueMap.put(distractedField, iterator.value());
                logger.debug("Get Field : {} and Value : {} from DB.", new String(distractedField), new String(iterator.value()));
            }
        }

        return fieldValueMap;
    }

    private static List<byte[]> getFullKeys(RocksIterator iterator, byte[] key) {

        List<byte[]> returnedList = new ArrayList<>();
        byte[] searchPrefix = null;
        boolean isFullyScan = false;

        if (SpecialCharacter.shouldFullyScan(key)) {
            isFullyScan = SpecialCharacter.shouldFullyScan(key);
        }

        searchPrefix = SpecialCharacter.getSearchPrefix(key);
        logger.debug("Key is : {}", new String(key));
        logger.debug("Search Prefix : {}", new String(searchPrefix));

        // Get keys from RocksDB
        if ( isFullyScan ) {
            returnedList = getKeysListASC(iterator, searchPrefix);
        } else {
            returnedList = getKeysListWithStopPrefix(iterator, searchPrefix, searchPrefix);
        }

        String regexString = SpecialCharacter.MappingNormalStringToJavaRegex(key);
        logger.debug("Regex String is : {}", regexString);

        ByteKeyHashSet finalByteKeyHashSet = getMatchPatternSet(regexString, returnedList);
        return Converter.byteKeyHashSetToByteArrayList(finalByteKeyHashSet);
    }

    /**
     * This method would return if the key from iterator is fully match
     * with the key that user defined. In this method will keep DOLLAR_BYTE 's position.
     * We get the length size before DOLLAR_BYTE. Finally compare two lengths.
     *
     * @param iteratedKey
     * @param key
     * @return boolean true or false
     */
    private static boolean isKeyFullyMatch(byte[] iteratedKey, byte[] key) {
        // Get dollar sign position of key which from DB
        int pivot = 0;
        for (byte b : iteratedKey) {
            if (b == DOLLAR_BYTE) {
                break;
            }
            pivot += 1;
        }
        return pivot == key.length;
    }

    private static boolean isContentOfKeyFullyMatch(byte[] iteratedKey, byte[] key) {
        int pivot = 0;
        for (byte b : iteratedKey) {
            if (b == DOLLAR_BYTE) {
                break;
            }
            pivot += 1;
        }
        String realKeyFromIteratedKey = new String(Arrays.copyOfRange(iteratedKey, 0, pivot));
        String keyOfString = new String(key);
        return (keyOfString.equals(realKeyFromIteratedKey));
    }

    private static List<byte[]> getMemberListByDESC(RocksIterator iterator, byte[] key) {

        return getMemberList(iterator, key, true, false, null, null);
    }

    private static List<byte[]> getMemberListByRangeDESC(RocksIterator iterator, byte[] key, Integer keyRangeStart, Integer keyRangeEnd) {
        byte[] keyRangeStartByte = combineDBKeyWithKeyField(key, paddingZeroBecomePositiveIntegerSize(keyRangeStart).getBytes());
        byte[] keyRangeEndByte = combineDBKeyWithKeyField(key, paddingZeroBecomePositiveIntegerSize(keyRangeEnd).getBytes());

        return getMemberList(iterator, key, true, true, keyRangeStartByte, keyRangeEndByte);
    }

//    private static List<byte[]> getMemberListByPositionWithASC(List<Map.Entry<byte[], byte[]>> list, Integer start, Integer end, Keyword keyword) {
//
//        return getList(list, start, end, keyword);
//    }
//
//    private void getMemberListByScoreWithASC() {
//
//    }

//    private static List<byte[]> getMemberListWithScoreWithASC(List<Map.Entry<byte[], byte[]>> list, Integer start, Integer end, Keyword keyword) {
//        return getList(list, start, end, keyword);
//    }

//    private static List<byte[]> getList(List<Map.Entry<byte[], byte[]>> list, Integer start, Integer end, Keyword keyword) {
//        List<byte[]> zrangeReturnList = new ArrayList<>();
//
//        // Determine how many record should return
//        Integer positionStart = start;
//        Integer positionEnd = end;
//
//        if (start < 0) {
//            positionStart = convertNegativePositionToPositivePosition(list.size(), start);
//        }
//        if (end < 0) {
//            positionEnd = convertNegativePositionToPositivePosition(list.size(), end);
//        }
//        if (end > list.size() - 1) {
//            positionEnd = list.size() - 1;
//        }
//
//
//        for (int i = positionStart; i <= positionEnd; i++) {
//            zrangeReturnList.add(list.get(i).getKey());
//            if (keyword.equals(Keyword.WITHSCORES)) {
//                zrangeReturnList.add(list.get(i).getValue());
//            }
//        }
//        return zrangeReturnList;
//    }

//    private static int convertNegativePositionToPositivePosition(int listSize, Integer position) {
//
//        if (position < -listSize) {
//            return 0;
//        } else if (position < 0) {
//            return listSize + position;
//        } else {
//            return position;
//        }
//    }


    private static List<byte[]> getMembersListByASC(RocksIterator iterator, byte[] key) {

        return getMemberList(iterator, key, false, false, null, null);
    }

    private static List<byte[]> getMemberListByRangeASC(RocksIterator iterator, byte[] key, Integer keyRangeStart, Integer keyRangeEnd) {
        byte[] keyRangeStartByte = combineDBKeyWithKeyField(key, paddingZeroBecomePositiveIntegerSize(keyRangeStart).getBytes());
        byte[] keyRangeEndByte = combineDBKeyWithKeyField(key, paddingZeroBecomePositiveIntegerSize(keyRangeEnd).getBytes());

        return getMemberList(iterator, key, false, true, keyRangeStartByte, keyRangeEndByte);
    }

    private static List<byte[]> getMemberList(RocksIterator iterator, byte[] key, boolean isReverseIterate, boolean isScanByRangeScore, byte[] keyRangeStart, byte[] keyRangeEnd) {

        logger.debug("[Get Member List] This method return member list sorted by score.");
        List<byte[]> memberList = new ArrayList<>();

        if (isReverseIterate) {
            // Reverse Iterator
            for (iterator.seekToLast(); iterator.isValid(); iterator.prev()) {
                if (!byteToString(iterator.key()).startsWith(new String(key))) {
                    break;
                }
                logger.debug("Get Key : {} and Member : {} from Forward DB.", byteToString(iterator.key()), new String(iterator.value()));
                if (isScanByRangeScore && byteArrayCompare(iterator.key(), keyRangeStart) <= 0 && byteArrayCompare(iterator.key(), keyRangeEnd) > 0) {
                    memberList.add(iterator.value());
                    logger.debug("Insert member info into memberList.");
                } else if (isScanByRangeScore && byteArrayCompare(iterator.key(), keyRangeEnd) < 0) {
                    break;
                } else if (!isScanByRangeScore) {
                    memberList.add(iterator.value());
                    logger.debug("Insert member info into memberList.");
                }
            }
        } else {
//            for (iterator.seek(key); iterator.isValid(); iterator.next()) {
//                if (!byteToString(iterator.key()).startsWith(new String(key))) {
//                    break;
//                }
//                logger.debug("Get Key : {} and Member : {} from Backward DB.", byteToString(iterator.key()), new String(iterator.value()));
//                if (isScanByRangeScore && byteArrayCompare(iterator.key(), keyRangeStart) >= 0 && byteArrayCompare(iterator.key(), keyRangeEnd) < 0) {
//                    memberList.add(iterator.value());
//                    logger.debug("Insert member info into memberList.");
//                } else if (isScanByRangeScore && byteArrayCompare(iterator.key(), keyRangeEnd) > 0) {
//                    break;
//                } else if (!isScanByRangeScore) {
//                    memberList.add(iterator.value());
//                    logger.debug("Insert member info into memberList.");
//                }
//            }
        }
        return memberList;
    }

    private static String byteToString(byte[] input) {
        return new String(input);
    }

    public static int byteArrayCompare(byte[] o1, byte[] o2) {
        for (int i = 0, j = 0; i < o1.length && j < o2.length; i++, j++) {
            int a = (o1[i] & 0xff);
            int b = (o2[j] & 0xff);
            if (a != b) {
                return a - b;
            }
        }
        return o1.length - o2.length;
    }


    private static List<byte[]> getKeysListASC(RocksIterator iterator, byte[] searchPrefix){
        List<byte[]> returnedList = new ArrayList<>();
        for (iterator.seek(searchPrefix); iterator.isValid(); iterator.next()) {
            logger.debug("Key:{} in DB", new String(iterator.key()));
            returnedList.add(iterator.key());
        }
        return returnedList;
    }

    private static List<byte[]> getKeysListWithStopPrefix(RocksIterator iterator, byte[] searchPrefix, byte[] stopPrefix){
        List<byte[]> returnedList = new ArrayList<>();
        for (iterator.seek(searchPrefix); iterator.isValid(); iterator.next()) {
            String iteratedKey = new String(iterator.key());
            if (!iteratedKey.startsWith(new String(stopPrefix))) {
                break;
            }
            logger.debug("Key:{} in DB", new String(iterator.key()));
            returnedList.add(iterator.key());
        }
        return returnedList;
    }

    private static ByteKeyHashSet getMatchPatternSet(String regexString, List<byte[]> returnedList){
        List<byte[]> finalList = new ArrayList<>();
        Set<byte[]> finalSet = new LinkedHashSet<>();
        // in order to avoid duplicate element stored in Set list, we use ByteKeyHashSet to store element.
        ByteKeyHashSet finalByteKeyHashSet = new ByteKeyHashSet();
        // Use Regex to filter returned list
        // Get Match Pattern Of HashSet
        Pattern pattern = Pattern.compile(regexString);

        for (int i = 0; i < returnedList.size(); i++) {
            String matchee = new String(returnedList.get(i));
            Matcher matcher = pattern.matcher(matchee);
            if (matcher.matches() == true) {
                finalList.add(returnedList.get(i));

                // Process Key in list, some keys have field like
                // hset("2020", "1210", "value)
                // The Key would be 2020$1210
                // So, we have to separate string by '$'
                // Find '$' Sign
                byte[] dollarSign = "$".getBytes();
                byte[] separatedByteArray = null;
                for (int index = 0; index < returnedList.get(i).length; index++) {
                    byte[] currentByte = Arrays.copyOfRange(returnedList.get(i), index, index + 1);
                    if (Arrays.equals(dollarSign, currentByte)) {
                        separatedByteArray = Arrays.copyOfRange(returnedList.get(i), 0, index);
                        break;
                    }
                }
                if (separatedByteArray == null) {
                    finalSet.add(returnedList.get(i));
                    finalByteKeyHashSet.add(returnedList.get(i));
                } else {
                    finalSet.add(separatedByteArray);
                    finalByteKeyHashSet.add(separatedByteArray);
                }
            }
        }

        logger.debug(" **** Final List =========================");
        for (int i = 0; i < finalList.size(); i++) {
            logger.debug(new String(finalList.get(i)));
        }
        logger.debug(" =========================================");


        logger.debug(" **** Final Set ==========================");
        Iterator<byte[]> setIterator = finalSet.iterator();
        while(setIterator.hasNext()){
            logger.debug(new String(setIterator.next()));
        }
        logger.debug(" =========================================");

        return finalByteKeyHashSet;
    }

    private static boolean isDBClosed(RocksDB db) {
        return !db.isOwningHandle();
    }

    private static RockdisResultObject generateEmptyReturnObject(){

        RockdisResultObject emptyRockdisResultObject = new RockdisResultObject();
        emptyRockdisResultObject.setCommand(Command.ERROR);
        emptyRockdisResultObject.setResult("null".getBytes());
        emptyRockdisResultObject.setFieldValueMap(new HashMap<>());
        emptyRockdisResultObject.setResultList(new ArrayList<>());
        emptyRockdisResultObject.setIntValue(0);
        return emptyRockdisResultObject;
    }
    
    public static void dbWrite(RocksDB db,WriteOptions writeOptions,WriteBatch writeBatch) throws RocksDBException {
    	synchronized(db) {
    		try {
            	db.write(writeOptions, writeBatch);		
    		} finally {
    			writeOptions.close();
    			writeBatch.close();
    		}
    	}
    }    
    //======================SendWriteBatchCommand===========
    
    //HMSET
    public static RockdisResultObject sendCommand(WriteBatch writeBatch, final Command command, byte[] params, Map<byte[], byte[]> params2) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (Objects.requireNonNull(command) == Command.HMSET) {
            rockdisResultObject.setCommand(Command.HMSET);
            byte[] rocksdbKey0 = new byte[0];
            byte[] rocksdbValue0 = new byte[0];
            try {
            	for (Map.Entry<byte[], byte[]> entry : params2.entrySet()) {
            		byte[] rocksdbKey = combineDBKeyWithKeyField(params, entry.getKey());
            		byte[] rocksdbValue = entry.getValue();

            		rocksdbKey0 = rocksdbKey;
            		rocksdbValue0 = rocksdbValue;
            		if(rocksdbKey!=null && rocksdbValue!=null) writeBatch.put(rocksdbKey, rocksdbValue);
            	}
            } catch(Exception ex) {
            	System.out.println(ex.toString()+" rocksdbKey:'"+new String(rocksdbKey0)+"' value="+new String(rocksdbValue0)); 
            } finally {

            }            
        }        
        return null;
    }    
    
    public static RockdisResultObject sendCommand(final Command command, byte[] params, byte[] params2, byte[] params3, WriteBatch writeBatch) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (Objects.requireNonNull(command) == Command.HSET) {
            logger.debug(" Writing data into RocksDB, start hset method.");
            rockdisResultObject.setCommand(Command.HSET);
            writeBatch.put(combineDBKeyWithKeyField(params, params2), params3);
        }
        return rockdisResultObject;
    }  
    
    public static RockdisResultObject sendCommand(final Command command, byte[] params, byte[] params2, WriteBatch writeBatch) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();

        if (Objects.requireNonNull(command) == Command.SET) {
            rockdisResultObject.setCommand(Command.SET);
            writeBatch.put(params, params2);
        }
        return rockdisResultObject;
    }    
    
    
    public static RockdisResultObject sendCommand(WriteBatch writeBatch,Command command , List<MemberScoreVO> memberScoreVOList) throws RocksDBException {
        RockdisResultObject rockdisResultObject = new RockdisResultObject();
        
        if (Objects.requireNonNull(command) == Command.ZADD) {
            int executedCount = 0;
            for (MemberScoreVO memberScoreVO : memberScoreVOList) {
            	// System.out.println("ZADD 即將寫入的 Member Score VO : " + memberScoreVO.toString());
            	if (isScorePositive(memberScoreVO.getScore())) {
            		// String positiveIntegerSize = paddingZeroBecomePositiveIntegerSize(memberScoreVO.getScore());
            		byte[] scoreByteArray = intToByteArray(memberScoreVO.getScore());
            		writeBatch.put(combineDBKeyWithKeyField(memberScoreVO.getKey(),
            				memberScoreVO.getMember()),
            				scoreByteArray);

                    executedCount++;
                    //   rockdisResultObject.setIntValue(memberScoreVO.getScore());
                } else {
                    logger.error("[ERROR] Rockdis API haven't provide negative scores yet.");
                    throw new ArithmeticException("[ERROR] Rockdis API haven't provide negative scores yet.");
                }
            }
            rockdisResultObject.setIntValue(executedCount);

        }
        if (Objects.requireNonNull(command) == Command.ZREM) {
        	WriteBatch zremWriteBatch = writeBatch;
            int zremExecutedCount = 0;
            for (MemberScoreVO memberScoreVO : memberScoreVOList) {
                zremWriteBatch.delete(combineDBKeyWithKeyField(memberScoreVO.getKey(), memberScoreVO.getMember()));
                zremExecutedCount++;
            }
            rockdisResultObject.setIntValue(zremExecutedCount);
        }
        return rockdisResultObject;
    }    
    
}
