package rocksdbapi.Rockdis;

import org.rocksdb.RocksDBException;

public interface BinaryRockdisTransactionCommands {

    void multi() throws RocksDBException;
    void exec() throws RocksDBException;
    byte[] getForUpdate(byte[] key) throws RocksDBException;
    void close() throws RocksDBException;
    void rollback() throws RocksDBException;

}
