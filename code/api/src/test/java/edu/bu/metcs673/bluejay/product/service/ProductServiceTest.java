// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProductServiceTest {
    private final MockProductRepository productRepository;
    private final ProductService productService;

    @BeforeEach
    public void setup() {
        productRepository = new MockProductRepository();
        productService = new ProductServiceImpl(productRepository);
    }

    @AfterEach
    public void teardown() {
        productRepository.reset();
    }

    @Test
    @DisplayName("Should create product when all fields are valid")
    public void shouldCreateProductWhenAllFieldsAreValid() {
        Product product = new Product();
        product.setBarcode("barcode-test-12345");
        product.setName("Milk");
        product.setCategoryId(1);

        Product result = productService.createProduct(product);

        assertNotNull(result);
        assertNotNull(result.getId());
    }
}
