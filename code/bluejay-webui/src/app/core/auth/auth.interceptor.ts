// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~65%
// AI-Assisted Areas: Functional HTTP interceptor that attaches the bearer token
// Human Contributions: Required the token to be attached to product API calls and checked the behavior against the backend
// Notes: Requests without a token are passed through unchanged.
// authors: Kimleng

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const token = inject(AuthService).getToken();
  if (!token) return next(request);

  return next(request.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
