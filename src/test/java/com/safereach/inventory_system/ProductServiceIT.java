package com.safereach.inventory_system;

import com.safereach.inventory_system.dto.ProductRequest;
import com.safereach.inventory_system.dto.ProductResponse;
import com.safereach.inventory_system.dto.ProductSummaryResponse;
import com.safereach.inventory_system.entity.Product;
import com.safereach.inventory_system.repository.ProductRepository;
import com.safereach.inventory_system.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@Import({
        TestcontainersConfiguration.class,
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("integration-test")
public class ProductServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>(
                    DockerImageName.parse("postgis/postgis:16-3.4-alpine")
                            .asCompatibleSubstituteFor("postgres"));

    private static final UUID ID_SEARCH       = UUID.fromString("00000000-0000-0000-0000-000000000100");
    private static final UUID ID_SEARCH_OTHER = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID ID_UPDATE       = UUID.fromString("00000000-0000-0000-0000-000000000102");
    private static final UUID ID_DELETE       = UUID.fromString("00000000-0000-0000-0000-000000000103");
    private static final UUID ID_SUMMARY_A    = UUID.fromString("00000000-0000-0000-0000-000000000104");
    private static final UUID ID_SUMMARY_B    = UUID.fromString("00000000-0000-0000-0000-000000000105");
    private static final UUID ID_SUMMARY_C    = UUID.fromString("00000000-0000-0000-0000-000000000106");

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanup() {
        productRepository.deleteAll();
    }

    @Test
    void whenCreateProduct_thenCanFindIt() {
        ProductRequest request = new ProductRequest("Widget", 5, BigDecimal.valueOf(9.99));
        ProductResponse response = productService.createProduct(request);

        assertNotNull(response.id());
        assertEquals("Widget", response.name());
        var entity = productRepository.findById(response.id());
        assertTrue(entity.isPresent());
        assertEquals(5, entity.get().getQuantity());
    }

    @Test
    void whenSearchByName_thenReturnsMatching() {
        productRepository.save(new Product(ID_SEARCH, "Alpha", 1, BigDecimal.ONE));
        productRepository.save(new Product(ID_SEARCH_OTHER, "Beta", 2, BigDecimal.TEN));

        var results = productService.searchProductByName("alpha");
        assertThat(results).hasSize(1)
                .first().extracting(ProductResponse::name).isEqualTo("Alpha");
    }

    @Test
    void whenUpdateQuantity_thenValueChanges() {
        productRepository.save(new Product(ID_UPDATE, "Gadget", 1, BigDecimal.ONE));
        ProductResponse updated = productService.updateProductQuantity(ID_UPDATE, 10);

        assertEquals(10, updated.quantity());
        assertEquals(10, productRepository.findById(ID_UPDATE).get().getQuantity());
    }

    @Test
    void whenDeleteProduct_thenCannotFindIt() {
        productRepository.save(new Product(ID_DELETE, "ToDelete", 1, BigDecimal.ONE));
        productService.deleteProduct(ID_DELETE);
        assertFalse(productRepository.existsById(ID_DELETE));
    }

    @Test
    void whenGetProductSummary_thenCorrectTotals() {
        productRepository.save(new Product(ID_SUMMARY_A, "A", 0, BigDecimal.TEN));
        productRepository.save(new Product(ID_SUMMARY_B, "B", 5, BigDecimal.TEN));
        productRepository.save(new Product(ID_SUMMARY_C, "C", 10, BigDecimal.TEN));

        ProductSummaryResponse summary = productService.getProductSummary();
        assertEquals(3, summary.totalProducts());
        assertEquals(15, summary.totalQuantity());
        assertEquals(BigDecimal.TEN, summary.averagePrice());
        assertThat(summary.outOfStockProductList())
                .hasSize(1)
                .first().extracting("name").isEqualTo("A");
    }
}
