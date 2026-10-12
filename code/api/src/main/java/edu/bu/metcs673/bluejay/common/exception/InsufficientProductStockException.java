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