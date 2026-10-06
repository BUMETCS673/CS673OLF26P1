// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~80%
// AI-Assisted Areas: TypeScript interfaces for products, categories, filtered
// product queries and API responses
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

export interface ProductQuery {
  productName?: string | null;
  categoryName?: string | null;
  pageNumber?: number;
  pageSize?: number;
}
