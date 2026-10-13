// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Spec unit tests for dropdown options selection, roleChange signal output emitter, unchanged role guard checks, and disabled state
// Human Contributions: Custom component integration and event assertion verification
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserRoleSelectComponent } from './user-role-select.component';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create unit test spec for UserRoleSelectComponent verifying dropdown options, currentRole signal input, and roleChange emission guard."
// AI Contribution: Initial draft (~100%)
// Modifications: Added test case checking that duplicate role selections do not emit roleChange events.
// Verification: Angular CLI test runner (npm run test).
// Confidence: High
describe('UserRoleSelectComponent', () => {
  let fixture: ComponentFixture<UserRoleSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserRoleSelectComponent]
    }).compileComponents();
  });

  it('should render available roles and select currentRole', () => {
    fixture = TestBed.createComponent(UserRoleSelectComponent);
    fixture.componentRef.setInput('currentRole', 'ROLE_MANAGER');
    fixture.detectChanges();

    const select = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    expect(select.value).toBe('ROLE_MANAGER');

    const options = fixture.nativeElement.querySelectorAll('option');
    expect(options.length).toBe(3);
    expect(options[0].value).toBe('ROLE_CASHIER');
    expect(options[1].value).toBe('ROLE_MANAGER');
    expect(options[2].value).toBe('ROLE_ADMIN');
  });

  it('should emit roleChange output when a new role is selected', () => {
    fixture = TestBed.createComponent(UserRoleSelectComponent);
    fixture.componentRef.setInput('currentRole', 'ROLE_CASHIER');
    const roleChangeSpy = vi.fn();
    fixture.componentInstance.roleChange.subscribe(roleChangeSpy);
    fixture.detectChanges();

    const select = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    select.value = 'ROLE_ADMIN';
    select.dispatchEvent(new Event('change'));

    expect(roleChangeSpy).toHaveBeenCalledWith('ROLE_ADMIN');
  });

  it('should NOT emit roleChange output if unchanged role is re-selected', () => {
    fixture = TestBed.createComponent(UserRoleSelectComponent);
    fixture.componentRef.setInput('currentRole', 'ROLE_CASHIER');
    const roleChangeSpy = vi.fn();
    fixture.componentInstance.roleChange.subscribe(roleChangeSpy);
    fixture.detectChanges();

    const select = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    select.value = 'ROLE_CASHIER';
    select.dispatchEvent(new Event('change'));

    expect(roleChangeSpy).not.toHaveBeenCalled();
  });

  it('should respect disabled signal input', () => {
    fixture = TestBed.createComponent(UserRoleSelectComponent);
    fixture.componentRef.setInput('currentRole', 'ROLE_CASHIER');
    fixture.componentRef.setInput('disabled', true);
    fixture.detectChanges();

    const select = fixture.nativeElement.querySelector('select') as HTMLSelectElement;
    expect(select.disabled).toBe(true);
  });
});
