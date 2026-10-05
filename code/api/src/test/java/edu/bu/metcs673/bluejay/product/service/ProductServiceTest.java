// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Refactor product service tests to use Mockito with Product-based pricing behavior"
// AI Contribution: Mockito-based repository stubbing and updated coverage for Product pricing/category flows (~80%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.service;

import edu.bu.metcs673.bluejay.product.domain.Category;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CategoryDto;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.exception.MissingProductPriceException;
import edu.bu.metcs673.bluejay.product.exception.ProductAlreadyExistedException;
import edu.bu.metcs673.bluejay.product.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.product.exception.UnknownProductCategoryException;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    private static final UUID FIRST_PRODUCT_ID = UUID.fromString("12345678-1234-1234-1234-123456789abc");
    private static final UUID SECOND_PRODUCT_ID = UUID.fromString("12345678-1234-1234-1234-123456789bcd");

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductService productService;

    @BeforeEach
    public void setup() {
        productService = new ProductServiceImpl(productRepository, categoryRepository);
    }

    @Test
    @DisplayName("Should create product when all fields are valid")
    public void shouldCreateProductWhenAllFieldsAreValid() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto();
        productDto.setBarcode("barcode-test-12345");
        productDto.setName("Milk");
        productDto.setCategoryId(1L);

        when(productRepository.getProductBy("barcode-test-12345")).thenReturn(Optional.empty());
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));
        when(productRepository.addProduct(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(UUID.randomUUID());
                    return product;
                });

        Product product = productService.createProduct(productDto);

        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals("barcode-test-12345", product.getBarcode());
        assertEquals("Milk", product.getName());
        assertEquals(1L, product.getCategoryId());
        assertEquals(BigDecimal.ZERO, product.getCostPrice());
        assertEquals(BigDecimal.ZERO, product.getSalePrice());
    }

    @Test
    @DisplayName("Should throw exception when create product with duplicated barcode")
    public void shouldThrowExceptionWhenCreateProductWithDuplicateBarcode() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto();
        productDto.setBarcode("barcode-existed-12345");
        productDto.setName("Chocolate Milk");
        productDto.setCategoryId(1L);

        when(productRepository.getProductBy("barcode-existed-12345"))
                .thenReturn(Optional.of(createExistingProduct("barcode-existed-12345", "product-existed", SECOND_PRODUCT_ID, 1L, 2.5)));

        assertThrows(ProductAlreadyExistedException.class, () -> productService.createProduct(productDto));
    }

    @Test
    @DisplayName("Should add new product with newly created category if category doesn't exist")
    public void shouldAddNewCategoryToProductWhenProductCategoryNotExist() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto();
        productDto.setBarcode("barcode-test-12345");
        productDto.setName("Soy Milk");
        productDto.setCategoryName("test-new-category");

        when(productRepository.getProductBy("barcode-test-12345")).thenReturn(Optional.empty());
        when(categoryRepository.addCategory(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(9999L);
                    return category;
                });
        when(productRepository.addProduct(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(UUID.randomUUID());
                    return product;
                });

        Product product = productService.createProduct(productDto);
        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals("barcode-test-12345", product.getBarcode());
        assertEquals("Soy Milk", product.getName());
        assertEquals(BigDecimal.ZERO, product.getCostPrice());
        assertEquals(BigDecimal.ZERO, product.getSalePrice());
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

        when(productRepository.getProductBy("barcode-test-12345")).thenReturn(Optional.empty());
        when(categoryRepository.addCategory(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(9999L);
                    return category;
                });
        when(productRepository.addProduct(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenAnswer(invocation -> {
                    Product product = invocation.getArgument(0);
                    product.setId(UUID.randomUUID());
                    return product;
                });

        Product product = productService.createProduct(productDto);
        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals("barcode-test-12345", product.getBarcode());
        assertEquals("Oat Milk", product.getName());
        assertEquals(BigDecimal.ZERO, product.getCostPrice());
        assertEquals(BigDecimal.ZERO, product.getSalePrice());
        Category category = product.getCategory();
        assertNotNull(category);
        assertEquals("test-new-category", category.getName());
        assertEquals("test-description-category", category.getDescription());
    }

    @Test
    @DisplayName("Should return product categories for frontend selection")
    public void shouldReturnProductCategories() {
        Category firstCategory = createCategory(1L);
        Category secondCategory = createCategory(2L);
        secondCategory.setName("beverages");
        secondCategory.setDescription("drinks");

        when(categoryRepository.getCategories()).thenReturn(List.of(firstCategory, secondCategory));

        List<CategoryDto> categories = productService.getProductCategories();

        assertNotNull(categories);
        assertEquals(2, categories.size());
        assertEquals(1L, categories.get(0).getId());
        assertEquals("test-category", categories.get(0).getName());
        assertEquals("test-description", categories.get(0).getDescription());
        assertEquals(2L, categories.get(1).getId());
        assertEquals("beverages", categories.get(1).getName());
        assertEquals("drinks", categories.get(1).getDescription());
    }

    @Test
    @DisplayName("Should return products with category when requested page has records")
    public void shouldReturnProductsWithCategoryWhenRequestedPageHasRecords() {
        when(productRepository.getProducts(2, 1))
                .thenReturn(List.of(createExistingProduct("barcode-existed-12345", "product-existed", SECOND_PRODUCT_ID, 1L, 2.5)));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

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

    @Test
    @DisplayName("Should throw product not found exception when requested page has no records")
    public void shouldThrowProductNotFoundExceptionWhenRequestedPageHasNoRecords() {
        when(productRepository.getProducts(1, 1)).thenReturn(List.of());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProducts(1, 1));
    }

    @Test
    @DisplayName("Should throw unknown product category exception when product category does not exist")
    public void shouldThrowUnknownProductCategoryExceptionWhenProductCategoryDoesNotExist() {
        when(productRepository.getProducts(1, 1))
                .thenReturn(List.of(createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 3.75)));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.empty());

        assertThrows(UnknownProductCategoryException.class,
                () -> productService.getProducts(1, 1));
    }

    @Test
    @DisplayName("Should return product with details and sale price")
    public void shouldReturnProductWithSalePrice() {
        when(productRepository.getProductBy("barcode-mock-12345"))
                .thenReturn(Optional.of(createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 3.75)));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        ProductDto product = productService.getProductBy("barcode-mock-12345");

        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals("product-mock", product.getName());
        assertEquals("test-category", product.getCategoryName());
        assertEquals(3.75, product.getPrice());
    }

    @Test
    @DisplayName("Should throw product not found exception when product barcode does not exist")
    public void shouldThrowProductNotFoundExceptionWhenGetProductByBarcodeDoesNotExist() {
        String unknownBarcode = "barcode-unknown-12345";

        when(productRepository.getProductBy(unknownBarcode)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.getProductBy(unknownBarcode));
    }

    @Test
    @DisplayName("Should throw unknown product category exception when find product by barcode and category does not exist")
    public void shouldThrowUnknownProductCategoryExceptionWhenFindProductByBarcodeAndCategoryDoesNotExist() {
        when(productRepository.getProductBy("barcode-mock-12345"))
                .thenReturn(Optional.of(createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 3.75)));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.empty());

        assertThrows(UnknownProductCategoryException.class,
                () -> productService.getProductBy("barcode-mock-12345"));
    }

    @Test
    @DisplayName("Should throw missing product price exception when find product by barcode and sale price is zero")
    public void shouldThrowMissingProductPriceExceptionWhenFindProductByBarcodeAndSalePriceIsZero() {
        Product product = createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 0);
        when(productRepository.getProductBy("barcode-mock-12345")).thenReturn(Optional.of(product));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        assertThrows(MissingProductPriceException.class,
                () -> productService.getProductBy("barcode-mock-12345"));
    }

    private Product createExistingProduct(String barcode, String name, UUID id, Long categoryId, double salePrice) {
        Product product = new Product();
        product.setId(id);
        product.setBarcode(barcode);
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setSalePrice(BigDecimal.valueOf(salePrice));
        return product;
    }

    private Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("test-category");
        category.setDescription("test-description");
        return category;
    }
}
