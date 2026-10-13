// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~80%
// AI-Assisted Areas: HttpTestingController setup and checkout request/response mapping assertions.
// Human Contributions: Aligned endpoints and payload structure with backend SaleController.
// Confidence: High

import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { SaleRequest, SaleResponse } from '../models/sale.model';
import { SaleService } from './sale.service';

describe('SaleService', () => {
  let service: SaleService;
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(SaleService);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('submits checkout request and unwraps the response data', () => {
    const requestPayload: SaleRequest = {
      items: [{ productId: 'prod-1', quantity: 2 }],
      cashReceived: 20.00,
    };

    const mockResponse: SaleResponse = {
      transactionId: 'txn-123',
      receiptNumber: 'BJ-TEST-001',
      transactionDate: '2026-10-12T00:00:00Z',
      grandTotal: 15.50,
      cashReceived: 20.00,
      changeDue: 4.50,
      paymentMethod: 'CASH',
      items: [
        {
          productId: 'prod-1',
          productName: 'Sample Product',
          quantity: 2,
          unitPrice: 7.75,
          subtotal: 15.50,
        },
      ],
      changeBreakdown: [
        { valueCents: 100, label: '$1 Bill', count: 4 },
        { valueCents: 25, label: 'Quarter', count: 2 },
      ],
    };

    let result: SaleResponse | undefined;
    service.checkout(requestPayload).subscribe((res) => (result = res));

    const request = controller.expectOne('/api/v1/sales');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(requestPayload);

    request.flush({
      success: true,
      message: 'Sale transaction completed successfully',
      data: mockResponse,
      errorCode: null,
      timestamp: '2026-10-12T00:00:00Z',
    });

    expect(result).toEqual(mockResponse);
  });
});
