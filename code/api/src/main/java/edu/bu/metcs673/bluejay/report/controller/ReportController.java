// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: REST endpoint, role guard, ApiResponse wrapping
// Human Contributions: Route and allowed roles from Story #57
// Notes: GET /api/v1/reports/inventory (the /api/v1 prefix comes from WebConfig).
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.controller;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.report.dto.InventoryReportItem;
import edu.bu.metcs673.bluejay.report.service.InventoryReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Endpoint GET /reports/inventory with Admin/Stock Manager role guard"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - "Stock Manager" maps to ROLE_MANAGER in the seeded roles table
//   - Cashier and any other role get 403 via GlobalExceptionHandler
// Verification:
//   - ReportControllerTest (manager, admin, cashier, anonymous, empty catalog)
// Confidence: High
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final InventoryReportService inventoryReportService;

    public ReportController(InventoryReportService inventoryReportService) {
        this.inventoryReportService = inventoryReportService;
    }

    @GetMapping("/inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<InventoryReportItem>>>
        getInventoryReport() {
        List<InventoryReportItem> items =
            inventoryReportService.getInventoryReport();
        return ResponseEntity.ok(
            ApiResponse.success(items, "Inventory report generated")
        );
    }
}