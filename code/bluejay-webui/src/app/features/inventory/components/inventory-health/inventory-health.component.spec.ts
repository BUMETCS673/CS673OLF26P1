// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: Signal state verification, status formatting logic, and async service mocking
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError} from 'rxjs';
import {InventoryHealthComponent} from './inventory-health.component';
import {InventoryService} from '../../services/inventory.service';
import {ApiResponse} from '../../../../shared/models/api-response.model';
import {InventoryHealthItem} from '../../models/inventory.model';

describe('InventoryHealthComponent', () => {
  let component: InventoryHealthComponent;
  let fixture: ComponentFixture<InventoryHealthComponent>;
  let inventoryServiceMock: { getInventoryHealth: { mockReturnValue: (arg0: Observable<ApiResponse<InventoryHealthItem[]>>) => void; } };

  beforeEach(async () => {
    inventoryServiceMock = {
      getInventoryHealth: vi.fn()
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

  it('should create component and load health data on init', () => {
    const mockItems: InventoryHealthItem[] = [
      { id: '1', productName: 'Coffee', percentage: 10, status: 'Critical' }
    ];
    const mockResponse: ApiResponse<InventoryHealthItem[]> = {
      success: true,
      message: 'Health metrics retrieved',
      data: mockItems,
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };
    inventoryServiceMock.getInventoryHealth.mockReturnValue(of(mockResponse));

    fixture.detectChanges(); // triggers ngOnInit

    expect(inventoryServiceMock.getInventoryHealth).toHaveBeenCalled();
    expect(component.healthItems()).toEqual(mockItems);
    expect(component.isLoading()).toBe(false);
  });

  it('should handle service error gracefully during loadData', () => {
    inventoryServiceMock.getInventoryHealth.mockReturnValue(throwError(() => new Error('Service failure')));

    component.loadData();

    expect(component.isLoading()).toBe(false);
    expect(component.healthItems()).toEqual([]);
  });

  it('should format status string to lowercase for CSS class mapping', () => {
    expect(component.getStatusClass('Healthy')).toBe('healthy');
    expect(component.getStatusClass('Low')).toBe('low');
    expect(component.getStatusClass('Critical')).toBe('critical');
  });
});
