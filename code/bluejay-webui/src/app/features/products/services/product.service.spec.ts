// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~75%
// AI-Assisted Areas: HttpTestingController setup and request/response mapping assertions for each ProductService method
// Human Contributions: Defined the expected endpoints and query parameters from the product API contract, added the barcode encoding and error propagation cases, reviewed the assertions, and ran the suite with npm test
// Notes: Verifies that responses are unwrapped from the ApiResponse envelope.
// authors: Kimleng

import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    service = TestBed.inject(ProductService);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('creates a product and unwraps the response', () => {
    const payload = { name: 'Milk', barcode: '123' };
    let result: unknown;
    service.create(payload).subscribe((p) => (result = p));

    const request = controller.expectOne('/api/v1/product');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);
    request.flush({ data: { id: '1', ...payload } });
    expect(result).toEqual({ id: '1', ...payload });
  });

  it('lists products with default pagination', () => {
    let result: unknown;
    service.list().subscribe((p) => (result = p));

    const request = controller.expectOne((r) => r.url === '/api/v1/product');
    expect(request.request.method).toBe('GET');
    expect(request.request.params.get('pageNumber')).toBe('1');
    expect(request.request.params.get('pageSize')).toBe('10');
    request.flush({ data: [{ id: '1' }] });
    expect(result).toEqual([{ id: '1' }]);
  });

  it('lists products with the requested page and size', () => {
    service.list(3, 25).subscribe();

    const request = controller.expectOne((r) => r.url === '/api/v1/product');
    expect(request.request.params.get('pageNumber')).toBe('3');
    expect(request.request.params.get('pageSize')).toBe('25');
    request.flush({ data: [] });
  });

  it('lists categories', () => {
    let result: unknown;
    service.listCategories().subscribe((c) => (result = c));

    const request = controller.expectOne('/api/v1/product/categories');
    expect(request.request.method).toBe('GET');
    request.flush({ data: [{ id: 1, name: 'Dairy', description: null }] });
    expect(result).toEqual([{ id: 1, name: 'Dairy', description: null }]);
  });

  it('finds a product by barcode', () => {
    let result: unknown;
    service.findByBarcode('123').subscribe((p) => (result = p));

    const request = controller.expectOne('/api/v1/product/123');
    expect(request.request.method).toBe('GET');
    request.flush({ data: { id: '1', barcode: '123' } });
    expect(result).toEqual({ id: '1', barcode: '123' });
  });

  it('URL-encodes the barcode', () => {
    service.findByBarcode('a/b c').subscribe();

    controller.expectOne('/api/v1/product/a%2Fb%20c').flush({ data: {} });
  });

  it('propagates API errors such as 422 PRODUCT_PRICE_MISSING', () => {
    let status = 0;
    let code = '';
    service.findByBarcode('123').subscribe({
      error: (e) => {
        status = e.status;
        code = e.error.code;
      },
    });

    controller.expectOne('/api/v1/product/123')
      .flush({ code: 'PRODUCT_PRICE_MISSING', message: 'missing' }, { status: 422, statusText: 'Unprocessable Entity' });
    expect(status).toBe(422);
    expect(code).toBe('PRODUCT_PRICE_MISSING');
  });
});
