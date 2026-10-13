// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~80%
// AI-Assisted Areas: HttpClient wrapper for create, filtered list, categories
// and find-by-barcode endpoints
// Human Contributions: Matched the endpoints, filter parameters and ApiResponse
// envelope to the backend product API, and reviewed the mapping
// Notes: Unwraps the ApiResponse envelope and URL-encodes the barcode.
// authors: Kimleng

import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';
import { CreateProduct, Product, ProductCategory, ProductPage, ProductQuery } from '../models/product.model';
import { ApiResponse } from '../../../shared/models/api-response.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/v1/product';

  create(product: CreateProduct): Observable<Product> {
    // @ts-ignore
    return this.http.post<ApiResponse<Product>>(this.endpoint, product).pipe(map((response) => response.data));
  }

  list(query: ProductQuery = {}): Observable<ProductPage> {
    let params = new HttpParams()
      .set('pageNumber', query.pageNumber ?? 1)
      .set('pageSize', query.pageSize ?? 10);

    if (query.searchTerm?.trim()) {
      params = params.set('searchTerm', query.searchTerm.trim());
    }

    if (query.productName?.trim()) {
      params = params.set('productName', query.productName.trim());
    }

    if (query.categoryName?.trim()) {
      params = params.set('categoryName', query.categoryName.trim());
    }
    // @ts-ignore
    return this.http.get<ApiResponse<ProductPage>>(this.endpoint, { params }).pipe(map((response) => response.data));
  }

  listCategories(): Observable<ProductCategory[]> {
    // @ts-ignore
    return this.http.get<ApiResponse<ProductCategory[]>>(`${this.endpoint}/categories`).pipe(map((response) => response.data));
  }

  findByBarcode(barcode: string): Observable<Product> {
    // @ts-ignore
    return this.http.get<ApiResponse<Product>>(`${this.endpoint}/${encodeURIComponent(barcode)}`).pipe(map((response) => response.data));
  }

  // AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Add updateLowStock method to ProductService calling PATCH /api/v1/product/{id}/low-stock."
// AI Contribution: Initial draft (~90%)
// Modifications: Added non-null assertion to response.data! to satisfy strict null checking.
// Verification: Browser network tab inspection.
// Confidence: High
  updateLowStock(id: string, lowStock: number): Observable<Product> {
    return this.http
      .patch<ApiResponse<Product>>(`${this.endpoint}/${id}/low-stock`, { lowStock })
      .pipe(map((response) => response.data!));
  }
}
