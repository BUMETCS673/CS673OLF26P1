// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~75%
// AI-Assisted Areas: @RestControllerAdvice implementation, MethodArgumentNotValidException extraction
// Human Contributions: Mapping specific HTTP status codes and error constants
// Notes: Global exception handler adhering to CS112 formatting and wrapping rules.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.common.exception;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create GlobalExceptionHandler handling BaseAppException, validation, and Spring Security exceptions"
// AI Contribution: Handler methods implementation (~80%)
// Modifications:
//   - Formatted stream operations and parameters to comply with 80-char CS112 limit
// Verification:
//   - Verified HTTP error payload format against client contract specifications
// Confidence: High
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseAppException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseAppException(
        BaseAppException ex) {
        ApiResponse<Void> response = ApiResponse.error(
            ex.getMessage(),
            ex.getErrorCode()
        );
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(
        BadCredentialsException ex) {
        ApiResponse<Void> response = ApiResponse.error(
            "Invalid username or password",
            "AUTH_INVALID_CREDENTIALS"
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));

        ApiResponse<Void> response = ApiResponse.error(
            errorMessage,
            "VALIDATION_FAILED"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
        Exception ex) {
        ApiResponse<Void> response = ApiResponse.error(
            "An unexpected internal error occurred",
            "INTERNAL_SERVER_ERROR"
        );
        return new ResponseEntity<>(
            response,
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}