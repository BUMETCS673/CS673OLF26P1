// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: HttpTestingController setup and Angular HttpClient method testing
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { InventoryService } from './inventory.service';
import { ApiResponse } from '../../../shared/models/api-response.model';
import { CreateStockEntry, StockEntry, InventoryItem, InventoryHealthItem } from '../models/inventory.model';

describe('InventoryService', () => {
  let service: InventoryService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        InventoryService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(InventoryService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should send POST request to record stock entry', () => {
    const request: CreateStockEntry = { barcode: '1001', quantity: 5, cost: 12.5 };
    const mockResponse: ApiResponse<StockEntry> = {
      success: true,
      message: 'Stock entry recorded',
      data: {
        movementId: 1,
        productId: 'prod-1',
        barcode: '1001',
        productName: 'Coffee',
        quantityAdded: 5,
        newTotalStock: 55,
        costPrice: 12.5,
        userId: 'user-1',
        createdAt: '2026-10-05T12:00:00Z',
        message: 'Stock entry recorded'
      },
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    service.recordStockEntry(request).subscribe((res) => {
      expect(res.success).toBe(true);
      expect(res.data?.barcode).toBe('1001');
    });

    const req = httpMock.expectOne('/api/v1/inventory/stock-entry');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });

  it('should send GET request to fetch stock inventory', () => {
    const mockResponse: ApiResponse<InventoryItem[]> = {
      success: true,
      message: 'Inventory items retrieved successfully',
      data: [
        { id: '1', barcode: '1001', productName: 'Coffee', cost: 10, quantity: 50 }
      ],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    service.getStockInventory().subscribe((res) => {
      expect(res.success).toBe(true);
      expect(res.data?.length).toBe(1);
      expect(res.data?.[0].productName).toBe('Coffee');
    });

    const req = httpMock.expectOne('/api/v1/inventory');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should send GET request to fetch inventory health', () => {
    const mockResponse: ApiResponse<InventoryHealthItem[]> = {
      success: true,
      message: 'Inventory health retrieved successfully',
      data: [
        { id: '1', productName: 'Coffee', percentage: 75, status: 'Healthy' }
      ],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    service.getInventoryHealth().subscribe((res) => {
      expect(res.success).toBe(true);
      expect(res.data?.[0].status).toBe('Healthy');
    });

    const req = httpMock.expectOne('/api/v1/inventory/health');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
