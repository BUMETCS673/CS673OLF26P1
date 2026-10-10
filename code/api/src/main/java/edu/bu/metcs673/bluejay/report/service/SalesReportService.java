// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Service interface
// Human Contributions: Story #29 scope
// Notes: Service contract for the sales report by date range.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service;

import edu.bu.metcs673.bluejay.report.dto.SalesReport;

import java.time.LocalDate;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Service interface for the sales report by date range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Both dates are inclusive calendar days
// Verification:
//   - SalesReportServiceImplTest
// Confidence: High
public interface SalesReportService {

    /**
     * Builds the sales report for every sale from the start of
     * {@code startDate} through the end of {@code endDate}.
     *
     * @throws edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException
     *         if startDate is after endDate
     */
    SalesReport getSalesReport(LocalDate startDate, LocalDate endDate);
}