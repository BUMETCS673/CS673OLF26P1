// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Component spec with a mocked ProductService covering found, not-found, 422 price-missing and generic error outcomes
// Human Contributions: Specified that search results are shown in the search card instead of filtering the product table, supplied the 422 PRODUCT_PRICE_MISSING behavior, reported the dirty-form issue after clear, reviewed the assertions, and ran the suite with npm test
// Notes: The clear test checks that the form returns to a pristine, untouched state.
// authors: Kimleng

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { ProductService } from '../../services/product.service';
import { ProductSearchComponent } from './product-search.component';

describe('ProductSearchComponent', { timeout: 15000 }, () => {
  const serviceMock = { findByBarcode: vi.fn() };
  let fixture: ComponentFixture<ProductSearchComponent>;
  let component: ProductSearchComponent;
  let element: HTMLElement;

  const search = async (barcode = '123') => {
    const input = element.querySelector('input') as HTMLInputElement;
    input.value = barcode;
    input.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    fixture.detectChanges();
    component.submit();
    await fixture.whenStable();
    fixture.detectChanges();
  };

  beforeEach(async () => {
    serviceMock.findByBarcode.mockReset();
    TestBed.configureTestingModule({ providers: [{ provide: ProductService, useValue: serviceMock }] });
    fixture = TestBed.createComponent(ProductSearchComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('shows the found product details', async () => {
    serviceMock.findByBarcode.mockReturnValue(of({
      id: '1', name: 'Milk', barcode: '123', categoryId: 1, categoryName: 'Dairy', price: 2.5,
    }));
    await search();

    expect(serviceMock.findByBarcode).toHaveBeenCalledWith('123');
    expect(element.textContent).toContain('Product found');
    expect(element.textContent).toContain('Milk');
    expect(element.textContent).toContain('Dairy');
    expect(element.textContent).toContain('$2.50');
  });

  it('trims the barcode before searching', async () => {
    serviceMock.findByBarcode.mockReturnValue(of({ id: '1', name: 'Milk', barcode: '123', categoryId: null, categoryName: null, price: 1 }));
    await search('  123  ');

    expect(serviceMock.findByBarcode).toHaveBeenCalledWith('123');
  });

  it('does not search when the barcode is blank', async () => {
    await search('   ');

    expect(serviceMock.findByBarcode).not.toHaveBeenCalled();
    expect(component.outcome()).toBeNull();
  });

  it('shows a not-found message when the API returns 404', async () => {
    serviceMock.findByBarcode.mockReturnValue(throwError(() => ({ status: 404, error: { message: 'No such product' } })));
    await search();

    expect(element.querySelector('.not-found')?.textContent).toContain('No such product');
  });

  it('shows a default not-found message when the API returns an empty result', async () => {
    serviceMock.findByBarcode.mockReturnValue(of(null));
    await search();

    expect(element.querySelector('.not-found')?.textContent).toContain('No product matches that barcode.');
  });

  it('shows the API message for 422 PRODUCT_PRICE_MISSING', async () => {
    serviceMock.findByBarcode.mockReturnValue(throwError(() => ({
      status: 422, error: { code: 'PRODUCT_PRICE_MISSING', message: 'Product has no price yet.' },
    })));
    await search();

    const alert = element.querySelector('[role="alert"]');
    expect(alert?.textContent).toContain('Product has no price yet.');
  });

  it('falls back to a default message for 422 without a body message', async () => {
    serviceMock.findByBarcode.mockReturnValue(throwError(() => ({ status: 422 })));
    await search();

    expect(element.querySelector('[role="alert"]')?.textContent).toContain('This product has no price yet.');
  });

  it('shows a generic error for other failures', async () => {
    serviceMock.findByBarcode.mockReturnValue(throwError(() => ({ status: 500 })));
    await search();

    expect(element.querySelector('[role="alert"]')?.textContent).toContain('Could not search for that barcode.');
  });

  it('clears the previous result when a new search starts', async () => {
    serviceMock.findByBarcode.mockReturnValue(of({ id: '1', name: 'Milk', barcode: '123', categoryId: null, categoryName: null, price: 1 }));
    await search();
    serviceMock.findByBarcode.mockReturnValue(throwError(() => ({ status: 404 })));
    await search('999');

    expect(element.textContent).not.toContain('Product found');
    expect(element.querySelector('.not-found')).not.toBeNull();
  });

  it('clear removes the result, empties the input and resets the form state', async () => {
    serviceMock.findByBarcode.mockReturnValue(throwError(() => ({ status: 404 })));
    const input = element.querySelector('input') as HTMLInputElement;
    input.value = '123';
    input.dispatchEvent(new Event('input'));
    input.dispatchEvent(new Event('blur'));
    await fixture.whenStable();
    fixture.detectChanges();
    component.submit();
    await fixture.whenStable();
    fixture.detectChanges();

    component.clear();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.outcome()).toBeNull();
    expect(input.value).toBe('');
    expect(element.querySelectorAll('.ng-invalid.ng-touched, .ng-invalid.ng-dirty').length).toBe(0);
  });
});
