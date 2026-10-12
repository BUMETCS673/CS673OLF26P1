// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Component logic for fetching and managing inventory health metrics signal state
// Human Contributions: Integration with inventory service and status formatting rules
// authors: Sara Orion

import { Component, inject, signal, OnInit } from '@angular/core';
import { NgClass } from '@angular/common';
import { InventoryService } from '../../services/inventory.service';
import { InventoryStockItem, StockHealth } from '../../models/inventory.model';

@Component({
  selector: 'app-inventory-health',
  standalone: true,
  imports: [NgClass],
  templateUrl: 'inventory-health.component.html',
  styleUrls: ['../inventory.component.scss']
})
export class InventoryHealthComponent implements OnInit {
  private readonly inventoryService = inject(InventoryService);

  stockItems = signal<InventoryStockItem[]>([]);
  isLoading = signal<boolean>(false);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.isLoading.set(true);
    this.inventoryService.getInventoryStock().subscribe({
      next: (res) => {
        this.isLoading.set(false);
        if (res.success && res.data) {
          this.stockItems.set(res.data);
        }
      },
      error: () => this.isLoading.set(false)
    });
  }

  calculateStockHealth(current: number, lowThreshold: number): StockHealth {
    if (current <= 0) return { percentage: 0, status: 'Critical', color: '#EF4444' };

    const qCrit = 0.5 * lowThreshold;
    const qMax = 2.0 * lowThreshold;

    if (current <= qCrit) {
      return {
        percentage: Math.min((current / qCrit) * 20, 20),
        status: 'Critical',
        color: '#EF4444' // Red
      };
    }

    if (current <= lowThreshold) {
      return {
        percentage: 20 + ((current - qCrit) / (lowThreshold - qCrit)) * 30,
        status: 'Low',
        color: '#F59E0B' // Amber/Yellow
      };
    }

    return {
      percentage: Math.min(50 + ((current - lowThreshold) / (qMax - lowThreshold)) * 50, 100),
      status: 'Healthy',
      color: '#10B981' // Green
    };
  }

  // Maps status to lowercase class suffixes expected by inventory.component.scss
  getStatusClass(status: string): string {
    return status.toLowerCase();
  }
}


