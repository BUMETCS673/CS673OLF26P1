// AI-USAGE SUMMARY
// Tools: Claude
// Overall AI Contribution: ~85%
// AI-Assisted Areas: Domain exception for an invalid report date range
// Human Contributions: Story #29 test case "invalid range"
// Notes: Mapped to 400 INVALID_DATE_RANGE by GlobalExceptionHandler via BaseAppException.
// Authors: Krizma Nagi

package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDate;

// AI-ASSISTED: YES
// Tool: Claude
// Prompt Summary: "Exception for a sales report where the start date is after the end date"
// AI Contribution: Initial draft (~85%)
// Modifications:
//   - Lives in common/exception with the other domain exceptions (moved there in #102)
// Verification:
//   - SalesReportServiceImplTest and SalesReportControllerTest invalid-range cases
// Confidence: High
public class InvalidDateRangeException extends BaseAppException {

    public InvalidDateRangeException(LocalDate startDate, LocalDate endDate) {
        super(
            String.format(
                "Start date %s must be on or before end date %s.",
                startDate,
                endDate
            ),
            HttpStatus.BAD_REQUEST,
            "INVALID_DATE_RANGE"
        );
    }
}