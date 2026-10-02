// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.service;

import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CreateProductDto;

public interface ProductService {
    Product createProduct(CreateProductDto productDto);
}
