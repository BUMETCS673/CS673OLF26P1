// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: Signal state assertions, DecimalPipe verification, and HTTP error response handling
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError} from 'rxjs';
import {InventoryListComponent} from './inventory-list.component';
import {InventoryService} from '../../services/inventory.service';
import {ApiResponse} from '../../../../shared/models/api-response.model';
import {InventoryItem} from '../../models/inventory.model';

describe('InventoryListComponent', () => {
  let component: InventoryListComponent;
  let fixture: ComponentFixture<InventoryListComponent>;
  let inventoryServiceMock: { getStockInventory: { mockReturnValue: (arg0: Observable<ApiResponse<InventoryItem[]>>) => void; } };

  beforeEach(async () => {
    inventoryServiceMock = {
      getStockInventory: vi.fn()
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

  it('should create and load stock inventory on init', () => {
    const mockList: InventoryItem[] = [
      { id: '1', barcode: '1001', productName: 'Tea', cost: 5.5, quantity: 100 }
    ];
    const mockResponse: ApiResponse<InventoryItem[]> = {
      success: true,
      message: 'Inventory retrieved successfully',
      data: mockList,
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };
    inventoryServiceMock.getStockInventory.mockReturnValue(of(mockResponse));

    fixture.detectChanges(); // triggers ngOnInit

    expect(inventoryServiceMock.getStockInventory).toHaveBeenCalled();
    expect(component.inventoryItems()).toEqual(mockList);
    expect(component.isLoading()).toBe(false);
  });

  it('should handle service error gracefully during loadData', () => {
    inventoryServiceMock.getStockInventory.mockReturnValue(throwError(() => new Error('Failed to fetch')));

    component.loadData();

    expect(component.isLoading()).toBe(false);
    expect(component.inventoryItems()).toEqual([]);
  });
});
