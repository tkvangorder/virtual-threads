package org.threading.imperative;

import lombok.extern.slf4j.Slf4j;

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

}
