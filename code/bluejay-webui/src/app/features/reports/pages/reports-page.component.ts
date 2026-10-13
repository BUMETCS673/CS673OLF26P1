// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Shared route wrapper that switches between inventory and sales reports
// Human Contributions: Consolidated both report routes into one page-level layout component
// Notes: ReportsPage now owns the heading/layout and chooses the report component by route.
// authors: Krizma Nagi

import { Component, computed, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { AuthService } from '../../../core/auth/auth.service';
import { InventoryReportComponent } from '../components/inventory-report/inventory-report.component';
import { SalesReportComponent } from '../components/sales-report/sales-report.component';

@Component({
  selector: 'app-reports-page',
  standalone: true,
  imports: [MatFormFieldModule, MatSelectModule, InventoryReportComponent, SalesReportComponent],
  templateUrl: './reports-page.component.html',
  styleUrls: ['./reports-page.component.scss'],
})
export class ReportsPage {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  readonly isAdmin = inject(AuthService).isAdmin();
  readonly currentReportType = this.route.snapshot.routeConfig?.path === 'sales' ? 'sales' : 'inventory';
  readonly isSalesView = this.currentReportType === 'sales';
  readonly selectedReportType = this.isSalesView ? 'sales' : 'inventory';
  readonly pageHeading = computed(() =>
    this.isSalesView
      ? {
          title: 'Sales Report',
          description: 'Total revenue, cost and net profit for a selected period.',
        }
      : {
          title: 'Inventory Report',
          description: 'Current stock and latest cost for every product.',
        },
  );

  onReportTypeChange(reportType: 'inventory' | 'sales'): void {
    this.router.navigateByUrl(`/reports/${reportType}`);
  }
}
