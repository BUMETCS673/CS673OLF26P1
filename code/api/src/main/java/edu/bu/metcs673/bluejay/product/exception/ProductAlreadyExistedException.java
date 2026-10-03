// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.exception;

public class ProductAlreadyExistedException extends RuntimeException {
    public ProductAlreadyExistedException(String barcode) {
        super(String.format("Product with barcode %s is already existed.", barcode));
    }
}
