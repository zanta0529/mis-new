package rocksdbapi.Rockdis.Models;

import org.rocksdb.*;
import rocksdbapi.Rockdis.RockdisProtocol.Command;

public class RocksDBTransactionDataExchange {

    private TransactionDB transactionDB;
    private Transaction transaction;
    private RocksIterator iterator;
    private ReadOptions readOptions;
    private WriteOptions writeOptions;
    private Command command;

    public RocksDBTransactionDataExchange(Transaction transaction, Command command){
        this.transaction = transaction;
        this.command = command;
    }

    public RocksDBTransactionDataExchange(Transaction transaction, ReadOptions readOptions, Command command){
        this.transaction = transaction;
        this.readOptions = readOptions;
        this.command = command;
    }

    public RocksDBTransactionDataExchange(Transaction transaction, ReadOptions readOptions, Command command, RocksIterator iterator){
        this.transaction = transaction;
        this.readOptions = readOptions;
        this.command = command;
        this.iterator = iterator;
    }

    public Command getCommand() {
        return command;
    }

    public TransactionDB getTransactionDB() {
        return transactionDB;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public ReadOptions getReadOptions() {
        return readOptions;
    }

    public WriteOptions getWriteOptions() {
        return writeOptions;
    }

    public RocksIterator getIterator() {
        return iterator;
    }
}
