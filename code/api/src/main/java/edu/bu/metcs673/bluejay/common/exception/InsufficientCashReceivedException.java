/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 90%
    AI-Assisted Areas: Creation of exception
    Human Contributions: Prompting for exception if not enough cash is tendered
    Notes: Exception for insufficient cash
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

public class InsufficientCashReceivedException extends BaseAppException {
    public InsufficientCashReceivedException(
        BigDecimal received,
        BigDecimal total
    ) {
        super(
            String.format("Cash received (%s) is below the sale total (%s).", received, total),
            HttpStatus.UNPROCESSABLE_CONTENT,
            "INSUFFICIENT_PAYMENT"
        );
    }
}