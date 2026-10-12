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