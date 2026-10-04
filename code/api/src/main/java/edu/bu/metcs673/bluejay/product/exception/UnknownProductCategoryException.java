// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Promote product domain exceptions to BaseAppException for REST controller responses"
// AI Contribution: HTTP status and error-code mapping for missing product category references (~80%)
// Confidence: High
package edu.bu.metcs673.bluejay.product.exception;

import edu.bu.metcs673.bluejay.common.exception.BaseAppException;
import org.springframework.http.HttpStatus;

public class UnknownProductCategoryException extends BaseAppException {
    public UnknownProductCategoryException(Long categoryId) {
        super(
            String.format("Category with id %s does not exist.", categoryId),
            HttpStatus.NOT_FOUND,
            "PRODUCT_CATEGORY_NOT_FOUND"
        );
    }
}
