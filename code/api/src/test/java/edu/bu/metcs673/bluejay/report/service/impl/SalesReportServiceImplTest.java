// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Mockito setup, BigDecimal assertions, date-boundary checks
// Human Contributions: Story #29 test cases (range with sales, empty range, invalid range)
// Notes: Unit tests for SalesReportServiceImpl with a mocked repository.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service.impl;

import edu.bu.metcs673.bluejay.report.dto.SalesReport;
import edu.bu.metcs673.bluejay.report.dto.SalesTotals;
import edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException;
import edu.bu.metcs673.bluejay.report.repository.SalesReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Unit tests: range with sales data, empty range with no sales, invalid range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Added a single-day range test to check the inclusive end date
// Verification:
//   - Ran ./mvnw test locally: all tests passing
// Confidence: High
class SalesReportServiceImplTest {

    private static final LocalDate OCT_1 = LocalDate.of(2026, 10, 1);
    private static final LocalDate OCT_31 = LocalDate.of(2026, 10, 31);

    private SalesReportRepository repository;
    private SalesReportServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(SalesReportRepository.class);
        service = new SalesReportServiceImpl(repository);
    }

    @Test
    @DisplayName("Range with sales returns revenue, cost and net profit")
    void getSalesReport_RangeWithSales_CalculatesNetProfit() {
        // Given
        when(repository.findSalesTotals(any(), any())).thenReturn(
            new SalesTotals(new BigDecimal("1250.00"),
                new BigDecimal("800.50"), 12, 40)
        );

        // When
        SalesReport report = service.getSalesReport(OCT_1, OCT_31);

        // Then
        assertThat(report.startDate()).isEqualTo(OCT_1);
        assertThat(report.endDate()).isEqualTo(OCT_31);
        assertThat(report.totalRevenue()).isEqualByComparingTo("1250.00");
        assertThat(report.totalCost()).isEqualByComparingTo("800.50");
        assertThat(report.netProfit()).isEqualByComparingTo("449.50");
        assertThat(report.transactionCount()).isEqualTo(12);
        assertThat(report.unitsSold()).isEqualTo(40);
    }

    @Test
    @DisplayName("Net profit is negative when cost is higher than revenue")
    void getSalesReport_LossMaking_ReturnsNegativeNetProfit() {
        when(repository.findSalesTotals(any(), any())).thenReturn(
            new SalesTotals(new BigDecimal("100.00"),
                new BigDecimal("130.00"), 1, 2)
        );

        SalesReport report = service.getSalesReport(OCT_1, OCT_31);

        assertThat(report.netProfit()).isEqualByComparingTo("-30.00");
    }

    @Test
    @DisplayName("Empty range with no sales returns zero totals")
    void getSalesReport_EmptyRange_ReturnsZeros() {
        // Given
        when(repository.findSalesTotals(any(), any()))
            .thenReturn(SalesTotals.empty());

        // When
        SalesReport report = service.getSalesReport(OCT_1, OCT_31);

        // Then
        assertThat(report.totalRevenue()).isEqualByComparingTo("0");
        assertThat(report.totalCost()).isEqualByComparingTo("0");
        assertThat(report.netProfit()).isEqualByComparingTo("0");
        assertThat(report.transactionCount()).isZero();
        assertThat(report.unitsSold()).isZero();
    }

    @Test
    @DisplayName("Start date after end date is rejected without querying")
    void getSalesReport_InvalidRange_Throws() {
        assertThatThrownBy(() -> service.getSalesReport(OCT_31, OCT_1))
            .isInstanceOf(InvalidDateRangeException.class)
            .hasMessageContaining("2026-10-31");

        verify(repository, never()).findSalesTotals(any(), any());
    }

    @Test
    @DisplayName("End date is inclusive: query runs up to the start of the next day")
    void getSalesReport_SingleDay_QueriesWholeDay() {
        when(repository.findSalesTotals(any(), any()))
            .thenReturn(SalesTotals.empty());

        service.getSalesReport(OCT_1, OCT_1);

        verify(repository).findSalesTotals(
            LocalDateTime.of(2026, 10, 1, 0, 0),
            LocalDateTime.of(2026, 10, 2, 0, 0)
        );
    }
}