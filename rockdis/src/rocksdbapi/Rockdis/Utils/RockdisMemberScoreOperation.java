package rocksdbapi.Rockdis.Utils;

public class RockdisMemberScoreOperation {

    public static boolean isScorePositive(Integer number) {
        return number >= 0;
    }

    public static String paddingZeroBecomePositiveIntegerSize(Integer number) {
        return String.format("%010d", number);
    }

    public static String givePositiveMarker(String paddedString) {
        return "/"+paddedString;
    }

    public static int sizeOfString(String inputString) {
        return inputString.length();
    }

    public static boolean isIntegerSizeString(String inputString) {
        return sizeOfString(inputString) == 11;
    }

    public static boolean isStartScoreSmallerThanEndScore(Integer startScore, Integer endScore) {
        return startScore < endScore;
    }

    public static boolean isStartScoreLagerThanEndScore(Integer startScore, Integer endScore) {
        return startScore > endScore;
    }

    public static byte[] intToByteArray(int a)
    {
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    public static int byteArrayToInt(byte[] b)
    {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

//    /**
//     * Given a member - score Map, then sorted by score value(which is byte array but need to convert to Integer)
//     * If two score value is the same, then use lexicographer to determine which is first and which is last.
//     * You can choose ASC or DSC of the list
//     * @param inputMap
//     * @param isReverse
//     * @return list
//     */
//    public static List<Map.Entry<byte[], byte[]>> memberScoreListSortedByScore(Map<byte[], byte[]> inputMap, boolean isReverse) {
//        // Compare score value
//        List<Map.Entry<byte[], byte[]>> list = new ArrayList<Map.Entry<byte[], byte[]>>(inputMap.entrySet());
//        Collections.sort(list, new Comparator<Map.Entry<byte[], byte[]>>() {
//            @Override
//            public int compare(Map.Entry<byte[], byte[]> o1, Map.Entry<byte[], byte[]> o2) {
//
//                // Convert byte[] to int
//                int o1Value = byteArrayToInt(o1.getValue());
//                int o2Value = byteArrayToInt(o2.getValue());
//
//                if (o1Value != o2Value) {
//
//                    if (isReverse) {
//                        return o2Value - o1Value;
//                    } else {
//                        return o1Value - o2Value;
//                    }
//
//                } else {
//                    // If score is the same, we need to compare the member's lexicographer
//                    for (int i = 0, j = 0; i < o1.getKey().length && j < o2.getKey().length; i++, j++) {
//                        int a = (o1.getKey()[i] & 0xff);
//                        int b = (o2.getKey()[j] & 0xff);
//
//                        if (a != b) {
//                            if (isReverse) {
//                                return b - a;
//                            } else {
//                                return a - b;
//                            }
//                        }
//                    }
//                    if (isReverse) {
//                        return o2.getKey().length - o1.getKey().length;
//                    } else {
//                        return o1.getKey().length - o2.getKey().length;
//                    }
//                }
//            }
//        });
//
//        return list;
//    }

    /*public static List<byte[]> retrieveList(List<Map.Entry<byte[], byte[]>> list, Integer positionStart, Integer positionEnd, boolean isReverse) {

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
    }*/
}
