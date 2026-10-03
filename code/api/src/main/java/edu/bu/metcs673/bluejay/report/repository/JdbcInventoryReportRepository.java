// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: JdbcTemplate query and row mapping
// Human Contributions: Decision to read products.cost_price / current_stock directly
// Notes: Uses plain SQL instead of a JPA entity so this report does not depend
//        on the Product model still in progress on the add-new-product branch.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.repository;

import edu.bu.metcs673.bluejay.report.dto.InventoryReportItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Backend query joining product + latest cost + current stock quantity"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - In the current schema the latest cost (cost_price) and on-hand stock
//     (current_stock) are both kept on the products row, so no join is needed
//   - Ordered by name so the report is stable for the UI and tests
// Verification:
//   - Checked column names against V1__initial_schema.sql
// Confidence: Medium (not yet run against the MySQL container)
@Repository
public class JdbcInventoryReportRepository
    implements InventoryReportRepository {

    private static final String INVENTORY_REPORT_SQL = """
        SELECT p.id,
               p.barcode,
               p.name,
               p.cost_price,
               p.current_stock
        FROM products p
        ORDER BY p.name
        """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcInventoryReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<InventoryReportItem> findInventoryReport() {
        return jdbcTemplate.query(
            INVENTORY_REPORT_SQL,
            (rs, rowNum) -> new InventoryReportItem(
                rs.getString("id"),
                rs.getString("barcode"),
                rs.getString("name"),
                rs.getBigDecimal("cost_price"),
                rs.getInt("current_stock")
            )
        );
    }
}