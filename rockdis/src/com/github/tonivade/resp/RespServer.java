/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp;

import static com.github.tonivade.resp.protocol.SafeString.safeAsList;
import static com.github.tonivade.resp.protocol.SafeString.safeString;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.github.tonivade.purefun.data.Sequence;
import com.github.tonivade.resp.CachingSystem.CachingSystemScheduler;
import com.github.tonivade.resp.Util.ChannelStrategy;
import static com.github.tonivade.resp.Util.ChannelStrategy.*;
import static com.github.tonivade.resp.protocol.RedisToken.string;

import com.github.tonivade.resp.protocol.*;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.purefun.Matcher1;
import com.github.tonivade.purefun.Pattern1;
import com.github.tonivade.purefun.data.ImmutableArray;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.command.CommandSuite;
import com.github.tonivade.resp.command.DefaultRequest;
import com.github.tonivade.resp.command.DefaultSession;
import com.github.tonivade.resp.command.Request;
import com.github.tonivade.resp.command.Session;
import com.github.tonivade.resp.protocol.AbstractRedisToken.ArrayRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.StringRedisToken;
import com.github.tonivade.resp.protocol.AbstractRedisToken.UnknownRedisToken;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.concurrent.Future;
import redisapi.RedisInNetty;
import redisapi.Util.ReadProperties;

public class RespServer implements Resp {

  private static final Logger LOGGER = LoggerFactory.getLogger(RespServer.class);

  private static final int BUFFER_SIZE = 1024 * 1024;
  private static final int MAX_FRAME_SIZE = BUFFER_SIZE * 100;

  private static final String DEFAULT_HOST = "localhost";
  private static final int DEFAULT_PORT = 12345;

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private ChannelFuture future;

  private final RespServerContext serverContext;

  public RespServer(RespServerContext serverContext) {
    this.serverContext = requireNonNull(serverContext);
  }

  public static Builder builder() {
    return new Builder();
  }

  public void start() {

    Strategy strategy = ChannelStrategy.getChannelStrategy();

    if (strategy.equals(Strategy.EPOLL)) {
      bossGroup = new EpollEventLoopGroup();
      workerGroup = new EpollEventLoopGroup(ReadProperties.getRockdisWorker());
    } else if (strategy.equals(Strategy.KQUEUE)) {
      bossGroup = new KQueueEventLoopGroup();
      workerGroup = new KQueueEventLoopGroup(ReadProperties.getRockdisWorker());
    } else if (strategy.equals(Strategy.NIO)){
      bossGroup = new NioEventLoopGroup();
      workerGroup = new NioEventLoopGroup(ReadProperties.getRockdisWorker());
    }



    ServerBootstrap bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup);
    // Setup Channel

    if (strategy.equals(Strategy.EPOLL)) {
      bootstrap.channel(EpollServerSocketChannel.class);
    } else if (strategy.equals(Strategy.KQUEUE)) {
      bootstrap.channel(KQueueServerSocketChannel.class);
    } else if (strategy.equals(Strategy.NIO)){
      bootstrap.channel(NioServerSocketChannel.class);
    }

    bootstrap.childHandler(new RespInitializerHandler(this))
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
        .option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

    future = bootstrap.bind(serverContext.getHost(), serverContext.getPort());
    // Bind and start to accept incoming connections.
    future.syncUninterruptibly();

    serverContext.start();

