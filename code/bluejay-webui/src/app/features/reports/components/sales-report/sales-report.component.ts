import { Component, OnInit, inject, signal } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ProductSalesItem } from '../../models/product-sales.model';
import { SalesReport } from '../../models/sales-report.model';
import { ProductSalesTableComponent } from '../product-sales-table/product-sales-table.component';
import { ProductSalesService } from '../../services/product-sales.service';
import { SalesReportService } from '../../services/sales-report.service';

type LoadState = 'idle' | 'loading' | 'loaded' | 'forbidden' | 'error';

/** Formats a Date as YYYY-MM-DD in local time (toISOString would use UTC). */
export function toIsoDate(date: Date): string {
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${date.getFullYear()}-${month}-${day}`;
}

@Component({
  selector: 'app-sales-report',
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe, ProductSalesTableComponent],
  templateUrl: './sales-report.component.html',
  styleUrls: ['./sales-report.component.scss'],
})
export class SalesReportComponent implements OnInit {
  private readonly salesReportService = inject(SalesReportService);
  private readonly productSalesService = inject(ProductSalesService);

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
    if (start > end) {
      this.validationError.set('Start date must be on or before the end date.');
      return;
    }
    this.validationError.set('');

    this.state.set('loading');
    this.loadProductSales(start, end);
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
