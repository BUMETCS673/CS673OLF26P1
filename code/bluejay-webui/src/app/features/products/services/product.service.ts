// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~75%
// AI-Assisted Areas: HttpClient wrapper for create, list, categories and find-by-barcode endpoints
// Human Contributions: Matched the endpoints, pagination parameters and ApiResponse envelope to the backend product API, and reviewed the mapping
// Notes: Unwraps the ApiResponse envelope and URL-encodes the barcode.
// authors: Kimleng

import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/models/api-response.model';
import { CreateProduct, Product, ProductCategory } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/v1/product';

  create(product: CreateProduct): Observable<Product> {
    // @ts-ignore
    return this.http.post<ApiResponse<Product>>(this.endpoint, product).pipe(map((response) => response.data));
  }

  list(pageNumber = 1, pageSize = 10): Observable<Product[]> {
    const params = new HttpParams().set('pageNumber', pageNumber).set('pageSize', pageSize);
    // @ts-ignore
    return this.http.get<ApiResponse<Product[]>>(this.endpoint, { params }).pipe(map((response) => response.data));
  }

  listCategories(): Observable<ProductCategory[]> {
    // @ts-ignore
    return this.http.get<ApiResponse<ProductCategory[]>>(`${this.endpoint}/categories`).pipe(map((response) => response.data));
  }

  findByBarcode(barcode: string): Observable<Product> {
    // @ts-ignore
    return this.http.get<ApiResponse<Product>>(`${this.endpoint}/${encodeURIComponent(barcode)}`).pipe(map((response) => response.data));
  }
}
