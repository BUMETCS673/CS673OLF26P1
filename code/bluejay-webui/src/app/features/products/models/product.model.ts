// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~75%
// AI-Assisted Areas: TypeScript interfaces for products, categories and API responses
// Human Contributions: Matched the fields to the backend product API contract
// Notes: Types only; no runtime logic.
// authors: Kimleng

export interface Product {
  id: string;
  name: string;
  barcode: string;
  categoryId: number | null;
  categoryName: string | null;
  categoryDescription?: string | null;
  price: number;
}

export interface CreateProduct {
  name: string;
  barcode: string;
  categoryId?: number | null;
  categoryName?: string | null;
  categoryDescription?: string | null;
}

export interface ProductCategory {
  id: number;
  name: string;
  description: string | null;
}
