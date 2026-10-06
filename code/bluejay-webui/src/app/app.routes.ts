// AI-ASSISTED: GEMINI
// Tool: Initial Angular CLI scaffold
// Prompt Summary: Updates for actual role guards
// AI Contribution: Refactored to include updates from admin.guard

import { Routes } from '@angular/router';
import { AppShellComponent } from './core/layout/app-shell/app-shell.component';
import { authGuard, adminGuard, adminAndManagerGuard } from './core/guards/admin.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => import('./features/auth/auth.routes').then((feature) => feature.AUTH_ROUTES),
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full',
  },
  {
    path: '',
    component: AppShellComponent,
    canActivate: [authGuard],
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./features/dashboard/dashboard.routes').then((feature) => feature.DASHBOARD_ROUTES),
      },
      {
        path: 'products',
        canActivate: [adminAndManagerGuard],
        loadChildren: () =>
          import('./features/products/products.routes').then((feature) => feature.PRODUCTS_ROUTES),
      },
      {
        path: 'inventory',
        canActivate: [adminAndManagerGuard],
        loadChildren: () =>
          import('./features/inventory/inventory.routes').then((feature) => feature.INVENTORY_ROUTES),
      },
      {
        path: 'sales',
        loadChildren: () =>
          import('./features/sales/sales.routes').then((feature) => feature.SALES_ROUTES),
      },
      {
        path: 'reports',
        canActivate: [adminAndManagerGuard],
        loadChildren: () =>
          import('./features/reports/reports.routes').then((feature) => feature.REPORTS_ROUTES),
      },
      {
        path: 'users',
        canActivate: [adminGuard],
        loadChildren: () =>
          import('./features/users/users.routes').then((feature) => feature.USERS_ROUTES),
      },
    ],
  },
  { path: '**', redirectTo: 'login' },
];
