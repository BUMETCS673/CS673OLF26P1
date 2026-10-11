// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: WebMvcTest setup with method security, MockMvc assertions
// Human Contributions: Story #30 scenarios (Admin-only access, per-product rows, invalid range)
// Notes: Controller slice test for GET /api/v1/reports/sales/products.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.controller;

import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.SecurityConfig;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesItem;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesReport;
import edu.bu.metcs673.bluejay.report.service.ProductSalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "MockMvc tests for the Admin-only per-product sales endpoint"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Same springSecurity() MockMvc setup as SalesReportControllerTest (Story #29)
// Verification:
//   - Ran ./mvnw test locally: all passed
// Confidence: High
@WebMvcTest(ProductSalesController.class)
@Import(SecurityConfig.class)
class ProductSalesControllerTest {

    private static final String URL = "/api/v1/reports/sales/products";
    private static final LocalDate OCT_1 = LocalDate.of(2026, 10, 1);
    private static final LocalDate OCT_31 = LocalDate.of(2026, 10, 31);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private ProductSalesService productSalesService;

    // Needed so JwtAuthenticationFilter can initialize
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin gets quantity sold, cost and profit per product")
    void getProductSales_AsAdmin_Returns200WithProducts() throws Exception {
        when(productSalesService.getProductSales(OCT_1, OCT_31)).thenReturn(
            new ProductSalesReport(OCT_1, OCT_31, List.of(
                new ProductSalesItem("p1", "111", "Cooking Oil 2L", 5,
                    new BigDecimal("32.50"), new BigDecimal("20.50"),
                    new BigDecimal("12.00")),
                new ProductSalesItem("p2", "222", "Lemons", 0,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
            ))
        );

        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.startDate").value("2026-10-01"))
            .andExpect(jsonPath("$.data.products.length()").value(2))
            .andExpect(jsonPath("$.data.products[0].name").value("Cooking Oil 2L"))
            .andExpect(jsonPath("$.data.products[0].quantitySold").value(5))
            .andExpect(jsonPath("$.data.products[0].totalCost").value(20.50))
            .andExpect(jsonPath("$.data.products[0].profit").value(12.00))
            .andExpect(jsonPath("$.data.products[1].quantitySold").value(0));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Manager is rejected with 403")
    void getProductSales_AsManager_Returns403() throws Exception {
        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(productSalesService, never()).getProductSales(any(), any());
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    @DisplayName("Cashier is rejected with 403")
    void getProductSales_AsCashier_Returns403() throws Exception {
        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isForbidden());

        verify(productSalesService, never()).getProductSales(any(), any());
    }

    @Test
    @DisplayName("Unauthenticated request is rejected with 401")
    void getProductSales_Anonymous_Returns401() throws Exception {
        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Start date after end date returns 400 INVALID_DATE_RANGE")
    void getProductSales_InvalidRange_Returns400() throws Exception {
        when(productSalesService.getProductSales(OCT_31, OCT_1))
            .thenThrow(new InvalidDateRangeException(OCT_31, OCT_1));

        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-31")
                .param("endDate", "2026-10-01"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode").value("INVALID_DATE_RANGE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Missing startDate returns 400")
    void getProductSales_MissingParam_Returns400() throws Exception {
        mockMvc.perform(get(URL).param("endDate", "2026-10-31"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode")
                .value("INVALID_REQUEST_PARAMETER"));
    }
}