// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Component test setup with a stubbed service
// Human Contributions: Scenarios from Story #57 (table rows, empty catalog, cashier 403)
// Notes: Rendering tests for the inventory report page.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { ReportsPage } from './reports-page.component';
import {
  InventoryReportItem,
  InventoryReportService,
} from '../services/inventory-report.service';

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
  let response: Observable<InventoryReportItem[]>;
  let admin = false;

  beforeEach(() => {
    admin = false;
  });

  function render(): HTMLElement {
    TestBed.configureTestingModule({
      imports: [ReportsPage],
      providers: [
        provideRouter([]),
        { provide: InventoryReportService, useValue: { getInventoryReport: () => response } },
        { provide: AuthService, useValue: { isAdmin: () => admin } },
      ],
    });
    const fixture = TestBed.createComponent(ReportsPage);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('shows a row per product with name, latest cost and on-hand quantity', () => {
    response = of([
      { productId: 'p-1', barcode: '111', name: 'Cooking Oil 2L', latestCost: 4.25, onHandQuantity: 34 },
      { productId: 'p-2', barcode: '222', name: 'Laundry Soap', latestCost: 2.1, onHandQuantity: 0 },
    ]);

    const el = render();
    const rows = el.querySelectorAll('tbody tr');

    expect(rows.length).toBe(2);
    expect(rows[0].textContent).toContain('Cooking Oil 2L');
    expect(rows[0].textContent).toContain('$4.25');
    expect(rows[0].textContent).toContain('34');
    expect(rows[1].textContent).toContain('Laundry Soap');
    expect(el.textContent).toContain('34 units on hand');
  });

  it('shows an empty-catalog message when there are no products', () => {
    response = of([]);

    const el = render();

    expect(el.querySelector('table')).toBeNull();
    expect(el.textContent).toContain('No products in the catalog yet.');
  });

  it('shows a permission message when the API returns 403', () => {
    response = throwError(() => new HttpErrorResponse({ status: 403 }));

    const el = render();

    expect(el.querySelector('table')).toBeNull();
    expect(el.textContent).toContain("You don't have permission to view this report");
  });

  it('shows the sales report link to Admins only (Story #29)', () => {
    response = of([]);
    expect(render().querySelector('a[href="/reports/sales"]')).toBeNull();

    TestBed.resetTestingModule();
    admin = true;
    expect(render().querySelector('a[href="/reports/sales"]')).not.toBeNull();
  });
});