// AI-USAGE SUMMARY
// Tools: GitHub Copilot
// Overall AI Contribution: ~75%
// AI-Assisted Areas: HttpTestingController setup with the functional interceptor and the authorization header assertions
// Human Contributions: Chose the scenarios (token present, token missing), aligned the mocked AuthService with the real getToken contract, reviewed the assertions, and ran the suite with npm test
// Notes: AuthService is mocked so the interceptor is tested in isolation from localStorage.
// authors: Kimleng

import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { vi } from 'vitest';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

describe('authInterceptor', () => {
  const authServiceMock = { getToken: vi.fn() };
  let http: HttpClient;
  let controller: HttpTestingController;

  beforeEach(() => {
    authServiceMock.getToken.mockReset();
    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => controller.verify());

  it('adds a bearer Authorization header when a token exists', () => {
    authServiceMock.getToken.mockReturnValue('abc123');
    http.get('/api/test').subscribe();

    const request = controller.expectOne('/api/test');
    expect(request.request.headers.get('Authorization')).toBe('Bearer abc123');
    request.flush({});
  });

  it('leaves the request untouched when there is no token', () => {
    authServiceMock.getToken.mockReturnValue(null);
    http.get('/api/test').subscribe();

    const request = controller.expectOne('/api/test');
    expect(request.request.headers.has('Authorization')).toBe(false);
    request.flush({});
  });

  it('reads the token for every request', () => {
    authServiceMock.getToken.mockReturnValueOnce('first').mockReturnValueOnce('second');
    http.get('/api/a').subscribe();
    http.get('/api/b').subscribe();

    const a = controller.expectOne('/api/a');
    const b = controller.expectOne('/api/b');
    expect(a.request.headers.get('Authorization')).toBe('Bearer first');
    expect(b.request.headers.get('Authorization')).toBe('Bearer second');
    a.flush({});
    b.flush({});
  });
});
