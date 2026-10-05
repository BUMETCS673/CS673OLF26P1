// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~75%
// AI-Assisted Areas: Component spec with mocked category loading and emitted
// product-name/category filters
// Human Contributions: Replaced barcode search with shared list filtering,
// required category dropdown coverage, reviewed the emitted filter assertions,
// and kept the clear-state regression
// Notes: The clear test checks that the form returns to a pristine, untouched
// state and emits an empty filter payload.
// authors: Kimleng

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { ProductService } from '../../services/product.service';
import { ProductSearchComponent } from './product-search.component';

describe('ProductSearchComponent', { timeout: 15000 }, () => {
  const serviceMock = { listCategories: vi.fn() };
  let fixture: ComponentFixture<ProductSearchComponent>;
  let component: ProductSearchComponent;
  let element: HTMLElement;
  const emitSpy = vi.fn();

  const apply = async (productName = '', categoryName = '') => {
    const input = element.querySelector('input') as HTMLInputElement;
    const select = element.querySelector('mat-select') as HTMLElement;
    input.value = productName;
    input.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    fixture.detectChanges();
    component.categoryName = categoryName;
    select.dispatchEvent(new Event('selectionChange'));
    component.submit();
    await fixture.whenStable();
    fixture.detectChanges();
  };

  beforeEach(async () => {
    emitSpy.mockReset();
    serviceMock.listCategories.mockReset();
    serviceMock.listCategories.mockReturnValue(of([
      { id: 1, name: 'beverage', description: null },
      { id: 2, name: 'fruit', description: null },
    ]));
    TestBed.configureTestingModule({ providers: [{ provide: ProductService, useValue: serviceMock }] });
    fixture = TestBed.createComponent(ProductSearchComponent);
    component = fixture.componentInstance;
    component.search.subscribe(emitSpy);
    element = fixture.nativeElement;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  });

  it('loads categories on init', () => {
    expect(serviceMock.listCategories).toHaveBeenCalled();
    expect(component.categories().map((category) => category.name)).toEqual(['beverage', 'fruit']);
  });

  it('emits trimmed product-name and category filters on submit', async () => {
    await apply('  Water  ', 'beverage');

    expect(emitSpy).toHaveBeenCalledWith({ productName: 'Water', categoryName: 'beverage' });
  });

  it('emits null filters when both fields are blank', async () => {
    await apply('   ', '');

    expect(emitSpy).toHaveBeenCalledWith({ productName: null, categoryName: null });
  });

  it('shows an error when categories fail to load', async () => {
    serviceMock.listCategories.mockReset();
    serviceMock.listCategories.mockReturnValue(throwError(() => ({ status: 500 })));
    fixture = TestBed.createComponent(ProductSearchComponent);
    component = fixture.componentInstance;
    component.search.subscribe(emitSpy);
    element = fixture.nativeElement;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(element.querySelector('[role="alert"]')?.textContent).toContain('Could not load product categories.');
  });

  it('clear empties the filters, resets the form state and emits empty filters', async () => {
    const input = element.querySelector('input') as HTMLInputElement;
    input.value = 'Water';
    input.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    fixture.detectChanges();
    component.categoryName = 'beverage';
    component.submit();

    component.clear();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(component.productName).toBe('');
    expect(component.categoryName).toBe('');
    expect(input.value).toBe('');
    expect(emitSpy).toHaveBeenLastCalledWith({});
    expect(element.querySelectorAll('.ng-invalid.ng-touched, .ng-invalid.ng-dirty').length).toBe(0);
  });
});
