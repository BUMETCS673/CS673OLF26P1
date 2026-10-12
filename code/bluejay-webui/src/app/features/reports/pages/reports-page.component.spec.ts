// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Component test setup with a stubbed service
// Human Contributions: Scenarios from Story #57 (table rows, empty catalog, cashier 403)
// Notes: Rendering tests for the inventory report page.
// authors: Krizma Nagi

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatSelectHarness } from '@angular/material/select/testing';
import { Observable, of, throwError } from 'rxjs';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ReportsPage } from './reports-page.component';
import {
  InventoryReportItem,
  InventoryReportService,
} from '../services/inventory-report.service';
import { SalesReportService } from '../services/sales-report.service';
import { ProductSalesReport } from '../models/product-sales.model';
import { SalesReport } from '../models/sales-report.model';
import { ProductSalesService } from '../services/product-sales.service';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Component tests for the inventory report table"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Stubbed InventoryReportService so no HTTP is involved
// - Story #29: stubbed AuthService and added a test for the Admin-only sales link
// Verification:
// - npm test
// Confidence: High
describe('ReportsPage', () => {
  let inventoryResponse: Observable<InventoryReportItem[]>;
  let salesResponse: Observable<SalesReport>;
  let productSalesResponse: Observable<ProductSalesReport>;
  let admin = false;
  let routePath = 'inventory';

  beforeEach(() => {
    admin = false;
    routePath = 'inventory';
    productSalesResponse = of({ startDate: '2026-10-01', endDate: '2026-10-31', products: [] });
  });

  function render(): ComponentFixture<ReportsPage> {
    TestBed.configureTestingModule({
      imports: [ReportsPage],
      providers: [
        provideRouter([]),
        { provide: ActivatedRoute, useValue: { snapshot: { routeConfig: { path: routePath } } } },
        { provide: InventoryReportService, useValue: { getInventoryReport: () => inventoryResponse } },
        { provide: SalesReportService, useValue: { getSalesReport: () => salesResponse } },
        { provide: ProductSalesService, useValue: { getProductSales: () => productSalesResponse } },
        { provide: AuthService, useValue: { isAdmin: () => admin } },
      ],
    });
    const fixture = TestBed.createComponent(ReportsPage);
    fixture.detectChanges();
    return fixture;
  }

  it('shows a row per product with name, latest cost and on-hand quantity', () => {
    inventoryResponse = of([
      { productId: 'p-1', barcode: '111', name: 'Cooking Oil 2L', latestCost: 4.25, onHandQuantity: 34 },
      { productId: 'p-2', barcode: '222', name: 'Laundry Soap', latestCost: 2.1, onHandQuantity: 0 },
    ]);
    salesResponse = of({
      startDate: '2026-10-01',
      endDate: '2026-10-31',
      totalRevenue: 62.48,
      totalCost: 40.48,
      netProfit: 22,
      transactionCount: 3,
      unitsSold: 7,
    });

    const el = render().nativeElement as HTMLElement;
    const rows = el.querySelectorAll('tbody tr');

    expect(rows.length).toBe(2);
    expect(rows[0].textContent).toContain('Cooking Oil 2L');
    expect(rows[0].textContent).toContain('$4.25');
    expect(rows[0].textContent).toContain('34');
    expect(rows[1].textContent).toContain('Laundry Soap');
    expect(el.textContent).toContain('34 units on hand');
  }, 10000);

  it('shows an empty-catalog message when there are no products', () => {
    inventoryResponse = of([]);
    salesResponse = of({
      startDate: '2026-10-01',
      endDate: '2026-10-31',
      totalRevenue: 0,
      totalCost: 0,
      netProfit: 0,
      transactionCount: 0,
      unitsSold: 0,
    });

    const el = render().nativeElement as HTMLElement;

    expect(el.querySelector('table')).toBeNull();
    expect(el.textContent).toContain('No products in the catalog yet.');
  });

  it('shows a permission message when the API returns 403', () => {
    inventoryResponse = throwError(() => new HttpErrorResponse({ status: 403 }));
    salesResponse = of({
      startDate: '2026-10-01',
      endDate: '2026-10-31',
      totalRevenue: 0,
      totalCost: 0,
      netProfit: 0,
      transactionCount: 0,
      unitsSold: 0,
    });

    const el = render().nativeElement as HTMLElement;

    expect(el.querySelector('table')).toBeNull();
    expect(el.textContent).toContain("You don't have permission to view this report");
  });

  it('shows the sales report option in the selector to Admins only (Story #29)', async () => {
    inventoryResponse = of([]);
    salesResponse = of({
      startDate: '2026-10-01',
      endDate: '2026-10-31',
      totalRevenue: 0,
      totalCost: 0,
      netProfit: 0,
      transactionCount: 0,
      unitsSold: 0,
    });
    const nonAdminFixture = render();
    const nonAdminEl = nonAdminFixture.nativeElement as HTMLElement;
    const nonAdminSelect = await TestbedHarnessEnvironment.loader(nonAdminFixture).getHarness(
      MatSelectHarness,
    );

    expect(nonAdminEl.querySelector('mat-form-field')).not.toBeNull();
    expect(nonAdminEl.textContent).toContain('Report type');
    expect(nonAdminEl.textContent).toContain('Inventory');
    await nonAdminSelect.open();
    expect(await nonAdminSelect.getOptions({ text: /Inventory/ })).toHaveLength(1);
    expect(await nonAdminSelect.getOptions({ text: /Sales/ })).toHaveLength(0);
    await nonAdminSelect.close();

    TestBed.resetTestingModule();
    admin = true;
    const adminFixture = render();
    const adminEl = adminFixture.nativeElement as HTMLElement;
    const adminSelect = await TestbedHarnessEnvironment.loader(adminFixture).getHarness(
      MatSelectHarness,
    );

    expect(adminEl.querySelector('mat-form-field')).not.toBeNull();
    await adminSelect.open();
    expect(await adminSelect.getOptions({ text: /Inventory/ })).toHaveLength(1);
    expect(await adminSelect.getOptions({ text: /Sales/ })).toHaveLength(1);
  });

  it('shows the sales heading and sales report component for the sales route', () => {
    inventoryResponse = of([]);
    salesResponse = of({
      startDate: '2026-10-01',
      endDate: '2026-10-31',
      totalRevenue: 62.48,
      totalCost: 40.48,
      netProfit: 22,
      transactionCount: 3,
      unitsSold: 7,
    });
    routePath = 'sales';

    const el = render().nativeElement as HTMLElement;

    expect(el.textContent).toContain('Sales Report');
    expect(el.querySelector('mat-form-field')).not.toBeNull();
    expect(el.textContent).toContain('Report type');
    expect(el.querySelector('[data-testid="revenue"]')?.textContent).toContain('$62.48');
  });
});