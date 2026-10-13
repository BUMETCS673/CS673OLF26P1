// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: DTO record definition for transmitting inventory health metrics.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.dto;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create InventoryStockResponse record representing dynamic stock level metrics and alert status."
// AI Contribution: Initial draft (~100%)
// Modifications: Formatted fields for stock quantities and low stock threshold.
// Verification: Jackson JSON serialization test.
// Confidence: High
public record InventoryStockResponse(
    String id,
    String productName,
    int currentStock,
    int lowStockThreshold
) {}