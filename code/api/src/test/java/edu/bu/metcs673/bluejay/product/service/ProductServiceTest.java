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
import edu.bu.metcs673.bluejay.product.dto.ProductPageDto;
import edu.bu.metcs673.bluejay.product.dto.ProductQueryDto;
import edu.bu.metcs673.bluejay.common.exception.MissingProductPriceException;
import edu.bu.metcs673.bluejay.common.exception.ProductAlreadyExistedException;
import edu.bu.metcs673.bluejay.common.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.common.exception.UnknownProductCategoryException;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto(
            "Milk",
            "barcode-test-12345",
            1L,
            null,
            null,
            1
        );

        when(productRepository.getProductBy("barcode-test-12345")).thenReturn(Optional.empty());
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));
        when(productRepository.addProduct(any(Product.class)))
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
        assertEquals(BigDecimal.ZERO, product.getStockCost());
        assertEquals(BigDecimal.ZERO, product.getSalePrice());
    }

    @Test
    @DisplayName("Should throw exception when create product with duplicated barcode")
    public void shouldThrowExceptionWhenCreateProductWithDuplicateBarcode() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto(
            "Chocolate Milk",
            "barcode-existed-12345",
            1L,
            null,
            null,
            1
        );

        when(productRepository.getProductBy("barcode-existed-12345"))
                .thenReturn(Optional.of(createExistingProduct("barcode-existed-12345", "product-existed", SECOND_PRODUCT_ID, 1L, 2.5)));

        assertThrows(ProductAlreadyExistedException.class, () -> productService.createProduct(productDto));
    }

    @Test
    @DisplayName("Should add new product with newly created category if category doesn't exist")
    public void shouldAddNewCategoryToProductWhenProductCategoryNotExist() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto(
            "Soy Milk",
            "barcode-test-12345",
            null,
            "test-new-category",
            null,
            1
        );

        when(productRepository.getProductBy("barcode-test-12345")).thenReturn(Optional.empty());
        when(categoryRepository.addCategory(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(9999L);
                    return category;
                });
        when(productRepository.addProduct(any(Product.class)))
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
        assertEquals(BigDecimal.ZERO, product.getStockCost());
        assertEquals(BigDecimal.ZERO, product.getSalePrice());
        Category category = product.getCategory();
        assertNotNull(category);
        assertEquals("test-new-category", category.getName());
    }

    @Test
    @DisplayName("Should save product category description when provided")
    public void shouldSaveOptionalCategoryDescriptionWhenDescriptionIsEmpty() {
        CreateProductWithCategoryDto productDto = new CreateProductWithCategoryDto(
            "Oat Milk",
            "barcode-test-12345",
            null,
            "test-new-category",
            "test-description-category",
            1
        );

        when(productRepository.getProductBy("barcode-test-12345")).thenReturn(Optional.empty());
        when(categoryRepository.addCategory(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.setId(9999L);
                    return category;
                });
        when(productRepository.addProduct(any(Product.class)))
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
        assertEquals(BigDecimal.ZERO, product.getStockCost());
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
        assertEquals(1L, categories.get(0).id());
        assertEquals("test-category", categories.get(0).name());
        assertEquals("test-description", categories.get(0).description());
        assertEquals(2L, categories.get(1).id());
        assertEquals("beverages", categories.get(1).name());
        assertEquals("drinks", categories.get(1).description());
    }

    @Test
    @DisplayName("Should return products with category when requested page has records")
    public void shouldReturnProductsWithCategoryWhenRequestedPageHasRecords() {
        ProductQueryDto queryDto = createProductQueryDto(null, null, 2, 1);

        when(productRepository.getProducts(null, null, null, 2, 1))
                .thenReturn(new PageImpl<>(
                    List.of(createExistingProduct("barcode-existed-12345", "product-existed", SECOND_PRODUCT_ID, 1L, 2.5)),
                    PageRequest.of(1, 1),
                    2
                ));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        ProductPageDto products = productService.getProducts(queryDto);

        assertNotNull(products);
        assertFalse(products.items().isEmpty());
        assertEquals(1, products.items().size());
        assertEquals(2, products.totalCount());

        ProductDto firstProduct = products.items().getFirst();
        assertNotNull(firstProduct);
        assertNotNull(firstProduct.id());
        assertEquals("product-existed", firstProduct.name());
        assertTrue(firstProduct.categoryId() > 0);
        assertEquals("test-category", firstProduct.categoryName());
        assertEquals(12, firstProduct.currentStock());
    }

    @Test
    @DisplayName("Should return an empty page when requested page has no records")
    public void shouldReturnEmptyPageWhenRequestedPageHasNoRecords() {
        ProductQueryDto queryDto = createProductQueryDto(null, null, 1, 1);

        when(productRepository.getProducts(null, null, null, 1, 1))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 1), 0));

        ProductPageDto products = productService.getProducts(queryDto);

        assertNotNull(products);
        assertTrue(products.items().isEmpty());
        assertEquals(0, products.totalCount());
    }

    @Test
    @DisplayName("Should throw unknown product category exception when product category does not exist")
    public void shouldThrowUnknownProductCategoryExceptionWhenProductCategoryDoesNotExist() {
        ProductQueryDto queryDto = createProductQueryDto(null, null, 1, 1);

        when(productRepository.getProducts(null, null, null, 1, 1))
                .thenReturn(new PageImpl<>(
                    List.of(createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 3.75)),
                    PageRequest.of(0, 1),
                    1
                ));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.empty());

        assertThrows(UnknownProductCategoryException.class,
                () -> productService.getProducts(queryDto));
    }

    @Test
    @DisplayName("Should pass product name and category filters to repository")
    public void shouldFilterProductsByProductNameAndCategoryName() {
        ProductQueryDto queryDto = createProductQueryDto("Water", "beverage", 1, 10);

        when(productRepository.getProducts(null, "Water", "beverage", 1, 10))
                .thenReturn(new PageImpl<>(
                    List.of(createExistingProduct("barcode-water-12345", "Spring Water", SECOND_PRODUCT_ID, 1L, 2.5)),
                    PageRequest.of(0, 10),
                    1
                ));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        ProductPageDto products = productService.getProducts(queryDto);

        assertNotNull(products);
        assertEquals(1, products.items().size());
        assertEquals("Spring Water", products.items().getFirst().name());
        assertEquals(2.5, products.items().getFirst().price());
    }

    @Test
    @DisplayName("Should default page number and page size when query pagination is missing")
    public void shouldDefaultProductQueryPagination() {
        ProductQueryDto queryDto = createProductQueryDto("Milk", "dairy", null, null);

        when(productRepository.getProducts(null, "Milk", "dairy", 1, 10))
                .thenReturn(new PageImpl<>(
                    List.of(createExistingProduct("barcode-milk-12345", "Milk", SECOND_PRODUCT_ID, 1L, 2.5)),
                    PageRequest.of(0, 10),
                    1
                ));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        ProductPageDto products = productService.getProducts(queryDto);

        assertNotNull(products);
        assertEquals(1, products.items().size());
    }

    @Test
    @DisplayName("Should search products using a combined name or barcode term")
    public void shouldSearchProductsUsingCombinedSearchTerm() {
        String searchTerm = "barcode-water-12345";
        ProductQueryDto queryDto = createProductQueryDto(searchTerm, null, null, 1, 10);

        when(productRepository.getProducts(searchTerm, null, null, 1, 10))
            .thenReturn(new PageImpl<>(
                List.of(createExistingProduct(searchTerm, "Spring Water", SECOND_PRODUCT_ID, 1L, 2.5)),
                PageRequest.of(0, 10),
                1
            ));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        ProductPageDto products = productService.getProducts(queryDto);

        assertEquals(1, products.items().size());
        assertEquals(searchTerm, products.items().getFirst().barcode());
        assertEquals("Spring Water", products.items().getFirst().name());
    }

    @Test
    @DisplayName("Should return an empty page when combined search term has no match")
    public void shouldReturnNoProductsForUnmatchedSearchTerm() {
        String searchTerm = "missing-product-12345";
        ProductQueryDto queryDto = createProductQueryDto(searchTerm, null, null, 1, 10);

        when(productRepository.getProducts(searchTerm, null, null, 1, 10))
            .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        ProductPageDto products = productService.getProducts(queryDto);

        assertTrue(products.items().isEmpty());
        assertEquals(0, products.totalCount());
    }

    @Test
    @DisplayName("Should return product with details and sale salePrice")
    public void shouldReturnProductWithSalePrice() {
        when(productRepository.getProductBy("barcode-mock-12345"))
                .thenReturn(Optional.of(createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 3.75)));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        ProductDto product = productService.getProductBy("barcode-mock-12345");

        assertNotNull(product);
        assertNotNull(product.id());
        assertEquals("product-mock", product.name());
        assertEquals("test-category", product.categoryName());
        assertEquals(3.75, product.price());
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
    @DisplayName("Should throw missing product salePrice exception when find product by barcode and sale salePrice is zero")
    public void shouldThrowMissingProductPriceExceptionWhenFindProductByBarcodeAndSalePriceIsZero() {
        Product product = createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 0);
        when(productRepository.getProductBy("barcode-mock-12345")).thenReturn(Optional.of(product));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        assertThrows(MissingProductPriceException.class,
                () -> productService.getProductBy("barcode-mock-12345"));
    }

    @Test
    @DisplayName("Should update low stock threshold and return ProductDto")
    public void shouldUpdateLowStockSuccessfully() {
        Product existing = createExistingProduct("barcode-mock-12345", "product-mock", FIRST_PRODUCT_ID, 1L, 5.00);
        existing.setLowStock(5);

        when(productRepository.findById(FIRST_PRODUCT_ID)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(categoryRepository.getCategoryBy(1L)).thenReturn(Optional.of(createCategory(1L)));

        ProductDto result = productService.updateLowStock(FIRST_PRODUCT_ID, 20);

        assertNotNull(result);
        assertEquals(FIRST_PRODUCT_ID, result.id());
        assertEquals(20, result.lowStock());
        assertEquals("test-category", result.categoryName());
        verify(productRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating low stock for nonexistent product")
    public void shouldThrowProductNotFoundExceptionWhenUpdatingLowStockForMissingProduct() {
        UUID unknownId = UUID.randomUUID();
        when(productRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.updateLowStock(unknownId, 20));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return products with null category when categoryId is null")
    public void shouldReturnProductsWithNullCategoryWhenCategoryIdIsNull() {
        ProductQueryDto queryDto = createProductQueryDto(null, null, 1, 1);

        when(productRepository.getProducts(null, null, null, 1, 1))
            .thenReturn(new PageImpl<>(
                List.of(createExistingProduct(
                    "barcode-no-cat", "product-no-cat",
                    SECOND_PRODUCT_ID, null, 2.5)),
                PageRequest.of(0, 1),
                1
            ));

        ProductPageDto products = productService.getProducts(queryDto);

        assertNotNull(products);
        assertEquals(1, products.items().size());
        ProductDto dto = products.items().getFirst();
        assertNull(dto.categoryId());
        assertNull(dto.categoryName());
        assertNull(dto.categoryDescription());
    }

    @Test
    @DisplayName("Should return product with null category by barcode")
    public void shouldReturnProductWithNullCategoryByBarcode() {
        when(productRepository.getProductBy("barcode-no-cat"))
            .thenReturn(Optional.of(createExistingProduct(
                "barcode-no-cat", "product-no-cat",
                FIRST_PRODUCT_ID, null, 3.75)));

        ProductDto product = productService.getProductBy("barcode-no-cat");

        assertNotNull(product);
        assertEquals("product-no-cat", product.name());
        assertNull(product.categoryId());
        assertNull(product.categoryName());
        assertNull(product.categoryDescription());
        assertEquals(3.75, product.price());
    }

    @Test
    @DisplayName("Should update low stock when product has null category")
    public void shouldUpdateLowStockWhenProductHasNullCategory() {
        Product existing = createExistingProduct(
            "barcode-no-cat", "product-no-cat",
            FIRST_PRODUCT_ID, null, 5.00);
        existing.setLowStock(5);

        when(productRepository.findById(FIRST_PRODUCT_ID))
            .thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        ProductDto result = productService
            .updateLowStock(FIRST_PRODUCT_ID, 20);

        assertNotNull(result);
        assertEquals(FIRST_PRODUCT_ID, result.id());
        assertEquals(20, result.lowStock());
        assertNull(result.categoryName());
        verify(productRepository).save(existing);
    }

    private Product createExistingProduct(String barcode, String name, UUID id, Long categoryId, double salePrice) {
        Product product = new Product();
        product.setId(id);
        product.setBarcode(barcode);
        product.setName(name);
        product.setCategoryId(categoryId);
        product.setSalePrice(BigDecimal.valueOf(salePrice));
        product.setCurrentStock(12);
        return product;
    }

    private Category createCategory(Long id) {
        Category category = new Category();
        category.setId(id);
        category.setName("test-category");
        category.setDescription("test-description");
        return category;
    }

    private ProductQueryDto createProductQueryDto(
        String productName,
        String categoryName,
        Integer pageNumber,
        Integer pageSize
    ) {
        return new ProductQueryDto(null, productName, categoryName, pageNumber, pageSize);
    }

    private ProductQueryDto createProductQueryDto(
        String searchTerm,
        String productName,
        String categoryName,
        Integer pageNumber,
        Integer pageSize
    ) {
        return new ProductQueryDto(searchTerm, productName, categoryName, pageNumber, pageSize);
    }
}
