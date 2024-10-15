package rocksdbapi.Rockdis.Utils;

import java.nio.ByteBuffer;
import java.util.LinkedHashSet;

/**
 * @author kkw
 * @project rocksdb
 * @date 2020/12/9
 */

public class ByteKeyHashSet extends LinkedHashSet<ByteBuffer> {

    private static final long serialVersionUID = -2702041216392736060L;

    public boolean add(byte[] key) {
        return super.add(ByteBuffer.wrap(key));
    }

    public boolean add(String key) {
        return super.add(ByteBuffer.wrap(key.getBytes()));
    }

    public boolean remove(byte[] key) {
        return super.remove(ByteBuffer.wrap(key));
    }

    public boolean remove(String key) {
        return super.remove(ByteBuffer.wrap(key.getBytes()));
    }

    public boolean contains(byte[] key) {
        return super.contains(ByteBuffer.wrap(key));
    }

    public boolean contains(String key) {
        return super.contains(ByteBuffer.wrap(key.getBytes()));
    }

//    public Iterator<byte[]> iterator() {
//        return super.map.keySet().iterator();
//    }
}
