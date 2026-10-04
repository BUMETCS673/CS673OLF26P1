// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~60%
// AI-Assisted Areas: Standalone page component composing the search, list and form components
// Human Contributions: Decided the page layout and how the form refreshes the list after create
// Notes: Search no longer talks to the list.
// authors: Kimleng

import { Component } from '@angular/core';
import { ProductFormComponent } from '../components/product-form/product-form.component';
import { ProductListComponent } from '../components/product-list/product-list.component';
import { ProductSearchComponent } from '../components/product-search/product-search.component';

@Component({
  selector: 'app-product-list-page',
  standalone: true,
  imports: [ProductFormComponent, ProductListComponent, ProductSearchComponent],
  templateUrl: './product-list-page.component.html',
  styleUrl: './product-list-page.component.scss',
})
export class ProductListPage {}
