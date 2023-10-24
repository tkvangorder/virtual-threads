package org.threading.utils;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class ReactiveTestHarness<T> {

  private final String experimentName;
  private final int numberOfTasks;

  private final Supplier<Mono<T>> supplier;

  public static void run(String experimentName, int numberOfTasks, Supplier<Mono<String>> supplier) {
    new ReactiveTestHarness<>(experimentName, numberOfTasks, supplier).run();
  }
  private ReactiveTestHarness(String experimentName, int numberOfTasks, Supplier<Mono<T>> supplier) {
    this.experimentName = experimentName;
    this.numberOfTasks = numberOfTasks;
    this.supplier = supplier;
  }

  private void run() {
    log.info("[" + experimentName + "] Starting " + numberOfTasks + " tasks");

    List<Mono<T>> requests = new ArrayList<>();
    for (int i = 0; i < numberOfTasks; i++) {
      requests.add(supplier.get());
    }
    long start = System.currentTimeMillis();
    // Run all the requests in parallel and wait for them to complete
    List<T> results = Flux.merge(requests).toStream().toList();
    log.info("[" + experimentName + "] Response Count : " + results.size());
    recordTiming(start);
  }

  public void recordTiming(long start) {
    long time = System.currentTimeMillis() - start;
    log.info("[" + experimentName + "] Time taken: " + time + "ms");
  }

}
