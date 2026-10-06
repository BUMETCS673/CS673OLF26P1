// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Add integration tests for filtered product listing by name and category"
// AI Contribution: Repository-level product filter integration assertions over Flyway-seeded data (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.integration;

import edu.bu.metcs673.bluejay.common.integration.AbstractIntegrationTest;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product Filtering Integration Tests")
class ProductFilteringIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Nested
    @DisplayName("Filtered Product Queries")
    class FilteredProductQueries {

        @Test
        @DisplayName("Should filter products by name using contains matching")
        void shouldFilterProductsByName() {
            // Query directly against seed data created by Flyway migrations
            List<Product> products = productRepository.getProducts("Water", null, 1, 10);

            assertThat(products)
                .extracting(Product::getName)
                .containsExactly("Spring Water");
        }

        @Test
        @DisplayName("Should filter products by exact category name")
        void shouldFilterProductsByExactCategoryName() {
            // Query directly against seed data created by Flyway migrations
            List<Product> products = productRepository.getProducts(null, "fruit", 1, 10);

            assertThat(products)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Lemons", "Raspberries");
        }
    }
}
