// AI-USAGE SUMMARY
// Tools: GitHub Copilot, Gemini
// Overall AI Contribution: 90%
// AI-Assisted Areas: User API service HTTP methods for GET, POST, and PATCH endpoints
// Human Contributions: Verified authorization header mapping and backend route paths
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// Authors: Italia Tran, Sara Orion

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { AuthService } from '../../../core/auth/auth.service';
import { ApiResponse } from '../../../shared/models/api-response.model';
import {
  CreateUserRequest,
  UpdateUserRequest,
  UserRecord,
} from '../models/users.model';

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  getUsers(): Observable<UserRecord[]> {
    // @ts-ignore
    return this.http
      .get<ApiResponse<UserRecord[]>>('/api/v1/users', {
        headers: this.getAuthHeaders(),
      })
      .pipe(map((response) => response.data));
  }

  createUser(request: CreateUserRequest): Observable<UserRecord> {
    // @ts-ignore
    return this.http
      .post<ApiResponse<UserRecord>>('/api/v1/users', request, {
        headers: this.getAuthHeaders(),
      })
      .pipe(map((response) => response.data));
  }

  // AI-ASSISTED: YES
  // Tool: Gemini
  // Prompt Summary: "Add updateUser method for PATCH requests to update role and account status."
  // AI Contribution: Initial draft (~90%)
  // Modifications: Configured request body payload type and URL parameters.
  // Verification: Unit test with HttpTestingController
  // Confidence: High
  updateUser(userId: string, request: UpdateUserRequest): Observable<UserRecord> {
    // @ts-ignore
    return this.http
      .patch<ApiResponse<UserRecord>>(`/api/v1/users/${userId}`, request, {
        headers: this.getAuthHeaders(),
      })
      .pipe(map((response) => response.data));
  }

  private getAuthHeaders(): HttpHeaders | undefined {
    const token = this.authService.getToken();
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
  }
}
