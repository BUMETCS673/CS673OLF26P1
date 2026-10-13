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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
    UUID transactionId,
    String receiptNumber,
    LocalDateTime transactionDate,
    BigDecimal grandTotal,
    BigDecimal cashReceived,
    BigDecimal changeDue,
    String paymentMethod,
    List<SaleLineResponse> items,
    List<ChangeDenominationResponse> changeBreakdown
) {
}