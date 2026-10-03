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
import edu.bu.metcs673.bluejay.product.mock.MockCategoryRepository;
import edu.bu.metcs673.bluejay.product.mock.MockProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
}
