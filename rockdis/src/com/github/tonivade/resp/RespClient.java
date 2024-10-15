/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp;

import static com.github.tonivade.resp.protocol.RedisToken.array;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.util.concurrent.TimeUnit;

import static com.github.tonivade.resp.Util.ChannelStrategy.*;

import com.github.tonivade.resp.Util.ChannelStrategy;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.resp.protocol.RedisDecoder;
import com.github.tonivade.resp.protocol.RedisEncoder;
import com.github.tonivade.resp.protocol.RedisToken;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class RespClient implements Resp {

  private static final Logger LOGGER = LoggerFactory.getLogger(RespClient.class);

  private static final int BUFFER_SIZE = 1024 * 1024;
  private static final int MAX_FRAME_SIZE = BUFFER_SIZE * 100;

  private Bootstrap bootstrap;
//  private KQueueEventLoopGroup workerGroup;
  private EventLoopGroup workerGroup;
  private ChannelFuture future;
  private ChannelHandlerContext context;

  private final int port;
  private final String host;
  private final RespCallback callback;

  public RespClient(String host, int port, RespCallback callback) {
    this.host = requireNonNull(host);
    this.port = requireRange(port, 1024, 65535);
    this.callback = requireNonNull(callback);
  }

  public void start() {

    Strategy strategy = ChannelStrategy.getChannelStrategy();
    if (strategy.equals(Strategy.EPOLL)) {
      workerGroup = new EpollEventLoopGroup(Runtime.getRuntime().availableProcessors() );
    } else if (strategy.equals(Strategy.KQUEUE)) {
      workerGroup = new KQueueEventLoopGroup(Runtime.getRuntime().availableProcessors() );
    } else if (strategy.equals(Strategy.NIO)){
      workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() );
    }

    bootstrap = new Bootstrap().group(workerGroup);

    if (strategy.equals(Strategy.EPOLL)) {
      bootstrap.channel(EpollServerSocketChannel.class);
    } else if (strategy.equals(Strategy.KQUEUE)) {
      bootstrap.channel(KQueueServerSocketChannel.class);
    } else if (strategy.equals(Strategy.NIO)){
      bootstrap.channel(NioServerSocketChannel.class);
    }

    bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.SO_RCVBUF, BUFFER_SIZE)
        .option(ChannelOption.SO_SNDBUF, BUFFER_SIZE)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .handler(new RespInitializerHandler(this));

    future = connect().addListener(new ConnectionListener(this));
  }

  public void stop() {
    try {
      if (future != null) {
        future.channel().close().syncUninterruptibly();
        future = null;
      }
    } finally {
      if (workerGroup != null) {
        workerGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup = null;
      }
    }
  }

  @Override
  public void channel(SocketChannel channel) {
    LOGGER.info("connected to server: {}:{}", host, port);
    channel.pipeline().addLast("redisEncoder", new RedisEncoder());
    channel.pipeline().addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
    channel.pipeline().addLast("linDelimiter", new RedisDecoder(MAX_FRAME_SIZE));
    channel.pipeline().addLast(new RespConnectionHandler(this));
  }

  @Override
  public void connected(ChannelHandlerContext ctx) {
    LOGGER.info("channel active");
    this.context = ctx;
    callback.onConnect();
  }

  @Override
  public void disconnected(ChannelHandlerContext ctx) {
    LOGGER.info("client disconected from server: {}:{}", host, port);
    if (this.context != null) {
      callback.onDisconnect();
      this.context = null;
      if (future != null) {
        future.channel().eventLoop().schedule(this::start, 1L, TimeUnit.SECONDS);
      }
    }
  }

  public void send(String... message) {
    send(array(asList(message).stream().map(RedisToken::string).collect(toList())));
  }

  public void send(RedisToken message) {
    writeAndFlush(message);
  }

  @Override
  public void receive(ChannelHandlerContext ctx, RedisToken message) {
    callback.onMessage(message);
  }

  private ChannelFuture connect() {
    LOGGER.info("trying to connect");
    return bootstrap.connect(host, port);
  }

  private void writeAndFlush(Object message) {
    if (context != null) {
      context.writeAndFlush(message);
    }
  }

  private int requireRange(int value, int min, int max) {
    if (value <= min || value > max) {
      throw new IllegalArgumentException(min + " <= " + value + " < " + max);
    }
    return value;
  }
}
