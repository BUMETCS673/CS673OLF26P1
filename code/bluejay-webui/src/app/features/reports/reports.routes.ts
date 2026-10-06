import { Routes } from '@angular/router';
import { ReportsPage } from './pages/reports-page.component';
import { adminAndManagerGuard } from '../../core/guards/admin.guard';

export const REPORTS_ROUTES: Routes = [{ path: '', component: ReportsPage, canActivate: [adminAndManagerGuard] }];