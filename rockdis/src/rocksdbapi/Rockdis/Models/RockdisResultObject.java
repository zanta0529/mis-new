package rocksdbapi.Rockdis.Models;

import rocksdbapi.Rockdis.RockdisProtocol.Command;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RockdisResultObject {

    private Command command;
    private byte[] result;
    private Map<byte[], byte[]> fieldValueMap;
    private List<byte[]> resultList = new LinkedList<>();
    private Integer intValue;
    private Boolean executeSucceed;
    private Integer memberCount;

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public Map<byte[], byte[]> getFieldValueMap() {
        return fieldValueMap;
    }

    public void setFieldValueMap(Map<byte[], byte[]> fieldValueMap) {
        this.fieldValueMap = fieldValueMap;
    }

    public List<byte[]> getResultList() {
        return resultList;
    }

    public void setResultList(List<byte[]> resultList) {
        this.resultList = resultList;
    }


    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Boolean isExecuteSucceed() {
        return executeSucceed;
    }

    public void setExecuteSucceed(Boolean executeSucceed) {
        this.executeSucceed = executeSucceed;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }
}
