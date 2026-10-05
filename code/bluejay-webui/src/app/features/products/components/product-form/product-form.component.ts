// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Product creation form, category autocomplete and filtering, submit payload and form reset
// Human Contributions: Reported the NG0100 error and the dirty/invalid state after a successful create, defined the category selection rules, and verified the fixes in the browser
// Notes: Async state uses signals; reset() resets the template-driven form so it returns to pristine.
// authors: Kimleng

import { Component, EventEmitter, OnInit, Output, ViewChild, computed, inject, signal } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { FormField, form } from '@angular/forms/signals';
import { MatAutocompleteModule, MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { finalize } from 'rxjs';
import { CreateProduct, ProductCategory } from '../../models/product.model';
import { ProductService } from '../../services/product.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [FormsModule, FormField, MatAutocompleteModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.scss',
})
export class ProductFormComponent implements OnInit {
  private readonly productService = inject(ProductService);
  @ViewChild('productForm') private productForm?: NgForm;
  @Output() created = new EventEmitter<void>();
  readonly saving = signal(false);
  readonly error = signal<string | null>(null);
  readonly categoryError = signal<string | null>(null);
  readonly categories = signal<ProductCategory[]>([]);
  name = '';
  barcode = '';
  private readonly categoryModel = signal({ name: '' });
  readonly categoryForm = form(this.categoryModel);
  private selectedCategory: ProductCategory | null = null;
  categoryDescription = '';

  ngOnInit(): void {
    this.productService.listCategories().subscribe({
      next: (categories) => this.categories.set(categories),
      error: () => this.categoryError.set('Could not load product categories.'),
    });
  }

  readonly filteredCategories = computed(() => {
    const query = this.categoryModel().name.toLocaleLowerCase().trim();
    return this.categories().filter((category) => category.name.toLocaleLowerCase().includes(query));
  });

  displayCategory(value: string | ProductCategory | null): string {
    return typeof value === 'string' ? value : value?.name ?? '';
  }

  onCategoryInput(): void {
    this.selectedCategory = null;
    this.categoryDescription = '';
  }

  selectCategory(event: MatAutocompleteSelectedEvent): void {
    const selectedName = event.option.value as string;
    this.selectedCategory = this.categories().find((category) => category.name === selectedName) ?? null;
    this.categoryModel.update((category) => ({ ...category, name: selectedName }));
    this.categoryDescription = this.selectedCategory?.description || '';
  }

  submit(): void {
    if (!this.name.trim() || !this.barcode.trim()) return;
    const product: CreateProduct = {
      name: this.name.trim(),
      barcode: this.barcode.trim(),
      categoryName: this.selectedCategory?.name ?? this.categoryModel().name.trim(),
      categoryId: this.selectedCategory?.id ?? null,
      categoryDescription: this.categoryDescription.trim() || null,
    };

    this.saving.set(true);
    this.error.set(null);
    this.productService.create(product).pipe(finalize(() => this.saving.set(false))).subscribe({
      next: () => {
        this.reset();
        this.created.emit();
      },
      error: (error) => this.error.set(error?.error?.message || 'Could not create the product.'),
    });
  }

  reset(): void {
    this.name = '';
    this.barcode = '';
    this.productForm?.resetForm({ name: '', barcode: '', categoryDescription: '' });
    this.categoryModel.update((category) => ({ ...category, name: '' }));
    this.selectedCategory = null;
    this.categoryDescription = '';
    this.categoryForm().reset();
  }
}
