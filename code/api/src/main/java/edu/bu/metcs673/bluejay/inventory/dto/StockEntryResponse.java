// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Response DTO containing updated inventory details and
// movement log information.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create response DTO for stock entry return payload.
// Include updated stock levels and audit information."
// AI Contribution: Initial draft (~100%)
// Modifications: Structured record attributes for UI notification and table
// update.
// Verification: Integration testing.
// Confidence: High
public record StockEntryResponse(
    Long movementId,
    String productId,
    String barcode,
    String productName,
    Integer quantityAdded,
    Integer newTotalStock,
    BigDecimal costPrice,
    String userId,
    LocalDateTime createdAt,
    String message
) {
}