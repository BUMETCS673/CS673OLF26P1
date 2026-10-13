import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { catchError, map, of, Subject, switchMap, timer } from 'rxjs';
import { Product } from '../../products/models/product.model';
import { ProductService } from '../../products/services/product.service';
import { SaleRequest, SaleResponse } from '../models/sale.model';
import { SaleService } from '../services/sale.service';

const CART_STORAGE_KEY = 'bluejay-sales-cart';

interface CartItem {
  product: Product;
  quantity: number;
}

interface ProductSearchResponse {
  term: string;
  products: Product[];
  error: string | null;
}

interface ChangeDenomination {
  valueCents: number;
  label: string;
  count: number;
}

const CHANGE_DENOMINATIONS = [
  { valueCents: 10000, label: '$100 bill' },
  { valueCents: 5000, label: '$50 bill' },
  { valueCents: 2000, label: '$20 bill' },
  { valueCents: 1000, label: '$10 bill' },
  { valueCents: 500, label: '$5 bill' },
  { valueCents: 100, label: '$1 bill' },
  { valueCents: 25, label: 'quarter' },
  { valueCents: 10, label: 'dime' },
  { valueCents: 5, label: 'nickel' },
  { valueCents: 1, label: 'penny' },
];

@Component({
  selector: 'app-sales-page',
  standalone: true,
  imports: [CurrencyPipe, FormsModule],
  templateUrl: './sales-page.component.html',
  styleUrl: './sales-page.component.scss',
})
export class SalesPage {
  private readonly productService = inject(ProductService);
  private readonly saleService = inject(SaleService);
  private readonly searchTerms = new Subject<string>();

  searchTerm = '';
  quantity = 1;
  readonly cashReceived = signal('');
  readonly searchResults = signal<Product[]>([]);
  readonly searchLoading = signal(false);
  readonly searchError = signal<string | null>(null);
  readonly hasSearched = signal(false);
  readonly dropdownOpen = signal(false);
  readonly cartError = signal<string | null>(null);
  readonly isProcessingSale = signal(false);
  readonly checkoutError = signal<string | null>(null);
  readonly completedSale = signal<SaleResponse | null>(null);
  readonly cartItems = signal<CartItem[]>(this.restoreCart());
  readonly cartTotalCents = computed(() =>
    this.cartItems().reduce(
      (total, item) => total + Math.round(item.product.price * 100) * item.quantity,
      0,
    ),
  );
  readonly cartTotal = computed(() => this.cartTotalCents() / 100);
  readonly cashReceivedCents = computed(() => this.parseMoneyToCents(this.cashReceived()));
  readonly changeDueCents = computed(() => {
    const receivedCents = this.cashReceivedCents();
    return receivedCents === null ? 0 : Math.max(0, receivedCents - this.cartTotalCents());
  });
  readonly cashShortfallCents = computed(() => {
    const receivedCents = this.cashReceivedCents();
    return receivedCents === null ? 0 : Math.max(0, this.cartTotalCents() - receivedCents);
  });
  readonly changeBreakdown = computed(() => {
    let remainingCents = this.changeDueCents();
    const breakdown: ChangeDenomination[] = [];

    for (const denomination of CHANGE_DENOMINATIONS) {
      const count = Math.floor(remainingCents / denomination.valueCents);
      if (count > 0) {
        breakdown.push({ ...denomination, count });
        remainingCents %= denomination.valueCents;
      }
    }

    return breakdown;
  });
  readonly paymentState = computed(() => {
    const enteredAmount = this.cashReceived().trim();
    if (!enteredAmount) return 'empty';

    const receivedCents = this.cashReceivedCents();
    if (receivedCents === null) return 'invalid';
    if (receivedCents < this.cartTotalCents()) return 'insufficient';
    return receivedCents === this.cartTotalCents() ? 'exact' : 'change';
  });
  readonly canPay = computed(() => {
    const receivedCents = this.cashReceivedCents();
    return this.cartItems().length > 0
      && receivedCents !== null
      && receivedCents >= this.cartTotalCents()
      && !this.isProcessingSale();
  });

  constructor() {
    this.searchTerms.pipe(
      switchMap((term) => timer(term ? 250 : 0).pipe(
        switchMap(() => term
          ? this.productService.list({ searchTerm: term, pageNumber: 1, pageSize: 10 }).pipe(
            map((page): ProductSearchResponse => ({ term, products: page.items, error: null })),
            catchError((error: HttpErrorResponse) => of<ProductSearchResponse>({
              term,
              products: [],
              error: error.status === 404 ? null : 'Could not search products. Please try again.',
            })),
          )
          : of<ProductSearchResponse>({ term, products: [], error: null })),
      )),
      takeUntilDestroyed(),
    ).subscribe(({ term, products, error }) => {
      if (term !== this.searchTerm.trim()) return;
      this.searchResults.set(products);
      this.searchError.set(error);
      this.searchLoading.set(false);
      this.hasSearched.set(!!term);
    });
  }

