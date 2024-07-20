package org.threading.coordinate.model;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record Order(String id, String userId, List<OrderItem> items, Instant orderTime) {
}
