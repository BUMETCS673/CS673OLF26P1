// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Promote product domain exceptions to BaseAppException for REST controller responses"
// AI Contribution: HTTP status and error-code mapping for duplicate product failures (~80%)
// Confidence: High
package edu.bu.metcs673.bluejay.common.exception;

import org.springframework.http.HttpStatus;

public class ProductAlreadyExistedException extends BaseAppException {
    public ProductAlreadyExistedException(String barcode) {
        super(
            String.format("Product with barcode %s is already existed.", barcode),
            HttpStatus.CONFLICT,
            "PRODUCT_ALREADY_EXISTS"
        );
    }
}
