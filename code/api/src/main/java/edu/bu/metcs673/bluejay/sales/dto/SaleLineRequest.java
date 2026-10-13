/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Record creation
    Human Contributions: None
    Notes:
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.sales.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record SaleLineRequest(
    @NotNull UUID productId,
    @NotNull @Positive Integer quantity
) {
}