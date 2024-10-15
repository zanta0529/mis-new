package redisapi.RockdisMappingCommand;

import rocksdbapi.Rockdis.Models.MemberScoreVO;
import rocksdbapi.Rockdis.Rockdis;
import redisapi.Util.ArithmeticUtil;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.annotation.ParamLength;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdbapi.Rockdis.RockdisProtocol;
import rocksdbapi.Rockdis.Utils.MemberScorePairProcessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static redisapi.Util.ArithmeticUtil.*;
import static redisapi.Util.ArithmeticUtil.SafeStringToDouble;
import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;
import static redisapi.Util.CollectionUtil.convertStringMapToByteArrayMap;
import static rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation.byteArrayToInt;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/12
 */
@Command("zrangebyscore")
@ParamLength(3)
public class ZRangeByScoresCommand implements RespCommand {

    private static final Logger logger = LoggerFactory.getLogger(ZRangeByScoresCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            if (!isParameterValid(request.getLength())) {
                return error("ERR wrong number of arguments for 'zrangebyscore' command");
            }

            if (ArithmeticUtil.checkByScoreParameter(request) != null) {
                return ArithmeticUtil.checkByScoreParameter(request);
            }

            SafeString key = request.getParam(1);
            SafeString minScore = request.getParam(2);
            SafeString maxScore = request.getParam(3);
            RockdisProtocol.Keyword keyword = null;
            int inx = -1;
            int size = -1;

            if (request.getParam(4) != null && request.getParam(4).toString().equalsIgnoreCase("WITHSCORES")) {
                keyword = RockdisProtocol.Keyword.WITHSCORES;
                if (request.getParam(5) != null && request.getParam(5).toString().equalsIgnoreCase("LIMIT")) {
                	try {
                        SafeString inxScore = request.getParam(6);
                        SafeString sizeScore = request.getParam(7);
                        inx = SafeStringToInt(inxScore);
                        size = SafeStringToInt(sizeScore);
                	} catch(Exception ex) {
                		
                	}
                }
            } else {
                keyword = RockdisProtocol.Keyword.NORMAL;
            }
            
            if (request.getParam(4) != null && request.getParam(4).toString().equalsIgnoreCase("LIMIT")) {
            	try {
                    SafeString inxScore = request.getParam(5);
                    SafeString sizeScore = request.getParam(6);
                    inx = SafeStringToInt(inxScore);
                    size = SafeStringToInt(sizeScore);
            	} catch(Exception ex) {
            		
            	}
                if (request.getParam(7) != null && request.getParam(7).toString().equalsIgnoreCase("WITHSCORES")) {
                    keyword = RockdisProtocol.Keyword.WITHSCORES;
                } else {
                    keyword = RockdisProtocol.Keyword.NORMAL;
                }
            }

            Double minDoubleScore = 0d;
            Double maxDoubleScore = 0d;


            try {
                maxDoubleScore = SafeStringToDouble(maxScore);
                minDoubleScore = SafeStringToDouble(minScore);
            } catch (NumberFormatException exception) {
                return error("ERR value is not an integer or out of range");
            }

            if (!isParameterPositiveNumber(minDoubleScore) || !isParameterPositiveNumber(maxDoubleScore)) {
                return error("ERR MAX MIN value currently only can support positive integer");
            }

            List<byte[]> zrangebyscoreList = new ArrayList<>();


            if (ChannelStatusUtil.isAuthorized(request)) {

                Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();

                // 把參數包裝成 MemberScoreVO
                MemberScoreVO memberScoreVO = new MemberScoreVO(key.getBytes(),
                        Integer.parseInt(minScore.toString()),
                        Integer.parseInt(maxScore.toString()),
                        keyword
                );

                logger.debug(" [ZRANGEBYSCORE Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 如果在切換DB，get相關的都回 null
                if (request.getServerContext().getSafemode()) {
                    return array(new ArrayList<>());
                }


                // 確認是否有啟動 Cache System
                if (request.getServerContext().getCacheSystemStatus()) {
                    // 詢問LCPS是否有值
                    Object LCPSResult = request.getServerContext().getLargeCachePoolSystem().getAllFromLCPS(key.toString());
                    if (LCPSResult != null) {
                        // Cache 有值，但是這個 Cache 內保留的是此 Key所有的 Score Member Pair
                        // 因為 LCPS 的 Return 介面是 Map<String, String> 所以要再轉回去 Map<byte[], byte[]>

                        zrangebyscoreList = new MemberScorePairProcessing(
                                convertStringMapToByteArrayMap((Map<String, String>) LCPSResult)
                        ).sortedByScore(false).getMemberListByScore(memberScoreVO,inx,size);

                        // 這裡的處理是要把 Score 先從 byteArray 轉回 int 再轉回 String 再轉回 byte[] 這樣顯示才會正常
                        // 不然會出現亂碼，舉例來說原本要顯示 "1" 的 字串會變成 "\x00\x00\x00\x01"
                        if (keyword.equals(RockdisProtocol.Keyword.WITHSCORES)) {
                            for (int i = 1; i < zrangebyscoreList.size(); i += 2) {
                                zrangebyscoreList.set(i, Integer.toString(byteArrayToInt(zrangebyscoreList.get(i))).getBytes());
                            }
                        }

                    } else {
                        // 因為 Cache沒有值，所以去 RocksDB後端拿看看有沒有值
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put("rockscommand", "zrangebyscore");

                        zrangebyscoreList = new MemberScorePairProcessing(
                                convertStringMapToByteArrayMap(
                                        request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap)
                                )
                        ).sortedByScore(false).getMemberListByScore(memberScoreVO,inx,size);
                        // 這裡的處理是要把 Score 先從 byteArray 轉回 int 再轉回 String 再轉回 byte[] 這樣顯示才會正常
                        // 不然會出現亂碼，舉例來說原本要顯示 "1" 的 字串會變成 "\x00\x00\x00\x01"
                        if (keyword.equals(RockdisProtocol.Keyword.WITHSCORES)) {
                            for (int i = 1; i < zrangebyscoreList.size(); i += 2) {
                                zrangebyscoreList.set(i, Integer.toString(byteArrayToInt(zrangebyscoreList.get(i))).getBytes());
                            }
                        }
                    }
                } else {
                    // 沒有啟動 Cache System，直接跟 RocksDB取值
                    try {
                        zrangebyscoreList = rockdisInstance.zrangebyscore(
                                key.toString().getBytes(),
                                roundDoubleNumberToInt(minDoubleScore),
                                roundDoubleNumberToInt(maxDoubleScore));
                    } catch (RocksDBException e) {
                        e.printStackTrace();
                    }
                }

                return array(ArithmeticUtil.byteArrayListToRedisTokenList(zrangebyscoreList));
            } else {
                return error("Auth Failed!");
            }
        } catch (Exception e) {
            // 避免 process 因為 exception而被卡住，
            // 因為前端需要接收一個 resp return，所以遇到exception 就直接回傳 error
            logger.error(" 執行失敗，Request => {} \n 詳細 stack traced => ", request, e);
            return error("執行失敗，請查看 error log");
        }
    }

    private boolean isParameterValid(int requestLength) {
        return requestLength >= 4;
    }
}
