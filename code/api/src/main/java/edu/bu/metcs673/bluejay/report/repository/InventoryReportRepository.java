// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Repository interface for the inventory report query
// Human Contributions: Interface + swappable implementation pattern (mirrors jpos)
// Notes: Read-only data access for Story #57.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.repository;

import edu.bu.metcs673.bluejay.report.dto.InventoryReportItem;

import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Define a repository interface returning inventory report rows"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Interface kept separate from the JDBC class so the service can be unit tested with a mock
// Verification:
//   - InventoryReportServiceImplTest
// Confidence: High
public interface InventoryReportRepository {

    /**
     * Returns one row per product with its latest cost and on-hand stock,
     * ordered by product name. Returns an empty list for an empty catalog.
     */
    List<InventoryReportItem> findInventoryReport();
}