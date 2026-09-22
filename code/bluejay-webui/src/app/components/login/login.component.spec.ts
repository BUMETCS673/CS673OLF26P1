// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Vitest component tests for the login info messages
// Human Contributions: Chose the scenarios (inactivity, manual logout, no reason, unknown reason), reviewed the assertions, and ran the suite with npm test
// Notes: Renders LoginComponent with a fake ActivatedRoute to check which info message is shown for each reason query parameter.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { LoginComponent } from './login.component';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Create Vitest tests that check which info message the login page shows for each reason query parameter."
// AI Contribution: Initial draft (~70%)
// Modifications:
// - Provides a fake ActivatedRoute snapshot so each test controls the reason query parameter
// - Renders the component and reads the .info element from the DOM
// Verification:
// - Run `npm test -- --watch=false` and review the Vitest output
// Confidence: High
describe('LoginComponent info messages', () => {
  function renderWithReason(reason: string | null): HTMLElement {
    TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: convertToParamMap(reason === null ? {} : { reason }),
            },
          },
        },
      ],
    });

    const fixture = TestBed.createComponent(LoginComponent);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that reason=inactivity shows the session-expired message."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should show the session expired message when reason is inactivity', () => {
    const element = renderWithReason('inactivity');

    expect(element.querySelector('.info')?.textContent).toContain(
      'Session Expired due to inactivity',
    );
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that reason=logout shows the logged-out confirmation."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should show the logged out message when reason is logout', () => {
    const element = renderWithReason('logout');

    expect(element.querySelector('.info')?.textContent).toContain(
      'You have been logged out successfully',
    );
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that no info message is shown without a reason parameter."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should show no info message when there is no reason', () => {
    const element = renderWithReason(null);

    expect(element.querySelector('.info')).toBeNull();
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that an unknown reason value shows no info message."
  // AI Contribution: Initial draft (~90%)
  // Modifications:
  // - Uses "constructor" to confirm built-in object property names are not treated as messages
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should show no info message for an unknown reason', () => {
    const element = renderWithReason('constructor');

    expect(element.querySelector('.info')).toBeNull();
  });
});