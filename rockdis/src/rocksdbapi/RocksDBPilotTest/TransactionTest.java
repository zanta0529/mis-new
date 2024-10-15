package rocksdbapi.RocksDBPilotTest;


import rocksdbapi.Rockdis.Rockdis;
import org.rocksdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TransactionTest {

    private static Logger logger = LoggerFactory.getLogger(TransactionTest.class);

    public static void main(String[] args) throws RocksDBException {

        Options options = new Options().setCreateIfMissing(true);
        TransactionDBOptions txnDbOptions = new TransactionDBOptions();
        TransactionDB tdb = TransactionDB.open(options, txnDbOptions, "/tmp/transactionTest");

        WriteOptions writeOptions = new WriteOptions();
        ReadOptions readOptions = new ReadOptions();

        Transaction txn = tdb.beginTransaction(writeOptions);
        Transaction txn2 = tdb.beginTransaction(writeOptions);
        final byte key[] = "firstKey".getBytes(UTF_8);
        final byte value[] = "firstValue".getBytes(UTF_8);
        final byte value2[] = "conflict".getBytes(UTF_8);

        logger.info("The original value of key: {}, value is {}", new String(key),  txn.get(readOptions, key));
        logger.info("[Transaction 1] start to put key : {}, value : {}", new String(key), new String(value));
        txn.put(key, value);
        logger.info("[Transaction 1] Get Value of Key just put ahead. Value is : {}", txn.get(readOptions, key));
//        byte[] result = txn.getForUpdate(readOptions, key, true);
//        logger.info("[Transaction 1] Execute getForUpdate() Value is : {}", new String(result));

        /*
        Transaction 2 操作到與 Transaction 1 同一個 key 導致 LockTimeOut
         */
        logger.info("New a transaction, the name is [Transaction 2].");
        try {
            logger.info("[[Transaction 2] Use put method to put key: {}, value: {}", new String(key), new String(value2));
            txn2.put(key, value2);
            byte[] result2 = txn2.getForUpdate(readOptions, key, true);
            logger.info("[Transaction 2] Execute getForUpdate() : {}", new String(result2));
            logger.info("[Transaction 2] Value in transaction : {}", new String(result2));
        } catch (RocksDBException expection){
            expection.printStackTrace();
            logger.error("[Transaction 2] Get error code : {}", expection.getStatus().getCode());
            logger.error("[Transaction 2] Time out status code : {}", Status.Code.TimedOut);
            logger.error("[Transaction 2] The reason why got error is it seems you operate the same key");
            logger.error("[Transaction 2] but \"transaction 1\" has not commit its transaction.");
        }

        logger.warn("[Tips] So, we knew before another transaction operate the same key, you should commit the key first.");
        logger.warn("[Tips] How about use get() to lookup key in transaction 2.");

        logger.info("[Transaction 2] Use get() to look up key : {}, the value is : {}", new String(key), txn2.get(readOptions, key));
        logger.warn("[Tips] You will get \"null\" value");
        logger.warn("[Tips] In transaction 2, you cannot get the newest value if transaction 1 do not commit its operation.");
        /*
        Secondary to read
         */
        Rockdis secondaryDB = new Rockdis("/tmp/transactionTest", "/tmp/transactionTest-second");
        logger.warn("[Tips] If you use secondary db, you still get the same value of null");
        logger.info("[Secondary DB] Get value from secondaryDB : {}", secondaryDB.get(key));


        final Map<Long, TransactionDB.KeyLockInfo> lockStatus = tdb.getLockStatusData();
        logger.info("[Transaction 1] LockStatus : {}", lockStatus.get(lockStatus.size()));

        /*
        Transaction commit, and trigger secondary to read
         */
        logger.info("[Transaction 1] Now, Transaction 1 has been committed.");
        txn.commit();
        secondaryDB.tryCatchUpWithPrimary();
        logger.info("[Secondary DB] Get value from secondaryDB : {}", new String(secondaryDB.get(key)));
        logger.warn("[Tips] Now, You can see value which transaction 1 committed.");


        logger.info("[Transaction 2] Transaction 2 Start to put new data.");
        logger.info("[Transaction 2] Key : {}, Value : {}", new String(key), new String(value2));
        try {
            txn2.put(key, value2);
            byte[] result2 = txn2.getForUpdate(readOptions, key, true);
            logger.info("[Transaction 2] Execute getForUpdate() : {}", new String(result2));
            logger.info("[Transaction 2] Value in transaction : {}", new String(result2));
            txn2.commit();
        } catch (RocksDBException expection){
            expection.printStackTrace();
            logger.error("Lock TimeOut. It seems you operate the same key");
        }

        secondaryDB.tryCatchUpWithPrimary();
        logger.info("[Secondary DB] Get value from secondaryDB : {}", new String(secondaryDB.get(key)));
        logger.info("[Tips] We can see the value has changed.");



    }

}
