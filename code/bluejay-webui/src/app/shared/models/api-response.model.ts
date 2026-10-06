// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: TypeScript interfaces for API request, response, and recent entries table state.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// Authors: Sara Orion

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create TypeScript interfaces matching backend  ApiResponse wrapper."
// AI Contribution: Initial draft (~100%)
// Modifications: Configured generic ApiResponse contract to align with Spring Boot backend wrapper.
// Verification: Angular compiler type checking.
// Confidence: High
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T | null;
  errorCode: string | null;
  timestamp: string;
}
