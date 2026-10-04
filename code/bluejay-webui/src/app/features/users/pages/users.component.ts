/*
  AI-USAGE SUMMARY
  Tools: GitHub Copilot
  Overall AI Contribution: 80%
  AI-Assisted Areas: Created base user component and the additiomal search and sort features
  Human Contributions: Prompted for search and sort features after base was built
  Notes: Angular component for the Users page
  Authors: Italia Tran
*/

import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { UserRecord, UsersService } from '../../../core/services/users/users.service';

type SortColumn = 'username' | 'id' | 'createdAt' | 'enabled';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [DatePipe, RouterLink],
  templateUrl: './users.component.html',
  styleUrl: './users.component.scss',
})
export class UsersPage implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly usersService = inject(UsersService);

  readonly users = signal<UserRecord[]>([]);
  readonly searchTerm = signal('');
  readonly isLoading = signal(false);
  readonly errorStatus = signal<number | null>(null);
  readonly sortColumn = signal<SortColumn>('username');
  readonly sortDirection = signal<SortDirection>('asc');
  readonly filteredUsers = computed(() => {
    const query = this.searchTerm().trim().toLowerCase();
    const matchingUsers = query
      ? this.users().filter(
          (user) =>
            user.username.toLowerCase().includes(query) ||
            user.id.toLowerCase().includes(query),
        )
      : this.users();

    const column = this.sortColumn();
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    return [...matchingUsers].sort(
      (left, right) => direction * this.compareUsers(left, right, column),
    );
  });
  readonly enabledCount = computed(
    () => this.users().filter((user) => user.enabled).length,
  );

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.errorStatus.set(null);

    this.usersService.getUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.isLoading.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.errorStatus.set(error.status);
        this.isLoading.set(false);
      },
    });
  }

  updateSearch(event: Event): void {
    this.searchTerm.set((event.target as HTMLInputElement).value);
  }

  sortBy(column: SortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) =>
        direction === 'asc' ? 'desc' : 'asc',
      );
      return;
    }

    this.sortColumn.set(column);
    this.sortDirection.set('asc');
  }

  ariaSort(column: SortColumn): 'ascending' | 'descending' | 'none' {
    if (this.sortColumn() !== column) return 'none';
    return this.sortDirection() === 'asc' ? 'ascending' : 'descending';
  }

  private compareUsers(
    left: UserRecord,
    right: UserRecord,
    column: SortColumn,
  ): number {
    switch (column) {
      case 'username':
        return left.username.localeCompare(right.username, undefined, {
          sensitivity: 'base',
        });
      case 'id':
        return left.id.localeCompare(right.id, undefined, {
          sensitivity: 'base',
        });
      case 'createdAt': {
        const leftTime = Date.parse(left.createdAt);
        const rightTime = Date.parse(right.createdAt);
        if (Number.isNaN(leftTime) || Number.isNaN(rightTime)) {
          return left.createdAt.localeCompare(right.createdAt);
        }
        return leftTime - rightTime;
      }
      case 'enabled':
        return Number(left.enabled) - Number(right.enabled);
    }
  }

  get errorMessage(): string {
    switch (this.errorStatus()) {
      case 401:
        return 'Your session has expired. Sign in again to continue.';
      case 403:
        return 'Administrator access is required to view this directory.';
      default:
        return 'The user directory could not be loaded.';
    }
  }

  signOut(): void {
    this.authService.logout();
    void this.router.navigate(['/login'], { queryParams: { reason: 'logout' } });
  }
}
