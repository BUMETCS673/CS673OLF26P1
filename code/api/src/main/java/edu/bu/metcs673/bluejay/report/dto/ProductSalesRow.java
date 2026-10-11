// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Record for one product's raw sales aggregates
// Human Contributions: Story #30 scope (quantity sold, total cost, profit per product)
// Notes: Raw query row; ProductSalesServiceImpl adds profit and sorting.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.dto;

import java.math.BigDecimal;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Record for per-product quantity, revenue and cost in a date range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Money kept as BigDecimal to match DECIMAL(12,2) in sale_items
// Verification:
//   - ProductSalesServiceImplTest
// Confidence: High
public record ProductSalesRow(
    String productId,
    String barcode,
    String name,
    long quantitySold,
    BigDecimal totalRevenue,
    BigDecimal totalCost) {
}