// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: TypeScript interfaces for API request, response, and recent entries table state.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create TypeScript interfaces matching backend StockEntryRequest, StockEntryResponse, and ApiResponse wrapper."
// AI Contribution: Initial draft (~100%)
// Modifications: Configured generic ApiResponse contract to align with Spring Boot backend wrapper.
// Verification: Angular compiler type checking.
// Confidence: High
export interface StockEntryRequest {
  barcode: string;
  quantity: number;
  cost?: number | null;
}

export interface StockEntryResponse {
  movementId: number;
  productId: string;
  barcode: string;
  productName: string;
  quantityAdded: number;
  newTotalStock: number;
  costPrice: number;
  userId: string;
  createdAt: string;
  message: string;
}

export interface RecentStockEntryItem {
  barcode: string;
  productName: string;
  cost: number;
  quantity: number;
}
