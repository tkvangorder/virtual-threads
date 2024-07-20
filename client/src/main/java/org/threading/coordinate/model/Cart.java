package org.threading.coordinate.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Cart(String id, String userId, List<CartItem> items) {
}
