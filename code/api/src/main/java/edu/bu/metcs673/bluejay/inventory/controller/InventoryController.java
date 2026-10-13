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
import edu.bu.metcs673.bluejay.inventory.dto.InventoryStockResponse;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryRequest;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;
import edu.bu.metcs673.bluejay.inventory.dto.UpdateSalePriceRequest;
import edu.bu.metcs673.bluejay.inventory.service.InventoryService;
import edu.bu.metcs673.bluejay.inventory.service.StockEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Refactor InventoryController to wrap endpoint response
// in ApiResponse entity. Add a PATCH endpoint to InventoryController for inline sale price updates.
// Secure with RBAC annotations and wrap response in ApiResponse."
// AI Contribution: Initial draft (~90%)
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

    @GetMapping("/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryStockResponse>>> getInventoryStock() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getInventoryStock()));
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

    @PatchMapping("/{id}/price")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateSalePrice(
        @PathVariable("id") String productId,
        @Valid @RequestBody UpdateSalePriceRequest request) {
        InventoryResponse response = inventoryService.updateSalePrice(productId, request.salePrice());
        return ResponseEntity.ok(ApiResponse.success(response, "Sale price updated successfully."));
    }
}