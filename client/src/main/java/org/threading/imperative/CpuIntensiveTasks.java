package org.threading.imperative;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

import static java.lang.Math.*;

@Slf4j
public class CpuIntensiveTasks {

  public static void main(String[] args) {
    Utils.waitForPrompt("Press enter to start CPU Intensive Tests");

    try (ExecutorService executorService = Utils.traditionalUnboundedExecutorService()) {
      TestHarness.run("CPU Intensive, Unbounded Threads", executorService,
          100, CpuIntensiveTasks::cpuIntensiveTask);
    }

    try (ExecutorService executorService = Executors.newFixedThreadPool(20)) {
      TestHarness.run("CPU Intensive, Thread Pool", executorService,
          100, CpuIntensiveTasks::cpuIntensiveTask);
    }
    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      TestHarness.run("CPU Intensive, Virtual Threads", executorService,
          100, CpuIntensiveTasks::cpuIntensiveTask);
    }

    Utils.waitForPrompt("Press enter to exit");
  }

  public static void cpuIntensiveTask() {
    log.info("Starting CPU intensive task");
    for (int i = 0; i < 10_000_000; i++) {
      double d = tan(atan(tan(atan(tan(atan(tan(atan(tan(atan(123456789.123456789))))))))));
      cbrt(d);
    }
  }


}
