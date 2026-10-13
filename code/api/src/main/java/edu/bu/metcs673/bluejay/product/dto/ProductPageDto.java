package edu.bu.metcs673.bluejay.product.dto;

import java.util.List;

public record ProductPageDto(
    List<ProductDto> items,
    long totalCount,
    int pageNumber,
    int pageSize
) {
}
