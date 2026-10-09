// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Date-range validation, per-product profit calculation, sorting
// Human Contributions: Story #30 scope and test cases
// Notes: Validates the range, queries per-product totals, adds profit, sorts by profit.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service.impl;

import edu.bu.metcs673.bluejay.common.exception.InvalidDateRangeException;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesItem;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesReport;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesRow;
import edu.bu.metcs673.bluejay.report.repository.ProductSalesRepository;
import edu.bu.metcs673.bluejay.report.service.ProductSalesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Calculate per-product quantity sold, total cost, and profit"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Same validation and inclusive day boundaries as SalesReportServiceImpl (#29)
//   - Profit = revenue - cost for each product
//   - Default order: profit high to low, then name, so the best performers
//     are on top and products with no sales sit at the bottom
// Verification:
//   - ProductSalesServiceImplTest (multiple products, zero-sales product, invalid range)
// Confidence: High
@Service
public class ProductSalesServiceImpl implements ProductSalesService {

    private static final Comparator<ProductSalesItem> BY_PROFIT_DESC =
        Comparator.comparing(ProductSalesItem::profit).reversed()
            .thenComparing(ProductSalesItem::name,
                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    private final ProductSalesRepository productSalesRepository;

    public ProductSalesServiceImpl(
        ProductSalesRepository productSalesRepository) {
        this.productSalesRepository = productSalesRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSalesReport getProductSales(
        LocalDate startDate,
        LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }

        LocalDateTime from = startDate.atStartOfDay();
        LocalDateTime to = endDate.plusDays(1).atStartOfDay();

        List<ProductSalesItem> products = productSalesRepository
            .findProductSales(from, to)
            .stream()
            .map(ProductSalesServiceImpl::toItem)
            .sorted(BY_PROFIT_DESC)
            .toList();

        return new ProductSalesReport(startDate, endDate, products);
    }

    private static ProductSalesItem toItem(ProductSalesRow row) {
        return new ProductSalesItem(
            row.productId(),
            row.barcode(),
            row.name(),
            row.quantitySold(),
            row.totalRevenue(),
            row.totalCost(),
            row.totalRevenue().subtract(row.totalCost())
        );
    }
}