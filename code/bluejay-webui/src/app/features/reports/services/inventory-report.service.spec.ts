// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: HttpTestingController setup and assertions
// Human Contributions: Scenarios from Story #57 (products, empty catalog)
// Notes: Unit tests for InventoryReportService.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { InventoryReportItem, InventoryReportService } from './inventory-report.service';
import { AuthService } from '../../../core/auth/auth.service';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Unit tests for the inventory report service"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Stubbed AuthService.getToken() instead of touching localStorage
// Verification:
// - npm test
// Confidence: High
describe('InventoryReportService', () => {
  let service: InventoryReportService;
  let httpMock: HttpTestingController;
  let token: string | null;

  beforeEach(() => {
    token = 'test.jwt.token';
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: { getToken: () => token } },
      ],
    });
    service = TestBed.inject(InventoryReportService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('returns each product with latest cost and on-hand quantity', () => {
    const rows: InventoryReportItem[] = [
      { productId: 'p-1', barcode: '111', name: 'Cooking Oil 2L', latestCost: 4.25, onHandQuantity: 34 },
      { productId: 'p-2', barcode: null, name: 'Premium Rice 5kg', latestCost: 9.99, onHandQuantity: 72 },
    ];
    let result: InventoryReportItem[] | undefined;

    service.getInventoryReport().subscribe((items) => (result = items));

    const req = httpMock.expectOne('/api/v1/reports/inventory');
    expect(req.request.method).toBe('GET');
    expect(req.request.headers.get('Authorization')).toBe('Bearer test.jwt.token');
    req.flush({ success: true, message: 'ok', data: rows, errorCode: null, timestamp: '' });

    expect(result).toEqual(rows);
  });

  it('returns an empty list for an empty catalog', () => {
    let result: InventoryReportItem[] | undefined;

    service.getInventoryReport().subscribe((items) => (result = items));

    httpMock
      .expectOne('/api/v1/reports/inventory')
      .flush({ success: true, message: 'ok', data: [], errorCode: null, timestamp: '' });

    expect(result).toEqual([]);
  });

  it('omits the Authorization header when not logged in', () => {
    token = null;

    service.getInventoryReport().subscribe();

    const req = httpMock.expectOne('/api/v1/reports/inventory');
    expect(req.request.headers.has('Authorization')).toBe(false);
    req.flush({ success: true, message: 'ok', data: [], errorCode: null, timestamp: '' });
  });
});