package com.safereach.inventory_system.service;

import com.safereach.inventory_system.dto.ProductRequest;
import com.safereach.inventory_system.dto.ProductResponse;
import com.safereach.inventory_system.dto.ProductSummaryResponse;
import com.safereach.inventory_system.entity.Product;
import com.safereach.inventory_system.exception.ProductAlreadyExistsException;
import com.safereach.inventory_system.exception.ProductNotFoundException;
import com.safereach.inventory_system.mapper.ProductMapper;
import com.safereach.inventory_system.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        String name = productRequest.name();
        if (productRepository.existsByName(name)) {
            throw new ProductAlreadyExistsException(name);
        }
        Product createdProduct = productRepository.save(productMapper.toEntity(productRequest));
        return productMapper.toResponse(createdProduct);
    }

    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    public List<ProductResponse> searchProductByName(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found with name: " + name);
        }
        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public ProductResponse updateProductQuantity(UUID id, Integer newQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setQuantity(newQuantity);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    public ProductSummaryResponse getProductSummary() {
        ProductRepository.ProductSummaryProjection summary = productRepository.getProductSummary();
        List<ProductSummaryResponse.OutOfStockProduct> outOfStockProducts = productRepository.findByQuantity(0);
        return new ProductSummaryResponse(
                summary.getTotalProducts(),
                summary.getTotalQuantity(),
                summary.getAveragePrice(),
                outOfStockProducts
        );
    }
}
