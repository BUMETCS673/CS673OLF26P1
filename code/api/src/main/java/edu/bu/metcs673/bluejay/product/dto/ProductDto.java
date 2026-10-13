// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Create a DTO for a single product"
// AI Contribution: DTO scaffolding for a product record (~90%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.dto;

import java.util.UUID;

public record ProductDto(
    UUID id,
    String name,
    String barcode,
    Long categoryId,
    String categoryName,
    String categoryDescription,
    double price,
    int currentStock,
    int lowStock
) {
}