    LOGGER.debug("Show Queue Strategy...");
    LOGGER.debug("KQueue is avaliable : {}", KQueue.isAvailable());
    LOGGER.debug("Epoll is avaliable: {}", Epoll.isAvailable());
    LOGGER.info("server started: {}:{}", serverContext.getHost(), serverContext.getPort());
  }

  public void stop() {

    // To persist cached data during abnormal situation.
    persistCacheSystemBeforeShutdown();

    try {
      if (future != null) {
        closeFuture(future.channel().close());
      }
      future = null;
    } finally {
      workerGroup = closeWorker(workerGroup);
      bossGroup = closeWorker(bossGroup);
    }

    serverContext.stop();

    LOGGER.info("server stopped");
  }

  @Override
  public void channel(SocketChannel channel) {
    LOGGER.debug("new channel: {}", sourceKey(channel));
    LOGGER.debug("Channel ID : {}", channel.id());

    channel.pipeline().addLast("redisEncoder", new RedisEncoder());
    channel.pipeline().addLast("linDelimiter", new RedisDecoder(MAX_FRAME_SIZE));
    channel.pipeline().addLast(new RespConnectionHandler(this));
  }

  @Override
  public void connected(ChannelHandlerContext ctx) {
    String sourceKey = sourceKey(ctx.channel());
    String channelId = ctx.channel().id().toString();
    LOGGER.debug("client connected: {}", sourceKey);
    LOGGER.debug("Channel ID : {}", channelId);
    initRedisInfo(channelId);
    if(RedisInNetty.debug) System.out.println(getFullyDateTimeStr()+" client connected\t"+ctx.toString());
    getSession(ctx, sourceKey, channelId);
  }

  @Override
  public void disconnected(ChannelHandlerContext ctx) {
    String sourceKey = sourceKey(ctx.channel());
    String channelId = ctx.channel().id().toString();
    LOGGER.debug("client disconnected: {}", sourceKey);
    LOGGER.debug("Channel ID : {}", channelId);
    destroyRedisInfo(channelId);
    if(RedisInNetty.debug) System.out.println(getFullyDateTimeStr()+" client disconnected\t"+ctx.toString());
    serverContext.removeSession(sourceKey);
  }

  @Override
  public void receive(ChannelHandlerContext ctx, RedisToken message) {
    String sourceKey = sourceKey(ctx.channel());
    String channelId = ctx.channel().id().toString();

    
    if(RedisInNetty.debug) System.out.println(getFullyDateTimeStr()+" client receive\t"+ctx.toString()+" "+message.toString());
    // Log太多了，暫時先抓幾個
//    LOGGER.debug("message received: {}", sourceKey);
//    LOGGER.debug("message : {}", message);
//    //TODO Boss Group 的進入點
//    LOGGER.debug("Timestamp : {}", System.currentTimeMillis());

    // Make RedisToken to String
    if (message.toString().contains("2330")) {
        LOGGER.debug("message received: {}", sourceKey);
        LOGGER.debug("message : {}", message);
        LOGGER.debug("Timestamp : {}", System.currentTimeMillis());
    }


    /*
    進行時間序列的 Wrapped, 確保執行順序
     */
    if (message instanceof ArrayRedisToken){
      ImmutableArray<RedisToken> redisTokenList = (ImmutableArray<RedisToken>) ((ArrayRedisToken) message).getValue();
//      LOGGER.debug(redisTokenList.toString());  // ImmutableArray([STRING=>set, STRING=>1, STRING=>1])
      ImmutableArray<RedisToken> newRedisTokenList = redisTokenList.insert(1, string(String.valueOf(System.currentTimeMillis())));
//      LOGGER.debug(newRedisTokenList.toString()); // ImmutableArray([STRING=>set, STRING=>1622446140371, STRING=>2, STRING=>2])
      ((ArrayRedisToken) message).setValue(newRedisTokenList);
    }

    parseMessage(message, getSession(ctx, sourceKey, channelId))
      .ifPresent(serverContext::processCommand);
  }

  private Option<Request> parseMessage(RedisToken message, Session session) {
//    LOGGER.debug("Execute Code Block : RespServer.parseMessage()");
    return Pattern1.<RedisToken, Option<Request>>build()
        .when(Matcher1.instanceOf(ArrayRedisToken.class))
          .then(token -> Option.some(parseArray((ArrayRedisToken) token, session)))
        .when(Matcher1.instanceOf(UnknownRedisToken.class))
          .then(token -> Option.some(parseLine((UnknownRedisToken) token, session)))
        .otherwise()
          .returns(Option.none())
        .apply(message);
  }

  private Request parseLine(UnknownRedisToken message, Session session) {
    SafeString command = message.getValue();
    String[] params = command.toString().split(" ");
    String[] array = new String[params.length - 1];
    System.arraycopy(params, 1, array, 0, array.length);
    return new DefaultRequest(serverContext, session, safeString(params[0]), ImmutableArray.from(safeAsList(array)));
  }

  private Request parseArray(ArrayRedisToken message, Session session) {
    List<SafeString> params = toParams(message);
    return new DefaultRequest(serverContext, session, params.remove(0), ImmutableArray.from(params));
  }

  private List<SafeString> toParams(ArrayRedisToken message) {
    return message.getValue().stream()
        .flatMap(this::toSafeStrings)
        .collect(toList());
  }

  private Stream<SafeString> toSafeStrings(RedisToken token) {
    return Pattern1.<RedisToken, Stream<SafeString>>build()
        .when(Matcher1.instanceOf(StringRedisToken.class))
          .then(string -> Stream.of(((StringRedisToken) string).getValue()))
        .otherwise()
          .returns(Stream.empty())
        .apply(token);
  }

  private String sourceKey(Channel channel) {
    InetSocketAddress remoteAddress = (InetSocketAddress) channel.remoteAddress();
    return remoteAddress.getHostName() + ":" + remoteAddress.getPort();
  }

  private Session getSession(ChannelHandlerContext ctx, String sourceKey, String channelId) {
    return serverContext.getSession(sourceKey, key -> newSession(ctx, key, channelId));
  }

  private Session newSession(ChannelHandlerContext ctx, String key, String channelId) {
    return new DefaultSession(key, channelId, ctx);
  }

  private void initRedisInfo(String channelID) {
    serverContext.initChannelInfo(channelID);
  }

  private void destroyRedisInfo(String channelId) {
    serverContext.destroyChannelInfo(channelId);
  }

  private EventLoopGroup closeWorker(EventLoopGroup worker) {
    if (worker != null) {
      closeFuture(worker.shutdownGracefully());
    }
    return null;
  }

  private void closeFuture(Future<?> future) {
    LOGGER.debug("closing future");
    future.syncUninterruptibly();
    LOGGER.debug("future closed");
  }

  private void persistCacheSystemBeforeShutdown(){

    // todo 若遇到有問題的情況，要盡可能把 LargeCacheSystem內的資料寫入 RocksDB

//    Thread persistSet = new Thread(new CachingSystemScheduler.RockdisSet());
//    Thread persistHSet = new Thread(new CachingSystemScheduler.RockdisHSet());
//    Thread persistHMSet = new Thread(new CachingSystemScheduler.RockdisHMSet());
//    persistSet.start();
//    persistHSet.start();
//    persistHMSet.start();

  }

  public static class Builder {
    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private CommandSuite commands = new CommandSuite();
    private String primaryDir = "/tmp/rockdisTemp/";
    private String secondaryDir = "/tmp/secondTemp/";
    private boolean enableCrossDayDebug = false;
    private boolean enableCrossDayMechanism = true;
    private boolean authEnabled = true;
    private String authString = "admin";
    private boolean cacheSystemEnabled = false;
    private int cachePeriod = 2000;
    private int cacheEvictTimePeriod = 15000;


    public Builder host(String host) {
      this.host = host;
      return this;
    }

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder commands(CommandSuite commands) {
      this.commands = commands;
      return this;
    }

    public Builder persistenceDir(String primaryDir, String secondaryDir) {
      this.primaryDir = primaryDir;
      this.secondaryDir = secondaryDir;
      return this;
    }

    public Builder enableCrossDayDebug(boolean enabled){
      this.enableCrossDayDebug = enabled;
      return this;
    }

    public Builder enableCrossDayMechanism(boolean enabled) {
      this.enableCrossDayMechanism = enabled;
      return this;
    }

    public Builder isAuthEnabled(boolean enabled) {
      this.authEnabled = enabled;
      return this;
    }

    public Builder enableCacheSystem(boolean isEnabled) {
      this.cacheSystemEnabled = isEnabled;
      return this;
    }

    public Builder setCachePeriod(int cachePeriod) {
      this.cachePeriod = cachePeriod;
      return this;
    }

    public Builder setCacheEvictTimePeriod(int cacheEvictTimePeriod) {
      this.cacheEvictTimePeriod = cacheEvictTimePeriod;
      return this;
    }

    public Builder getAuthString(String authString) {
      this.authString = authString;
      return this;
    }

    public RespServer build() {
      LOGGER.debug("hello,world");
      if (authEnabled){
        return new RespServer(
                new RespServerContext(host, port, commands,
                                      primaryDir, secondaryDir,
                                      enableCrossDayDebug, enableCrossDayMechanism,
                                      authEnabled, authString,
                                      cacheSystemEnabled, cachePeriod, cacheEvictTimePeriod));
      } else{
        return new RespServer(
                new RespServerContext(host, port, commands,
                                      primaryDir, secondaryDir,
                                      enableCrossDayDebug, enableCrossDayMechanism,
                                      cacheSystemEnabled, cachePeriod, cacheEvictTimePeriod));
      }

    }
  }
  public static String getFullyDateTimeStr() {
      java.sql.Timestamp now = new java.sql.Timestamp(System.
              currentTimeMillis());
      String nowStr = now.toString();
      while (nowStr.length() < 23)
          nowStr = nowStr + "0";
      //System.out.println(nowStr);

      String timeStr = nowStr.substring(0, 4);
      timeStr += nowStr.substring(5, 7);
      timeStr += nowStr.substring(8, 10);
      timeStr += nowStr.substring(11, 13);
      timeStr += nowStr.substring(14, 16);
      timeStr += nowStr.substring(17, 19);
      timeStr += nowStr.substring(20, 23);
      return timeStr;
  }
}
