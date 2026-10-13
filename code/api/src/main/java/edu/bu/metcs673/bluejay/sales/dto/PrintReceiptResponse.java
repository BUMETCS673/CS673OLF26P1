package edu.bu.metcs673.bluejay.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PrintReceiptResponse(
    UUID transactionId,
    String receiptNumber,
    LocalDateTime transactionDate,
    BigDecimal grandTotal,
    String paymentMethod,
    List<String> items
) {
}