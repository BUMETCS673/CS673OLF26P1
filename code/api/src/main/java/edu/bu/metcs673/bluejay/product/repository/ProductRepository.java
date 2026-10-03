// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Refactor product repository contract to support pagination"
// AI Contribution: Method signature update for pagination (~15%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product addProduct(Product product);
    Optional<Product> getProductBy(String barcode);
    List<Product> getProducts(int pageNumber, int pageSize);
}
