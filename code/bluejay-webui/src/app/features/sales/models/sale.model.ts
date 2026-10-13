// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~90%
// AI-Assisted Areas: TypeScript interfaces for sale checkout request, line items, and receipt response.
// Human Contributions: Aligned with Spring Boot backend DTO schemas and ApiResponse wrappers.
// Confidence: High

export interface SaleLineRequest {
  productId: string;
  quantity: number;
}

export interface SaleRequest {
  items: SaleLineRequest[];
  cashReceived: number;
}

export interface ChangeDenomination {
  valueCents: number;
  label: string;
  count: number;
}

export interface SaleLineResponse {
  productId: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface SaleResponse {
  transactionId: string;
  receiptNumber: string;
  transactionDate: string;
  grandTotal: number;
  cashReceived: number;
  changeDue: number;
  paymentMethod: string;
  items: SaleLineResponse[];
  changeBreakdown: ChangeDenomination[];
}
