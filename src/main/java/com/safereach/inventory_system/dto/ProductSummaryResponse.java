package com.safereach.inventory_system.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "Inventory summary response containing statistics about all products")
public record ProductSummaryResponse(
        @Schema(description = "Total number of products in inventory", example = "5")
        long totalProducts,

        @Schema(description = "Total quantity of all products combined", example = "78")
        long totalQuantity,

        @Schema(description = "Average price of all products", example = "219.99")
        BigDecimal averagePrice,

        @Schema(description = "List of products that are out of stock (quantity = 0)")
        List<OutOfStockProduct> outOfStockProductList
) {

    @Schema(description = "Product information for out-of-stock items")
    public record OutOfStockProduct(
            @Schema(description = "Unique identifier of the product", example = "550e8400-e29b-41d4-a716-446655440000")
            UUID id,

            @Schema(description = "Name of the product", example = "Monitor")
            String name
    ) {
    }
}
