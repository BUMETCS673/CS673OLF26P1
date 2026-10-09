// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Typed models for the product sales analysis API response
// Human Contributions: Fields from Story #30 (quantity sold, total cost, profit per product)
// Notes: Matches ProductSalesReport / ProductSalesItem from GET /api/v1/reports/sales/products.
// authors: Krizma Nagi

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "TypeScript models for the per-product sales response"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Dates are ISO strings (YYYY-MM-DD), the format the API sends and accepts
// Verification:
// - product-sales.service.spec.ts
// Confidence: High
export interface ProductSalesItem {
  productId: string;
  barcode: string | null;
  name: string;
  quantitySold: number;
  totalRevenue: number;
  totalCost: number;
  profit: number;
}

export interface ProductSalesReport {
  startDate: string;
  endDate: string;
  products: ProductSalesItem[];
}