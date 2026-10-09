// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Sortable per-product table (profit / quantity sold)
// Human Contributions: Story #30 columns and sort options
// Notes: Presentational component; the Sales Report page loads the data and passes it in.
// authors: Krizma Nagi

import { Component, computed, input, signal } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { ProductSalesItem } from '../../models/product-sales.model';

export type ProductSortKey = 'profit' | 'quantitySold';
export type SortDirection = 'desc' | 'asc';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Frontend table showing results per product, sortable by profit or quantity"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Click Quantity sold or Profit to sort; click again to flip the direction
// - Default sort: profit, highest first (matches the API order)
// - Products with no sales are dimmed; negative profit is red
// - Uses the recent-table styles from report-layout.scss (same as Inventory)
// Verification:
// - product-sales-table.component.spec.ts
// Confidence: High
@Component({
  selector: 'app-product-sales-table',
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe],
  template: `
    @if (products().length === 0) {
      <p class="card-subtitle">No products in the catalog yet.</p>
    } @else {
      <div class="table-responsive">
        <table class="recent-table">
          <thead>
            <tr>
              <th>PRODUCT</th>
              <th>BARCODE</th>
              <th class="text-right">
                <button
                  type="button"
                  class="sort-btn"
                  data-testid="sort-quantity"
                  [class.active]="sortKey() === 'quantitySold'"
                  [attr.aria-sort]="ariaSort('quantitySold')"
                  (click)="sortBy('quantitySold')"
                >
                  QTY SOLD {{ arrow('quantitySold') }}
                </button>
              </th>
              <th class="text-right">REVENUE</th>
              <th class="text-right">TOTAL COST</th>
              <th class="text-right">
                <button
                  type="button"
                  class="sort-btn"
                  data-testid="sort-profit"
                  [class.active]="sortKey() === 'profit'"
                  [attr.aria-sort]="ariaSort('profit')"
                  (click)="sortBy('profit')"
                >
                  PROFIT {{ arrow('profit') }}
                </button>
              </th>
            </tr>
          </thead>
          <tbody>
            @for (item of sorted(); track item.productId) {
              <tr [class.no-sales]="item.quantitySold === 0">
                <td class="font-medium">{{ item.name }}</td>
                <td class="text-muted">{{ item.barcode ?? '—' }}</td>
                <td class="text-right">{{ item.quantitySold | number }}</td>
                <td class="text-right">{{ item.totalRevenue | currency }}</td>
                <td class="text-right">{{ item.totalCost | currency }}</td>
                <td
                  class="text-right profit"
                  [class.text-danger]="item.profit < 0"
                  [class.text-success]="item.profit > 0"
                >
                  {{ item.profit | currency }}
                </td>
              </tr>
            }
          </tbody>
        </table>
      </div>
    }
  `,
  styleUrls: ['../../report-layout.scss', './product-sales-table.component.scss'],
})
export class ProductSalesTableComponent {
  readonly products = input<ProductSalesItem[]>([]);

  readonly sortKey = signal<ProductSortKey>('profit');
  readonly sortDirection = signal<SortDirection>('desc');

  readonly sorted = computed(() => {
    const key = this.sortKey();
    const dir = this.sortDirection() === 'desc' ? -1 : 1;
    return [...this.products()].sort(
      (a, b) => (a[key] - b[key]) * dir || a.name.localeCompare(b.name),
    );
  });

  sortBy(key: ProductSortKey): void {
    if (this.sortKey() === key) {
      this.sortDirection.set(this.sortDirection() === 'desc' ? 'asc' : 'desc');
    } else {
      this.sortKey.set(key);
      this.sortDirection.set('desc');
    }
  }

  arrow(key: ProductSortKey): string {
    if (this.sortKey() !== key) return '';
    return this.sortDirection() === 'desc' ? '↓' : '↑';
  }

  ariaSort(key: ProductSortKey): string {
    if (this.sortKey() !== key) return 'none';
    return this.sortDirection() === 'desc' ? 'descending' : 'ascending';
  }
}