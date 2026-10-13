/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 90%
    AI-Assisted Areas: Added front end unit tests for the sales page
    Human Contributions: Prompted what tests to cover and how to generate the data
    Notes: The unit tests for sales page
    Authors: Italia Tran
*/

import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Product } from '../../products/models/product.model';
import { SalesPage } from './sales-page.component';

const CART_STORAGE_KEY = 'bluejay-sales-cart';

describe('SalesPage', () => {
  let httpTestingController: HttpTestingController;

  beforeEach(async () => {
    sessionStorage.removeItem(CART_STORAGE_KEY);
    await TestBed.configureTestingModule({
      imports: [SalesPage],
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    sessionStorage.removeItem(CART_STORAGE_KEY);
  });

  it('creates the component', () => {
    const fixture = TestBed.createComponent(SalesPage);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('renders the mockup content', () => {
    const fixture = TestBed.createComponent(SalesPage);
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;
    expect(element.textContent).toContain('Sales Terminal');
    expect(element.textContent).toContain('No items in the cart yet.');
    expect(element.textContent).toContain('GRAND TOTAL');
  });

  it('searches by text or barcode and renders matching products', async () => {
    const fixture = TestBed.createComponent(SalesPage);
    fixture.componentInstance.onSearchTermChange('Water');
    await new Promise((resolve) => setTimeout(resolve, 300));

    const request = httpTestingController.expectOne((r) =>
      r.url === '/api/v1/product' && r.params.get('searchTerm') === 'Water'
    );
    request.flush({
      data: {
        items: [{
          id: 'product-1',
          name: 'Spring Water',
          barcode: '12345',
          categoryId: 1,
          categoryName: 'beverages',
          price: 2.5,
          currentStock: 5,
          lowStock: 1
        } satisfies Product],
        totalCount: 1,
        pageNumber: 1,
        pageSize: 10,
      },
    });
    fixture.detectChanges();

    const content = (fixture.nativeElement as HTMLElement).textContent;
    expect(content).toContain('Spring Water');
    expect(content).toContain('12345');
  });

  it('shows an empty result state when the search has no matches', async () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    page.onSearchTermChange('unknown product');
    await new Promise((resolve) => setTimeout(resolve, 300));

    httpTestingController.expectOne((r) =>
      r.url === '/api/v1/product' && r.params.get('searchTerm') === 'unknown product'
    ).flush(
      { success: false, message: 'Product not found', data: null, errorCode: 'PRODUCT_NOT_FOUND', timestamp: '' },
      { status: 404, statusText: 'Not Found' },
    );
    fixture.detectChanges();

    expect(page.searchResults()).toEqual([]);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('No matching products.');
  });

  it('adds a matching product to the cart using the selected quantity', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1',
      name: 'Spring Water',
      barcode: '12345',
      categoryId: 1,
      categoryName: 'beverages',
      price: 2.5,
      currentStock: 5,
      lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    fixture.detectChanges();

    expect(page.cartItems()).toEqual([{ product, quantity: 2 }]);
    expect(page.cartTotal()).toBe(5);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Spring Water');
  });

  it('restores the cart after the Sales page is recreated', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1',
      name: 'Spring Water',
      barcode: '12345',
      categoryId: 1,
      categoryName: 'beverages',
      price: 2.5,
      currentStock: 5,
      lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    fixture.destroy();

    const restoredFixture = TestBed.createComponent(SalesPage);
    restoredFixture.detectChanges();

    expect(restoredFixture.componentInstance.cartItems()).toEqual([{ product, quantity: 2 }]);
    expect(restoredFixture.componentInstance.cartTotal()).toBe(5);
    expect(sessionStorage.getItem(CART_STORAGE_KEY)).not.toBeNull();
  });

  it('removes a product from the cart and updates the grand total', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1',
      name: 'Spring Water',
      barcode: '12345',
      categoryId: 1,
      categoryName: 'beverages',
      price: 2.5,
      currentStock: 5,
      lowStock: 1
    };
    page.addToCart(product);
    fixture.detectChanges();

    const removeButton = fixture.nativeElement.querySelector(
      'button[aria-label="Remove Spring Water from cart"]',
    ) as HTMLButtonElement;
    removeButton.click();
    fixture.detectChanges();

    expect(page.cartItems()).toEqual([]);
    expect(page.cartTotal()).toBe(0);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('No items in the cart yet.');
  });

  it('rejects a quantity greater than available stock', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1',
      name: 'Spring Water',
      barcode: '12345',
      categoryId: 1,
      categoryName: 'beverages',
      price: 2.5,
      currentStock: 3,
      lowStock: 1
    };
    page.quantity = 4;
    page.addToCart(product);
    fixture.detectChanges();

    expect(page.cartItems()).toEqual([]);
    expect(page.cartTotal()).toBe(0);
    expect(page.cartError()).toContain('Only 3');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Only 3 more Spring Water available.');
  });

  it('rejects additions that would make cart quantity exceed available stock', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1',
      name: 'Spring Water',
      barcode: '12345',
      categoryId: 1,
      categoryName: 'beverages',
      price: 2.5,
      currentStock: 3,
      lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    page.addToCart(product);

    expect(page.cartItems()).toEqual([{ product, quantity: 2 }]);
    expect(page.cartError()).toBe('Only 1 more Spring Water available.');
    expect(page.cartTotal()).toBe(5);
  });

  it('shows the additional amount needed when cash received is insufficient', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1', name: 'Spring Water', barcode: '12345', categoryId: 1,
      categoryName: 'beverages', price: 2.5, currentStock: 5, lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    page.cashReceived.set('4.99');
    fixture.detectChanges();

    expect(page.cartTotalCents()).toBe(500);
    expect(page.changeDueCents()).toBe(0);
    expect(page.cashShortfallCents()).toBe(1);
    expect(page.paymentState()).toBe('insufficient');
    const element = fixture.nativeElement as HTMLElement;
    expect(element.textContent).toContain('Additional amount needed: $0.01');
    expect(element.querySelector('.payment-error')).not.toBeNull();
    expect((element.querySelector('.pay-button') as HTMLButtonElement).disabled).toBe(true);
  });

  it('shows zero change when cash received exactly matches the total', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1', name: 'Spring Water', barcode: '12345', categoryId: 1,
      categoryName: 'beverages', price: 2.5, currentStock: 5, lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    page.cashReceived.set('5.00');

    expect(page.changeDueCents()).toBe(0);
    expect(page.paymentState()).toBe('exact');
  });

  it('disables Pay until a non-empty cart has sufficient valid cash', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1', name: 'Spring Water', barcode: '12345', categoryId: 1,
      categoryName: 'beverages', price: 2.5, currentStock: 5, lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    fixture.detectChanges();

    const payButton = fixture.nativeElement.querySelector('.pay-button') as HTMLButtonElement;
    expect(payButton.disabled).toBe(true);

    page.cashReceived.set('4.99');
    fixture.detectChanges();
    expect(payButton.disabled).toBe(true);

    page.cashReceived.set('5.00');
    fixture.detectChanges();
    expect(payButton.disabled).toBe(false);

    page.cashReceived.set('invalid');
    fixture.detectChanges();
    expect(payButton.disabled).toBe(true);
  });

  it('returns the fewest US bills and coins for overpayment', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'product-1', name: 'Spring Water', barcode: '12345', categoryId: 1,
      categoryName: 'beverages', price: 2.5, currentStock: 5, lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    page.cashReceived.set('18.67');
    fixture.detectChanges();

    expect(page.changeDueCents()).toBe(1367);
    expect(page.paymentState()).toBe('change');
    expect(page.changeBreakdown()).toEqual([
      { valueCents: 1000, label: '$10 bill', count: 1 },
      { valueCents: 100, label: '$1 bill', count: 3 },
      { valueCents: 25, label: 'quarter', count: 2 },
      { valueCents: 10, label: 'dime', count: 1 },
      { valueCents: 5, label: 'nickel', count: 1 },
      { valueCents: 1, label: 'penny', count: 2 },
    ]);
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('1 x $10 bill');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('2 x penny');
  });

  it('rejects cash amounts with more than two decimal places', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    page.cashReceived.set('10.001');

    expect(page.cashReceivedCents()).toBeNull();
    expect(page.paymentState()).toBe('invalid');
  });

  it('executes checkout when Pay is clicked, clears the cart, and displays the receipt', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'prod-101',
      name: 'Spring Water',
      barcode: '12345',
      categoryId: 1,
      categoryName: 'beverages',
      price: 2.5,
      currentStock: 10,
      lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    page.cashReceived.set('10.00');
    fixture.detectChanges();

    const payButton = fixture.nativeElement.querySelector('.pay-button') as HTMLButtonElement;
    expect(payButton.disabled).toBe(false);
    payButton.click();
    fixture.detectChanges();

    const request = httpTestingController.expectOne('/api/v1/sales');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      items: [{ productId: 'prod-101', quantity: 2 }],
      cashReceived: 10,
    });

    request.flush({
      success: true,
      message: 'Sale transaction completed successfully',
      data: {
        transactionId: 'txn-999',
        receiptNumber: 'BJ-RECEIPT-999',
        transactionDate: '2026-10-12T00:00:00Z',
        grandTotal: 5.0,
        cashReceived: 10.0,
        changeDue: 5.0,
        paymentMethod: 'CASH',
        items: [
          {
            productId: 'prod-101',
            productName: 'Spring Water',
            quantity: 2,
            unitPrice: 2.5,
            subtotal: 5.0,
          },
        ],
        changeBreakdown: [{ valueCents: 500, label: '$5 bill', count: 1 }],
      },
      errorCode: null,
      timestamp: '2026-10-12T00:00:00Z',
    });
    fixture.detectChanges();

    expect(page.cartItems()).toEqual([]);
    expect(page.cashReceived()).toBe('');
    expect(page.completedSale()?.receiptNumber).toBe('BJ-RECEIPT-999');

    const content = (fixture.nativeElement as HTMLElement).textContent;
    expect(content).toContain('Sale Complete!');
    expect(content).toContain('BJ-RECEIPT-999');
    expect(content).toContain('No items in the cart yet.');
  });

  it('handles checkout errors and displays the error message', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    const product: Product = {
      id: 'prod-101',
      name: 'Spring Water',
      barcode: '12345',
      categoryId: 1,
      categoryName: 'beverages',
      price: 2.5,
      currentStock: 10,
      lowStock: 1
    };
    page.quantity = 2;
    page.addToCart(product);
    page.cashReceived.set('10.00');
    fixture.detectChanges();

    page.onPay();
    fixture.detectChanges();

    const request = httpTestingController.expectOne('/api/v1/sales');
    request.flush(
      {
        success: false,
        message: 'Insufficient product stock for requested quantity',
        data: null,
        errorCode: 'INSUFFICIENT_STOCK',
        timestamp: '2026-10-12T00:00:00Z',
      },
      { status: 409, statusText: 'Conflict' },
    );
    fixture.detectChanges();

    expect(page.cartItems().length).toBe(1);
    expect(page.checkoutError()).toBe('Insufficient product stock for requested quantity');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Insufficient product stock');
  });

  it('dismisses the receipt when the dismiss button is clicked', () => {
    const fixture = TestBed.createComponent(SalesPage);
    const page = fixture.componentInstance;
    page.completedSale.set({
      transactionId: 'txn-999',
      receiptNumber: 'BJ-RECEIPT-999',
      transactionDate: '2026-10-12T00:00:00Z',
      grandTotal: 5.0,
      cashReceived: 10.0,
      changeDue: 5.0,
      paymentMethod: 'CASH',
      items: [],
      changeBreakdown: [],
    });
    fixture.detectChanges();

    expect((fixture.nativeElement as HTMLElement).textContent).toContain('BJ-RECEIPT-999');

    const dismissButton = fixture.nativeElement.querySelector('.dismiss-receipt-button') as HTMLButtonElement;
    dismissButton.click();
    fixture.detectChanges();

    expect(page.completedSale()).toBeNull();
    expect((fixture.nativeElement as HTMLElement).textContent).not.toContain('BJ-RECEIPT-999');
  });
});
