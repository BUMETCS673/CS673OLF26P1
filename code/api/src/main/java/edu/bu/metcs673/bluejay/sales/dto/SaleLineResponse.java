package edu.bu.metcs673.bluejay.sales.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleLineResponse(
    UUID productId,
    String name,
    int quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal
) {
}