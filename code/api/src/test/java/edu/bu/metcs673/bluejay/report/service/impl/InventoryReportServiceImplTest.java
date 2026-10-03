// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Mockito setup and assertions
// Human Contributions: Test scenarios from Story #57 (report with products, empty catalog)
// Notes: Unit tests for InventoryReportServiceImpl with a mocked repository.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service.impl;

import edu.bu.metcs673.bluejay.report.dto.InventoryReportItem;
import edu.bu.metcs673.bluejay.report.repository.InventoryReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Unit tests: report with products, empty catalog"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Used varying stock levels to mirror acceptance test 1
// Verification:
//   - Ran ./mvnw test locally: all tests passing
// Confidence: High
class InventoryReportServiceImplTest {

    private InventoryReportRepository repository;
    private InventoryReportServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(InventoryReportRepository.class);
        service = new InventoryReportServiceImpl(repository);
    }

    @Test
    @DisplayName("Returns name, latest cost and on-hand quantity for each product")
    void getInventoryReport_WithProducts_ReturnsEachProduct() {
        // Given
        List<InventoryReportItem> rows = List.of(
            new InventoryReportItem("p-1", "111", "Cooking Oil 2L",
                new BigDecimal("4.25"), 34),
            new InventoryReportItem("p-2", "222", "Laundry Soap",
                new BigDecimal("2.10"), 0),
            new InventoryReportItem("p-3", "333", "Premium Rice 5kg",
                new BigDecimal("9.99"), 72)
        );
        when(repository.findInventoryReport()).thenReturn(rows);

        // When
        List<InventoryReportItem> result = service.getInventoryReport();

        // Then
        assertThat(result).hasSize(3);
        assertThat(result)
            .extracting(InventoryReportItem::name)
            .containsExactly("Cooking Oil 2L", "Laundry Soap",
                "Premium Rice 5kg");
        assertThat(result)
            .extracting(InventoryReportItem::latestCost)
            .containsExactly(new BigDecimal("4.25"),
                new BigDecimal("2.10"), new BigDecimal("9.99"));
        assertThat(result)
            .extracting(InventoryReportItem::onHandQuantity)
            .containsExactly(34, 0, 72);
        verify(repository).findInventoryReport();
    }

    @Test
    @DisplayName("Returns an empty list when the catalog is empty")
    void getInventoryReport_EmptyCatalog_ReturnsEmptyList() {
        // Given
        when(repository.findInventoryReport()).thenReturn(List.of());

        // When
        List<InventoryReportItem> result = service.getInventoryReport();

        // Then
        assertThat(result).isNotNull().isEmpty();
    }
}