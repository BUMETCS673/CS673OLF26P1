// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Standalone page component composing inventory health, list, and form components
// Human Contributions: Page layout architecture and template variable event wiring to refresh child components
// authors: Sara Orion

import { Component } from '@angular/core';
import { InventoryHealthComponent } from '../components/inventory-health/inventory-health.component';
import { InventoryListComponent } from '../components/inventory-list/inventory-list.component';
import { StockEntryFormComponent } from '../components/stock-entry-form/stock-entry-form.component';

@Component({
  selector: 'app-inventory-page',
  standalone: true,
  imports: [
    InventoryHealthComponent,
    InventoryListComponent,
    StockEntryFormComponent
  ],
  templateUrl: './inventory-page.component.html',
  styleUrl: './inventory-page.component.scss',
})
export class InventoryPage {}
