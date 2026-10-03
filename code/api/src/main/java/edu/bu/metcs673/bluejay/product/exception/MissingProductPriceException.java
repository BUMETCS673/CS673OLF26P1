// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.exception;

public class MissingProductPriceException extends RuntimeException {
    public MissingProductPriceException(String productName) {
        super(String.format("Price of product with name '%s' has not been set", productName));
    }
}
