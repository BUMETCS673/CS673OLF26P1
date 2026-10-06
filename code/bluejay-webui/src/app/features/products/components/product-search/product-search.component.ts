// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~75%
// AI-Assisted Areas: Filter form component with product-name/category inputs,
// category loading and list-filter event emission
// Human Contributions: Replaced barcode lookup with shared list filtering so
// pagination remains consistent, specified category dropdown behavior, and
// verified reset behavior
// Notes: Emits filters to the list instead of querying products directly.
// authors: Kimleng

import { Component, EventEmitter, OnInit, Output, ViewChild, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { ProductCategory, ProductQuery } from '../../models/product.model';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-product-search',
  standalone: true,
  imports: [FormsModule, MatButtonModule, MatFormFieldModule, MatIconModule, MatInputModule, MatProgressBarModule, MatSelectModule],
  templateUrl: './product-search.component.html',
  styleUrl: './product-search.component.scss',
})
export class ProductSearchComponent implements OnInit {
  private readonly productService = inject(ProductService);
  @ViewChild('searchForm') private searchForm?: NgForm;
  @Output() search = new EventEmitter<ProductQuery>();
  productName = '';
  categoryName = '';
  readonly loading = signal(false);
  readonly categoryError = signal<string | null>(null);
  readonly categories = signal<ProductCategory[]>([]);

  ngOnInit(): void {
    this.loading.set(true);
    this.categoryError.set(null);
    this.productService.listCategories()
      .subscribe({
        next: (categories) => {
          this.categories.set(categories);
          this.loading.set(false);
        },
        error: () => {
          this.categoryError.set('Could not load product categories.');
          this.loading.set(false);
        },
      });
  }

  submit(): void {
    this.search.emit({
      productName: this.productName.trim() || null,
      categoryName: this.categoryName.trim() || null,
    });
  }

  clear(): void {
    this.productName = '';
    this.categoryName = '';
    this.searchForm?.resetForm({ productName: '', categoryName: '' });
    this.search.emit({});
  }
}
