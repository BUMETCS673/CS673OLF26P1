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

export const adminGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAdmin()) return true;

  return authService.isLoggedIn()
    ? router.createUrlTree(['/dashboard'])
    : router.createUrlTree(['/login'], { queryParams: { returnUrl: state.url } });
};
