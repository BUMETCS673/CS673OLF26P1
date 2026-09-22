// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Vitest component tests for dashboard logout and idle-timer lifecycle
// Human Contributions: Chose the scenarios (logout redirect, timer start and stop), reviewed the assertions, and ran the suite with npm test
// Notes: Mocks AuthService, Router and IdleTimerService to check what the dashboard calls on init, logout and destroy.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';
import { AuthService } from '../../services/auth.service';
import { IdleTimerService } from '../../services/idle-timer.service';
import { DashboardComponent } from './dashboard.component';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Create Vitest tests for DashboardComponent logout redirect and idle timer start/stop."
// AI Contribution: Initial draft (~70%)
// Modifications:
// - Mocks AuthService, Router and IdleTimerService with vi.fn()
// Verification:
// - Run `npm test -- --watch=false` and review the Vitest output
// Confidence: High
describe('DashboardComponent', () => {
  const authServiceMock = { getUsername: vi.fn(), logout: vi.fn() };
  const routerMock = { navigate: vi.fn() };
  const idleTimerMock = { start: vi.fn(), stop: vi.fn() };

  beforeEach(() => {
    authServiceMock.getUsername.mockReset();
    authServiceMock.getUsername.mockReturnValue('cashier');
    authServiceMock.logout.mockReset();
    routerMock.navigate.mockReset();
    idleTimerMock.start.mockReset();
    idleTimerMock.stop.mockReset();

    TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: IdleTimerService, useValue: idleTimerMock },
      ],
    });
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that the dashboard starts the idle timer when it opens and stops it when destroyed."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should start the idle timer on init and stop it on destroy', () => {
    const fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();
    expect(idleTimerMock.start).toHaveBeenCalledTimes(1);

    fixture.destroy();
    expect(idleTimerMock.stop).toHaveBeenCalledTimes(1);
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that Log Out clears the session and redirects to login with reason=logout."
  // AI Contribution: Initial draft (~90%)
  // Modifications:
  // - Clicks the real Log Out button in the rendered template
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should log out and redirect to login with the logout reason when Log Out is clicked', () => {
    const fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();

    const button = (fixture.nativeElement as HTMLElement).querySelector('button');
    button?.click();

    expect(authServiceMock.logout).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { reason: 'logout' },
    });
  });
});