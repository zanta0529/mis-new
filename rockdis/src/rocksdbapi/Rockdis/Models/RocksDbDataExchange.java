package rocksdbapi.Rockdis.Models;

import rocksdbapi.Rockdis.RockdisProtocol.Command;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;
import org.rocksdb.WriteOptions;

public class RocksDbDataExchange {

    private Command command;
    private RocksDB rocksDB;
    private ReadOptions readOptions;
    private WriteOptions writeOptions;
    private RocksIterator iterator;

    public RocksDbDataExchange(){

    }

    public RocksDbDataExchange(RocksDB rocksDB, Command command) {
        this.command = command;
        this.rocksDB = rocksDB;
    }

    public RocksDbDataExchange(RocksDB rocksDB, Command command, RocksIterator iterator) {
        this.command = command;
        this.rocksDB = rocksDB;
        this.iterator = iterator;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public RocksDB getRocksDB() {
        return rocksDB;
    }

    public void setRocksDB(RocksDB rocksDB) {
        this.rocksDB = rocksDB;
    }

    public ReadOptions getReadOptions() {
        return readOptions;
    }

    public void setReadOptions(ReadOptions readOptions) {
        this.readOptions = readOptions;
    }

    public WriteOptions getWriteOptions() {
        return writeOptions;
    }

    public void setWriteOptions(WriteOptions writeOptions) {
        this.writeOptions = writeOptions;
    }

    public RocksIterator getIterator() {
        return iterator;
    }

    public void setIterator(RocksIterator iterator) {
        this.iterator = iterator;
    }
}
