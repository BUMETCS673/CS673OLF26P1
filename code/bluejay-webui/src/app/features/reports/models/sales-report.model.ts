// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Typed model for the sales report API response
// Human Contributions: Fields from Story #29 (total revenue, cost, net profit)
// Notes: Matches the SalesReport record returned by GET /api/v1/reports/sales.
// authors: Krizma Nagi

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "TypeScript model for the sales report response"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Dates are ISO strings (YYYY-MM-DD), the format the API sends and accepts
// Verification:
// - sales-report.service.spec.ts
// Confidence: High
export interface SalesReport {
  startDate: string;
  endDate: string;
  totalRevenue: number;
  totalCost: number;
  netProfit: number;
  transactionCount: number;
  unitsSold: number;
}