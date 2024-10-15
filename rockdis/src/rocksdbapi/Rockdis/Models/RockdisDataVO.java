package rocksdbapi.Rockdis.Models;

import java.util.Map;

public class RockdisDataVO {

    private byte[] key;
    private byte[] filed;
    private byte[] value;
    private byte[][] fieldArray;
    private Map<byte[], byte[]> fieldValueMap;
    private int addend;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getFiled() {
        return filed;
    }

    public void setFiled(byte[] filed) {
        this.filed = filed;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public byte[][] getFieldArray() {
        return fieldArray;
    }

    public void setFieldArray(byte[][] fieldArray) {
        this.fieldArray = fieldArray;
    }

    public Map<byte[], byte[]> getFieldValueMap() {
        return fieldValueMap;
    }

    public void setFieldValueMap(Map<byte[], byte[]> fieldValueMap) {
        this.fieldValueMap = fieldValueMap;
    }

    public int getAddend() {
        return addend;
    }

    public void setAddend(int addend) {
        this.addend = addend;
    }
}
