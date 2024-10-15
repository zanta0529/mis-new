/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp.protocol;

import com.github.tonivade.resp.RespServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisEncoder extends MessageToByteEncoder<RedisToken> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RedisEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, RedisToken msg, ByteBuf out) throws Exception {
//    LOGGER.debug("RedisToken msg to encoder : {}", msg);
    out.writeBytes(RedisSerializer.encodeToken(msg));
  }
}
