// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~65%
// AI-Assisted Areas: Base exception structure and status code binding
// Human Contributions: Error code mapping specifications, BaseAppException naming
// Notes: Abstract base class for domain exceptions adhering to CS112 standards.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create base BaseAppException class carrying HttpStatus and errorCode"
// AI Contribution: Class structure (~75%)
// Modifications:
//   - Made abstract and added explicit getters for GlobalExceptionHandler mapping
// Verification:
//   - Checked exception propagation in Spring MVC pipeline
// Confidence: High
@Getter
public abstract class BaseAppException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    protected BaseAppException(
        String message,
        HttpStatus status,
        String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}