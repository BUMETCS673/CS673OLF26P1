// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Response wrapper for the product sales analysis
// Human Contributions: Story #30 scope
// Notes: Response body of GET /api/v1/reports/sales/products.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.dto;

import java.time.LocalDate;
import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Response wrapper echoing the date range with the per-product rows"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Echoes the requested dates, like SalesReport (Story #29)
// Verification:
//   - ProductSalesControllerTest JSON assertions
// Confidence: High
public record ProductSalesReport(
    LocalDate startDate,
    LocalDate endDate,
    List<ProductSalesItem> products) {
}