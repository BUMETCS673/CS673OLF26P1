// AI-USAGE SUMMARY
// Tools: Github Copilot, Claude
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Login, token persistence and helper methods (Copilot); server-side logout with JWT revocation (Claude)
// Human Contributions: Required logout to always clear local auth state even if the server call fails, matched the API path and localStorage key to the existing code, reviewed the logout logic, and verified it with auth.service.spec.ts
// Notes: Feature 18 changed logout() to call POST /api/v1/auth/logout and always clear local auth state.
// authors: Krizma Nagi

import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

// AI-ASSISTED: YES
// Tool: Github Copilot (original); Claude (Feature 18 changes)
// Prompt Summary: "Create an authentication service for login, token access, persistence, and logout"
// AI Contribution: Authentication service logic (~80%)
// Modifications:
// - Added HTTP login handling and localStorage-backed authentication helpers
// - Feature 18: logout() now revokes the JWT on the server (POST /api/v1/auth/logout) and always clears local auth state
// Verification:
// - Verified by Angular build validation and auth.service.spec.ts
// Confidence: High
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/v1/auth';
  private readonly AUTH_DATA_KEY = 'auth-data';

  // AI-ASSISTED: YES
  // Tool: Github Copilot
  // Prompt Summary: "Create an authentication service for login, token access, persistence, and logout"
  // AI Contribution: Login request and token persistence (~80%)
  // Modifications:
  // - Unchanged in Feature 18
  // Verification:
  // - Verified by Angular build validation and auth.service.spec.ts
  // Confidence: High
  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        if (response?.data) {
          localStorage.setItem(this.AUTH_DATA_KEY, JSON.stringify(response.data));
        }
      }),
    );
  }

  // AI-ASSISTED: YES
  // Tool: Github Copilot
  // Prompt Summary: "Create an authentication service for login, token access, persistence, and logout"
  // AI Contribution: localStorage-backed auth helpers (~80%)
  // Modifications:
  // - Unchanged in Feature 18
  // Verification:
  // - Verified by Angular build validation and auth.service.spec.ts
  // Confidence: High
  getAuthData(): any | null {
    const value = localStorage.getItem(this.AUTH_DATA_KEY);
    return value ? JSON.parse(value) : null;
  }

  getToken(): string | null {
    return this.getAuthData()?.token ?? null;
  }

  getUsername(): string | null {
    return this.getAuthData()?.username ?? null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Make logout revoke the JWT on the backend and always clear local auth state"
  // AI Contribution: Logout logic with server-side token revocation (~70%)
  // Modifications:
  // - Reads the token before clearing storage, then clears localStorage and sessionStorage
  // - Sends POST /api/v1/auth/logout with the Bearer token; errors are ignored so the user is always logged out locally
  // - Skips the server call when there is no token to revoke
  // Verification:
  // - auth.service.spec.ts logout tests and manual browser test
  // Confidence: High
  logout(): void {
    const token = this.getToken();

    localStorage.removeItem(this.AUTH_DATA_KEY);
    sessionStorage.clear();

    if (token) {
      this.http
        .post(
          `${this.API_URL}/logout`,
          {},
          { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) },
        )
        .subscribe({ error: () => {} });
    }
  }
}