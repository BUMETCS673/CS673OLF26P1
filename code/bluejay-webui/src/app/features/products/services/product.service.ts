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
import { ApiResponse, CreateProduct, Product, ProductCategory, ProductQuery } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/v1/product';

  create(product: CreateProduct): Observable<Product> {
    return this.http.post<ApiResponse<Product>>(this.endpoint, product).pipe(map((response) => response.data));
  }

  list(query: ProductQuery = {}): Observable<Product[]> {
    let params = new HttpParams()
      .set('pageNumber', query.pageNumber ?? 1)
      .set('pageSize', query.pageSize ?? 10);

    if (query.productName?.trim()) {
      params = params.set('productName', query.productName.trim());
    }

    if (query.categoryName?.trim()) {
      params = params.set('categoryName', query.categoryName.trim());
    }

    return this.http.get<ApiResponse<Product[]>>(this.endpoint, { params }).pipe(map((response) => response.data));
  }

  listCategories(): Observable<ProductCategory[]> {
    return this.http.get<ApiResponse<ProductCategory[]>>(`${this.endpoint}/categories`).pipe(map((response) => response.data));
  }

  findByBarcode(barcode: string): Observable<Product> {
    return this.http.get<ApiResponse<Product>>(`${this.endpoint}/${encodeURIComponent(barcode)}`).pipe(map((response) => response.data));
  }
}
