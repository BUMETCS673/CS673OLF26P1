import { Routes } from '@angular/router';
import { ReportsPage } from './pages/reports-page.component';
import { adminAndManagerGuard, adminGuard } from '../../core/guards/admin.guard';

export const REPORTS_ROUTES: Routes = [
  { path: '', redirectTo: 'inventory', pathMatch: 'full' },
  { path: 'inventory', component: ReportsPage, canActivate: [adminAndManagerGuard] },
  { path: 'sales', component: ReportsPage, canActivate: [adminGuard] },
];