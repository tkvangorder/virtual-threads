package org.threading.coordinate.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItem(Product product, int quantity, BigDecimal price) {
}
