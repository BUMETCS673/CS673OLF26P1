// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 90%
// AI-Assisted Areas: Vitest unit test suite setup, mock AuthService implementation, role-based route filtering assertions, DOM rendering verification
// Human Contributions: Configured route permissions in navigation.model.ts, refactored component property into a getter, executed and verified tests with Vitest runner
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { describe, beforeEach, it, expect, vi, Mock } from 'vitest';
import { SidebarComponent } from './sidebar.component';
import { AuthService } from '../../auth/auth.service';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create a Vitest unit test suite for SidebarComponent to verify dynamic navigation link filtering based on user roles. Test expected item counts and DOM links for Admin, Manager, and standard users."
// AI Contribution: Initial draft (~90%)
// Modifications: Refactored test assertions from Jasmine to Vitest, updated link counts to reflect 4 role-restricted routes, adjusted tests for dynamic getter property evaluation.
// Verification: Vitest test runner (`npm run test`)
// Confidence: High
describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;
  let authServiceMock: { isRole: Mock };

  beforeEach(async () => {
    authServiceMock = {
      isRole: vi.fn().mockReturnValue(false),
    };

    await TestBed.configureTestingModule({
      imports: [SidebarComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authServiceMock },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return all 6 navigation items when user is an ADMIN', () => {
    authServiceMock.isRole.mockReturnValue(true);

    fixture.detectChanges();

    expect(component.navItems.length).toBe(6);
  });

  it('should return 5 navigation items for MANAGER (excluding Users link)', () => {
    authServiceMock.isRole.mockImplementation(
      (role: string) => role === 'ROLE_MANAGER',
    );

    fixture.detectChanges();

    const routes = component.navItems.map((item) => item.route);
    expect(component.navItems.length).toBe(5);
    expect(routes).toContain('/reports');
    expect(routes).toContain('/products');
    expect(routes).toContain('/inventory');
    expect(routes).not.toContain('/users');
  });

  it('should return only 2 unrestricted items (Dashboard, Sales) for standard users', () => {
    authServiceMock.isRole.mockReturnValue(false);

    fixture.detectChanges();

    const routes = component.navItems.map((item) => item.route);
    expect(component.navItems.length).toBe(2);
    expect(routes).toEqual(['/dashboard', '/sales']);
  });

  it('should render 2 nav links in the DOM for standard users', () => {
    authServiceMock.isRole.mockReturnValue(false);

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const links = compiled.querySelectorAll('a.nav-link');

    expect(links.length).toBe(2);
  });
});
