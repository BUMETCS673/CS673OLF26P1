package edu.bu.metcs673.bluejay.product.dto;

import java.util.UUID;

public record ProductDto(
    UUID id,
    String name,
    String barcode,
    Long categoryId,
    String categoryName,
    String categoryDescription,
    double price
) {
}
