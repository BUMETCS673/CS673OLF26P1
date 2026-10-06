// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Component logic for fetching and managing inventory health metrics signal state
// Human Contributions: Integration with inventory service and status formatting rules
// authors: Sara Orion

import { Component, inject, signal, OnInit } from '@angular/core';
import { NgClass } from '@angular/common';
import { InventoryService } from '../../services/inventory.service';
import { InventoryHealthItem } from '../../models/inventory.model';

@Component({
  selector: 'app-inventory-health',
  standalone: true,
  imports: [NgClass],
  templateUrl: 'inventory-health.component.html',
  styleUrls: ['../inventory.component.scss']
})
export class InventoryHealthComponent implements OnInit {
  private readonly inventoryService = inject(InventoryService);

  healthItems = signal<InventoryHealthItem[]>([]);
  isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);
    this.inventoryService.getInventoryHealth().subscribe({
      next: (res) => {
        this.isLoading.set(false);
        if (res.success && res.data) {
          this.healthItems.set(res.data);
        }
      },
      error: () => this.isLoading.set(false)
    });
  }

  getStatusClass(status: 'Healthy' | 'Low' | 'Critical'): string {
    return status.toLowerCase();
  }
}
