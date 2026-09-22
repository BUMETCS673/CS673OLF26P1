// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Vitest fake-timer tests for the idle timeout, activity resets, stop() and repeated start()
// Human Contributions: Chose the scenarios from the Feature 18 acceptance criteria (15-minute idle logout; keypress, click and touch activity), reviewed the assertions, and ran the suite with npm test
// Notes: Uses vi.useFakeTimers() so the 15-minute timeout is tested instantly, with AuthService and Router mocked.
// authors: Krizma Nagi

import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { vi } from 'vitest';
import { AuthService } from './auth.service';
import { IdleTimerService } from './idle-timer.service';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Create a Vitest unit test suite for IdleTimerService using fake timers and mocked AuthService and Router."
// AI Contribution: Initial draft (~70%)
// Modifications:
// - Mocked AuthService.logout and Router.navigate with vi.fn()
// - Used vi.useFakeTimers() and vi.advanceTimersByTime() to simulate 15 minutes instantly
// Verification:
// - Run `npm test -- --watch=false` and review the Vitest output
// Confidence: High
describe('IdleTimerService', () => {
  const FIFTEEN_MINUTES = 15 * 60 * 1000;
  const authServiceMock = { logout: vi.fn() };
  const routerMock = { navigate: vi.fn() };
  let service: IdleTimerService;

  beforeEach(() => {
    vi.useFakeTimers();
    authServiceMock.logout.mockClear();
    routerMock.navigate.mockClear();

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    });
    service = TestBed.inject(IdleTimerService);
  });

  afterEach(() => {
    service.stop();
    vi.useRealTimers();
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that the default idle timeout is 15 minutes."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should default to a 15 minute timeout', () => {
    expect(service.timeoutMs).toBe(FIFTEEN_MINUTES);
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that 15 minutes of inactivity logs out and redirects to login with reason=inactivity."
  // AI Contribution: Initial draft (~90%)
  // Modifications:
  // - Checks the boundary: no logout at 14:59.999, logout exactly at 15:00
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should log out and redirect with the inactivity reason after 15 minutes', () => {
    service.start();

    vi.advanceTimersByTime(FIFTEEN_MINUTES - 1);
    expect(authServiceMock.logout).not.toHaveBeenCalled();

    vi.advanceTimersByTime(1);
    expect(authServiceMock.logout).toHaveBeenCalledTimes(1);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/login'], {
      queryParams: { reason: 'inactivity' },
    });
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that keydown, click and touchstart events each reset the idle timer."
  // AI Contribution: Initial draft (~90%)
  // Modifications:
  // - Uses it.each so the same scenario runs once per activity event named in the acceptance criteria
  // Verification: Vitest test runner execution
  // Confidence: High
  it.each(['keydown', 'click', 'touchstart'])(
    'should reset the timer when a %s event happens',
    (eventName) => {
      service.start();

      vi.advanceTimersByTime(10 * 60 * 1000);
      window.dispatchEvent(new Event(eventName));

      vi.advanceTimersByTime(10 * 60 * 1000);
      expect(authServiceMock.logout).not.toHaveBeenCalled();

      vi.advanceTimersByTime(5 * 60 * 1000);
      expect(authServiceMock.logout).toHaveBeenCalledTimes(1);
    },
  );

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that calling stop() prevents any later logout or redirect."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should not log out after stop() is called', () => {
    service.start();
    service.stop();

    vi.advanceTimersByTime(FIFTEEN_MINUTES * 2);

    expect(authServiceMock.logout).not.toHaveBeenCalled();
    expect(routerMock.navigate).not.toHaveBeenCalled();
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that activity events are ignored after stop() removes the listeners."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should ignore activity events after stop() is called', () => {
    service.start();
    service.stop();

    window.dispatchEvent(new Event('click'));
    vi.advanceTimersByTime(FIFTEEN_MINUTES * 2);

    expect(authServiceMock.logout).not.toHaveBeenCalled();
  });

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Test that calling start() twice does not register duplicate listeners or timers."
  // AI Contribution: Initial draft (~90%)
  // Modifications: None
  // Verification: Vitest test runner execution
  // Confidence: High
  it('should only log out once even if start() is called twice', () => {
    service.start();
    service.start();

    vi.advanceTimersByTime(FIFTEEN_MINUTES);

    expect(authServiceMock.logout).toHaveBeenCalledTimes(1);
  });
});