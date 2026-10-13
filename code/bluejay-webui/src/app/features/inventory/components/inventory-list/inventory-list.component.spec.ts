// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: Signal state assertions, DecimalPipe verification, and HTTP error response handling
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { InventoryListComponent } from './inventory-list.component';
import { InventoryService } from '../../services/inventory.service';
import { ApiResponse } from '../../../../shared/models/api-response.model';
import { InventoryItem } from '../../models/inventory.model';

describe('InventoryListComponent', () => {
  let component: InventoryListComponent;
  let fixture: ComponentFixture<InventoryListComponent>;
  let inventoryServiceMock: {
    getInventory: ReturnType<typeof vi.fn>;
    updateSalePrice: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    inventoryServiceMock = {
      getInventory: vi.fn(),
      updateSalePrice: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [InventoryListComponent],
      providers: [
        { provide: InventoryService, useValue: inventoryServiceMock }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryListComponent);
    component = fixture.componentInstance;
  });

  it('should create and load inventory on init', () => {
    const mockList: InventoryItem[] = [
      { id: '1', barcode: '1001', productName: 'Tea', stockCost: 5.5, salePrice: 8.0, quantity: 100 }
    ];
    const mockResponse: ApiResponse<InventoryItem[]> = {
      success: true,
      message: 'Inventory retrieved successfully',
      data: mockList,
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };
    inventoryServiceMock.getInventory.mockReturnValue(of(mockResponse));

    fixture.detectChanges(); // triggers ngOnInit

    expect(inventoryServiceMock.getInventory).toHaveBeenCalled();
    expect(component.inventoryItems()).toEqual(mockList);
    expect(component.isLoading()).toBe(false);
  });

  it('should handle service error gracefully during loadData', () => {
    inventoryServiceMock.getInventory.mockReturnValue(throwError(() => new Error('Failed to fetch')));

    component.loadData();

    expect(component.isLoading()).toBe(false);
    expect(component.inventoryItems()).toEqual([]);
  });

  it('should start inline editing and set initial edit price', () => {
    const item: InventoryItem = { id: '1', barcode: '1001', productName: 'Tea', stockCost: 5.5, salePrice: 8.0, quantity: 100 };

    component.startEdit(item);

    expect(component.editingItemId()).toBe('1');
    expect(component.editingSalePrice()).toBe(8.0);
  });

  it('should cancel inline editing', () => {
    component.editingItemId.set('1');

    component.cancelEdit();

    expect(component.editingItemId()).toBeNull();
  });

  it('should save updated sale price and update local signal state', () => {
    const item: InventoryItem = { id: '1', barcode: '1001', productName: 'Tea', stockCost: 5.5, salePrice: 8.0, quantity: 100 };
    component.inventoryItems.set([item]);
    component.editingItemId.set('1');
    component.editingSalePrice.set(10.0);

    const updateResponse: ApiResponse<InventoryItem> = {
      success: true,
      message: 'Sale price updated successfully.',
      data: { ...item, salePrice: 10.0 },
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    inventoryServiceMock.updateSalePrice.mockReturnValue(of(updateResponse));

    component.saveEdit(item);

    expect(inventoryServiceMock.updateSalePrice).toHaveBeenCalledWith('1', 10.0);
    expect(component.inventoryItems()[0].salePrice).toBe(10.0);
    expect(component.editingItemId()).toBeNull();
  });
});
