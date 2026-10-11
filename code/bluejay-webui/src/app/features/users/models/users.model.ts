// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Type interface definitions for User records and API request payloads
// Human Contributions: None
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

export interface UserRecord {
  id: string;
  username: string;
  enabled: boolean;
  createdAt: string;
  role: string;
}

export interface CreateUserRequest {
  username: string;
  password: string;
  enabled: boolean;
  role: string;
}

export interface UpdateUserRequest {
  role?: string;
  enabled?: boolean;
}

export interface RoleOption {
  value: string;
  label: string;
}
