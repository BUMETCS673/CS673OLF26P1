// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateProductWithCategoryDto {
    private String name;
    private String barcode;
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
}
