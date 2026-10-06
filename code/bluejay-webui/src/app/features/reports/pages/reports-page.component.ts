// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Report table template, loading/empty/error states
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
// - PR review (Sara): styles moved to reports-page.component.scss
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
  styleUrl: './reports-page.component.scss',
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