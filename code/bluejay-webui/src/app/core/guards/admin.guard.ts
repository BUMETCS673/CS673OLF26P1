/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Created guard to allow admins to have access to specific pages
    Human Contributions: None
    Notes: Admin guard to check if user is admin
    Authors: Italia Tran
*/

import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';

// 1. General Guard: Protects any route requiring authentication
export const authGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) return true;

  return router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};

// 2. Admin Guard: Restricts route to Admins
export const adminGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) return true;

  return authService.isLoggedIn()
    ? router.createUrlTree(['/dashboard'])
    : router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};

// 3. Cashier Guard: Restricts route to Cashiers
export const cashierGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isCashier()) return true;

  return authService.isLoggedIn()
    ? router.createUrlTree(['/dashboard'])
    : router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};

// 4. Admin & Manager Guard: Restricts route to Admins or Managers
export const adminAndManagerGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isManager() || authService.isAdmin()) return true;

  return authService.isLoggedIn()
    ? router.createUrlTree(['/dashboard'])
    : router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};
