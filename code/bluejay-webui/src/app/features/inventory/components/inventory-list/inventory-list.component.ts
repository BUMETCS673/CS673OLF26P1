// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Standalone catalog stock list component managing inventory fetch state
// Human Contributions: Integration with inventory API and data formatting
// authors: Sara Orion

import { Component, inject, signal, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { InventoryService } from '../../services/inventory.service';
import { InventoryItem } from '../../models/inventory.model';
import { InlineActionButtonsComponent } from '../../../../shared/components/inline-action-buttons.component';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [DecimalPipe, FormsModule, InlineActionButtonsComponent],
  templateUrl: 'inventory-list.component.html',
  styleUrls: ['../inventory.component.scss']
})
export class InventoryListComponent implements OnInit {
  private readonly inventoryService = inject(InventoryService);

  inventoryItems = signal<InventoryItem[]>([]);
  isLoading = signal<boolean>(false);

  // Track inline editing state
  editingItemId = signal<string | null>(null);
  editingSalePrice = signal<number>(0);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);
    this.inventoryService.getInventory().subscribe({
      next: (res) => {
        this.isLoading.set(false);
        if (res.success && res.data) {
          this.inventoryItems.set(res.data);
        }
      },
      error: () => this.isLoading.set(false)
    });
  }

  startEdit(item: InventoryItem): void {
    this.editingItemId.set(item.id);
    this.editingSalePrice.set(item.salePrice);
  }

  cancelEdit(): void {
    this.editingItemId.set(null);
  }

  // inventory-list.component.ts

  saveEdit(item: InventoryItem): void {
    const updatedPrice = this.editingSalePrice();

    // Guard against unchanged value
    if (updatedPrice === item.salePrice) {
      this.editingItemId.set(null);
      return;
    }

    this.isLoading.set(true);

    this.inventoryService.updateSalePrice(item.id, updatedPrice).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        if (res.success) {
          // Update local signal state with updated item from server (or local patch)
          this.inventoryItems.update((items) =>
            items.map((i) => (i.id === item.id ? { ...i, salePrice: updatedPrice } : i))
          );
          this.editingItemId.set(null);
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        console.error('Failed to update sale price:', err);
        // Optional: Trigger a notification/toast error here
      }
    });
  }
}
