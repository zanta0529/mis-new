package rocksdbapi.Rockdis;


import redisapi.DAO.ScoreMemberVO;
import redisapi.Util.CollectionUtil;
import rocksdbapi.Rockdis.Models.*;
import rocksdbapi.Rockdis.Params.RockdisParams;
import rocksdbapi.Rockdis.RockdisProtocol.Command;
import rocksdbapi.Rockdis.RockdisProtocol.Keyword;
import rocksdbapi.Rockdis.Utils.Converter;

import org.rocksdb.*;
import org.rocksdb.util.SizeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;


public class RockdisConnection {

    static {
        RocksDB.loadLibrary();
    }

    private Statistics stats = new Statistics();
    private Options options = new Options();
    private BlockBasedTableConfig table_options = new BlockBasedTableConfig();
    protected ReadOptions readOptions = new ReadOptions();
    protected WriteOptions writeOptions = new WriteOptions();
    protected RocksDB db = null;
    private RockdisParams.Type RocksDBType = RockdisParams.Type.PRIMARY;
    public static Logger logger = LoggerFactory.getLogger(RockdisConnection.class);
    private CompressionType compressionType = CompressionType.ZSTD_COMPRESSION;
    protected boolean enableTransaction = false;
    protected boolean isTransact = false;

    public AtomicLong iteratorCount = new AtomicLong(0);

    public AtomicLong getIteratorCount() {
        return iteratorCount;
    }

    public void decreaseIteratorCount() {
        iteratorCount.decrementAndGet();
    }

    /*
        Add Transaction DB
         */
    protected TransactionDB transactionDB = null;
    protected TransactionDBOptions transactionDBOptions = null;
    protected Transaction transaction = null;


    /*
    Three Constructure, determine primary or secondary
     */
    public RockdisConnection() {

    }

    public RockdisConnection(String db_path) throws RocksDBException {
        setUpTableOptions();
        setUpPrimaryReadOptions();
        setUpPrimaryOptions();
        setUpPrimaryDB(db_path);
    }

    public RockdisConnection(String db_path, CompressionType compressionType) throws RocksDBException {
        this.setCompressionType(compressionType);
        setUpTableOptions();
        setUpPrimaryReadOptions();
        setUpPrimaryOptions();
        setUpPrimaryDB(db_path);
    }

    public RockdisConnection(String db_path, String secondary_db_path) throws RocksDBException {
        //setUpTableOptions();
        //setUpSecondaryReadOptions();
        setUpSecondaryOptions();
        setUpSecondaryDB(db_path, secondary_db_path);
    }

    public RockdisConnection(String dbPath, boolean enableTransaction) throws RocksDBException {

        this.enableTransaction = enableTransaction;
        setUpTransactionReadOptions();
        setUpTransactionWriteOptions();
        setUpTransactionOptions();
        setUpTransactionDB(dbPath);
    }

    private void setUpTransactionOptions() {
        transactionDBOptions = new TransactionDBOptions();
        options = new Options().setCreateIfMissing(true);
    }

    private void setUpTransactionReadOptions() {
        readOptions = new ReadOptions();
    }

    private void setUpTransactionWriteOptions() {
        writeOptions = new WriteOptions();
    }


    protected RockdisResultObject sendCommand(final RockdisProtocol.Command command, byte[] params, byte[] params2) throws RocksDBException {

        if (command.equals(Command.SET)) {
            return RockdisProtocol.sendCommand(command, params, params2, db);
        } else if (command.equals(Command.HGET)){
        	try(RocksIterator iterator = getIterator()){
                return RockdisProtocol.sendCommand(command, params, params2, db, iterator, this);	
        	}
        } else {
            // 這裡不應該出現喔！
            return new RockdisResultObject();
        }
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, Integer score, byte[] member) throws RocksDBException {

        return sendZCommand(command, key, score, member, false);
    }

    protected RockdisResultObject sendZCommand(final Command command, List<MemberScoreVO> memberScoreVOList) throws RocksDBException {

        return sendZCommand(command, memberScoreVOList, false);
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, byte[] member) throws RocksDBException {

        return sendZCommand(command, key, member, false);
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, byte[]... memberArray) throws RocksDBException {

        return sendZCommand(command, false, key, memberArray);
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, int min, int max) throws RocksDBException {

        return sendZCommand(command, key, min, max, false);
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, Integer rangeStart, Integer rangeEnd, Keyword keyword) throws RocksDBException {
        return sendZCommand(command, key, rangeStart, rangeEnd, keyword, false);
    }

//    protected RockdisResultObject sendZCommand(final Command command, byte[] key, Integer rangeStart, Integer rangeEnd, final Keyword keyword) throws RocksDBException {
//        return sendZCommand(command, key, rangeStart, rangeEnd, keyword, false);
//    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, Integer rangeStart, Integer rangeEnd, Keyword keyword, boolean enableTransaction) throws RocksDBException {

