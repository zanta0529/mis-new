/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp;

//import Rockdis.Rockdis;
import rocksdbapi.Rockdis.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.CachingSystem.CachingSystem;
import com.github.tonivade.resp.CachingSystem.CachingSystemScheduler;
import com.github.tonivade.resp.CachingSystem.InorderSystem;
import com.github.tonivade.resp.CachingSystem.LargeCachePoolSystem;
//import com.github.tonivade.resp.CrossDaySystem.CrossDayCheck;
//import com.github.tonivade.resp.CrossDaySystem.CrossDayFileOps;
import com.github.tonivade.resp.Model.ChannelStatus;
import com.github.tonivade.resp.Util.ChannelStrategy;
import com.github.tonivade.resp.command.CommandSuite;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.RespCommand;
import com.github.tonivade.resp.command.ServerContext;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.RedisToken;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.github.tonivade.resp.SessionListener.nullListener;
import static java.util.Objects.requireNonNull;

public class RespServerContext implements ServerContext {
  private static final Logger LOGGER = LoggerFactory.getLogger(RespServerContext.class);

  private final StateHolder state = new StateHolder();
  private final ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();
  private final Scheduler scheduler = Schedulers.from(Executors.newSingleThreadExecutor());

  private final String host;
  private final int port;
  private final CommandSuite commands;
  private final boolean authEnabled;
  private SessionListener sessionListener;
  private boolean enableCrossDayDebug;
  private boolean enableCrossDayMechanism;
  private boolean authStatus;
  private String authString;
  private boolean cacheSystemEnabled;

  @Deprecated
  private int cachePeriod;
  private final int cachEvictTimePeriod;
  private Rockdis rockdis;
  private Rockdis secondary;
  private String primaryDir;
  private String secondaryDir;
  private HashMap<String, ChannelStatus> channelInfoHashMap;

  /**
   * 安全模式，當 flushall 指令執行後，會將其數值改為 true。
   * 此時，所有的寫入指令都會寫道 LargeCachePoolSystem內的 Waiting Cache中。
   * 所有的讀取指令都會直接回傳 null，避免操作 iterator導致切換失敗。
   * 當完成 flushall 後，將其數值改為 false。
   */
  private AtomicBoolean safemode = new AtomicBoolean(false);

  public boolean getSafemode(){
        return safemode.get();
  }

  public void setSafemode(boolean status){
        this.safemode.set(status);
  }

  public RespServerContext(String host, int port, CommandSuite commands,
                           String primaryDir, String secondaryDir,
                           boolean enableCrossDayDebug, boolean enableCrossDayMechanism,
                           boolean cacheSystemEnabled, int cachePeriod, int cacheEvictTimePeriod) {
    this(host, port, commands, nullListener(),
            primaryDir, secondaryDir,
            enableCrossDayDebug, enableCrossDayMechanism,
            false, "", cacheSystemEnabled, cachePeriod, cacheEvictTimePeriod);
  }

  public RespServerContext(String host, int port, CommandSuite commands,
                           String primaryDir, String secondaryDir,
                           boolean enableCrossDayDebug, boolean enableCrossDayMechanism,
                           boolean authEnabled, String authString,
                           boolean cacheSystemEnabled, int cachePeriod, int cacheEvictTimePeriod) {
    this(host, port, commands, nullListener(),
            primaryDir, secondaryDir,
            enableCrossDayDebug, enableCrossDayMechanism,
            authEnabled, authString,
            cacheSystemEnabled, cachePeriod, cacheEvictTimePeriod);
  }


//  public RespServerContext(String host, int port, CommandSuite commands) {
//    this(host, port, commands, nullListener());
//  }

  public int getCachEvictTimePeriod() {
    return cachEvictTimePeriod;
  }

