// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Report table template, loading/empty/error states, styles
// Human Contributions: Columns from Story #57 (product, latest cost, on-hand quantity)
// Notes: Replaces the "coming soon" placeholder with the core inventory report table.
// authors: Krizma Nagi

import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  InventoryReportItem,
  InventoryReportService,
} from '../services/inventory-report.service';

type LoadState = 'loading' | 'loaded' | 'forbidden' | 'error';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Frontend report table for the inventory report"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Shows a clear message on 403 so cashiers know why the report is unavailable
// - Uses signals, matching the standalone Angular 21 setup
// Verification:
// - reports-page.component.spec.ts and ng build
// Confidence: High
@Component({
  selector: 'app-reports-page',
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe],
  template: `
    <section class="report">
      <header class="report__header">
        <div>
          <h1>Inventory Report</h1>
          <p class="report__subtitle">Current stock and latest cost for every product.</p>
        </div>
        <button type="button" class="report__refresh" (click)="load()" [disabled]="state() === 'loading'">
          Refresh
        </button>
      </header>

      @switch (state()) {
        @case ('loading') {
          <p class="report__message">Loading inventory…</p>
        }
        @case ('forbidden') {
          <p class="report__message report__message--error" role="alert">
            You don't have permission to view this report. It is available to Admins and Stock Managers.
          </p>
        }
        @case ('error') {
          <p class="report__message report__message--error" role="alert">
            The inventory report could not be loaded. Please try again.
          </p>
        }
        @case ('loaded') {
          @if (items().length === 0) {
            <p class="report__message">No products in the catalog yet.</p>
          } @else {
            <p class="report__summary">
              {{ items().length }} products · {{ totalUnits() | number }} units on hand
            </p>
            <div class="report__table-wrap">
              <table class="report__table">
                <thead>
                  <tr>
                    <th scope="col">Product</th>
                    <th scope="col">Barcode</th>
                    <th scope="col" class="num">Latest cost</th>
                    <th scope="col" class="num">On hand</th>
                  </tr>
                </thead>
                <tbody>
                  @for (item of items(); track item.productId) {
                    <tr>
                      <td>{{ item.name }}</td>
                      <td class="muted">{{ item.barcode ?? '—' }}</td>
                      <td class="num">{{ item.latestCost | currency }}</td>
                      <td class="num" [class.out]="item.onHandQuantity <= 0">
                        {{ item.onHandQuantity | number }}
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          }
        }
      }
    </section>
  `,
  styles: `
    .report { max-width: 960px; }
    .report__header { display: flex; justify-content: space-between; align-items: flex-start; gap: 16px; margin-bottom: 16px; }
    .report__header h1 { margin: 0; }
    .report__subtitle { margin: 4px 0 0; color: #64748b; }
    .report__refresh { padding: 8px 16px; border: 0; border-radius: 8px; background: #2f4fe0; color: #fff; font-weight: 600; cursor: pointer; }
    .report__refresh:disabled { opacity: 0.6; cursor: default; }
    .report__summary { color: #475569; margin: 0 0 8px; }
    .report__message { padding: 16px; border-radius: 8px; background: #f1f5f9; }
    .report__message--error { background: #fef2f2; color: #b91c1c; }
    .report__table-wrap { overflow-x: auto; border: 1px solid #e2e8f0; border-radius: 12px; }
    .report__table { width: 100%; border-collapse: collapse; }
    .report__table th, .report__table td { padding: 10px 14px; text-align: left; border-bottom: 1px solid #e2e8f0; }
    .report__table th { background: #f8fafc; font-size: 0.85rem; color: #475569; }
    .report__table tbody tr:last-child td { border-bottom: 0; }
    .num { text-align: right !important; font-variant-numeric: tabular-nums; }
    .muted { color: #64748b; }
    .out { color: #b91c1c; font-weight: 600; }
  `,
})
export class ReportsPage implements OnInit {
  private reportService = inject(InventoryReportService);

  readonly items = signal<InventoryReportItem[]>([]);
  readonly state = signal<LoadState>('loading');
  readonly totalUnits = computed(() =>
    this.items().reduce((sum, item) => sum + item.onHandQuantity, 0),
  );

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.state.set('loading');
    this.reportService.getInventoryReport().subscribe({
      next: (items) => {
        this.items.set(items);
        this.state.set('loaded');
      },
      error: (err: HttpErrorResponse) => {
        this.items.set([]);
        this.state.set(err.status === 403 ? 'forbidden' : 'error');
      },
    });
  }
}