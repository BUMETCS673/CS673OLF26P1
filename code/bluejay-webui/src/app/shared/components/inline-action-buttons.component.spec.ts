// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Spec unit tests covering conditional edit/save/cancel button rendering, disabled input checks, and output event spy triggers
// Human Contributions: Custom component integration and event assertion verification
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InlineActionButtonsComponent } from './inline-action-buttons.component';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Fix NG0100 ExpressionChangedAfterItHasBeenCheckedError in InlineActionButtonsComponent test suite."
// AI Contribution: Initial draft (~100%)
// Modifications: Synchronized detectChanges() calls after property mutations to prevent lifecycle mismatch.
// Verification: Angular CLI test runner (npm run test).
// Confidence: High
describe('InlineActionButtonsComponent', () => {
  let component: InlineActionButtonsComponent;
  let fixture: ComponentFixture<InlineActionButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InlineActionButtonsComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(InlineActionButtonsComponent);
    component = fixture.componentInstance;
  });

  it('should create component with default input properties', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.isEditing).toBe(false);
    expect(component.loading).toBe(false);
    expect(component.editDisabled).toBe(false);
  });

  it('should render edit button when isEditing is false and emit edit event on click', () => {
    const editSpy = vi.fn();
    component.edit.subscribe(editSpy);
    component.isEditing = false;
    fixture.detectChanges();

    const editBtn = fixture.nativeElement.querySelector('button') as HTMLButtonElement;
    expect(editBtn).toBeTruthy();
    expect(editBtn.textContent).toContain('edit');

    editBtn.click();
    expect(editSpy).toHaveBeenCalledTimes(1);
  });

  it('should render save and cancel buttons when isEditing is true', () => {
    component.isEditing = true;
    fixture.detectChanges();

    const buttons = fixture.nativeElement.querySelectorAll('button');
    expect(buttons.length).toBe(2);
    expect(buttons[0].textContent).toContain('save');
    expect(buttons[1].textContent).toContain('cancel');
  });

  it('should emit save and cancel events when corresponding buttons are clicked', () => {
    const saveSpy = vi.fn();
    const cancelSpy = vi.fn();
    component.save.subscribe(saveSpy);
    component.cancel.subscribe(cancelSpy);

    component.isEditing = true;
    fixture.detectChanges();

    const buttons = fixture.nativeElement.querySelectorAll('button');
    buttons[0].click();
    expect(saveSpy).toHaveBeenCalledTimes(1);

    buttons[1].click();
    expect(cancelSpy).toHaveBeenCalledTimes(1);
  });

  it('should disable all buttons when loading is true', () => {
    component.isEditing = true;
    component.loading = true;
    fixture.detectChanges();

    const buttons = fixture.nativeElement.querySelectorAll('button');
    expect(buttons[0].disabled).toBe(true);
    expect(buttons[1].disabled).toBe(true);
  });
});
