package org.threading.coordinate;

import org.threading.coordinate.model.Cart;
import org.threading.coordinate.model.Order;
import org.threading.coordinate.model.UserDetails;
import org.threading.utils.CheckedSupplier;
import org.threading.utils.Utils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;

public class UnstructuredExperiments {

  private final StoreService storeService = new StoreService();

  void main() {

//    runExperiment(this::futures, "Using Futures");
//    runExperiment(this::futuresAndTimeouts, "Using Futures with Timeouts");
//    runExperiment(this::completableFutures, "Using Completable Futures");
//    runExperiment(this::completableFuturesFailOnAny, "Using Completable Futures with failure handling");
//    parentChildMess();
  }

  /**
   * A naive implementation that uses Futures with an executor service. Things work fine on the "happy path" when
   * no errors are thrown, but if one of the tasks fails, the other task is still running. Imagine a world in a high
   * load service where this type of error handling could result in all threads being "leaked".
   */
  public UserDetails futures() throws Exception {

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

      Future<Cart> cartFuture = executorService.submit(
          () -> storeService.getUserCart("fred", 1, false)
      );
      Future<List<Order>> ordersFuture = executorService.submit(
          () -> storeService.getUserOrders("fred", 3, false)
      );

      return new UserDetails("fred", cartFuture.get(), ordersFuture.get());
    }
  }

  /**
   * Using futures and attempting put a time limit on the calls. Responsibility is on the developer to not only set the
   * time limits but also to handle the case where the futures do not complete in time, they must also cancel them.
   * Try running this experiment with/without the cancel calls.
   */
  public UserDetails futuresAndTimeouts() throws Exception {

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

      Future<Cart> cartFuture = executorService.submit(
          () -> storeService.getUserCart("fred", 1, false)
      );
      Future<List<Order>> ordersFuture = executorService.submit(
          () -> storeService.getUserOrders("fred", 10, false)
      );

      try {
        return new UserDetails(
            "fred",
            cartFuture.get(2, TimeUnit.SECONDS),
            ordersFuture.get(2, TimeUnit.SECONDS)
        );
      } catch (TimeoutException e) {
        System.out.println("Timed out waiting for futures to complete");
        cartFuture.cancel(true);
        ordersFuture.cancel(true);
        throw new RuntimeException("Timed out waiting for futures to complete", e);
      }
    }
  }

  /**
   * Using CompletableFutures instead of submitting work to an executor service.
   */
  public UserDetails completableFutures() throws Exception {

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

      CompletableFuture<Cart> cartFuture = CompletableFuture.supplyAsync(
          () -> storeService.getUserCart("fred", 1, false), executorService
      );
      CompletableFuture<List<Order>> ordersFuture = CompletableFuture.supplyAsync(
          () -> storeService.getUserOrders("fred", 4, false), executorService
      );
      // Wait for both operations to complete (either with or without an error)
      CompletableFuture.allOf(cartFuture, ordersFuture).join();
      System.out.println("Both futures completed");
      return new UserDetails("fred", cartFuture.get(), ordersFuture.get());
    }
  }

  /**
   * Using CompletableFutures attempt to short circuit the operation if any of the futures fail.
   * <P>
   * As an exercise, can you modify this experiment such that all operations abort upon the first failure?
   */
  public UserDetails completableFuturesFailOnAny() throws Exception {

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

      CompletableFuture<Cart> cartFuture = CompletableFuture.supplyAsync(
          () -> storeService.getUserCart("fred", 1, true), executorService
      );
      CompletableFuture<List<Order>> ordersFuture = CompletableFuture.supplyAsync(
          () -> storeService.getUserOrders("fred", 4, false), executorService
      );

      CompletableFuture<Void> failure = new CompletableFuture<>();
      cartFuture.exceptionally(e -> {
        failure.completeExceptionally(e);
        return null;
      });
      ordersFuture.exceptionally(e -> {
        failure.completeExceptionally(e);
        return null;
      });

      try {
        CompletableFuture.anyOf(failure, CompletableFuture.allOf(cartFuture, ordersFuture)).join();
      } catch (Exception e) {
        System.out.println("Should get here is any of the futures fail!");
        throw e;
      }
      return new UserDetails("fred", cartFuture.get(), ordersFuture.get());
    }
  }

  /**
   * This experiment creates a parent executor that then calls a nested child executor where the cart call will
   * fail after one second but the orders call will sleep for 10 seconds.
   * <P>
   * As an exercise, use jcmd to take a thread dump AFTER cart call has failed but before the orders call has completed.
   * 1) Start the application, it will prompt you to press enter before starting the experiment.
   * 2) `jcmd` with no arguments will list all the running JVMs and their PIDs.
   * 3) Press enter and wait for the messages "ah oh, something went wrong".
   * 4) `jcmd <pid> Thread.dump_to_file ~/thread.json -overwrite`
   */
  public UserDetails parentChildMess() {

    Utils.waitForPrompt("Press enter to start");

    try (ExecutorService executorParent = Utils.newVirtualThreadExecutor("parent-task-")) {
      for (int i = 0; i < 10; i++) {
        executorParent.submit(() -> {
          try (ExecutorService executorChild = Utils.newVirtualThreadExecutor("child-task-")) {

            Future<Cart> cartFuture = executorChild.submit(
                () -> storeService.getUserCart("fred", 1, true)
            );
            Future<List<Order>> ordersFuture = executorChild.submit(
                () -> storeService.getUserOrders("fred", 10, false)
            );

            try {
              return new UserDetails("fred", cartFuture.get(), ordersFuture.get());
            } catch (Exception e) {
              System.out.println("ah oh, something went wrong");
              throw e;
            }
          }

        });
      }
    }
    return null;
  }


  private void runExperiment(CheckedSupplier<?> supplier, String name) {

    System.out.println("Running experiment: " + name);

    Instant start = Instant.now();
    try {
      supplier.get();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Time taken: " + Utils.timeElapsed(start));
  }

}