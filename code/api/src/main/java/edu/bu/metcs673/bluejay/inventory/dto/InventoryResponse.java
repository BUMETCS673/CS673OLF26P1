// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: DTO record representing full inventory stock details and health metrics.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.dto;

import java.math.BigDecimal;

public record InventoryResponse(
    String id,
    String barcode,
    String productName,
    BigDecimal cost,
    int quantity
) {}