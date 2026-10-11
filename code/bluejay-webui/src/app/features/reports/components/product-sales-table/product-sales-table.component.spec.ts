// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Component tests for rendering and sorting
// Human Contributions: Story #30 scenarios (multiple products, zero sales, sort by profit/quantity)
// Notes: Rendering and sorting tests for the product sales table.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { ProductSalesTableComponent } from './product-sales-table.component';
import { ProductSalesItem } from '../../models/product-sales.model';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Component tests for the sortable product sales table"
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Reads row order from the rendered table to check sorting
// Verification:
// - npm test
// Confidence: High
describe('ProductSalesTableComponent', () => {
  const products: ProductSalesItem[] = [
    { productId: 'p1', barcode: '111', name: 'Cooking Oil 2L', quantitySold: 5, totalRevenue: 32.5, totalCost: 20.5, profit: 12 },
    { productId: 'p2', barcode: '222', name: 'Premium Rice 5kg', quantitySold: 2, totalRevenue: 29.98, totalCost: 19.98, profit: 10 },
    { productId: 'p3', barcode: '333', name: 'Bread', quantitySold: 8, totalRevenue: 6, totalCost: 9, profit: -3 },
    { productId: 'p4', barcode: null, name: 'Lemons', quantitySold: 0, totalRevenue: 0, totalCost: 0, profit: 0 },
  ];

  function render(items: ProductSalesItem[]) {
    TestBed.configureTestingModule({ imports: [ProductSalesTableComponent] });
    const fixture = TestBed.createComponent(ProductSalesTableComponent);
    fixture.componentRef.setInput('products', items);
    fixture.detectChanges();
    const el = fixture.nativeElement as HTMLElement;
    const names = () =>
      Array.from(el.querySelectorAll('tbody tr td:first-child')).map((td) => td.textContent?.trim());
    const click = (testId: string) => {
      (el.querySelector(`[data-testid="${testId}"]`) as HTMLButtonElement).click();
      fixture.detectChanges();
    };
    return { el, names, click };
  }

  it('shows one row per product with quantity, cost and profit', () => {
    const { el } = render(products);
    const rows = el.querySelectorAll('tbody tr');

    expect(rows.length).toBe(4);
    expect(rows[0].textContent).toContain('Cooking Oil 2L');
    expect(rows[0].textContent).toContain('5');
    expect(rows[0].textContent).toContain('$20.50');
    expect(rows[0].textContent).toContain('$12.00');
  });

  it('sorts by profit, highest first, by default', () => {
    const { names } = render(products);

    expect(names()).toEqual(['Cooking Oil 2L', 'Premium Rice 5kg', 'Lemons', 'Bread']);
  });

  it('sorts by quantity sold when that header is clicked, and flips on a second click', () => {
    const { names, click } = render(products);

    click('sort-quantity');
    expect(names()).toEqual(['Bread', 'Cooking Oil 2L', 'Premium Rice 5kg', 'Lemons']);

    click('sort-quantity');
    expect(names()).toEqual(['Lemons', 'Premium Rice 5kg', 'Cooking Oil 2L', 'Bread']);
  });

  it('flips profit to lowest first when Profit is clicked while already sorted by profit', () => {
    const { names, click } = render(products);

    click('sort-profit');

    expect(names()).toEqual(['Bread', 'Lemons', 'Premium Rice 5kg', 'Cooking Oil 2L']);
  });

  it('dims products with zero sales and marks losses in red', () => {
    const { el } = render(products);

    expect(el.querySelectorAll('tr.no-sales').length).toBe(1);
    expect(el.querySelector('tr.no-sales')?.textContent).toContain('Lemons');
    expect(el.querySelectorAll('td.text-danger').length).toBe(1);
  });

  it('shows a message for an empty catalog', () => {
    const { el } = render([]);

    expect(el.querySelector('table')).toBeNull();
    expect(el.textContent).toContain('No products in the catalog yet.');
  });
});