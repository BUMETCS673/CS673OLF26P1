package edu.bu.metcs673.bluejay.sale.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.sale.dto.PrintReceiptRequest;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
@AutoConfigureMockMvc(addFilters = false)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Add these missing bean mocks so JwtAuthenticationFilter can initialize
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    // Needed by both AuthController and JwtAuthenticationFilter
    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Nested
    @DisplayName("POST /sale tests")
    class SaleEndpoint {
        
        @Test
        @DisplayName("Should return total price and itemized items")
        void notfoundPath() throws Exception {
            // Given
            final var prr = new PrintReceiptRequest("be596978-01d5-425c-af96-829626d69824");
            
            mockMvc.perform(post("/api/v1/sale/printReceipt")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(prr)))
                .andExpect(jsonPath("$.message").value("Unable to find the requested transactionId"));

        }
    }
}