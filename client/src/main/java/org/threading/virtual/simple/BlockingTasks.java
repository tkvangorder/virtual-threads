package org.threading.virtual.simple;

import lombok.extern.slf4j.Slf4j;
import org.threading.utils.TestHarness;
import org.threading.utils.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This test will compare the performance of traditional thread, virtual threads and a thread pool with working
 * with a task that simulates blocking IO.
 */
@Slf4j
public class BlockingTasks {

  private static AtomicLong taskCount = new AtomicLong(0);

  public static void main(String[] args) {

    // This test harness compares the performance:
    //
    // Creating a traditional thread for each blocking task.
    // Creating a virtual thread for each blocking task.
    // Using a thread pool (Size 100) for each blocking task.


    //Run all three experiments with 900 traditional tasks, 900 virtual tasks, and 200 thread pool tasks. We don't want
    runExperiments(900, 900, 200);

    //Bump up the virtual threads by a magnitude of 10, skip the pool tasks.
    runExperiments(900, 9000, 0);

    //Bump up the virtual threads by a magnitude of 1000, skip the pool tasks.
    runExperiments(900, 900_000, 0);

    //Ah oh, the VM is not happy with 9000 traditional threads.
    runExperiments(9000, 0, 0);

    Utils.waitForPrompt("Press enter to exit");
  }

  public static void runExperiments(int traditionalTasks, int virtualTasks, int poolTasks) {

    taskCount = new AtomicLong(0);

    Utils.waitForPrompt("Press enter to start Experiment traditionalTask [" +
        traditionalTasks + "] virtualTasks [" + virtualTasks + "] poolTasks [" + poolTasks + "]");

    if (traditionalTasks > 0) {
      try (ExecutorService executorService = Utils.traditionalUnboundedExecutorService()) {
        TestHarness.run("Blocking, Unbounded Threads", executorService,
            traditionalTasks, BlockingTasks::sleepTask);
      }
    }
    if (virtualTasks > 0) {
      try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
        TestHarness.run("Blocking, Virtual Threads", executorService,
            virtualTasks, BlockingTasks::sleepTask);
      }
    }

    if (poolTasks > 0) {
      try (ExecutorService executorService = Executors.newFixedThreadPool(100)) {
        TestHarness.run("Blocking, Thread Pool", executorService,
            poolTasks, BlockingTasks::sleepTask);
      }
    }

    log.info("Total tasks executed: " + taskCount.get());

    // Blank line for readability
    System.out.println();
  }
  public static void sleepTask() {
    //log.info("Starting blocking task, going to sleep.");
    try {
      taskCount.incrementAndGet();
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }


}
