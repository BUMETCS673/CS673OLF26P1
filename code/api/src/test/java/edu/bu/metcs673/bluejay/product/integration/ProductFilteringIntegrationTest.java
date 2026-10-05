// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Add integration tests for filtered product listing by name and category"
// AI Contribution: Repository-level product filter integration assertions over Flyway-seeded data (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.integration;

import edu.bu.metcs673.bluejay.common.integration.AbstractIntegrationTest;
import edu.bu.metcs673.bluejay.product.domain.Category;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product Filtering Integration Tests")
class ProductFilteringIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Nested
    @DisplayName("Filtered Product Queries")
    class FilteredProductQueries {

        @BeforeEach
        void setUpProducts() {
            Category fruit = categoryRepository.save(createCategory("fruit"));
            Category beverage = categoryRepository.save(createCategory("beverage"));

            productRepository.save(createProduct("Lemons", "045678901231", fruit.getId()));
            productRepository.save(createProduct("Raspberries", "051000012347", fruit.getId()));
            productRepository.save(createProduct("Spring Water", "012345123455", beverage.getId()));
        }

        @Test
        @DisplayName("Should filter products by name using contains matching")
        void shouldFilterProductsByName() {
            List<Product> products = productRepository.getProducts("Water", null, 1, 10);

            assertThat(products)
                .extracting(Product::getName)
                .containsExactly("Spring Water");
        }

        @Test
        @DisplayName("Should filter products by exact category name")
        void shouldFilterProductsByExactCategoryName() {
            List<Product> products = productRepository.getProducts(null, "fruit", 1, 10);

            assertThat(products)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Lemons", "Raspberries");
        }
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(name + " description");
        return category;
    }

    private Product createProduct(String name, String barcode, Long categoryId) {
        Product product = new Product();
        product.setName(name);
        product.setBarcode(barcode);
        product.setCategoryId(categoryId);
        product.setCostPrice(BigDecimal.ZERO);
        product.setSalePrice(BigDecimal.ONE);
        product.setCurrentStock(0);
        return product;
    }
}
