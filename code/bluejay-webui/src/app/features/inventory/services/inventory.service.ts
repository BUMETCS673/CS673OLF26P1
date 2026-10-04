// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Angular Injectable service using HttpClient for inventory stock entry API calls.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {ApiResponse} from '../../../shared/models/api-response.model';
import {
  StockEntryRequest,
  StockEntryResponse
} from '../models/stock-entry.model';


// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create Angular HTTP service to execute POST request for recording stock entries."
// AI Contribution: Initial draft (~100%)
// Modifications: Integrated Angular 17+ inject() function and generic ApiResponse typing.
// Verification: Angular unit test with HttpTestingController.
// Confidence: High
@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = '/api/v1/inventory/stock-entries';

  recordStockEntry(request: StockEntryRequest): Observable<ApiResponse<StockEntryResponse>> {
    return this.http.post<ApiResponse<StockEntryResponse>>(this.apiUrl, request);
  }
}
