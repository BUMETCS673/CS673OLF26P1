// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Status badge UI component with interactive toggle click emitter
// Human Contributions: None
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { Component, input, output } from '@angular/core';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create standalone UserStatusBadgeComponent for rendering interactive enabled/disabled pills."
// AI Contribution: Initial draft (~100%)
// Modifications: None.
// Verification: Component unit test
// Confidence: High

@Component({
  selector: 'app-user-status-badge',
  standalone: true,
  template: `
    <button
      type="button"
      class="status-toggle-button"
      (click)="toggle.emit()"
      [disabled]="disabled()"
      [title]="enabled() ? 'Disable user account' : 'Enable user account'"
    >
      <span class="status" [class.status-disabled]="!enabled()">
        <span class="status-dot" aria-hidden="true"></span>
        {{ enabled() ? 'Enabled' : 'Disabled' }}
      </span>
    </button>
  `,
})
export class UserStatusBadgeComponent {
  readonly enabled = input.required<boolean>();
  readonly disabled = input<boolean>(false);

  readonly toggle = output<void>();
}
