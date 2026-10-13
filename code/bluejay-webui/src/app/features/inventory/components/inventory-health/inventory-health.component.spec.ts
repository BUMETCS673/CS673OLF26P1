// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: Signal state verification, status formatting logic, and async service mocking
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { InventoryHealthComponent } from './inventory-health.component';
import { InventoryService } from '../../services/inventory.service';
import { ApiResponse } from '../../../../shared/models/api-response.model';
import { InventoryStockItem } from '../../models/inventory.model';

describe('InventoryHealthComponent', () => {
  let component: InventoryHealthComponent;
  let fixture: ComponentFixture<InventoryHealthComponent>;
  let inventoryServiceMock: { getInventoryStock: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    inventoryServiceMock = {
      getInventoryStock: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [InventoryHealthComponent],
      providers: [
        { provide: InventoryService, useValue: inventoryServiceMock }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryHealthComponent);
    component = fixture.componentInstance;
  });

  it('should create component and load stock data on init', () => {
    const mockItems: InventoryStockItem[] = [
      { id: '1', productName: 'Coffee', currentStock: 5, lowStockThreshold: 10 }
    ];
    const mockResponse: ApiResponse<InventoryStockItem[]> = {
      success: true,
      message: 'Stock metrics retrieved',
      data: mockItems,
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };
    inventoryServiceMock.getInventoryStock.mockReturnValue(of(mockResponse));

    fixture.detectChanges(); // triggers ngOnInit

    expect(inventoryServiceMock.getInventoryStock).toHaveBeenCalled();
    expect(component.stockItems()).toEqual(mockItems);
    expect(component.isLoading()).toBe(false);
  });

  it('should handle service error gracefully during loadData', () => {
    inventoryServiceMock.getInventoryStock.mockReturnValue(throwError(() => new Error('Service failure')));

    component.loadData();

    expect(component.isLoading()).toBe(false);
    expect(component.stockItems()).toEqual([]);
  });

  it('should calculate stock health status correctly', () => {
    // Critical (<= 0 or <= 0.5 * lowThreshold)
    expect(component.calculateStockHealth(0, 10).status).toBe('Critical');
    expect(component.calculateStockHealth(4, 10).status).toBe('Critical');

    // Low (> 0.5 * lowThreshold and <= lowThreshold)
    expect(component.calculateStockHealth(8, 10).status).toBe('Low');

    // Healthy (> lowThreshold)
    expect(component.calculateStockHealth(25, 10).status).toBe('Healthy');
  });

  it('should format status string to lowercase for CSS class mapping', () => {
    expect(component.getStatusClass('Healthy')).toBe('healthy');
    expect(component.getStatusClass('Low')).toBe('low');
    expect(component.getStatusClass('Critical')).toBe('critical');
  });
});
