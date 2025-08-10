package com.safereach.inventory_system.service;

import com.safereach.inventory_system.dto.ProductRequest;
import com.safereach.inventory_system.dto.ProductResponse;
import com.safereach.inventory_system.dto.ProductSummaryResponse;
import com.safereach.inventory_system.entity.Product;
import com.safereach.inventory_system.exception.ProductAlreadyExistsException;
import com.safereach.inventory_system.exception.ProductNotFoundException;
import com.safereach.inventory_system.mapper.ProductMapper;
import com.safereach.inventory_system.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTests {
    private static final String PRODUCT_NAME = "Test Product";
    private static final String PRODUCT_NAME_2 = "Another Test Product";
    private static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(100L);
    private static final int PRODUCT_QUANTITY = 10;
    private static final UUID PRODUCT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID PRODUCT_ID_2 = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Test
    void givenProductRequest_whenCreateProduct_thenReturnProductResponse() {
        ProductRequest request = new ProductRequest(PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);
        Product productEntity = new Product();
        ProductResponse expected = new ProductResponse(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);

        when(productMapper.toEntity(request)).thenReturn(productEntity);
        when(productRepository.save(any(Product.class))).thenReturn(productEntity);
        when(productMapper.toResponse(productEntity)).thenReturn(expected);


        ProductResponse actual = productService.createProduct(request);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void givenExistingName_whenCreateProduct_thenThrowProductAlreadyExistsException() {
        ProductRequest request = new ProductRequest(PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);

        when(productRepository.existsByName(PRODUCT_NAME)).thenReturn(true);

        assertThrows(ProductAlreadyExistsException.class,
                () -> productService.createProduct(request));
        verify(productRepository, never()).save(any());
    }

    @Test
    void givenProducts_whenGetAllProducts_thenReturnProductResponses() {
        // prepare sample products
        Product product1 = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);
        Product product2 = new Product(
                PRODUCT_ID_2,
                PRODUCT_NAME_2,
                PRODUCT_QUANTITY + 5,
                PRODUCT_PRICE.add(BigDecimal.valueOf(50))
        );
        List<Product> products = List.of(product1, product2);

        // create a pageable and wrap products in a Page
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(products, pageable, products.size());

        // expected responses
        ProductResponse response1 = new ProductResponse(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);
        ProductResponse response2 = new ProductResponse(
                PRODUCT_ID_2,
                PRODUCT_NAME_2,
                PRODUCT_QUANTITY + 5,
                PRODUCT_PRICE.add(BigDecimal.valueOf(50))
        );

        // stubbing
        when(productRepository.findAll(pageable)).thenReturn(page);
        when(productMapper.toResponse(product1)).thenReturn(response1);
        when(productMapper.toResponse(product2)).thenReturn(response2);

        // execute
        Page<ProductResponse> actual = productService.getAllProducts(pageable);

        // verify
        assertNotNull(actual);
        assertEquals(2, actual.getTotalElements());
        assertEquals(List.of(response1, response2), actual.getContent());
        verify(productRepository).findAll(pageable);
    }

    @Test
    void givenName_whenSearchProductByName_thenReturnProductResponses() {
        String searchName = "Test";
        Product product1 = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);
        Product product2 = new Product(
                PRODUCT_ID_2,
                PRODUCT_NAME_2,
                PRODUCT_QUANTITY + 5,
                PRODUCT_PRICE.add(BigDecimal.valueOf(50))
        );
        List<Product> products = List.of(product1, product2);

        ProductResponse response1 = new ProductResponse(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);
        ProductResponse response2 = new ProductResponse(
                PRODUCT_ID_2,
                PRODUCT_NAME_2,
                PRODUCT_QUANTITY + 5,
                PRODUCT_PRICE.add(BigDecimal.valueOf(50))
        );

        when(productRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(products);
        when(productMapper.toResponse(product1)).thenReturn(response1);
        when(productMapper.toResponse(product2)).thenReturn(response2);

        List<ProductResponse> actual = productService.searchProductByName(searchName);

        assertNotNull(actual);
        assertEquals(List.of(response1, response2), actual);
        verify(productRepository).findByNameContainingIgnoreCase(searchName);
    }

    @Test
    void givenExistingId_whenDeleteProduct_thenInvokeDelete() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(true);

        productService.deleteProduct(PRODUCT_ID);

        verify(productRepository).deleteById(PRODUCT_ID);
    }

    @Test
    void givenNonExistingId_whenDeleteProduct_thenThrowException() {
        when(productRepository.existsById(PRODUCT_ID)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(PRODUCT_ID));
    }

    @Test
    void givenExistingIdAndNewQuantity_whenUpdateProductQuantity_thenReturnUpdatedResponse() {
        int newQuantity = 20;
        Product existing = new Product(PRODUCT_ID, PRODUCT_NAME, PRODUCT_QUANTITY, PRODUCT_PRICE);
        Product updated = new Product(PRODUCT_ID, PRODUCT_NAME, newQuantity, PRODUCT_PRICE);
        ProductResponse expected = new ProductResponse(PRODUCT_ID, PRODUCT_NAME, newQuantity, PRODUCT_PRICE);

        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(updated);
        when(productMapper.toResponse(updated)).thenReturn(expected);

        ProductResponse actual = productService.updateProductQuantity(PRODUCT_ID, newQuantity);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository).save(existing);
    }

    @Test
    void givenNonExistingId_whenUpdateProductQuantity_thenThrowException() {
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProductQuantity(PRODUCT_ID, 5));
        verify(productRepository).findById(PRODUCT_ID);
        verify(productRepository, never()).save(any());
    }

    @Test
    void givenProductsInSummary_whenGetProductSummary_thenReturnSummaryResponse() {
        // stub projection
        ProductRepository.ProductSummaryProjection summary = new ProductRepository.ProductSummaryProjection() {
            @Override
            public long getTotalProducts() {
                return 5L;
            }
            @Override
            public long getTotalQuantity() {
                return 100L;
            }
            @Override
            public BigDecimal getAveragePrice() {
                return BigDecimal.valueOf(50);
            }
        };

        // prepare out-of-stock list
        ProductSummaryResponse.OutOfStockProduct p1 =
                new ProductSummaryResponse.OutOfStockProduct(PRODUCT_ID, PRODUCT_NAME);
        ProductSummaryResponse.OutOfStockProduct p2 =
                new ProductSummaryResponse.OutOfStockProduct(PRODUCT_ID_2, PRODUCT_NAME_2);
        List<ProductSummaryResponse.OutOfStockProduct> outOfStock = List.of(p1, p2);

        // stubbing repository
        when(productRepository.getProductSummary()).thenReturn(summary);
        when(productRepository.findByQuantity(0)).thenReturn(outOfStock);

        // execute service
        ProductSummaryResponse actual = productService.getProductSummary();

        // assertions
        assertNotNull(actual);
        assertEquals(5L, actual.totalProducts());
        assertEquals(100L, actual.totalQuantity());
        assertEquals(BigDecimal.valueOf(50), actual.averagePrice());
        assertEquals(outOfStock, actual.outOfStockProductList());

        // verify interactions
        verify(productRepository).getProductSummary();
        verify(productRepository).findByQuantity(0);
    }
}
