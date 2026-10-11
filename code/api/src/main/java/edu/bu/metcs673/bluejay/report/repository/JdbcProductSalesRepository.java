// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Per-product aggregate SQL over products + sale_items + sale_transactions
// Human Contributions: Use the sale_items cost/price snapshots per DB_SCHEMA_CHANGELOG.md
// Notes: Extends the Story #29 date-range query by grouping per product.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.repository;

import edu.bu.metcs673.bluejay.report.dto.ProductSalesRow;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Extend the date-range sales query to group results by product"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - LEFT JOIN from products so products with no sales in the range still
//     appear with zeros (needed to spot low performers)
//   - Date filter is inside the subquery, so it only limits sales, not products
//   - Same half-open window and sale_items snapshots as JdbcSalesReportRepository
// Verification:
//   - Column names checked against V1__initial_schema.sql
//   - Manual QA with docs/qa/story-29-sales-seed.sql
// Confidence: Medium (aggregate SQL is covered by manual QA, not unit tests)
// TODO(#30): Move to a JPA/JPQL query once sale entities exist.
@Repository
public class JdbcProductSalesRepository implements ProductSalesRepository {

    private static final String PRODUCT_SALES_SQL = """
        SELECT p.id,
               p.barcode,
               p.name,
               COALESCE(SUM(s.quantity), 0)               AS quantity_sold,
               COALESCE(SUM(s.subtotal), 0)               AS total_revenue,
               COALESCE(SUM(s.unit_cost * s.quantity), 0) AS total_cost
        FROM products p
        LEFT JOIN (
            SELECT si.product_id, si.quantity, si.subtotal, si.unit_cost
            FROM sale_items si
            JOIN sale_transactions st ON st.id = si.transaction_id
            WHERE st.transaction_date >= ?
              AND st.transaction_date < ?
        ) s ON s.product_id = p.id
        GROUP BY p.id, p.barcode, p.name
        ORDER BY p.name
        """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcProductSalesRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<ProductSalesRow> findProductSales(
        LocalDateTime from,
        LocalDateTime to) {
        return jdbcTemplate.query(
            PRODUCT_SALES_SQL,
            (rs, rowNum) -> new ProductSalesRow(
                rs.getString("id"),
                rs.getString("barcode"),
                rs.getString("name"),
                rs.getLong("quantity_sold"),
                orZero(rs.getBigDecimal("total_revenue")),
                orZero(rs.getBigDecimal("total_cost"))
            ),
            Timestamp.valueOf(from),
            Timestamp.valueOf(to)
        );
    }

    private static BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}