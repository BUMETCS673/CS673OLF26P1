/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 90%
    AI-Assisted Areas: Creation of exception
    Human Contributions: Prompting for exception if not enough product is in stock
    Notes: Exception for insufficient number of stocked products
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

public class InsufficientProductStockException extends BaseAppException {
    public InsufficientProductStockException(
        String productName,
        int requested,
        int available
    ) {
        super(
            String.format(
                "Cannot sell %d of %s; only %d remain.",
                requested,
                productName,
                available
            ),
            HttpStatus.CONFLICT,
            "INSUFFICIENT_STOCK"
        );
    }
}