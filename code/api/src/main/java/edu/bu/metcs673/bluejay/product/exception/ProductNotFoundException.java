// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Promote product domain exceptions to BaseAppException for REST controller responses"
// AI Contribution: HTTP status and error-code mapping for missing product lookups (~80%)
// Confidence: High
package edu.bu.metcs673.bluejay.product.exception;

import edu.bu.metcs673.bluejay.common.exception.BaseAppException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseAppException {
    public ProductNotFoundException() {
        super("Product not found.", HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND");
    }

    public ProductNotFoundException(String barcode) {
        super(
            String.format("Product with barcode '%s' not found.", barcode),
            HttpStatus.NOT_FOUND,
            "PRODUCT_NOT_FOUND"
        );
    }
}