        MemberScoreVO memberScoreVO = new MemberScoreVO(key, rangeStart, rangeEnd);
        memberScoreVO.setKeyword(keyword);

        if (enableTransaction) {
            RocksDBTransactionDataExchange rocksDBTransactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);

            return RockdisProtocol.sendTransactionCommand(rocksDBTransactionDataExchange, memberScoreVO);
        } else {
        	try(RocksIterator iterator = getIterator()){
                RocksDbDataExchange rocksDbDataExchange = new RocksDbDataExchange(db, command, iterator);

                return RockdisProtocol.sendCommand(rocksDbDataExchange, memberScoreVO, this);        		
        	}
        }
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, Integer score, byte[] member, boolean enableTransaction) throws RocksDBException {

        MemberScoreVO memberScoreVO = new MemberScoreVO(key, score, member);

        if (enableTransaction) {
            RocksDBTransactionDataExchange rocksDBTransactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);

            return RockdisProtocol.sendTransactionCommand(rocksDBTransactionDataExchange, memberScoreVO);
        } else {
            RocksDbDataExchange rocksDbDataExchange = new RocksDbDataExchange(db, command);

            return RockdisProtocol.sendCommand(rocksDbDataExchange, memberScoreVO, this);
        }
    }

    protected RockdisResultObject sendZCommand(final Command command, List<MemberScoreVO> memberScoreVOList, boolean enableTransaction) throws RocksDBException {

        if (enableTransaction) {
            // todo 暫時不研發 transaction
//            RocksDBTransactionDataExchange rocksDBTransactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);
//            return RockdisProtocol.sendTransactionCommand(rocksDBTransactionDataExchange, memberScoreVO);
            return null;
        } else {
            RocksDbDataExchange rocksDbDataExchange = new RocksDbDataExchange(db, command);

            return RockdisProtocol.sendCommand(rocksDbDataExchange, memberScoreVOList);
        }
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, byte[] member, boolean enableTransaction) throws RocksDBException {

        MemberScoreVO memberScoreVO = new MemberScoreVO(key, member);

        if (enableTransaction) {
            RocksDBTransactionDataExchange rocksDBTransactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);

            return RockdisProtocol.sendTransactionCommand(rocksDBTransactionDataExchange, memberScoreVO);
        } else {
            RocksDbDataExchange rocksDbDataExchange = new RocksDbDataExchange(db, command);

            return RockdisProtocol.sendCommand(rocksDbDataExchange, memberScoreVO, this);
        }
    }

    protected RockdisResultObject sendZCommand(final Command command, boolean enableTransaction, byte[] key, byte[]... memberArray) throws RocksDBException {

        List<MemberScoreVO> memberVOList = new ArrayList<>();
        for (byte[] member : memberArray) {
            MemberScoreVO memberScoreVO = new MemberScoreVO(key, member);
            memberVOList.add(memberScoreVO);
        }

        if (enableTransaction) {
            // transaction 暫時不做了
//            RocksDBTransactionDataExchange rocksDBTransactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);
//
//            return RockdisProtocol.sendTransactionCommand(rocksDBTransactionDataExchange, memberVOList);
            return null;
        } else {
            RocksDbDataExchange rocksDbDataExchange = new RocksDbDataExchange(db, command);

            return RockdisProtocol.sendCommand(rocksDbDataExchange, memberVOList);
        }
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, int min, int max, boolean enableTransaction) throws RocksDBException {

        MemberScoreVO memberScoreVO = new MemberScoreVO(key, min, max);

        if (enableTransaction) {
            RocksDBTransactionDataExchange rocksDBTransactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);

            return RockdisProtocol.sendTransactionCommand(rocksDBTransactionDataExchange, memberScoreVO);
        } else {
        	try(RocksIterator iterator = getIterator()){
                RocksDbDataExchange rocksDbDataExchange = new RocksDbDataExchange(db, command, iterator);

                return RockdisProtocol.sendCommand(rocksDbDataExchange, memberScoreVO, this);        		
        	}
        }
    }

    protected RockdisResultObject sendZCommand(final Command command, byte[] key, boolean enableTransaction) throws RocksDBException {
        MemberScoreVO memberScoreVO = new MemberScoreVO(key);

        if (enableTransaction) {
            RocksDBTransactionDataExchange rocksDBTransactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);
            return RockdisProtocol.sendTransactionCommand(rocksDBTransactionDataExchange, memberScoreVO);
        } else {
        	try(RocksIterator iterator = getIterator()){
                RocksDbDataExchange rocksDbDataExchange = new RocksDbDataExchange(db, command, iterator);

                return RockdisProtocol.sendCommand(rocksDbDataExchange, memberScoreVO, this);        		
        	}
        }
    }

