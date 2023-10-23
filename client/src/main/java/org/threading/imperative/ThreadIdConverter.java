package org.threading.imperative;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ThreadIdConverter extends ClassicConverter {
  @Override
  public String convert(ILoggingEvent iLoggingEvent) {
    if ("".equals(Thread.currentThread().getName())) {
      return Thread.currentThread().toString();
    }
    return Thread.currentThread().getName();
  }
}
