package edu.bu.metcs673.bluejay.product.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("Product not found.");
    }
}