//    final Command command, byte[] key, Integer rangeStart, Integer rangeEnd, final Keyword keyword

    protected RockdisResultObject sendZCommand(final Command command, byte[] key) throws RocksDBException {

        return sendZCommand(command, key, false);
    }

    protected RockdisResultObject sendTransactionCommand(final Command command, boolean enableTransaction, byte[] params) throws RocksDBException {
        if (enableTransaction) {

            RocksDBTransactionDataExchange transactionDataExchange = new RocksDBTransactionDataExchange(transaction, readOptions, command);
            RockdisDataVO dataVO = new RockdisDataVO();
            dataVO.setKey(params);
            return RockdisProtocol.sendTransactionCommand(transactionDataExchange, dataVO);
        } else {
            logger.error("[ERROR] System do not enable TRANSACTION mechanism.");
            return null;
        }
    }

    protected RockdisResultObject sendTransactionCommand(final Command command, boolean enableTransaction, byte[] param, byte[] param2, int param3) throws RocksDBException {
        if (enableTransaction) {
            RocksDBTransactionDataExchange transactionDataExchange = new RocksDBTransactionDataExchange(transaction, readOptions, command);
            RockdisDataVO dataVO = new RockdisDataVO();
            dataVO.setKey(param);
            dataVO.setFiled(param2);
            dataVO.setAddend(param3);
            return RockdisProtocol.sendTransactionCommand(transactionDataExchange, dataVO);
        } else {
            logger.error("[ERROR] System do not enable TRANSACTION mechanism.");
            return null;
        }
    }

    protected RockdisResultObject sendTransactionCommand(final Command command, boolean enableTransaction, byte[] params, byte[] params2) throws RocksDBException {
    	try(RocksIterator iterator = getIterator()){
        	RocksDBTransactionDataExchange transactionDataExchange = new RocksDBTransactionDataExchange(transaction, readOptions, command, iterator);
            RockdisDataVO dataVO = null;

            if (enableTransaction) {
                switch (command) {
                    case SET:
                        dataVO = new RockdisDataVO();
                        dataVO.setKey(params);
                        dataVO.setValue(params2);
                        break;

                    case HGET:
                        dataVO = new RockdisDataVO();
                        dataVO.setKey(params);
                        dataVO.setFiled(params2);
                        break;

                    default:
                }
            } else {
                logger.error("[ERROR] System do not enable TRANSACTION mechanism.");
                return null;
            }

            return RockdisProtocol.sendTransactionCommand(transactionDataExchange, dataVO);    		
    	}
    }

    protected RockdisResultObject sendTransactionCommand(final Command command, boolean enableTransaction, byte[] params, byte[]... params2) throws RocksDBException {
        if (enableTransaction) {
            RocksDBTransactionDataExchange transactionDataExchange = new RocksDBTransactionDataExchange(transaction, readOptions, command);
            RockdisDataVO dataVO = new RockdisDataVO();
            dataVO.setKey(params);
            dataVO.setFieldArray(params2);
            return RockdisProtocol.sendTransactionCommand(transactionDataExchange, dataVO);
        } else {
            logger.error("[ERROR] System do not enable TRANSACTION mechanism.");
            return null;
        }
    }

    protected RockdisResultObject sendTransactionCommand(final Command command, boolean enableTransaction, byte[] params, byte[] params2, byte[] params3) throws RocksDBException {
        if (enableTransaction) {
            RocksDBTransactionDataExchange transactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);
            RockdisDataVO dataVO = new RockdisDataVO();
            dataVO.setKey(params);
            dataVO.setFiled(params2);
            dataVO.setValue(params3);
            return RockdisProtocol.sendTransactionCommand(transactionDataExchange, dataVO);
        } else {
            logger.error("[ERROR] System do not enable TRANSACTION mechanism.");
            return null;
        }
    }

    protected RockdisResultObject sendTransactionCommand(final Command command, boolean enableTransaction, byte[] key, Map<byte[], byte[]> hash) throws RocksDBException {
        if (enableTransaction) {
            RocksDBTransactionDataExchange transactionDataExchange = new RocksDBTransactionDataExchange(transaction, command);
            RockdisDataVO dataVO = new RockdisDataVO();
            dataVO.setKey(key);
            dataVO.setFieldValueMap(hash);
            return RockdisProtocol.sendTransactionCommand(transactionDataExchange, dataVO);
        } else {
            logger.error("[ERROR] System do not enable TRANSACTION mechanism.");
            return null;
        }
    }


