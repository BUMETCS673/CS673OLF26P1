// AI-ASSISTED: YES
// Tool: Github Copilot
// Prompt Summary: "Create a dashboard component that displays the user and supports logout"
// AI Contribution: Dashboard component logic and template (~75%)
// Modifications:
//   - Added authenticated username display and logout navigation to the login route
// Verification:
//   - Verified by Angular build validation
// Confidence: High

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

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
export class DashboardComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  username = this.authService.getUsername();

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
