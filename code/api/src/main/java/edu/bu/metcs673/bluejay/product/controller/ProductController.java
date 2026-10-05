// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Create secured ProductController endpoints for create, list, barcode lookup, and category selection"
// AI Contribution: REST endpoint scaffolding, ApiResponse wrapping, and role-based method security for product/category reads (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.controller;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CategoryDto;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
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
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProducts(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize) {
        List<ProductDto> products = productService.getProducts(pageNumber, pageSize);
        ApiResponse<List<ProductDto>> response = ApiResponse.success(
            products,
            "Products retrieved successfully"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getProductCategories() {
        List<CategoryDto> categories = productService.getProductCategories();
        ApiResponse<List<CategoryDto>> response = ApiResponse.success(
            categories,
            "Product categories retrieved successfully"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{barcode}")
    public ResponseEntity<ApiResponse<ProductDto>> getProductBy(
        @PathVariable String barcode) {
        ProductDto product = productService.getProductBy(barcode);
        ApiResponse<ProductDto> response = ApiResponse.success(
            product,
            "Product retrieved successfully"
        );
        return ResponseEntity.ok(response);
    }
}
