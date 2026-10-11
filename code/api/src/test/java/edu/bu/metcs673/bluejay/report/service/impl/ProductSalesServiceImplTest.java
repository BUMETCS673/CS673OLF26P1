// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Mockito setup, BigDecimal assertions, sorting checks
// Human Contributions: Story #30 test cases (multiple products, zero-sales product, invalid range)
// Notes: Unit tests for ProductSalesServiceImpl with a mocked repository.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service.impl;

import edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesItem;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesReport;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesRow;
import edu.bu.metcs673.bluejay.report.repository.ProductSalesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Unit tests: multiple products with sales, a product with zero sales, invalid range"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Added a loss-making product to check negative profit and ordering
// Verification:
//   - Ran ./mvnw test locally: all passed
// Confidence: High
class ProductSalesServiceImplTest {

    private static final LocalDate OCT_1 = LocalDate.of(2026, 10, 1);
    private static final LocalDate OCT_31 = LocalDate.of(2026, 10, 31);

    private ProductSalesRepository repository;
    private ProductSalesServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(ProductSalesRepository.class);
        service = new ProductSalesServiceImpl(repository);
    }

    private static ProductSalesRow row(String id, String name, long qty,
                                       String revenue, String cost) {
        return new ProductSalesRow(id, "BC-" + id, name, qty,
            new BigDecimal(revenue), new BigDecimal(cost));
    }

    @Test
    @DisplayName("Multiple products: profit per product, sorted highest profit first")
    void getProductSales_MultipleProducts_CalculatesProfitAndSorts() {
        // Given (repository returns rows ordered by name)
        when(repository.findProductSales(any(), any())).thenReturn(List.of(
            row("p1", "Cooking Oil 2L", 5, "32.50", "20.50"),
            row("p2", "Premium Rice 5kg", 2, "29.98", "19.98"),
            row("p3", "Sparkling Water", 4, "10.00", "6.00")
        ));

        // When
        ProductSalesReport report = service.getProductSales(OCT_1, OCT_31);

        // Then
        assertThat(report.startDate()).isEqualTo(OCT_1);
        assertThat(report.endDate()).isEqualTo(OCT_31);
        assertThat(report.products())
            .extracting(ProductSalesItem::name)
            .containsExactly("Cooking Oil 2L", "Premium Rice 5kg",
                "Sparkling Water");

        ProductSalesItem oil = report.products().get(0);
        assertThat(oil.quantitySold()).isEqualTo(5);
        assertThat(oil.totalCost()).isEqualByComparingTo("20.50");
        assertThat(oil.profit()).isEqualByComparingTo("12.00");
        assertThat(report.products().get(1).profit())
            .isEqualByComparingTo("10.00");
        assertThat(report.products().get(2).profit())
            .isEqualByComparingTo("4.00");
    }

    @Test
    @DisplayName("A product with zero sales is included with zero quantity, cost and profit")
    void getProductSales_ProductWithNoSales_IncludedWithZeros() {
        when(repository.findProductSales(any(), any())).thenReturn(List.of(
            row("p1", "Lemons", 0, "0", "0"),
            row("p2", "Premium Rice 5kg", 2, "29.98", "19.98")
        ));

        ProductSalesReport report = service.getProductSales(OCT_1, OCT_31);

        assertThat(report.products()).hasSize(2);
        ProductSalesItem lemons = report.products().get(1);
        assertThat(lemons.name()).isEqualTo("Lemons");
        assertThat(lemons.quantitySold()).isZero();
        assertThat(lemons.totalCost()).isEqualByComparingTo("0");
        assertThat(lemons.profit()).isEqualByComparingTo("0");
    }

    @Test
    @DisplayName("Loss-making product has negative profit and sorts below zero-sales products")
    void getProductSales_LossMaking_NegativeProfitSortedLast() {
        when(repository.findProductSales(any(), any())).thenReturn(List.of(
            row("p1", "Bread", 3, "6.00", "9.00"),
            row("p2", "Lemons", 0, "0", "0")
        ));

        ProductSalesReport report = service.getProductSales(OCT_1, OCT_31);

        assertThat(report.products())
            .extracting(ProductSalesItem::name)
            .containsExactly("Lemons", "Bread");
        assertThat(report.products().get(1).profit())
            .isEqualByComparingTo("-3.00");
    }

    @Test
    @DisplayName("Empty catalog returns an empty list")
    void getProductSales_EmptyCatalog_ReturnsEmptyList() {
        when(repository.findProductSales(any(), any())).thenReturn(List.of());

        ProductSalesReport report = service.getProductSales(OCT_1, OCT_31);

        assertThat(report.products()).isEmpty();
    }

    @Test
    @DisplayName("Start date after end date is rejected without querying")
    void getProductSales_InvalidRange_Throws() {
        assertThatThrownBy(() -> service.getProductSales(OCT_31, OCT_1))
            .isInstanceOf(InvalidDateRangeException.class);

        verify(repository, never()).findProductSales(any(), any());
    }

    @Test
    @DisplayName("End date is inclusive: query runs up to the start of the next day")
    void getProductSales_SingleDay_QueriesWholeDay() {
        when(repository.findProductSales(any(), any())).thenReturn(List.of());

        service.getProductSales(OCT_1, OCT_1);

        verify(repository).findProductSales(
            LocalDateTime.of(2026, 10, 1, 0, 0),
            LocalDateTime.of(2026, 10, 2, 0, 0)
        );
    }
}