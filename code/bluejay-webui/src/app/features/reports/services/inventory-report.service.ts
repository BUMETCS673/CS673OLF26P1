// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: HTTP service for the inventory report, response typing
// Human Contributions: Endpoint path and fields from Story #57
// Notes: Calls GET /api/v1/reports/inventory with the stored JWT.
// authors: Krizma Nagi

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';

export interface InventoryReportItem {
  productId: string;
  barcode: string | null;
  name: string;
  latestCost: number;
  onHandQuantity: number;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode: string | null;
  timestamp: string;
}

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Angular service that fetches the inventory report and unwraps ApiResponse"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Adds the Bearer header manually, matching AuthService.logout(), since the app has no HTTP interceptor yet
// - Returns an empty array if the API sends no data
// Verification:
// - inventory-report.service.spec.ts
// Confidence: High
@Injectable({
  providedIn: 'root',
})
export class InventoryReportService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private readonly API_URL = '/api/v1/reports/inventory';

  getInventoryReport(): Observable<InventoryReportItem[]> {
    const token = this.auth.getToken();
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : new HttpHeaders();

    return this.http
      .get<ApiResponse<InventoryReportItem[]>>(this.API_URL, { headers })
      .pipe(map((response) => response?.data ?? []));
  }
}