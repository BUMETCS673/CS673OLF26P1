// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Standalone catalog stock list component managing inventory fetch state
// Human Contributions: Integration with inventory API and data formatting
// authors: Sara Orion

import { Component, inject, signal, OnInit } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { InventoryService } from '../../services/inventory.service';
import { InventoryItem } from '../../models/inventory.model';

@Component({
  selector: 'app-inventory-list',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: 'inventory-list.component.html',
  styleUrls: ['../inventory.component.scss']
})
export class InventoryListComponent implements OnInit {
  private readonly inventoryService = inject(InventoryService);

  inventoryItems = signal<InventoryItem[]>([]);
  isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);
    this.inventoryService.getStockInventory().subscribe({
      next: (res) => {
        this.isLoading.set(false);
        if (res.success && res.data) {
          this.inventoryItems.set(res.data);
        }
      },
      error: () => this.isLoading.set(false)
    });
  }
}
