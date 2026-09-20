// AI-USAGE SUMMARY
// Tools: Github Copilot, Claude
// Overall AI Contribution: ~80%
// AI-Assisted Areas: Reactive login form, validation, submission and error handling (Copilot); session-expired message from the idle-timer redirect (Claude)
// Human Contributions: Wrote the exact "Session Expired due to inactivity" wording from the Feature 18 acceptance criteria, reviewed the query-parameter logic, and verified it with a manual browser test
// Notes: Feature 18 reads the reason=inactivity query parameter set by IdleTimerService and shows an info message on the login page.
// authors: Krizma Nagi

import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';

// AI-ASSISTED: YES
// Tool: Github Copilot (original); Claude (Feature 18 changes)
// Prompt Summary: "Create a reactive login component with validation, authentication, and error handling"
// AI Contribution: Login component logic (~80%)
// Modifications:
// - Added form validation, loading state, authentication submission, and navigation/error handling
// - Feature 18: injects ActivatedRoute and shows "Session Expired due to inactivity" when redirected by the idle timer
// Verification:
// - Verified by Angular build validation and manual browser test
// Confidence: High
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  loginForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  isLoading = false;
  errorMessage: string | null = null;

  // AI-ASSISTED: YES
  // Tool: Claude
  // Prompt Summary: "Show a session-expired message on the login page when redirected by the idle timer"
  // AI Contribution: Query-parameter check and message (~80%)
  // Modifications:
  // - Reads reason=inactivity from the route snapshot and sets the exact message text from the Feature 18 acceptance criteria
  // Verification:
  // - Manual browser test with a shortened idle timeout
  // Confidence: High
  infoMessage: string | null =
    this.route.snapshot.queryParamMap.get('reason') === 'inactivity'
      ? 'Session Expired due to inactivity'
      : null;

  // AI-ASSISTED: YES
  // Tool: Github Copilot
  // Prompt Summary: "Create a reactive login component with validation, authentication, and error handling"
  // AI Contribution: Login submit handler (~80%)
  // Modifications:
  // - Unchanged in Feature 18
  // Verification:
  // - Verified by Angular build validation and manual browser test
  // Confidence: High
  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    // 2. Reset states before starting the HTTP request
    this.isLoading = true;
    this.errorMessage = null;

    // 3. Extract the form data (username and password)
    const credentials = this.loginForm.value;

    // 4. Call your AuthService login method
    this.authService.login(credentials).subscribe({
      next: (response) => {
        // This runs on a successful HTTP 200 OK status from Spring Boot
        this.isLoading = false;

        // Redirect the authenticated user to your secure landing page/dashboard
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        // This runs if Spring Boot returns a non-200 status (e.g., 401 Unauthorized or 403 Forbidden)
        this.isLoading = false;

        // Extract the error message string sent from Spring Boot's custom exception handler,
        // or fall back to a generic message if the backend is down or unreachable.
        if (err.status === 0) {
          this.errorMessage =
            'Cannot connect to the server. Please check if your Spring Boot backend is running.';
        } else {
          this.errorMessage = err.error?.message || 'Invalid username or password.';
        }
      },
    });
  }
}