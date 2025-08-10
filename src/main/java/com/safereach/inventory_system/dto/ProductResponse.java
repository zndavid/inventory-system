package com.safereach.inventory_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Product response containing product details")
public record ProductResponse(
        @Schema(description = "Unique identifier of the product", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Name of the product", example = "Wireless Headphones")
        String name,

        @Schema(description = "Available quantity in inventory", example = "50", minimum = "0")
        Integer quantity,

        @Schema(description = "Price of the product", example = "99.99", minimum = "0.0")
        BigDecimal price
) {
}

