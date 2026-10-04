package edu.bu.metcs673.bluejay.sale.dto;

import jakarta.validation.constraints.NotBlank;
public record PrintReceiptRequest(
    @NotBlank(message = "transactionId is required")
    String transactionId
) {
}