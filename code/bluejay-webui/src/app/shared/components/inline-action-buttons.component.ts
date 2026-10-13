// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Angular Material Tooltip integration on disabled button container wrapper
// Human Contributions: Input bindings for customizable title tooltips and conditional edit/save/cancel disabling
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Wrap Material icon button in a span with matTooltip to allow tooltip display even when the button is disabled."
// AI Contribution: Initial draft (~100%)
// Modifications: Added MatTooltipModule and span wrapper with [matTooltip] directive.
// Verification: Angular compiler check and browser hover test.
// Confidence: High
@Component({
  selector: 'app-inline-action-buttons',
  standalone: true,
  imports: [MatButtonModule, MatIconModule, MatTooltipModule],
  template: `
    <div class="action-buttons-group">
      @if (isEditing) {
        <span [matTooltip]="saveTitle">
          <button
            type="button"
            mat-icon-button
            color="primary"
            [disabled]="loading || saveDisabled"
            (click)="save.emit()">
            <mat-icon>save</mat-icon>
          </button>
        </span>
        <span [matTooltip]="cancelTitle">
          <button
            type="button"
            mat-icon-button
            color="warn"
            [disabled]="loading || cancelDisabled"
            (click)="cancel.emit()">
            <mat-icon>cancel</mat-icon>
          </button>
        </span>
      } @else {
        <span [matTooltip]="editTitle">
          <button
            type="button"
            mat-icon-button
            [disabled]="loading || editDisabled"
            (click)="edit.emit()">
            <mat-icon>edit</mat-icon>
          </button>
        </span>
      }
    </div>
  `,
  styles: [`
    .action-buttons-group {
      display: inline-flex;
      align-items: center;
      gap: 0.25rem;
      justify-content: flex-end;

      /* Ensures disabled button pointer events don't suppress span tooltips */
      span {
        display: inline-block;
      }
    }
  `]
})
export class InlineActionButtonsComponent {
  @Input({ required: true }) isEditing = false;
  @Input() loading = false;

  // Customization Inputs with defaults
  @Input() editDisabled = false;
  @Input() editTitle = 'Edit';
  @Input() saveDisabled = false;
  @Input() saveTitle = 'Save';
  @Input() cancelDisabled = false;
  @Input() cancelTitle = 'Cancel';

  @Output() edit = new EventEmitter<void>();
  @Output() save = new EventEmitter<void>();
  @Output() cancel = new EventEmitter<void>();
}
