/*
  AI-USAGE SUMMARY
  Tools: GitHub Copilot / Gemini
  Overall AI Contribution: 90%
  AI-Assisted Areas: Inline editing assertions, self-edit protection tests, and action error validation
  Human Contributions: Designed test cases for active admin lockout protection
  Notes: Unit Tests for the Users page
  Authors: Italia Tran, Sara Orion
*/

import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { vi } from 'vitest';
import { AuthService } from '../../../core/auth/auth.service';
import { UserRecord } from '../models/users.model';
import { UsersService } from '../services/users.services';
import { UsersPage } from './users-page.component';

describe('UsersPage directory & row editing behavior', () => {
  const userRecords: UserRecord[] = [
    {
      id: 'user-z',
      username: 'Zoe',
      enabled: true,
      role: 'ROLE_CASHIER',
      createdAt: '2025-03-01T10:00:00',
    },
    {
      id: 'user-b',
      username: 'Blair',
      enabled: false,
      role: 'ROLE_MANAGER',
      createdAt: '2023-03-01T10:00:00',
    },
    {
      id: 'user-a',
      username: 'Anna',
      enabled: true,
      role: 'ROLE_ADMIN',
      createdAt: '2024-03-01T10:00:00',
    },
  ];

  const usersServiceMock = {
    getUsers: vi.fn(),
    updateUser: vi.fn(),
  };

  const authServiceMock = {
    logout: vi.fn(),
    getUsername: vi.fn().mockReturnValue('Anna'), // 'Anna' is logged in
  };

  beforeEach(() => {
    usersServiceMock.getUsers.mockReset();
    usersServiceMock.updateUser.mockReset();
    usersServiceMock.getUsers.mockReturnValue(of(userRecords));

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
        { provide: UsersService, useValue: usersServiceMock },
      ],
    });
  });

  function createFixture() {
    const fixture = TestBed.createComponent(UsersPage);
    fixture.detectChanges();
    return fixture;
  }

  function displayedUsernames(element: HTMLElement): string[] {
    return Array.from(element.querySelectorAll('.account-cell strong'))
      .map((node) => node.textContent?.trim() ?? '');
  }

  it('filters usernames and IDs without case sensitivity', () => {
    const fixture = createFixture();
    const search = fixture.nativeElement.querySelector(
      'input[type="search"]',
    ) as HTMLInputElement;

    search.value = 'bLa';
    search.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    expect(displayedUsernames(fixture.nativeElement)).toEqual(['Blair']);

    search.value = 'USER-Z';
    search.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    expect(displayedUsernames(fixture.nativeElement)).toEqual(['Zoe']);
  });

  it('prevents the logged-in user from editing their own account', () => {
    const fixture = createFixture();
    const component = fixture.componentInstance;

    const annaUser = userRecords.find((u) => u.username === 'Anna')!;
    expect(component.isCurrentUser(annaUser)).toBe(true);

    component.startEdit(annaUser);
    expect(component.editingUserId()).toBeNull(); // Blocked from entering edit mode
  });

  it('enters inline edit mode and saves role and status updates', () => {
    const fixture = createFixture();
    const component = fixture.componentInstance;
    const zoeUser = userRecords.find((u) => u.username === 'Zoe')!;

    usersServiceMock.updateUser.mockReturnValue(of({
      ...zoeUser,
      role: 'ROLE_MANAGER',
      enabled: false,
    }));

    component.startEdit(zoeUser);
    expect(component.editingUserId()).toBe('user-z');
    expect(component.editRole()).toBe('ROLE_CASHIER');

    component.editRole.set('ROLE_MANAGER');
    component.toggleEditStatus(); // Flip enabled to false
    component.saveEdit(zoeUser);

    expect(usersServiceMock.updateUser).toHaveBeenCalledWith('user-z', {
      role: 'ROLE_MANAGER',
      enabled: false,
    });
    expect(component.editingUserId()).toBeNull();
  });

  it('displays actionError banner when inline edit save fails with 403', () => {
    const fixture = createFixture();
    const component = fixture.componentInstance;
    const blairUser = userRecords.find((u) => u.username === 'Blair')!;

    usersServiceMock.updateUser.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 403 })),
    );

    component.startEdit(blairUser);
    component.editRole.set('ROLE_ADMIN');
    component.saveEdit(blairUser);
    fixture.detectChanges();

    expect(component.actionError()).toContain('You do not have administrative privileges');
    expect(component.errorStatus()).toBeNull(); // Ensures main page error is untouched
  });
});
