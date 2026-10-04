// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Service interface
// Human Contributions: Story #57 scope
// Notes: Service contract for the core inventory report.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.report.service;

import edu.bu.metcs673.bluejay.report.dto.InventoryReportItem;

import java.util.List;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Service interface for the inventory report"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - None
// Verification:
//   - InventoryReportServiceImplTest
// Confidence: High
public interface InventoryReportService {

    List<InventoryReportItem> getInventoryReport();
}