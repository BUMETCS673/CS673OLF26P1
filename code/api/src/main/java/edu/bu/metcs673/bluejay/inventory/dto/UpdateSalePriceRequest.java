// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Request DTO record with Jakarta Validation constraints for sale price updates.
// Human Contributions: Verification with Spring MVC validation rules and integration testing.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create a Java 21 Record DTO for updating product sale price. Include Jakarta validation for null check and non-negative value."
// AI Contribution: Initial draft (~100%)
// Modifications: Applied Record pattern with @NotNull and @DecimalMin annotations.
// Verification: Spring MVC MockMvc controller validation unit tests.
// Confidence: High
public record UpdateSalePriceRequest(
    @NotNull(message = "Sale price is required")
    @DecimalMin(value = "0.00", message = "Sale price cannot be negative")
    BigDecimal salePrice
) {}