package org.threading.coordinate;

import org.threading.coordinate.model.Cart;
import org.threading.coordinate.model.Order;
import org.threading.coordinate.model.UserDetails;
import org.threading.utils.CheckedSupplier;
import org.threading.utils.Utils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.List;

public class ReactiveExperiments {
  private final StoreService storeService = new StoreService();

  void main() {
    //BlockHound.install();
    runExperiment(this::reactive, "Using Reactor to fetch Cart and Orders in parallel");
    runExperiment(this::reactiveWithTimeouts, "Using Reactor to fetch Cart and Orders in parallel with timeouts");
  }

  /**
   * Use reactive programming to get the user's cart and orders in parallel, as an alternative to using futures
   */
  public UserDetails reactive() {

    Mono<Cart> cartMono = Mono
        .fromCallable(() -> storeService.getUserCart("fred", 1, false))
        .subscribeOn(Schedulers.parallel());

    Mono<List<Order>> ordersMono = Mono
        .fromCallable(() -> storeService.getUserOrders("fred", 3, false))
        .subscribeOn(Schedulers.parallel());

    return Mono.zip(cartMono, ordersMono).map(
            tuple -> new UserDetails("fred", tuple.getT1(), tuple.getT2())
        ).block();
  }

  public UserDetails reactiveWithTimeouts() {

    Mono<Cart> cartMono = Mono
        .fromCallable(() -> storeService.getUserCart("fred", 1, false))
        .subscribeOn(Schedulers.parallel())
        .timeout(java.time.Duration.ofSeconds(2));

    Mono<List<Order>> ordersMono = Mono
        .fromCallable(() -> storeService.getUserOrders("fred", 3, false))
        .subscribeOn(Schedulers.parallel())
        .timeout(java.time.Duration.ofSeconds(2));

    return Mono.zip(cartMono, ordersMono).map(
        tuple -> new UserDetails("fred", tuple.getT1(), tuple.getT2())
    ).block();
  }

  private void runExperiment(CheckedSupplier<UserDetails> supplier, String name) {

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

