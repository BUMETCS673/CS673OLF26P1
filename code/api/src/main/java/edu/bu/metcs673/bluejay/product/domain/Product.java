// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class Product {
    private UUID id;
    private String name;
    private String barcode;
    private int categoryId;
}
