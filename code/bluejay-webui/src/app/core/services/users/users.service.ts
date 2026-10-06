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
import { ApiResponse} from '../../../shared/models/api-response.model';

export interface UserRecord {
  id: string;
  username: string;
  enabled: boolean;
  createdAt: string;
}

export interface CreateUserRequest {
  username: string;
  password: string;
  enabled: boolean;
  role: string;
}

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

  private getAuthHeaders(): HttpHeaders | undefined {
    const token = this.authService.getToken();
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : undefined;
  }
}
