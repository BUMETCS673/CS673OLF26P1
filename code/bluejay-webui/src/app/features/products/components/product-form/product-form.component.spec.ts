// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Component spec with a mocked ProductService covering category loading, filtering, submit payloads, error handling and form reset
// Human Contributions: Reported the dirty/invalid state after a successful create and the NG0100 error on this component, defined the category selection rules, reviewed the assertions, and ran the suite with npm test
// Notes: Includes a regression test that the form is pristine and untouched after a successful create.
// authors: Kimleng

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatAutocompleteSelectedEvent } from '@angular/material/autocomplete';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { ProductService } from '../../services/product.service';
import { ProductFormComponent } from './product-form.component';

const categories = [
  { id: 1, name: 'Dairy', description: 'Milk and cheese' },
  { id: 2, name: 'Bakery', description: null },
];

describe('ProductFormComponent', { timeout: 15000 }, () => {
  const serviceMock = { listCategories: vi.fn(), create: vi.fn() };
  let fixture: ComponentFixture<ProductFormComponent>;
  let component: ProductFormComponent;
  let element: HTMLElement;

  const settle = async () => {
    await fixture.whenStable();
    fixture.detectChanges();
  };

  const create = async () => {
    fixture = TestBed.createComponent(ProductFormComponent);
    component = fixture.componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
    await settle();
  };

  const type = async (name: string, value: string) => {
    const input = element.querySelector(
      name === 'categoryName' ? 'input[placeholder^="Type or choose"]' : `input[name=${name}]`,
    ) as HTMLInputElement;
    input.value = value;
    input.dispatchEvent(new Event('input'));
    input.dispatchEvent(new Event('blur'));
    await settle();
  };

  const select = (name: string) =>
    component.selectCategory({ option: { value: name } } as MatAutocompleteSelectedEvent);

  beforeEach(() => {
    serviceMock.listCategories.mockReset().mockReturnValue(of(categories));
    serviceMock.create.mockReset().mockReturnValue(of({ id: '1' }));
    TestBed.configureTestingModule({ providers: [{ provide: ProductService, useValue: serviceMock }] });
  });

  it('loads categories on init', async () => {
    await create();

    expect(component.categories()).toEqual(categories);
    expect(component.categoryError()).toBeNull();
  });

  it('shows an error when categories cannot be loaded', async () => {
    serviceMock.listCategories.mockReturnValue(throwError(() => new Error('fail')));
    await create();

    expect(component.categoryError()).toBe('Could not load product categories.');
    expect(element.textContent).toContain('Could not load product categories.');
  });

  it('filters categories by the typed name, ignoring case', async () => {
    await create();
    await type('categoryName', 'DAI');

    expect(component.filteredCategories().map((c) => c.name)).toEqual(['Dairy']);
  });

  it('fills the description when an existing category is selected', async () => {
    await create();
    select('Dairy');

    expect(component.categoryDescription).toBe('Milk and cheese');
  });

  it('keeps the submit button disabled until name and barcode are filled', async () => {
    await create();
    const button = element.querySelector('button[type=submit]') as HTMLButtonElement;
    expect(button.disabled).toBe(true);

    await type('name', 'Milk');
    expect(button.disabled).toBe(true);
    await type('barcode', '123');
    expect(button.disabled).toBe(false);
  });

  it('does not call the API when name or barcode is blank', async () => {
    await create();
    component.name = '  ';
    component.barcode = '123';
    component.submit();

    expect(serviceMock.create).not.toHaveBeenCalled();
  });

  it('submits a trimmed product with the selected existing category', async () => {
    await create();
    component.name = ' Milk ';
    component.barcode = ' 123 ';
    select('Dairy');
    component.submit();

    expect(serviceMock.create).toHaveBeenCalledWith({
      name: 'Milk',
      barcode: '123',
      categoryName: 'Dairy',
      categoryId: 1,
      categoryDescription: 'Milk and cheese',
    });
  });

  it('submits a new category by name with a null id', async () => {
    await create();
    await type('categoryName', 'Snacks');
    component.name = 'Chips';
    component.barcode = '555';
    component.categoryDescription = ' Crunchy ';
    component.submit();

    expect(serviceMock.create).toHaveBeenCalledWith({
      name: 'Chips',
      barcode: '555',
      categoryName: 'Snacks',
      categoryId: null,
      categoryDescription: 'Crunchy',
    });
  });

  it('forgets the selected category once the user edits the category input', async () => {
    await create();
    select('Dairy');
    component.onCategoryInput();

    expect(component.categoryDescription).toBe('');
  });

  it('emits created and resets the fields after a successful create', async () => {
    await create();
    const created = vi.fn();
    component.created.subscribe(created);
    await type('name', 'Milk');
    await type('barcode', '123');
    component.submit();
    await settle();

    expect(created).toHaveBeenCalledTimes(1);
    expect(component.name).toBe('');
    expect(component.barcode).toBe('');
    expect(component.saving()).toBe(false);
  });

  it('leaves the form pristine and untouched after a successful create', async () => {
    await create();
    await type('name', 'Milk');
    await type('barcode', '123');
    component.submit();
    await settle();
    await settle();

    expect(element.querySelectorAll('.ng-invalid.ng-touched, .ng-invalid.ng-dirty').length).toBe(0);
    expect((element.querySelector('input[name=name]') as HTMLInputElement).value).toBe('');
  });

  it('shows the API error and keeps the input when create fails', async () => {
    serviceMock.create.mockReturnValue(throwError(() => ({ error: { message: 'Duplicate barcode' } })));
    await create();
    const created = vi.fn();
    component.created.subscribe(created);
    component.name = 'Milk';
    component.barcode = '123';
    component.submit();
    await settle();

    expect(component.error()).toBe('Duplicate barcode');
    expect(element.querySelector('[role="alert"]')?.textContent).toContain('Duplicate barcode');
    expect(component.name).toBe('Milk');
    expect(created).not.toHaveBeenCalled();
    expect(component.saving()).toBe(false);
  });

  it('falls back to a default error message', async () => {
    serviceMock.create.mockReturnValue(throwError(() => ({})));
    await create();
    component.name = 'Milk';
    component.barcode = '123';
    component.submit();

    expect(component.error()).toBe('Could not create the product.');
  });
});
