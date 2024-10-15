package rocksdbapi.RocksDBPilotTest;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.util.Arrays;

public class deleteTest {

    public static void main(String[] args) throws RocksDBException {

        RocksDB rocksDB = RocksDB.open("/tmp/rocksdb-delete-test");
        rocksDB.put("books".getBytes(), "java".getBytes());
        System.out.println(new String(rocksDB.get("books".getBytes())));


        System.out.println("Delete Key");
        rocksDB.delete("books".getBytes());


        if ( rocksDB.get("books".getBytes()) != null) {
            System.out.println(new String(rocksDB.get("books".getBytes())));
        } else {
            System.out.println(" Value is null");
        }
    }
}
