
// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Domain exception extending project BaseAppException for
// inventory quantity validation.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create custom exception extending BaseAppException for
// quantity validation failures satisfying AC 2."
// AI Contribution: Initial draft (~100%)
// Modifications: Configured HTTP status 400 BAD_REQUEST and exact error
// message string.
// Verification: Unit test and GlobalExceptionHandler integration test.
// Confidence: High
public class InvalidQuantityException extends BaseAppException {

    public InvalidQuantityException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_QUANTITY");
    }

    public InvalidQuantityException() {
        this("Invalid quantity.");
    }
}