  public RespServerContext(String host, int port, CommandSuite commands,
                           SessionListener sessionListener, String primaryDir, String secondaryDir,
                           boolean enableCrossDayDebug, boolean enableCrossDayMechanism,
                           boolean authEnabled, String authString, boolean cacheSystemEnabled, int cachePeriod, int cacheEvictTimePeriod) {
    this.host = requireNonNull(host);
    this.port = requireRange(port, 1024, 65535);
    this.commands = requireNonNull(commands);
    this.sessionListener = sessionListener;
    this.enableCrossDayDebug = enableCrossDayDebug;
    this.enableCrossDayMechanism = enableCrossDayMechanism;
    this.authEnabled = authEnabled;
    this.authString = authString;
    this.cacheSystemEnabled = cacheSystemEnabled;
    this.cachePeriod = cachePeriod;
    this.cachEvictTimePeriod = cacheEvictTimePeriod;
    this.channelInfoHashMap = new HashMap<>();

    // 初始化 RocksDB 的 Runtime Directory
//    boolean enable_cross_day_debug_mode = true;
    this.primaryDir = primaryDir;
    this.secondaryDir = secondaryDir;
//    String rocksdbPrimaryAbsolutePath = primaryDir;
//    String rocksdbSecondaryAbsolutePath = secondaryDir;

//    if ( !enableCrossDayDebug ) {
//      LOGGER.debug("Cross Day Debug set FALSE. Prepare RocksDB initial.");
//      CrossDayCheck crossDayCheck = new CrossDayCheck();
//      String runtimeDateString = crossDayCheck.getRuntimeDate();
//      CrossDayCheck.setCurrentRuntimeDate(runtimeDateString);
//      CrossDayFileOps.createRuntimeDirectory(primaryDir, runtimeDateString);
//      CrossDayFileOps.createRuntimeDirectory(secondaryDir, runtimeDateString);
//    }
    String rocksdbPrimaryAbsolutePath = primaryDir + "currentDB/primary/";
    String rocksdbSecondaryAbsolutePath = secondaryDir + "currentDB/secondary/";

    LOGGER.debug("Primary DB Absolute Path : " + rocksdbPrimaryAbsolutePath);
    LOGGER.debug("Secondary DB Absolute Path : " + rocksdbSecondaryAbsolutePath);

    // 建立目錄
    createRocksDBDirectory(rocksdbPrimaryAbsolutePath, rocksdbSecondaryAbsolutePath);

    try {
      rockdis = new Rockdis(rocksdbPrimaryAbsolutePath);
      secondary = new Rockdis(rocksdbPrimaryAbsolutePath, rocksdbSecondaryAbsolutePath);
      LOGGER.debug("Primary @[Address] : " + rockdis.toString());
      LOGGER.debug("Secondary @[Address] : " + secondary.toString());
      // 用來進行secondary 的 try-catchup primary 程序
      secondaryDBScheduler();

      // =======================================================================================================================================
      // Cache period 將會被 deprecated
      // 若有啟動 Cache System，則會依照 Cache Period 來啟動 Scheduler，到達 Period 時，會將 Cache System 中的資料寫入 RocksDB
      // Cache Period 參數取自 rockdis.properties 中的 cache_period
      // =======================================================================================================================================
//      if ( cacheSystemEnabled ){
//        CachingSystemScheduler.getInstance(this , this.cachePeriod).triggerScheduler();
//      }

    } catch (RocksDBException e) {
      LOGGER.error(e.getMessage());
    }

//    //啟動 Cross Day Check Scheduler
//    if ( !enableCrossDayDebug && enableCrossDayMechanism) {
//      LOGGER.debug("Cross Day Mechanism is enabled.");
//      crossDayCheckScheduler();
//    } else {
//      LOGGER.debug("Cross Day Mechanism is disabled.");
//    }
    // Initial Channel Strategy
    ChannelStrategy channelStrategy = new ChannelStrategy();
    channelStrategy.searchChannelStrategy();
    LOGGER.debug("Channel Strategy : {}", channelStrategy.getChannelStrategy().toString());
  }

  private void createRocksDBDirectory(String rocksdbPrimaryAbsolutePath, String rocksdbSecondaryAbsolutePath) {
    Path primary = Paths.get(rocksdbPrimaryAbsolutePath);
    // 检查目录是否存在
    if (!Files.exists(primary)) {
      try {
        // 创建目录
        Files.createDirectories(primary);
        LOGGER.debug("Primary DB directory has been created.");
      } catch (IOException e) {
        // 处理异常
        LOGGER.error("Primary DB directory can not be created.");
      }
    } else {
      LOGGER.debug("Primary DB directory has existed.");
    }

    Path secondary = Paths.get(rocksdbSecondaryAbsolutePath);
    if (!Files.exists(secondary)) {
      try {
        // 创建目录
        Files.createDirectories(secondary);
        LOGGER.debug("Secondary DB directory has been created.");
      } catch (IOException e) {
        // 处理异常
        LOGGER.error("Secondary DB directory can not be created.");
      }
    } else {
      LOGGER.debug("Secondary DB directory has existed.");
    }
  }

