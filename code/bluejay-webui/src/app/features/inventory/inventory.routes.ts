import { Routes } from '@angular/router';
import { InventoryPage } from './pages/inventory-page.component';
import { adminAndManagerGuard } from '../../core/guards/admin.guard';

export const INVENTORY_ROUTES: Routes = [{ path: '', component: InventoryPage, canActivate: [adminAndManagerGuard] }];