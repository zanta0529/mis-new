package rocksdbapi.StreamPackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author kkw
 * @project rocksdb
 * @date 2020/11/19
 */

public class ListProcessor {

    private List<Integer> lastList = null;

    public ListProcessor(List<Integer> inputList) {
        this.lastList = inputList;
    }

    public ListProcessor inOrder() {
        Collections.sort(this.lastList);
        return this;
    }

    public ListProcessor multiply(int multiply) {
        List<Integer> returnList = new ArrayList<>();
        for (Integer number : lastList) {
            returnList.add(number * multiply);
        }
        this.lastList = returnList;
        return this;
    }

    public List<Integer> getList() {
        return this.lastList;
    }

    public static void main(String[] args) {
        List<Integer> inputList = Arrays.asList(2, 5, 3, 7, 8);
        List<Integer> processedList = new ListProcessor(inputList).inOrder().multiply(2).getList();
        for (Integer element : processedList) {
            System.out.println(element);
        }
    }
}
