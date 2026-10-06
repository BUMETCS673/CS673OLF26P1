// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.dto;

public record CreateProductWithCategoryDto(
    String name,
    String barcode,
    Long categoryId,
    String categoryName,
    String categoryDescription
) {
}