  public void start() {

  }

  public void stop() {
    clear();
    scheduler.shutdown();
  }

  @Override
  public int getClients() {
    return clients.size();
  }

  @Override
  public RespCommand getCommand(String name) {
    return commands.getCommand(name);
  }

  @Override
  public <T> Option<T> getValue(String key) {
    return state.getValue(key);
  }

  @Override
  public <T> Option<T> removeValue(String key) {
    return state.removeValue(key);
  }

  @Override
  public void putValue(String key, Object value) {
    state.putValue(key, value);
  }

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public int getPort() {
    return port;
  }

  Session getSession(String sourceKey, Function<String, Session> factory) {
    return clients.computeIfAbsent(sourceKey, key -> {
      Session session = factory.apply(key);
      sessionListener.sessionCreated(session);
      return session;
    });
  }

  public void setAuthStatus(boolean status) {
    this.authStatus = status;
  }

  public boolean getAuthStatus() {
    return this.authStatus;
  }

  @Override
  public boolean getCacheSystemStatus() {
    return this.cacheSystemEnabled;
  }

  public String getAuthString() {
    return this.authString;
  }

  public Rockdis getRockdisInstance() {
    return this.rockdis;
  }

  public CachingSystem getCacheSystem(){
    return CachingSystem.getInstance();
  }

  public LargeCachePoolSystem getLargeCachePoolSystem() {
    return LargeCachePoolSystem.getInstance(this);
  }

  public InorderSystem getInorderSystem(){
    return InorderSystem.getInstance();
  }

  public boolean initChannelInfo(String channelID) {

    // if auth has been enabled, isAuthorized status of ChannelStatus should set false.
    // Once the request has been authorized, then this connection should be clear.
    if (this.authEnabled) {
      setChannelInfo(channelID, new ChannelStatus(false));
    } else {
      setChannelInfo(channelID, new ChannelStatus(true));
    }
    return true;
  }

  public ChannelStatus getChannelInfo(String channelID){
    return this.channelInfoHashMap.get(channelID);
  }

  public void setChannelInfo(String channelID, ChannelStatus channelStatus) {
    this.channelInfoHashMap.put(channelID, channelStatus);
  }

  public void destroyChannelInfo(String channelId) {
    LOGGER.debug("Destroy Information of Channel Id : {}", channelId);
    this.channelInfoHashMap.remove(channelId);
  }

  public Rockdis getSecondRockdisInstance(){
    return this.secondary;
  }

