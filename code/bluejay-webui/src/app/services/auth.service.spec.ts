// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 85%
// AI-Assisted Areas: Angular HTTP testing setup, AuthService spec suite, Jest matcher conversions, JSON-parsed auth-data storage assertions
// Human Contributions: Custom assertions, aligning storage keys with AuthService logic, student team verification
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: [Student Name / Team]

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
    // Tool: Gemini
    // Prompt Summary: "Test logout() method clearing auth-data from localStorage in Jest."
    // AI Contribution: Initial draft (~90%)
    // Modifications:
    // - Updated boolean assertions to toBe(true) / toBe(false)
    // Verification: Jest test runner execution
    // Confidence: High
    it('should remove auth-data from localStorage on logout', () => {
      localStorage.setItem(AUTH_DATA_KEY, JSON.stringify({ token: 'jwt-token-xyz' }));
      expect(service.isLoggedIn()).toBe(true);

      service.logout();

      expect(localStorage.getItem(AUTH_DATA_KEY)).toBeNull();
      expect(service.isLoggedIn()).toBe(false);
    });
  });
});
