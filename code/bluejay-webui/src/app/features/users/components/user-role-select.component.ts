// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Role selection component with signal inputs and output event emission
// Human Contributions: None
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { Component, input, output } from '@angular/core';
import { RoleOption } from '../models/users.model';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create standalone UserRoleSelectComponent to encapsulate inline table role dropdown."
// AI Contribution: Initial draft (~100%)
// Modifications: None.
// Verification: Component unit test and template interaction
// Confidence: High

@Component({
  selector: 'app-user-role-select',
  standalone: true,
  template: `
    <select
      class="role-select"
      [value]="currentRole()"
      [disabled]="disabled()"
      (change)="onRoleChange($event)"
      [attr.aria-label]="ariaLabel()"
    >
      @for (roleOption of availableRoles; track roleOption.value) {
        <option
          [value]="roleOption.value"
          [selected]="roleOption.value === currentRole()"
        >
          {{ roleOption.label }}
        </option>
      }
    </select>
  `,
})
export class UserRoleSelectComponent {
  readonly currentRole = input.required<string>();
  readonly disabled = input<boolean>(false);
  readonly ariaLabel = input<string>('Select user role');

  readonly roleChange = output<string>();

  readonly availableRoles: RoleOption[] = [
    { value: 'ROLE_CASHIER', label: 'Cashier' },
    { value: 'ROLE_MANAGER', label: 'Manager' },
    { value: 'ROLE_ADMIN', label: 'Administrator' },
  ];

  onRoleChange(event: Event): void {
    const newRole = (event.target as HTMLSelectElement).value;
    if (newRole && newRole !== this.currentRole()) {
      this.roleChange.emit(newRole);
    }
  }
}
