// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: Standalone parent container integration testing and sub-component rendering assertion
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of} from 'rxjs';
import {InventoryPage} from './inventory-page.component';
import {InventoryService} from '../services/inventory.service';
import {
  InventoryHealthComponent
} from '../components/inventory-health/inventory-health.component';
import {
  InventoryListComponent
} from '../components/inventory-list/inventory-list.component';
import {
  StockEntryFormComponent
} from '../components/stock-entry-form/stock-entry-form.component';
import {ApiResponse} from '../../../shared/models/api-response.model';
import {
  InventoryHealthItem,
  InventoryItem,
  StockEntry
} from '../models/inventory.model';

describe('InventoryPage', () => {
  let component: InventoryPage;
  let fixture: ComponentFixture<InventoryPage>;
  let inventoryServiceMock: {
    getInventoryHealth: { mockReturnValue: (arg0: Observable<ApiResponse<InventoryHealthItem[]>>) => void; };
    getStockInventory: { mockReturnValue: (arg0: Observable<ApiResponse<InventoryItem[]>>) => void; };
    recordStockEntry: { mockReturnValue: (arg0: Observable<ApiResponse<StockEntry>>) => void; };
  };

  beforeEach(async () => {
    inventoryServiceMock = {
      getInventoryHealth: vi.fn(),
      getStockInventory: vi.fn(),
      recordStockEntry: vi.fn()
    };

    const healthResponse: ApiResponse<InventoryHealthItem[]> = {
      success: true,
      message: 'Health loaded',
      data: [],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    const stockResponse: ApiResponse<InventoryItem[]> = {
      success: true,
      message: 'Stock loaded',
      data: [],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    inventoryServiceMock.getInventoryHealth.mockReturnValue(of(healthResponse));
    inventoryServiceMock.getStockInventory.mockReturnValue(of(stockResponse));

    await TestBed.configureTestingModule({
      imports: [
        InventoryPage,
        InventoryHealthComponent,
        InventoryListComponent,
        StockEntryFormComponent
      ],
      providers: [
        { provide: InventoryService, useValue: inventoryServiceMock }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create page container component', () => {
    expect(component).toBeTruthy();
  });

  it('should render all child components', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-inventory-health')).toBeTruthy();
    expect(compiled.querySelector('app-inventory-list')).toBeTruthy();
    expect(compiled.querySelector('app-stock-entry-form')).toBeTruthy();
  });
});
