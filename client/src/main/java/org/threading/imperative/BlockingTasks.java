package org.threading.imperative;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class BlockingTasks {

  public static void main(String[] args) {
    Utils.waitForPrompt("Press enter to start Blocking Tests");

    try (ExecutorService executorService = Utils.traditionalUnboundedExecutorService()) {
      TestHarness.run("Blocking, Unbounded Threads", executorService,
          8000, BlockingTasks::sleepTask);
    }

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      TestHarness.run("Blocking, Virtual Threads", executorService,
          90000, BlockingTasks::sleepTask);
    }

    try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {
      TestHarness.run("Blocking, Thread Pool", executorService,
          9000, BlockingTasks::sleepTask);
    }

    Utils.waitForPrompt("Press enter to exit");
  }

  public static void sleepTask() {
    //log.info("Starting blocking task, going to sleep.");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


}
