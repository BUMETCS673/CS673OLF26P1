// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Date-range validation, day-boundary conversion, net profit calculation
// Human Contributions: Story #29 scope and test cases
// Notes: Validates the range, queries totals, and calculates net profit.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service.impl;

import edu.bu.metcs673.bluejay.report.dto.SalesReport;
import edu.bu.metcs673.bluejay.report.dto.SalesTotals;
import edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException;
import edu.bu.metcs673.bluejay.report.repository.SalesReportRepository;
import edu.bu.metcs673.bluejay.report.service.SalesReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Calculate total revenue, total cost, and net profit from the query results"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Rejects startDate after endDate before touching the database
//   - Converts inclusive dates to [startDate 00:00, endDate+1 00:00)
//   - Net profit = total revenue - total cost
// Verification:
//   - SalesReportServiceImplTest (sales in range, empty range, invalid range)
// Confidence: High
@Service
public class SalesReportServiceImpl implements SalesReportService {

    private final SalesReportRepository salesReportRepository;

    public SalesReportServiceImpl(SalesReportRepository salesReportRepository) {
        this.salesReportRepository = salesReportRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public SalesReport getSalesReport(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }

        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.plusDays(1).atStartOfDay();
        SalesTotals totals = salesReportRepository.findSalesTotals(from, to);

        return new SalesReport(
            startDate,
            endDate,
            totals.totalRevenue(),
            totals.totalCost(),
            totals.totalRevenue().subtract(totals.totalCost()),
            totals.transactionCount(),
            totals.unitsSold()
        );
    }
}