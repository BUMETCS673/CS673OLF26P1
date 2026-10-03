// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Refactor product retrieval contract to support pagination"
// AI Contribution: Method signature update for pagination (~15%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.service;

import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product createProduct(CreateProductWithCategoryDto productDto);
    List<ProductDto> getProducts(int pageNumber, int pageSize);
    ProductDto getProductBy(UUID id);
}
