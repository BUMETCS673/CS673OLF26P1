// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Reactive stock entry form setup, custom validation, and POST submission handling
// Human Contributions: Validation requirements, quantity check rules, and event emitter setup
// authors: Sara Orion

import { Component, inject, signal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { InventoryService } from '../../services/inventory.service';
import { ApiResponse } from '../../../../shared/models/api-response.model';
import { StockEntry } from '../../models/inventory.model';

export function positiveIntegerValidator(control: AbstractControl): ValidationErrors | null {
  if (control.value === null || control.value === undefined || control.value === '') {
    return null;
  }
  return Number.isInteger(control.value) && Number(control.value) > 0 ? null : { invalidQuantity: true };
}

@Component({
  selector: 'app-stock-entry-form',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: 'stock-entry-form.component.html',
  styleUrls: ['../inventory.component.scss']
})
export class StockEntryFormComponent {
  private readonly fb = inject(FormBuilder);
  private readonly inventoryService = inject(InventoryService);

  created = output<StockEntry>();

  isSubmitting = signal<boolean>(false);
  successMessage = signal<string | null>(null);
  errorMessage = signal<string | null>(null);

  stockForm = this.fb.group({
    barcode: ['', [Validators.required]],
    cost: [0.00, [Validators.min(0)]],
    quantity: [1, [Validators.required, positiveIntegerValidator]]
  });

  onSubmit(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    if (this.stockForm.get('quantity')?.invalid) {
      this.errorMessage.set('Invalid quantity');
      return;
    }

    if (this.stockForm.invalid) {
      this.errorMessage.set('Please fill out all required fields correctly.');
      return;
    }

    const { barcode, cost, quantity } = this.stockForm.getRawValue();
    this.isSubmitting.set(true);

    this.inventoryService.recordStockEntry({
      barcode: barcode!,
      quantity: Number(quantity),
      cost: cost ? Number(cost) : undefined
    }).subscribe({
      next: (response: ApiResponse<StockEntry>) => {
        this.isSubmitting.set(false);
        if (response.success && response.data) {
          const creationResult = response.data;
          this.successMessage.set(creationResult.message);

          // Emit created event to trigger refreshes on siblings via the page container
          this.created.emit(creationResult);

          this.stockForm.patchValue({
            barcode: '',
            cost: 0.00,
            quantity: 1
          });
          this.stockForm.markAsUntouched();
        }
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        const apiError = error.error as ApiResponse<void>;
        if (apiError && apiError.message) {
          this.errorMessage.set(String(apiError.message));
        } else if (error.status === 404) {
          this.errorMessage.set('Product not found in catalog.');
        } else if (error.status === 400) {
          this.errorMessage.set('Invalid quantity');
        } else {
          this.errorMessage.set('An error occurred while processing stock entry.');
        }
      }
    });
  }
}
