// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: REST endpoint with ISO date parameters, Admin-only role guard
// Human Contributions: Route and allowed role from Story #29
// Notes: GET /api/v1/reports/sales?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
//        (the /api/v1 prefix comes from WebConfig).
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.controller;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.report.dto.SalesReport;
import edu.bu.metcs673.bluejay.report.service.SalesReportService;
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
// Prompt Summary: "Implement backend endpoint accepting start/end date params, restricted to Admin"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Separate controller from ReportController so the inventory report and
//     its tests are unchanged
//   - Admin only (Manager and Cashier get 403), per sub-issue #41
//   - Missing or malformed dates return 400 via GlobalExceptionHandler
// Verification:
//   - SalesReportControllerTest
// Confidence: High
@RestController
@RequestMapping("/reports")
public class SalesReportController {

    private final SalesReportService salesReportService;

    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SalesReport>> getSalesReport(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        SalesReport report =
            salesReportService.getSalesReport(startDate, endDate);
        return ResponseEntity.ok(
            ApiResponse.success(report, "Sales report generated")
        );
    }
}