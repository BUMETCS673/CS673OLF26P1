// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Aggregate SQL over sale_transactions + sale_items, row mapping
// Human Contributions: Use the sale_items cost/price snapshots per DB_SCHEMA_CHANGELOG.md
// Notes: Revenue and cost come from the per-sale snapshots in sale_items, so later
//        changes to products.cost_price / sale_price never change past reports.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.repository;

import edu.bu.metcs673.bluejay.report.dto.SalesTotals;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Design query joining sale records with product cost data, filtered by date range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Cost is sale_items.unit_cost * quantity (cost snapshot at time of sale),
//     not products.cost_price, so historical profit stays correct
//   - Revenue is the sum of sale_items.subtotal
//   - Half-open window (>= from, < to) so a whole end day is included
//   - COALESCE returns zeros instead of nulls for a range with no sales
// Verification:
//   - Column names checked against V1__initial_schema.sql
//   - Manual QA with docs/qa/story-29-sales-seed.sql
// Confidence: Medium (aggregate SQL is covered by manual QA, not unit tests)
// TODO(#29): Move to a JPA/JPQL query once sale entities exist.
@Repository
public class JdbcSalesReportRepository implements SalesReportRepository {

    private static final String SALES_TOTALS_SQL = """
        SELECT COUNT(DISTINCT st.id)                   AS transaction_count,
               COALESCE(SUM(si.quantity), 0)           AS units_sold,
               COALESCE(SUM(si.subtotal), 0)           AS total_revenue,
               COALESCE(SUM(si.unit_cost * si.quantity), 0) AS total_cost
        FROM sale_transactions st
        JOIN sale_items si ON si.transaction_id = st.id
        WHERE st.transaction_date >= ?
          AND st.transaction_date < ?
        """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcSalesReportRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SalesTotals findSalesTotals(LocalDateTime from, LocalDateTime to) {
        SalesTotals totals = jdbcTemplate.queryForObject(
            SALES_TOTALS_SQL,
            (rs, rowNum) -> new SalesTotals(
                orZero(rs.getBigDecimal("total_revenue")),
                orZero(rs.getBigDecimal("total_cost")),
                rs.getLong("transaction_count"),
                rs.getLong("units_sold")
            ),
            Timestamp.valueOf(from),
            Timestamp.valueOf(to)
        );
        return Objects.requireNonNullElseGet(totals, SalesTotals::empty);
    }

    private static BigDecimal orZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}