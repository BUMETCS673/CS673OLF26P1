// AI-ASSISTED: YES
// Tool: Github Copilot
// Prompt Summary: "Create an authentication service for login, token access, persistence, and logout"
// AI Contribution: Authentication service logic (~80%)
// Modifications:
//   - Added HTTP login handling and localStorage-backed authentication helpers
// Verification:
//   - Verified by Angular build validation
// Confidence: High

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly API_URL = '/api/v1/auth';
  private readonly AUTH_DATA_KEY = 'auth-data';

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        if (response?.data) {
          localStorage.setItem(this.AUTH_DATA_KEY, JSON.stringify(response.data));
        }
      }),
    );
  }

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

  logout(): void {
    localStorage.removeItem(this.AUTH_DATA_KEY);
  }
}
