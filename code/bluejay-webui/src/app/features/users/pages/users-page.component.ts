/*
  AI-USAGE SUMMARY
  Tools: GitHub Copilot
  Overall AI Contribution: 80%
  AI-Assisted Areas: Created base user component and the additional search and sort features
  Human Contributions: Prompted for search and sort features after base was built
  Notes: Angular component for the Users page
  Authors: Italia Tran, Sara Orion
*/

// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 90%
// AI-Assisted Areas: Logged-in user check to prevent self-editing, guard checks in startEdit, and contextual error handling
// Human Contributions: Integration with AuthService user claims
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { UserRoleSelectComponent } from '../components/user-role-select.component';
import { UserStatusBadgeComponent } from '../components/user-status-badge.component';
import { RoleOption, UserRecord } from '../models/users.model';
import { UsersService } from '../services/users.services';

type SortColumn = 'username' | 'id' | 'createdAt' | 'enabled' | 'role';
type SortDirection = 'asc' | 'desc';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [
    DatePipe,
    RouterLink,
    UserRoleSelectComponent,
    UserStatusBadgeComponent,
  ],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.scss',
})
export class UsersPage implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly usersService = inject(UsersService);

  readonly availableRoles: RoleOption[] = [
    { value: 'ROLE_CASHIER', label: 'Cashier' },
    { value: 'ROLE_MANAGER', label: 'Manager' },
    { value: 'ROLE_ADMIN', label: 'Administrator' },
  ];

  readonly users = signal<UserRecord[]>([]);
  readonly searchTerm = signal('');
  readonly isLoading = signal(false);
  readonly errorStatus = signal<number | null>(null);
  readonly actionError = signal<string | null>(null);
  readonly sortColumn = signal<SortColumn>('username');
  readonly sortDirection = signal<SortDirection>('asc');

  // Inline Editing Signals
  readonly editingUserId = signal<string | null>(null);
  readonly editRole = signal<string>('');
  readonly editEnabled = signal<boolean>(true);
  readonly isSaving = signal<boolean>(false);

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

  // Check if target user matches currently authenticated session
  isCurrentUser(user: UserRecord): boolean {
    const currentUsername = this.authService.getUsername?.();
    return user.username === currentUsername;
  }

  loadUsers(): void {
    this.isLoading.set(true);
    this.errorStatus.set(null);
    this.actionError.set(null);

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

  startEdit(user: UserRecord): void {
    if (this.isCurrentUser(user)) return; // Prevent user from editing themself

    this.actionError.set(null);
    this.editingUserId.set(user.id);

    // Normalize role string format (e.g., handles both 'ADMIN' and 'ROLE_ADMIN')
    let role = user.role || 'ROLE_CASHIER';
    if (!role.startsWith('ROLE_')) {
      role = `ROLE_${role}`;
    }

    this.editRole.set(role);
    this.editEnabled.set(user.enabled);
  }

  cancelEdit(): void {
    this.editingUserId.set(null);
    this.editRole.set('');
    this.editEnabled.set(true);
    this.actionError.set(null);
  }

  saveEdit(user: UserRecord): void {
    const updatedRole = this.editRole();
    const updatedEnabled = this.editEnabled();

    if (updatedRole === user.role && updatedEnabled === user.enabled) {
      this.cancelEdit();
      return;
    }

    this.isSaving.set(true);
    this.actionError.set(null);

    this.usersService
      .updateUser(user.id, { role: updatedRole, enabled: updatedEnabled })
      .subscribe({
        next: () => {
          this.users.update((current) =>
            current.map((u) =>
              u.id === user.id
                ? { ...u, role: updatedRole, enabled: updatedEnabled }
                : u,
            ),
          );
          this.isSaving.set(false);
          this.cancelEdit();
        },
        error: (error: HttpErrorResponse) => {
          this.isSaving.set(false);
          if (error.status === 403) {
            this.actionError.set('You do not have administrative privileges to modify this user.');
          } else {
            this.actionError.set('Failed to save user changes. Please try again.');
          }
        },
      });
  }

  toggleEditStatus(): void {
    this.editEnabled.update((status) => !status);
  }

  getRoleLabel(roleValue: string): string {
    const found = this.availableRoles.find((r) => r.value === roleValue);
    return found ? found.label : roleValue || 'Cashier';
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
      case 'role':
        return this.getRoleLabel(left.role).localeCompare(
          this.getRoleLabel(right.role),
          undefined,
          { sensitivity: 'base' },
        );
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
