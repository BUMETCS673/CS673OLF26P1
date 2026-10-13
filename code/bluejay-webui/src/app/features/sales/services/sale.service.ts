// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~85%
// AI-Assisted Areas: HttpClient wrapper for sales checkout endpoint and ApiResponse mapping.
// Human Contributions: Aligned endpoint routes, DTO payload, and error handling with backend SaleController.
// Confidence: High

import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map, Observable } from 'rxjs';
import { ApiResponse } from '../../../shared/models/api-response.model';
import { SaleRequest, SaleResponse } from '../models/sale.model';

@Injectable({ providedIn: 'root' })
export class SaleService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/v1/sales';

  checkout(request: SaleRequest): Observable<SaleResponse> {
    // @ts-ignore
    return this.http.post<ApiResponse<SaleResponse>>(this.endpoint, request).pipe(
      map((response) => response.data as SaleResponse),
    );
  }
}