  onSearchTermChange(value: string): void {
    const term = value.trim();
    this.searchTerm = value;
    this.searchResults.set([]);
    this.searchError.set(null);
    this.searchLoading.set(!!term);
    this.hasSearched.set(false);
    this.dropdownOpen.set(!!term);
    this.searchTerms.next(term);
  }

  selectProduct(product: Product): void {
    this.addToCart(product);
    this.searchTerm = '';
    this.searchResults.set([]);
    this.searchError.set(null);
    this.searchLoading.set(false);
    this.hasSearched.set(false);
    this.dropdownOpen.set(false);
    this.searchTerms.next('');
  }

  addToCart(product: Product): void {
    this.cartError.set(null);
    if (!Number.isInteger(this.quantity) || this.quantity <= 0) {
      this.cartError.set('Enter a quantity greater than zero.');
      return;
    }

    const quantity = this.quantity;
    const existingItem = this.cartItems().find((item) => item.product.id === product.id);
    const quantityInCart = existingItem?.quantity ?? 0;
    const availableToAdd = product.currentStock - quantityInCart;
    if (quantity > availableToAdd) {
      this.cartError.set(
        availableToAdd > 0
          ? `Only ${availableToAdd} more ${product.name} available.`
          : `No more ${product.name} available to add.`,
      );
      return;
    }

    const items = this.cartItems();
    const updatedItems = !existingItem
      ? [...items, { product, quantity }]
      : items.map((item) => item.product.id === product.id
        ? { ...item, quantity: item.quantity + quantity }
        : item);
    this.saveCart(updatedItems);
  }

  removeFromCart(productId: string): void {
    this.cartError.set(null);
    this.saveCart(this.cartItems().filter((item) => item.product.id !== productId));
  }

  cartItemSubtotal(item: CartItem): number {
    return Math.round(item.product.price * 100) * item.quantity / 100;
  }

  onPay(): void {
    if (!this.canPay() || this.isProcessingSale()) return;

    const cashAmount = (this.cashReceivedCents() ?? 0) / 100;
    const request: SaleRequest = {
      items: this.cartItems().map((item) => ({
        productId: item.product.id,
        quantity: item.quantity,
      })),
      cashReceived: cashAmount,
    };

    this.isProcessingSale.set(true);
    this.checkoutError.set(null);
    this.completedSale.set(null);

    this.saleService.checkout(request).subscribe({
      next: (response) => {
        this.isProcessingSale.set(false);
        this.completedSale.set(response);
        this.saveCart([]);
        this.cashReceived.set('');
        this.quantity = 1;
      },
      error: (error: HttpErrorResponse) => {
        this.isProcessingSale.set(false);
        const message = error.error?.message || error.message || 'Sale could not be processed. Please try again.';
        this.checkoutError.set(message);
      },
    });
  }

  dismissReceipt(): void {
    this.completedSale.set(null);
    this.checkoutError.set(null);
  }

  private parseMoneyToCents(value: string): number | null {
    const match = /^(\d+)(?:\.(\d{0,2}))?$|^\.(\d{1,2})$/.exec(value.trim());
    if (!match) return null;

    const dollars = Number(match[1] ?? 0);
    const cents = Number((match[2] ?? match[3] ?? '').padEnd(2, '0'));
    const totalCents = dollars * 100 + cents;
    return Number.isSafeInteger(totalCents) ? totalCents : null;
  }

  private restoreCart(): CartItem[] {
    const storedCart = sessionStorage.getItem(CART_STORAGE_KEY);
    if (!storedCart) return [];

    try {
      const parsed: unknown = JSON.parse(storedCart);
      if (!Array.isArray(parsed)) throw new Error('Saved cart must be an array.');

      const validItems = parsed.filter((item): item is CartItem => {
        if (!item || typeof item !== 'object') return false;
        const cartItem = item as Partial<CartItem>;
        const product = cartItem.product;
        return !!product
          && typeof product.id === 'string'
          && typeof product.name === 'string'
          && typeof product.barcode === 'string'
          && Number.isFinite(product.price)
          && Number.isInteger(product.currentStock)
          && Number.isInteger(cartItem.quantity)
          && (cartItem.quantity ?? 0) > 0;
      });

      if (validItems.length !== parsed.length) this.saveCart(validItems);
      return validItems;
    } catch {
      sessionStorage.removeItem(CART_STORAGE_KEY);
      return [];
    }
  }

  private saveCart(items: CartItem[]): void {
    this.cartItems.set(items);
    if (items.length === 0) {
      sessionStorage.removeItem(CART_STORAGE_KEY);
      return;
    }

    sessionStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items));
  }
}

