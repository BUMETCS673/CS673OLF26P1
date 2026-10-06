// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Service implementation
// Human Contributions: Story #57 scope
// Notes: Thin service over the report repository; the place to add
//        filtering or low-stock flags later.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service.impl;

import edu.bu.metcs673.bluejay.report.dto.InventoryReportItem;
import edu.bu.metcs673.bluejay.report.repository.InventoryReportRepository;
import edu.bu.metcs673.bluejay.report.service.InventoryReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Implement the inventory report service"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Marked read-only transactional
//   - PR review (Sara): removed List.copyOf(); JdbcTemplate already
//     returns a new list, so the copy was an extra allocation
// Verification:
//   - InventoryReportServiceImplTest
// Confidence: High
@Service
public class InventoryReportServiceImpl implements InventoryReportService {

    private final InventoryReportRepository inventoryReportRepository;

    public InventoryReportServiceImpl(
        InventoryReportRepository inventoryReportRepository) {
        this.inventoryReportRepository = inventoryReportRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryReportItem> getInventoryReport() {
        return inventoryReportRepository.findInventoryReport();
    }
}