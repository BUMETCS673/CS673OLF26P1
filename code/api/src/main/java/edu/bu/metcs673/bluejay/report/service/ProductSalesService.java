// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Service interface
// Human Contributions: Story #30 scope
// Notes: Service contract for the per-product sales analysis.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service;

import edu.bu.metcs673.bluejay.report.dto.ProductSalesReport;

import java.time.LocalDate;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Service interface for per-product sales analysis by date range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Both dates are inclusive calendar days, same as the Story #29 report
// Verification:
//   - ProductSalesServiceImplTest
// Confidence: High
public interface ProductSalesService {

    /**
     * Builds per-product quantity sold, revenue, cost and profit for every
     * sale from the start of {@code startDate} through the end of
     * {@code endDate}, sorted by profit (highest first).
     *
     * @throws edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException
     *         if startDate is after endDate
     */
    ProductSalesReport getProductSales(LocalDate startDate, LocalDate endDate);
}