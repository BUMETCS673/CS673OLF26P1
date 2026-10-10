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
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
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
// - PR review (Sara): styles moved out of the component into a stylesheet
// - Story #29: shows a "Sales report" link to Admins only
// - Story #29: restyled to the Inventory/Products page layout (eyebrow heading,
//   card, recent-table, alert) using the shared report-layout.scss
// Verification:
// - reports-page.component.spec.ts and ng build
// Confidence: High
@Component({
  selector: 'app-reports-page',
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe, RouterLink],
  template: `
    <main class="page">
      <header class="page-heading">
        <p class="eyebrow">REPORTS</p>
        <h1>Inventory Report</h1>
        <p>Current stock and latest cost for every product.</p>
      </header>

      <div class="card">
        <div class="card-header">
          <div>
            <h2 class="card-title">Current Stock Levels</h2>
            @if (state() === 'loaded' && items().length > 0) {
              <p class="card-subtitle">
                {{ items().length }} products · {{ totalUnits() | number }} units on hand
              </p>
            } @else {
              <p class="card-subtitle">Every product in the catalog.</p>
            }
          </div>
          <div class="card-actions">
            @if (isAdmin) {
              <a class="btn-outline" routerLink="/reports/sales">Sales report →</a>
            }
            <button type="button" class="btn-action" (click)="load()" [disabled]="state() === 'loading'">
              Refresh
            </button>
          </div>
        </div>

        @switch (state()) {
          @case ('loading') {
            <p class="card-subtitle">Loading inventory…</p>
          }
          @case ('forbidden') {
            <div class="alert alert-danger" role="alert">
              You don't have permission to view this report. It is available to Admins and Stock Managers.
            </div>
          }
          @case ('error') {
            <div class="alert alert-danger" role="alert">
              The inventory report could not be loaded. Please try again.
            </div>
          }
          @case ('loaded') {
            @if (items().length === 0) {
              <p class="card-subtitle">No products in the catalog yet.</p>
            } @else {
              <div class="table-responsive">
                <table class="recent-table">
                  <thead>
                    <tr>
                      <th>PRODUCT</th>
                      <th>BARCODE</th>
                      <th class="text-right">LATEST COST</th>
                      <th class="text-right">ON HAND</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (item of items(); track item.productId) {
                      <tr>
                        <td class="font-medium">{{ item.name }}</td>
                        <td class="text-muted">{{ item.barcode ?? '—' }}</td>
                        <td class="text-right">{{ item.latestCost | currency }}</td>
                        <td class="text-right" [class.text-danger]="item.onHandQuantity <= 0">
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
      </div>
    </main>
  `,
  styleUrl: '../report-layout.scss',
})
export class ReportsPage implements OnInit {
  private reportService = inject(InventoryReportService);
  readonly isAdmin = inject(AuthService).isAdmin();

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