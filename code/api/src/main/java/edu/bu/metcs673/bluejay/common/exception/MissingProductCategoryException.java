package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

public class MissingProductCategoryException extends BaseAppException {
    public MissingProductCategoryException() {
        super(
            "A product category is required.",
            HttpStatus.BAD_REQUEST,
            "PRODUCT_CATEGORY_REQUIRED"
        );
    }
}