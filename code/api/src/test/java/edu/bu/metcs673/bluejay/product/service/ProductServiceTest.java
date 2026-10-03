// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Refactor product service test to verify paginated retrieval"
// AI Contribution: Test refactor for paginated retrieval (~35%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.service;

import edu.bu.metcs673.bluejay.product.domain.Category;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.exception.ProductAlreadyExistedException;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProductService productService;

    @BeforeEach
    public void setup() {
        productRepository = new MockProductRepository();
        categoryRepository = new MockCategoryRepository();
        productService = new ProductServiceImpl(productRepository,  categoryRepository);
    }

    @AfterEach
    public void teardown() {
        MockProductRepository repository = (MockProductRepository) productRepository;
        repository.reset();

        MockCategoryRepository cRepository = (MockCategoryRepository) categoryRepository;
        cRepository.reset();
    }

    @Test
    @DisplayName("Should create product when all fields are valid")
    public void shouldCreateProductWhenAllFieldsAreValid() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto();
        productDto.setBarcode("barcode-test-12345");
        productDto.setName("Milk");
        productDto.setCategoryId(1);

        Product product = productService.createProduct(productDto);

        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals("barcode-test-12345", product.getBarcode());
        assertEquals("Milk", product.getName());
        assertEquals(1, product.getCategoryId());
    }

    @Test
    @DisplayName("Should throw exception when create product with duplicated barcode")
    public void shouldThrowExceptionWhenCreateProductWithDuplicateBarcode() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto();
        productDto.setBarcode("barcode-existed-12345");
        productDto.setName("Chocolate Milk");
        productDto.setCategoryId(1);

        assertThrows(ProductAlreadyExistedException.class, () -> productService.createProduct(productDto));
    }

    @Test
    @DisplayName("Should add new product with newly created category if category doesn't exist")
    public void shouldAddNewCategoryToProductWhenProductCategoryNotExist() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto();
        productDto.setBarcode("barcode-test-12345");
        productDto.setName("Soy Milk");
        productDto.setCategoryName("test-new-category");

        Product product = productService.createProduct(productDto);
        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals("barcode-test-12345", product.getBarcode());
        assertEquals("Soy Milk", product.getName());
        Category category = product.getCategory();
        assertNotNull(category);
        assertEquals("test-new-category", category.getName());
    }

    @Test
    @DisplayName("Should save product category description when provided")
    public void shouldSaveOptionalCategoryDescriptionWhenDescriptionIsEmpty() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto();
        productDto.setBarcode("barcode-test-12345");
        productDto.setName("Oat Milk");
        productDto.setCategoryName("test-new-category");
        productDto.setCategoryDescription("test-description-category");

        Product product = productService.createProduct(productDto);
        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals("barcode-test-12345", product.getBarcode());
        assertEquals("Oat Milk", product.getName());
        Category category = product.getCategory();
        assertNotNull(category);
        assertEquals("test-new-category", category.getName());
        assertEquals("test-description-category", category.getDescription());
    }

    @Test
    @DisplayName("Should return products with category when requested page has records")
    public void shouldReturnProductsWithCategoryWhenRequestedPageHasRecords() {
        List<ProductDto> products = productService.getProducts(2, 1);

        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertEquals(1, products.size());

        ProductDto firstProduct = products.getFirst();
        assertNotNull(firstProduct);
        assertNotNull(firstProduct.getId());
        assertEquals("product-existed", firstProduct.getName());
        assertTrue(firstProduct.getCategoryId() > 0);
        assertEquals("test-category", firstProduct.getCategoryName());
    }

    private static class MockProductRepository implements ProductRepository {
        private final ArrayList<Product> products = new ArrayList<>();

        public MockProductRepository() {
            Product p1 = new Product();
            p1.setId(UUID.randomUUID());
            p1.setBarcode("barcode-mock-12345");
            p1.setName("product-mock");
            p1.setCategoryId(1);

            Product p2 = new Product();
            p2.setId(UUID.randomUUID());
            p2.setBarcode("barcode-existed-12345");
            p2.setName("product-existed");
            p2.setCategoryId(1);

            products.add(p1);
            products.add(p2);
        }

        void reset() {
            products.clear();
        }

        @Override
        public Product addProduct(Product product) {
            // Should create new entity in real database implementation
            // rather than update Product domain class
            product.setId(UUID.randomUUID());
            products.add(product);
            return product;
        }

        @Override
        public Optional<Product> getProductBy(String barcode) {
             return products.stream()
                    .filter(p -> p.getBarcode().equals(barcode))
                    .findFirst();
        }

        @Override
        public List<Product> getProducts(int pageNumber, int pageSize) {
            int fromIndex = (pageNumber - 1) * pageSize;
            if (fromIndex >= products.size()) {
                return List.of();
            }

            int toIndex = Math.min(fromIndex + pageSize, products.size());
            return products.subList(fromIndex, toIndex);
        }
    }

    private static class MockCategoryRepository implements CategoryRepository {
        private final ArrayList<Category> categories = new ArrayList<>();

        public MockCategoryRepository() {
            Category c1 = new Category();
            c1.setId(1);
            c1.setName("test-category");
            c1.setDescription("test-description");

            categories.add(c1);
        }

        @Override
        public Category addCategory(Category category) {
            category.setId(9999);
            categories.add(category);
            return category;
        }

        @Override
        public Optional<Category> getCategoryBy(int categoryId) {
            return categories.stream()
                    .filter(c -> c.getId() == categoryId)
                    .findFirst();
        }

        public void reset() {
            categories.clear();
        }
    }
}
