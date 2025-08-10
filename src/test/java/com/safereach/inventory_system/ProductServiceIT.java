package com.safereach.inventory_system;

import com.safereach.inventory_system.dto.ProductRequest;
import com.safereach.inventory_system.dto.ProductResponse;
import com.safereach.inventory_system.dto.ProductSummaryResponse;
import com.safereach.inventory_system.repository.ProductRepository;
import com.safereach.inventory_system.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration-test")
class ProductServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:16-3.4-alpine")
                            .asCompatibleSubstituteFor("postgres"));

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanup() {
        productRepository.deleteAll();
    }

    private ProductResponse create(String name, int qty, BigDecimal price) {
        return productService.createProduct(new ProductRequest(name, qty, price));
    }

    @Test
    void whenCreateProduct_thenCanFindIt() {
        ProductResponse created = create("Widget", 5, BigDecimal.valueOf(9.99));

        assertNotNull(created.id());
        assertEquals("Widget", created.name());
        assertTrue(productRepository.findById(created.id()).isPresent());
        assertEquals(5, productRepository.findById(created.id()).get().getQuantity());
    }

    @Test
    void whenSearchByName_thenReturnsMatching() {
        create("Alpha", 1, BigDecimal.ONE);
        create("Beta", 2, BigDecimal.TEN);

        var results = productService.searchProductByName("alpha");
        assertThat(results).hasSize(1)
                .first().extracting(ProductResponse::name).isEqualTo("Alpha");
    }

    @Test
    void whenUpdateQuantity_thenValueChanges() {
        ProductResponse created = create("Gadget", 1, BigDecimal.ONE);

        ProductResponse updated = productService.updateProductQuantity(created.id(), 10);

        assertEquals(10, updated.quantity());
        assertEquals(10, productRepository.findById(created.id()).get().getQuantity());
    }

    @Test
    void whenDeleteProduct_thenCannotFindIt() {
        ProductResponse created = create("ToDelete", 1, BigDecimal.ONE);

        productService.deleteProduct(created.id());

        assertFalse(productRepository.existsById(created.id()));
    }

    @Test
    void whenGetProductSummary_thenCorrectTotals() {
        create("A", 0, BigDecimal.TEN);
        create("B", 5, BigDecimal.TEN);
        create("C", 10, BigDecimal.TEN);

        ProductSummaryResponse summary = productService.getProductSummary();

        assertEquals(3, summary.totalProducts());
        assertEquals(15, summary.totalQuantity());
        assertEquals(BigDecimal.TEN.setScale(1, RoundingMode.HALF_UP), summary.averagePrice());
        assertThat(summary.outOfStockProductList())
                .hasSize(1)
                .first().extracting("name").isEqualTo("A");
    }
}
