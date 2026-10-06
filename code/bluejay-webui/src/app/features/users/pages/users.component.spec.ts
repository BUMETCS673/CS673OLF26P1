/*
  AI-USAGE SUMMARY
  Tools: GitHub Copilot
  Overall AI Contribution: 90%
  AI-Assisted Areas: Created unit tests for the users page based on documentation requirements
  Human Contributions: Prompted type of unit tests that needs to be covered
  Notes: Unit Tests for the Users page
  Authors: Italia Tran
*/

import { provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { vi } from 'vitest';
import { AuthService } from '../../../core/auth/auth.service';
import { UserRecord, UsersService } from '../../../core/services/users/users.service';
import { UsersPage } from './users.component';

describe('UsersComponent directory behavior', () => {
  const userRecords: UserRecord[] = [
    {
      id: 'user-z',
      username: 'Zoe',
      enabled: true,
      createdAt: '2025-03-01T10:00:00',
    },
    {
      id: 'user-b',
      username: 'Blair',
      enabled: false,
      createdAt: '2023-03-01T10:00:00',
    },
    {
      id: 'user-a',
      username: 'Anna',
      enabled: true,
      createdAt: '2024-03-01T10:00:00',
    },
  ];
  const usersServiceMock = { getUsers: vi.fn() };

  beforeEach(() => {
    usersServiceMock.getUsers.mockReset();
    usersServiceMock.getUsers.mockReturnValue(of(userRecords));

    TestBed.configureTestingModule({
      imports: [UsersPage],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: { logout: vi.fn() } },
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

  it('sorts usernames ascending by default and toggles to descending', () => {
    const fixture = createFixture();
    const usernameSort = fixture.nativeElement.querySelector(
      '[data-sort="username"]',
    ) as HTMLButtonElement;

    expect(displayedUsernames(fixture.nativeElement)).toEqual([
      'Anna',
      'Blair',
      'Zoe',
    ]);
    expect(usernameSort.closest('th')?.getAttribute('aria-sort')).toBe(
      'ascending',
    );

    usernameSort.click();
    fixture.detectChanges();

    expect(displayedUsernames(fixture.nativeElement)).toEqual([
      'Zoe',
      'Blair',
      'Anna',
    ]);
    expect(usernameSort.closest('th')?.getAttribute('aria-sort')).toBe(
      'descending',
    );
  });

  it('sorts by creation date after search filtering', () => {
    const fixture = createFixture();
    const search = fixture.nativeElement.querySelector(
      'input[type="search"]',
    ) as HTMLInputElement;
    search.value = 'a';
    search.dispatchEvent(new Event('input'));

    const createdSort = fixture.nativeElement.querySelector(
      '[data-sort="createdAt"]',
    ) as HTMLButtonElement;
    createdSort.click();
    fixture.detectChanges();

    expect(displayedUsernames(fixture.nativeElement)).toEqual([
      'Blair',
      'Anna',
    ]);
    expect(createdSort.closest('th')?.getAttribute('aria-sort')).toBe(
      'ascending',
    );
  });
});
