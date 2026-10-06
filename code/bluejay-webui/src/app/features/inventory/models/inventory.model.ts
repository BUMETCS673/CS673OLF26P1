// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: TypeScript interfaces for API request, response, and recent entries table state.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create TypeScript interfaces matching backend CreateStockEntry, StockEntry, and InventoryItem wrapper."
// AI Contribution: Initial draft (~100%)
// Verification: Angular compiler type checking.
// Confidence: High
export interface CreateStockEntry {
  barcode: string;
  quantity: number;
  cost?: number | null;
}

export interface StockEntry {
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

export interface InventoryItem {
  id: string;
  barcode: string;
  productName: string;
  cost: number;
  quantity: number;
}

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create InventoryHealthItem interface for dynamic product stock level rendering. Define status types and percentage attributes."
// AI Contribution: Initial draft (~100%)
// Modifications: Added explicit union type for health status levels.
// Verification: Angular compiler type check.
// Confidence: High
export interface InventoryHealthItem {
  id: string;
  productName: string;
  percentage: number;
  status: 'Healthy' | 'Low' | 'Critical';
}
