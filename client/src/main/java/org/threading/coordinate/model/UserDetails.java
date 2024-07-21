package org.threading.coordinate.model;

import java.util.List;

public record UserDetails(String userId, Cart cart, List<Order> orders) {

}
