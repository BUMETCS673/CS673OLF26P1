// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 90%
// AI-Assisted Areas: REST Controller wrapped in standardized ApiResponse
// contract with RBAC security annotations.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.controller;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryHealthResponse;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryRequest;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;
import edu.bu.metcs673.bluejay.inventory.service.InventoryService;
import edu.bu.metcs673.bluejay.inventory.service.StockEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Refactor InventoryController to wrap endpoint response
// in ApiResponse entity."
// AI Contribution: Initial draft (~95%)
// Modifications: Wrapped StockEntryResponse inside ApiResponse.success
// static factory method.
// Verification: Postman integration test for API payload structure.
// Confidence: High
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final StockEntryService stockEntryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventory() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getInventory()));
    }

    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryHealthResponse>>> getInventoryHealth() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getInventoryHealth()));
    }

    @PostMapping("/stock-entry")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<StockEntryResponse>> createStockEntry(
        @Valid @RequestBody StockEntryRequest request) {
        StockEntryResponse response = stockEntryService.processStockEntry(
            request);
        return new ResponseEntity<>(
            ApiResponse.success(response, response.message()),
            HttpStatus.CREATED
        );
    }

}