  public void setFlushAll(){

    String tseDate = "";
    String otcDate = "";
    // 取得當天日期
    try {
      tseDate = getSecondRockdisInstance().get("tse");
      otcDate = getSecondRockdisInstance().get("otc");
      //把 Cache內的資料寫入 RocksDB
      getLargeCachePoolSystem().cleanCache();
    } catch (RocksDBException e) {
      throw new RuntimeException(e);
    }

    System.out.println("確認 Safe Mode 狀態 : " + getSafemode());
    System.out.println("確認 Manual CleanAll 狀態 : " + getLargeCachePoolSystem().manualCleanAll.get());
    System.out.println("確認 LCPS 內的資料筆數 : " + getLargeCachePoolSystem().cache.estimatedSize());


    System.out.println("開始確認 RocksDB iterator 是否為 0，進行確認是確保 cleanCache() 把LCPS資料已經寫入DB");
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    // todo 確認 Primary 與 Secondary 的 RocksDB iterator 都是 0
    // 设置日期格式
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    Date currentTime = new Date();
    // 格式化当前时间并打印输出
    String formattedTime = sdf.format(currentTime);
    System.out.println("目前系統時間：" + formattedTime);

    int breakCount = 0;

    long lastLogTime = System.currentTimeMillis();
    while ( getRockdisInstance().getIteratorCount().get() > 0L || getSecondRockdisInstance().getIteratorCount().get() > 0L){

      if ( breakCount > 10 ){
        System.out.println("經過了100秒");
        break;
      }

      long elapsedTime = System.currentTimeMillis() - lastLogTime;
      if (elapsedTime > 10000) {
        System.out.println("=================================================================");
        currentTime = new Date();
        // 格式化当前时间并打印输出
        formattedTime = sdf.format(currentTime);
        System.out.println("目前系統時間：" + formattedTime);
        System.out.println("Waiting for all RocksDB iterator to be 0.");
        System.out.println("Primary RocksDB iterator count : " + getRockdisInstance().getIteratorCount().get());
        System.out.println("Secondary RocksDB iterator count : " + getSecondRockdisInstance().getIteratorCount().get());
        System.out.println("確認 LCPS 內的資料筆數 : " + getLargeCachePoolSystem().cache.estimatedSize());
        System.out.println();
        System.out.println();
        lastLogTime = System.currentTimeMillis();
        breakCount++;
      }
    }
    System.out.println("RocksDB iterator 降低為 0");

    System.out.println("準備關閉RocksDB");
    // 關閉RocksDB
    try {
      getRockdisInstance().close();
      getSecondRockdisInstance().close();
      this.secondary = null;
    } catch (RocksDBException e) {
      throw new RuntimeException(e);
    }


    System.out.println("完成關閉RocksDB");
    System.out.println("準備更名DB");
    //準備更名
    String replacedDate = "";
    if (tseDate.equals("null") && otcDate.equals("null")) {
      //使用系統日期
      replacedDate = getSystemDate();
    } else {
      replacedDate = tseDate;
    }

    // 判斷該日期是否有重複
    TreeSet<Integer> dateSet = new TreeSet<Integer>();
    File directory = new File(primaryDir);
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory() && file.getName().startsWith(replacedDate)) {
        	try {
            	Integer a = Integer.parseInt(file.getName().split("_")[1]);
            	dateSet.add(a);	
        	} catch(Exception ex) {
        		System.out.println(cleanString("定義外的檔名："+file.getName()));
        	}
        }
      }
    }

    Path targetPath = null;
    if (dateSet.isEmpty()) {
      // 日期沒有重複
      targetPath = Paths.get(primaryDir + replacedDate + "_0");
    } else {
      // 日期有重複
      int index = dateSet.last();//Integer.parseInt(dateSet.last().split("_")[1]);
      targetPath = Paths.get(primaryDir + replacedDate + "_" + ++index);
    }

    // 最後目錄位置
    Path sourcePath = Paths.get(primaryDir+"currentDB");

    try {
      // 重命名目录
      Files.move(sourcePath, targetPath);
      LOGGER.debug("更名成功");
    } catch (FileAlreadyExistsException e) {
      // 目标目录已存在
      LOGGER.error("无法重命名目录，目标目录已存在: " + e.getMessage());
    } catch (IOException e) {
      // 处理其他异常
      LOGGER.error("无法重命名目录: " + e.getMessage());
    }

    System.out.println("完成DB更名 from:"+sourcePath+" to:"+targetPath);
    //建立 新的currentDB
    long ct = System.currentTimeMillis();
    System.out.println("準備建立新的CurrentDB s:"+ct);
    createRocksDBDirectory(primaryDir+"currentDB/primary/", secondaryDir+"/"+ct+"/secondary/");

    try {
      rockdis = new Rockdis(primaryDir+"currentDB/primary/");
      secondary = new Rockdis(primaryDir+"currentDB/primary/", secondaryDir+"/"+ct+"/secondary/");
    } catch (RocksDBException e) {
      throw new RuntimeException(e);
    }
    System.out.println("完成建立新的CurrentDB");
    // 準備將 Waiting Cache內的資料轉換到 LCPS，注意有可能會觸發 LCPS往後端DB寫入
    // 因此要確認 DB已經Ready後才可進行 Cache 移轉

    System.out.println("將 Safe Mode下的 Cache 移轉至 LCPS");
    getLargeCachePoolSystem().switchCacheFromWaitingToLCPS();
  }

  private String getSystemDate() {
    // 获取当前日期
    LocalDate currentDate = LocalDate.now();
    // 定义日期格式
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    // 将日期格式化为字符串
    LOGGER.debug("tse跟otc 沒有數值，採用系統數值 : " + currentDate.format(formatter));
    return currentDate.format(formatter);
  }

