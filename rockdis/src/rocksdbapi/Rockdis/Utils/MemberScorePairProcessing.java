package rocksdbapi.Rockdis.Utils;

import rocksdbapi.Rockdis.Models.MemberScoreVO;
import rocksdbapi.Rockdis.RockdisProtocol;

import java.util.*;

/**
 * @author kkw
 * @project rocksdb
 * @date 2020/11/20
 */

public class MemberScorePairProcessing {

    private Map<byte[], byte[]> haspMap = new HashMap<>();
    private List<Map.Entry<byte[], byte[]>> memberScoreList = new ArrayList<>();

    /**
     * Given a Member-Score Map as an initial parameter.
     * @param haspMap
     */
    public MemberScorePairProcessing(Map<byte[], byte[]> haspMap) {
        this.haspMap = haspMap;
    }

    /**
     * Given a List that contain Member-Score Map as an initial parameter.
     * @param memberScoreList
     */
    public MemberScorePairProcessing(List<Map.Entry<byte[], byte[]>> memberScoreList) {
        this.memberScoreList = memberScoreList;
    }

    /**
     * Sorted hashMap by Score value(which is byte array but need to convert to Integer)
     * If two score value is the same, then use lexicographer to determine which is first and which is last.
     * You can choose ASC or DSC of the list
     *
     * @param isReverse
     * @return list
     */
    public MemberScorePairProcessing sortedByScore(boolean isReverse) {
        // Compare score value
        List<Map.Entry<byte[], byte[]>> list = new ArrayList<Map.Entry<byte[], byte[]>>(haspMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<byte[], byte[]>>() {
            @Override
            public int compare(Map.Entry<byte[], byte[]> o1, Map.Entry<byte[], byte[]> o2) {

                // Convert byte[] to int
                int o1Value = RockdisMemberScoreOperation.byteArrayToInt(o1.getValue());
                int o2Value = RockdisMemberScoreOperation.byteArrayToInt(o2.getValue());

                if (o1Value != o2Value) {

                    if (isReverse) {
                        return o2Value - o1Value;
                    } else {
                        return o1Value - o2Value;
                    }

                } else {
                    // If score is the same, we need to compare the Member's lexicographer
                    for (int i = 0, j = 0; i < o1.getKey().length && j < o2.getKey().length; i++, j++) {
                        int a = (o1.getKey()[i] & 0xff);
                        int b = (o2.getKey()[j] & 0xff);

                        if (a != b) {
                            if (isReverse) {
                                return b - a;
                            } else {
                                return a - b;
                            }
                        }
                    }
                    if (isReverse) {
                        return o2.getKey().length - o1.getKey().length;
                    } else {
                        return o1.getKey().length - o2.getKey().length;
                    }
                }
            }
        });

        this.memberScoreList = list;
        return this;
    }

    public List<Map.Entry<byte[], byte[]>> getMemberScoreList() {
        return this.memberScoreList;
    }

    public List<byte[]> getMemberList(MemberScoreVO memberScoreVO, boolean isReverse) {
        return getMemberList(memberScoreVO.getScoreRangeStart(), memberScoreVO.getScoreRangeEnd(), isReverse);
    }


    public List<byte[]> getMemberList(Integer positionStart, Integer positionEnd, boolean isReverse) {

        List<Map.Entry<byte[], byte[]>> list = this.memberScoreList;
        List<byte[]> returnedList = new ArrayList<>();
        if (isReverse) {
            Integer start = 0;
            if (positionStart < -list.size()) {
                start = list.size() -1 ;
            } else {
                start = (list.size() - 1 - positionStart) % list.size();
            }
            Integer end =0;
            if (positionEnd >= list.size()) {
                end = 0;
            } else if (positionEnd < -list.size()) {
                end = list.size();
            } else {
                end = (list.size() - 1 - positionEnd) % list.size();
            }
            for (int i = start; i > start - (start - end) - 1; i--) {
                returnedList.add(list.get(i).getKey());
            }

        } else {
            Integer start = 0;
            if (positionStart >= list.size()) {
                start = positionStart;
            } else if (positionStart < -list.size()){
                start = 0;
            }
            else {
                start = (list.size() + positionStart) % list.size();
            }
            Integer end = 0;
            if (positionEnd >= list.size()) {
                end = list.size() -1;
            } else  {
                end = (list.size() + positionEnd) % list.size();
            }
            for (int i = start; i < start + (end - start) + 1; i++) {
                returnedList.add(list.get(i).getKey());
            }
        }
        return returnedList;
    }


