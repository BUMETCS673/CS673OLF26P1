// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~90%
// AI-Assisted Areas: Added updateSalePrice interface contract to inventory service.
// Human Contributions: Integration with domain workflows and repository contracts.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.service;

import edu.bu.metcs673.bluejay.inventory.dto.InventoryStockResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;

import java.math.BigDecimal;
import java.util.List;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Update InventoryService interface to include a contract for updating product sale price. Return updated InventoryResponse DTO."
// AI Contribution: Initial draft (~90%)
// Modifications: Added updateSalePrice declaration with productId and salePrice params.
// Verification: Unit testing with Mockito.
// Confidence: High
public interface InventoryService {

    List<InventoryStockResponse> getInventoryStock();
    List<InventoryResponse> getInventory();
    InventoryResponse updateSalePrice(String productId, BigDecimal salePrice);

}
