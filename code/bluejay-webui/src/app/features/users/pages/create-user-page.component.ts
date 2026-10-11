/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Added the create user page component
    Human Contributions: None
    Notes: The create user page
    Authors: Italia Tran
*/

import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CreateUserRequest, RoleOption } from '../models/users.model';
import { UsersService } from '../services/users.services';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './create-user-page.component.html',
  styleUrl: './create-user-page.component.scss',
})
export class CreateUserPage {
  private readonly formBuilder = inject(FormBuilder);
  private readonly usersService = inject(UsersService);

  readonly roles: RoleOption[] = [
    { value: 'ROLE_CASHIER', label: 'Cashier' },
    { value: 'ROLE_MANAGER', label: 'Manager' },
    { value: 'ROLE_ADMIN', label: 'Administrator' },
  ];
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  readonly userForm = this.formBuilder.nonNullable.group({
    username: ['', [Validators.required, Validators.maxLength(50)]],
    password: ['', Validators.required],
    enabled: [true],
    role: ['ROLE_CASHIER', Validators.required],
  });

  onSubmit(): void {
    if (this.userForm.invalid || this.isSubmitting()) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const request: CreateUserRequest = this.userForm.getRawValue();
    this.usersService.createUser(request).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.userForm.reset({
          username: '',
          password: '',
          enabled: true,
          role: 'ROLE_CASHIER',
        });
        this.successMessage.set('User created successfully.');
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(
          error.error?.message ?? 'The user could not be created. Please try again.',
        );
      },
    });
  }
}
