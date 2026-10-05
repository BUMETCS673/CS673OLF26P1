// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Create a DTO for filtered product list requests with pagination"
// AI Contribution: DTO scaffolding for product search and pagination inputs (~90%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQueryDto {
    private String productName;
    private String categoryName;
    private Integer pageNumber;
    private Integer pageSize;
}
