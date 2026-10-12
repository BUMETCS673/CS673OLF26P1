// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Component spec with a mocked ProductService covering loading, pagination, error state and rendering
// Human Contributions: Found the ExpressionChangedAfterItHasBeenCheckedError (NG0100) that led to the signal-based state, defined the paginator-length rules and error scenarios, reviewed the assertions, and ran the suite with npm test
// Notes: Includes a regression test that renders the component through a full load cycle with change detection to guard against NG0100.
// authors: Kimleng

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PageEvent } from '@angular/material/paginator';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { Product, ProductPage, ProductQuery } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { ProductListComponent } from './product-list.component';

const product = (n: number): Product => ({
  id: String(n),
  name: `Product ${n}`,
  barcode: `000${n}`,
  categoryId: null,
  categoryName: n % 2 ? 'Dairy' : null,
  price: n,
  lowStock: 5,
});

const page = (items: Product[], totalCount = items.length): ProductPage => ({
  items,
  totalCount,
  pageNumber: 1,
  pageSize: 10,
});

describe('ProductListComponent', { timeout: 15000 }, () => {
  const serviceMock = {
    list: vi.fn(),
    updateLowStock: vi.fn(),
  };
  let fixture: ComponentFixture<ProductListComponent>;
  let component: ProductListComponent;

  const create = async () => {
    fixture = TestBed.createComponent(ProductListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    await fixture.whenStable();
    fixture.detectChanges();
  };

  beforeEach(() => {
    serviceMock.list.mockReset();
    serviceMock.updateLowStock.mockReset();
    serviceMock.list.mockReturnValue(of(page([product(1), product(2)], 2)));
    TestBed.configureTestingModule({ providers: [{ provide: ProductService, useValue: serviceMock }] });
  });

  it('loads the first page on init', async () => {
    await create();

    expect(serviceMock.list).toHaveBeenCalledWith({ pageNumber: 1, pageSize: 10 });
    expect(component.products().length).toBe(2);
    expect(component.loading()).toBe(false);
  });

  it('renders a row for each product', async () => {
    await create();

    const rows = fixture.nativeElement.querySelectorAll('tr[mat-row]');
    expect(rows.length).toBe(2);
    expect(rows[0].textContent).toContain('Product 1');
    expect(rows[1].textContent).toContain('—');
  });

  it('renders without ExpressionChangedAfterItHasBeenChecked errors during a load cycle', async () => {
    await create();
    component.loadPage(2);
    expect(() => fixture.detectChanges()).not.toThrow();
    await fixture.whenStable();
    expect(() => fixture.detectChanges()).not.toThrow();
  });

  it('shows the empty message when there are no products', async () => {
    serviceMock.list.mockReturnValue(of(page([], 0)));
    await create();

    expect(fixture.nativeElement.textContent).toContain('No products found.');
  });

  it('shows an error and clears products when loading fails', async () => {
    serviceMock.list.mockReturnValue(throwError(() => ({ error: { message: 'Server down' } })));
    await create();

    expect(component.products()).toEqual([]);
    expect(component.error()).toBe('Server down');
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain('Server down');
    expect(fixture.nativeElement.textContent).not.toContain('No products found.');
  });

  it('falls back to a default error message', async () => {
    serviceMock.list.mockReturnValue(throwError(() => ({})));
    await create();

    expect(component.error()).toBe('Could not load the product catalog.');
  });

  it('clamps the page number to at least 1', async () => {
    await create();
    component.loadPage(-4);

    expect(component.page()).toBe(1);
    expect(serviceMock.list).toHaveBeenLastCalledWith({ pageNumber: 1, pageSize: 10 });
  });

  it('loads the requested page and size on page change', async () => {
    await create();
    component.changePage({ pageIndex: 2, pageSize: 25, length: 100 } as PageEvent);

    expect(component.pageSize()).toBe(25);
    expect(component.page()).toBe(3);
    expect(serviceMock.list).toHaveBeenLastCalledWith({ pageNumber: 3, pageSize: 25 });
  });

  it('applies shared filters and resets back to the first page', async () => {
    await create();
    component.loadPage(3);
    component.applyFilters({ productName: 'Water', categoryName: 'beverage' } satisfies ProductQuery);

    expect(component.page()).toBe(1);
    expect(serviceMock.list).toHaveBeenLastCalledWith({
      productName: 'Water',
      categoryName: 'beverage',
      pageNumber: 1,
      pageSize: 10,
    });
  });

  it('starts inline edit and sets initial editing low stock value', async () => {
    await create();
    const p = product(1);

    component.startEdit(p);

    expect(component.editingProductId()).toBe('1');
    expect(component.editingLowStock()).toBe(5);
  });

  it('cancels inline edit', async () => {
    await create();
    component.editingProductId.set('1');

    component.cancelEdit();

    expect(component.editingProductId()).toBeNull();
  });

  it('saves updated low stock threshold and updates local products signal', async () => {
    await create();
    const p = product(1);
    component.editingProductId.set('1');
    component.editingLowStock.set(20);

    const updatedProduct = { ...p, lowStock: 20 };
    serviceMock.updateLowStock.mockReturnValue(of(updatedProduct));

    component.saveLowStock(p);

    expect(serviceMock.updateLowStock).toHaveBeenCalledWith('1', 20);
    expect(component.products()[0].lowStock).toBe(20);
    expect(component.editingProductId()).toBeNull();
  });

  it('handles error when saving low stock threshold fails', async () => {
    await create();
    const p = product(1);
    component.editingProductId.set('1');
    component.editingLowStock.set(20);

    serviceMock.updateLowStock.mockReturnValue(throwError(() => ({ error: { message: 'Update failed' } })));

    component.saveLowStock(p);

    expect(component.error()).toBe('Update failed');
    expect(component.loading()).toBe(false);
  });
});
