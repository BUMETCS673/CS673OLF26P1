// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 80%
// AI-Assisted Areas: App spec router provider setup, Signal property evaluation test, template DOM assertion cleanup
// Human Contributions: Initial template structure, route definitions
// Notes: Code updated to align with Signal title property and router outlet testing dependencies.
// authors: Kimleng Lim; Sara Orion

import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { App } from './app';
import { routes } from './app.routes';

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Fix app.spec.ts to test Signal title property and satisfy router provider dependencies."
// AI Contribution: Initial draft (~80%)
// Modifications:
// - Added provideRouter(routes) to testing providers for router-outlet support
// - Tested signal title property read call `title()` instead of missing DOM h1 query
// Verification:
// - Run `ng test` or `npm test` to confirm app spec passes
// Confidence: High
describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter(routes)]
    }).compileComponents();
  });

  // AI-ASSISTED: NO
  // Tool: Angular CLI scaffold
  // Prompt Summary: "N/A"
  // AI Contribution: None
  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  // AI-ASSISTED: YES
  // Tool: Gemini
  // Prompt Summary: "Verify signal title property value on App instance."
  // AI Contribution: Refactored test (~90%)
  // Modifications:
  // - Invoked title signal function `app.title()` to read signal value
  // Verification:
  // - Vitest test runner execution
  // Confidence: High
  it('should have correct signal title property', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app['title']()).toBe('bluejay-webui');
  });
});
