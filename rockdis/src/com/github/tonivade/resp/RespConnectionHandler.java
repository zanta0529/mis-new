/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.resp.protocol.RedisToken;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

@Sharable
class RespConnectionHandler extends ChannelInboundHandlerAdapter {

  private static final Logger LOGGER = LoggerFactory.getLogger(RespConnectionHandler.class);

  private final Resp impl;

  RespConnectionHandler(Resp impl) {
    this.impl = impl;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    impl.connected(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    try {
      // 透過 RedisDecoder已經將RESP Protocol 轉換成字串類型
      // ARRAY=>ImmutableArray([STRING=>AUTH, STRING=>admin1])
      impl.receive(ctx, (RedisToken) msg);
    } finally {
      ReferenceCountUtil.release(msg);
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    LOGGER.debug("channel inactive");
    impl.disconnected(ctx);
    ctx.close();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOGGER.debug("uncaught exception", cause);
    impl.disconnected(ctx);
    ctx.close();
  }
}