//  class CrossDayCheckTask extends TimerTask{
//    private final Logger LOGGER = LoggerFactory.getLogger(CrossDayCheckTask.class);
//
//    @Override
//    public void run() {
//      CrossDayCheck crossDayCheck = new CrossDayCheck();
//      String runtimeDateString = crossDayCheck.getRuntimeDate();
////      LOGGER.debug("[Scheduler] Runtime date : {}", runtimeDateString);
////      LOGGER.debug("[Scheduler] Current cached date : {}", CrossDayCheck.currentRuntimeDate);
//
//      if (!runtimeDateString.equals(CrossDayCheck.currentRuntimeDate)){
//        LOGGER.debug("[Scheduler] Trigger Cross Day mechanism.");
//        CrossDayFileOps.createRuntimeDirectory(primaryDir, runtimeDateString);
//        CrossDayFileOps.createRuntimeDirectory(secondaryDir, runtimeDateString);
//        CrossDayCheck.setCurrentRuntimeDate(runtimeDateString);
//        LOGGER.debug("[Scheduler] After cross day mechanism , Primary DB Absolute Path become : " + primaryDir + runtimeDateString);
//        LOGGER.debug("[Scheduler] After cross day mechanism , Secondary DB Absolute Path become : " + secondaryDir + runtimeDateString);
//
//        try{
//          rockdis = new Rockdis(primaryDir + runtimeDateString);
//          secondary = new Rockdis(primaryDir + runtimeDateString, secondaryDir + runtimeDateString);
//          LOGGER.debug("Primary @[Address] : " + rockdis.toString());
//          LOGGER.debug("Secondary @[Address] : " + secondary.toString());
//        } catch (RocksDBException e) {
//          LOGGER.error(e.getMessage());
//        }
//      }
//    }
//  }

//  void crossDayCheckScheduler(){
//    Timer timer = new Timer();
//    LOGGER.debug("[Scheduler] Cross Day Check Scheduler");
//    timer.schedule(new CrossDayCheckTask(), 5000, 500);
//  }

  class tryCatchUpWithPrimaryTask extends TimerTask{
    private final Logger LOGGER = LoggerFactory.getLogger(tryCatchUpWithPrimaryTask.class);
    @Override
    public void run() {
//      LOGGER.debug("Start tryCatchUpWithPrimary.");
      //        LOGGER.debug("Secondary @[Address] : " + secondary.toString());
      try {

//        System.out.println("執行 TryCatchUpWithPrimary, 檢查是否為Safe Mode : " + getSafemode());
//        if (secondary != null) {
        if ( !getSafemode() ) {
          secondary.tryCatchUpWithPrimary();
        }
      } catch (RocksDBException e) {
        e.printStackTrace();
      }
    }
  }

  void secondaryDBScheduler(){
    Timer timer = new Timer();
//    LOGGER.debug("Secondary DB Scheduler starting......");
    timer.schedule(new tryCatchUpWithPrimaryTask(), 5000, 500);
  }

  void processCommand(Request request) {

    if (request.toString().contains("2330")) {
      LOGGER.debug("received command: {}", request);
    }

    RespCommand command = getCommand(request.getCommand());
    try {
      executeOn(execute(command, request))
        .subscribe(response -> processResponse(request, response),
                   ex -> LOGGER.error("error executing command from executeOn: " + request, ex));
    } catch (RuntimeException ex) {
      LOGGER.error("error executing command: " + request, ex);
    }
  }

  protected CommandSuite getCommands() {
    return commands;
  }

  protected void removeSession(String sourceKey) {
    Session session = clients.remove(sourceKey);
    if (session != null) {
      sessionListener.sessionDeleted(session);
    }
  }

  protected Session getSession(String key) {
    return clients.get(key);
  }

  protected RedisToken executeCommand(RespCommand command, Request request) {
    return command.execute(request);
  }

  protected <T> Observable<T> executeOn(Observable<T> observable) {
    return observable.observeOn(scheduler);
  }

  private void processResponse(Request request, RedisToken token) {
    request.getSession().publish(token);
    if (request.isExit()) {
      request.getSession().close();
    }
  }

  /**
   * 將執行 Redis 命令的結果包裝成一個 RxJava 的 Observable，
   * 以便使用 RxJava 提供的操作符和異步處理功能。當有訂閱者訂閱這個 Observable 時，
   * 它會發射一個元素，即執行命令的結果，然後標記為完成。
   */
  private Observable<RedisToken> execute(RespCommand command, Request request) {
    return Observable.create(observer -> {
      observer.onNext(executeCommand(command, request));
      observer.onComplete();
    });
  }

  private int requireRange(int value, int min, int max) {
    if (value <= min || value > max) {
      throw new IllegalArgumentException(min + " <= " + value + " < " + max);
    }
    return value;
  }

  private void clear() {
    clients.clear();
    state.clear();
  }
  
  public static String cleanString(String aString) {
	  if (aString == null) return null;
	  String cleanString = "";
	  cleanString = aString.replaceAll("[^\\w\\s]", "");
	  return cleanString;
  }  
}
