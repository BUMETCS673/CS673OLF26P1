// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Spec unit tests for signal inputs (enabled, disabled), label class binding, and slide-toggle toggle emission
// Human Contributions: Custom component integration and event assertion verification
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { UserStatusBadgeComponent } from './user-status-badge.component';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Fix disabled state assertion for Angular Material MatSlideToggle directive in UserStatusBadgeComponent test."
// AI Contribution: Initial draft (~100%)
// Modifications: Queried MatSlideToggle directive instance via By.directive to verify disabled property directly.
// Verification: Angular CLI test runner (npm run test).
// Confidence: High
describe('UserStatusBadgeComponent', () => {
  let fixture: ComponentFixture<UserStatusBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserStatusBadgeComponent]
    }).compileComponents();
  });

  it('should render Enabled label with green class when enabled signal is true', () => {
    fixture = TestBed.createComponent(UserStatusBadgeComponent);
    fixture.componentRef.setInput('enabled', true);
    fixture.detectChanges();

    const label = fixture.nativeElement.querySelector('.status-toggle-label');
    expect(label.textContent.trim()).toBe('Enabled');
    expect(label.classList.contains('enabled')).toBe(true);
  });

  it('should render Disabled label with red class when enabled signal is false', () => {
    fixture = TestBed.createComponent(UserStatusBadgeComponent);
    fixture.componentRef.setInput('enabled', false);
    fixture.detectChanges();

    const label = fixture.nativeElement.querySelector('.status-toggle-label');
    expect(label.textContent.trim()).toBe('Disabled');
    expect(label.classList.contains('disabled')).toBe(true);
  });

  it('should emit toggle event on toggle change', () => {
    fixture = TestBed.createComponent(UserStatusBadgeComponent);
    fixture.componentRef.setInput('enabled', true);
    const toggleSpy = vi.fn();
    fixture.componentInstance.toggle.subscribe(toggleSpy);
    fixture.detectChanges();

    const slideToggle = fixture.nativeElement.querySelector('mat-slide-toggle');
    slideToggle.dispatchEvent(new Event('change'));

    expect(toggleSpy).toHaveBeenCalledTimes(1);
  });

  it('should apply disabled attribute to mat-slide-toggle when disabled signal is true', () => {
    fixture = TestBed.createComponent(UserStatusBadgeComponent);
    fixture.componentRef.setInput('enabled', true);
    fixture.componentRef.setInput('disabled', true);
    fixture.detectChanges();

    // Query the MatSlideToggle directive instance directly from the DebugElement
    const slideToggleDebugElement = fixture.debugElement.query(By.directive(MatSlideToggle));
    const slideToggleInstance = slideToggleDebugElement.componentInstance as MatSlideToggle;

    expect(slideToggleInstance.disabled).toBe(true);
  });
});
