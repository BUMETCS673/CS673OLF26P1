import { Routes } from '@angular/router';
import { ProductListPage } from './pages/product-list-page.component';
import { adminAndManagerGuard } from '../../core/guards/admin.guard';

export const PRODUCTS_ROUTES: Routes = [{ path: '', component: ProductListPage, canActivate: [adminAndManagerGuard] }];