//    protected RockdisResultObject sendTransactionCommand (final Command command, boolean enableTransaction, byte[] params) throws RocksDBException {
//        if ( enableTransaction ){
//            return RockdisProtocol.sendCommand(command, transaction, getIterator(), params);
//        } else {
//            logger.error("[ERROR] System do not enable TRANSACTION mechanism.");
//            return null;
//        }
//    }

    protected RockdisResultObject sendCommand(final RockdisProtocol.Command command, byte[] params) throws RocksDBException {
    	try(RocksIterator iterator = getIterator()){
            return RockdisProtocol.sendCommand(command, params, db, getRocksDBType(), iterator, this);	
    	}
    }

    protected RockdisResultObject sendCommand(final RockdisProtocol.Command command, byte[] params, byte[] params2, byte[] params3) throws RocksDBException {
        return RockdisProtocol.sendCommand(command, params, params2, params3, db);
    }

    protected RockdisResultObject sendCommand(final RockdisProtocol.Command command, byte[] key, byte[] field, int value) throws RocksDBException {
    	try(RocksIterator iterator = getIterator()){
        	return RockdisProtocol.sendCommand(db, getRocksDBType(), iterator, command, key, field, value, this);    		
    	}
    }

    protected RockdisResultObject sendCommand(final RockdisProtocol.Command command, byte[] params, byte[]... params2) throws RocksDBException {

        if (command.equals(Command.HDEL)) {
            return RockdisProtocol.sendCommand(db, this, command, params, params2);
        } else {
        	try(RocksIterator iterator = getIterator()){
                return RockdisProtocol.sendCommand(db, iterator, this, command, params, params2);	
        	}
        }

    }

    protected RockdisResultObject sendCommand(final RockdisProtocol.Command command, byte[] key, Map<byte[], byte[]> hash) throws RocksDBException {
        return RockdisProtocol.sendCommand(db, command, key, hash);
    }

    private void setUpPrimaryDB(String db_path) throws RocksDBException {
        db = RocksDB.open(options, db_path);
    }

    private void setUpSecondaryDB(String db_path, String secondary_db_path) throws RocksDBException {
        db = RocksDB.openAsSecondary(options, db_path, secondary_db_path);

    }

    private void setUpTransactionDB(String dbPath) throws RocksDBException {
        logger.info("Set Transaction DB Instance.");
        transactionDB = TransactionDB.open(options, transactionDBOptions, dbPath);
        //transactionDB = TransactionDB.open(options, transactionDBOptions, dbPath);
    }

