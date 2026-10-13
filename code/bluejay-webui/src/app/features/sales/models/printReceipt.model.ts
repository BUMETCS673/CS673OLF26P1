export interface PrintReceiptRequest {
  transactionId: string;
}

export interface PrintReceiptResponse {
  transactionId: string;
  receiptNumber: string;
  transactionDate: string;
  grandTotal: string;
  paymentMethod: string;
  items: string[];
}
