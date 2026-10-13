// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: HttpTestingController setup and assertions
// Human Contributions: Story #29 scenarios (range with sales, empty range)
// Notes: Unit tests for SalesReportService.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SalesReportService } from './sales-report.service';
import { SalesReport } from '../models/sales-report.model';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Unit tests for the sales report service"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Checks the startDate/endDate query params are sent unchanged
// Verification:
// - npm test
// Confidence: High
describe('SalesReportService', () => {
  let service: SalesReportService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SalesReportService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('requests the report for the given dates and returns the totals', () => {
    const report: SalesReport = {
      startDate: '2026-10-01',
      endDate: '2026-10-31',
      totalRevenue: 62.48,
      totalCost: 40.48,
      netProfit: 22,
      transactionCount: 3,
      unitsSold: 7,
    };
    let result: SalesReport | undefined;

    service.getSalesReport('2026-10-01', '2026-10-31').subscribe((r) => (result = r));

    const req = httpMock.expectOne(
      (r) => r.url === '/api/v1/reports/sales',
    );
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('startDate')).toBe('2026-10-01');
    expect(req.request.params.get('endDate')).toBe('2026-10-31');
    req.flush({ success: true, message: 'ok', data: report, errorCode: null, timestamp: '' });

    expect(result).toEqual(report);
  });

  it('returns zero totals for an empty range', () => {
    const empty: SalesReport = {
      startDate: '2026-11-01',
      endDate: '2026-11-30',
      totalRevenue: 0,
      totalCost: 0,
      netProfit: 0,
      transactionCount: 0,
      unitsSold: 0,
    };
    let result: SalesReport | undefined;

    service.getSalesReport('2026-11-01', '2026-11-30').subscribe((r) => (result = r));

    httpMock
      .expectOne((r) => r.url === '/api/v1/reports/sales')
      .flush({ success: true, message: 'ok', data: empty, errorCode: null, timestamp: '' });

    expect(result?.transactionCount).toBe(0);
    expect(result?.netProfit).toBe(0);
  });
});