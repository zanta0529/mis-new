/*
 * Copyright (c) 2015-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.resp.command;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.github.tonivade.purefun.Producer;
import com.github.tonivade.resp.command.server.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tonivade.purefun.type.Try;
import com.github.tonivade.resp.annotation.Command;

public class CommandSuite {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommandSuite.class);

  private final Map<String, Class<?>> metadata = new HashMap<>();
  private final Map<String, RespCommand> commands = new HashMap<>();

  private final NullCommand nullCommand = new NullCommand();

  private final CommandWrapperFactory factory;

  public CommandSuite() {
    this(new DefaultCommandWrapperFactory());
  }

  public CommandSuite(CommandWrapperFactory factory) {
    this.factory = factory;
    addCommand(PingCommand::new);
    addCommand(EchoCommand::new);
    addCommand(QuitCommand::new);
    addCommand(TimeCommand::new);
//    addCommand(SetTestCommand::new);
//    addCommand(SetCommand::new);
//    addCommand(AuthCommand::new);
  }

  public RespCommand getCommand(String name) {
    return commands.getOrDefault(name.toLowerCase(), nullCommand);
  }

  public boolean isPresent(String name, Class<? extends Annotation> annotationClass) {
    return getMetadata(name).isAnnotationPresent(annotationClass);
  }

  public boolean contains(String name) {
    return commands.get(name) != null;
  }

  @Deprecated
  protected void addCommand(Class<?> clazz) {
    addCommand(clazz::newInstance);
  }

  protected void addCommand(Producer<?> newInstance) {
    Try.of(newInstance)
       .onSuccess(this::processCommand)
       .onFailure(e -> LOGGER.error("error loading command", e));
  }

  public Map<String, RespCommand> getAllCommands() {
    return commands;
  }

  protected void addCommand(String name, RespCommand command) {
    commands.put(name.toLowerCase(), factory.wrap(command));
  }

  private void processCommand(Object command) {
    Class<?> clazz = command.getClass();
    Command annotation = clazz.getAnnotation(Command.class);
    if (annotation != null) {
      commands.put(annotation.value(), factory.wrap(command));
      metadata.put(annotation.value(), clazz);
    } else {
      LOGGER.warn("annotation not present at {}", clazz.getName());
    }
  }

  private Class<?> getMetadata(String name) {
    return metadata.getOrDefault(name.toLowerCase(), Void.class);
  }
}
