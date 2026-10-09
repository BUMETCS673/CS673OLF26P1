// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: HttpTestingController setup and assertions
// Human Contributions: Story #30 scenarios (multiple products, empty catalog)
// Notes: Unit tests for ProductSalesService.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductSalesService } from './product-sales.service';
import { ProductSalesReport } from '../models/product-sales.model';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Unit tests for the product sales service"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Checks the startDate/endDate query params are sent unchanged
// Verification:
// - npm test
// Confidence: High
describe('ProductSalesService', () => {
  let service: ProductSalesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ProductSalesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('requests product sales for the given dates and returns the rows', () => {
    const report: ProductSalesReport = {
      startDate: '2026-10-01',
      endDate: '2026-10-31',
      products: [
        { productId: 'p1', barcode: '111', name: 'Cooking Oil 2L', quantitySold: 5, totalRevenue: 32.5, totalCost: 20.5, profit: 12 },
        { productId: 'p2', barcode: null, name: 'Lemons', quantitySold: 0, totalRevenue: 0, totalCost: 0, profit: 0 },
      ],
    };
    let result: ProductSalesReport | undefined;

    service.getProductSales('2026-10-01', '2026-10-31').subscribe((r) => (result = r));

    const req = httpMock.expectOne((r) => r.url === '/api/v1/reports/sales/products');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('startDate')).toBe('2026-10-01');
    expect(req.request.params.get('endDate')).toBe('2026-10-31');
    req.flush({ success: true, message: 'ok', data: report, errorCode: null, timestamp: '' });

    expect(result).toEqual(report);
  });

  it('returns an empty product list for an empty catalog', () => {
    let result: ProductSalesReport | undefined;

    service.getProductSales('2026-10-01', '2026-10-31').subscribe((r) => (result = r));

    httpMock
      .expectOne((r) => r.url === '/api/v1/reports/sales/products')
      .flush({
        success: true,
        message: 'ok',
        data: { startDate: '2026-10-01', endDate: '2026-10-31', products: [] },
        errorCode: null,
        timestamp: '',
      });

    expect(result?.products).toEqual([]);
  });
});