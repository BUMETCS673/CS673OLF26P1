// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Response record for one product in the product sales analysis
// Human Contributions: Story #30 fields (quantity sold, total cost, profit)
// Notes: One row of GET /api/v1/reports/sales/products.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.dto;

import java.math.BigDecimal;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Response DTO for one product's quantity sold, revenue, cost and profit"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Includes revenue so the UI can show how profit is made up
// Verification:
//   - ProductSalesControllerTest JSON assertions
// Confidence: High
public record ProductSalesItem(
    String productId,
    String barcode,
    String name,
    long quantitySold,
    BigDecimal totalRevenue,
    BigDecimal totalCost,
    BigDecimal profit) {
}