/*
    public RocksDB getDb(){
        return db;
    }
*/

    public ReadOptions getReadOptions() {
        return readOptions;
    }

    public WriteOptions getWriteOptions() {
        return writeOptions;
    }

    public RocksIterator getIterator() throws RocksDBException {

/*        if ( RocksDBType == RockdisParams.Type.SECONDARY){
            logger.trace("Only SECONDARY MODE can use tryCatchUpWithPrimary.");
            db.tryCatchUpWithPrimary();
        }*/
        if (enableTransaction) {
            logger.debug("Use Transaction iterator.");
            return transactionDB.newIterator(readOptions);
        } else {
            iteratorCount.incrementAndGet();
            logger.debug("使用 iterator, 目前 iterator Count 為 : {}", iteratorCount.get());
//            System.out.println("使用 iterator, 目前 iterator Count 為 : " + iteratorCount.get());
            if ( db.isOwningHandle()) {
                return db.newIterator(readOptions);
            } else {
                return null;
            }

        }
    }

    java.util.concurrent.atomic.AtomicBoolean catching = new java.util.concurrent.atomic.AtomicBoolean(false);
    protected void tryCatchUpWithPrimary() throws RocksDBException {

        if (RocksDBType == RockdisParams.Type.SECONDARY) {
            // logger.trace("Only SECONDARY MODE can use tryCatchUpWithPrimary.");
        	if(!catching.getAndSet(true)) {
        		try {
            		synchronized(db) {
            			db.tryCatchUpWithPrimary(); 	
            		}  	
        		} finally {
        			catching.set(false);
        		}     		
        	} else System.out.println("tryCatchUpWithPrimary skipping");

        } else {
            logger.trace("Only SECONDARY MODE can use tryCatchUpWithPrimary.");
        }
    }
    
    java.util.concurrent.atomic.AtomicBoolean compacting = new java.util.concurrent.atomic.AtomicBoolean(false);
    long compactingMillis = 0;
    protected void compactRange() throws RocksDBException {

        if (RocksDBType == RockdisParams.Type.PRIMARY) {
        	if((System.currentTimeMillis()-compactingMillis) < 180000L) return;
            // logger.trace("Only SECONDARY MODE can use tryCatchUpWithPrimary.");
        	if(!compacting.getAndSet(true)) {
        		try {
            		synchronized(db) {
            			db.compactRange();	
            			compactingMillis = System.currentTimeMillis();
            		}  	
        		} finally {
        			compacting.set(false);
        		}     		
        	} else System.out.println("compactRange skipping");

        } else {
            logger.trace("Only PRIMARY MODE can use compactRange.");
        }
    }

    public RockdisParams.Type getRocksDBType() {
        return RocksDBType;
    }

    public void setRocksDBType(RockdisParams.Type rocksDBType) {
        RocksDBType = rocksDBType;
    }

    public void close() throws RocksDBException {
        db.closeE();
    }

    public void setCompressionType(CompressionType compressionType) {
        this.compressionType = compressionType;
    }

    private void setUpTableOptions() {

        final Filter bloomFilter = new BloomFilter(10);
//        table_options.setBlockCacheSize(64 * SizeUnit.KB)
        table_options.setBlockCacheSize(32 * SizeUnit.MB)
                .setFilter(bloomFilter)
                .setCacheNumShardBits(6)
                .setBlockSizeDeviation(5)
                .setBlockRestartInterval(10)
                .setCacheIndexAndFilterBlocks(true)
                .setCacheIndexAndFilterBlocksWithHighPriority(true)
                .setHashIndexAllowCollision(false)
                .setBlockCacheCompressedSize(32 * SizeUnit.MB)
                .setBlockCacheCompressedNumShardBits(10);

    }


    private void setUpSecondaryReadOptions() {
        readOptions.setTotalOrderSeek(true);
        readOptions.setVerifyChecksums(true);
        readOptions.setFillCache(false);
    }

    private void setUpPrimaryReadOptions() {

        readOptions.setTotalOrderSeek(true);
        readOptions.setFillCache(false);
    }


    private void setUpSecondaryOptions() {
        setRocksDBType(RockdisParams.Type.SECONDARY);
        options.setCreateIfMissing(true).setMaxOpenFiles(-1);
                //.setStatistics(stats)
                //.setMaxOpenFiles(-1)
                //.setTableFormatConfig(table_options);
    }


    private void setUpPrimaryOptions() {

        final RateLimiter rateLimiter = new RateLimiter(10000000, 10000, 10);
        options.setMemTableConfig(
                new HashSkipListMemTableConfig()
                        .setHeight(4)
                        .setBranchingFactor(4)
                        .setBucketCount(2000000));

        options.setCreateIfMissing(true)
                .setMaxOpenFiles(-1)  //表示內容中可容納的 index 和 filter block 無限制，參數過小會導致讀取的性能大幅下滑

                .setStatistics(stats)
                .setStatsDumpPeriodSec(5)
//                .setWriteBufferSize(8 * SizeUnit.KB)h
                .setWriteBufferSize(1024 * SizeUnit.MB)  //Write Buffer Size越大，寫放大效應越小，但必須一起調整幾個參數
                .setMaxBytesForLevelBase(512 * SizeUnit.MB)  //每個 level 的最大大小，超過後會觸發 compaction

//                .setMaxBytesForLevelMultiplier(10)  //每個 level 的大小是前一個 level 的幾倍
                .setLevelCompactionDynamicLevelBytes(true)  //這個參數允許RocksDB對每層儲存的數據閾值進行動態調整，不只是單單 Level Base的倍數
                                                            // 如果是HDD的情境，建議設置為True，這個參數跟上面的參數選一個設定

                .setMaxWriteBufferNumber(3)  //預設值是2，對於HDD如果內存夠大，可以調到5。
                .setManualWalFlush(false)
                .setMaxBackgroundCompactions(10)
                .setCompressionType(this.compressionType)
                .setCompactionStyle(CompactionStyle.UNIVERSAL);

        options.setMemTableConfig(
                new HashLinkedListMemTableConfig()
                        .setBucketCount(100000));

        options.setMemTableConfig(
                new VectorMemTableConfig().setReservedSize(10000));

        options.setMemTableConfig(new SkipListMemTableConfig());
        options.setTableFormatConfig(new PlainTableConfig());
        // Plain-Table requires mmap read
        options.setAllowMmapReads(true);
        options.setRateLimiter(rateLimiter);
        options.setTableFormatConfig(table_options);

        logger.debug("Compression Type is : {}", options.compressionType());
    }
    
	public long[] writeBatchIntoDB(ConcurrentSkipListMap<String, Object> patchIntoDbMap) throws RocksDBException {
    	Map.Entry<String,Object> skey = patchIntoDbMap.firstEntry();
    	long count = 0;
    	long exec = 0;
    	
        WriteBatch writeBatch = new WriteBatch();
        WriteOptions writeOptions = new WriteOptions();
    	
    	while(true) {
        	if(skey==null) break;
        	count++;
        	Object value = skey.getValue();
        	if(value==null) continue;
        	String rocksCommand = (String)((Map) value).remove("rockscommand");
            if (rocksCommand != null ) {
            	boolean rt = insertIntoBatch(writeBatch,skey.getKey(), value, rocksCommand);
                if(rt) {
                	exec++;
                }
                String aaa = skey.getKey();
                if(aaa.indexOf("IX0189")!=-1 && aaa.indexOf("quote")!=-1) System.out.println(aaa+" __003 "+value);
            } 
    		skey = patchIntoDbMap.higherEntry(skey.getKey());
    	}
    	if(exec > 0 ) {
    		RockdisProtocol.dbWrite(db,writeOptions, writeBatch);
    	}
    	
		return new long[] {count,exec};
	}
    
    private boolean insertIntoBatch(WriteBatch writeBatch, String key, Object value, String rocksCommand) {
        // todo 這裡要改成從DB拿資料
    	boolean rt = false;
        switch (rocksCommand) {
            case "set":
                try {
                    // foreach value in valueMap
                    NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                    // 刪除 rockscommand
                    valueMap.remove("rockscommand");
                    // 將資料寫入 writeBatch
                    RockdisProtocol.sendCommand(Command.SET, key.getBytes(), valueMap.get("setCommandNoFiled").getBytes(), writeBatch);
                    rt = true;
                } catch (RocksDBException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "hset":
                try {
                    // foreach value in valueMap
                    NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                    // 刪除 rockscommand
                    valueMap.remove("rockscommand");
                    // 將資料寫入 RocksDB
                    while (!valueMap.isEmpty()) {
                        Map.Entry entry = valueMap.pollFirstEntry();
                        if (entry == null) {
                            continue;
                        }
                        RockdisProtocol.sendCommand(Command.HSET, key.getBytes(), ((String)entry.getKey()).getBytes(), ((String) entry.getValue()).getBytes(), writeBatch);
                        rt = true;
                    }
                } catch (RocksDBException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "hmset":
                try {
                    // foreach value in valueMap
                    NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                    // 刪除 rockscommand
                    valueMap.remove("rockscommand");
                    RockdisProtocol.sendCommand(writeBatch, Command.HMSET, key.getBytes(), Converter.stringMapToByteArrayMap(valueMap));
                    rt = true;
                } catch (RocksDBException e) {
                    throw new RuntimeException(e);
                }
                break;

            case "zadd":

                NavigableMap<String, String> valueMap = (ConcurrentSkipListMap<String, String>) value;
                valueMap.remove("rockscommand");

                try {
                	RockdisProtocol.sendCommand(writeBatch,Command.ZADD ,CollectionUtil.convertMapToList(key, valueMap));
                	rt = true;
                } catch (RocksDBException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
        return rt;
    }	
	
}
