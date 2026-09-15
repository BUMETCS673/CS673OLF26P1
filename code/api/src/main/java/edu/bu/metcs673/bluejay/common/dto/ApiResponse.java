// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Generic record design, compact factory methods
// Human Contributions: Standardized API response contract design
// Notes: Standard response model formatted for Java 21 and CS112 standards.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.common.dto;

import java.time.Instant;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create generic ApiResponse record in Java 21 with success and error helpers"
// AI Contribution: Immutable record declaration and static factory methods (~80%)
// Modifications:
//   - Structured constructors to populate explicit UTC timestamps
// Verification:
//   - Verified REST response payload compatibility with Jackson
// Confidence: High
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    String errorCode,
    Instant timestamp) {

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
            true,
            message,
            data,
            null,
            Instant.now()
        );
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }

    public static <T> ApiResponse<T> error(
        String message,
        String errorCode) {
        return new ApiResponse<>(
            false,
            message,
            null,
            errorCode,
            Instant.now()
        );
    }
}