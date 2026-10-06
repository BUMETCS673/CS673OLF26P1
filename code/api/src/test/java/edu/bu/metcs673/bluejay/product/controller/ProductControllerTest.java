// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Create MockMvc tests for ProductController secured admin/manager endpoints"
// AI Contribution: WebMvcTest setup, role-based access assertions, and ApiResponse JSON checks (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.SecurityConfig;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.common.exception.GlobalExceptionHandler;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CategoryDto;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.dto.ProductQueryDto;
import edu.bu.metcs673.bluejay.common.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class ProductControllerTest {

private static final String PRODUCT_BARCODE = "barcode-mock-12345";
private static final UUID PRODUCT_ID =
    UUID.fromString("12345678-1234-1234-1234-123456789abc");

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Nested
    @DisplayName("POST /api/v1/product Tests")
    class CreateProductEndpoint {

        @Test
        @DisplayName("Should allow admin to create product and return 201")
        void createProduct_Admin_Returns201() throws Exception {
            CreateProductWithCategoryDto request = new CreateProductWithCategoryDto(
                "Milk",
                "barcode-test-12345",
                1L,
                null,
                null
            );

            Product product = new Product();
            product.setId(PRODUCT_ID);
            product.setBarcode("barcode-test-12345");
            product.setName("Milk");
            product.setCategoryId(1L);

            when(productService.createProduct(any(CreateProductWithCategoryDto.class)))
                .thenReturn(product);

            mockMvc.perform(post("/api/v1/product")
                    .with(user("admin").roles("ADMIN"))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.data.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.data.name").value("Milk"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/product Tests")
    class GetProductsEndpoint {

        @Test
        @DisplayName("Should allow manager to get paginated products and return 200")
        void getProducts_Manager_Returns200() throws Exception {
            ProductDto product = new ProductDto(
                PRODUCT_ID,
                "product-mock",
                "barcode-mock-12345",
                1L,
                "test-category",
                "test-description",
                3.75
            );

            when(productService.getProducts(argThat(query ->
                query != null
                    && "product".equals(query.productName())
                    && "test-category".equals(query.categoryName())
                    && Integer.valueOf(1).equals(query.pageNumber())
                    && Integer.valueOf(1).equals(query.pageSize())
            ))).thenReturn(List.of(product));

            mockMvc.perform(get("/api/v1/product")
                    .with(user("manager").roles("MANAGER"))
                    .param("productName", "product")
                    .param("categoryName", "test-category")
                    .param("pageNumber", "1")
                    .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.data[0].categoryName").value("test-category"));
        }

        @Test
        @DisplayName("Should deny non-admin non-manager role from listing products")
        void getProducts_User_Returns403() throws Exception {
            mockMvc.perform(get("/api/v1/product")
                    .with(user("user").roles("USER"))
                    .param("pageNumber", "1")
                    .param("pageSize", "1"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/product/categories Tests")
    class GetProductCategoriesEndpoint {

        @Test
        @DisplayName("Should allow manager to get product categories and return 200")
        void getProductCategories_Manager_Returns200() throws Exception {
            CategoryDto category = new CategoryDto(1L, "test-category", "test-description");

            when(productService.getProductCategories()).thenReturn(List.of(category));

            mockMvc.perform(get("/api/v1/product/categories")
                    .with(user("manager").roles("MANAGER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product categories retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("test-category"));
        }

        @Test
        @DisplayName("Should deny non-admin non-manager role from getting product categories")
        void getProductCategories_User_Returns403() throws Exception {
            mockMvc.perform(get("/api/v1/product/categories")
                    .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/product/{barcode} Tests")
    class GetProductByBarcodeEndpoint {

        @Test
        @DisplayName("Should allow admin to get product by barcode and return 200")
        void getProductByBarcode_Admin_Returns200() throws Exception {
            ProductDto product = new ProductDto(
                PRODUCT_ID,
                "product-mock",
                PRODUCT_BARCODE,
                1L,
                "test-category",
                "test-description",
                3.75
            );

            when(productService.getProductBy(PRODUCT_BARCODE)).thenReturn(product);

            mockMvc.perform(get("/api/v1/product/{barcode}", PRODUCT_BARCODE)
                    .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(PRODUCT_ID.toString()))
                .andExpect(jsonPath("$.data.price").value(3.75));
        }

        @Test
        @DisplayName("Should require authentication to get product by barcode")
        void getProductByBarcode_Unauthenticated_Returns401() throws Exception {
            mockMvc.perform(get("/api/v1/product/{barcode}", PRODUCT_BARCODE))
                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void getProductByBarcode_ProductMissing_Returns404() throws Exception {
            when(productService.getProductBy(PRODUCT_BARCODE))
                .thenThrow(new ProductNotFoundException(PRODUCT_BARCODE));

            mockMvc.perform(get("/api/v1/product/{barcode}", PRODUCT_BARCODE)
                    .with(user("manager").roles("MANAGER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("PRODUCT_NOT_FOUND"));
        }
    }
}
