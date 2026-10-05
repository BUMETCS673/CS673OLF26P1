import { Routes } from '@angular/router';
import { adminGuard } from '../../core/guards/admin.guard';
import { CreateUserPage } from './pages/create-user.component';
import { UsersPage } from './pages/users.component';

export const USERS_ROUTES: Routes = [
	{ path: 'new', component: CreateUserPage, canActivate: [adminGuard] },
	{ path: '', component: UsersPage, canActivate: [adminGuard] },
];
