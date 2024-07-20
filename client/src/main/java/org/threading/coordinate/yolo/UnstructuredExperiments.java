package org.threading.coordinate.yolo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnstructuredExperiments {

  public static void main(String[] args) {
    System.out.println("Unstructured experiments");
    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      executorService.submit(() -> {
        System.out.println("Hello from a virtual thread");
      });
    }
  }
}
