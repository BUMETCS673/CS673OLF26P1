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

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record SaleRequest(
    @NotEmpty List<@Valid SaleLineRequest> items,
    @NotNull @DecimalMin("0.00") @Digits(integer = 10, fraction = 2)
    BigDecimal cashReceived
) {
}