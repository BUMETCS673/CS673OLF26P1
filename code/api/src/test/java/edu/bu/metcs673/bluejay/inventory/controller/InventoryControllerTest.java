// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: MockMvc REST controller unit tests verifying payload wrapping and HTTP status codes.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.metcs673.bluejay.auth.security.JwtTokenProvider;
import edu.bu.metcs673.bluejay.auth.security.SecurityConfig;
import edu.bu.metcs673.bluejay.auth.security.TokenBlacklistService;
import edu.bu.metcs673.bluejay.common.exception.GlobalExceptionHandler;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryHealthResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryRequest;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryResponse;
import edu.bu.metcs673.bluejay.inventory.service.InventoryService;
import edu.bu.metcs673.bluejay.inventory.service.StockEntryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private StockEntryService stockEntryService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @DisplayName("GET /inventory should return HTTP 200 OK with wrapped InventoryResponse list")
    void getInventory_Returns200AndInventoryList() throws Exception {
        InventoryResponse item = new InventoryResponse("id-1", "1001", "Coffee", new BigDecimal("10.00"), 50);
        when(inventoryService.getInventory()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/inventory")
                .with(user("manager").roles("MANAGER")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].id").value("id-1"))
            .andExpect(jsonPath("$.data[0].barcode").value("1001"))
            .andExpect(jsonPath("$.data[0].productName").value("Coffee"))
            .andExpect(jsonPath("$.data[0].quantity").value(50));
    }

    @Test
    @DisplayName("GET /inventory/health should return HTTP 200 OK with wrapped InventoryHealthResponse list")
    void getInventoryHealth_Returns200AndHealthList() throws Exception {
        InventoryHealthResponse health = new InventoryHealthResponse("id-1", "Coffee", 50, "Low");
        when(inventoryService.getInventoryHealth()).thenReturn(List.of(health));

        mockMvc.perform(get("/api/v1/inventory/health")
                .with(user("manager").roles("MANAGER")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].productName").value("Coffee"))
            .andExpect(jsonPath("$.data[0].percentage").value(50))
            .andExpect(jsonPath("$.data[0].status").value("Low"));
    }

    @Test
    @DisplayName("POST /inventory/stock-entry should return HTTP 201 Created on valid request")
    void createStockEntry_Returns201Created() throws Exception {
        StockEntryRequest request = new StockEntryRequest("1001", new BigDecimal("12.00"), 10);
        StockEntryResponse response = new StockEntryResponse(
            1L,
            UUID.randomUUID().toString(),
            "1001",
            "Coffee",
            10,
            60,
            new BigDecimal("12.00"),
            UUID.randomUUID().toString(),
            LocalDateTime.now(),
            "Coffee stock entry recorded."
        );

        when(stockEntryService.processStockEntry(any(StockEntryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/inventory/stock-entry")
                .with(user("admin").roles("ADMIN"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Coffee stock entry recorded."))
            .andExpect(jsonPath("$.data.movementId").value(1))
            .andExpect(jsonPath("$.data.barcode").value("1001"))
            .andExpect(jsonPath("$.data.quantityAdded").value(10))
            .andExpect(jsonPath("$.data.newTotalStock").value(60));
    }
}