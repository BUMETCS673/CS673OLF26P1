// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Record design and field selection for the inventory report row
// Human Contributions: Story #57 acceptance criteria (name, latest cost, on-hand quantity)
// Notes: One row of the core inventory report (Story #57).
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.dto;

import java.math.BigDecimal;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Create a DTO for an inventory report row with product, latest cost and on-hand quantity"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Kept cost as BigDecimal to match DECIMAL(12,2) in products.cost_price
// Verification:
//   - Checked field types against V1__initial_schema.sql
// Confidence: High
public record InventoryReportItem(
    String productId,
    String barcode,
    String name,
    BigDecimal latestCost,
    int onHandQuantity) {
}