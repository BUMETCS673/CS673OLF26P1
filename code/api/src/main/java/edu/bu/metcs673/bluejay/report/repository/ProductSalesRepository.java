// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Repository interface for the per-product sales query
// Human Contributions: Interface + swappable implementation pattern (mirrors jpos)
// Notes: Read-only data access for Story #30.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.repository;

import edu.bu.metcs673.bluejay.report.dto.ProductSalesRow;

import java.time.LocalDateTime;
import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Repository interface returning per-product sales totals for a time window"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Interface kept separate from the JDBC class so the service can be unit tested with a mock
// Verification:
//   - ProductSalesServiceImplTest
// Confidence: High
public interface ProductSalesRepository {

    /**
     * Returns one row per product in the catalog with the quantity, revenue
     * and cost of its sales whose transaction_date is on or after
     * {@code from} and before {@code to}. Products with no sales in the
     * window are included with zero totals.
     */
    List<ProductSalesRow> findProductSales(LocalDateTime from, LocalDateTime to);
}