/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 90%
    AI-Assisted Areas: Creating unit tests for sale controller
    Human Contributions: Prompted what tests should be added
    Notes: Unit tests for sale controller
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.sales.controller;

import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.SecurityConfig;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.common.exception.GlobalExceptionHandler;
import edu.bu.metcs673.bluejay.sales.dto.SaleRequest;
import edu.bu.metcs673.bluejay.sales.service.SaleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class SaleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleService saleService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Nested
    @DisplayName("POST /api/v1/sales")
    class CheckoutEndpoint {

    @Test
    void allowsCashierToCompleteSale() throws Exception {
        when(saleService.checkout(any(SaleRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/sales")
                .with(user("cashier").roles("CASHIER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "cashReceived": 10.00,
                      "items": [{
                        "productId": "12345678-1234-1234-1234-123456789abc",
                        "quantity": 2
                      }]
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Sale completed successfully"));

        verify(saleService).checkout(any(SaleRequest.class));
    }

    @Test
    void allowsManagerToCompleteSale() throws Exception {
      when(saleService.checkout(any(SaleRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/sales")
                .with(user("manager").roles("MANAGER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void allowsAdminToCompleteSale() throws Exception {
      when(saleService.checkout(any(SaleRequest.class))).thenReturn(null);

        mockMvc.perform(post("/api/v1/sales")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void requiresAuthenticationToCompleteSale() throws Exception {
        mockMvc.perform(post("/api/v1/sales")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void validatesSaleRequestBeforeCheckout() throws Exception {
        mockMvc.perform(post("/api/v1/sales")
                .with(user("cashier").roles("CASHIER"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cashReceived\": 10.00, \"items\": []}"))
            .andExpect(status().isBadRequest());
    }

    private String validRequest() {
        return """
            {
              "cashReceived": 10.00,
              "items": [{
                "productId": "12345678-1234-1234-1234-123456789abc",
                "quantity": 2
              }]
            }
            """;
    }
        }
}