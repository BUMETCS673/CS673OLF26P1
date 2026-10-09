// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Response record for the sales report
// Human Contributions: Story #29 fields (total revenue, total cost, net profit)
// Notes: Response body of GET /api/v1/reports/sales.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Response DTO for a consolidated sales report over a date range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Echoes the requested dates so the UI can show the period it is displaying
//   - Includes transaction and unit counts for context next to the money totals
// Verification:
//   - SalesReportControllerTest JSON assertions
// Confidence: High
public record SalesReport(
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal totalRevenue,
    BigDecimal totalCost,
    BigDecimal netProfit,
    long transactionCount,
    long unitsSold) {
}