// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.Product;

import java.util.Optional;

public interface ProductRepository {
    Product addProduct(Product product);
    Optional<Product> getProductBy(String barcode);
}
