/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp.command;

//import Rockdis.Rockdis;
import rocksdbapi.Rockdis.*;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.resp.CachingSystem.CachingSystem;
import com.github.tonivade.resp.CachingSystem.InorderSystem;
import com.github.tonivade.resp.CachingSystem.LargeCachePoolSystem;
import com.github.tonivade.resp.Model.ChannelStatus;

import java.util.Map;

public interface ServerContext {
  String getHost();
  int getPort();
  int getClients();
  RespCommand getCommand(String name);
  <T> Option<T> getValue(String key);
  void putValue(String key, Object value);
  <T> Option<T> removeValue(String key);
  void setAuthStatus(boolean status);
  boolean getAuthStatus();
  String getAuthString();
  Rockdis getRockdisInstance();
  Rockdis getSecondRockdisInstance();
  void setFlushAll();
  ChannelStatus getChannelInfo(String channelID);
  boolean getCacheSystemStatus();
  CachingSystem getCacheSystem();
  LargeCachePoolSystem getLargeCachePoolSystem();
  InorderSystem getInorderSystem();
  void setChannelInfo(String channelID, ChannelStatus channelStatus);

  boolean getSafemode();

  int getCachEvictTimePeriod();

  void setSafemode(boolean safemode);

}
