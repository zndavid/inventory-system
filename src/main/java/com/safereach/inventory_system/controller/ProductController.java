package com.safereach.inventory_system.controller;

import com.safereach.inventory_system.dto.ProductRequest;
import com.safereach.inventory_system.dto.ProductResponse;
import com.safereach.inventory_system.dto.ProductSummaryResponse;
import com.safereach.inventory_system.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product in the inventory system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content)
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        ProductResponse createdProduct = productService.createProduct(productRequest);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(createdProduct.id())
//                .toUri();
        // Ideally, you would set the URI of the created resource here, but the 'getOne' endpoint was not specified in the task.
//        return ResponseEntity.created(location)
//                .body(ProductMapper.INSTANCE.toResponse(createdProduct));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a paginated list of all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ProductResponse> productPage = productService.getAllProducts(pageable);

        return ResponseEntity.ok(productPage);
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name", description = "Searches for products by name (case-insensitive partial match)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products found",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameter",
                    content = @Content)
    })
    public ResponseEntity<List<ProductResponse>> searchProductByName(@RequestParam @NotBlank String name) {
        List<ProductResponse> products = productService.searchProductByName(name);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product from the inventory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/quantity")
    @Operation(summary = "Update product quantity", description = "Updates the quantity of a specific product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product quantity updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid quantity value",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    public ResponseEntity<ProductResponse> updateProductQuantity(
            @PathVariable UUID id,
            @RequestParam @Min(value = 0, message = "Quantity must be at least 0") Integer quantity) {
        ProductResponse updatedProduct = productService.updateProductQuantity(id, quantity);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get inventory summary", description = "Retrieves inventory statistics including " +
            "total products, quantities, average price, and out-of-stock items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductSummaryResponse.class)))
    })
    public ResponseEntity<ProductSummaryResponse> getInventorySummary() {
        ProductSummaryResponse productSummary = productService.getProductSummary();
        return ResponseEntity.ok(productSummary);
    }
}
