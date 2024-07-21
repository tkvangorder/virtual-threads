package org.threading.coordinate.model;

import lombok.Builder;
import lombok.With;

import java.time.Instant;
import java.util.List;

@Builder
@With
public record Order(String id, String userId, List<OrderItem> items, Instant orderTime) {
}
