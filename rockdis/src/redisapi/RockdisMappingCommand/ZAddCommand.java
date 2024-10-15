package redisapi.RockdisMappingCommand;

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

import java.util.LinkedList;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.github.tonivade.resp.protocol.RedisToken.*;
import static redisapi.Util.ArithmeticUtil.checkScoreIsPositiveInteger;

/**
 * @author kkw
 * @project redis-practise
 * @date 2020/11/5
 */

@Command("zadd")
public class ZAddCommand implements RespCommand {

    private static final Logger logger = LoggerFactory.getLogger(ZAddCommand.class);

    @Override
    public RedisToken execute(Request request) {

        try {
//        System.out.println("Score : " +request.getParam(2));
            Rockdis rockdisInstance = request.getServerContext().getRockdisInstance();

            if (!isParameterValid(request.getLength())) {
                return error("ERR wrong number of arguments for 'zadd' command");
            }

            if (!checkScoreIsPositiveInteger(request)) {
                return error("ERR Score's value is not an positive integer.");
            }

            if (ChannelStatusUtil.isAuthorized(request)) {

                logger.debug(" [ZADD Command triggered] Cache System Status : " + request.getServerContext().getCacheSystemStatus());

                // 遇到 flushall 正在移轉 Cache
                while (request.getServerContext().getLargeCachePoolSystem().transitCache.get()) {
//                System.out.println("遇到 flushall 正在移轉 Cache");
                    continue;
                }

                SafeString key = request.getParam(1);
                Integer returnCount = 0;

//            System.out.println("接收到 ZADD 指令，key是 : " + key.toString());


                if (request.getServerContext().getCacheSystemStatus()) {
                    logger.debug("Enable Cache System.");

                    if (!request.getServerContext().getSafemode()) {

                        request.getServerContext().getLargeCachePoolSystem().putToLCPS(
                                request.getParam(1).toString(),
                                getMemberScoreMap(request));

                    } else {
                        // 啟動 Cache System 但是進入 SafeMode，所有的資料要寫到 Waiting Cache
                        // 等待 SafeMode 解除後從 Waiting Cache 把資料轉移到 Caffeine Cache
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(
                                request.getParam(1).toString(),
                                getMemberScoreMap(request)
                        );

                    }
                } else {
                    if (!request.getServerContext().getSafemode()) {
                        // 沒有啟動 Cache System，所以直接寫入RocksDB
                        try {
                            List<MemberScoreVO> memberScoreVOList = getMemberScoreList(request);
                            returnCount = rockdisInstance.zadd(memberScoreVOList);

                        } catch (NumberFormatException exception) {
                            return RedisToken.error(exception.toString());
                        } catch (RocksDBException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // todo
                        // 沒有啟動Cache 而且已經進入 SafeMode，所有的資料要寫到 Waiting Cache
                        request.getServerContext().getLargeCachePoolSystem().putToWaitingCache(
                                request.getParam(1).toString(),
                                getMemberScoreMap(request)
                        );
                    }
                }
                return integer(returnCount);
            } else {
                return error("Auth Failed");
            }
        } catch (Exception e) {
            // 避免 process 因為 exception而被卡住，
            // 因為前端需要接收一個 resp return，所以遇到exception 就直接回傳 error
            logger.error(" 執行失敗，Request => {} \n 詳細 stack traced => ", request, e);
            return error("執行失敗，請查看 error log");
        }
    }

    /**
     * 此方法將使用者透過 redis client 送進來的 zadd 指令的 Score, Member 資料轉換成 Map
     * 舉例來說使用者輸入 zadd key 1 "one" 2 "two"
     * 會被轉換成下列的 Map
     * ("one", "1")
     * ("two", "2")
     * ("rockscommand", "zadd")
     *
     * @param request
     * @return
     */
    private NavigableMap<String, String> getMemberScoreMap(Request request) {
        NavigableMap<String, String> memberScoreMap = new ConcurrentSkipListMap<>();
        int requestLength = request.getLength();
        for (int i = 2; i < requestLength - 1; i += 2) {

//            System.out.println("ZADD Command 被呼叫，其 Request key : " + request.getParam(1).toString() + ", Request member : " + request.getParam(i).toString() + ", Request score : " + request.getParam(i + 1).toString());

            if (request.toString().contains("2330")) {
                logger.debug("Request key : {}, Request field : {}, request value : {}",
                        request.getParam(1).toString(),
                        request.getParam(i).toString(),
                        request.getParam(i + 1).toString());
            }
            memberScoreMap.put(request.getParam(i+1).toString(),
                    String.valueOf((int)Math.round(ArithmeticUtil.SafeStringToDouble(request.getParam(i))))
            );
        }
        memberScoreMap.put("rockscommand", "zadd");
        return memberScoreMap;
    }



    private List<MemberScoreVO> getMemberScoreList(Request request) throws NumberFormatException {
        List<MemberScoreVO> memberScoreVOList = new LinkedList<>();
        int requestLength = request.getLength();
        for (int i = 2; i < requestLength - 1; i += 2) {
            logger.debug("Request Score : {}, request Member : {}",
                    request.getParam(i).toString(), request.getParam(i + 1).toString());

            // TODO why we have this piece of dirty code here? Because currently we only support Integer ZADD
            //  So we have to convert String to Double, and then convert Double into Integer.
            //  Maybe We can take this feature into consideration.

            MemberScoreVO memberScoreVO = new MemberScoreVO(
                    request.getParam(1).getBytes(),
                    (int)Math.round(ArithmeticUtil.SafeStringToDouble(request.getParam(i))),
                    request.getParam(i + 1).getBytes());
//            ScoreMemberVO scoreMemberVO = new ScoreMemberVO((int)Math.round(ArithmeticUtil.SafeStringToDouble(request.getParam(i))),
//                    request.getParam(i + 1).toString());
            memberScoreVOList.add(memberScoreVO);
        }
        return memberScoreVOList;
    }

    private boolean isParameterValid(int requestLength) {
        // zadd 的參數撇除 timestamp 與 key 之後，必須是成對的
        return requestLength > 2 && (requestLength - 2) % 2 == 0;
    }
}
