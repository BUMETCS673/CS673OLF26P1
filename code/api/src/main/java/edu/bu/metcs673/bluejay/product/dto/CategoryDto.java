// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Create a lightweight category DTO for product category selection responses"
// AI Contribution: DTO scaffolding and field selection for category API responses (~90%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {
    private Long id;
    private String name;
    private String description;
}
