// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: HTTP service for the sales report with date query params
// Human Contributions: Endpoint path and parameters from Story #29
// Notes: Calls GET /api/v1/reports/sales; the JWT is added by authInterceptor.
// authors: Krizma Nagi

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../../../shared/models/api-response.model';
import { SalesReport } from '../models/sales-report.model';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Angular service that fetches the sales report for a start/end date"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Relies on the shared authInterceptor for the Bearer token (no manual header)
// - Follows the HttpParams pattern used by ProductService
// - Uses the shared ApiResponse model (shared/models) added in #102
// Verification:
// - sales-report.service.spec.ts
// Confidence: High
@Injectable({
  providedIn: 'root',
})
export class SalesReportService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/v1/reports/sales';

  /** Dates are inclusive and in YYYY-MM-DD format. */
  getSalesReport(startDate: string, endDate: string): Observable<SalesReport> {
    const params = new HttpParams().set('startDate', startDate).set('endDate', endDate);
    return this.http
      .get<ApiResponse<SalesReport>>(this.API_URL, { params })
      .pipe(
        map((response) => {
          if (!response.data) {
            throw new Error(response.message || 'Sales report response had no data');
          }
          return response.data;
        }),
      );
  }
}