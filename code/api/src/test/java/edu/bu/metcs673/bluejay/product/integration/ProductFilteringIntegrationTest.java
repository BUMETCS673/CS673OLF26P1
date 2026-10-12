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
            List<Product> products = productRepository.getProducts("water", null, null, 1, 10);

            assertThat(products)
                .extracting(Product::getName)
                .containsExactly("Spring Water");
        }

        @Test
        @DisplayName("Should match a barcode using the combined search term")
        void shouldFindProductsByBarcodeSearchTerm() {
            Product seededProduct = productRepository.getProducts("Spring Water", null, 1, 10).getFirst();

            List<Product> products = productRepository.getProducts(
                seededProduct.getBarcode(), null, null, 1, 10
            );

            assertThat(products)
                .extracting(Product::getBarcode)
                .containsExactly(seededProduct.getBarcode());
        }

        @Test
        @DisplayName("Should return no products when the combined search term does not match")
        void shouldReturnEmptyWhenSearchTermDoesNotMatch() {
            List<Product> products = productRepository.getProducts(
                "no-product-matches-this-term", null, null, 1, 10
            );

            assertThat(products).isEmpty();
        }

        @Test
        @DisplayName("Should seed every demo product with a positive price and stock")
        void shouldSeedProductsWithPricesAndStock() {
            List<Product> products = productRepository.findAll();

            assertThat(products)
                .isNotEmpty()
                .allSatisfy(product -> {
                    assertThat(product.getSalePrice()).isPositive();
                    assertThat(product.getCurrentStock()).isPositive();
                });
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
