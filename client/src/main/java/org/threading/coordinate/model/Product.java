package org.threading.coordinate.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record Product(String id, String name, String description, BigDecimal price) {
}
