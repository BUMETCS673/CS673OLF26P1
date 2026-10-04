// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: WebMvcTest setup with method security, MockMvc assertions
// Human Contributions: Scenarios from Story #57 acceptance tests
// Notes: Controller slice test for GET /api/v1/reports/inventory, including the role guard.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.controller;

import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.SecurityConfig;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.report.dto.InventoryReportItem;
import edu.bu.metcs673.bluejay.report.service.InventoryReportService;
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
import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "MockMvc tests for the inventory report endpoint and its Admin/Manager role guard"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Imported the real SecurityConfig so @PreAuthorize and the 401 entry point are exercised
//   - Built MockMvc with springSecurity(): the auto-configured MockMvc ignored
//     @WithMockUser and every request returned 401
// Verification:
//   - Ran ./mvnw test locally: all tests passing
// Confidence: High
@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
class ReportControllerTest {

    private static final String URL = "/api/v1/reports/inventory";

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private InventoryReportService inventoryReportService;

    // Needed so JwtAuthenticationFilter can initialize
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    // Build MockMvc with springSecurity() so the @WithMockUser user reaches
    // the stateless security filter chain (otherwise every request is 401)
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Stock Manager gets name, latest cost and on-hand quantity per product")
    void getInventoryReport_AsManager_Returns200WithProducts()
        throws Exception {
        when(inventoryReportService.getInventoryReport()).thenReturn(List.of(
            new InventoryReportItem("p-1", "111", "Cooking Oil 2L",
                new BigDecimal("4.25"), 34),
            new InventoryReportItem("p-2", "222", "Premium Rice 5kg",
                new BigDecimal("9.99"), 72)
        ));

        mockMvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(2))
            .andExpect(jsonPath("$.data[0].name").value("Cooking Oil 2L"))
            .andExpect(jsonPath("$.data[0].latestCost").value(4.25))
            .andExpect(jsonPath("$.data[0].onHandQuantity").value(34))
            .andExpect(jsonPath("$.data[1].name").value("Premium Rice 5kg"))
            .andExpect(jsonPath("$.data[1].onHandQuantity").value(72));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin can view the inventory report")
    void getInventoryReport_AsAdmin_Returns200() throws Exception {
        when(inventoryReportService.getInventoryReport()).thenReturn(List.of());

        mockMvc.perform(get(URL))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Empty catalog returns 200 with an empty list")
    void getInventoryReport_EmptyCatalog_ReturnsEmptyList() throws Exception {
        when(inventoryReportService.getInventoryReport()).thenReturn(List.of());

        mockMvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    @DisplayName("Cashier is rejected with 403")
    void getInventoryReport_AsCashier_Returns403() throws Exception {
        mockMvc.perform(get(URL))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(inventoryReportService, never()).getInventoryReport();
    }

    @Test
    @DisplayName("Unauthenticated request is rejected with 401")
    void getInventoryReport_Anonymous_Returns401() throws Exception {
        mockMvc.perform(get(URL))
            .andExpect(status().isUnauthorized());

        verify(inventoryReportService, never()).getInventoryReport();
    }
}