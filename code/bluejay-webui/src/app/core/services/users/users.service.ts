/*
  AI-USAGE SUMMARY
  Tools: GitHub Copilot
  Overall AI Contribution: 100%
  AI-Assisted Areas: Created user service to get data from the user API
  Human Contributions: None
  Notes: Service to get data from the user API
  Authors: Italia Tran
*/

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { AuthService } from '../../auth/auth.service';

export interface UserRecord {
  id: string;
  username: string;
  enabled: boolean;
  createdAt: string;
}

interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode: string | null;
  timestamp: string;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);

  getUsers(): Observable<UserRecord[]> {
    const token = this.authService.getToken();
    const headers = token
      ? new HttpHeaders({ Authorization: `Bearer ${token}` })
      : undefined;

    return this.http
      .get<ApiResponse<UserRecord[]>>('/api/v1/users', { headers })
      .pipe(map((response) => response.data));
  }
}
