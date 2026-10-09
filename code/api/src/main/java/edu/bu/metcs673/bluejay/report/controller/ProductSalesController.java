// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: REST endpoint with ISO date parameters, Admin-only role guard
// Human Contributions: Route and allowed role from Story #30
// Notes: GET /api/v1/reports/sales/products?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
//        (the /api/v1 prefix comes from WebConfig).
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.controller;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.report.dto.ProductSalesReport;
import edu.bu.metcs673.bluejay.report.service.ProductSalesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Implement backend endpoint for per-product sales, restricted to Admin"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Separate controller so the Story #29 SalesReportController is unchanged
//   - Admin only (Manager and Cashier get 403), same as Story #29
//   - Missing or malformed dates return 400 via GlobalExceptionHandler
// Verification:
//   - ProductSalesControllerTest
// Confidence: High
@RestController
@RequestMapping("/reports")
public class ProductSalesController {

    private final ProductSalesService productSalesService;

    public ProductSalesController(ProductSalesService productSalesService) {
        this.productSalesService = productSalesService;
    }

    @GetMapping("/sales/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductSalesReport>> getProductSales(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ProductSalesReport report =
            productSalesService.getProductSales(startDate, endDate);
        return ResponseEntity.ok(
            ApiResponse.success(report, "Product sales report generated")
        );
    }
}