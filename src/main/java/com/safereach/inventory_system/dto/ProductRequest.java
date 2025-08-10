package com.safereach.inventory_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Request object for creating or updating a product")
public record ProductRequest(
        @Schema(description = "Name of the product", example = "Wireless Headphones", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Product name cannot be blank")
        String name,

        @Schema(description = "Available quantity in inventory", example = "50", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be at least 0")
        Integer quantity,

        @Schema(description = "Price of the product", example = "99.99", minimum = "0.0", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be at least 0.0")
        BigDecimal price
) {
}
