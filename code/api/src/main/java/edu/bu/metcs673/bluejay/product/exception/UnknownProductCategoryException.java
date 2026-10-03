// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.exception;

public class UnknownProductCategoryException extends RuntimeException {
    public UnknownProductCategoryException(int categoryId) {
        super(String.format("Category with id %d does not exist.", categoryId));
    }
}
