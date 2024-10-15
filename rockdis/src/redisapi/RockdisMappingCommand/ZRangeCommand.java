package redisapi.RockdisMappingCommand;

import redisapi.Util.CollectionUtil;
import rocksdbapi.Rockdis.Models.MemberScoreVO;
import rocksdbapi.Rockdis.Rockdis;
import redisapi.Util.ArithmeticUtil;
import redisapi.Util.ChannelStatusUtil;
import com.github.tonivade.resp.annotation.Command;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.protocol.RedisToken;
import com.github.tonivade.resp.protocol.SafeString;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rocksdbapi.Rockdis.RockdisProtocol;
import rocksdbapi.Rockdis.Utils.MemberScorePairProcessing;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static com.github.tonivade.resp.protocol.RedisToken.error;


import static redisapi.Util.CollectionUtil.convertStringMapToByteArrayMap;
import static rocksdbapi.Rockdis.Utils.RockdisMemberScoreOperation.*;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/6
 */

@Command("zrange")
public class ZRangeCommand implements RespCommand {

    private static final Logger logger = LoggerFactory.getLogger(ZRangeCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {

            if (!isParameterValid(request.getLength())) {
                return error("ERR wrong number of arguments for 'zrange' command");
            }

            if (ArithmeticUtil.checkParameter(request) != null) {
                return ArithmeticUtil.checkParameter(request);
            }

            SafeString key = request.getParam(1);
            SafeString start = request.getParam(2);
            SafeString stop = request.getParam(3);
            RockdisProtocol.Keyword keyword = null;

            if (request.getParam(4) != null && request.getParam(4).toString().equalsIgnoreCase("WITHSCORES")) {
                keyword = RockdisProtocol.Keyword.WITHSCORES;
            } else {
                keyword = RockdisProtocol.Keyword.NORMAL;
            }

            List<byte[]> zrangeList = new ArrayList<>();


            if (ChannelStatusUtil.isAuthorized(request)) {
                Rockdis rockdisInstance = request.getServerContext().getSecondRockdisInstance();

                // 把參數包裝成 MemberScoreVO
                MemberScoreVO memberScoreVO = new MemberScoreVO(key.getBytes(),
                        Integer.parseInt(start.toString()),
                        Integer.parseInt(stop.toString()),
                        keyword
                );

                logger.debug(" [ZRANGE Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

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

                        zrangeList = new MemberScorePairProcessing(
                                convertStringMapToByteArrayMap((Map<String, String>) LCPSResult)
                        ).sortedByScore(false).getMemberListWithScore(memberScoreVO);

                        // 這裡的處理是要把 Score 先從 byteArray 轉回 int 再轉回 String 再轉回 byte[] 這樣顯示才會正常
                        // 不然會出現亂碼，舉例來說原本要顯示 "1" 的 字串會變成 "\x00\x00\x00\x01"
                        if (keyword.equals(RockdisProtocol.Keyword.WITHSCORES)) {
                            for (int i = 1; i < zrangeList.size(); i += 2) {
                                zrangeList.set(i, Integer.toString(byteArrayToInt(zrangeList.get(i))).getBytes());
                            }
                        }
                    } else {
                        // 因為 Cache沒有值，所以去 RocksDB後端拿看看有沒有值
                        NavigableMap<String, String> fieldValueMap = new ConcurrentSkipListMap<>();
                        fieldValueMap.put("rockscommand", "zrange");

                        zrangeList = new MemberScorePairProcessing(
                                convertStringMapToByteArrayMap(
                                        request.getServerContext().getLargeCachePoolSystem().putToLCPS(key.toString(), fieldValueMap)
                                )
                        ).sortedByScore(false).getMemberListWithScore(memberScoreVO);
                        // 這裡的處理是要把 Score 先從 byteArray 轉回 int 再轉回 String 再轉回 byte[] 這樣顯示才會正常
                        // 不然會出現亂碼，舉例來說原本要顯示 "1" 的 字串會變成 "\x00\x00\x00\x01"
                        if (keyword.equals(RockdisProtocol.Keyword.WITHSCORES)) {
                            for (int i = 1; i < zrangeList.size(); i += 2) {
                                zrangeList.set(i, Integer.toString(byteArrayToInt(zrangeList.get(i))).getBytes());
                            }
                        }
                    }
                } else {
                    // 沒有啟動 Cache System，直接跟 RocksDB取值
                    try {
                        if (request.getLength() == 4) {
                            zrangeList = rockdisInstance.zrange(key.toString().getBytes(), Integer.parseInt(start.toString()), Integer.parseInt(stop.toString()));
                        } else if (request.getLength() == 5) {
                            List<byte[]> returnedList = rockdisInstance.zrangeWithScores(
                                    key.toString().getBytes(),
                                    Integer.parseInt(start.toString()),
                                    Integer.parseInt(stop.toString()));

                            for (int i = 0; i < returnedList.size(); i++) {
                                if (i % 2 != 0) {
                                    // todo 這裡利用 byteArrayToInt 來轉換 byte[] to int，這裡應該是解亂碼的方法
                                    zrangeList.add(Integer.toString(byteArrayToInt(returnedList.get(i))).getBytes());
                                } else {
                                    zrangeList.add(returnedList.get(i));

                                }
                            }
                        }
                    } catch (RocksDBException exception) {
                        return error(exception.toString());

                    }
                }

                return array(ArithmeticUtil.byteArrayListToRedisTokenList(zrangeList));
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
