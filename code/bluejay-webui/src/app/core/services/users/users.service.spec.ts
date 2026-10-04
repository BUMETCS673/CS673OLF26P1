/*
  AI-USAGE SUMMARY
  Tools: GitHub Copilot
  Overall AI Contribution: 80%
  AI-Assisted Areas: Created base unit tests and modified tests based on user input
  Human Contributions: Prompted for specific unit tests depending on requirements
  Notes: Unit test for the user service API
  Authors: Italia Tran
*/

import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from '../../auth/auth.service';
import { CreateUserRequest, UserRecord, UsersService } from './users.service';

describe('UsersService', () => {
  let service: UsersService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: { getToken: () => 'test-token' } },
      ],
    });
    service = TestBed.inject(UsersService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTestingController.verify());

  it('loads users with the bearer token and unwraps the API response', () => {
    const users: UserRecord[] = [
      {
        id: 'user-1',
        username: 'cashier',
        enabled: true,
        createdAt: '2026-10-03T10:00:00',
      },
    ];
    let result: UserRecord[] | undefined;

    service.getUsers().subscribe((response) => (result = response));

    const request = httpTestingController.expectOne('/api/v1/users');
    expect(request.request.method).toBe('GET');
    expect(request.request.headers.get('Authorization')).toBe(
      'Bearer test-token',
    );
    request.flush({
      success: true,
      message: 'Users retrieved successfully',
      data: users,
      errorCode: null,
      timestamp: '2026-10-03T10:00:00Z',
    });

    expect(result).toEqual(users);
  });

  it('creates a user with the bearer token and unwraps the API response', () => {
    const request: CreateUserRequest = {
      username: 'new-cashier',
      password: 'secret-pass',
      enabled: true,
      role: 'ROLE_CASHIER',
    };
    const createdUser: UserRecord = {
      id: 'user-2',
      username: 'new-cashier',
      enabled: true,
      createdAt: '2026-10-04T10:00:00',
    };
    let result: UserRecord | undefined;

    service.createUser(request).subscribe((response) => (result = response));

    const httpRequest = httpTestingController.expectOne('/api/v1/users');
    expect(httpRequest.request.method).toBe('POST');
    expect(httpRequest.request.headers.get('Authorization')).toBe(
      'Bearer test-token',
    );
    expect(httpRequest.request.body).toEqual(request);
    httpRequest.flush({
      success: true,
      message: 'User created successfully',
      data: createdUser,
      errorCode: null,
      timestamp: '2026-10-04T10:00:00Z',
    });

    expect(result).toEqual(createdUser);
  });
});
