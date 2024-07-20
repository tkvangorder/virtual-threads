package org.threading.coordinate.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItem(Product product, int quantity, BigDecimal price) {
}
