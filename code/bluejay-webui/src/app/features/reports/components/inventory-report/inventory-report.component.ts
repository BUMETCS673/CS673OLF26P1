import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import {
  InventoryReportItem,
  InventoryReportService,
} from '../../services/inventory-report.service';

type LoadState = 'loading' | 'loaded' | 'forbidden' | 'error';

@Component({
  selector: 'app-inventory-report',
  standalone: true,
  imports: [CurrencyPipe, DecimalPipe],
  templateUrl: './inventory-report.component.html',
  styleUrls: ['./inventory-report.component.scss'],
})
export class InventoryReportComponent implements OnInit {
  private readonly reportService = inject(InventoryReportService);

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
