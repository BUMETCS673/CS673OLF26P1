// AI-USAGE SUMMARY
// Tools: Gemini, Claude
// Overall AI Contribution: 85%
// AI-Assisted Areas: Angular HTTP testing setup, AuthService spec suite, Jest matcher conversions, JSON-parsed auth-data storage assertions, logout revocation tests
// Human Contributions: Custom assertions, aligning storage keys with AuthService logic, student team verification, reviewing and running the Claude-drafted logout tests against the updated AuthService
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration. Feature 18 replaced the old logout test with three tests covering server-side token revocation.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create unit test suite for AuthService converting Jasmine matchers to Jest matchers."
// AI Contribution: Initial draft (~85%)
// Modifications:
// - Replaced Jasmine toBeTrue()/toBeFalse() with Jest toBe(true)/toBe(false)
// - Replaced fail() with explicit throw Error() for Jest compatibility
// - Verified localStorage key alignment to 'auth-data'
// Verification:
// - Run `ng test` or `npm test` in terminal and review Jest output
// Confidence: High
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const AUTH_DATA_KEY = 'auth-data';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
  });

  // AI-ASSISTED: YES
  // Tool: Gemini
  // Prompt Summary: "Verify basic instantiation of AuthService in Jest."
  // AI Contribution: Initial draft (~100%)
  // Modifications: None
  // Verification: Jest test runner execution
  // Confidence: High
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('login()', () => {
    // AI-ASSISTED: YES
    // Tool: Gemini
    // Prompt Summary: "Test login HTTP POST request, response mapping, and JSON auth-data persistence in Jest."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Converted toBeTrue() to toBe(true)
    // - Verified HTTP POST targeting /api/v1/auth/login
    // Verification: Jest test runner execution
    // Confidence: High
    it('should send POST request to /api/v1/auth/login and save auth-data on success', () => {
      const mockCredentials = { username: 'testuser', password: 'password123' };
      const mockApiResponse = {
        success: true,
        data: {
          token: 'jwt-token-xyz',
          username: 'testuser',
          roles: ['ROLE_CASHIER']
        },
        message: 'Login successful'
      };

      service.login(mockCredentials).subscribe((response) => {
        expect(response.success).toBe(true);
        expect(response.data.token).toBe('jwt-token-xyz');
      });

      const req = httpMock.expectOne('/api/v1/auth/login');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockCredentials);

      req.flush(mockApiResponse);

      // Verify localStorage persisted stringified JSON under 'auth-data'
      const storedData = JSON.parse(localStorage.getItem(AUTH_DATA_KEY) || '{}');
      expect(storedData.token).toBe('jwt-token-xyz');
      expect(storedData.username).toBe('testuser');
    });

    // AI-ASSISTED: YES
    // Tool: Gemini
    // Prompt Summary: "Test handling of 401 Unauthorized response during login in Jest."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Replaced Jasmine fail() with thrown Error for Jest compatibility
    // Verification: Jest test runner execution
    // Confidence: High
    it('should handle 401 Unauthorized errors and leave localStorage empty', () => {
      const mockCredentials = { username: 'baduser', password: 'wrongpassword' };

      service.login(mockCredentials).subscribe({
        next: () => {
          throw new Error('Should have failed with 401 error');
        },
        error: (error) => {
          expect(error.status).toBe(401);
        }
      });

      const req = httpMock.expectOne('/api/v1/auth/login');
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });

      expect(localStorage.getItem(AUTH_DATA_KEY)).toBeNull();
    });
  });

  describe('Helper Methods & Session State', () => {
    // AI-ASSISTED: YES
    // Tool: Gemini
    // Prompt Summary: "Test getToken(), getUsername(), and isLoggedIn() using Jest matchers."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Updated boolean assertions to toBe(true)
    // Verification: Jest test runner execution
    // Confidence: High
    it('should correctly retrieve token, username, and login state when auth-data exists', () => {
      const mockAuthData = { token: 'jwt-token-abc', username: 'john_doe', roles: ['ROLE_ADMIN'] };
      localStorage.setItem(AUTH_DATA_KEY, JSON.stringify(mockAuthData));

      expect(service.getAuthData()).toEqual(mockAuthData);
      expect(service.getToken()).toBe('jwt-token-abc');
      expect(service.getUsername()).toBe('john_doe');
      expect(service.isLoggedIn()).toBe(true);
    });

    // AI-ASSISTED: YES
    // Tool: Gemini
    // Prompt Summary: "Test helper methods when no auth-data exists in localStorage in Jest."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Updated boolean assertions to toBe(false)
    // Verification: Jest test runner execution
    // Confidence: High
    it('should return null/false when no auth-data exists', () => {
      expect(service.getAuthData()).toBeNull();
      expect(service.getToken()).toBeNull();
      expect(service.getUsername()).toBeNull();
      expect(service.isLoggedIn()).toBe(false);
    });

    // AI-ASSISTED: YES
    // Tool: Claude (replaces the earlier Gemini logout test)
    // Prompt Summary: "Test logout() clears auth-data and revokes the JWT on the server via POST /api/v1/auth/logout."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Replaced the old localStorage-only logout test because logout() now issues an HTTP request
    // - Asserts the POST method, the /api/v1/auth/logout URL, and the Bearer token header
    // - Flushes the request so httpMock.verify() in afterEach passes
    // Verification: Vitest test runner execution via `npm test`
    // Confidence: High
    it('should remove auth-data and call the logout endpoint with the Bearer token', () => {
      localStorage.setItem(AUTH_DATA_KEY, JSON.stringify({ token: 'jwt-token-xyz' }));
      expect(service.isLoggedIn()).toBe(true);

      service.logout();

      expect(localStorage.getItem(AUTH_DATA_KEY)).toBeNull();
      expect(service.isLoggedIn()).toBe(false);

      const req = httpMock.expectOne('/api/v1/auth/logout');
      expect(req.request.method).toBe('POST');
      expect(req.request.headers.get('Authorization')).toBe('Bearer jwt-token-xyz');
      req.flush({ success: true });
    });

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Test that logout still clears local auth-data when the server call fails."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Simulates an HTTP 500 response with req.flush()
    // - Asserts local auth-data is removed and isLoggedIn() is false despite the server error
    // Verification: Vitest test runner execution via `npm test`
    // Confidence: High
    it('should still clear auth-data when the logout request fails', () => {
      localStorage.setItem(AUTH_DATA_KEY, JSON.stringify({ token: 'jwt-token-xyz' }));

      service.logout();

      const req = httpMock.expectOne('/api/v1/auth/logout');
      req.flush('Server error', { status: 500, statusText: 'Server Error' });

      expect(localStorage.getItem(AUTH_DATA_KEY)).toBeNull();
      expect(service.isLoggedIn()).toBe(false);
    });

    // AI-ASSISTED: YES
    // Tool: Claude
    // Prompt Summary: "Test that logout makes no server call when there is no token to revoke."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Uses httpMock.expectNone() to confirm no request is sent when auth-data is empty
    // Verification: Vitest test runner execution via `npm test`
    // Confidence: High
    it('should not call the server when there is no token to revoke', () => {
      service.logout();

      httpMock.expectNone('/api/v1/auth/logout');
      expect(service.isLoggedIn()).toBe(false);
    });
  });
});