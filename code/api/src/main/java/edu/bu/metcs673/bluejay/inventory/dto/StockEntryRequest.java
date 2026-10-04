// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Java 21 Record implementation for request DTO with
// Compact Constructor validation.
// Human Contributions: Custom annotations if needed.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Refactor StockEntryRequest into a Java 21 Record with
// Jakarta validation annotations."
// AI Contribution: Initial draft (~100%)
// Modifications: Converted class to Java 21 Record based on user request.
// Verification: Spring MVC validation unit tests.
// Confidence: High
public record StockEntryRequest(
    @NotBlank(message = "Barcode is required")
    String barcode,

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.00", message = "Cost cannot be negative")
    BigDecimal cost,

    @NotNull(message = "Invalid quantity")
    @Positive(message = "Invalid quantity")
    Integer quantity
) {
}