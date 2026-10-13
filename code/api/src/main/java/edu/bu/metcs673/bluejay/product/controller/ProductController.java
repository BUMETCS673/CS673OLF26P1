// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Create secured ProductController endpoints for create, filtered list, barcode lookup, and category selection"
// AI Contribution: REST endpoint scaffolding, ApiResponse wrapping, and role-based method security for filtered product/category reads (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.controller;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CategoryDto;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.dto.ProductPageDto;
import edu.bu.metcs673.bluejay.product.dto.ProductQueryDto;
import edu.bu.metcs673.bluejay.product.dto.UpdateLowStockRequest;
import edu.bu.metcs673.bluejay.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Product>> createProduct(
        @RequestBody CreateProductWithCategoryDto productDto) {
        Product product = productService.createProduct(productDto);
        ApiResponse<Product> response = ApiResponse.success(
            product,
            "Product created successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ApiResponse<ProductPageDto>> getProducts(
        ProductQueryDto productQueryDto) {
        ProductPageDto products = productService.getProducts(productQueryDto);
        ApiResponse<ProductPageDto> response = ApiResponse.success(
            products,
            "Products retrieved successfully"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getProductCategories() {
        List<CategoryDto> categories = productService.getProductCategories();
        ApiResponse<List<CategoryDto>> response = ApiResponse.success(
            categories,
            "Product categories retrieved successfully"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{barcode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBy(
        @PathVariable String barcode) {
        ProductDto product = productService.getProductBy(barcode);
        ApiResponse<ProductDto> response = ApiResponse.success(
            product,
            "Product retrieved successfully"
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/low-stock")
    public ResponseEntity<ApiResponse<ProductDto>> updateLowStock(
        @PathVariable("id") UUID id,
        @Valid @RequestBody UpdateLowStockRequest request) {
        ProductDto product = productService.updateLowStock(id, request.lowStock());
        ApiResponse<ProductDto> response = ApiResponse.success(
            product,
            "Low stock threshold updated successfully"
        );
        return ResponseEntity.ok(response);
    }
}
