// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Idle-timeout service design, activity-event listeners, and timer reset/expiry logic
// Human Contributions: Chose the 15-minute limit and the keypress/click/touch events from the Feature 18 acceptance criteria, reviewed the timer logic, and verified it with unit tests and a manual browser test
// Notes: Logs the user out and redirects to /login after 15 minutes without keypress, click or touch. Authenticated pages call start() when they open and stop() when they close.
// authors: Krizma Nagi

import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Create an Angular idle-timer service that logs the user out after 15 minutes of inactivity"
// AI Contribution: Service structure and timer logic (~70%)
// Modifications:
// - Listens for keydown, click and touchstart events to match the Feature 18 acceptance criteria
// - Logs out through AuthService and redirects to /login?reason=inactivity so the login page can show the expiry message
// - Exposes timeoutMs so it can be shortened for demos and tests
// Verification:
// - idle-timer.service.spec.ts (fake timers) and manual browser test with a shortened timeout
// Confidence: High
@Injectable({
  providedIn: 'root',
})
export class IdleTimerService {
  private authService = inject(AuthService);
  private router = inject(Router);

  /** Inactivity limit (15 minutes). Public so it is easy to shorten in demos. */
  timeoutMs = 15 * 60 * 1000;

  private timerId: ReturnType<typeof setTimeout> | null = null;
  private running = false;
  private readonly activityEvents = ['keydown', 'click', 'touchstart'];
  private readonly onActivity = (): void => this.resetTimer();

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Add start() that registers activity listeners once and starts the idle countdown"
  // AI Contribution: Start logic (~70%)
  // Modifications:
  // - Guards with a running flag so listeners are never registered twice
  // Verification:
  // - idle-timer.service.spec.ts ("start() called twice" and reset-on-activity tests)
  // Confidence: High
  /** Call when an authenticated page opens. Safe to call more than once. */
  start(): void {
    if (!this.running) {
      this.running = true;
      this.activityEvents.forEach((eventName) =>
        window.addEventListener(eventName, this.onActivity),
      );
    }
    this.resetTimer();
  }

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Add stop() that removes listeners and cancels the pending timeout"
  // AI Contribution: Stop and cleanup logic (~70%)
  // Modifications:
  // - Clears the pending timeout and removes all listeners so nothing fires after the user leaves the page
  // Verification:
  // - idle-timer.service.spec.ts ("after stop()" tests)
  // Confidence: High
  /** Call when leaving an authenticated page. */
  stop(): void {
    this.activityEvents.forEach((eventName) =>
      window.removeEventListener(eventName, this.onActivity),
    );
    if (this.timerId !== null) {
      clearTimeout(this.timerId);
      this.timerId = null;
    }
    this.running = false;
  }

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Add a resetTimer method that restarts the idle countdown on user activity"
  // AI Contribution: Timer reset logic (~70%)
  // Modifications:
  // - Clears any existing timeout before starting a new one
  // Verification:
  // - idle-timer.service.spec.ts (keydown/click/touchstart reset tests)
  // Confidence: High
  private resetTimer(): void {
    if (this.timerId !== null) {
      clearTimeout(this.timerId);
    }
    this.timerId = setTimeout(() => this.onTimeout(), this.timeoutMs);
  }

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "On idle timeout, log out through AuthService and redirect to login with an inactivity reason"
  // AI Contribution: Timeout handler (~70%)
  // Modifications:
  // - Stops the timer first, then calls AuthService.logout() (which revokes the JWT on the server)
  // - Navigates to /login with the query parameter reason=inactivity
  // Verification:
  // - idle-timer.service.spec.ts (15-minute logout test) and manual browser test
  // Confidence: High
  private onTimeout(): void {
    this.stop();
    this.authService.logout();
    void this.router.navigate(['/login'], {
      queryParams: { reason: 'inactivity' },
    });
  }
}