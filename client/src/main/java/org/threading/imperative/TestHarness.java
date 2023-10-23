package org.threading.imperative;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class TestHarness {

  private final String experimentName;
  private final ExecutorService executorService;
  private final int numberOfTasks;

  private final Runnable runnable;

  public static void run(String experimentName, ExecutorService executorService, int numberOfTasks, Runnable runnable) {
    new TestHarness(experimentName, executorService, numberOfTasks, runnable).run();
  }
  private TestHarness(String experimentName, ExecutorService executorService, int numberOfTasks, Runnable runnable) {
    this.experimentName = experimentName;
    this.executorService = executorService;
    this.numberOfTasks = numberOfTasks;
    this.runnable = runnable;
  }

  private void run() {
    long start = System.currentTimeMillis();
    log.info("[" + experimentName + "] Starting " + numberOfTasks + " tasks");
    CompletableFuture<?>[] futures = new CompletableFuture[numberOfTasks];
    for (int i = 0; i < numberOfTasks; i++) {
      futures[i] = CompletableFuture.runAsync(runnable, executorService);
    }

    //Wait for them all to complete
    CompletableFuture.allOf(futures).join();
    recordTiming(start);
  }

  public void recordTiming(long start) {
    long time = System.currentTimeMillis() - start;
    log.info("[" + experimentName + "] Time taken: " + time + "ms");
  }

}
