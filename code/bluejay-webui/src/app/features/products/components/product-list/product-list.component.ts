// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~75%
// AI-Assisted Areas: Paginated product table component, shared filter state,
// load/error handling and paginator length logic
// Human Contributions: Diagnosed the ExpressionChangedAfterItHasBeenCheckedError
// (NG0100), directed the move to signal-based state, replaced barcode search
// with shared list filters, and verified paging in the browser
// Notes: State uses signals and a computed paginatorLength because the API
// returns no total count.
// authors: Kimleng

import { CurrencyPipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { Product, ProductQuery } from '../../models/product.model';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CurrencyPipe, MatPaginatorModule, MatProgressBarModule, MatTableModule],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss',
})
export class ProductListComponent implements OnInit {
  private readonly productService = inject(ProductService);
  readonly products = signal<Product[]>([]);
  readonly page = signal(1);
  readonly pageSize = signal(10);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly filters = signal<ProductQuery>({});
  readonly displayedColumns = ['name', 'barcode', 'category', 'price'];

  // The API returns a page without a total count; one extra row keeps Next available
  // when the current page is full. The next response determines whether it exists.
  readonly paginatorLength = computed(() => {
    const size = this.pageSize();
    const count = this.products().length;
    const endOfCurrentPage = (this.page() - 1) * size + count;
    if (count === size) return endOfCurrentPage + 1;
    return Math.max(endOfCurrentPage, this.page() * size);
  });

  ngOnInit(): void {
    this.loadPage(1);
  }

  changePage(event: PageEvent): void {
    this.pageSize.set(event.pageSize);
    this.loadPage(event.pageIndex + 1);
  }

  applyFilters(filters: ProductQuery): void {
    this.filters.set({
      productName: filters.productName?.trim() || null,
      categoryName: filters.categoryName?.trim() || null,
    });
    this.loadPage(1);
  }

  loadPage(page: number): void {
    this.page.set(Math.max(1, page));
    this.loading.set(true);
    this.error.set(null);
    this.productService.list({
      ...this.filters(),
      pageNumber: this.page(),
      pageSize: this.pageSize(),
    })
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (products) => this.products.set(products),
        error: (error) => {
          this.products.set([]);
          this.error.set(error?.error?.message || 'Could not load the product catalog.');
        },
      });
  }
}
