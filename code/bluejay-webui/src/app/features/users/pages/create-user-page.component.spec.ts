/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot / Gemini
    Overall AI Contribution: 90%
    AI-Assisted Areas: Updated unit tests for CreateUserPage to include user role attributes
    Human Contributions: Added role assertion verifications
    Notes: Unit tests for the create user page
    Authors: Italia Tran, Sara Orion
*/

import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { UsersService } from '../services/users.services';
import { CreateUserPage } from './create-user-page.component';

describe('CreateUserPage', () => {
  const createUser = vi.fn();

  beforeEach(() => {
    createUser.mockReset();
    TestBed.configureTestingModule({
      imports: [CreateUserPage],
      providers: [
        provideRouter([]),
        { provide: UsersService, useValue: { createUser } },
      ],
    });
  });

  function createPage() {
    const fixture = TestBed.createComponent(CreateUserPage);
    fixture.detectChanges();
    return fixture;
  }

  it('submits the account details and shows success feedback', () => {
    createUser.mockReturnValue(of({
      id: 'user-1',
      username: 'new-cashier',
      enabled: true,
      role: 'ROLE_CASHIER',
      createdAt: '2026-10-04T10:00:00',
    }));
    const fixture = createPage();
    const component = fixture.componentInstance;
    component.userForm.setValue({
      username: 'new-cashier',
      password: 'temporary-pass',
      enabled: true,
      role: 'ROLE_CASHIER',
    });

    component.onSubmit();
    fixture.detectChanges();

    expect(createUser).toHaveBeenCalledWith({
      username: 'new-cashier',
      password: 'temporary-pass',
      enabled: true,
      role: 'ROLE_CASHIER',
    });
    expect(component.successMessage()).toBe('User created successfully.');
    expect(component.userForm.controls.password.value).toBe('');
  });

  it('shows the API error when user creation fails', () => {
    createUser.mockReturnValue(
      throwError(() => new HttpErrorResponse({
        status: 400,
        error: { message: 'Username already exists' },
      })),
    );
    const fixture = createPage();
    const component = fixture.componentInstance;
    component.userForm.setValue({
      username: 'existing-user',
      password: 'temporary-pass',
      enabled: true,
      role: 'ROLE_CASHIER',
    });

    component.onSubmit();
    fixture.detectChanges();

    expect(component.errorMessage()).toBe('Username already exists');
    expect(fixture.nativeElement.querySelector('[role="alert"]')?.textContent)
      .toContain('Username already exists');
  });

  it('does not submit an invalid form', () => {
    const fixture = createPage();
    fixture.componentInstance.onSubmit();

    expect(createUser).not.toHaveBeenCalled();
    expect(fixture.componentInstance.userForm.controls.username.touched).toBe(true);
  });
});
