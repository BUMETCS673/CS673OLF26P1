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

import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { Product, ProductQuery } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { InlineActionButtonsComponent } from '../../../../shared/components/inline-action-buttons.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [MatPaginatorModule, MatProgressBarModule, MatTableModule,
    FormsModule, InlineActionButtonsComponent],
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
  readonly totalCount = signal(0);
  readonly displayedColumns = ['name', 'barcode', 'category', 'lowStock', 'actions'];
  readonly paginatorLength = computed(() => this.totalCount());

  // Inline editing state signals
  readonly editingProductId = signal<string | null>(null);
  readonly editingLowStock = signal<number>(0);

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
        next: (result) => {
          this.products.set(result.items);
          this.totalCount.set(result.totalCount);
        },
        error: (error) => {
          this.products.set([]);
          this.totalCount.set(0);
          this.error.set(error?.error?.message || 'Could not load the product catalog.');
        },
      });
  }

  // AI-ASSISTED: YES
  // Tool: Gemini
  // Prompt Summary: "Add methods startEdit, cancelEdit, and saveLowStock for inline editing of product low stock."
  // AI Contribution: Initial draft (~85%)
  // Modifications: Connected updateLowStock HTTP call with local product signal array patch.
  // Verification: Angular UI interaction test.
  // Confidence: High
  startEdit(product: Product): void {
    this.editingProductId.set(product.id);
    this.editingLowStock.set(product.lowStock);
  }

  cancelEdit(): void {
    this.editingProductId.set(null);
  }

  saveLowStock(product: Product): void {
    const updatedValue = this.editingLowStock();
    if (updatedValue === product.lowStock) {
      this.cancelEdit();
      return;
    }

    this.loading.set(true);
    this.productService.updateLowStock(product.id, updatedValue)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (updatedProduct) => {
          this.products.update((items) =>
            items.map((item) => (item.id === product.id ? { ...item, lowStock: updatedProduct.lowStock } : item))
          );
          this.cancelEdit();
        },
        error: (err) => {
          this.error.set(err?.error?.message || 'Failed to update low stock threshold.');
        }
      });
  }
}
