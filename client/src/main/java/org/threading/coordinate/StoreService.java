package org.threading.coordinate;

import org.threading.coordinate.model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * A mock store service that simulates getting a user's cart and orders. The methods allow the caller to configure
 * an artificial delay and whether the call should fail. This is just to simulate what a real service might look like.
 */
public class StoreService {

  private final Cart cart;
  private final List<Order> orders;

  public StoreService() {
    // Generate some fake data, nothing fancy here.
    cart = generateCart();
    orders = generateOrders();
  }

  /**
   * Simulates getting a user's cart. This method will sleep for the specified amount of time before returning the cart.
   * If the fail parameter is true, the method will throw a RuntimeException.
   *
   * @param userId The user ID. (really just a placeholder, this method doesn't actually use it)
   * @param sleepTime The amount of time to sleep before returning the cart.
   * @param fail If true, the method will throw a RuntimeException.
   * @return The user's cart.
   */
  public Cart getUserCart(String userId, int sleepTime, boolean fail) throws InterruptedException {
    Thread.sleep(sleepTime);
    if (fail) {
      throw new RuntimeException("Failed to get cart");
    }
    return cart;
  }

  /**
   * Simulates getting a user's orders. This method will sleep for the specified amount of time before returning the orders.
   * If the fail parameter is true, the method will throw a RuntimeException.
   *
   * @param userId The user ID. (really just a placeholder, this method doesn't actually use it)
   * @param sleepTime The amount of time to sleep before returning the orders.
   * @param fail If true, the method will throw a RuntimeException.
   * @return The user's orders.
   */
  public List<Order> getUserOrders(String userId, int sleepTime, boolean fail) throws InterruptedException {
    Thread.sleep(sleepTime);
    if (fail) {
      throw new RuntimeException("Failed to get orders");
    }
    return orders;
  }

  private static Cart generateCart() {
    return Cart.builder()
        .id("1")
        .userId("1")
        .items(
            List.of(
                CartItem.builder()
                    .product(Product.builder().id("1").name("Qwixx").price(new BigDecimal("7.00")).build())
                    .quantity(2)
                    .price(new BigDecimal("14.00"))
                    .build(),
                CartItem.builder()
                    .product(Product.builder().id("2").name("Jaipur").price(new BigDecimal("22.50")).build())
                    .quantity(1)
                    .price(new BigDecimal("22.50"))
                    .build()
            )
        ).build();
  }
  private static List<Order> generateOrders() {
    return List.of(
        Order.builder()
            .id("12312414")
            .userId("1")
            .items(
                List.of(
                    OrderItem.builder()
                        .product(Product.builder().id("3").name("Yahtzee").price(new BigDecimal("9.00")).build())
                        .quantity(2)
                        .price(new BigDecimal("9.00"))
                        .build(),
                    OrderItem.builder()
                        .product(Product.builder().id("4").name("Chess").price(new BigDecimal("36.50")).build())
                        .quantity(1)
                        .price(new BigDecimal("36.50"))
                        .build()
                )
            ).build(),
        Order.builder()
            .id("3458943")
            .userId("1")
            .items(
                List.of(
                    OrderItem.builder()
                        .product(Product.builder().id("5").name("Monopoly").price(new BigDecimal("19.00")).build())
                        .quantity(1)
                        .price(new BigDecimal("19.00"))
                        .build(),
                    OrderItem.builder()
                        .product(Product.builder().id("6").name("Risk").price(new BigDecimal("33.00")).build())
                        .quantity(1)
                        .price(new BigDecimal("33.00"))
                        .build()
                )
            ).build()
    );
  }

}
