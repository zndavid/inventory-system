package com.safereach.inventory_system.repository;

import com.safereach.inventory_system.dto.ProductSummaryResponse;
import com.safereach.inventory_system.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    /* * Finds products by name containing the specified string, ignoring case.
     *
     * Found here: https://stackoverflow.com/questions/37524599/jpa-findby-field-ignore-case
     *
     * @param name the name to search for
     * @return a list of products that match the search criteria
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);

    @Query("""
             SELECT
              COUNT(p) AS totalProducts,
              SUM(p.quantity) AS totalQuantity,
              AVG(p.price) AS averagePrice
             FROM Product p
            """)
    ProductSummaryProjection getProductSummary();

    List<ProductSummaryResponse.OutOfStockProduct> findByQuantity(Integer quantity);

    interface ProductSummaryProjection {
        long getTotalProducts();

        long getTotalQuantity();

        BigDecimal getAveragePrice();
    }
}
