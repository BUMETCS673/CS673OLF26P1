// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Barcode search component with found, not-found and error outcomes
// Human Contributions: Specified that search results appear in the search card instead of filtering the product table, supplied the 422 PRODUCT_PRICE_MISSING behavior, reported the dirty-form issue after clear, and verified it in the browser
// Notes: Calls ProductService directly and no longer emits events to the list.
// authors: Kimleng

import { CurrencyPipe } from '@angular/common';
import { Component, ViewChild, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { finalize } from 'rxjs';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';

type SearchOutcome =
  | { kind: 'found'; product: Product }
  | { kind: 'not-found'; message: string }
  | { kind: 'error'; message: string };

@Component({
  selector: 'app-product-search',
  standalone: true,
  imports: [CurrencyPipe, FormsModule, MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule, MatProgressBarModule],
  templateUrl: './product-search.component.html',
  styleUrl: './product-search.component.scss',
})
export class ProductSearchComponent {
  private readonly productService = inject(ProductService);
  @ViewChild('searchForm') private searchForm?: NgForm;
  barcode = '';
  readonly loading = signal(false);
  readonly outcome = signal<SearchOutcome | null>(null);

  submit(): void {
    const value = this.barcode.trim();
    if (!value) return;
    this.loading.set(true);
    this.outcome.set(null);
    this.productService.findByBarcode(value)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (product) => this.outcome.set(product
          ? { kind: 'found', product }
          : { kind: 'not-found', message: 'No product matches that barcode.' }),
        error: (error) => this.outcome.set(this.toFailure(error)),
      });
  }

  clear(): void {
    this.barcode = '';
    this.outcome.set(null);
    this.searchForm?.resetForm({ barcode: '' });
  }

  private toFailure(error: { status?: number; error?: { message?: string } }): SearchOutcome {
    const message = error?.error?.message;
    if (error?.status === 404) return { kind: 'not-found', message: message || 'No product matches that barcode.' };
    // 422 (e.g. PRODUCT_PRICE_MISSING) carries a descriptive message from the API
    if (error?.status === 422) return { kind: 'error', message: message || 'This product has no price yet.' };
    return { kind: 'error', message: message || 'Could not search for that barcode.' };
  }
}
