// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Extending BaseAppException, default status binding
// Human Contributions: Specific error code design (AUTH_INVALID_CREDENTIALS)
// Notes: Derived domain exception for login authentication failures.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create InvalidCredentialsException extending BaseAppException"
// AI Contribution: Constructor logic (~80%)
// Modifications:
//   - Bound HTTP 401 UNAUTHORIZED status and AUTH_INVALID_CREDENTIALS error code
// Verification:
//   - Verified behavior during failed authentication integration testing
// Confidence: High
public class InvalidCredentialsException extends BaseAppException {

    public InvalidCredentialsException(String message) {
        super(
            message,
            HttpStatus.UNAUTHORIZED,
            "AUTH_INVALID_CREDENTIALS"
        );
    }

    public InvalidCredentialsException() {
        this("Invalid username or password");
    }
}