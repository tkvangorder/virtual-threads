package org.threading.simple;

import lombok.extern.slf4j.Slf4j;
import org.threading.utils.TestHarness;
import org.threading.utils.Utils;

import java.util.concurrent.*;

import static java.lang.Math.*;

/**
 * This test will compare the performance of traditional threads, virtual threads and a thread pool with working
 * with a task that is CPU intensive
 */
@Slf4j
public class CpuIntensiveTasks {

  public static void main(String[] args) {

    int taskCount = 100;

    Utils.waitForPrompt("Press enter to start " + taskCount + " CPU intensive tasks using a traditional threads.");

    try (ExecutorService executorService = Utils.traditionalUnboundedExecutorService()) {
      TestHarness.run("CPU Intensive, Unbounded Threads", executorService,
          taskCount, CpuIntensiveTasks::cpuIntensiveTask);
    }

    System.out.println();
    Utils.waitForPrompt("Press enter to start " + taskCount + " CPU intensive tasks using a thread pool of 20 threads.");

    try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {
      TestHarness.run("CPU Intensive, Thread Pool", executorService,
          taskCount, CpuIntensiveTasks::cpuIntensiveTask);
    }

    System.out.println();
    Utils.waitForPrompt("Press enter to start " + taskCount + " CPU intensive tasks using a traditional threads.");

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      TestHarness.run("CPU Intensive, Virtual Threads", executorService,
          taskCount, CpuIntensiveTasks::cpuIntensiveTask);
    }

    Utils.waitForPrompt("Press enter to exit");
  }

  public static void cpuIntensiveTask() {
    log.info("Starting CPU intensive task");
    for (int i = 0; i < 5_000_000; i++) {
      double d = tan(atan(tan(atan(tan(atan(tan(atan(tan(atan(123456789.123456789))))))))));
      cbrt(d);
    }
  }


}
