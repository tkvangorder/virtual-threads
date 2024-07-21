package org.threading.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.*;

@Slf4j
public class Utils {
  public static Scanner scanner = new Scanner(System.in);

  public static void waitForPrompt(String message) {
    log.info(message + " (Press enter to continue)");
    scanner.nextLine();
  }

  public static ExecutorService traditionalUnboundedExecutorService() {
    return new ThreadPoolExecutor(
        0, 100000, 0, TimeUnit.SECONDS, new SynchronousQueue<>());
  }

  public static String timeElapsed(Instant start) {
    Duration duration = Duration.between(start, Instant.now());
    return StringTemplate.STR."\{duration.toSeconds()}s \{duration.toMillisPart()}ms";
  }

  public static void sleep(long sleepTimeSeconds) {
    if (sleepTimeSeconds <= 0) {
      return;
    }
    try {
      Thread.sleep(sleepTimeSeconds * 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static ExecutorService newVirtualThreadExecutor(String threadPrefix) {
    ThreadFactory factory = Thread.ofVirtual().name(threadPrefix, 0).factory();
    return Executors.newThreadPerTaskExecutor(factory);
  }
}
