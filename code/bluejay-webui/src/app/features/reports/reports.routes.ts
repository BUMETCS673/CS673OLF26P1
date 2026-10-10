import { Routes } from '@angular/router';
import { ReportsPage } from './pages/reports-page.component';
import { SalesReportPage } from './pages/sales-report-page.component';
import { adminAndManagerGuard, adminGuard } from '../../core/guards/admin.guard';

export const REPORTS_ROUTES: Routes = [
  { path: 'sales', component: SalesReportPage, canActivate: [adminGuard] },
  { path: '', component: ReportsPage, canActivate: [adminAndManagerGuard] },
];