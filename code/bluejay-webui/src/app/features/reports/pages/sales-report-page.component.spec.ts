// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Component test setup with a stubbed service
// Human Contributions: Story #29 scenarios (totals, empty range, invalid range, 403)
// Notes: Rendering and validation tests for the sales report page.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { SalesReportPage, toIsoDate } from './sales-report-page.component';
import { SalesReportService } from '../services/sales-report.service';
import { SalesReport } from '../models/sales-report.model';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Component tests for the sales report page"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Stubbed SalesReportService and counted calls to check client-side validation
// Verification:
// - npm test
// Confidence: High
describe('SalesReportPage', () => {
  let response: Observable<SalesReport>;
  let calls: Array<[string, string]>;

  const report = (overrides: Partial<SalesReport> = {}): SalesReport => ({
    startDate: '2026-10-01',
    endDate: '2026-10-31',
    totalRevenue: 62.48,
    totalCost: 40.48,
    netProfit: 22,
    transactionCount: 3,
    unitsSold: 7,
    ...overrides,
  });

  function setup() {
    calls = [];
    TestBed.configureTestingModule({
      imports: [SalesReportPage],
      providers: [
        provideRouter([]),
        {
          provide: SalesReportService,
          useValue: {
            getSalesReport: (start: string, end: string) => {
              calls.push([start, end]);
              return response;
            },
          },
        },
      ],
    });
    const fixture = TestBed.createComponent(SalesReportPage);
    fixture.detectChanges();
    return { fixture, el: fixture.nativeElement as HTMLElement };
  }

  it('loads the current month on first render', () => {
    response = of(report());
    setup();

    const today = new Date();
    const first = new Date(today.getFullYear(), today.getMonth(), 1);
    expect(calls).toEqual([[toIsoDate(first), toIsoDate(today)]]);
  });

  it('shows total revenue, total cost and net profit', () => {
    response = of(report());
    const { el } = setup();

    expect(el.querySelector('[data-testid="revenue"]')?.textContent).toContain('$62.48');
    expect(el.querySelector('[data-testid="cost"]')?.textContent).toContain('$40.48');
    expect(el.querySelector('[data-testid="profit"]')?.textContent).toContain('$22.00');
    expect(el.textContent).toContain('3 transactions');
  });

  it('marks a loss in red', () => {
    response = of(report({ totalRevenue: 100, totalCost: 130, netProfit: -30 }));
    const { el } = setup();

    expect(el.querySelector('.stat--loss')).not.toBeNull();
  });

  it('shows a message for an empty range with no sales', () => {
    response = of(report({ totalRevenue: 0, totalCost: 0, netProfit: 0, transactionCount: 0, unitsSold: 0 }));
    const { el } = setup();

    expect(el.textContent).toContain('No sales in this period.');
  });

  it('rejects a start date after the end date without calling the API', () => {
    response = of(report());
    const { fixture, el } = setup();
    calls = [];

    fixture.componentInstance.startDate.set('2026-10-31');
    fixture.componentInstance.endDate.set('2026-10-01');
    fixture.componentInstance.load();
    fixture.detectChanges();

    expect(calls).toEqual([]);
    expect(el.textContent).toContain('Start date must be on or before the end date.');
  });

  it('shows a permission message when the API returns 403', () => {
    response = throwError(() => new HttpErrorResponse({ status: 403 }));
    const { el } = setup();

    expect(el.querySelector('.stat-grid')).toBeNull();
    expect(el.textContent).toContain("You don't have permission to view this report");
  });

  it('shows the API message for a 400 error', () => {
    response = throwError(
      () =>
        new HttpErrorResponse({
          status: 400,
          error: { message: 'Start date 2026-10-31 must be on or before end date 2026-10-01.' },
        }),
    );
    const { el } = setup();

    expect(el.textContent).toContain('must be on or before end date');
  });

  it('formats dates as YYYY-MM-DD in local time', () => {
    expect(toIsoDate(new Date(2026, 0, 5))).toBe('2026-01-05');
  });
});