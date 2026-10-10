// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Repository interface for the sales report query
// Human Contributions: Interface + swappable implementation pattern (mirrors jpos)
// Notes: Read-only data access for Story #29.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.repository;

import edu.bu.metcs673.bluejay.report.dto.SalesTotals;

import java.time.LocalDateTime;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Define a repository interface that sums sales between two timestamps"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Interface kept separate from the JDBC class so the service can be unit tested with a mock
// Verification:
//   - SalesReportServiceImplTest
// Confidence: High
public interface SalesReportRepository {

    /**
     * Sums revenue, cost, transactions and units for sales whose
     * transaction_date is on or after {@code from} and before {@code to}.
     * Returns zero totals when there are no sales in the window.
     */
    SalesTotals findSalesTotals(LocalDateTime from, LocalDateTime to);
}