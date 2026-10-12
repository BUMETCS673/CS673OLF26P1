// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Material Slide Toggle component setup with inline 11px typography overrides
// authors: Sara Orion

import { Component, input, output } from '@angular/core';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Refactor UserStatusBadgeComponent to render a MatSlideToggle for user row editing with 11px font size."
// AI Contribution: Initial draft (~100%)
// Modifications: Applied MatSlideToggleModule and styled label to match 11px view mode typography.
// Verification: Angular compiler build check.
// Confidence: High
@Component({
  selector: 'app-user-status-badge',
  standalone: true,
  imports: [MatSlideToggleModule],
  template: `
    <mat-slide-toggle
      color="primary"
      [checked]="enabled()"
      [disabled]="disabled()"
      (change)="toggle.emit()"
      [aria-label]="enabled() ? 'Disable user account' : 'Enable user account'">
      <span class="status-toggle-label" [class.enabled]="enabled()" [class.disabled]="!enabled()">
        {{ enabled() ? 'Enabled' : 'Disabled' }}
      </span>
    </mat-slide-toggle>
  `,
  styles: [`
    :host {
      display: inline-flex;
      align-items: center;

      /* Slightly scale down switch track to fit 11px table row height */
      ::ng-deep .mdc-switch {
        transform: scale(0.85);
        transform-origin: left center;
      }
    }

    .status-toggle-label {
      font-size: 11px;
      font-weight: 650;
      line-height: 1;
      margin-left: -2px;

      &.enabled {
        color: #15803d;
      }

      &.disabled {
        color: #dc2626;
      }
    }
  `]
})
export class UserStatusBadgeComponent {
  readonly enabled = input.required<boolean>();
  readonly disabled = input<boolean>(false);

  readonly toggle = output<void>();
}
