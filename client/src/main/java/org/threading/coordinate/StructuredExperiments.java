package org.threading.coordinate;

import org.threading.coordinate.model.Cart;
import org.threading.coordinate.model.Order;
import org.threading.coordinate.model.UserDetails;
import org.threading.utils.CheckedSupplier;
import org.threading.utils.Utils;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

@SuppressWarnings("preview")
public class StructuredExperiments {
  private final StoreService storeService = new StoreService();

  void main() {
//    runExperiment(this::structured, "Using Structured Concurrency to fetch Cart and Orders in parallel", true);
//    runExperiment(this::structuredWithTimeouts, "Using Structured Concurrency and setting timeouts", true);
//    runExperiment(this::structuredTreeOfWork, "Using Structured Concurrency to fetch multiple user details in parallel", false);
    runExperiment(this::firstToSucceed, "Using Structured Concurrency to fetch the first successful request", true);

  }

  /**
   * Use Java's structured concurrency to get the user's cart and orders in parallel, as an alternative to using futures
   * or reactive programming
   */
  public UserDetails structured() throws Exception {

    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      StructuredTaskScope.Subtask<Cart> cartTask = scope.fork(
          () -> storeService.getUserCart("fred", 1, true)
      );
      StructuredTaskScope.Subtask<List<Order>> ordersTask = scope.fork(
          () -> storeService.getUserOrders("fred", 3, false)
      );
      scope.join().throwIfFailed();

      return new UserDetails("fred", cartTask.get(), ordersTask.get());
    }
  }

  /**
   * Use Java's structured concurrency to get the user's cart and orders in parallel and set a time limit for the
   * operation to succeed.
   */
  public UserDetails structuredWithTimeouts() throws Exception {

    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      StructuredTaskScope.Subtask<Cart> cartTask = scope.fork(
          () -> storeService.getUserCart("fred", 1, false)
      );
      StructuredTaskScope.Subtask<List<Order>> ordersTask = scope.fork(
          () -> storeService.getUserOrders("fred", 3, false)
      );
      scope
          .joinUntil(Instant.now().plusSeconds(2))
          .throwIfFailed();

      return new UserDetails("fred", cartTask.get(), ordersTask.get());
    }
  }

  /**
   * Use Java's structured concurrency create a parent/child structure for concurrent tasks.
   */
  public List<UserDetails> structuredTreeOfWork() throws Exception {

    try (var topLevel = new StructuredTaskScope.ShutdownOnFailure()) {

      var userDetailsTasks = List.of("fred", "sean", "priya").stream()
          .map(userId -> topLevel.fork(() -> getUserDetails(userId, 1)))
          .toList();

      topLevel.join().throwIfFailed();

      return userDetailsTasks.stream()
          .map(StructuredTaskScope.Subtask::get)
          .toList();
    }
  }

  /**
   * Use Java's structured concurrency issue three concurrent tasks and return the result of the first one to succeed.
   */
  public UserDetails firstToSucceed() throws Exception {

    try (var topLevel = new StructuredTaskScope.ShutdownOnSuccess<UserDetails>()) {

      var userDetailsTasks = Stream.of("fred", "sam", "fred")
          .map(userId -> topLevel.fork(() -> getUserDetails(userId, "sam".equals(userId) ? 1 : 2)))
          .toList();

      topLevel.join();
      System.out.println("First task to succeed: " + topLevel.result().userId());
      return topLevel.result();
    }
  }

  private UserDetails getUserDetails(String userId, int sleepTimeSeconds) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      StructuredTaskScope.Subtask<Cart> cartTask = scope.fork(
          () -> storeService.getUserCart(userId, sleepTimeSeconds, false)
      );
      StructuredTaskScope.Subtask<List<Order>> ordersTask = scope.fork(
          () -> storeService.getUserOrders(userId, sleepTimeSeconds, false)
      );
      scope.join().throwIfFailed();

      return new UserDetails(userId, cartTask.get(), ordersTask.get());
    }
  }


  private void runExperiment(CheckedSupplier<?> supplier, String name, boolean printResult) {

    System.out.println("Running experiment: " + name);

    Instant start = Instant.now();
    try {
      Object result = supplier.get();
      if (printResult) {
        System.out.println("Experiment Result : " + result.toString());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Time taken: " + Utils.timeElapsed(start));
  }

}
