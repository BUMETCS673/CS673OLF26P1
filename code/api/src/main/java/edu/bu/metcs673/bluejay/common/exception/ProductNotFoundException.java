// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Domain exception extending project BaseAppException for
// catalog validation.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create custom exception extending BaseAppException for
// missing product catalog entries satisfying AC 1."
// AI Contribution: Initial draft (~100%)
// Modifications: Configured HTTP status 404 NOT_FOUND and exact error
// message string.
// Verification: Unit test and GlobalExceptionHandler integration test.
// Confidence: High
public class ProductNotFoundException extends BaseAppException {

    public ProductNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }

    public ProductNotFoundException() {
        this("Product not found in catalog.");
    }
}