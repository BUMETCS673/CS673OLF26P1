// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Refactor product service to support pagination, barcode lookup, and category selection"
// AI Contribution: Service contract updates for pagination, barcode lookup, and category retrieval (~35%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.service;

import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CategoryDto;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;

import java.util.List;

public interface ProductService {
    Product createProduct(CreateProductWithCategoryDto productDto);
    List<CategoryDto> getProductCategories();
    List<ProductDto> getProducts(int pageNumber, int pageSize);
    ProductDto getProductBy(String barcode);
}