    public List<byte[]> getMemberListWithScore(MemberScoreVO memberScoreVO) {

        Integer start = memberScoreVO.getScoreRangeStart();
        Integer end = memberScoreVO.getScoreRangeEnd();
        RockdisProtocol.Keyword keyword = memberScoreVO.getKeyword();

        List<Map.Entry<byte[], byte[]>> list = this.memberScoreList;
        List<byte[]> zrangeReturnList = new ArrayList<>();

        // Determine how many record should return
        Integer positionStart = start;
        Integer positionEnd = end;

        if (start < 0) {
            positionStart = convertNegativePositionToPositivePosition(list.size(), start);
        }
        if (end < 0) {
            positionEnd = convertNegativePositionToPositivePosition(list.size(), end);
        }
        if (end > list.size() - 1) {
            positionEnd = list.size() - 1;
        }


        if (list.size() == 0) {
            return zrangeReturnList;
        }

        for (int i = positionStart; i <= positionEnd; i++) {
            zrangeReturnList.add(list.get(i).getKey());
            if (keyword.equals(RockdisProtocol.Keyword.WITHSCORES)) {
                zrangeReturnList.add(list.get(i).getValue());
            }
        }
        return zrangeReturnList;
    }

    public List<byte[]> getMemberListByScore(MemberScoreVO memberScoreVO) {
        List<byte[]> zrangeByScoreReturnList = new ArrayList<>();
        List<Map.Entry<byte[], byte[]>> sortedList = this.memberScoreList;
        // Determine how many record should return
        Integer start = memberScoreVO.getScoreRangeStart();
        Integer end = memberScoreVO.getScoreRangeEnd();
        RockdisProtocol.Keyword keyword = memberScoreVO.getKeyword();

        for (int i = 0; i < sortedList.size(); i++) {

            int valueFromDB = RockdisMemberScoreOperation.byteArrayToInt(sortedList.get(i).getValue());

            // Search Head
            if (valueFromDB >= start) {
                if (valueFromDB <= end) {
                    zrangeByScoreReturnList.add(sortedList.get(i).getKey());
                    if (keyword.equals(RockdisProtocol.Keyword.WITHSCORES)) {
                        zrangeByScoreReturnList.add(sortedList.get(i).getValue());
                    }
                } else {
                    break;
                }
            }
        }
        return zrangeByScoreReturnList;
    }

    public List<byte[]> getMemberListByScore(MemberScoreVO memberScoreVO,int inx, int size) {
    	if(inx==-1 && size == -1) return getMemberListByScore(memberScoreVO);
        List<byte[]> zrangeByScoreReturnList = new ArrayList<>();
        List<Map.Entry<byte[], byte[]>> sortedList = this.memberScoreList;
        // Determine how many record should return
        Integer start = memberScoreVO.getScoreRangeStart();
        Integer end = memberScoreVO.getScoreRangeEnd();
        RockdisProtocol.Keyword keyword = memberScoreVO.getKeyword();
        int count = 0;
        int insert = 0;
        for (int i = 0; i < sortedList.size(); i++) {

            int valueFromDB = RockdisMemberScoreOperation.byteArrayToInt(sortedList.get(i).getValue());

            // Search Head
            if (valueFromDB >= start) {
                if (valueFromDB <= end) {
                	count++;
                	if(count<=inx) continue;
                	if(insert>=size) continue;
                	insert++;
                    zrangeByScoreReturnList.add(sortedList.get(i).getKey());
                    if (keyword.equals(RockdisProtocol.Keyword.WITHSCORES)) {
                        zrangeByScoreReturnList.add(sortedList.get(i).getValue());
                    }
                } else {
                    break;
                }
            }
        }
        return zrangeByScoreReturnList;
    }    
    
    public List<byte[]> getMemberListByScoreDSC(MemberScoreVO memberScoreVO) {
        List<byte[]> zrangeByScoreReturnList = new ArrayList<>();
        List<Map.Entry<byte[], byte[]>> sortedList = this.memberScoreList;

        // Determine how many record should return
        Integer start = memberScoreVO.getScoreRangeStart();
        Integer end = memberScoreVO.getScoreRangeEnd();

        for (int i = 0; i < sortedList.size(); i++) {

            int valueFromDB = RockdisMemberScoreOperation.byteArrayToInt(sortedList.get(i).getValue());

            // Search Head
            if (valueFromDB <= start) {
                if (valueFromDB >= end) {
                    zrangeByScoreReturnList.add(sortedList.get(i).getKey());
                } else {
                    break;
                }
            }
        }
        return zrangeByScoreReturnList;
    }

    private static int convertNegativePositionToPositivePosition(int listSize, Integer position) {

        if (position < -listSize) {
            return 0;
        } else if (position < 0) {
            return listSize + position;
        } else {
            return position;
        }
    }


}
