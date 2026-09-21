// AI-USAGE SUMMARY
// Tools: Github Copilot, Claude
// Overall AI Contribution: ~75%
// AI-Assisted Areas: Dashboard component and template (Copilot); idle-timer lifecycle hooks and logout message redirect (Claude)
// Human Contributions: Decided the idle timer should run while an authenticated page is open, added the logged-out confirmation after a reviewer suggestion, reviewed the wiring, and verified it with unit tests and a manual browser test
// Notes: Feature 18 starts the idle timer in ngOnInit and stops it in ngOnDestroy, and manual logout redirects to /login?reason=logout so the login page can confirm the logout.
// authors: Krizma Nagi

import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { IdleTimerService } from '../../services/idle-timer.service';

// AI-ASSISTED: YES
// Tool: Github Copilot (original); Claude (Feature 18 changes)
// Prompt Summary: "Create a dashboard component that displays the user and supports logout"
// AI Contribution: Dashboard component logic and template (~75%)
// Modifications:
// - Added authenticated username display and logout navigation to the login route
// - Feature 18: injects IdleTimerService and implements OnInit/OnDestroy so the idle timer runs only while this page is open
// - Feature 18: manual logout redirects to /login?reason=logout so the login page shows a confirmation message
// Verification:
// - Verified by Angular build validation, dashboard.component.spec.ts and manual browser test
// Confidence: High
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 40px; text-align: center; font-family: Arial, sans-serif;">
      <h1>Welcome{{ username ? ', ' + username : '' }}!</h1>
      <p>Welcome to your secure dashboard.</p>

      <!-- Optional: Logout button to make testing easy -->
      <button (click)="onLogout()" style="padding: 10px 20px; margin-top: 20px; cursor: pointer;">
        Log Out
      </button>
    </div>
  `,
})
export class DashboardComponent implements OnInit, OnDestroy {
  private authService = inject(AuthService);
  private router = inject(Router);
  private idleTimer = inject(IdleTimerService);

  username = this.authService.getUsername();

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Start the idle timer when the dashboard opens"
  // AI Contribution: Lifecycle hook (~80%)
  // Modifications:
  // - Calls IdleTimerService.start() on init
  // Verification:
  // - Manual browser test with a shortened timeout
  // Confidence: High
  ngOnInit(): void {
    this.idleTimer.start();
  }

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Stop the idle timer when the dashboard is closed"
  // AI Contribution: Lifecycle hook (~80%)
  // Modifications:
  // - Calls IdleTimerService.stop() on destroy, which also covers manual logout navigating away
  // Verification:
  // - Manual browser test (Log Out button)
  // Confidence: High
  ngOnDestroy(): void {
    this.idleTimer.stop();
  }

  // AI-ASSISTED: YES
  // Tool: Github Copilot (original); Claude (Feature 18 update)
  // Prompt Summary: "Create a dashboard component that displays the user and supports logout"
  // AI Contribution: Logout handler (~75%)
  // Modifications:
  // - AuthService.logout() now also revokes the JWT on the server
  // - Redirects to /login with the query parameter reason=logout so the login page shows "You have been logged out successfully"
  // Verification:
  // - dashboard.component.spec.ts and manual browser test (Log Out button)
  // Confidence: High
  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login'], { queryParams: { reason: 'logout' } });
  }
}