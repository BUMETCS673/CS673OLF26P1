// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Date range form, quick presets, summary tiles, loading/empty/error states
// Human Contributions: Story #29 scope (revenue, cost, net profit for a selected period)
// Notes: Admin-only page at /reports/sales (guarded by adminGuard in reports.routes.ts).
// authors: Krizma Nagi

import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { RouterLink } from '@angular/router';
import { SalesReport } from '../models/sales-report.model';
import { SalesReportService } from '../services/sales-report.service';
import { ProductSalesItem } from '../models/product-sales.model';
import { ProductSalesService } from '../services/product-sales.service';
import { ProductSalesTableComponent } from '../components/product-sales-table/product-sales-table.component';

type LoadState = 'idle' | 'loading' | 'loaded' | 'forbidden' | 'error';

/** Formats a Date as YYYY-MM-DD in local time (toISOString would use UTC). */
export function toIsoDate(date: Date): string {
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${date.getFullYear()}-${month}-${day}`;
}

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Build frontend for the sales report by date range"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Defaults to the current month so the page shows data on first load
// - Checks start <= end in the browser before calling the API
// - Shows the API's message for 400 errors and a permission message for 403
// - Net profit tile turns red when the period made a loss
// - Follows the Inventory/Products page layout: eyebrow heading, summary card
//   on the left, form card on the right (shared report-layout.scss)
// - Story #30: full-width "Sales by Product" card below the summary and form,
//   loaded for the same date range (ProductSalesService + ProductSalesTableComponent)
// Verification:
// - sales-report-page.component.spec.ts and manual QA with docs/qa/story-29-sales-seed.sql
// Confidence: High
@Component({
  selector: 'app-sales-report-page',
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe, RouterLink, ProductSalesTableComponent],
  template: `
    <main class="page">
      <a class="back-link" routerLink="/reports">← Back to reports</a>

      <header class="page-heading">
        <p class="eyebrow">REPORTS</p>
        <h1>Sales Report</h1>
        <p>Total revenue, cost and net profit for a selected period.</p>
      </header>

      <div class="columns">
        <section class="stack" aria-label="Sales summary">
          <div class="card">
            <div class="card-header">
              <div>
                <h2 class="card-title">Summary</h2>
                @if (state() === 'loaded' && report(); as r) {
                  <p class="card-subtitle">
                    {{ r.startDate }} to {{ r.endDate }} ·
                    {{ r.transactionCount | number }} transactions ·
                    {{ r.unitsSold | number }} units sold
                  </p>
                } @else {
                  <p class="card-subtitle">Choose a period to run the report.</p>
                }
              </div>
            </div>

            @switch (state()) {
              @case ('loading') {
                <p class="card-subtitle">Loading sales…</p>
              }
              @case ('forbidden') {
                <div class="alert alert-danger" role="alert">
                  You don't have permission to view this report. It is available to Admins only.
                </div>
              }
              @case ('error') {
                <div class="alert alert-danger" role="alert">{{ errorMessage() }}</div>
              }
              @case ('loaded') {
                @if (report(); as r) {
                  <div class="stat-grid">
                    <div class="stat">
                      <span class="stat__label">TOTAL REVENUE</span>
                      <span class="stat__value" data-testid="revenue">{{ r.totalRevenue | currency }}</span>
                    </div>
                    <div class="stat">
                      <span class="stat__label">TOTAL COST</span>
                      <span class="stat__value" data-testid="cost">{{ r.totalCost | currency }}</span>
                    </div>
                    <div class="stat stat--profit" [class.stat--loss]="r.netProfit < 0">
                      <span class="stat__label">NET PROFIT</span>
                      <span class="stat__value" data-testid="profit">{{ r.netProfit | currency }}</span>
                    </div>
                  </div>
                  @if (r.transactionCount === 0) {
                    <p class="card-subtitle empty-note">No sales in this period.</p>
                  }
                }
              }
            }
          </div>
        </section>

        <div class="card">
          <div class="card-header">
            <div>
              <h2 class="card-title">Report Period</h2>
              <p class="card-subtitle">Both dates are included.</p>
            </div>
          </div>

          <form class="form-stack" (submit)="$event.preventDefault(); load()">
            <div class="form-group">
              <label for="startDate">Start date</label>
              <input
                id="startDate"
                type="date"
                name="startDate"
                class="form-control"
                [value]="startDate()"
                (change)="startDate.set($any($event.target).value)"
                required
              />
            </div>
            <div class="form-group">
              <label for="endDate">End date</label>
              <input
                id="endDate"
                type="date"
                name="endDate"
                class="form-control"
                [value]="endDate()"
                (change)="endDate.set($any($event.target).value)"
                required
              />
            </div>
            <div class="presets">
              <button type="button" class="btn-outline" (click)="setThisMonth()">This month</button>
              <button type="button" class="btn-outline" (click)="setLastMonth()">Last month</button>
              <button type="button" class="btn-outline" (click)="setLastDays(30)">Last 30 days</button>
            </div>
            <button type="submit" class="btn-action btn-block" [disabled]="state() === 'loading'">
              {{ state() === 'loading' ? 'Running...' : 'Run Report' }}
            </button>
          </form>

          @if (validationError()) {
            <div class="alert alert-danger" role="alert">{{ validationError() }}</div>
          }
        </div>
      </div>

      <div class="card full-width">
        <div class="card-header">
          <div>
            <h2 class="card-title">Sales by Product</h2>
            <p class="card-subtitle">
              Quantity sold, cost and profit per product. Click Qty sold or Profit to sort.
            </p>
          </div>
        </div>

        @switch (productState()) {
          @case ('loading') {
            <p class="card-subtitle">Loading products…</p>
          }
          @case ('error') {
            <div class="alert alert-danger" role="alert">
              Product sales could not be loaded. Please try again.
            </div>
          }
          @case ('loaded') {
            <app-product-sales-table [products]="productSales()" />
          }
        }
      </div>
    </main>
  `,
  styleUrls: ['../report-layout.scss', './sales-report-page.component.scss'],
})
export class SalesReportPage implements OnInit {
  private salesReportService = inject(SalesReportService);
  private productSalesService = inject(ProductSalesService);

  readonly startDate = signal('');
  readonly endDate = signal('');
  readonly report = signal<SalesReport | null>(null);
  readonly state = signal<LoadState>('idle');
  readonly errorMessage = signal('');
  readonly validationError = signal('');
  readonly productSales = signal<ProductSalesItem[]>([]);
  readonly productState = signal<LoadState>('idle');

  ngOnInit(): void {
    this.setThisMonth();
  }

  setThisMonth(): void {
    const today = new Date();
    this.setRange(new Date(today.getFullYear(), today.getMonth(), 1), today);
  }

  setLastMonth(): void {
    const today = new Date();
    const first = new Date(today.getFullYear(), today.getMonth() - 1, 1);
    const last = new Date(today.getFullYear(), today.getMonth(), 0);
    this.setRange(first, last);
  }

  setLastDays(days: number): void {
    const today = new Date();
    const start = new Date(today.getFullYear(), today.getMonth(), today.getDate() - (days - 1));
    this.setRange(start, today);
  }

  load(): void {
    const start = this.startDate();
    const end = this.endDate();

    if (!start || !end) {
      this.validationError.set('Please choose both a start date and an end date.');
      return;
    }
    // YYYY-MM-DD strings compare correctly as text
    if (start > end) {
      this.validationError.set('Start date must be on or before the end date.');
      return;
    }
    this.validationError.set('');

    this.state.set('loading');
    this.salesReportService.getSalesReport(start, end).subscribe({
      next: (report) => {
        this.report.set(report);
        this.state.set('loaded');
      },
      error: (err: HttpErrorResponse) => {
        this.report.set(null);
        if (err.status === 403) {
          this.state.set('forbidden');
          return;
        }
        this.errorMessage.set(
          err.status === 400 && err.error?.message
            ? err.error.message
            : 'The sales report could not be loaded. Please try again.',
        );
        this.state.set('error');
      },
    });

    this.loadProductSales(start, end);
  }

  private loadProductSales(start: string, end: string): void {
    this.productState.set('loading');
    this.productSalesService.getProductSales(start, end).subscribe({
      next: (report) => {
        this.productSales.set(report.products);
        this.productState.set('loaded');
      },
      error: (err: HttpErrorResponse) => {
        this.productSales.set([]);
        // A 403 is already explained by the summary card's permission message
        this.productState.set(err.status === 403 ? 'forbidden' : 'error');
      },
    });
  }

  private setRange(start: Date, end: Date): void {
    this.startDate.set(toIsoDate(start));
    this.endDate.set(toIsoDate(end));
    this.load();
  }
}