// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: Standalone parent container integration testing and sub-component rendering assertion
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { InventoryPage } from './inventory-page.component';
import { InventoryService } from '../services/inventory.service';
import { InventoryHealthComponent } from '../components/inventory-health/inventory-health.component';
import { InventoryListComponent } from '../components/inventory-list/inventory-list.component';
import { StockEntryFormComponent } from '../components/stock-entry-form/stock-entry-form.component';
import { ApiResponse } from '../../../shared/models/api-response.model';
import { InventoryStockItem, InventoryItem, StockEntry } from '../models/inventory.model';

describe('InventoryPage', () => {
  let component: InventoryPage;
  let fixture: ComponentFixture<InventoryPage>;
  let inventoryServiceMock: {
    getInventoryStock: ReturnType<typeof vi.fn>;
    getInventory: ReturnType<typeof vi.fn>;
    recordStockEntry: ReturnType<typeof vi.fn>;
  };

  beforeEach(async () => {
    inventoryServiceMock = {
      getInventoryStock: vi.fn(),
      getInventory: vi.fn(),
      recordStockEntry: vi.fn()
    };

    const stockHealthResponse: ApiResponse<InventoryStockItem[]> = {
      success: true,
      message: 'Stock health loaded',
      data: [],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    const inventoryResponse: ApiResponse<InventoryItem[]> = {
      success: true,
      message: 'Inventory loaded',
      data: [],
      errorCode: null,
      timestamp: '2026-10-05T12:00:00Z'
    };

    inventoryServiceMock.getInventoryStock.mockReturnValue(of(stockHealthResponse));
    inventoryServiceMock.getInventory.mockReturnValue(of(inventoryResponse));

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
