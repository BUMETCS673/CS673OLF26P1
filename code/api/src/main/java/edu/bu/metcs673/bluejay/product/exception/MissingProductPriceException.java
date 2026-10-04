// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Promote product domain exceptions to BaseAppException for REST controller responses"
// AI Contribution: HTTP status and error-code mapping for missing product price failures (~80%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.exception;

import edu.bu.metcs673.bluejay.common.exception.BaseAppException;
import org.springframework.http.HttpStatus;

public class MissingProductPriceException extends BaseAppException {
    public MissingProductPriceException(String productName) {
        super(
            String.format("Price of product with name '%s' has not been set", productName),
            HttpStatus.UNPROCESSABLE_ENTITY,
            "PRODUCT_PRICE_MISSING"
        );
    }
}
