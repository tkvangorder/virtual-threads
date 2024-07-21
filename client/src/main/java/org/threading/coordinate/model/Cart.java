package org.threading.coordinate.model;

import lombok.Builder;
import lombok.With;

import java.util.List;

@Builder
@With
public record Cart(String id, String userId, List<CartItem> items) {
}
