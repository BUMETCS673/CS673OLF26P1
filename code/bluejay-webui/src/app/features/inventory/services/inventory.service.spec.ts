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
import { CreateStockEntry, StockEntry, InventoryItem, InventoryStockItem } from '../models/inventory.model';

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
    const request: CreateStockEntry = { barcode: '1001', quantity: 5, stockCost: 12.5, salePrice: 15.0 };
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
        stockCost: 12.5,
        salePrice: 15.0,
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

  it('should send GET request to fetch inventory list', () => {
    const mockResponse: ApiResponse<InventoryItem[]> = {
      success: true,
      message: 'Inventory items retrieved successfully',
      data: [
        { id: '1', barcode: '1001', productName: 'Coffee', stockCost: 10, salePrice: 15, quantity: 50 }
      ],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    service.getInventory().subscribe((res) => {
      expect(res.success).toBe(true);
      expect(res.data?.length).toBe(1);
      expect(res.data?.[0].productName).toBe('Coffee');
    });

    const req = httpMock.expectOne('/api/v1/inventory');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should send GET request to fetch inventory stock', () => {
    const mockResponse: ApiResponse<InventoryStockItem[]> = {
      success: true,
      message: 'Inventory stock retrieved successfully',
      data: [
        { id: '1', productName: 'Coffee', currentStock: 50, lowStockThreshold: 10 }
      ],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    service.getInventoryStock().subscribe((res) => {
      expect(res.success).toBe(true);
      expect(res.data?.[0].productName).toBe('Coffee');
    });

    const req = httpMock.expectOne('/api/v1/inventory/stock');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should send PATCH request to update sale price', () => {
    const mockResponse: ApiResponse<InventoryItem> = {
      success: true,
      message: 'Sale price updated successfully.',
      data: { id: '1', barcode: '1001', productName: 'Coffee', stockCost: 10, salePrice: 18.5, quantity: 50 },
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    service.updateSalePrice('1', 18.5).subscribe((res) => {
      expect(res.success).toBe(true);
      expect(res.data?.salePrice).toBe(18.5);
    });

    const req = httpMock.expectOne('/api/v1/inventory/1/price');
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ salePrice: 18.5 });
    req.flush(mockResponse);
  });
});
