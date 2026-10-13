// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: HTTP service for the per-product sales analysis
// Human Contributions: Endpoint path and parameters from Story #30
// Notes: Calls GET /api/v1/reports/sales/products; the JWT is added by authInterceptor.
// authors: Krizma Nagi

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../shared/models/api-response.model';
import { ProductSalesReport } from '../models/product-sales.model';

@Injectable({
  providedIn: 'root',
})
export class ProductSalesService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/reports/sales/products';

  /** Dates are inclusive and in YYYY-MM-DD format. */
  getProductSales(startDate: string, endDate: string): Observable<ProductSalesReport> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http.get<ApiResponse<ProductSalesReport>>(this.apiUrl, { params }).pipe(
      map((response) => {
        if (!response.data) {
          throw new Error(response.message || 'Product sales response had no data');
        }
        return response.data;
      }),
    );
  }
}
