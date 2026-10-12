// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Request DTO record with Jakarta Validation constraints for low stock threshold updates.
// Human Contributions: Verification with Spring MVC validation rules and integration testing.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create a Java 21 Record DTO for updating product low stock threshold. Include Jakarta validation for non-null and non-negative value."
// AI Contribution: Initial draft (~100%)
// Modifications: Created record with @NotNull and @Min(1) constraints.
// Verification: Spring MVC MockMvc controller validation unit tests.
// Confidence: High
public record UpdateLowStockRequest(
    @NotNull(message = "Low stock threshold is required")
    @Min(value = 1, message = "Low stock threshold cannot be negative")
    Integer lowStock
) {}