// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Create a DTO for filtered product list requests with pagination"
// AI Contribution: DTO scaffolding for product search and pagination inputs (~90%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.dto;

public record ProductQueryDto(
    String productName,
    String categoryName,
    Integer pageNumber,
    Integer pageSize
) {
}
