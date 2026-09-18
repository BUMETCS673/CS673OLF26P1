import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

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

  loginForm: FormGroup = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  isLoading = false;
  errorMessage: string | null = null;

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
