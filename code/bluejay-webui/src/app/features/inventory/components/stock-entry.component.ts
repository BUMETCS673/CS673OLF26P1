// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Refactored component imports to replace CommonModule with DecimalPipe for modern Angular standalone setup.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import {Component, inject, signal} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from '@angular/forms';
import {HttpErrorResponse} from '@angular/common/http';
import {InventoryService} from '../services/inventory.service';
import {
  RecentStockEntryItem,
  StockEntryResponse
} from '../models/stock-entry.model';
import {ApiResponse} from '../../../shared/models/api-response.model';


// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create positive integer validator to block negative numbers, zero, and decimals for AC 2."
// AI Contribution: Initial draft (~100%)
// Modifications: Used regex check to enforce positive whole numbers.
// Verification: Angular form control unit test.
// Confidence: High
export function positiveIntegerValidator(control: AbstractControl): ValidationErrors | null {
  if (control.value === null || control.value === undefined || control.value === '') {
    return null;
  }
  const isPositiveInteger = Number.isInteger(control.value) && Number(control.value) > 0;
  return isPositiveInteger ? null : {invalidQuantity: true};
}

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Refactor StockEntryComponent to remove legacy CommonModule and import DecimalPipe directly for modern built-in control flow."
// AI Contribution: Initial draft (~100%)
// Modifications: Updated imports array for standalone component compliance.
// Verification: Angular compiler type checking.
// Confidence: High
@Component({
  selector: 'app-stock-entry',
  standalone: true,
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './stock-entry.component.html',
  styleUrls: ['./stock-entry.component.css']
})
export class StockEntryComponent {
  private readonly fb = inject(FormBuilder);
  private readonly inventoryService = inject(InventoryService);

  // Component Signals
  isFormOpen = signal<boolean>(true);
  isSubmitting = signal<boolean>(false);
  successMessage = signal<string | null>('Brown Sugar 1kg stock entry recorded.');
  errorMessage = signal<string | null>(null);

  // Initial table state matching prototype layout
  recentEntries = signal<RecentStockEntryItem[]>([
    {
      barcode: '1001004',
      productName: 'Brown Sugar 1kg',
      cost: 10.75,
      quantity: 100
    },
    {
      barcode: '1001001',
      productName: 'Premium Rice 5kg',
      cost: 2.25,
      quantity: 10
    }
  ]);

  // Reactive Form Initialization
  stockForm = this.fb.group({
    barcode: ['', [Validators.required]],
    cost: [0.00, [Validators.min(0)]],
    quantity: [1, [Validators.required, positiveIntegerValidator]]
  });

  toggleForm(): void {
    this.isFormOpen.update(val => !val);
  }

  onSubmit(): void {
    this.errorMessage.set(null);
    this.successMessage.set(null);

    // AC 2: Client-side Quantity Validation Check
    if (this.stockForm.get('quantity')?.invalid) {
      this.errorMessage.set('Invalid quantity');
      return;
    }

    if (this.stockForm.invalid) {
      this.errorMessage.set('Please fill out all required fields correctly.');
      return;
    }

    const {barcode, cost, quantity} = this.stockForm.getRawValue();

    this.isSubmitting.set(true);

    this.inventoryService.recordStockEntry({
      barcode: barcode!,
      quantity: Number(quantity),
      cost: cost ? Number(cost) : undefined
    }).subscribe({
      next: (response: ApiResponse<StockEntryResponse>) => {
        this.isSubmitting.set(false);
        if (response.success && response.data) {
          const data = response.data;
          // AC 3: Display success feedback and update table
          this.successMessage.set(`${data.productName} stock entry recorded.`);

          this.recentEntries.update(entries => [
            {
              barcode: data.barcode,
              productName: data.productName,
              cost: data.costPrice,
              quantity: data.quantityAdded
            },
            ...entries
          ]);

          // Reset inputs keeping default values
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

        // AC 1 & AC 2: Display backend rejection messages
        if (apiError && apiError.message) {
          this.errorMessage.set(apiError.message);
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
