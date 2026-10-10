// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: WebMvcTest setup with method security, MockMvc assertions
// Human Contributions: Story #29 scenarios and sub-issues #40 (totals) and #41 (Admin only)
// Notes: Controller slice test for GET /api/v1/reports/sales, including the role guard
//        and parameter validation.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.controller;

import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.SecurityConfig;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.report.dto.SalesReport;
import edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException;
import edu.bu.metcs673.bluejay.report.service.SalesReportService;
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
// Prompt Summary: "MockMvc tests for the Admin-only sales report endpoint with date params"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Same springSecurity() MockMvc setup as ReportControllerTest (Story #57)
//   - Covers Admin 200, Manager/Cashier 403, anonymous 401, and 400 for
//     invalid range, missing parameter and bad date format
// Verification:
//   - Pending: run ./mvnw test locally before merging
// Confidence: High
@WebMvcTest(SalesReportController.class)
@Import(SecurityConfig.class)
class SalesReportControllerTest {

    private static final String URL = "/api/v1/reports/sales";
    private static final LocalDate OCT_1 = LocalDate.of(2026, 10, 1);
    private static final LocalDate OCT_31 = LocalDate.of(2026, 10, 31);

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private SalesReportService salesReportService;

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
    @DisplayName("Admin gets total revenue, cost and net profit for the range")
    void getSalesReport_AsAdmin_Returns200WithTotals() throws Exception {
        when(salesReportService.getSalesReport(OCT_1, OCT_31)).thenReturn(
            new SalesReport(OCT_1, OCT_31, new BigDecimal("1250.00"),
                new BigDecimal("800.50"), new BigDecimal("449.50"), 12, 40)
        );

        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.startDate").value("2026-10-01"))
            .andExpect(jsonPath("$.data.endDate").value("2026-10-31"))
            .andExpect(jsonPath("$.data.totalRevenue").value(1250.00))
            .andExpect(jsonPath("$.data.totalCost").value(800.50))
            .andExpect(jsonPath("$.data.netProfit").value(449.50))
            .andExpect(jsonPath("$.data.transactionCount").value(12))
            .andExpect(jsonPath("$.data.unitsSold").value(40));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Manager is rejected with 403")
    void getSalesReport_AsManager_Returns403() throws Exception {
        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));

        verify(salesReportService, never()).getSalesReport(any(), any());
    }

    @Test
    @WithMockUser(roles = "CASHIER")
    @DisplayName("Cashier is rejected with 403")
    void getSalesReport_AsCashier_Returns403() throws Exception {
        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isForbidden());

        verify(salesReportService, never()).getSalesReport(any(), any());
    }

    @Test
    @DisplayName("Unauthenticated request is rejected with 401")
    void getSalesReport_Anonymous_Returns401() throws Exception {
        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-01")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Start date after end date returns 400 INVALID_DATE_RANGE")
    void getSalesReport_InvalidRange_Returns400() throws Exception {
        when(salesReportService.getSalesReport(OCT_31, OCT_1))
            .thenThrow(new InvalidDateRangeException(OCT_31, OCT_1));

        mockMvc.perform(get(URL)
                .param("startDate", "2026-10-31")
                .param("endDate", "2026-10-01"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errorCode").value("INVALID_DATE_RANGE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Missing endDate returns 400")
    void getSalesReport_MissingParam_Returns400() throws Exception {
        mockMvc.perform(get(URL).param("startDate", "2026-10-01"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode")
                .value("INVALID_REQUEST_PARAMETER"));

        verify(salesReportService, never()).getSalesReport(any(), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Malformed date returns 400")
    void getSalesReport_BadDateFormat_Returns400() throws Exception {
        mockMvc.perform(get(URL)
                .param("startDate", "10/01/2026")
                .param("endDate", "2026-10-31"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errorCode")
                .value("INVALID_REQUEST_PARAMETER"));

        verify(salesReportService, never()).getSalesReport(any(), any());
    }
}