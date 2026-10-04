/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Created unit tests for admin role so that app will redirect correctly
    if user is non-admin
    Human Contributions: None
    Notes: Admin guard unit tests
    Authors: Italia Tran
*/

import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  provideRouter,
  Router,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { adminGuard } from './admin.guard';

describe('adminGuard', () => {
  let isAdmin: boolean;
  let isLoggedIn: boolean;
  let router: Router;

  beforeEach(() => {
    isAdmin = false;
    isLoggedIn = false;

    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        {
          provide: AuthService,
          useValue: {
            isAdmin: () => isAdmin,
            isLoggedIn: () => isLoggedIn,
          },
        },
      ],
    });

    router = TestBed.inject(Router);
  });

  function runGuard(): boolean | UrlTree {
    return TestBed.runInInjectionContext(() =>
      adminGuard(
        {} as ActivatedRouteSnapshot,
        { url: '/users' } as RouterStateSnapshot,
      ),
    ) as boolean | UrlTree;
  }

  it('allows admins', () => {
    isAdmin = true;
    isLoggedIn = true;

    expect(runGuard()).toBe(true);
  });

  it('redirects authenticated non-admins to the dashboard', () => {
    isLoggedIn = true;

    const result = runGuard() as UrlTree;
    expect(router.serializeUrl(result)).toBe('/dashboard');
  });

  it('redirects unauthenticated users to login with the requested URL', () => {
    const result = runGuard() as UrlTree;
    expect(router.serializeUrl(result)).toBe('/login?returnUrl=%2Fusers');
  });
});
