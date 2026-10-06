// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~100%
// AI-Assisted Areas: Custom validator testing, reactive form submission, output emission spy, and HTTP error status handling
// Human Contributions: Custom API response model alignment and verification
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { FormControl } from '@angular/forms';
import { Observable, of, throwError} from 'rxjs';
import {
  StockEntryFormComponent,
  positiveIntegerValidator
} from './stock-entry-form.component';
import {InventoryService} from '../../services/inventory.service';
import {ApiResponse} from '../../../../shared/models/api-response.model';
import {StockEntry} from '../../models/inventory.model';

describe('StockEntryFormComponent', () => {
  let component: StockEntryFormComponent;
  let fixture: ComponentFixture<StockEntryFormComponent>;
  let inventoryServiceMock: { recordStockEntry: { mockReturnValue: (arg0: Observable<ApiResponse<StockEntry>>) => void; } };

  beforeEach(async () => {
    inventoryServiceMock = {
      recordStockEntry: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [StockEntryFormComponent],
      providers: [
        { provide: InventoryService, useValue: inventoryServiceMock }
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StockEntryFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  describe('positiveIntegerValidator', () => {
    it('should return null for valid positive integers', () => {
      const control = new FormControl(5);
      expect(positiveIntegerValidator(control)).toBeNull();
    });

    it('should return null for empty/null values', () => {
      expect(positiveIntegerValidator(new FormControl(''))).toBeNull();
      expect(positiveIntegerValidator(new FormControl(null))).toBeNull();
    });

    it('should return invalidQuantity for decimals, zero, or negative numbers', () => {
      expect(positiveIntegerValidator(new FormControl(0))).toEqual({ invalidQuantity: true });
      expect(positiveIntegerValidator(new FormControl(-5))).toEqual({ invalidQuantity: true });
      expect(positiveIntegerValidator(new FormControl(1.5))).toEqual({ invalidQuantity: true });
    });
  });

  describe('onSubmit', () => {
    it('should set error message if quantity control is invalid', () => {
      component.stockForm.patchValue({ barcode: '1001', quantity: -1 });

      component.onSubmit();

      expect(component.errorMessage()).toBe('Invalid quantity');
      expect(inventoryServiceMock.recordStockEntry).not.toHaveBeenCalled();
    });

    it('should set error message if required barcode field is missing', () => {
      component.stockForm.patchValue({ barcode: '', quantity: 5 });

      component.onSubmit();

      expect(component.errorMessage()).toBe('Please fill out all required fields correctly.');
      expect(inventoryServiceMock.recordStockEntry).not.toHaveBeenCalled();
    });

    it('should submit valid form, emit created output, and reset form', () => {
      const createdSpy = vi.fn();
      component.created.subscribe(createdSpy);

      component.stockForm.patchValue({ barcode: '1001', cost: 12.50, quantity: 10 });

      const mockData: StockEntry = {
        movementId: 1,
        productId: 'prod-1',
        barcode: '1001',
        productName: 'Coffee',
        quantityAdded: 10,
        newTotalStock: 60,
        costPrice: 12.50,
        userId: 'user-1',
        createdAt: '2026-10-05T12:00:00Z',
        message: 'Coffee stock entry recorded.'
      };
      const mockResponse: ApiResponse<StockEntry> = {
        success: true,
        message: 'Coffee stock entry recorded.',
        data: mockData,
        errorCode: null,
        timestamp: '2026-10-05T12:00:00Z'
      };

      inventoryServiceMock.recordStockEntry.mockReturnValue(of(mockResponse));

      component.onSubmit();

      expect(inventoryServiceMock.recordStockEntry).toHaveBeenCalledWith({
        barcode: '1001',
        quantity: 10,
        cost: 12.50
      });
      expect(component.isSubmitting()).toBe(false);
      expect(component.successMessage()).toBe('Coffee stock entry recorded.');
      expect(createdSpy).toHaveBeenCalledWith(mockData);
      expect(component.stockForm.getRawValue().barcode).toBe('');
    });

    it('should handle 404 Product Not Found error correctly', () => {
      component.stockForm.patchValue({ barcode: '9999', quantity: 5 });

      const errorResponse = new HttpErrorResponse({ status: 404 });
      inventoryServiceMock.recordStockEntry.mockReturnValue(throwError(() => errorResponse));

      component.onSubmit();

      expect(component.isSubmitting()).toBe(false);
      expect(component.errorMessage()).toBe('Product not found in catalog.');
    });

    it('should handle custom ApiResponse error payload', () => {
      component.stockForm.patchValue({ barcode: '1001', quantity: 5 });

      const errorPayload: ApiResponse<void> = {
        success: false,
        message: 'Custom API Error',
        data: null,
        errorCode: 'BAD_REQUEST',
        timestamp: '2026-10-05T12:00:00Z'
      };

      const errorResponse = new HttpErrorResponse({
        error: errorPayload,
        status: 400
      });
      inventoryServiceMock.recordStockEntry.mockReturnValue(throwError(() => errorResponse));

      component.onSubmit();

      expect(component.errorMessage()).toBe('Custom API Error');
    });
  });
});
