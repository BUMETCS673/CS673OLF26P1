// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Record holding the raw aggregates from the sales query
// Human Contributions: Story #29 scope (revenue, cost, net profit for a date range)
// Notes: Raw query result; SalesReportServiceImpl turns it into a SalesReport.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.dto;

import java.math.BigDecimal;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Record for the summed revenue, cost, transaction and unit counts of a date range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Money kept as BigDecimal to match DECIMAL(12,2) in sale_items
// Verification:
//   - SalesReportServiceImplTest
// Confidence: High
public record SalesTotals(
    BigDecimal totalRevenue,
    BigDecimal totalCost,
    long transactionCount,
    long unitsSold) {

    public static SalesTotals empty() {
        return new SalesTotals(BigDecimal.ZERO, BigDecimal.ZERO, 0, 0);
    }
}